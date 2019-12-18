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

## [Spring-Cloud-Config-Server (Configuration management)](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/spring-cloud-config-server)
   
 
