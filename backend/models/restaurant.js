const db = require('../config/db');

class Restaurant {
  // Create a new restaurant
  static async create(restaurantData) {
    const { name, description, address, phone, image_url, opening_hours, category } = restaurantData;
    
    const query = `
      INSERT INTO restaurants (name, description, address, phone, image_url, opening_hours, category, created_at)
      VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
      RETURNING *
    `;
    
    const values = [
      name,
      description,
      address,
      phone,
      image_url || null,
      opening_hours,
      category,
      new Date()
    ];
    
    try {
      const result = await db.query(query, values);
      return result.rows[0];
    } catch (error) {
      console.error('Error creating restaurant:', error);
      throw error;
    }
  }
  
  // Get all restaurants
  static async findAll() {
    try {
      const query = 'SELECT * FROM restaurants ORDER BY name ASC';
      const result = await db.query(query);
      return result.rows;
    } catch (error) {
      console.error('Error finding all restaurants:', error);
      throw error;
    }
  }
  
  // Find restaurant by ID
  static async findById(id) {
    try {
      const query = 'SELECT * FROM restaurants WHERE id = $1';
      const result = await db.query(query, [id]);
      return result.rows[0];
    } catch (error) {
      console.error('Error finding restaurant by id:', error);
      throw error;
    }
  }
  
  // Update restaurant
  static async update(id, restaurantData) {
    const { name, description, address, phone, image_url, opening_hours, category } = restaurantData;
    
    try {
      const query = `
        UPDATE restaurants
        SET name = $1, description = $2, address = $3, phone = $4, image_url = $5, 
            opening_hours = $6, category = $7, updated_at = $8
        WHERE id = $9
        RETURNING *
      `;
      
      const values = [
        name,
        description,
        address,
        phone,
        image_url,
        opening_hours,
        category,
        new Date(),
        id
      ];
      
      const result = await db.query(query, values);
      return result.rows[0];
    } catch (error) {
      console.error('Error updating restaurant:', error);
      throw error;
    }
  }
  
  // Delete restaurant
  static async delete(id) {
    try {
      // First, delete all related menu items
      await db.query('DELETE FROM menu_items WHERE restaurant_id = $1', [id]);
      
      // Then, delete the restaurant
      const query = 'DELETE FROM restaurants WHERE id = $1 RETURNING *';
      const result = await db.query(query, [id]);
      return result.rows[0];
    } catch (error) {
      console.error('Error deleting restaurant:', error);
      throw error;
    }
  }
  
  // Get restaurant by category
  static async findByCategory(category) {
    try {
      const query = 'SELECT * FROM restaurants WHERE category = $1 ORDER BY name ASC';
      const result = await db.query(query, [category]);
      return result.rows;
    } catch (error) {
      console.error('Error finding restaurants by category:', error);
      throw error;
    }
  }
  
  // Search restaurants
  static async search(term) {
    try {
      const query = `
        SELECT * FROM restaurants 
        WHERE name ILIKE $1 OR description ILIKE $1 OR category ILIKE $1
        ORDER BY name ASC
      `;
      const result = await db.query(query, [`%${term}%`]);
      return result.rows;
    } catch (error) {
      console.error('Error searching restaurants:', error);
      throw error;
    }
  }
}

module.exports = Restaurant;
