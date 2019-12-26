This project aims to demostrate building an application (ecommerce) in microservice architecture style using SpringBoot and SpringCloud.

# Table of Contents
**1. General**
* What is microservice architecture.
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
   * Backend Microservices
     * [product-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/product-catalog-microservice)
     * [inventory-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/inventory-microservice)
     * [customer-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/customer-microservice)
     * [order-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/order-microservice)
     * [review-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/review-microservice)
     * Port and application name details. (server.port and spring.application.name)
     
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
* API gateway (Authentication/Authorization/logging) -> Netflix Zuul API gateway

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
