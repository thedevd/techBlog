## Netflix Zuul - An API Gateway
* In a typical microservice architecture we have many small microservices with multiple instances running on different host and port. 
* So in this situation to access a particular instance of the microservice , clients (browsers/mobile) need to know the host and port on which that particular microservice is running, and this is very problematic from client perspective as they can not access the end microservices without knowing their port and host. 
* What we need here is a common entry point to our all microservices that should be able to decide where to route the request. `By using a common entry point we will not only free our clients from knowing the deployment details of all the end microservices but also we can do lot of things like authentication/authorization/logging/tracing at this level which will reduce significant development effort on the end microservices side.`
* This common entry point is termed as `API Gateway`. Netflix has created `Zuul server` for the same purpose and has open-sourced it and spring cloud community has provided a nice wrapper around it for easy integration with spring boot based microservice styled application. 

**Note-** `Spring also has its own api gateway called Spring Cloud Gateway.` It has non-blocking APIs and supports long-lived connections like WebSockets.

### Topic of discussion
In this we will discuss on three topics -
1. Setup of netflix-zuul API gateway proxy.
2. Demonstration of end user interacting with microservices through Zuul API gateway.
3. Configuration of Microservices for microservice-to-microservice communication through Zuul API gateway.

#### 1. Setup
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
