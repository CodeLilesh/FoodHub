const express = require('express');
const router = express.Router();
const { Pool } = require('pg');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

// Database connection
const pool = new Pool({
  connectionString: process.env.DATABASE_URL
});

// Middleware to authenticate admin
const authenticateAdmin = async (req, res, next) => {
  try {
    // Get token from header
    const token = req.header('Authorization')?.replace('Bearer ', '');
    
    if (!token) {
      return res.status(401).json({ message: 'No token, authorization denied' });
    }
    
    // Verify token
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    
    // Check if user exists and is an admin
    const result = await pool.query(
      'SELECT * FROM users WHERE id = $1 AND role = $2',
      [decoded.userId, 'admin']
    );
    
    if (result.rows.length === 0) {
      return res.status(401).json({ message: 'Not authorized as admin' });
    }
    
    // Set user in request
    req.user = result.rows[0];
    next();
  } catch (err) {
    console.error(err.message);
    res.status(401).json({ message: 'Token is not valid' });
  }
};

// Login route
router.post('/login', async (req, res) => {
  const { email, password } = req.body;
  
  try {
    // Check if user exists
    const result = await pool.query(
      'SELECT * FROM users WHERE email = $1 AND role = $2',
      [email, 'admin']
    );
    
    if (result.rows.length === 0) {
      return res.status(400).json({ message: 'Invalid credentials' });
    }
    
    const user = result.rows[0];
    
    // Check password
    const isMatch = await bcrypt.compare(password, user.password);
    
    if (!isMatch) {
      return res.status(400).json({ message: 'Invalid credentials' });
    }
    
    // Create and sign JWT
    const payload = {
      userId: user.id,
      role: user.role
    };
    
    jwt.sign(
      payload,
      process.env.JWT_SECRET,
      { expiresIn: '1d' },
      (err, token) => {
        if (err) throw err;
        res.json({ token, user: { id: user.id, name: user.name, email: user.email, role: user.role } });
      }
    );
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server error');
  }
});

// Get dashboard stats
router.get('/stats', authenticateAdmin, async (req, res) => {
  try {
    // Get total users
    const usersResult = await pool.query('SELECT COUNT(*) FROM users');
    const totalUsers = parseInt(usersResult.rows[0].count);
    
    // Get total restaurants
    const restaurantsResult = await pool.query('SELECT COUNT(*) FROM restaurants WHERE is_active = true');
    const activeRestaurants = parseInt(restaurantsResult.rows[0].count);
    
    // Get total orders
    const ordersResult = await pool.query('SELECT COUNT(*) FROM orders');
    const totalOrders = parseInt(ordersResult.rows[0].count);
    
    // Get total revenue
    const revenueResult = await pool.query('SELECT SUM(total_amount) FROM orders WHERE status != $1', ['cancelled']);
    const totalRevenue = parseFloat(revenueResult.rows[0].sum || 0);
    
    // Get recent orders
    const recentOrdersResult = await pool.query(`
      SELECT o.id, o.status, o.total_amount, o.created_at, u.name as customer_name, r.name as restaurant_name
      FROM orders o
      LEFT JOIN users u ON o.user_id = u.id
      LEFT JOIN restaurants r ON o.restaurant_id = r.id
      ORDER BY o.created_at DESC
      LIMIT 5
    `);
    
    res.json({
      stats: {
        totalUsers,
        activeRestaurants,
        totalOrders,
        totalRevenue
      },
      recentOrders: recentOrdersResult.rows
    });
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server error');
  }
});

module.exports = router;