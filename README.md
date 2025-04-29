# Food Ordering Platform

A comprehensive food ordering platform with a mobile app for customers (Android/Kotlin) and a web-based admin panel (Node.js/Express) sharing a common PostgreSQL database.

## Project Structure

- **backend/** - Node.js/Express backend API and admin panel
- **FoodOrderingApp/** - Android/Kotlin mobile app for customers (to be implemented)

## Prerequisites

- Node.js (v14.x or higher)
- npm (v6.x or higher)
- PostgreSQL (v12.x or higher)
- Android Studio (for mobile app development)

## Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd food-ordering-platform
```

### 2. Backend Setup

Run the installation script:

```bash
chmod +x install.sh
./install.sh
```

Or set up manually:

```bash
cd backend

# Install dependencies
npm install

# Configure your environment
cp .env.example .env
# Edit .env with your PostgreSQL connection details
```

### 3. Database Setup

Make sure PostgreSQL is installed and running. Then initialize the database:

```bash
cd backend
npm run init-db
```

### 4. Start the Backend Server

```bash
cd backend
npm run dev
```

The server will start on port 5000 (or the port specified in your .env file).

## Backend API Routes

### Authentication

- `POST /api/auth/admin/login` - Admin login
- `GET /api/auth/admin/me` - Get current admin user

### Restaurants

- `GET /api/restaurants` - Get all restaurants
- `GET /api/restaurants/:id` - Get restaurant by ID
- `POST /api/restaurants` - Create a restaurant (Admin only)
- `PUT /api/restaurants/:id` - Update a restaurant (Admin only)
- `DELETE /api/restaurants/:id` - Delete a restaurant (Admin only)

### Menu Items

- `GET /api/menu-items/restaurant/:restaurantId` - Get menu items by restaurant
- `GET /api/menu-items/:id` - Get menu item by ID
- `POST /api/menu-items` - Create a menu item (Admin only)
- `PUT /api/menu-items/:id` - Update a menu item (Admin only)
- `DELETE /api/menu-items/:id` - Delete a menu item (Admin only)

### Orders

- `GET /api/orders/user` - Get orders for current user
- `GET /api/orders/restaurant/:restaurantId` - Get orders for a restaurant (Restaurant admin only)
- `POST /api/orders` - Create a new order
- `PUT /api/orders/:id/status` - Update order status (Admin only)

## Admin Panel

Access the admin panel at `http://localhost:5000/admin/login` with these credentials:

- Email: `admin@foodapp.com`
- Password: `admin123`

## Mobile App

The Kotlin Android app is under development.

## Database Schema

The database includes the following tables:

- `users` - User information and authentication
- `restaurants` - Restaurant information
- `menu_items` - Menu items for each restaurant
- `orders` - Customer orders
- `order_items` - Items within each order

## License

This project is licensed under the MIT License.