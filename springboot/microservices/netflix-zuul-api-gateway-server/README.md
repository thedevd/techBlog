## Netflix Zuul - An API Gateway
* In a typical microservice architecture we have many small microservices with multiple instances running on different host and port. 
* So in this situation to access a particular instance of the microservice , clients (browsers/mobile) need to know the host and port on which that particular microservice is running, and this is very problematic from client perspective as they can not access the end microservices without knowing their port and host. 
* What we need here is a common entry point to our all microservices that should be able to decide where to route the request. `By using a common entry point we will not only free our clients from knowing the deployment details of all the end microservices but also we can do lot of things like authentication/authorization/logging/tracing at this level which will reduce significant development effort on the end microservices side.`
* This common entry point is termed as `API Gateway`. Netflix has created `Zuul server` for the same purpose and has open-sourced it and spring cloud community has provided a nice wrapper around it for easy integration with spring boot based microservice styled application. 

