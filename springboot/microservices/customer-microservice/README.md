### Rest Endpoints
* GET http://localhost:8062/api/customer
  ```
  [
    {
        "id": 10001,
        "name": "customer_10001",
        "emailId": "customer_10001@gmail.com",
        "phoneNo": "9876510001",
        "address": "Flat no 10001- Noida"
    },
    {
        "id": 10002,
        "name": "customer_10002",
        "emailId": "customer_10002@gmail.com",
        "phoneNo": "9876510002",
        "address": "Flat no 10002- Noida"
    },
    {
        "id": 10003,
        "name": "customer_10003",
        "emailId": "customer_10003@gmail.com",
        "phoneNo": "9876510003",
        "address": "Flat no 10003- Noida"
    }
  ]
  ```
* POST http://localhost:8062/api/customer \
  request payload - `{ "name": "customer_10004", "emailId": "customer_10004@gmail.com", "phoneNo": "9876510004", "address": "Flat no 10004- Noida" }`
  
  ```
  {
    "id": 1,
    "name": "customer_10004",
    "emailId": "customer_10004@gmail.com",
    "phoneNo": "9876510004",
    "address": "Flat no 10004- Noida"
  }
  ```
  
* GET http://localhost:8062/api/customer/id/1
  ```
  {
    "id": 1,
    "name": "customer_10004",
    "emailId": "customer_10004@gmail.com",
    "phoneNo": "9876510004",
    "address": "Flat no 10004- Noida"
  }
  ```
  
 * GET http://localhost:8062/api/customer/phone/9876510004
   ```
   {
    "id": 1,
    "name": "customer_10004",
    "emailId": "customer_10004@gmail.com",
    "phoneNo": "9876510004",
    "address": "Flat no 10004- Noida"
   }
   ```
