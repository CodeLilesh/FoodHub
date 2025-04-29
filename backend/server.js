const express = require('express');
const path = require('path');
const morgan = require('morgan');
const ejs = require('ejs');
const expressLayouts = require('express-ejs-layouts');
const cors = require('cors');

// Initialize express app
const app = express();

// Basic middleware
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(morgan('dev'));
app.use(cors());

// Set up EJS view engine
app.use(expressLayouts);
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));
app.set('layout', 'layouts/admin');

// Static assets
app.use(express.static(path.join(__dirname, 'public')));

// API Routes
app.use('/api/auth', require('./routes/api/auth'));
app.use('/api/users', require('./routes/api/users'));
app.use('/api/restaurants', require('./routes/api/restaurants'));
app.use('/api/menu-items', require('./routes/api/menuItems'));
app.use('/api/orders', require('./routes/api/orders'));

// Admin routes
app.use('/admin', require('./routes/admin/index'));

// Default route
app.get('/', (req, res) => {
  res.redirect('/admin/login');
});

// Error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  
  const statusCode = err.statusCode || 500;
  res.status(statusCode).json({
    success: false,
    error: err.message || 'Server Error',
    stack: process.env.NODE_ENV === 'production' ? undefined : err.stack
  });
});

// Start server
const PORT = process.env.PORT || 5000;
app.listen(PORT, '0.0.0.0', () => {
  console.log(`Server running on port ${PORT}`);
});

module.exports = app;
