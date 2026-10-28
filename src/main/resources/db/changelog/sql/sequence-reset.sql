SELECT setval('customer_id_seq', (SELECT MAX(id) FROM customer));
SELECT setval('customer_order_id_seq',    (SELECT MAX(id) FROM customer_order));