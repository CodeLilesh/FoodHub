const express = require('express');
const router = express.Router();
const Restaurant = require('../../models/restaurant');
const MenuItem = require('../../models/menuItem');
const Order = require('../../models/order');
const auth = require('../../middleware/auth');
const admin = require('../../middleware/admin');

// @route   GET admin/login
// @desc    Admin login page
// @access  Public
router.get('/login', (req, res) => {
  res.render('admin/login', {
    title: 'Admin Login',
    layout: false
  });
});

// @route   GET admin/dashboard
// @desc    Admin dashboard
// @access  Private (Admin only)
router.get('/dashboard', [auth, admin], async (req, res) => {
  try {
    // Get order statistics
    const stats = await Order.getStats();
    
    // Get recent orders
    const recentOrders = await Order.getRecent(5);
    
    // Get restaurant count
    const restaurantResult = await Restaurant.findAll();
    const restaurantCount = restaurantResult.length;
    
    res.render('admin/dashboard', {
      title: 'Admin Dashboard',
      stats,
      recentOrders,
      restaurantCount
    });
  } catch (error) {
    console.error('Error getting order statistics:', error);
    res.status(500).render('admin/dashboard', {
      title: 'Admin Dashboard',
      error: 'Failed to load dashboard data',
      stats: {
        total_orders: 0,
        pending_orders: 0,
        confirmed_orders: 0,
        preparing_orders: 0,
        ready_orders: 0,
        delivered_orders: 0,
        cancelled_orders: 0,
        total_revenue: 0
      },
      recentOrders: [],
      restaurantCount: 0
    });
  }
});

// @route   GET admin/restaurants
// @desc    Manage restaurants
// @access  Private (Admin only)
router.get('/restaurants', [auth, admin], async (req, res) => {
  try {
    const restaurants = await Restaurant.findAll();
    
    res.render('admin/restaurant', {
      title: 'Manage Restaurants',
      restaurants
    });
  } catch (error) {
    console.error(error);
    res.status(500).render('admin/restaurant', {
      title: 'Manage Restaurants',
      error: 'Failed to load restaurants'
    });
  }
});

// @route   GET admin/restaurants/:id/menu
// @desc    Manage restaurant menu
// @access  Private (Admin only)
router.get('/restaurants/:id/menu', [auth, admin], async (req, res) => {
  try {
    const restaurant = await Restaurant.findById(req.params.id);
    
    if (!restaurant) {
      return res.status(404).render('admin/menuItems', {
        title: 'Restaurant Menu',
        error: 'Restaurant not found'
      });
    }
    
    const menuItems = await MenuItem.findByRestaurant(req.params.id);
    
    res.render('admin/menuItems', {
      title: `${restaurant.name} - Menu`,
      restaurant,
      menuItems
    });
  } catch (error) {
    console.error(error);
    res.status(500).render('admin/menuItems', {
      title: 'Restaurant Menu',
      error: 'Failed to load menu items'
    });
  }
});

// @route   GET admin/orders
// @desc    Manage all orders
// @access  Private (Admin only)
router.get('/orders', [auth, admin], async (req, res) => {
  try {
    const restaurants = await Restaurant.findAll();
    
    // Default to the first restaurant if no restaurant_id is specified
    const restaurantId = req.query.restaurant_id || (restaurants.length > 0 ? restaurants[0].id : null);
    
    // If we have a restaurant, get its orders
    let orders = [];
    let selectedRestaurant = null;
    
    if (restaurantId) {
      selectedRestaurant = await Restaurant.findById(restaurantId);
      orders = await Order.findByRestaurant(restaurantId);
    }
    
    res.render('admin/orders', {
      title: 'Manage Orders',
      restaurants,
      selectedRestaurant,
      orders
    });
  } catch (error) {
    console.error(error);
    res.status(500).render('admin/orders', {
      title: 'Manage Orders',
      error: 'Failed to load orders'
    });
  }
});

module.exports = router;
