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
* Configuration management -> Spring Cloud Config Server
* Service Registration and Discovery  (aka Naming Server) -> eureka  
* Load balancing -> Ribbon
* Fault tolerance -> Hystrix
* Circuit breaker
* One-time tokens 
* Distributed sessions
* Global locks
* Leadership election and cluster state monitoring
* Distributed tracing (Debugging and testing) -> Zipkin Distributed tracing
* Login/Security -> Netflix Zuul API gateway

## [Configuration management: Spring-Cloud-Config-Server](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/spring-cloud-config-server)
* Need of configuration manager in microservice architecture.
* Setting up config-server using spring-cloud-config-server
* Configure a microservice to communicate with config-server.
   
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
