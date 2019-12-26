### Rest Endpoints
1. Get all reviews of a product - `/api/review/{productCode}` \
   GET http://localhost:8052/api/review/p10000
   ```
   [
    {
        "id": 20000,
        "productCode": "p10000",
        "customerName": "customer_10001",
        "headline": "Overall good",
        "description": "the product is good in working",
        "rating": 3.5,
        "reviewDate": "2019-12-26T14:50:58.223"
    },
    {
        "id": 20003,
        "productCode": "p10000",
        "customerName": "customer_10003",
        "headline": "Worth at price",
        "description": "the product is worth at this price. I am happy",
        "rating": 5,
        "reviewDate": "2019-12-26T15:00:08.333"
    }
   ]
   ```
 
2. Delete a review - `/api/review/{id}` \
   DELETE http://localhost:8052/api/review/20000
   
3. Create a review - `/api/review/` \
   POST http://localhost:8052/api/review/ \
   request payload - 
   ```
   {
    "productCode": "p10000",
    "customerName": "customer_10002",
    "headline": "Felt cheated",
    "description": "product seems fake, got fake accessories with it, so disappointed",
    "rating": 1
   }
   ```
   Response - 
   ```
   {
    "id": 3,
    "productCode": "p10000",
    "customerName": "customer_10002",
    "headline": "Felt cheated",
    "description": "product seems fake, got fake accessories with it, so disappointed",
    "rating": 1,
    "reviewDate": "2019-12-26T14:41:55.967"
   }
   ```
