### Bean's property filtering - static and dynamic
* In some cases you do not want to expose some attributes/properties of a bean as reponse to the RESTApi. In this situation you can apply filtering on that bean to ensure this feature. There are two ways to do this-
  * Static way of filtering - achieved by using **@JsonIgnore** annotation on property level
  * Dynamic way of filtering - achieved by using **@JsonFilter()** on Bean level + along with using SimpleBeanFilterProvider in the RestAPI call. 
  
* The static way does not give flexibilty on controlling different different set of properties to be exposed with different different RESTApi response. What I mean let;s say you have three properties in your bean (property1, property2 and property3) and you want to expose only - [property1, property2] in one of RestAPI response and [property2, property3] in another RestAPI response. So this kind of dynamic filtering is not possible with static way.

* In this demo we will look at both i.e. static way and dynamic way.
<hr/>

### 1. Static filtering using @JsonIgore
* The property which you do not want to expose in RESTApi response, use @JsonIgnore annotation on top of that.\
  [Person.java](https://github.com/thedevd/techBlog/blob/master/springboot/restful-web-services/06-filtering-beanproperty/src/main/java/com/thedevd/springboot/bean/Person.java)
  ```java
  import com.fasterxml.jackson.annotation.JsonIgnore;

  public class Person {

	    private String name;
	    private int age;
	    
	    @JsonIgnore // static filtering
	    private String ssn;
	    
	    public Person()
	    {
	    	super();
	    }
	    
	    public Person( String name, int age, String ssn )
	    {
	    	super();
	    	this.name = name;
	    	this.age = age;
	    	this.ssn = ssn;
	    }
	    
	    // getters and setters
  }
  ```

* [FilteringController.java](https://github.com/thedevd/techBlog/blob/master/springboot/restful-web-services/06-filtering-beanproperty/src/main/java/com/thedevd/springboot/controller/FilteringController.java)
  ```java
  @RestController
  public class FilteringController {

	    @GetMapping("/filtering/static")
	    public ResponseEntity<Person> staticFilteringDemo() {
	    	Person person = new Person("Ammy Jackson", 26, "123456");
	    	return ResponseEntity.status(HttpStatus.OK).body(person);
	    }
	}
  ```
* Now see the response after invoking this RestApi - http://localhost:8080/filtering/static
  ```
  {
    "name": "Ammy Jackson",
    "age": 26
  }
  ```
  So you can see ssn property is not included.
