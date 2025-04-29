const db = require('../config/db');

class Order {
  // Create a new order
  static async create(orderData) {
    const { user_id, restaurant_id, items, total_price, delivery_address, payment_method, special_instructions } = orderData;
    
    try {
      // Begin transaction
      await db.query('BEGIN');
      
      // Create order
      const orderQuery = `
        INSERT INTO orders (
          user_id, restaurant_id, total_price, delivery_address, 
          payment_method, status, special_instructions, created_at
        )
        VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
        RETURNING *
      `;
      
      const orderValues = [
        user_id,
        restaurant_id,
        total_price,
        delivery_address,
        payment_method,
        'pending', // Initial status
        special_instructions || null,
        new Date()
      ];
      
      const orderResult = await db.query(orderQuery, orderValues);
      const order = orderResult.rows[0];
      
      // Insert order items
      for (const item of items) {
        const orderItemQuery = `
          INSERT INTO order_items (
            order_id, menu_item_id, quantity, price_per_item
          )
          VALUES ($1, $2, $3, $4)
        `;
        
        await db.query(orderItemQuery, [
          order.id,
          item.menu_item_id,
          item.quantity,
          item.price_per_item
        ]);
      }
      
      // Commit transaction
      await db.query('COMMIT');
      
      // Return the created order with items
      return await this.findById(order.id);
      
    } catch (error) {
      // Rollback transaction on error
      await db.query('ROLLBACK');
      console.error('Error creating order:', error);
      throw error;
    }
  }
  
  // Find order by ID with related items
  static async findById(id) {
    try {
      // Get order details
      const orderQuery = `
        SELECT o.*, r.name as restaurant_name, u.name as user_name, u.phone as user_phone
        FROM orders o
        JOIN restaurants r ON o.restaurant_id = r.id
        JOIN users u ON o.user_id = u.id
        WHERE o.id = $1
      `;
      
      const orderResult = await db.query(orderQuery, [id]);
      const order = orderResult.rows[0];
      
      if (!order) {
        return null;
      }
      
      // Get order items
      const itemsQuery = `
        SELECT oi.*, mi.name, mi.description, mi.image_url
        FROM order_items oi
        JOIN menu_items mi ON oi.menu_item_id = mi.id
        WHERE oi.order_id = $1
      `;
      
      const itemsResult = await db.query(itemsQuery, [id]);
      order.items = itemsResult.rows;
      
      return order;
    } catch (error) {
      console.error('Error finding order by id:', error);
      throw error;
    }
  }
  
  // Get all orders for a user
  static async findByUser(userId) {
    try {
      const query = `
        SELECT o.*, r.name as restaurant_name 
        FROM orders o
        JOIN restaurants r ON o.restaurant_id = r.id
        WHERE o.user_id = $1
        ORDER BY o.created_at DESC
      `;
      
      const result = await db.query(query, [userId]);
      return result.rows;
    } catch (error) {
      console.error('Error finding orders by user:', error);
      throw error;
    }
  }
  
  // Get all orders for a restaurant
  static async findByRestaurant(restaurantId) {
    try {
      const query = `
        SELECT o.*, u.name as user_name, u.phone as user_phone 
        FROM orders o
        JOIN users u ON o.user_id = u.id
        WHERE o.restaurant_id = $1
        ORDER BY 
          CASE 
            WHEN o.status = 'pending' THEN 1
            WHEN o.status = 'confirmed' THEN 2
            WHEN o.status = 'preparing' THEN 3
            WHEN o.status = 'ready' THEN 4
            WHEN o.status = 'delivered' THEN 5
            WHEN o.status = 'cancelled' THEN 6
          END,
          o.created_at DESC
      `;
      
      const result = await db.query(query, [restaurantId]);
      const orders = result.rows;
      
      // Get items for each order
      for (const order of orders) {
        const itemsQuery = `
          SELECT oi.*, mi.name
          FROM order_items oi
          JOIN menu_items mi ON oi.menu_item_id = mi.id
          WHERE oi.order_id = $1
        `;
        
        const itemsResult = await db.query(itemsQuery, [order.id]);
        order.items = itemsResult.rows;
      }
      
      return orders;
    } catch (error) {
      console.error('Error finding orders by restaurant:', error);
      throw error;
    }
  }
  
  // Update order status
  static async updateStatus(id, status) {
    try {
      const query = `
        UPDATE orders
        SET status = $1, updated_at = $2
        WHERE id = $3
        RETURNING *
      `;
      
      const result = await db.query(query, [status, new Date(), id]);
      return result.rows[0];
    } catch (error) {
      console.error('Error updating order status:', error);
      throw error;
    }
  }
  
  // Get recent orders for admin dashboard
  static async getRecent(limit = 10) {
    try {
      const query = `
        SELECT o.*, r.name as restaurant_name, u.name as user_name
        FROM orders o
        JOIN restaurants r ON o.restaurant_id = r.id
        JOIN users u ON o.user_id = u.id
        ORDER BY o.created_at DESC
        LIMIT $1
      `;
      
      const result = await db.query(query, [limit]);
      return result.rows;
    } catch (error) {
      console.error('Error getting recent orders:', error);
      throw error;
    }
  }
  
  // Get order statistics for admin dashboard
  static async getStats() {
    try {
      const query = `
        SELECT 
          COUNT(*) as total_orders,
          COUNT(CASE WHEN status = 'pending' THEN 1 END) as pending_orders,
          COUNT(CASE WHEN status = 'confirmed' THEN 1 END) as confirmed_orders,
          COUNT(CASE WHEN status = 'preparing' THEN 1 END) as preparing_orders,
          COUNT(CASE WHEN status = 'ready' THEN 1 END) as ready_orders,
          COUNT(CASE WHEN status = 'delivered' THEN 1 END) as delivered_orders,
          COUNT(CASE WHEN status = 'cancelled' THEN 1 END) as cancelled_orders,
          SUM(total_price) as total_revenue
        FROM orders
        WHERE created_at >= NOW() - INTERVAL '30 days'
      `;
      
      const result = await db.query(query);
      return result.rows[0];
    } catch (error) {
      console.error('Error getting order statistics:', error);
      throw error;
    }
  }
}

module.exports = Order;
