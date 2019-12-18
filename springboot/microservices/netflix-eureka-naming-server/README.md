## Netflix-Eureka - A naming server for Service registry and discovery
* In microservice architecture we break the application into number of small self-independent microservice. So it is not wrong to say that in microservice architecture an application may end up with running large no of microservices and also we may run multiple instances of one microservice to distribute the load using Load-Balancer.
* Having said an application running large no of microservice with multiple instances, if a microservice wants to communicate with other microservice, it should have information about list of instances of that other microservice. So if you are using Ribbon alone then you have to provide hardcoded urls of all the instances of that other microservice which is not recommended at all.
* So we need some kind of way which can allow a microservice to discover all other up and running microservices. The `Naming Server` provides this type of capability. The fundamental role of `Naming Server` is to keep the list of running services and easilty manage this list as whenever a service is removed or new service is added.
  <p align="center">
    <img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/architecture-using-namingserver.png"/>
  </p>
