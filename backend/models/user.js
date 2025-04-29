const db = require('../config/db');
const bcrypt = require('bcryptjs');

class User {
  // Create a new user
  static async create(userData) {
    const { name, email, password, phone, address } = userData;
    
    // Hash password
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);
    
    const query = `
      INSERT INTO users (name, email, password, phone, address, role, created_at)
      VALUES ($1, $2, $3, $4, $5, $6, $7)
      RETURNING id, name, email, phone, address, role, created_at
    `;
    
    const values = [
      name, 
      email, 
      hashedPassword, 
      phone, 
      address, 
      'user', 
      new Date()
    ];
    
    try {
      const result = await db.query(query, values);
      return result.rows[0];
    } catch (error) {
      console.error('Error creating user:', error);
      throw error;
    }
  }
  
  // Find user by email
  static async findByEmail(email) {
    try {
      const query = 'SELECT * FROM users WHERE email = $1';
      const result = await db.query(query, [email]);
      return result.rows[0];
    } catch (error) {
      console.error('Error finding user by email:', error);
      throw error;
    }
  }
  
  // Find user by ID
  static async findById(id) {
    try {
      const query = 'SELECT id, name, email, phone, address, role, created_at FROM users WHERE id = $1';
      const result = await db.query(query, [id]);
      return result.rows[0];
    } catch (error) {
      console.error('Error finding user by id:', error);
      throw error;
    }
  }
  
  // Update user details
  static async update(id, userData) {
    const { name, email, phone, address } = userData;
    
    try {
      const query = `
        UPDATE users
        SET name = $1, email = $2, phone = $3, address = $4, updated_at = $5
        WHERE id = $6
        RETURNING id, name, email, phone, address, role, created_at
      `;
      
      const values = [name, email, phone, address, new Date(), id];
      const result = await db.query(query, values);
      
      return result.rows[0];
    } catch (error) {
      console.error('Error updating user:', error);
      throw error;
    }
  }
  
  // Create admin user (for initial setup)
  static async createAdmin(userData) {
    const { name, email, password } = userData;
    
    // Hash password
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);
    
    const query = `
      INSERT INTO users (name, email, password, role, created_at)
      VALUES ($1, $2, $3, $4, $5)
      RETURNING id, name, email, role, created_at
    `;
    
    const values = [name, email, hashedPassword, 'admin', new Date()];
    
    try {
      const result = await db.query(query, values);
      return result.rows[0];
    } catch (error) {
      console.error('Error creating admin user:', error);
      throw error;
    }
  }
}

module.exports = User;
