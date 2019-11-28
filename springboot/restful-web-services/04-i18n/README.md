To implement i18n in spring boot, these main classes are used -
1. org.springframework.context.support.**ResourceBundleMessageSource** - \
It is used to access resource bundles using specified basenames.
2. org.springframework.web.servlet.**LocaleResolver** - \
It is used for locale resolution strategies that allows to decide which locale to use either the locale based on Accept-Language param in request header or default locale if failed to resolve locale from request header.
3. org.springframework.context.i18n.**LocaleContextHolder** - \
Used as a central holder to retrieve the current Locale configured in Spring.

Steps to implement i18N -
1. Create resource bundles files for each of the locale in src/main/resource folder. (take a special care here with basename and the locale. Locale is appended with underscore for ex- messages_in (IN for India Locale), messages_fr (FR for france).\
   Add key-value for the messages in the specific language.\
   ```message.good.morning=Namaste```
   [See this](https://github.com/thedevd/techBlog/tree/master/springboot/restful-web-services/04-i18n/src/main/resources)
   
2. Once the resource bundle files are created, then we should somehow tell the Spring about the basename for the resource bundles files.
This can be done by initializing @Bean of ResourceBundleMessageSource -
   ```java
   @Bean
   public ResourceBundleMessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("messages"); // messages is the basename of resource bundles
    return messageSource;
   }
   ```
   [See this](https://github.com/thedevd/techBlog/blob/master/springboot/restful-web-services/04-i18n/src/main/java/com/thedevd/springboot/Application.java)
   
 3. Next step is we need to tell the spring to use a requested Locale coming in request header or if not available then use a particular locate as default locale in the application. This can be done by initilializing @Bean of LocaleResolver - 
    ```java
    @Bean
    public LocaleResolver localeResolver() {
     AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
     localeResolver.setDefaultLocale(Locale.US);
     return localeResolver;
    }
    ```
    [See this](https://github.com/thedevd/techBlog/blob/master/springboot/restful-web-services/04-i18n/src/main/java/com/thedevd/springboot/Application.java)
    
Now we all set to go. In order to use any message from the configured locale, we need to use **LocaleContextHolder.getLocale()** - this will retreive the message from a correct resource bundle of configured locale.

```java
@RestController
public class I18NGreetingController {

	@Autowired
	private ResourceBundleMessageSource messageSource;
	
	@GetMapping("/greetme")
	public String sayGoodMorning() {
		return messageSource.getMessage("message.good.morning", null, LocaleContextHolder.getLocale());
	}
}
```
[See this](https://github.com/thedevd/techBlog/blob/master/springboot/restful-web-services/04-i18n/src/main/java/com/thedevd/springboot/controller/I18NGreetingController.java)
