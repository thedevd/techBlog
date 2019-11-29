## SpringBoot Java-Bean Validations using javax.validations API
In this demo, we are going to look at basic of java bean validation in Spring boot using Java-Validation-Api (javax.validations package). This API provides lot of validation annotations that can be used to validate a Java Bean in a RestCall.

In this demo, we have a User bean, and we have added simple validation on name and dob - [See full code here](https://github.com/thedevd/techBlog/blob/master/springboot/restful-web-services/05-bean-validation/src/main/java/com/thedevd/springboot/bean/User.java)
```java
import java.time.LocalDate;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

public class User {

	private Integer id;
	
	@Size(min=3, max=20, message="name should be 3 to 20 characters long") // bean validation
	private String name;
	
	@Past(message = "dob should be past date") // bean validation
	private LocalDate dob;
	
	/*
	 * @Min(value = 18, message = "Age should not be less than 18")
	 * @Max(value = 100, message = "Age should not be greater than 100")
	 * private int age;
    */

	public User( Integer id, String name, LocalDate dob )
	{
		super();
		this.id = id;
		this.name = name;
		this.dob = dob;
	}

	// getters and setters ...
}
```

All of the annotations used above are standard JSR annotations. (JSR 380 is a specification of the Java API for bean validation). Some commonly used annotations are -
* **@NotNull** – validates that the annotated property value is not null.
* **@NotEmpty** – validates that the property is not null or empty; can be applied to String, Collection, Map or Array values.
* **@NotBlank** – can be applied only to text values and validated that the property is not null or whitespace.

* **@AssertTrue** – validates that the annotated property value is true
* **@Positive and @PositiveOrZero** – apply to numeric values and validate that they are strictly positive, or positive including 0.
* **@Negative and @NegativeOrZero** – apply to numeric values and validate that they are strictly negative, or negative including 0.

* **@Size** – validates that the annotated property value has a size between the attributes min and max; can be applied to String, Collection, Map, and array properties
* **@Min** – vValidates that the annotated property has a value no smaller than the value attribute
* **@Max** – validates that the annotated property has a value no larger than the value attribute
* **@Email** – validates that the annotated property is a valid email address

* **@Past and @PastOrPresent** – validate that a date value is in the past or the past including the present; can be applied to date types including those added in Java 8.
* **@Future and @FutureOrPresent** – validates that a date value is in the future, or in the future including the present.

Some annotations accept additional attributes, but the message attribute is common to all of them. This is the message that will usually be rendered when the value of the respective property fails.
<hr/>

To use the above defined User Java-Bean validations in a restCall defined in Controller, you just have to add @Valid annotation.[See full code here](https://github.com/thedevd/techBlog/blob/master/springboot/restful-web-services/05-bean-validation/src/main/java/com/thedevd/springboot/controller/UserController.java)
```java
	@PostMapping("/users/save")
	public ResponseEntity<Object> saveUser(@Valid @RequestBody User user) { // @Valid enables the bean validation
		User savedUser = userService.save(user);
		return ResponseEntity.status(HttpStatus.OK).body(savedUser);
	}
```
When the bean validation fails, spring will throw MethodArgumentNotValidException. And we have defined our own handler to takle that exception and send proper well organized response that can be undestood by API client. [See full code here](https://github.com/thedevd/techBlog/blob/master/springboot/restful-web-services/05-bean-validation/src/main/java/com/thedevd/springboot/exceptionhandler/CustomGlobalExceptionHandler.java)
```java
  /*
	 * Handle MethodArgumentNotValidException, thrown when a bean fails @Valid validations
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid( MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request )
	{
		RestApiException apiError = new RestApiException(status, "Arguments validation failed", ex);
		return buildRestApiExceptionResponse(apiError);
	}
```




