## Netflix-Eureka - A naming server for Service registry and discovery
* In microservice architecture we break the application into number of small self-independent microservice. So it is not wrong to say that in microservice architecture an application may end up with running large no of microservices and also we may run multiple instances of one microservice to distribute the load using Load-Balancer.
* Having said an application running large no of microservice with multiple instances, if a microservice wants to communicate with other microservice, it should have information about list of instances of that other microservice. So if you are using Ribbon alone then you have to provide hardcoded urls of all the instances of that other microservice which is not recommended at all.
* So we need some kind of way which can allow a microservice to discover all other up and running microservices. The `Naming Server` provides this type of capability. The fundamental role of `Naming Server` is to keep the list of running services and easilty manage this list as whenever a service is removed or new service is added.
  <p align="center">
    <img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/architecture-using-naming-server.png"/>
  </p>

`In this, we will see how to create a Naming Server using Netflix-Eureka of Spring Cloud and how microservice will register itself with Netflix-Eureka server (this operation is known as Service Registration). And at the end we will see how a service will communicate with other services using combination of FeignClient + Ribbon + Eureka`
<hr/>

### Setting up the Netflix-Eureka Server
Setting up the Eureka server is very easy and straightforward, as you just have to add the netflix-eureka dependency and enable the eureka server using `@EnableEurekaServer` annotation. (You can also refer [official documentation link](https://spring.io/guides/gs/service-registration-and-discovery/) on setting up the Eureka-Server)
* Add starter dependency of netflix-eureka server `spring-cloud-starter-netflix-eureka-server`
  ```xml
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
  </dependency>
  ```
* Enable the Eureka-Server in main class of the application. Doing this, spring will automatically configure the eureka-server.
  ```java
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

  @SpringBootApplication
  @EnableEurekaServer // this annotation is used to enable Eureka Server and configure it
  public class NetflixEurekaNamingServerApplication {

	  public static void main(String[] args) {
		  SpringApplication.run(NetflixEurekaNamingServerApplication.class, args);
	  }
  }
  ```
* And at last couple of configuration. The Eureka client in the eureka-server is instructed not to register itself upon start up (`eureka.client.register-with-eureka: false`), and it is told not to search for other registry nodes to connect to, as there are none - at least not when running locally (`eureka.client.fetch-registry: false`).
  ```
  spring.application.name=netflix-eureka-naming-server
  server.port=8761

  # By default, the eureka client of naming server will also attempt to register itself, so need to disable that
  eureka.client.register-with-eureka=false 
  eureka.client.fetch-registry=false
  ```
* You can open the Eureka-Server console using the port we have mentioned (`server.port`) -\
  http://localhost:8761
  
This is what I have created to act as Eureka-Server for my microservices (inventory-service and product-catalog-service).
[See this](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/netflix-eureka-naming-server)

`Ok, now we can move on to the services to see how to configure them so that they can register themselves with Eureka-Server — this is where things start to get more interesting.`
<hr/>

### Configure the Services to register in Eureka-Server
Here we will see how to configure services in a way that on startup they should connect to `eureka-server` and register themselves as eureka client. We are assuming that our services are already using Ribbon as load-balancer to talk to list of instances of other microservices (and this list is hardcoded in `ribbon.listOfServers` property, and after using Eureka there will not be a need to hardcode this list).

`The problem we are going to solve here is that, the product-catalog-service need to call one of the API of inventory-service. So before product-catalog-service will make call to inventory-service, the service would first need to know the list of running instances of inventory-service. This information will be provided by the Eureka-Server. So first we will configure our inventory-service to register in eureka-server`

<p align="center">
  <img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/ribbon-with-eureka.png"/>
</p>

Lets start configuring our `inventory-service` application so that on startup it can registry itself as eureka-client with eureka-server -
* First include the spring-cloud-starter `netflix-eureka-client` dependency in the project -
  ```xml
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
  </dependency>
  ```
* Just like enabling Eureka-Server, enable the eureka-client for the `inventory-service`. For this add `@EnabledDiscoveryClient` annotation on the main class of the service -
  ```java
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

  @SpringBootApplication
  @EnableDiscoveryClient // Enable the client implementation for Eureka-Client
  public class InventoryMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryMicroserviceApplication.class, args);
	}
  }
  
  @RestController
  class ServiceInstanceRestController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/service-instances/{applicationName}")
    public List<ServiceInstance> serviceInstancesByApplicationName(
      @PathVariable String applicationName) {
      return this.discoveryClient.getInstances(applicationName);
    }
  }
  ```
  `@EnabledDiscoveryClient` tells the Spring Boot service to activate the Netflix Eureka DiscoveryClient implementation and register its own host and port with the Eureka server.
  
  In addition to this, the eureka-client also provides ability to define a Spring MVC REST endpoint, `ServiceInstanceRestController`, that returns an enumeration of all the `ServiceInstance` instances of the service registered in the registry.\
  http://localhost:8082/service-instances/inventory-service
  
* After enabling DisconveryClient implementation of Eureka-client, provide the url of eureka-server to `inventory-service` so that it knows which url to use to connect `Eureka-Server`. (see [bootstrap.properties](https://github.com/thedevd/techBlog/blob/master/springboot/microservices/inventory-microservice/src/main/resources/bootstrap.properties))
  ```
  # Url of Eureka-server for service registry
  eureka.client.serviceUrl.default-zone=http://localhost:8761/eureka/
  ```
That's it, service has been configured for Service-Registry operation with Eureka-Server. So when a eureka-client enabled service starts then it connects with Eureka-server and registers itself with Eureka, it provides `meta-data` about itself — such as `host, port, health indicator URL,and other details`. Eureka-Server receives heartbeat messages from each instance belonging to a service. If the heartbeat fails over a configurable timetable, the instance is normally removed from the registry.

Lets verify that `inventory-service` is getting registered with Eureka-service properly-
* Make sure our eureka-server [netflix-eureka-naming-server](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/netflix-eureka-naming-server) is started and running on port 8761. Hit http://localhost:8761 to check the console, initially when no service is registered you will see nothing under `Instances currently registered with Eureka` section.
* Now start two instances of [inventory-service](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/inventory-microservice) application one by one. Run the first one on port 8082 and second on 8083. On startup each instance will register itself with eureka-server.
* Hit http://localhost:8761 url of eureka-server and check the console, under `[Instances currently registered with Eureka]` section, you will see two instances of `inventory-service` registered .
  <p align="center">
    <img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/eureka-server-console.png"/>
  </p>
  
`Do the same configuration for our product-catalog-inventory service`
