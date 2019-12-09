### Bean's property filtering - static and dynamic
* In some cases you do not want to expose some attributes/properties of a bean as reponse to the RESTApi. In this situation you can apply filtering on that bean to ensure this feature. There are two ways to do this-
  * Static way of filtering - achieved by using **@JsonIgnore** annotation on property level
  * Dynamic way of filtering - achieved by using **@JsonFilter()** on Bean level + along with using SimpleBeanFilterProvider in the RestAPI call. 
  
* The static way does not give flexibilty on controlling different different set of properties to be exposed with different different RESTApi response. What I mean let;s say you have three properties in your bean (property1, property2 and property3) and you want to expose only - [property1, property2] in one of RestAPI response and [property2, property3] in another RestAPI response. So this kind of dynamic filtering is not possible with static way.

* In this demo we will look at both i.e. static way and dynamic way.
* Rest endpoits created in this demo- 
  * http://localhost:8080/filtering/static
  * http://localhost:8080/filtering/dynamic/property1and2
  * http://localhost:8080/filtering/dynamic/property2and3
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

### 2. Dynamic filtering using FilterProviders and @JsonFilter annotation
* This way gives flexibility to apply different-different filtering with different-different RestAPI calls. Let's see how to do this.
* Apply @JsonFilter annotation on Bean level. Noted that this annotation takes filterId as parameter which is very important because this filterId will be used further in RestAPI call to let the MappingJacksonValue know that which FilterId bean class is using.\
  [SomeBean.java](https://github.com/thedevd/techBlog/blob/master/springboot/restful-web-services/06-filtering-beanproperty/src/main/java/com/thedevd/springboot/bean/SomeBean.java)
  ```java
  import com.fasterxml.jackson.annotation.JsonFilter;

  @JsonFilter("SomeBeanFilter") // dynamic Filtering. Filter id here is very IMPORTANT, as this is used by FilterProvider
  public class SomeBean {

	private String property1;
	private String property2;
	private String property3;

	public SomeBean()
	{
		super();
	}

	public SomeBean( String property1, String property2, String property3 )
	{
		super();
		this.property1 = property1;
		this.property2 = property2;
		this.property3 = property3;
	}
	
	// getters and setters

  }
  ```

* Now use the same filter Id when creating the FilterProvider in RestAPI call. Steps are -
  * Create SimpleBeanPropertyFilter by specifying the properties you want and all others will be filtered out.
  ```java
  // SimpleBeanPropertyFilter.filterOutAllExcept() static method to construct filter that filters out all properties 
  // except ones specified in filterOutAllExcept()
  SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("property1","property2");
  ```
  * Create FilterProvider and add the above created property filter with correct FilterId.
  ```java
  // note- filter id is very IMPORTANT
  SimpleFilterProvider filterProvider = new SimpleFilterProvider().addFilter("SomeBeanFilter", filter); 
  ```
  * Inform the MappingJacksonValue to use above created FilterProvider by using setFilters() method. So when returning response mappingJacksonValue will know which FiterProvider to apply on the bean and what properties to filter out.
  ```java
  MappingJacksonValue mapping = new MappingJacksonValue(sbean);
  mapping.setFilters(filterProvider); // telling the mappingJackson to use this filterProvider
  ```
  [FilteringController.java](https://github.com/thedevd/techBlog/blob/master/springboot/restful-web-services/06-filtering-beanproperty/src/main/java/com/thedevd/springboot/controller/FilteringController.java)
  ```java
  @RestController
  public class FilteringController {

	// include only property1 and property2 of SomeBean
	@GetMapping("/filtering/dynamic/property1and2")
	public ResponseEntity<MappingJacksonValue> dynamicFilteringDemo1(){
		SomeBean sbean = new SomeBean("value1", "value2", "value3");
		
		// SimpleBeanPropertyFilter.filterOutAllExcept() static method to construct filter that filters out all properties except ones specified in filterOutAllExcept()
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("property1","property2");
		
		SimpleFilterProvider filterProvider = new SimpleFilterProvider().addFilter("SomeBeanFilter", filter); // note- filter id is very IMPORTANT
		
		MappingJacksonValue mapping = new MappingJacksonValue(sbean);
		mapping.setFilters(filterProvider); // telling the mappingJackson to use this filterProvider
		
		return ResponseEntity.status(HttpStatus.OK).body(mapping);
	}
	
	// include only property2 and property3 of SomeBean
	@GetMapping("/filtering/dynamic/property2and3")
	public ResponseEntity<MappingJacksonValue> dynamicFilteringDemo2(){
		List<SomeBean> sbeanList = Arrays.asList(new SomeBean("value1", "value2", "value3"),
				new SomeBean("value11", "value22", "value33"));
		
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("property2", "property3");
		
		SimpleFilterProvider filterProvider = new SimpleFilterProvider().addFilter("SomeBeanFilter", filter); // note- filter id is very IMPORTANT
		
		MappingJacksonValue mapping = new MappingJacksonValue(sbeanList);
		mapping.setFilters(filterProvider);// telling the mappingJackson to use this filterProvider
		
		return ResponseEntity.status(HttpStatus.OK).body(mapping);
	}
  }
  ```
  So you can see we have two restAPI calls here which are exposing only the specified properties means calling these API will produced different-different response based on how the filtering is defined by **SimpleBeanPropertyFilter.filterOutAllExcept() methods**. (**Note that these APIs have return type as MappingJacksonValue**)

    * http://localhost:8080/filtering/dynamic/property1and2
    ```
    {
    "property1": "value1",
    "property2": "value2"
    }
    ```
    * http://localhost:8080/filtering/dynamic/property2and3
    ```
    [
    {
        "property2": "value2",
        "property3": "value3"
    },
    {
        "property2": "value22",
        "property3": "value33"
    }
    ]
    ```
