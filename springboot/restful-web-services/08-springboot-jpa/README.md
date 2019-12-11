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

`Just to give you a fair idea of what we are going to implement here using JPA,`\
`We will build an application to keep track of User's details along with the posts they have posted in social type of platform.`

### 2. Configuring Tables and thier relationships using JPA annotations
* Considering the use case of User having more than one post in the site, lets create two pojos User.java, Post.java and map them to database tables using JPA annotations -
  * @Entity - informs the JPA that this bean is JPA managed entity, so create schema and manage data automatically.
  * @ID, @GeneratedValue - These are for attributes of bean class. @ID inform the JPA that this property has to be treated as primary key, and @GeneratedValue informs the JPA that generated the values of primary key attributes automatically according to the generation strategy (AUTO, SEQUENCE, IDENTITY, TABLE).
  * @ManyToOne, @OneToMany - These are used to establish relationship between the tables.\
    [User.java](https://github.com/thedevd/techBlog/blob/master/springboot/restful-web-services/08-springboot-jpa/src/main/java/com/thedevd/springboot/bean/User.java)
    ```java
    @Entity
    public class User {
  
  	  @Id
  	  //@GeneratedValue(strategy = GenerationType.AUTO) --> AUTO is the default strategy if strategy is not given
  	  @GeneratedValue
  	  private Integer id;
  	  
  	  @Size( min = 3, max = 20, message = "name should be 3 to 20 characters long" )
  	  private String name;
  	  
  	  @Past( message = "dob should be past date" )
  	  private LocalDate dob;
  	  
  	  @OneToMany( mappedBy = "user" ) // relate the Post to user by user property in Post.java.
  	  private List<Post> posts;
  	  
  	  public User()
  	  {
  	  	// Default constructor is required by hibernate JPA
  	  	super();
  	  }
  	  
  	  public User( String name, LocalDate dob )
  	  {
  	  	super();
  	  	this.name = name;
  	  	this.dob = dob;
  	  }
  	  
  	  // getters and setters ...
    }
    ```
    [Post.java](https://github.com/thedevd/techBlog/blob/master/springboot/restful-web-services/08-springboot-jpa/src/main/java/com/thedevd/springboot/bean/Post.java)
    ```java
    @Entity
    public class Post {
    
      @Id
      @GeneratedValue
      private Integer postId;
      
      private String description;
      private LocalDateTime postedAt;
      
      @ManyToOne(fetch = FetchType.LAZY)
      @JsonIgnore // do not want to include user details, otherwise there will be recursive retrieval of User and post within user.
      private User user;
      
      public Post()
      {
      	super();
      	this.postedAt = LocalDateTime.now();
      }
      
      public Post( String description )
      {
      	this();
      	this.description = description;
      }
      
      // getters and setters ...
    }
    ```
    When you start the application, the Hibernate JPA will look for all the classes annotated with @Entity and then create table structure according to attribute level annotations.\
    To see those queries which will be run by Hibernate internally, you need to enable sping.jpa.show-sql property in application.properties file. I have enabled this show-sql property, so this thing we will see in the console -
    ```
    Hibernate: drop table post if exists
    Hibernate: drop table user if exists
    Hibernate: drop sequence if exists hibernate_sequence
    Hibernate: create sequence hibernate_sequence start with 1 increment by 1
    Hibernate: create table post (post_id integer not null, description varchar(255), posted_at timestamp, user_id integer, primary key (post_id))
    Hibernate: create table user (id integer not null, dob date, name varchar(20), primary key (id))
    Hibernate: alter table post add constraint FK72mt33dhhs48hf9gcqrq4fxte foreign key (user_id) references user
    ```
    
* In order to interact with tables in persistent storage layer, you need to create corresponsing JpaRepository. As in this demo, we have two tables - User and Post, so we will create JpaRepository for each table.
  * [UserRepository.java](https://github.com/thedevd/techBlog/blob/master/springboot/restful-web-services/08-springboot-jpa/src/main/java/com/thedevd/springboot/repository/UserRepository.java)
  ```java
  @Repository
  public interface UserRepository extends JpaRepository<User, Integer> {
  
  }
  ```
  * [PostRepository.java](https://github.com/thedevd/techBlog/blob/master/springboot/restful-web-services/08-springboot-jpa/src/main/java/com/thedevd/springboot/repository/PostRepository.java)
  ```java
  @Repository
  public interface PostRepository extends JpaRepository<Post, Integer> {
  
  }
  ```
  * At runtime, hibernate is going to provide an implementation of these repository and then you can use these repositories to achieve these some default tasks using following methods -
    1. findAll()
    2. findById()
    3. deleteById()
    4. save()
  
  * Apart from these default JPA APIs provided by JpaRepository, we can also create cutomized Query using JPA criteria API. JPA criteria API provides lot of keywords which you can use to create Queuries. For example-
    ```java
    @Repository
    public interface UserRepository extends JpaRepository<User, Integer> {
      List<User> findByNameLike(String name);
    }
    ```
    Essentially, this translates into the following query automatically by hibernate\
    ` select u from User u where u.name like ?1 `
    
  * This table describes the supported keywords which you can use inside method names to create custom Queuries.
  ![JPA Criteria API keywords](https://github.com/thedevd/imageurls/blob/master/sprintboot/jpa_criteria_api_keywords.png)

