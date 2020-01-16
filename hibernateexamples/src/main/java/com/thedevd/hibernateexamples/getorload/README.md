## Difference b/w get() and load() method of hibernate's Session
This is frequently asked interview question of hibernate.
* Both are related to hibernate's Session interface.
* Both are used to fetch record (a row) from database. 
* Both accepts two parameters `<class>, id` the first parameter tell which table record it will fetch and second is the primary identifier value which is used to uniquely identify a row in table. So you can call them using `session.get(<class>, id)` or `session.load(<class>,id)`

Lets have a look at both of them separately, then we will conclude the major difference b/w them.

### Session.load()
* When you call session.load(), this always returns a proxy object. Proxy object means, hibernate will create a fake object and will not hit the database. This fake object will be having only the given primary identifier value. For example - calling `session.load(Author.class, 102)` will create a fake object in memory with id 102 and other properties of this Author object will not be initialized at this point.
  ```java
  Author authorFetchedUsingLoad = session.load(Author.class, 102);
  System.out.println(authorFetchedFromLoad.getId());
  ```
* Now the question is does load() ever hit the database, the answer is yes. It will only hit the database when we try to retrieve remaining properties of the Author object (name) other than primary key identifier(id).
  ```java
  Author authorFetchedUsingLoad = session.load(Author.class, 102);
  System.out.println(authorFetchedFromLoad.getId());
  System.out.println(authorFetchedFromLoad.getName());
  ```
  In this case calling `authorFetchedFromLoad.getName()` will force hibernate to hit the database and get the name property from the Author where id is 102. And by chance if object does not exists in database then load() will throw `ObjectNotFoundException`
  
### Session.get()
* When you call session.get(), it will immediately hit the database and return the original object. And by chance if object does not exist in database it will simply returns `null`. For example - calling `session.get(Author.class, 101)` will force hibernate to hit the database and fetch the row having the primary identifier value as 101. 
  ```java
  Author authorFetchedUsingGet = session.get(Author.class, 101);
  ```

So from the above discussion we can conclude and say that -
* get() always hit the database where as load() only hit the database when try to get non-primary identifiers of the record.
* If object exists get() returns the original object by hitting the database where as even if object exists or not load() always return the proxy object having only the value of primary identifier.
* If object does not exist, get() will return null, where as load() always returns proxy object.
* When using get(), accessing all the fields on the non-existent object will throw NullPointerException where as accessing only non-primary field on the non-existent object will throw `ObjectNotFoundException`. So when you are not sure about existence of object, use get() because in this case you have abililty to check immediately for null object before doing further operation but it is not possible using load() as load() always return proxyObject and you come to know about its non-existence when try to fetch other non-primary fields. (so if you want to stick only with load() make sure you always handle the ObjectNotFoundException in the code).
