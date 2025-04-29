const express = require('express');
const router = express.Router();
const Restaurant = require('../../models/restaurant');
const MenuItem = require('../../models/menuItem');
const auth = require('../../middleware/auth');
const admin = require('../../middleware/admin');

// @route   GET api/restaurants
// @desc    Get all restaurants
// @access  Public
router.get('/', async (req, res) => {
  try {
    let restaurants;
    
    // Check if category query param exists
    if (req.query.category) {
      restaurants = await Restaurant.findByCategory(req.query.category);
    } 
    // Check if search query param exists
    else if (req.query.search) {
      restaurants = await Restaurant.search(req.query.search);
    } 
    // Otherwise, get all restaurants
    else {
      restaurants = await Restaurant.findAll();
    }
    
    res.json({ success: true, data: restaurants });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET api/restaurants/:id
// @desc    Get restaurant by ID
// @access  Public
router.get('/:id', async (req, res) => {
  try {
    const restaurant = await Restaurant.findById(req.params.id);
    
    if (!restaurant) {
      return res.status(404).json({ success: false, message: 'Restaurant not found' });
    }
    
    res.json({ success: true, data: restaurant });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET api/restaurants/:id/menu
// @desc    Get restaurant menu
// @access  Public
router.get('/:id/menu', async (req, res) => {
  try {
    const restaurant = await Restaurant.findById(req.params.id);
    
    if (!restaurant) {
      return res.status(404).json({ success: false, message: 'Restaurant not found' });
    }
    
    let menuItems;
    
    // Check if category query param exists
    if (req.query.category) {
      menuItems = await MenuItem.findByCategory(req.params.id, req.query.category);
    } 
    // Check if search query param exists
    else if (req.query.search) {
      menuItems = await MenuItem.search(req.params.id, req.query.search);
    } 
    // Otherwise, get all menu items
    else {
      menuItems = await MenuItem.findByRestaurant(req.params.id);
    }
    
    res.json({ success: true, data: menuItems });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST api/restaurants
// @desc    Create a restaurant
// @access  Private (Admin only)
router.post('/', [auth, admin], async (req, res) => {
  try {
    const { name, description, address, phone, image_url, opening_hours, category } = req.body;
    
    // Validate required fields
    if (!name || !address || !phone || !category) {
      return res.status(400).json({ 
        success: false, 
        message: 'Please provide name, address, phone and category' 
      });
    }
    
    const restaurant = await Restaurant.create({
      name,
      description,
      address,
      phone,
      image_url,
      opening_hours,
      category
    });
    
    res.status(201).json({ success: true, data: restaurant });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT api/restaurants/:id
// @desc    Update a restaurant
// @access  Private (Admin only)
router.put('/:id', [auth, admin], async (req, res) => {
  try {
    const { name, description, address, phone, image_url, opening_hours, category } = req.body;
    
    // Check if restaurant exists
    const restaurant = await Restaurant.findById(req.params.id);
    
    if (!restaurant) {
      return res.status(404).json({ success: false, message: 'Restaurant not found' });
    }
    
    // Update restaurant
    const updatedRestaurant = await Restaurant.update(req.params.id, {
      name: name || restaurant.name,
      description: description || restaurant.description,
      address: address || restaurant.address,
      phone: phone || restaurant.phone,
      image_url: image_url || restaurant.image_url,
      opening_hours: opening_hours || restaurant.opening_hours,
      category: category || restaurant.category
    });
    
    res.json({ success: true, data: updatedRestaurant });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   DELETE api/restaurants/:id
// @desc    Delete a restaurant
// @access  Private (Admin only)
router.delete('/:id', [auth, admin], async (req, res) => {
  try {
    // Check if restaurant exists
    const restaurant = await Restaurant.findById(req.params.id);
    
    if (!restaurant) {
      return res.status(404).json({ success: false, message: 'Restaurant not found' });
    }
    
    // Delete restaurant (will also delete related menu items in the model)
    await Restaurant.delete(req.params.id);
    
    res.json({ success: true, message: 'Restaurant removed' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
