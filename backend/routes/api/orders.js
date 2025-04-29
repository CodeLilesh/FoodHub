const express = require('express');
const router = express.Router();
const Order = require('../../models/order');
const Restaurant = require('../../models/restaurant');
const MenuItem = require('../../models/menuItem');
const auth = require('../../middleware/auth');
const admin = require('../../middleware/admin');

// @route   GET api/orders
// @desc    Get user's orders
// @access  Private
router.get('/', auth, async (req, res) => {
  try {
    const orders = await Order.findByUser(req.user.id);
    res.json({ success: true, data: orders });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET api/orders/:id
// @desc    Get order by ID
// @access  Private
router.get('/:id', auth, async (req, res) => {
  try {
    const order = await Order.findById(req.params.id);
    
    if (!order) {
      return res.status(404).json({ success: false, message: 'Order not found' });
    }
    
    // Check if user owns this order or if user is admin
    if (order.user_id !== req.user.id && req.user.role !== 'admin') {
      return res.status(403).json({ success: false, message: 'Unauthorized' });
    }
    
    res.json({ success: true, data: order });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST api/orders
// @desc    Create an order
// @access  Private
router.post('/', auth, async (req, res) => {
  try {
    const { restaurant_id, items, delivery_address, payment_method, special_instructions } = req.body;
    
    // Validate required fields
    if (!restaurant_id || !items || !items.length || !delivery_address || !payment_method) {
      return res.status(400).json({ 
        success: false, 
        message: 'Please provide restaurant_id, items, delivery_address and payment_method' 
      });
    }
    
    // Check if restaurant exists
    const restaurant = await Restaurant.findById(restaurant_id);
    
    if (!restaurant) {
      return res.status(404).json({ success: false, message: 'Restaurant not found' });
    }
    
    // Verify and calculate price for each item
    let total_price = 0;
    const processedItems = [];
    
    for (const item of items) {
      const menuItem = await MenuItem.findById(item.menu_item_id);
      
      if (!menuItem) {
        return res.status(404).json({ 
          success: false, 
          message: `Menu item with ID ${item.menu_item_id} not found` 
        });
      }
      
      if (!menuItem.is_available) {
        return res.status(400).json({ 
          success: false, 
          message: `Menu item "${menuItem.name}" is currently unavailable` 
        });
      }
      
      const itemPrice = menuItem.price * item.quantity;
      total_price += itemPrice;
      
      processedItems.push({
        menu_item_id: menuItem.id,
        quantity: item.quantity,
        price_per_item: menuItem.price
      });
    }
    
    // Create order
    const order = await Order.create({
      user_id: req.user.id,
      restaurant_id,
      items: processedItems,
      total_price,
      delivery_address,
      payment_method,
      special_instructions
    });
    
    res.status(201).json({ success: true, data: order });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET api/orders/restaurant/:restaurant_id
// @desc    Get restaurant orders (admin only)
// @access  Private (Admin only)
router.get('/restaurant/:restaurant_id', [auth, admin], async (req, res) => {
  try {
    // Check if restaurant exists
    const restaurant = await Restaurant.findById(req.params.restaurant_id);
    
    if (!restaurant) {
      return res.status(404).json({ success: false, message: 'Restaurant not found' });
    }
    
    const orders = await Order.findByRestaurant(req.params.restaurant_id);
    res.json({ success: true, data: orders });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT api/orders/:id/status
// @desc    Update order status (admin only)
// @access  Private (Admin only)
router.put('/:id/status', [auth, admin], async (req, res) => {
  try {
    const { status } = req.body;
    
    // Validate status
    const validStatuses = ['pending', 'confirmed', 'preparing', 'ready', 'delivered', 'cancelled'];
    
    if (!status || !validStatuses.includes(status)) {
      return res.status(400).json({ 
        success: false, 
        message: `Status must be one of: ${validStatuses.join(', ')}` 
      });
    }
    
    // Check if order exists
    const order = await Order.findById(req.params.id);
    
    if (!order) {
      return res.status(404).json({ success: false, message: 'Order not found' });
    }
    
    // Update order status
    const updatedOrder = await Order.updateStatus(req.params.id, status);
    
    res.json({ success: true, data: updatedOrder });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET api/orders/stats
// @desc    Get order statistics (admin only)
// @access  Private (Admin only)
router.get('/stats', [auth, admin], async (req, res) => {
  try {
    const stats = await Order.getStats();
    res.json({ success: true, data: stats });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET api/orders/recent
// @desc    Get recent orders (admin only)
// @access  Private (Admin only)
router.get('/recent', [auth, admin], async (req, res) => {
  try {
    const limit = req.query.limit ? parseInt(req.query.limit) : 10;
    const recentOrders = await Order.getRecent(limit);
    res.json({ success: true, data: recentOrders });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
