## Spring boot- JPA configuration
### 1. **Setup**
* To enable JPA support in SprintBoot, add this starter depedency of data-jpa
  ```
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>
  ```
* Add the driver dependency for the database which is going to be persistent storage layer for the application. In this demo, we will be working with in-memory h2 database, so adding the driver dependency for this -
  ```
  <dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
  </dependency>
  ```
  See the [pom.xml](https://github.com/thedevd/techBlog/blob/master/springboot/restful-web-services/08-springboot-jpa/pom.xml) of this demo application.

  `Just to give you an idea of what we are going to implement here using JPA.`\
  `We are building an application to keep track of User's details along with the posts they have posted in social type of platform.`
