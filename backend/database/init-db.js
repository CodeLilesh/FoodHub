const { Pool } = require('pg');
const fs = require('fs');
const path = require('path');
require('dotenv').config();

// Get database URL from environment variables
const dbUrl = process.env.DATABASE_URL;

if (!dbUrl) {
  console.error('DATABASE_URL environment variable is not set.');
  process.exit(1);
}

// Function to execute SQL file
async function executeSqlFile(filePath) {
  const sql = fs.readFileSync(filePath, 'utf8');
  
  const pool = new Pool({
    connectionString: dbUrl,
    ssl: process.env.NODE_ENV === 'production' 
      ? { rejectUnauthorized: false }
      : false
  });
  
  try {
    console.log(`Executing SQL from file: ${filePath}`);
    await pool.query(sql);
    console.log('SQL execution completed successfully.');
    return true;
  } catch (error) {
    console.error('Error executing SQL:', error.message);
    if (error.position) {
      const errorPosition = parseInt(error.position, 10);
      const errorContext = sql.substring(
        Math.max(0, errorPosition - 100),
        Math.min(sql.length, errorPosition + 100)
      );
      console.error('Error context:', errorContext);
    }
    return false;
  } finally {
    await pool.end();
  }
}

// Initialize database
async function initializeDatabase() {
  console.log('Initializing database...');
  
  // There's a circular reference in the setup.sql file
  // We need to create the restaurants table before the categories table
  
  // Create restaurants table first
  const restaurantsTableSql = `
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
  );`;
  
  const pool = new Pool({
    connectionString: dbUrl,
    ssl: process.env.NODE_ENV === 'production' 
      ? { rejectUnauthorized: false }
      : false
  });
  
  try {
    // Create restaurants table first
    console.log('Creating restaurants table...');
    await pool.query(restaurantsTableSql);
    
    // Then execute the full setup script
    const setupSqlPath = path.join(__dirname, 'setup.sql');
    const success = await executeSqlFile(setupSqlPath);
    
    if (success) {
      console.log('Database initialized successfully!');
    } else {
      console.error('Failed to initialize database.');
    }
  } catch (error) {
    console.error('Error initializing database:', error);
  } finally {
    await pool.end();
  }
}

// Run the initialization
initializeDatabase();