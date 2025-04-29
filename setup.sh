#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}===== Food Ordering Platform Setup =====${NC}"
echo -e "${YELLOW}This script will set up your development environment${NC}"
echo ""

# Create backend directories if they don't exist
echo -e "${GREEN}Creating directory structure...${NC}"
mkdir -p backend/config
mkdir -p backend/public/css
mkdir -p backend/public/js
mkdir -p backend/public/img
mkdir -p backend/views
mkdir -p backend/views/admin
mkdir -p backend/routes/api
mkdir -p backend/models
mkdir -p backend/middleware
mkdir -p backend/database

# Create .env file if it doesn't exist
if [ ! -f "backend/.env" ]; then
    echo -e "${GREEN}Creating .env file...${NC}"
    cat > backend/.env << EOL
PORT=5000
DATABASE_URL=postgresql://localhost:5432/food_ordering_app
JWT_SECRET=your-secret-key-for-jwt-tokens
SESSION_SECRET=your-secret-key-for-sessions
EOL
    echo -e "${GREEN}Created .env file${NC}"
else
    echo -e "${GREEN}.env file already exists${NC}"
fi

# Check if PostgreSQL is installed
if command -v psql >/dev/null 2>&1; then
    echo -e "${GREEN}PostgreSQL is installed${NC}"
    
    # Prompt user to create database
    echo -e "${YELLOW}Would you like to create the PostgreSQL database? (y/n)${NC}"
    read -r create_db
    
    if [ "$create_db" = "y" ] || [ "$create_db" = "Y" ]; then
        echo -e "${GREEN}Creating PostgreSQL database...${NC}"
        
        # Check if database exists
        if psql -lqt | cut -d \| -f 1 | grep -qw food_ordering_app; then
            echo -e "${YELLOW}Database already exists. Do you want to drop and recreate it? (y/n)${NC}"
            read -r recreate_db
            
            if [ "$recreate_db" = "y" ] || [ "$recreate_db" = "Y" ]; then
                # Drop and recreate database
                dropdb food_ordering_app
                createdb food_ordering_app
                echo -e "${GREEN}Database recreated${NC}"
                
                # Apply schema
                echo -e "${GREEN}Applying database schema...${NC}"
                psql -d food_ordering_app -f backend/database/schema.sql
                echo -e "${GREEN}Schema applied${NC}"
            fi
        else
            # Create new database
            createdb food_ordering_app
            echo -e "${GREEN}Database created${NC}"
            
            # Apply schema
            echo -e "${GREEN}Applying database schema...${NC}"
            psql -d food_ordering_app -f backend/database/schema.sql
            echo -e "${GREEN}Schema applied${NC}"
        fi
    else
        echo -e "${YELLOW}Skipping database creation${NC}"
    fi
else
    echo -e "${RED}PostgreSQL is not installed. Please install it manually.${NC}"
    echo -e "${YELLOW}For Ubuntu: sudo apt-get install postgresql postgresql-contrib${NC}"
    echo -e "${YELLOW}For macOS: brew install postgresql${NC}"
    echo -e "${YELLOW}For Windows: Download from https://www.postgresql.org/download/windows/${NC}"
fi

# Install backend dependencies
echo -e "${GREEN}Installing backend dependencies...${NC}"
cd backend && npm install
cd ..

echo -e "${GREEN}Setup complete! Here's how to get started:${NC}"
echo -e "${YELLOW}1. Start the backend server:${NC}"
echo -e "   cd backend && npm run dev"
echo -e "${YELLOW}2. Access the admin panel:${NC}"
echo -e "   http://localhost:5000/admin/login"
echo -e "${YELLOW}3. Default admin credentials:${NC}"
echo -e "   Email: admin@foodapp.com"
echo -e "   Password: admin123"
echo -e "${YELLOW}4. API Documentation:${NC}"
echo -e "   Available at http://localhost:5000/api/docs"
echo -e ""
echo -e "${GREEN}Happy coding!${NC}"