CREATE DATABASE food_delivery_db;
USE food_delivery_db;
SHOW DATABASES;

USE food_delivery_db;

CREATE TABLE customer (
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(15) UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
DESCRIBE customer;
INSERT INTO customer (name, email, phone, password)
VALUES
('Arun', 'arun@gmail.com', '9876543210', 'TEMP_HASH_1'),
('Priya', 'priya@gmail.com', '9876543211', 'TEMP_HASH_2');

SELECT * FROM customer;
USE food_delivery_db;

-- 1. Customer addresses
CREATE TABLE address (
    address_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    house_details VARCHAR(150) NOT NULL,
    street VARCHAR(150) NOT NULL,
    city VARCHAR(100) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

-- 2. Restaurants
CREATE TABLE restaurant (
    restaurant_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(15) UNIQUE,
    address VARCHAR(255) NOT NULL,
    is_open BOOLEAN NOT NULL DEFAULT TRUE,
    approval_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Food categories
CREATE TABLE category (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- 4. Food items
CREATE TABLE food_item (
    food_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    restaurant_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant(restaurant_id),
    FOREIGN KEY (category_id) REFERENCES category(category_id)
);

-- 5. Customer cart
CREATE TABLE cart (
    cart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT UNIQUE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

-- 6. Items in cart
CREATE TABLE cart_item (
    cart_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id BIGINT NOT NULL,
    food_item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    UNIQUE (cart_id, food_item_id),
    FOREIGN KEY (cart_id) REFERENCES cart(cart_id),
    FOREIGN KEY (food_item_id) REFERENCES food_item(food_item_id)
);

-- 7. Delivery partners
CREATE TABLE delivery_partner (
    delivery_partner_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) UNIQUE NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 8. Orders
CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    delivery_partner_id BIGINT,
    address_id BIGINT,

    -- Delivery address snapshot
    delivery_house_details VARCHAR(150) NOT NULL,
    delivery_street VARCHAR(150) NOT NULL,
    delivery_city VARCHAR(100) NOT NULL,
    delivery_pincode VARCHAR(10) NOT NULL,

    subtotal DECIMAL(10,2) NOT NULL,
    delivery_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,

    order_status VARCHAR(30) NOT NULL DEFAULT 'PENDING_PAYMENT',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurant(restaurant_id),
    FOREIGN KEY (delivery_partner_id)
        REFERENCES delivery_partner(delivery_partner_id),
    FOREIGN KEY (address_id) REFERENCES address(address_id)
);

-- 9. Items belonging to an order
CREATE TABLE order_item (
    order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    food_item_id BIGINT NOT NULL,
    item_name_snapshot VARCHAR(150) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (food_item_id) REFERENCES food_item(food_item_id)
);

-- 10. Payment attempts
CREATE TABLE payment (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(30),
    payment_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    transaction_reference VARCHAR(150) UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

-- 11. Refund records
CREATE TABLE refund (
    refund_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    refund_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    reason VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (payment_id) REFERENCES payment(payment_id)
);

-- 12. Order status history
CREATE TABLE order_status_history (
    history_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    remarks VARCHAR(255),

    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

-- 13. Coupons
CREATE TABLE coupon (
    coupon_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) UNIQUE NOT NULL,
    discount_type VARCHAR(20) NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    minimum_order_amount DECIMAL(10,2) DEFAULT 0.00,
    expiry_date DATETIME,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- 14. Customer reviews
CREATE TABLE review (
    review_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurant(restaurant_id)
);
