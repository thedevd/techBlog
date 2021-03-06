## [Spring Cloud Config Server](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/inventory-microservice) (Configuration Manager)
One of the challange in microservice architecture is managing the external configurations for microservices. In typical microservice architecture we usually have number of instances of services running with mutliple environments, what I mean - 
* lets say we have three microservices [microservice-1, microservice-2 and microserver-3]. Now suppose we have different different external configurations specific to environment (dev/qa/prod) for each of the services. So the `problem is How to enable a service to run in multiple environments without modification`.
* One way of managing these external configurations is to keep all environment specific external configuration withing microservice itself, but as soon as you scale your applications to run large no of services on multiple environments, it becomes difficult to manage those configurations which are stored in microservice level.
* Better way to manage them is to store all external configurations in a central place which makes managing those external properties/configurations easy for services across all environments. Another advantage of keeping them in centralized repository is that as an application moves through the deployment pipeline from dev to test and then into production, we can easily manage the configuration between those environments and be assured that applications have everything they need to run when they are migrated. So in nutshell we would have these benefits -
  * A service can be run in multiple environments - dev, test, qa, staging, production - without modification and/or recompilation.
  * Different environments have different instances of the external/3rd party services, so a service can be provided with required configuration data that tells it how to connect to the external/3rd party services based on the environment type. For example, the database network location and credentials.

`Spring-Cloud-Config-Server is the module in the spring cloud which is used to manage the external configurations stored in centralized repostitory`

<p align="center">
<img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/spring-cloud-config-server.png">
</p>

* The default implementation of cloud-config-server uses `Git` as centralized configuration repository. 
* **Steps to create Spring-Cloud-Config-Server talking to a git repository** -
  * First step is to create a seperate Git repository where we will be storing configurations for microservices.\
    I have created [ecom-microservices-config-repo](https://github.com/thedevd/ecom-microservices-config-repo) for the same.
  * Create a spring boot project using [Spring Initializr](https://start.spring.io), and select the dependency for spring-cloud config server
    ```
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
    ```
    I have created [spring-cloud-config-server](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/spring-cloud-config-server) for the same.
  * Now we have to tell our spring-cloud-config-server project about how to talk to git repository which we have created in first step. To do this open [application.properties](https://github.com/thedevd/techBlog/blob/master/springboot/microservices/spring-cloud-config-server/src/main/resources/application.properties) of the project and add properties for `spring.cloud.config.server.git` -
    
    ```
    spring.application.name = spring-cloud-config-server
    server.port = 8888
    
    # Let the spring cloud config server know the git repo where configurations are stored
    spring.cloud.config.server.git.uri=file://C:/Users/Dell/mygithub/ecom-microservices-config-repo
    
    #spring.cloud.config.server.git.uri=https://github.com/thedevd/ecom-microservices-config-repo
    #spring.cloud.config.server.git.username=github_username
    #spring.cloud.config.server.git.password=github_password
    #spring.cloud.config.server.git.clone-on-start=true
    ```
    **Note- For simplicity I have used local filesystem URI to point to the centralized configuration storage. (For this first you need to clone the configuration git repository (created in first step) in your local filesystem)**
    
  * `This is very important and last step`. Enable the cloud config server on the startup, to do this use `@EnableConfigServer` annotation on the main class of the spring-cloud-config-server project -\
    [SpringCloudConfigServerApplication.java](https://github.com/thedevd/techBlog/blob/master/springboot/microservices/spring-cloud-config-server/src/main/java/com/thedevd/springboot/SpringCloudConfigServerApplication.java)
    ```java
    @SpringBootApplication
    @EnableConfigServer // this is required to enable spring-cloud-config-server
    public class SpringCloudConfigServerApplication {

	  public static void main(String[] args) {
		  SpringApplication.run(SpringCloudConfigServerApplication.class, args);
	  }
    ```
    
## Microservice to spring-cloud-config-server communication
After we have spring-cloud-config-server created for configuration management, now we have to connect our microservice to cloud-config-server in order to fetch environment specific configuration. Once we establish the communication b/w microservice and cloud-config-server then we will see upon startup microservice will connect to config-server and ask for a configuration specific to an environment.
* For demonstration, I have created a microservice called [inventory-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/inventory-microservice). And We want to maintain seperate configurations for dev and qa envrionment.
  ```
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
  </dependency>
  ```
* Very first step is rename the application.properties file of microservice to [bootstap.properties](https://github.com/thedevd/techBlog/blob/master/springboot/microservices/inventory-microservice/src/main/resources/bootstrap.properties) and there mention the `uri` to spring-cloud-config server.
   ```
   spring.application.name=inventory-service
   
   # Let the application know from where to get the configuration of a specific environment.
   # (i.e. provide uri of config-server)
   spring.cloud.config.uri=http://localhost:8888
   ```
  **Note- Take a special care with application name defined by spring.application.name. This name is very important when creating the environment specific configuration in centralized Git configuration repository.** 
 
 * Next step is to create the envrionment specific configuration for our inventory-microservice in the centralized Git configuration repository and commit the files. `Take a special care while giving the name to the configuration file, the name should be the combination of application-name of the microservice and profile name.`\
   * [inventory-service-dev.properties](https://github.com/thedevd/ecom-microservices-config-repo/blob/master/inventory-service-dev.properties)- this is for dev environment's configuration
   * [inventory-service-qa.properties](https://github.com/thedevd/ecom-microservices-config-repo/blob/master/inventory-service-qa.properties)- this is for qa environment's configuration
   * [inventory-service.properties](https://github.com/thedevd/ecom-microservices-config-repo/blob/master/inventory-service.properties) - this is default configuration.
   
   **Note- Do not forget to commit the changes to git configuration repository**
  
* **Verify the connection to spring-cloud-config-server** -
  * Start the spring-cloud-config-server. (This is made to run on port 8888)
  * Start the [inventory-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/inventory-microservice). Provide the VM argument `-Dspring.profiles.active` to specify which enviroment's configuration it will fetch from cloud-config-server. (if no profile is given then default configuration will be fetched). On startup you will see this kind of log message in the microservice console -
     ```
     INFO 2020 --- [  restartedMain] c.c.c.ConfigServicePropertySourceLocator : Fetching config from server at : http://localhost:8888
     INFO 2020 --- [  restartedMain] c.c.c.ConfigServicePropertySourceLocator : Located environment: name=inventory-service, profiles=[dev], label=null, version=cb2ad5ac4552fefdcbd549043977e1c2f914422b, state=null
     INFO 2020 --- [  restartedMain] b.c.PropertySourceBootstrapConfiguration : Located property source: OriginTrackedCompositePropertySource {name='configService', propertySources=[MapPropertySource {name='configClient'}, OriginTrackedMapPropertySource {name='file://C:/Users/Dell/mygithub/ecom-microservices-config-repo/inventory-service-dev.properties'}, OriginTrackedMapPropertySource {name='file://C:/Users/Dell/mygithub/ecom-microservices-config-repo/inventory-service.properties'}]}
     INFO 2020 --- [  restartedMain] c.t.s.InventoryMicroserviceApplication   : The following profiles are active: dev
     ```
  * We can also take a look at the environment specific configuration of a micro-service using these endpoints exposed by spring-cloud-config-server itself.\
    http://localhost:8888/inventory-service/default
    ```
    {
	"name": "inventory-service",
	"profiles": [
		"default"
	],
	"label": null,
	"version": "ac91b918c8c7b2a8e23744c044107c499572157c",
	"state": null,
	"propertySources": [
		{
			"name": "file://C:/Users/Dell/mygithub/ecom-microservices-config-repo/inventory-service.properties",
			"source": {
				"spring.jpa.show-sql": "true",
				"spring.h2.console.enabled": "false",
				"inventory-service.dummy.property1": "default_dummyvalue1",
				"inventory-service.dummy.property2": "default_dummyvalue2"
			}
		}
	]
    } 
    ```
    http://localhost:8888/inventory-service/dev
    ```
    {
	"name": "inventory-service",
	"profiles": [
		"dev"
	],
	"label": null,
	"version": "ac91b918c8c7b2a8e23744c044107c499572157c",
	"state": null,
	"propertySources": [
		{
			"name": "file://C:/Users/Dell/mygithub/ecom-microservices-config-repo/inventory-service-dev.properties",
			"source": {
				"spring.jpa.show-sql": "true",
				"spring.h2.console.enabled": "true",
				"inventory-service.dummy.property1": "dev_dummyvalue1",
				"inventory-service.dummy.property2": "dev_dummyvalue2"
			}
		},
		{
			"name": "file://C:/Users/Dell/mygithub/ecom-microservices-config-repo/inventory-service.properties",
			"source": {
				"spring.jpa.show-sql": "true",
				"spring.h2.console.enabled": "false",
				"inventory-service.dummy.property1": "default_dummyvalue1",
				"inventory-service.dummy.property2": "default_dummyvalue2"
			}
		}
	]
    }
    ```
## Problem with Spring Cloud Config server without Spring Cloud Bus
* Suppose we have three instances of `inventory-service` running with dev profile, it means each instance are using dev profile's external configurations fetched from centralized git repository through spring-cloud-config-server. And later point of time you want to change some property for dev's profile (i.e. `inventory-service-dev.properties`).
* So you made changes in the `inventory-service-dev.properties` stored in `centralized git repository` and committed the changes. Now the question is how to reflect this particular change in each three instances of inventory-service.
* The straight-forward way to reflect any changes done in the external configuration stored in centralized git repo is to take help of **spring-boot-actuator**. There is an endpoint provided by Actuator to refresh the configuration in the application, and that endpoint is - POST `/actuator/refresh`. \
  **Note- By default this endpoint is restricted for security reason. So for demo I have disabled it and thus allowed Actuator to expose all its endpoints i.e. health,info,beans,env,myendpoints**. And to enable this, add this property in [bootstrap.properties](https://github.com/thedevd/techBlog/blob/master/springboot/microservices/inventory-microservice/src/main/resources/bootstrap.properties) file of inventory-service.
  ```
  # management.endpoints.web.exposure.include is related to actuator. * means enable all the endpoints of actuator i.e.
  # health,info,beans,env,myendpoints
  management.endpoints.web.exposure.include=*
  ```
* There is again a problem of using the above mentioned Actuator endpoint to refresh configuration. Now imagine we have 100 of inventory-service instance are running, so it is not at all good practice to hit `/actuator/refresh` endpoint for each instance, this is like an overhead of maintaince activity of going to every single instance and reload configuration by accessing actuator endpoint to refersh configuration. **Spring-Cloud-Bus is a way to solve this problem that prevents invoking the same endpoint on each instance of microservice to refersh the configuration.**  

### Spring Cloud Bus
Spring Cloud Bus links nodes of distributed system using a lightweight message broker (AMQP based broker such as RabbitMQ, ActiveMQ). The primary use of Spring Cloud bus is to broadcast configuration changes related to a particular microservice in the application.

Let's try to solve our problem of refreshing configuration in each instance of inventory-service using Spring-Cloud-Bus. In this we are going to use RabbitMQ as AMPQ based message broker. To install RabbitMQ, I recommend use of docker image, this how to download dockerized RabbitMQ image and start it (Before this make sure Docker is installed and running) -
#### 1. RabbitMQ docker setup -
```
> docker pull rabbitmq:3-management 
```
```
> docker run -d --name my-rabbitmq -p 15672:15672 -p 5672:5672 rabbitmq:3-management
```
Open http://localhost:15672, which will show the management console login screen. The default username/password `guest/guest`. RabbitMQ will also listen on port 5672.

#### 2. Updated config-sever and config-client to configure spring-cloud-bus and subscribe RabbitMQ exchange-
Add these dependencies in spring-cloud-config-server and inventory-microservice, Spring Cloud takes care of the rest -
```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-bus</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-stream-binder-rabbit</artifactId>
</dependency>
```
* The `spring-cloud-bus` dependency will enable the Spring-Cloud-Bus support and provide a `/actuator/bus-refresh` endpoint inside Actuator. (this endpoint is similar to `/actuator/refresh` endpoint but there is a major difference that we will see in next section)
* The `[spring-boot-starter-amqp`, `spring-cloud-stream-binder-rabbit]` dependencies will allow application to connect and subscribe RabbitMQ (on port 5672 as default) and now application will start listening for configuration refresh events.

#### 3. Test the Spring-Cloud-Bus
* First start these applications in following order
  * Start [netflix-eureka-naming-server](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/netflix-eureka-naming-server)
  * Start [spring-cloud-config-server](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/spring-cloud-config-server)
  * Start three instances of [inventory-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/inventory-microservice) on port 8082,8083,8084. And also specify the profile as "dev" to request config-server for dev environment configuration which is stored in [inventory-service-dev.properties](https://github.com/thedevd/ecom-microservices-config-repo/blob/master/inventory-service-dev.properties) file.
    `Use -Dspring.profiles.active -Dserver.port` to specify profile and port to use. \
    Ex - `-Dserver.port=8083 -Dspring.profiles.active=dev`
    
    You can use `/actuator/env` endpoints to look at what configurations each instance is using.
    
 * Change the configuration related to dev profile i.e. in the file [inventory-service-dev.properties](https://github.com/thedevd/ecom-microservices-config-repo/blob/master/inventory-service-dev.properties). Suppose changing these both dummy properties -
   ```
   #inventory-service.dummy.property1=dev_dummyvalue1
   #inventory-service.dummy.property2=dev_dummyvalue2
   inventory-service.dummy.property1=dev_dummyvalue1_changes
   inventory-service.dummy.property2=dev_dummyvalue2_changed_too
   ```
* Now it is time to refresh these changed configuration in each three instance of inventory-microservice. To do this you only need to invoke the `/actuator/bus-refesh` endpoint for one of the instance only, spring cloud bus will take care of refershing the same on remaining instances. So **This way, we don't need to go to individual nodes and trigger configuration update which was needed in case not using spring-cloud-bus.**

  For example- we invoke the `/actuator/bus-refesh` for instance running on 8083 \
  **POST http://localhost:8082/actualtor/bus-refresh**
  
  After invoking bus-refresh endpoint on one of the node,  a message will be sent to RabbitMQ exchange to inform about configuration refresh event. And all subscribed nodes will then update their configuration automatically.
 
* As a proof, look for this log information in each instance of inventory-service -
  ```
  o.s.cloud.bus.event.RefreshListener      : Received remote refresh request. Keys refreshed 
  [config.client.version, inventory-service.dummy.property1, inventory-service.dummy.property2]
  ```
