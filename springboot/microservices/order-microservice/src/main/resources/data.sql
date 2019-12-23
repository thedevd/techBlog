insert into orders (order_id,customer_phone_no, order_amount, order_date) VALUES
(10001, '9876510001', 200, parsedatetime('2019-12-23 14:50:58.223','yyyy-MM-dd hh:mm:ss.SS'));
INSERT INTO order_items (id, product_code, unit_price, quantity, order_id) values 
(50000, 'p10001', 100, 1, 10001),(50001, 'p10002', 50, 2, 10001);

INSERT INTO orders (order_id,customer_phone_no, order_amount, order_date) VALUES
(10002, '9876510002', 500, parsedatetime('2019-12-23 15:50:58.223','yyyy-MM-dd hh:mm:ss.SS'));
INSERT INTO order_items (id, product_code, unit_price, quantity, order_id) values 
(50003, 'p10003', 200, 1, 10002),(50004, 'p10002', 50, 6, 10002);