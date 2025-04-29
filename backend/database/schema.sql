-- Food Ordering Platform Database Schema

-- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS menu_items CASCADE;
DROP TABLE IF EXISTS restaurants CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Create users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    role VARCHAR(50) DEFAULT 'customer',  -- 'customer', 'admin', 'restaurant_owner'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create restaurants table
CREATE TABLE restaurants (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    address TEXT NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(255),
    image_url TEXT,
    cover_image_url TEXT,
    category VARCHAR(100),
    rating DECIMAL(2,1) DEFAULT 0.0,
    price_level VARCHAR(5),  -- '$', '$$', '$$$', '$$$$'
    is_active BOOLEAN DEFAULT TRUE,
    opening_hours JSONB,  -- Store hours in JSON format
    owner_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create menu_items table
CREATE TABLE menu_items (
    id SERIAL PRIMARY KEY,
    restaurant_id INTEGER REFERENCES restaurants(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    image_url TEXT,
    category VARCHAR(100),
    is_vegetarian BOOLEAN DEFAULT FALSE,
    is_vegan BOOLEAN DEFAULT FALSE,
    is_gluten_free BOOLEAN DEFAULT FALSE,
    spicy_level INTEGER DEFAULT 0,  -- 0-4 for spice level
    preparation_time INTEGER,  -- in minutes
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create orders table
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    restaurant_id INTEGER REFERENCES restaurants(id) ON DELETE SET NULL,
    restaurant_name VARCHAR(255),  -- Store name in case restaurant is deleted
    status VARCHAR(50) DEFAULT 'received',  -- received, preparing, ready, picked_up, in_transit, delivered, cancelled
    total_amount DECIMAL(10,2) NOT NULL,
    delivery_address TEXT NOT NULL,
    delivery_fee DECIMAL(10,2) DEFAULT 0,
    estimated_delivery_time INTEGER,  -- in minutes
    payment_method VARCHAR(100),
    payment_status VARCHAR(50) DEFAULT 'pending',  -- pending, completed, failed, refunded
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create order_items table
CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id INTEGER REFERENCES orders(id) ON DELETE CASCADE,
    menu_item_id INTEGER REFERENCES menu_items(id) ON DELETE SET NULL,
    name VARCHAR(255) NOT NULL,  -- Store name in case menu item is deleted
    price DECIMAL(10,2) NOT NULL,  -- Store price at time of order
    quantity INTEGER NOT NULL,
    special_instructions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_restaurants_category ON restaurants(category);
CREATE INDEX idx_restaurants_rating ON restaurants(rating);
CREATE INDEX idx_menu_items_restaurant ON menu_items(restaurant_id);
CREATE INDEX idx_menu_items_category ON menu_items(category);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_restaurant ON orders(restaurant_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order ON order_items(order_id);

-- Add update triggers to set updated_at timestamp
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_users_modtime
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_restaurants_modtime
BEFORE UPDATE ON restaurants
FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_menu_items_modtime
BEFORE UPDATE ON menu_items
FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_orders_modtime
BEFORE UPDATE ON orders
FOR EACH ROW EXECUTE FUNCTION update_modified_column();

-- Insert admin user (password: admin123)
INSERT INTO users (email, password, name, role) 
VALUES ('admin@foodapp.com', '$2a$10$Tm/dZ7ucIrOkGfIKMKLwpe6GwXZZmEMZ.69g6LtFpKQPBQhT2u8OS', 'Admin User', 'admin');

-- Sample data for testing (optional)
-- Insert a few restaurants
INSERT INTO restaurants (name, description, address, phone, category, rating, price_level)
VALUES 
('Burger Palace', 'Delicious burgers and fries', '123 Main St, Anytown, USA', '555-1234', 'Fast Food', 4.2, '$$'),
('Pizza Paradise', 'Authentic Italian pizza', '456 Elm St, Anytown, USA', '555-5678', 'Italian', 4.5, '$$'),
('Sushi Supreme', 'Fresh sushi and Japanese cuisine', '789 Oak St, Anytown, USA', '555-9012', 'Japanese', 4.7, '$$$');

-- Insert some menu items
INSERT INTO menu_items (restaurant_id, name, description, price, category, is_vegetarian, preparation_time)
VALUES 
(1, 'Classic Burger', 'Beef patty with lettuce, tomato, and special sauce', 8.99, 'Burgers', FALSE, 15),
(1, 'Cheeseburger', 'Classic burger with American cheese', 9.99, 'Burgers', FALSE, 15),
(1, 'Veggie Burger', 'Plant-based patty with all the fixings', 10.99, 'Burgers', TRUE, 15),
(1, 'French Fries', 'Crispy golden fries', 3.99, 'Sides', TRUE, 10),
(2, 'Margherita Pizza', 'Tomato sauce, mozzarella, and basil', 12.99, 'Pizzas', TRUE, 20),
(2, 'Pepperoni Pizza', 'Tomato sauce, mozzarella, and pepperoni', 14.99, 'Pizzas', FALSE, 20),
(2, 'Garlic Bread', 'Toasted bread with garlic butter', 4.99, 'Appetizers', TRUE, 10),
(3, 'California Roll', 'Crab, avocado, and cucumber', 6.99, 'Rolls', FALSE, 15),
(3, 'Salmon Nigiri', 'Fresh salmon over rice', 5.99, 'Nigiri', FALSE, 10),
(3, 'Vegetable Tempura', 'Assorted vegetables fried in tempura batter', 8.99, 'Hot Dishes', TRUE, 15);

-- Create a regular customer
INSERT INTO users (email, password, name, phone, address, role) 
VALUES ('customer@example.com', '$2a$10$Tm/dZ7ucIrOkGfIKMKLwpe6GwXZZmEMZ.69g6LtFpKQPBQhT2u8OS', 'Test Customer', '555-1111', '100 Customer St, Anytown, USA', 'customer');