## Say 'NO' to Spring boot Default Exception handlers
* If you are relying on the Default Exception Handlers of SpringBoot, then you see they provide more of error stack traces which are hard to understand and thus become useless for the API's client or users. 
* So you always want to handle those API errors correctly and want to provide some meaningful user's friendly error messages which can help the API's client to easilty understand and handle them properly. 
* And to achieve this you basically put a well defined structure to the error information so that client can easily parse them according to thier usage.

A sample of error response by Default exception handler in spring boot -\
```java
{
    "timestamp": "2019-11-28T13:50:05.667+0000",
    "status": 400,
    "error": "Bad Request",
    "message": "JSON parse error: Cannot deserialize value of type `java.time.LocalDate` from String \"1989-12-23aa\": Failed to deserialize java.time.LocalDate: (java.time.format.DateTimeParseException) Text '1989-12-23aa' could not be parsed, unparsed text found at index 10; nested exception is com.fasterxml.jackson.databind.exc.InvalidFormatException: Cannot deserialize value of type `java.time.LocalDate` from String \"1989-12-23aa\": Failed to deserialize java.time.LocalDate: (java.time.format.DateTimeParseException) Text '1989-12-23aa' could not be parsed, unparsed text found at index 10\n at [Source: (PushbackInputStream); line: 4, column: 12] (through reference chain: com.thedevd.springboot.bean.User[\"dob\"])",
    "trace": "org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error: Cannot deserialize value of type `java.time.LocalDate` from String \"1989-12-23aa\": Failed to deserialize java.time.LocalDate: (java.time.format.DateTimeParseException) Text '1989-12-23aa' could not be parsed, unparsed text found at index 10; nested exception is com.fasterxml.jackson.databind.exc.InvalidFormatException: Cannot deserialize value of type `java.time.LocalDate` from String \"1989-12-23aa\": Failed to deserialize java.time.LocalDate: (java.time.format.DateTimeParseException) Text '1989-12-23aa' could not be parsed, unparsed text found at index 10\n at [Source: (PushbackInputStream); line: 4, column: 12] (through reference chain: com.thedevd.springboot.bean.User[\"dob\"])\r\n\tat org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter.readJavaType(AbstractJackson2HttpMessageConverter.java:245)\r\n\tat 
    org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter.read(AbstractJackson2HttpMessageConverter.java:227)\r\n\tat org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver.readWithMessageConverters(AbstractMessageConverterMethodArgumentResolver.java:205)\r\n\tat org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor.readWithMessageConverters(RequestResponseBodyMethodProcessor.java:158)\r\n\tat org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor.resolveArgument(RequestResponseBodyMethodProcessor.java:131)\r\n\tat org.springframework.web.method.support.HandlerMethodArgumentResolverComposite.resolveArgument(HandlerMethodArgumentResolverComposite.java:121)\r\n\tat org.springframework.web.method.support.InvocableHandlerMethod.getMethodArgumentValues(InvocableHandlerMethod.java:167)\r\n\tat org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:134)\r\n\tat org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:106)\r\n\tat org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:888)\r\n\tat org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:793)\r\n\tat org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)\r\n\tat org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1040)\r\n\tat org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:943)\r\n\tat org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006)\r\n\tat org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:909)\r\n\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:660)\r\n\tat org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883)\r\n\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:741)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\r\n\tat org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\r\n\tat org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)\r\n\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\r\n\tat org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)\r\n\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\r\n\tat org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)\r\n\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\r\n\tat org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:202)\r\n\tat org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96)\r\n\tat org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:526)\r\n\tat org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:139)\r\n\tat org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)\r\n\tat org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)\r\n\tat org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343)\r\n\tat org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:408)\r\n\tat org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:66)\r\n\tat org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:861)\r\n\tat org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1579)\r\n\tat org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)\r\n\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)\r\n\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)\r\n\tat org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)\r\n\tat java.lang.Thread.run(Thread.java:745)\r\nCaused by: com.fasterxml.jackson.databind.exc.InvalidFormatException: Cannot deserialize value of type `java.time.LocalDate` from String \"1989-12-23aa\": Failed to deserialize java.time.LocalDate: (java.time.format.DateTimeParseException) Text '1989-12-23aa' could not be parsed, unparsed text found at index 10\n at [Source: (PushbackInputStream); line: 4, column: 12] (through reference chain: com.thedevd.springboot.bean.User[\"dob\"])\r\n\tat com.fasterxml.jackson.databind.exc.InvalidFormatException.from(InvalidFormatException.java:67)\r\n\tat com.fasterxml.jackson.databind.DeserializationContext.weirdStringException(DeserializationContext.java:1676)\r\n\tat com.fasterxml.jackson.databind.DeserializationContext.handleWeirdStringValue(DeserializationContext.java:932)\r\n\tat com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase._handleDateTimeException(JSR310DeserializerBase.java:86)\r\n\tat com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer.deserialize(LocalDateDeserializer.java:103)\r\n\tat com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer.deserialize(LocalDateDeserializer.java:36)\r\n\tat com.fasterxml.jackson.databind.deser.SettableBeanProperty.deserialize(SettableBeanProperty.java:530)\r\n\tat com.fasterxml.jackson.databind.deser.BeanDeserializer._deserializeWithErrorWrapping(BeanDeserializer.java:528)\r\n\tat com.fasterxml.jackson.databind.deser.BeanDeserializer._deserializeUsingPropertyBased(BeanDeserializer.java:417)\r\n\tat com.fasterxml.jackson.databind.deser.BeanDeserializerBase.deserializeFromObjectUsingNonDefault(BeanDeserializerBase.java:1287)\r\n\tat com.fasterxml.jackson.databind.deser.BeanDeserializer.deserializeFromObject(BeanDeserializer.java:326)\r\n\tat com.fasterxml.jackson.databind.deser.BeanDeserializer.deserialize(BeanDeserializer.java:159)\r\n\tat com.fasterxml.jackson.databind.ObjectMapper._readMapAndClose(ObjectMapper.java:4202)\r\n\tat com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3258)\r\n\tat org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter.readJavaType(AbstractJackson2HttpMessageConverter.java:239)\r\n\t... 51 more\r\nCaused by: java.time.format.DateTimeParseException: Text '1989-12-23aa' could not be parsed, unparsed text found at index 10\r\n\tat java.time.format.DateTimeFormatter.parseResolved0(DateTimeFormatter.java:1952)\r\n\tat java.time.format.DateTimeFormatter.parse(DateTimeFormatter.java:1851)\r\n\tat java.time.LocalDate.parse(LocalDate.java:400)\r\n\tat com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer.deserialize(LocalDateDeserializer.java:101)\r\n\t... 61 more\r\n",
    "path": "/users/save"
}
```
Above given large response is from restCall - http://localhost:8080/users/save of [02-userservice-crud](https://github.com/thedevd/techBlog/tree/master/springboot/restful-web-services/02-userservice-crud) application, when we try to save a user with this malformed JSON-
```json
{
    "id": null,
    "name": "dev",
    "dob": "1989-12-23aa"
}
```

You can see, "status" and "message" fields are only useful for client, but even though "message" filed has too much information which does not seem to be client friendly (But can be useful for only developers). So let's see how we can handle these errors properly by wrapping them in nice and clean JSON representation to make client's life happier.

## Spring Boot custom Exception Handling and handlers
In spring boot, we have two main annotations that are used to provide custom exception handler in the application, they are-
1. @ControllerAdvice - used with Class
2. @ExceptionHandler - used with method

* @ControllerAdvice annoted class provides a central point to define how to handle particular exception when they are thrown from any Controller classes. As the name suggest, it is "Advice" for multiple controllers.
* @ExceptionHandler annoted method actually defined how to handle the exception when it is thrown from any Controller classes. Handle the exception means wrap the error information in organized way and return the response.

Altogether, we use @ExceptionHandler on methods of @ControllerAdvice classes so that the exception handling will be applied globally or to a subset of controllers.
<hr/>

So what we have done in this demo, we have created a well defined strucutred for a custom business domain related exception such as - **UserNotFoundException.java** and for other generic exception - **RestApiException.java**
```java
@SuppressWarnings( "serial" )
public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException( String message )
	{
		super(message);
		// TODO Auto-generated constructor stub
	}

}
```
```java
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public class RestApiException {

	private HttpStatus status; // holds the HTTP call response status
	private LocalDateTime timestamp; // holds the date-time instance of when the error happened.
	private String message; // holds a user-friendly message about the error.
	private String debugMessage; // holds message describing the error in more detail for debug.

	public RestApiException()
	{
		this.timestamp = LocalDateTime.now();
	}

	public RestApiException( HttpStatus status, String message )
	{
		this(); // to set timestamp
		this.status = status;
		this.message = message;
	}

	public RestApiException( HttpStatus status, String message, Throwable ex )
	{
		this(); // to set timestamp
		this.status = status;
		this.message = message;
		this.debugMessage = ex.getLocalizedMessage();
	}

	public HttpStatus getStatus()
	{
		return status;
	}

	public void setStatus( HttpStatus status )
	{
		this.status = status;
	}

	public LocalDateTime getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp( LocalDateTime timestamp )
	{
		this.timestamp = timestamp;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage( String message )
	{
		this.message = message;
	}

	public String getDebugMessage()
	{
		return debugMessage;
	}

	public void setDebugMessage( String debugMessage )
	{
		this.debugMessage = debugMessage;
	}
}
```
