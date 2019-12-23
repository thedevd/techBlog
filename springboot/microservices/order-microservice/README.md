### Rest endpoints
* GET order details by id - http://localhost:8072/api/order/id/10001
  ```
  {
    "id": 10001,
    "customerPhoneNo": "9876510001",
    "orderItems": [
        {
            "id": 50000,
            "productCode": "p10001",
            "quantity": 1,
            "productPrice": 100,
            "totalPrice": 100
        },
        {
            "id": 50001,
            "productCode": "p10002",
            "quantity": 2,
            "productPrice": 50,
            "totalPrice": 100
        }
    ],
    "orderAmount": 200,
    "orderDate": "2019-12-23T14:50:58.223",
    "customerName": "customer_10001",
    "customerEmailId": "customer_10001@gmail.com",
    "customerAddress": "Flat no 10001- Noida"
  }
  ```
  
* GET order details of customer using phoneNo - http://localhost:8072/api/order/phone/9876510001
  ```
  {
    "id": 10001,
    "customerPhoneNo": "9876510001",
    "orderItems": [
        {
            "id": 50000,
            "productCode": "p10001",
            "quantity": 1,
            "productPrice": 100,
            "totalPrice": 100
        },
        {
            "id": 50001,
            "productCode": "p10002",
            "quantity": 2,
            "productPrice": 50,
            "totalPrice": 100
        }
    ],
    "orderAmount": 200,
    "orderDate": "2019-12-23T14:50:58.223",
    "customerName": "customer_10001",
    "customerEmailId": "customer_10001@gmail.com",
    "customerAddress": "Flat no 10001- Noida"
  }
  ```
  
* POST create a new order for customer - http://localhost:8072/api/order/ \
  Request payload- 
  ```
  {
        "customerPhoneNo": "9876510003",
        "orderItems" : [{
        	"productCode":"p10001",
        	"quantity":"5",
        	"productPrice":100
        },{
        	"productCode":"p10003",
        	"quantity":"5",	
        	"productPrice":500
        }]
  }
  ```
  Response is -
  ```
  {
    "id": 1,
    "customerPhoneNo": "9876510003",
    "orderItems": [
        {
            "id": 2,
            "productCode": "p10001",
            "quantity": 5,
            "productPrice": 100,
            "totalPrice": 500
        },
        {
            "id": 3,
            "productCode": "p10003",
            "quantity": 5,
            "productPrice": 500,
            "totalPrice": 2500
        }
    ],
    "orderAmount": 3000,
    "orderDate": "2019-12-23T16:52:44.754",
    "customerName": "customer_10003",
    "customerEmailId": "customer_10003@gmail.com",
    "customerAddress": "Flat no 10003- Noida"
  }
  ```
