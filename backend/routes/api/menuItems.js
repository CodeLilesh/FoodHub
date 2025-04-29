const express = require('express');
const router = express.Router();
const MenuItem = require('../../models/menuItem');
const Restaurant = require('../../models/restaurant');
const auth = require('../../middleware/auth');
const admin = require('../../middleware/admin');

// @route   GET api/menu-items/:id
// @desc    Get menu item by ID
// @access  Public
router.get('/:id', async (req, res) => {
  try {
    const menuItem = await MenuItem.findById(req.params.id);
    
    if (!menuItem) {
      return res.status(404).json({ success: false, message: 'Menu item not found' });
    }
    
    res.json({ success: true, data: menuItem });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST api/menu-items
// @desc    Create a menu item
// @access  Private (Admin only)
router.post('/', [auth, admin], async (req, res) => {
  try {
    const { 
      restaurant_id, 
      name, 
      description, 
      price, 
      image_url, 
      category,
      is_vegetarian,
      is_available
    } = req.body;
    
    // Validate required fields
    if (!restaurant_id || !name || !price) {
      return res.status(400).json({ 
        success: false, 
        message: 'Please provide restaurant_id, name and price' 
      });
    }
    
    // Check if restaurant exists
    const restaurant = await Restaurant.findById(restaurant_id);
    
    if (!restaurant) {
      return res.status(404).json({ success: false, message: 'Restaurant not found' });
    }
    
    const menuItem = await MenuItem.create({
      restaurant_id,
      name,
      description,
      price,
      image_url,
      category,
      is_vegetarian,
      is_available
    });
    
    res.status(201).json({ success: true, data: menuItem });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT api/menu-items/:id
// @desc    Update a menu item
// @access  Private (Admin only)
router.put('/:id', [auth, admin], async (req, res) => {
  try {
    const { 
      name, 
      description, 
      price, 
      image_url, 
      category,
      is_vegetarian,
      is_available
    } = req.body;
    
    // Check if menu item exists
    const menuItem = await MenuItem.findById(req.params.id);
    
    if (!menuItem) {
      return res.status(404).json({ success: false, message: 'Menu item not found' });
    }
    
    // Update menu item
    const updatedMenuItem = await MenuItem.update(req.params.id, {
      name: name || menuItem.name,
      description: description !== undefined ? description : menuItem.description,
      price: price || menuItem.price,
      image_url: image_url !== undefined ? image_url : menuItem.image_url,
      category: category || menuItem.category,
      is_vegetarian: is_vegetarian !== undefined ? is_vegetarian : menuItem.is_vegetarian,
      is_available: is_available !== undefined ? is_available : menuItem.is_available
    });
    
    res.json({ success: true, data: updatedMenuItem });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   DELETE api/menu-items/:id
// @desc    Delete a menu item
// @access  Private (Admin only)
router.delete('/:id', [auth, admin], async (req, res) => {
  try {
    // Check if menu item exists
    const menuItem = await MenuItem.findById(req.params.id);
    
    if (!menuItem) {
      return res.status(404).json({ success: false, message: 'Menu item not found' });
    }
    
    // Delete menu item
    await MenuItem.delete(req.params.id);
    
    res.json({ success: true, message: 'Menu item removed' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT api/menu-items/:id/toggle-availability
// @desc    Toggle menu item availability
// @access  Private (Admin only)
router.put('/:id/toggle-availability', [auth, admin], async (req, res) => {
  try {
    // Check if menu item exists
    const menuItem = await MenuItem.findById(req.params.id);
    
    if (!menuItem) {
      return res.status(404).json({ success: false, message: 'Menu item not found' });
    }
    
    // Toggle availability
    const updatedMenuItem = await MenuItem.toggleAvailability(req.params.id);
    
    res.json({ success: true, data: updatedMenuItem });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
