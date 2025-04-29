#!/bin/bash

# Food Ordering Platform Installation Script

echo "====================================================="
echo "  Food Ordering Platform Installation Script"
echo "====================================================="

# Create backend directory if it doesn't exist
mkdir -p backend
mkdir -p backend/config
mkdir -p backend/models
mkdir -p backend/routes
mkdir -p backend/middleware
mkdir -p backend/database
mkdir -p backend/public/css
mkdir -p backend/public/js
mkdir -p backend/views/admin
mkdir -p backend/views/layouts

# Install backend dependencies
echo "Installing backend dependencies..."
cd backend
npm init -y

# Update package.json
cat > package.json << 'EOF'
{
  "name": "food-ordering-platform-backend",
  "version": "1.0.0",
  "description": "Food Ordering Platform Backend",
  "main": "server.js",
  "scripts": {
    "start": "node server.js",
    "dev": "nodemon server.js",
    "init-db": "node database/init-db.js"
  },
  "keywords": [
    "food",
    "ordering",
    "platform"
  ],
  "author": "",
  "license": "ISC",
  "dependencies": {
    "bcryptjs": "^2.4.3",
    "cors": "^2.8.5",
    "dotenv": "^16.0.3",
    "ejs": "^3.1.9",
    "express": "^4.18.2",
    "express-ejs-layouts": "^2.5.1",
    "jsonwebtoken": "^9.0.0",
    "morgan": "^1.10.0",
    "pg": "^8.10.0"
  },
  "devDependencies": {
    "nodemon": "^2.0.22"
  }
}
EOF

# Install packages
npm install

# Create .env file with default values
cat > .env << 'EOF'
PORT=5000
JWT_SECRET=your_jwt_secret_key_change_this_in_production
DATABASE_URL=postgres://postgres:postgres@localhost:5432/food_ordering
EOF

echo "Backend dependencies installed successfully!"

cd ..

echo "====================================================="
echo "Installation completed successfully!"
echo ""
echo "Next steps:"
echo "1. Make sure PostgreSQL is installed and running"
echo "2. Initialize the database: cd backend && npm run init-db"
echo "3. Start the server: cd backend && npm run dev"
echo "====================================================="