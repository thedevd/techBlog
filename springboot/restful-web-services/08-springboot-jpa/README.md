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
