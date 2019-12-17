## Use of Feign Client to call other microservices
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
    `The major drawback of using Feign client without Ribbon (load balancer) or Eureka (Service Registry) is that you have to use hardcoded url and if there is some other instance of inventory-service we want to use, we have to modify that url value each time which is is not recommended at all. We will see how this is prevented when FeignClient is used with Ribbon or Eureka.`
    
