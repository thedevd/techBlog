## Use of Feign Client to call other microservices
product-catalog-service need to call inventory-service to fetch the availableQuantity of a product in inventory store. We will see how product-catalog-service can call inventory-service's API using `FeignClient`.
* Spring Cloud OpenFeign is a declarative REST client which can be used by a micro-service to call another microservice.
* **How to include Feign Client**
  * To include Feign in project use the `spring-cloud-starter-openfeign` dependency -
  
    ```
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    ```
  * Enable the Feign client support in main class of the application using `@EnableFeignClients` annotation -
    ```java
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.cloud.openfeign.EnableFeignClients;

    @SpringBootApplication
    @EnableFeignClients
    public class ProductCatalogMicroserviceApplication {

	    public static void main(String[] args) {
		    SpringApplication.run(ProductCatalogMicroserviceApplication.class, args);
	    }

    }
    ```
    By doing this, spring will be auto scanning all the interfaces which are acting as FeignClient.
* Now create a FeignClient using the `@FeignClient` annotation -

    ```java
    import org.springframework.cloud.openfeign.FeignClient;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;

    @FeignClient(name = "inventory-service", url = "localhost:8082")
    public interface InventoryServiceFeignClient {

	    @GetMapping("/api/inventory/{productCode}")
	    public InventoryItemResponse getInventoryByProductCode(@PathVariable("productCode") String productCode);
    }
    ```
    In the above example, we have created a Feign client to read from `inventory-service` which has the base url as `localhost:8082`. Furthermore, we have added the APIs of `inventory-service` that we want to call from `product-catalog-service`.\
    `The major drawback of using Feign client without Ribbon (load balancer) or Eureka (Service Registry) is that you have to use hardcoded url and if there is some other instance of inventory-service we want to use, we have to modify that url value each time which is is not recommended at all. We will see how this problem can be resolved when FeignClient is used with Ribbon or Eureka.`

## Use of Ribbon (A Load balancer)
In this we will see how we can make use of Ribbon (Load balancer) to load balance the request.
* In this demo we will see how to configure our product-catalog-service to allow FeignClient to communicate with Load balancer component called Ribbon. 
* We will run more than two instances of inventory-service (first on 8082 port, second on 8083 and so on) and then configure the Ribbon in our product-catalog-service so that when it will make call to the APIs of inventory-service, the request will be routed to one of the running instance of inventory-service.
  <p align="center">
    <img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/ribbon-load-balance-inventory-service.png">
  </p>
  
* **How to enable support of Ribbon**
  * To include Ribbon in project, add this starter dependency of ribbon provided by Spring-Cloud-Starter-Netflix -
    ```
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
    </dependency>
    ```
  * Modify our existing Feign client interface to inform FeignClient to use RibbonClient (to communicate to load balanaced inventory-service instances). To do this also use `@RibbonClient` annotation on the client class.
    ```java
    import org.springframework.cloud.netflix.ribbon.RibbonClient;
    import org.springframework.cloud.openfeign.FeignClient;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;

    // @FeignClient(name = "inventory-service", url = "localhost:8082")
    @FeignClient(name = "inventory-service")
    @RibbonClient(name = "inventory-service")
    public interface InventoryServiceFeignClient {
    
        @GetMapping("/api/inventory/{productCode}")
	public InventoryItemResponse getInventoryByProductCode(@PathVariable("productCode") String productCode);
    }
    ```
    
    **Now you can see, we do not have to hard code the url in FeignClient to let it know which instance to talk. Now it has become the resposibility of Ribbon to provide list of available servers/instances of inventory-service to FeignClient. So the next step is to provide list of inventory-service's servers to Ribbon module. (see the next step)**
  * Add the `inventory-service.ribbon.listOfServers` to the configuration file for product-catalog-service. (As we have spring-cloud-config-server to manage the configuration, so I have added this property in centralized git repository, see [product-catalog-service.properties](https://github.com/thedevd/ecom-microservices-config-repo/blob/master/product-catalog-service.properties) 
    ```
    # Ribbon (Load balancer). Specify the list of inventory-service instances that you want to 
    # load balanced when product-catalog-service need to call APIs of inventory-service.
    inventory-service.ribbon.listOfServers=http://localhost:8082,http://localhost:8083
    ```
 * Lets see the action of Load-balancer which we have configured so far-
   * Make sure spring-cloud-config-server is running, because each service on startup will talk to config-server for the required configuration.
   * Start two instances of inventory-service - first on 8082 port and second on 8083 port (use -Dserver.port property to launch them in 8082 and 8083 port respectively). For demo we are persisting some inventory details for several products (see [data.sql](https://github.com/thedevd/techBlog/blob/master/springboot/microservices/inventory-microservice/src/main/resources/data.sql)).
   * Now start the product-catalog-service which internally talks to inventory-service to fetch availableQuantity of the product. The product-catalog-service is made to run on port 8092 by default. For demo we are persisting some products in the database on application startup, see [data.sql](https://github.com/thedevd/techBlog/blob/master/springboot/microservices/product-catalog-microservice/src/main/resources/data.sql). Lets see the product catalog details using the following api -\
     GET http://localhost:8092/api/product/p10000
     ```
     {
     "id": 10000,
     "productCode": "p10000",
     "productName": "p10000 name",
     "description": "p10000 description",
     "availableQuantity": 250,
     "inventoryServicePort": "8082"
     }
     ```
     Lets hit the same api again and see which port get displayed 
     ```
     {
     "id": 10000,
     "productCode": "p10000",
     "productName": "p10000 name",
     "description": "p10000 description",
     "availableQuantity": 250,
     "inventoryServicePort": "8083"
     }
     ```
     **Conclusion- You can see that, when we call the above API first time, the request for fetching availableQuantity goes to one of the instance of inventory-service running on port 8082. And on second time the request routed to different instance of inventory-service which runs on 8083. This proves that our Ribbon (Load-Balancer) is working expected**
     
* **Drawback of using Ribbon alone**
 You might have noticed that, the product-catalog-service (which want to call inventory-service) need to have inventory-service's listOfServers (see above) which is hardcoded list. So incase there is a new instance of inventory-service is added in the application, we also need to make change in the `inventory-service.ribbon.listOfServers` property in the configuration file of product-catalog-service. This makes it more troublesome when the instances are frequently added and removed, so use of Ribbon alone is not sufficient when you have large no of services talking to each other. **And this is the place where Naming Server comes into the picture, where you do not have to hard code the listOfServers that load-balancer has to talk.**
