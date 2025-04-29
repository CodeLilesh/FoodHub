const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const User = require('../../models/user');
const auth = require('../../middleware/auth');

// JWT Secret
const JWT_SECRET = process.env.JWT_SECRET || 'foodappjwtsecret';

// @route   POST api/auth/register
// @desc    Register a user
// @access  Public
router.post('/register', async (req, res) => {
  try {
    const { name, email, password, phone, address } = req.body;
    
    // Validate required fields
    if (!name || !email || !password) {
      return res.status(400).json({ success: false, message: 'Please provide name, email and password' });
    }
    
    // Check if user already exists
    const existingUser = await User.findByEmail(email);
    
    if (existingUser) {
      return res.status(400).json({ success: false, message: 'User already exists' });
    }
    
    // Create user
    const user = await User.create({
      name,
      email,
      password,
      phone,
      address
    });
    
    // Create and sign JWT token
    const payload = {
      user: {
        id: user.id,
        role: user.role
      }
    };
    
    jwt.sign(
      payload,
      JWT_SECRET,
      { expiresIn: '7d' },
      (err, token) => {
        if (err) throw err;
        res.status(201).json({
          success: true,
          token,
          user: {
            id: user.id,
            name: user.name,
            email: user.email,
            role: user.role
          }
        });
      }
    );
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST api/auth/login
// @desc    Authenticate user & get token
// @access  Public
router.post('/login', async (req, res) => {
  try {
    const { email, password } = req.body;
    
    // Validate required fields
    if (!email || !password) {
      return res.status(400).json({ success: false, message: 'Please provide email and password' });
    }
    
    // Check if user exists
    const user = await User.findByEmail(email);
    
    if (!user) {
      return res.status(400).json({ success: false, message: 'Invalid credentials' });
    }
    
    // Check password
    const isMatch = await bcrypt.compare(password, user.password);
    
    if (!isMatch) {
      return res.status(400).json({ success: false, message: 'Invalid credentials' });
    }
    
    // Create and sign JWT token
    const payload = {
      user: {
        id: user.id,
        role: user.role
      }
    };
    
    jwt.sign(
      payload,
      JWT_SECRET,
      { expiresIn: '7d' },
      (err, token) => {
        if (err) throw err;
        res.json({
          success: true,
          token,
          user: {
            id: user.id,
            name: user.name,
            email: user.email,
            role: user.role
          }
        });
      }
    );
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET api/auth/user
// @desc    Get user data
// @access  Private
router.get('/user', auth, async (req, res) => {
  try {
    const user = await User.findById(req.user.id);
    
    if (!user) {
      return res.status(404).json({ success: false, message: 'User not found' });
    }
    
    res.json({
      success: true,
      user: {
        id: user.id,
        name: user.name,
        email: user.email,
        phone: user.phone,
        address: user.address,
        role: user.role,
        created_at: user.created_at
      }
    });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST api/auth/admin/login
// @desc    Admin login
// @access  Public
router.post('/admin/login', async (req, res) => {
  try {
    const { email, password } = req.body;
    
    // Validate required fields
    if (!email || !password) {
      return res.status(400).json({ success: false, message: 'Please provide email and password' });
    }
    
    // Check if user exists
    const user = await User.findByEmail(email);
    console.log('Login attempt for:', email);
    
    if (!user) {
      console.log('User not found');
      return res.status(400).json({ success: false, message: 'Invalid credentials' });
    }
    
    console.log('User found, role:', user.role);
    
    // Check if user is admin
    if (user.role !== 'admin') {
      console.log('Not an admin user');
      return res.status(403).json({ success: false, message: 'Unauthorized: Admin access required' });
    }
    
    // Check password
    console.log('Checking password...');
    const isMatch = await bcrypt.compare(password, user.password);
    console.log('Password match:', isMatch);
    
    if (!isMatch) {
      return res.status(400).json({ success: false, message: 'Invalid credentials' });
    }
    
    // Create and sign JWT token
    const payload = {
      user: {
        id: user.id,
        role: user.role
      }
    };
    
    jwt.sign(
      payload,
      JWT_SECRET,
      { expiresIn: '1d' },
      (err, token) => {
        if (err) throw err;
        res.json({
          success: true,
          token,
          user: {
            id: user.id,
            name: user.name,
            email: user.email,
            role: user.role
          }
        });
      }
    );
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
