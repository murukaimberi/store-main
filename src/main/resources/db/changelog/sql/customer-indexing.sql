CREATE INDEX idx_order_customer_id ON customer_order(customer_id);
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_customer_name_trgm ON customer USING GIN (name gin_trgm_ops);