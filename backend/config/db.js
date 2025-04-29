const { Pool } = require('pg');
require('dotenv').config();

// Get database URL from environment variables or use default
const dbUrl = process.env.DATABASE_URL || 'postgres://postgres:postgres@localhost:5432/food_ordering';

const pool = new Pool({
  connectionString: dbUrl,
  ssl: process.env.NODE_ENV === 'production' 
    ? { rejectUnauthorized: false }
    : false
});

// Test the database connection
pool.query('SELECT NOW()', (err, res) => {
  if (err) {
    console.error('Error connecting to PostgreSQL database:', err);
  } else {
    console.log('Connected to PostgreSQL database');
  }
});

module.exports = {
  query: (text, params) => pool.query(text, params)
};