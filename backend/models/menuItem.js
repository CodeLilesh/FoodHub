const db = require('../config/db');

class MenuItem {
  // Create a new menu item
  static async create(menuItemData) {
    const { 
      restaurant_id, 
      name, 
      description, 
      price, 
      image_url, 
      category,
      is_vegetarian,
      is_available
    } = menuItemData;
    
    const query = `
      INSERT INTO menu_items (
        restaurant_id, name, description, price, image_url, 
        category, is_vegetarian, is_available, created_at
      )
      VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
      RETURNING *
    `;
    
    const values = [
      restaurant_id,
      name,
      description || null,
      price,
      image_url || null,
      category || 'Main',
      is_vegetarian || false,
      is_available !== undefined ? is_available : true,
      new Date()
    ];
    
    try {
      const result = await db.query(query, values);
      return result.rows[0];
    } catch (error) {
      console.error('Error creating menu item:', error);
      throw error;
    }
  }
  
  // Get all menu items for a specific restaurant
  static async findByRestaurant(restaurantId) {
    try {
      const query = `
        SELECT * FROM menu_items 
        WHERE restaurant_id = $1 
        ORDER BY category, name ASC
      `;
      
      const result = await db.query(query, [restaurantId]);
      return result.rows;
    } catch (error) {
      console.error('Error finding menu items by restaurant:', error);
      throw error;
    }
  }
  
  // Find menu item by ID
  static async findById(id) {
    try {
      const query = 'SELECT * FROM menu_items WHERE id = $1';
      const result = await db.query(query, [id]);
      return result.rows[0];
    } catch (error) {
      console.error('Error finding menu item by id:', error);
      throw error;
    }
  }
  
  // Update menu item
  static async update(id, menuItemData) {
    const { 
      name, 
      description, 
      price, 
      image_url, 
      category,
      is_vegetarian,
      is_available
    } = menuItemData;
    
    try {
      const query = `
        UPDATE menu_items
        SET name = $1, description = $2, price = $3, image_url = $4, 
            category = $5, is_vegetarian = $6, is_available = $7, updated_at = $8
        WHERE id = $9
        RETURNING *
      `;
      
      const values = [
        name,
        description,
        price,
        image_url,
        category,
        is_vegetarian,
        is_available,
        new Date(),
        id
      ];
      
      const result = await db.query(query, values);
      return result.rows[0];
    } catch (error) {
      console.error('Error updating menu item:', error);
      throw error;
    }
  }
  
  // Delete menu item
  static async delete(id) {
    try {
      const query = 'DELETE FROM menu_items WHERE id = $1 RETURNING *';
      const result = await db.query(query, [id]);
      return result.rows[0];
    } catch (error) {
      console.error('Error deleting menu item:', error);
      throw error;
    }
  }
  
  // Get menu items by category
  static async findByCategory(restaurantId, category) {
    try {
      const query = `
        SELECT * FROM menu_items 
        WHERE restaurant_id = $1 AND category = $2
        ORDER BY name ASC
      `;
      
      const result = await db.query(query, [restaurantId, category]);
      return result.rows;
    } catch (error) {
      console.error('Error finding menu items by category:', error);
      throw error;
    }
  }
  
  // Get menu items with search
  static async search(restaurantId, term) {
    try {
      const query = `
        SELECT * FROM menu_items 
        WHERE restaurant_id = $1 AND 
              (name ILIKE $2 OR description ILIKE $2 OR category ILIKE $2)
        ORDER BY category, name ASC
      `;
      
      const result = await db.query(query, [restaurantId, `%${term}%`]);
      return result.rows;
    } catch (error) {
      console.error('Error searching menu items:', error);
      throw error;
    }
  }
  
  // Toggle availability of menu item
  static async toggleAvailability(id) {
    try {
      const query = `
        UPDATE menu_items
        SET is_available = NOT is_available,
            updated_at = $1
        WHERE id = $2
        RETURNING *
      `;
      
      const result = await db.query(query, [new Date(), id]);
      return result.rows[0];
    } catch (error) {
      console.error('Error toggling menu item availability:', error);
      throw error;
    }
  }
}

module.exports = MenuItem;
