## Richardson Maturity Model for RESTful APIs
* Richardson maturity model is used to decide how mature is your Rest APIs, mature means how much your REST API is RESTful compliant.
* This model was introduced by Leonard Richardson. What he did, he analyzed no of web services design and then came up with total 4 categories to identify their maturity level as per RESTful desing principles. The better your API fulfils these priciples, the more mature it is considered.
* Leonard Richardson has used these 4 levels to decide maturity of a web service -
  * Level 0 - **Single URI and single HTTP Method**
  * Level 1 - **Resources with unique URI** but single HTTP method
  * Level 2 - **HTTP methods/verbs**
  * Level 3 - **Hypermedia controls (HATEOAS)**
  
    ![Richardson Maturity Model](https://restfulapi.net/wp-content/uploads/Richardson-Maturity-Model-300x249.jpg)
 <hr/>
 
1. Level-0 (**Single URI and single HTTP Method**)\
   * RestAPIs at this level do not use multiple URIs, multiple HTTP methods and HATEOAS capabilities, rather it, the services has single entry point (URI) and use single HTTP method (typically POST).
   * Generally SOAP and XML-RPC based services follow this level where services use a single URI to identify an endpoint, and HTTP POST to transfer payloads.
 
2. Level-1 (**Resources with unique URI**)\
   * At this level, services use mutiple URIs for resoureces, where each resource is separately identified by a unique URI, and this makes it more better than level 0. **But this level still uses single HTTP method (typically POST)**.
   * Example- Instead of calling POST http://localhost:8080/users all time with correct payload, now you can distinguish users using http://localhost:8080/users/1 or http://localhost:8080/users/2 and so on.

3. Level-2 (**HTTP verbs**)\
Most of the time we have seen that developers do not use proper HTTP methods and HTTP response code in REST services, i.e. 
   * they mix GET and POST, i.e. they also use HTTP POST for retrieve operation.
   * they use same http status code i.e. 500-INTERNAL_SERVER_ERROR when something went wrong for instance JSON is MALFORMED, or aruguments are invalid. And 200-OK when new resource is created.\
   
   So this level suggests all the services to use proper HTTP methods and response code according to thier action. Means do not use single POST method for all actions, instead 
   * use GET when requesting resource with proper HTTP status code (200-OK, 404-NOT_FOUND)
   * use DELETE when deleting resource with proper HTTP status code (200-OK, 404-NOT_FOUND)
   * use PUT when updating an existing resource with proper HTTP status code (200-OK, 400-BAD_REQUEST)
   * use POST when creating a new resource with proper HTTP status code (**201-CREATED**, 400-BAD_REQUEST)
   * use TRACE when want test what server receives. (It simply returns what was sent).
Example-
     * GET http://localhost:8080/users - retrieves all users
     * POST http://localhost:8080/users - creates a new user and returns 201 on success
     * PUT http://localhost:8080/users - updates an existing user and returns 200 on success
     * GET http://localhost:8080/users/1 - get specific user with id 1 and returns 404 on failure.
     * DELETE http://localhost:8080/users/1 - delete specific user with id 1 and returns 404 on failure.
  
   Services falling at this level can be treated close to truly RESTful complaint.
   
4. Level-3 (**Hypermedia controls - HATEOAS**)\
* This level means high maturity level and services at this level can be called truly RESTful complaint. If webservices follows Level-2 plus HATEOAS capabilities, then these services are at LEVEL-3 of Richardson maturity model. 
* Level-3 encourages to use HATEOAS in the response, doing this will help your clients to know about more information about your API and the further possibilities or action they might be interested.
* Example- When creating a new resource, it would be very useful to provide a link/URI of getting all-users in order to let them know how to get details of all users. This way we would make our API response more informative and self-explanatory.\

```In this demo, I have tried to bring all my APIs to LEVEL-3.```

