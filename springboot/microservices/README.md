## Spring Cloud Config Server (Configuration Manager)
One of the challange in microservice architecture is managing the configurations for microservices. In typical microservice architecture we usually have number of instances of services running with mutliple environments, what I mean - 
* lets say we have three microservices [microservice-1, microservice-2 and microserver-3]. Now suppose we have different different configurations specific to environment (dev/qa/prod) for each of the services.
* One way of managing these configurations is to keep all environment specific configuration withing microservice itself, but as soon as you scale your applications to run large no of services on multiple environments, it becomes difficult to manage those configurations which are stored in microservice level.
* Better way to manage them is to store all configurations in a central place which makes managing those external properties/configurations easy for services across all environments. Another advantage of keeping them in centralized repository is that as an application moves through the deployment pipeline from dev to test and then into production, we can easily manage the configuration between those environments and be assured that applications have everything they need to run when they are migrated. `Spring-Cloud-Config-Server is the module in the spring cloud which is used to manage the configurations stored in centralized repostitory`

<p align="center">
<img src="https://github.com/thedevd/imageurls/blob/master/sprintboot/spring-cloud-config-server.png">
</p>

* The default implementation of cloud-config-server uses `Git` as centralized configuration repository. 
* **Steps to create Spring-Cloud-Config-Server talking to a git repository** -
  * First step is to create a seperate Git repository where we will be storing configurations for microservices.\
    I have created [ecom-microservices-config-repo](https://github.com/thedevd/ecom-microservices-config-repo) for the same.
  * Create a spring boot project using [Spring Initializr](https://start.spring.io), and select the dependency for spring-cloud config server
    ```
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
    ```
    I have created [spring-cloud-config-server](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/spring-cloud-config-server) for the same.
  * Now we have to tell our spring-cloud-config-server project about how to talk to git repository which we have created in first step. To do this open [application.properties](https://github.com/thedevd/techBlog/blob/master/springboot/microservices/spring-cloud-config-server/src/main/resources/application.properties) of the project and add properties for `spring.cloud.config.server.git` -
    
    ```
    spring.application.name = spring-cloud-config-server
    server.port = 8888
    
    # Let the spring cloud config server know the git repo where configurations are stored
    spring.cloud.config.server.git.uri=file://C:/Users/Dell/mygithub/ecom-microservices-config-repo
    
    #spring.cloud.config.server.git.uri=https://github.com/thedevd/ecom-microservices-config-repo
    #spring.cloud.config.server.git.username=github_username
    #spring.cloud.config.server.git.password=github_password
    #spring.cloud.config.server.git.clone-on-start=true
    ```
    **Note- For simplicity I have used local filesystem URI to point to the centralized configuration storage. (For this first you need to clone the configuration git repository (created in first step) in your local filesystem)**
    
  * `This is very important and last step`. Enable the cloud config server on the startup, to do this use `@EnableConfigServer` annotation on the main class of the spring-cloud-config-server project -\
    [SpringCloudConfigServerApplication.java](https://github.com/thedevd/techBlog/blob/master/springboot/microservices/spring-cloud-config-server/src/main/java/com/thedevd/springboot/SpringCloudConfigServerApplication.java)
    ```java
    @SpringBootApplication
    @EnableConfigServer // this is required to enable spring-cloud-config-server
    public class SpringCloudConfigServerApplication {

	  public static void main(String[] args) {
		  SpringApplication.run(SpringCloudConfigServerApplication.class, args);
	  }
    ```
    
 ## Microservice to spring-cloud-config-server communication
 After we have spring-cloud-config-server created for configuration management, now we have to connect our microservice to cloud-config-server in order to fetch environment specific configuration. Once we establish the communication b/w microservice and cloud-config-server then we will see upon startup microservice will connect to config-server and ask for a configuration specific to an environment.
 * For demonstration, I have created a microservice called [inventory-microservice](https://github.com/thedevd/techBlog/tree/master/springboot/microservices/inventory-microservice). And We want to maintain seperate configurations for dev and qa envrionment.
 * Very first step is rename the application.properties file of microservice to [bootstap.properties](https://github.com/thedevd/techBlog/blob/master/springboot/microservices/inventory-microservice/src/main/resources/bootstrap.properties) and there mention the `uri` to spring-cloud-config server.
   ```
   spring.application.name=inventory-service
   
   # Let the application know from where to get the configuration of a specific environment.
   # (i.e. provide uri of config-server)
   spring.cloud.config.uri=http://localhost:8888
   ```
   **Note- Take a special care with application name defined by spring.application.name. This name is very important when creating the environment specific configuration in centralized Git configuration repository.** 
 * Next step is to create the envrionment specific configuration for our inventory-microservice in the centralized Git configuration repository and commit the files. `Take a special care while giving the name to the configuration file, the name should be the combination of application-name of the microservice and profile name.`\
   [inventory-service-dev.properties](https://github.com/thedevd/ecom-microservices-config-repo/blob/master/inventory-service-dev.properties)- this is for dev environment's configuration\
   [inventory-service-qa.properties](https://github.com/thedevd/ecom-microservices-config-repo/blob/master/inventory-service-qa.properties)- this is for qa environment's configuration\
   [inventory-service.properties](https://github.com/thedevd/ecom-microservices-config-repo/blob/master/inventory-service.properties) - this is default configuration.\
   **Note- Do not forget to commit the changes to git repository**
   
   
 
