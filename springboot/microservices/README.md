This project aims to demostrate building an application (ecommerce) in microservice architecture style using SpringBoot and SpringCloud.

# Table of Contents
**1. General**
* [What is microservice architecture](https://github.com/thedevd/techBlog/tree/master/springboot/microservices#architecture).
* [Microservice architecture advantages](https://github.com/thedevd/techBlog/tree/master/springboot/microservices#microservice-architecture-advantages)
* [Challenges in microservice architecture](https://github.com/thedevd/techBlog/tree/master/springboot/microservices#challenges-in-microservice-architecture)
* [What is Spring Cloud](https://github.com/thedevd/techBlog/tree/master/springboot/microservices#spring-cloud)
   
**2. Building Ecommerce application in microservice architecture style**
   * [Architecture](https://github.com/thedevd/techBlog/tree/master/springboot/microservices#architecture)
   * Spring cloud components to build microservice pattern
     * [Spring-Cloud-Config-Server](https://github.com/thedevd/techBlog/tree/master/springboot/microservices#configuration-management-spring-cloud-config-server) `(Configuration Management)`
     * [OpenFeign](https://github.com/thedevd/techBlog/tree/master/springboot/microservices#service-to-service-call-feignclient) `(REST client for service-to-service calling)`
     * [RibbonClient](https://github.com/thedevd/techBlog/tree/master/springboot/microservices#load-balancer-ribbon-a-client-side-load-balancer) `(A client side load-balancer)`
     * [Netflix-Eureka](https://github.com/thedevd/techBlog/tree/master/springboot/microservices#naming-server-for-service-discovery-netflix-eureka) `(Naming server for service Discovery and Registry)`
     * [Netflix-Zuul](https://github.com/thedevd/techBlog/tree/master/springboot/microservices#api-gateway-netflix-zuul) `(An API Gateway proxy server)`
     * [Sleuth with Zipkin server](https://github.com/thedevd/techBlog/tree/master/springboot/microservices#distribute-tracing-spring-cloud-sleuth-with-zipkin-server) `(Distributed Tracing)`
   * Backend Microservices
     * [product-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/product-catalog-microservice)
     * [inventory-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/inventory-microservice)
     * [customer-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/customer-microservice)
     * [order-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/order-microservice)
     * [review-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/review-microservice)
     * [Port and application name details](https://github.com/thedevd/techBlog/tree/master/springboot/microservices#port-and-application-name-details) (`server.port` and `spring.application.name`)
     
## Microservice architecture advantages
* Technologies agnostics /independent. 
* Easy development/Management and independent deployment (Rapid Release cycle) 
* Dynamic scaling (Distributed capabilities + Cloud support) 
* Fault tolerance. 

## Challenges in microservice architecture
* Configuration management (100's of services + multiple environments + multiple instances) 
* Dynamic scale up and scale down (service discovery + Load balancing) 
* Monitoring 
* Fault-tolerant (Services interacting like Pack of cards i,e,--> m1 -> m2 -> m3 -> m4 -> m5, then how to make fault tolerant system) 

## Spring Cloud 
Spring cloud provides lot of tools which helps developers to `quickly build some common patterns of distributed system`. Some of those patterns are -
* Configuration management -> `Spring Cloud Config Server`, `Spring-Cloud-Bus`
* Service Registration and Discovery  (aka Naming Server) -> `Netflix Eureka`  
* Load balancing -> `Netflix Ribbon`
* Fault tolerance -> `Hystrix`
* Circuit breaker
* One-time tokens 
* Distributed sessions
* Global locks
* Leadership election and cluster state monitoring
* Distributed tracing (Debugging and testing) -> `Spring cloud Sleuth and Zipkin Distributed tracing server`
* API gateway (Authentication/Authorization/logging) -> `Netflix Zuul API gateway`

## Architecture
Microservice architecture style approach says decompose an application into smaller services where each service is 
* independent/self-contained means independent release cycle,
* runs in its own process,
* handles single bussiness domain,
* communicate with other services using lightweight mechanism (Mostly HTTP based RESTful APIs).

Therefor divinding our ecommerce application into these business domain (there could be more, But I have covered important ones)-
* Product
* Inventory
* Customer
* Order
* Review
<p align="center"><img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/ecomm-highlevel-architecture.png"/></p>

## Port and application name details
| Application | Application name | port |
| ----------- | ---------------- | ---- |
| Spring-Cloud-Config-server | spring-cloud-config-server | 8888 |
| Neflix-Eureka Server | netflix-eureka-naming-server | 8761 |
| Netflix-Zuul API Gateway | netflix-zuul-api-gateway-server | 8765 |
| product-microservice | product-catalog-service | 8092,8093... |
| inventory-microservice | inventory-service | 8082,8083... |
| order-microservice | order-service | 8072,8073... |
| customer-microservice | customer-service | 8062,8063... |
| review-microservice | review-service | 8052,8053... |

## [Configuration management: Spring-Cloud-Config-Server](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/spring-cloud-config-server)
* Need of configuration manager in microservice architecture.
* Setting up config-server using spring-cloud-config-server
* Configure a microservice to communicate with config-server.
* Problem in Configuration refersh without using Spring-Cloud-Bus.
* How to setup spring-cloud-bus for easy configuration refresh operation.
   
## [Service-to-Service call: FeignClient](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/product-catalog-microservice#use-of-feign-client-to-call-other-microservices)
* Configure a FeignClient in microservice using Spring-Cloud-OpenFeign.
* Drawback of using FeignClient alone.

## [Load-Balancer: Ribbon-A client side load balancer](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/product-catalog-microservice#use-of-ribbon-a-load-balancer)
* Configure RibbonClient to talk with Ribbon- A client side load-balancer for routing requests to instances of outerworld.
* Drawback of using RibbonClient alone.

## [Naming Server for Service Discovery: Netflix-Eureka](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/netflix-eureka-naming-server)
* What is the need of Naming-server in microservice architecture.
* Setting up the Netflix-Eureka Server
* Configure the Services to register in Eureka-Server.
* [Use of Ribbon with Eureka-Server](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/product-catalog-microservice#use-of-ribbon-with-eureka-server).

## [API Gateway: Netflix-Zuul](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/netflix-zuul-api-gateway-server#netflix-zuul---an-api-gateway)
* What is the need of API gateway in microservice architecture. (What problems it solves)
* Setting up the Netflix-Zuul proxy server.
* Executing a client request using Zuul. (Client-to-microservice communication via Zuul)
* Configuring the FeignClient for microservice-to-microservice communication via Zuul.
* Implementation of simple Pre type ZuulFilter. (For logging request information)

## [Distribute Tracing: Spring Cloud Sleuth with Zipkin Server](https://github.com/thedevd/techBlog/tree/master/springboot/microservices#distribute-tracing-spring-cloud-sleuth-with-zipkin-server)
* In Microservice architecture, it is obviously possible that multiple microservices may be involved to serve a particular client's request where one microservice can in turn call other microservice and so on (like chain of calling), and in real world application this chain of calling could be long. 

<p align="center"><img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/microservice-call-chaining.png"/></p>

  We have already this microservice chaining scenario in our ecommerce sample application where `product-catalog-service` in turn calls `inventory-service`.
* Now the major challange here is if anything goes wrong (exception occured or action is not completed) in this kind of scenario where multiple microservices are involved, then how are you going to debug this. Even if you have logging in each microservice then by looking at the logs of each application, how are you going to correlate those logs across multiple services and decide that they belong to the same user request. So it becomes very difficult to trace a specific user's request and debug the problem in distributed environment. There is one simple solution to correlate the logs across mutltiple-microservice and that is using a unique id to each user's request and passing the same id to the subsequently requests made by a microservice to call others. Doing this manually could be error prone so  `Spring-Cloud-Sleuth helps us to solve this problem of correlating the logs by assigning a unique Id to request and using the same id throughout the complete lifecycle of serving the request`. 
* Spring cloud Sleuth integrates effortlessly with logging frameworks like Logback and SLF4J to add unique identifiers that help to trace and diagnose issues using logs. Let's see how Spring-Cloud-Sleuth works.

  **Note- To demonstrat setup and working of Sleuth we will be referring to our existing scenario of ecommerce sample application where `product-catalog-service` in turn calls `inventory-service`. Although we have configured client-to-microservice and microservice-to-microservice communication via Zuul API gaetway, so we will also configure the Zuul along with product-catalog-service and inventory-service to connect Sleuth for tracing of request across multiple components.**

### Sleuth Setup
* To enable support of Spring-Cloud-Sleuth, add Sleuth dependency in the project (we are adding it to all three components i.e. netflix-zuul-api-gateway, product-catalog-microservice and inventory-micoservice).
  ```xml
  <dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-starter-sleuth</artifactId>
  </dependency>
  ```
  This way we are actually allowing each component to talk to Sleuth.
### Test the Sleuth
* As mentioned, Spring cloud Sleuth integrates effortlessly with logging frameworks like Logback and SLF4J. This means wherever you will log the information, Sleuth will prefix that log with extra information that will identify which log belongs to which request by the help of unique trace Id. So for this purpose we have added some logging in the [Zuul's Filter](https://github.com/thedevd/techBlog/blob/master/springboot/microservices/netflix-zuul-api-gateway-server/src/main/java/com/thedevd/springboot/filters/ZuulRequestLoggingPreFilter.java), [product-catalog-microservice controller](https://github.com/thedevd/techBlog/blob/master/springboot/microservices/product-catalog-microservice/src/main/java/com/thedevd/springboot/controller/ProductCatalogController.java) and [inventory-microservice controller](https://github.com/thedevd/techBlog/blob/master/springboot/microservices/inventory-microservice/src/main/java/com/thedevd/springboot/controller/InventoryItemController.java).
* Start the each components in this order -
  * Start netflix-eureka-naming-server (port 8761)
  * Start netflix-zuul-api-gateway-server (port 8765)
  * Start inventory-service
  * Start product-catalog-service
* Send the request to `product-catalog-service` via Zuul to fetch the details of one of the existing product using its code (p10000) -\
  GET http://localhost:8765/product-catalog-service/api/product/p10000 \
  response-
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
* Now check the console of each component (Zuul, product-catalog-service and inventory-service) and look the logs closely, you will find something like this -
  ```
  Zuul
  
  2019-12-30 15:08:00.487  INFO [netflix-zuul-api-gateway-server,fa871f20fafe1e0f,fa871f20fafe1e0f,false] 11056 
  --- [nio-8765-exec-5] c.t.s.f.ZuulRequestLoggingPreFilter      : 
  Request method: GET, Request Url: http://localhost:8765/product-catalog-service/api/product/p10000
  
  2019-12-30 15:08:00.504  INFO [netflix-zuul-api-gateway-server,fa871f20fafe1e0f,415547afa49d4b66,false] 11056 
  --- [nio-8765-exec-6] c.t.s.f.ZuulRequestLoggingPreFilter      : 
  Request method: GET, Request Url: http://RENLTP2N025.mshome.net:8765/inventory-service/api/inventory/p10000
  ```
  ```
  product-catalog-service
  
  2019-12-30 15:08:00.523  INFO [product-catalog-service,fa871f20fafe1e0f,9fc951b17ada2968,false] 25476 
  --- [nio-8092-exec-2] c.t.s.c.ProductCatalogController         : 
  Each log is prefixed with extra information by Sleuth. product detail requested for productCode: p10000
  ```
  ```
  inventory-service
  
  2019-12-30 15:08:00.517  INFO [inventory-service,fa871f20fafe1e0f,6abc0caec3f20e7f,false] 19832 
  --- [nio-8082-exec-2] c.t.s.c.InventoryItemController          : 
  Each log is prefixed with extra information by Sleuth.  Is inventoryItem for productCode:p10000 present:true
  ```
  <p align="center"><img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/sleuth-testing-demo.png"/></p>
* Look at each log closely, they look like a normal log, except for the part in the beginning and between the brackets for example- `2019-12-30 15:08:00.523  INFO [product-catalog-service,fa871f20fafe1e0f,9fc951b17ada2968,false]`, This is the core information that Spring Sleuth has added. This data follows the format of: \
  **[application name, traceId, spanId, export]**
  * `Application name`- This is the application name which we configure in the properties file (spring.application.name). This can be used to aggregate logs of multiple instances of the same application. 
  * `traceId` -  Each unique user initiated web request will have its own traceId. And the same id will be used in subsequent sub requests made by further microservices to serve the part of the user request.
  * `spanId` - Think of a request that consists of multiple steps. For example in our scenario, fetching a product details via Zuul consists of multiple steps such as - first send request to Zuul, then from Zuul send request to product-catalog-service, then request sent back to Zuul by product-catalog-service in order to talk to inventory-service, then request is forwareded to inventory-service by Zuul etc. \
  **SpanId is used to indentify each step involved in serving the request. Each step could have its own spanId and be tracked individually. By default, any web request flow will start with same TraceId and SpanId (look at the very first log of Zuul, there you can see same traceId and spanId is used**
  * `export` - This property is a boolean that indicates whether or not this log was aggregated and exported to an aggregator like Zipkin. Zipkin is a log-aggregator like logstash or kibana that can be used to aggregate the Sleuth created logs across multiple microservices and provides a UI based dashboard to easily analyze them. 
  
**So we saw how to use Sleuth to add tracing information in logs. But there is a little problem of correlating the logs belonging to one particular user request and that is, manually we have to go to console or log file of each and every microservices which are involved in serving the request and then need to correlated thier logs manually using the traceId and spaneID. So correlation becomes a pain when we have large no of components participating in the complete lifecycle of user request. So instead of going and check individual component's log, we can also export log tracing information to a centralized place such as Zipkin so that we would have all the logs at one place which makes it easy to analyze them easily. In addition to that zipkin also provide a way to visualize the log traces through UI.(In next section we will see usage of Sleuth with Zipking server for tracing and analyzing the logs very easily with the help of UI based dashboard provided by zipkin).**
  
### Zipkin Distributed tracing server
So above, we observed that the tracing information is printed in logs/console but not exported. We can export them to Zipkin server so that we can visualize log traces in Zipkin Server UI Dashboard.
<p align="center"><img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/sleuth-with-zipkin-log-tracing-server.png"/></p>

* As per the image show above, we will use RabbitMQ as middle layer b/w zipkin and application's components. So all the components will be putting log trace into RabbitMQ and Zipkin will be consuming them from rabbitMQ. So the whole thing can be divided into these steps-
  1. Install and run the RabbitMQ.
  2. Install and setup the zipkin server to consume from RabbitMQ.
  3. Configure the components to export log trace to zipkin server via RabbitMQ.

#### 1. RabbitMQ installation
* We will use docker image to install and run RabbitMQ.
  ```
  > docker pull rabbitmq:3-management
  ```
  ```
  > docker run -d --name my-rabbitmq -p 15672:15672 -p 5672:5672 rabbitmq:3-management
  ```
  Open http://localhost:15672, which will show the management console login screen. The default username/password guest/guest. RabbitMQ will also listen on port 5672.

#### 2. Install and setup the zipkin server to consume from RabbitMQ
* We will use docker image to install and run zipkin server. Zipkin uses some persitant storage as backend database, we could use mysql or cassandra. 
* For simplicity we will be using zipkin docker with in-memory storage. (Refer this [url](https://github.com/openzipkin-attic/docker-zipkin) for using zipkin with other storage).
  ```
  > docker pull openzipkin/zipkin-slim
  ```
  ```
  > docker run -d -p 9411:9411 -e RABBIT_ADDRESSES=localhost:5672 -e RABBIT_USER=guest -e RABBIT_PASSWORD=guest --name openzipkin openzipkin/zipkin-slim
  ```
  Once the Zipkin server is started you can go to http://localhost:9411/ to view the Zipkin Server UI Dashboard. We are passing the rabbitMQ related environment variables so that zipkin can listen on RabbitMQ and recieve the log trace information sent by application's components. 

#### 3. Configure the components to export log trace to zipkin server
<p align="center"><img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/sleuth-to-zipking-over-rabbitmq.png"/></p>

* To export trace to zipkin server over RabbitMQ instead HTTP, add the `zipkin client` and `spring rabbit` dependency in the application's component. (If you use spring-kafka, and set `spring.zipkin.sender.type: kafka`, your app will send traces to a Kafka broker). 
* Taking the same scenario here as in Sleuth demo, lets add spring-rabbit and zipkin-client dependency in `[netflix-zuul-api-gateway-server, product-catalog-microservice and inventory-microservice]` components of our ecommerce sample application.
  ```xml
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
  </dependency>
  ```
  ```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
  </dependency>
  ```
* **NOTE: By default, `spring.sleuth.sampler.probability=0.1` which means only 10% of tracing information will be exported to Zipkin. Make it to your desired percentage.**. Value 1 means 100% trace will be exported. So configure this property in properties file of netflix-zuul-api-gateway-server, product-catalog-microservice and inventory-microservice.
  ```
  spring.sleuth.sampler.probability=1
  ```
  And one more thing about sampler in Sleuth, By default Spring Cloud Sleuth sets all spans to non-exportable. So you want to export that too, we can do this by creating Sampler.ALWAYS Bean in the main class. (in this demo we are not doing this as of now).
  ```java
  @Bean
  public Sampler defaultSampler() {
	return Sampler.ALWAYS_SAMPLE;
  }
  ```

### Test the Sleuth with zipkin server
So till this point we have configured sleuth in each components of our demonstration scenario i.e. `[netflix-zuul-api-gateway-server, product-catalog-microservice and inventory-microservice]`, and also configured these component to export the sleuth generated log trace information to zipkin server over RabbitMQ. Now lets see this in action to know how Sleuth with zipkin help us to debug a client request and see how many microservices are taking participation to serve the request.

* Start these components in following order -
  * Start netflix-eureka-naming-server (port 8761)
  * Start netflix-zuul-api-gateway-server (port 8765)
  * Start inventory-service
  * Start product-catalog-service

* Send GET request to product-catalog-service through zuul-api-gateway and fetch details of a existing product with id say p10001 `(You can use any third party restclient to hit a particular HTTP rest api. I use Postman)` \
  url - http://localhost:8765/product-catalog-service/api/product/p10000 \
  output - you should get output like this
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
  
* Now open the zipkin UI using the url - http://localhost:9411/zipkin/. On the UI,select the serviceName as selection criteria for your trace lookup, then You will see list of service names which are exporting log trace information to zipkin server -
  <p align="center"><img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/zipkin-test/zipkin-ui-servicenames.png"/></p>

* Select the serviceName of netflix-zuul-api-gateway-server, because we have sent request to product-catalog-service via zuul (Although all the requests will be routed via gateway-api in microservice based architecture). And search for trace, you will list of trace information for each request sent to zuul-api-gateway. 
  <p align="cetner"><img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/zipkin-test/zipkin-ui-request-traces.png"/></p>
  Looking at the image we can say that the request is served by 3 microservice based components `[netflix-zuul-api-gateway-server, product-catalog-microservice and inventory-microservice]`.

* Select the particular request traceid, then you will see overall flow of the request.
  <p align="center"><img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/zipkin-test/zipkin-ui-trace-details.png"/></p>
  
  Looking at the image we can say that -
  * Request to product-catalog service goes via netflix-zuul-api-gateway. `(1)`
  * netflix-zuul-api-gateway then forwards the request to product-catalog-service. `(2)`
  * To fetch inventory-detail of the product, the product-catalog-service makes a call to inventory-service via netflix-zuul-api-gateway. `(3)`
  * Finally netflix-zuul-api-gatway forwards the request to inventory-service. `(4)`.
  
  The complete response is then returned to the client.
  
* Now let's test negative behavior. We would shutdown the inventory-service and then try to fetch the product details. (Obviously the request will not be completed because when product-catalog-service will go and try to call inventory-service via netflix-zuul-api-gateway, the product-catalog-service will get a Internal-Server-Error response as inventory-service is down. This whole flow can be tracked easily to find out where exactly issue occured. \

  * So shutdown inventory-service for demo purpose and hit the restendpoint to get same product details i.e. http://localhost:8765/product-catalog-service/api/product/p10000. The output would be of `500 Internal Server Error`. Now lets open the zipkin ui and trace that request to know where it failed.
    <p align="center"><img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/zipkin-test/zipkin-ui-request-failed-trace.png"/></p>
  * Select the trace, and you will see overall flow -
    <p align="center"><img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/zipkin-test/zipkin-ui-request-failed-trace-details.png"/></p>
    
    Looking at the image, we can say that the request got failed from inventory-service. `So you can now imaging how much helpful distributed tracing is going to be when you want debug a request which is being served by large no of microservices. So instead of going in each microservice's log, you are given a centalized place where you can go and trace the overall flow of the request.`
    
    
  
  
