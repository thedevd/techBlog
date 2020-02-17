## JPA/Hibernate CascadeType

* In JPA/Hibernate, most of the time we have to work with entity relationships where existence of an entity dependend on another entity,

  **for example**- 
  * `Person-Address relationship.` \
     Here we can say Address entity is dependent on Person
  * `Author-Book relationship.` \
     Here we can say Book entity is dependent on Author

* Ideally what we want, we want whatever operation we do on main/target entity (Person or Author), the same operation should be applied automatically to associated entity(Address or Book), i.e. when we delete Author entity from db, then all the associated book entities should automatically be removed from db too. `This general action of passing on something to succession of others is called Cascading.` 

  `CascadeType` is the way of achieving the same in JPA/Hibernate.
  ```java
  @OneToMany(cascade=CascadeType.REMOVE, fetch = FetchType.LAZY)
  ```
 
* JPA/Hibernate provides `CascadeType` enum which can allow us to cascade specific operation, means if we want to cascade only save operation but not remove operation. Then we need to clearly specify it using below code. 
  ```java
  @Entity
  class Author {

	  @Id
	  private Integer id;

	  private String name;

	  @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	  private List<Book> book = new ArrayList<>();
 
   // getters and setters..
  }
  ```
