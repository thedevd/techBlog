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
 
* JPA/Hibernate provides `CascadeType` enum which can allow us to cascade specific operation, means if we want to cascade only save operation but not remove operation. Then we need to clearly specify it in cascade configuration option of relationship annotation, see below where we are cascading save operation to book entities. 
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
  
* The cascade configuration option of relationship annotation also accepts an array of CascadeTypes, means if we want to cascade both save and remove operations in One-to-Many relationship, then we can use something like this -
  ```java
  @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
  private List<Book> book = new ArrayList<>();
  ```
  
### JPA CascadeType
The `javax.persistence.CascadeType` enum is used to represent JPA specific CascadeType. In this enum, 6 CascadeTypes are defined-
1. **ALL** - The value cascade=ALL is equivalent to cascade={PERSIST, MERGE, REMOVE, REFRESH, DETACH}.
2. **PERSIST**
3. **MERGE**
4. **REMOVE**
5. **REFRESH**
6. **DETACH**

### Hibernate CascadeType
Apart from above mentioned CascadeTypes, Hibernate provides 3 additional cascadeTypes. To use hibernate specific CascadeType, we need to use `org.hibernate.annotations.CascadeType` enum.
1. **REPLICATE** - when we replicate the main entity, then its associated entities also get replicated.
2. **SAVE_UPDATE** - This cascadeType is used to cascade hibernate specific save operations such as - save(), update() and saveOrUpdate().
3. **LOCK** - CascadeType.LOCK re-attaches the entity and its associated child entity with the persistent context again.

`Hibernate has an deprecated cascade type - DELETE_ORPHAN. In place of this use @OneToOne(orphanRemoval=true) or @OneToMany(orphanRemoval=true)`

