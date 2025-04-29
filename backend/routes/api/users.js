const express = require('express');
const router = express.Router();
const User = require('../../models/user');
const auth = require('../../middleware/auth');

// @route   GET api/users/me
// @desc    Get current user
// @access  Private
router.get('/me', auth, async (req, res) => {
  try {
    const user = await User.findById(req.user.id);
    
    if (!user) {
      return res.status(404).json({ success: false, message: 'User not found' });
    }
    
    res.json({ success: true, data: user });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT api/users/me
// @desc    Update current user
// @access  Private
router.put('/me', auth, async (req, res) => {
  try {
    const { name, email, phone, address } = req.body;
    
    // Build update object
    const updateData = {};
    if (name) updateData.name = name;
    if (email) updateData.email = email;
    if (phone) updateData.phone = phone;
    if (address) updateData.address = address;
    
    const updatedUser = await User.update(req.user.id, updateData);
    
    res.json({ success: true, data: updatedUser });
  } catch (error) {
    console.error(error);
    
    // Check for duplicate email
    if (error.code === '23505') {
      return res.status(400).json({ success: false, message: 'Email already in use' });
    }
    
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
