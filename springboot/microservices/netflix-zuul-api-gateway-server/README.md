## Netflix Zuul - An API Gateway
<p align="center"><img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/general-api-gateway-architecture.png"/></p>

* In a typical microservice architecture we have many small microservices with multiple instances running on different host and port. 
* So in this situation to access a particular instance of the microservice , clients (browsers/mobile) need to know the host and port on which that particular microservice is running, and this is very problematic from client perspective as they can not access the end microservices without knowing their port and host. (and it is very hard for client to remember port and host for each microservice).
* What we need here is a common entry point to our all microservices that should be able to decide where to route the request. `By using a common entry point we will not only free our clients from knowing the deployment details of all the end microservices but also we can put common aspects like authentication/authorization/monitoring/logging at this level, so all these common aspects will be applied on each request and thus this will reduce significant development effort on the end microservices side.`
* This common entry point is termed as `API Gateway`. Netflix has created `Zuul server` for the same purpose and has open-sourced it and spring cloud community has provided a nice wrapper around it for easy integration with spring boot based microservice styled application. 

**Note-** `Spring also has its own api gateway called Spring Cloud Gateway.` It has non-blocking APIs and supports long-lived connections like WebSockets.

### Topic of discussion
In this we will discuss on three topics -
1. Setup of netflix-zuul API gateway proxy.
2. Demonstration of end user interacting with microservices through Zuul API gateway.
3. Configuration of Microservices for microservice-to-microservice communication through Zuul API gateway.

#### 1. Setup of Netflix-Zuul proxy server
* As we know the best way of creating spring boot skeleton is to use [Spring intitializer](https://start.spring.io/). So create a maven project there and add these dependencies-
  * Zuul
    ```xml
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
    </dependency>
    ```
  * Eureka client
    ```xml
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    ```
 * To make application act as Zuul proxy server, add `@EnableZuulProxy` annotation to main class-
   ```java
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
   import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

   @SpringBootApplication
   @EnableZuulProxy // To make it a Zuul proxy server, added this annotation
   @EnableDiscoveryClient
   public class NetflixZuulApiGatewayServerApplication {
     
     public static void main(String[] args) {
       SpringApplication.run(NetflixZuulApiGatewayServerApplication.class, args);
     }
   }
   ```
 * We will be running Zuul proxy server on port 8765. And we will also need Zuul to register with eureka-server (this is needed for microservice-to-microservice communication through Zuul, so that each end microservice will have the information about the host and port of Zuul in order to communicate with other microservices). See [application.properties](https://github.com/thedevd/techBlog/blob/master/springboot/microservices/netflix-zuul-api-gateway-server/src/main/resources/application.properties)
   ```
   spring.application.name=netflix-zuul-api-gateway-server
   server.port=8765

   # zuul api-gateway will also register with eureka.
   eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka
   ```
`That's it, we are done with configuring Zuul api gateway proxy server. In the next section we will see how client can now make request to a microservice via Zuul.`

#### 2. Client to microservice communication via Zuul
<p align="center"><img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/rest-calls-without-zuul.png"/></p>
<p align="center"><img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/rest-calls-with-zuul.png"/></p>

* The first image shows the direct interaction of client to end microservices without Zuul, where clients should know the port and host of the microservice they want to connect. The second image shows, we have now Zuul as common entry point means it receives all the requests coming from the clients(browser/mobile) and then delegates the requests to correct internal microservice. So in this clients does not need to know the port and host of the microservice they want to connect, this things is now handled by Zuul proxy server with the help of Eureka-Server having the service registry.
* Let's test how client's request is executed/routed through Zuul proxy server which we have configured in first step. Before we do this we are assuming-
  * Already a [Eureka-Server](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/netflix-eureka-naming-server) is running on port 8761.
  * Atleast single instance of [product-catalog-service](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/product-catalog-microservice) is running on port 8092 (.. 8093). (Keep in mind the `spring.application.name` property which is `product-catalog-service`). This app has exposed an API to query the product catalog details which is - `/api/product/{productCode}`
  * Atleast single instance of [inventory-service](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/inventory-microservice) is running on port 8082 (.. 8083).(Keep in mind the `spring.application.name` property which is `inventory-service`). This service has exposed an API to query inventory details of a product - `/api/inventory/{productCode}`
  * [Zuul API gateway](https://github.com/thedevd/techBlog/edit/master/springboot/microservices/netflix-zuul-api-gateway-server/) is running on port 8765. (Note-`You might have to wait for couple of minutes for Zuul to come into action`
  
  **The url structure for accessing the microservice through Zuul is going to be** \
  `http://{zuul-host}:{zuul-port}/{application-name}/{api-url}`.
  * So to access `/api/product/{productCode}` api of `product-catalog-service`, the GET url will be - \
    GET http://localhost:8765/product-catalog-service/api/product/p10001
    ```
    {
    "id": 10001,
    "productCode": "p10001",
    "productName": "p10001 name",
    "description": "p10001 description",
    "availableQuantity": 100,
    "inventoryServicePort": "8082"
    }
    ```
    **Behind the scene -** here we made a call to `/api/product/{productCode}` api of `product-catalog-service`through Zuul. First Zuul will check with Eureka if there is any service registered as product-catalog-service in service registry. If it's there, it will get the port and host for the product-catalog-service and append to the URL part to complete the rest API url and make the call. (Also, Zuul is Ribbon aware, so it will automatically load balance the call if there are multiple instance of the backend service running)
  * Similarly inventory-service's api `/api/inventory/{productCode}` can be accessed through Zuul using the GET url- \
    GET http://localhost:8765/inventory-service/api/inventory/p10001
    ```
    {
    "id": 10001,
    "productCode": "p10001",
    "availableQuantity": 100,
    "port": "8082"
    }
    ```
  **So we can see that, now clients do not need to know the port and host of the microservices, instead they only need to know about Gateway service host and port along with application-name of the service and the api path; it is the Gateway's responsibility to route the service to the appropriate microservice.**
  
#### 3. Microservice-to-microservice communication through Zuul
* Previously, we have seen how Zuul helps clients to communicate with appropiate microservice without knowing its deployment details (host and port). So it was a basically client-to-microservice communication through zuul, now we also want microservice-to-microservice communication to happen though zuul. We have already that scenario with `product-catalog-service` which internally makes call to `inventory-service` to get the inventory detail of the product. It is the perfect example as of now to demonstrat the microservice-to-microservice communication through Zuul.
* As of now, product-catalog-service is calling inventory-service directly using FeignClient, where it takes help of Eureka to find out up and running instances of inventory-service. See [InventoryServiceFeignClient.java](https://github.com/thedevd/techBlog/blob/master/springboot/microservices/product-catalog-microservice/src/main/java/com/thedevd/springboot/service/InventoryServiceFeignClient.java) which is acting as proxy for inventory-service.
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
* So seeing above, instead of telling FeignClient to connect with inventory-microservice directly, we want to force FeignClient to send the further request of inventory-service to Zuul API gateway which intern will route the request to inventory-microservice with help of eureka. To do this you just need to pass the application-name of Zuul to FeignClient and then update all the mappings to start with application-name of inventory-microservice, i.e.
   ```java
   // @FeignClient(name = "inventory-service", url = "localhost:8082")
   // @FeignClient(name = "inventory-service")
   // zuul: tell the Feign client to talk to Zuul API gateway instead of  end microservice.
   @FeignClient(name = "netflix-zuul-api-gateway-server")
   // @RibbonClient(name = "inventory-service")
   public interface InventoryServiceFeignClient {

	// zuul: update all the mappings to start with application-name of inventory-microservice application.
	//@GetMapping("/api/inventory/{productCode}")
	@GetMapping("/inventory-service/api/inventory/{productCode}")
	public InventoryItemResponse getInventoryByProductCode(@PathVariable("productCode") String productCode);
   }
   ```
   see [InventoryServiceFeignClient.java](https://github.com/thedevd/techBlog/blob/master/springboot/microservices/product-catalog-microservice/src/main/java/com/thedevd/springboot/service/InventoryServiceFeignClient.java).\
   **Note- Ribbon is auto integrated with Zuul, so you may also remove @RibbonClient annotation from FeignClient.**
* After we configured FeignClient to talk Zuul API gateway, we are good to go to verify the product-catalog-microservice -to- inventory-microservice communication through Zuul. But before we do this, lets implement a ZuulFilter which we will use to log the information of request recieved at Zuul API gateway server.

  ```java
  import javax.servlet.http.HttpServletRequest;
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  import org.springframework.stereotype.Component;
  import com.netflix.zuul.ZuulFilter;
  import com.netflix.zuul.context.RequestContext;
  import com.netflix.zuul.exception.ZuulException;

  @Component
  public class ZuulRequestLoggingPreFilter extends ZuulFilter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public boolean shouldFilter() {
		return true; // true means should be applied for each request.
	}

	@Override
	public Object run() throws ZuulException {
		// run method is the place where our filter logic is placed
		RequestContext reqCtx = RequestContext.getCurrentContext();
		HttpServletRequest httpRequest = reqCtx.getRequest();

		logger.info("Request method: {}, Request Url: {}", httpRequest.getMethod(),
				httpRequest.getRequestURL().toString());

		return null;
	}

	@Override
	public String filterType() {
		// Zuul supports 4 types of filters - pre,post,route and error
		return "pre";
	}

	@Override
	public int filterOrder() {
		// decide in which order filter to be applied
		return 1;
	}

  }
  ```
  This is a pre-type filter means this will be executed before request is routed to other end by Zuul. In this filter we are just logging the information of request like request-method, request-url. It means when the product-catalog-service will send request of calling inventory-service to Zuul, we will come to know about this with the help of this filter. (Also we can create such type of other filters at Zuul level to do lot of common aspects against the incoming requests like- authentication/authorization/monitoring).
* Lets verify if actually the microservice-to-microservice communication is happening through Zuul. Before we go ahead make sure -
  * [Eureka-Server](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/netflix-eureka-naming-server) is running on port 8761.
  * [Netflix-Zuul API gateway proxy server](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/netflix-zuul-api-gateway-server) is running on port 8765.
  * [inventory-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/inventory-microservice) is running on port 8082.
  * [product-catalog-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/product-catalog-microservice) is running on port 8092.
  
  Send the request to Zuul for getting the product-catalog details of a product with productCode 'p10001'- \
  * GET http://localhost:8765/product-catalog-service/api/product/p10001 \
    The response it is going to return is -
    ```
    {
    "id": 10001,
    "productCode": "p10001",
    "productName": "p10001 name",
    "description": "p10001 description",
    "availableQuantity": 100,
    "inventoryServicePort": "8082"
    }
    ```
  * Now go to the console of Zuul api gateway server, you will see something like this -
    ```
    INFO 12044 --- [nio-8765-exec-5] c.t.s.f.ZuulRequestLoggingPreFilter      : Request method: GET, Request Url: http://localhost:8765/product-catalog-service/api/product/p10001
    INFO 12044 --- [nio-8765-exec-6] c.t.s.f.ZuulRequestLoggingPreFilter      : Request method: GET, Request Url: http://RENLTP2N025.mshome.net:8765/inventory-service/api/inventory/p10001
    ```
    **You can see we have logging information for two request. First line is for the request we sent to product-catalog-service through Zuul API gateway (http://localhost:8765/product-catalog-service/api/product/p10001) and second for the request that product-catalog-service internally sent to inventory-service through Zuul (http://RENLTP2N025.mshome.net:8765/inventory-service/api/inventory/p10001). So this prooves that our microservice-to-microservice communication is happending pefectly via Zuul.**
    
    <p align="center"><img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/microservice-to-microservice-comm-via-zuul.png"/></p>


Similary [order-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/order-microservice) is calling [customer-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/customer-microservice) via Zuul to get the customer details for a particular order (such as customer-name, customer-address, customer-emailid).
```java
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "customer-service", url = "localhost:8062")
// @FeignClient(name = "customer-service")
//zuul: tell the Feign client to talk to Zuul API gateway instead of  end microservice.
@FeignClient(name = "netflix-zuul-api-gateway-server")
// @RibbonClient(name = "customer-service")
public interface CustomerServiceFeignClient {

	// zuul: update all the mappings to start with application-name of inventory-microservice application.
	//@GetMapping("/api/customer/phone/{phoneNo}")
	@GetMapping("/customer-service/api/customer/phone/{phoneNo}")
	public CustomerDetailResponse getCustomerDetailsByPhoneNo(@PathVariable("phoneNo") String phoneNo);
}
```
