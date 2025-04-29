-- Food Ordering Platform Database Schema

-- Users table
CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  phone VARCHAR(20),
  address TEXT,
  role VARCHAR(20) NOT NULL DEFAULT 'user',
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP
);

-- Restaurant Categories table
CREATE TABLE IF NOT EXISTS categories (
  id SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  restaurant_id INTEGER REFERENCES restaurants(id) ON DELETE CASCADE,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  UNIQUE(name, restaurant_id)
);

-- Menu Items table
CREATE TABLE IF NOT EXISTS menu_items (
  id SERIAL PRIMARY KEY,
  restaurant_id INTEGER NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  price DECIMAL(10,2) NOT NULL,
  image_url TEXT,
  category VARCHAR(50),
  is_available BOOLEAN DEFAULT TRUE,
  is_featured BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  restaurant_id INTEGER NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
  total_amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'pending',
  delivery_address TEXT NOT NULL,
  payment_method VARCHAR(20) NOT NULL,
  payment_status VARCHAR(20) DEFAULT 'pending',
  contact_phone VARCHAR(20),
  notes TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP
);

-- Order Items table
CREATE TABLE IF NOT EXISTS order_items (
  id SERIAL PRIMARY KEY,
  order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  menu_item_id INTEGER NOT NULL REFERENCES menu_items(id) ON DELETE CASCADE,
  quantity INTEGER NOT NULL,
  price_per_item DECIMAL(10,2) NOT NULL,
  special_instructions TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Restaurants table
CREATE TABLE IF NOT EXISTS restaurants (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  address TEXT NOT NULL,
  phone VARCHAR(20) NOT NULL,
  email VARCHAR(100),
  image_url TEXT,
  logo_url TEXT,
  category VARCHAR(50),
  opening_hours VARCHAR(255),
  closing_hours VARCHAR(255),
  delivery_fee DECIMAL(5,2) DEFAULT 0,
  min_order_amount DECIMAL(10,2) DEFAULT 0,
  delivery_time VARCHAR(50),
  rating DECIMAL(3,2),
  is_featured BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP
);

-- Create default admin user
INSERT INTO users (name, email, password, role, created_at)
VALUES (
  'Admin User', 
  'admin@foodapp.com', 
  '$2b$10$iMQn0LfsMyRel..lRc08q.PpJtZ5aH1DUx4SPXAr5BjWh5DymifQ6', -- password is 'admin123'
  'admin',
  NOW()
) ON CONFLICT (email) DO NOTHING;

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_restaurants_category ON restaurants(category);
CREATE INDEX IF NOT EXISTS idx_menu_items_restaurant ON menu_items(restaurant_id);
CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_restaurant ON orders(restaurant_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);

-- Seed Restaurants
INSERT INTO restaurants (name, description, address, phone, email, category, opening_hours, closing_hours, delivery_fee, min_order_amount, delivery_time)
VALUES
  ('Burger House', 'Delicious burgers and fries', '123 Main St, Downtown', '555-1234', 'contact@burgerhouse.com', 'Fast Food', '10:00', '22:00', 2.99, 10.00, '30-45 min'),
  ('Pizza Palace', 'Authentic Italian pizza', '456 Park Ave, Midtown', '555-5678', 'info@pizzapalace.com', 'Italian', '11:00', '23:00', 1.99, 15.00, '25-40 min'),
  ('Sushi World', 'Fresh and authentic Japanese cuisine', '789 Broadway, Uptown', '555-9012', 'hello@sushiworld.com', 'Japanese', '12:00', '22:30', 3.99, 20.00, '35-50 min'),
  ('Taco Time', 'Authentic Mexican street food', '321 Oak St, West Side', '555-3456', 'hola@tacotime.com', 'Mexican', '10:30', '21:00', 2.50, 12.00, '20-35 min'),
  ('Green Salad', 'Healthy salads and bowls', '654 Pine St, East Side', '555-7890', 'eat@greensalad.com', 'Healthy', '09:00', '20:00', 2.99, 15.00, '15-30 min')
ON CONFLICT DO NOTHING;

-- Seed Menu Items
INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available, is_featured)
VALUES
  (1, 'Classic Burger', 'Beef patty with lettuce, tomato, and special sauce', 8.99, 'Burgers', TRUE, TRUE),
  (1, 'Cheeseburger', 'Classic burger with American cheese', 9.99, 'Burgers', TRUE, FALSE),
  (1, 'Bacon Burger', 'Classic burger with crispy bacon', 10.99, 'Burgers', TRUE, FALSE),
  (1, 'French Fries', 'Crispy golden fries', 3.99, 'Sides', TRUE, FALSE),
  (1, 'Onion Rings', 'Crispy battered onion rings', 4.99, 'Sides', TRUE, FALSE),
  (1, 'Chocolate Milkshake', 'Creamy chocolate milkshake', 5.99, 'Drinks', TRUE, FALSE),
  
  (2, 'Margherita Pizza', 'Classic tomato sauce, mozzarella, and basil', 12.99, 'Pizzas', TRUE, TRUE),
  (2, 'Pepperoni Pizza', 'Tomato sauce, mozzarella, and pepperoni', 14.99, 'Pizzas', TRUE, FALSE),
  (2, 'Vegetarian Pizza', 'Tomato sauce, mozzarella, and mixed vegetables', 13.99, 'Pizzas', TRUE, FALSE),
  (2, 'Garlic Bread', 'Toasted bread with garlic butter', 4.99, 'Sides', TRUE, FALSE),
  (2, 'Tiramisu', 'Classic Italian dessert', 6.99, 'Desserts', TRUE, FALSE),
  
  (3, 'California Roll', 'Crab, avocado, and cucumber', 7.99, 'Rolls', TRUE, TRUE),
  (3, 'Spicy Tuna Roll', 'Spicy tuna and cucumber', 8.99, 'Rolls', TRUE, FALSE),
  (3, 'Salmon Nigiri', 'Fresh salmon on rice (2 pieces)', 5.99, 'Nigiri', TRUE, FALSE),
  (3, 'Miso Soup', 'Traditional Japanese soup', 3.99, 'Sides', TRUE, FALSE),
  (3, 'Edamame', 'Steamed soybeans with salt', 4.99, 'Sides', TRUE, FALSE),
  
  (4, 'Beef Taco', 'Seasoned beef, lettuce, cheese in corn tortilla', 3.99, 'Tacos', TRUE, TRUE),
  (4, 'Chicken Quesadilla', 'Grilled chicken and cheese in flour tortilla', 8.99, 'Quesadillas', TRUE, FALSE),
  (4, 'Veggie Burrito', 'Rice, beans, and vegetables in flour tortilla', 7.99, 'Burritos', TRUE, FALSE),
  (4, 'Guacamole & Chips', 'Fresh guacamole with tortilla chips', 5.99, 'Sides', TRUE, FALSE),
  (4, 'Mexican Rice', 'Seasoned rice with vegetables', 2.99, 'Sides', TRUE, FALSE),
  
  (5, 'Caesar Salad', 'Romaine lettuce, croutons, parmesan, and Caesar dressing', 9.99, 'Salads', TRUE, TRUE),
  (5, 'Greek Salad', 'Tomatoes, cucumbers, olives, feta cheese, and vinaigrette', 10.99, 'Salads', TRUE, FALSE),
  (5, 'Quinoa Bowl', 'Quinoa, roasted vegetables, and tahini dressing', 11.99, 'Bowls', TRUE, FALSE),
  (5, 'Fresh Juice', 'Freshly squeezed fruit juice', 4.99, 'Drinks', TRUE, FALSE),
  (5, 'Fruit Parfait', 'Yogurt, granola, and fresh fruits', 6.99, 'Desserts', TRUE, FALSE)
ON CONFLICT DO NOTHING;