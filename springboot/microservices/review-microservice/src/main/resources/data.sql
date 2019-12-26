INSERT INTO review (id, product_code, customer_name, headline, description, rating, review_date) VALUES 
(20000, 'p10000', 'customer_10001', 'Overall good','the product is good in working', 3.5, parsedatetime('2019-12-26 14:50:58.223','yyyy-MM-dd hh:mm:ss.SS')),
(20001, 'p10001', 'customer_10001', 'Very Bad','the product stopped working after 2 months', 1, parsedatetime('2019-12-26 15:00:08.333','yyyy-MM-dd hh:mm:ss.SS')),
(20002, 'p10001', 'customer_10002', 'Average','the product is average at this price', 2.5, parsedatetime('2019-12-26 15:12:08.333','yyyy-MM-dd hh:mm:ss.SS')),
(20003, 'p10000', 'customer_10003', 'Worth at price','the product is worth at this price. I am happy', 5, parsedatetime('2019-12-26 15:00:08.333','yyyy-MM-dd hh:mm:ss.SS'));
