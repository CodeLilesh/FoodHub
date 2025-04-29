const express = require('express');
const cors = require('cors');
const path = require('path');
const expressLayouts = require('express-ejs-layouts');
const morgan = require('morgan');
require('dotenv').config();

// Initialize express
const app = express();

// Database connection
require('./config/db');

// Middleware
app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization', 'x-auth-token']
}));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(morgan('dev'));

// Static files
app.use(express.static(path.join(__dirname, 'public')));

// View engine
app.use(expressLayouts);
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));
app.set('layout', 'layouts/main');

// Routes
app.use('/api/auth', require('./routes/api/auth'));
app.use('/api/users', require('./routes/api/users'));
app.use('/api/restaurants', require('./routes/api/restaurants'));
app.use('/api/menu-items', require('./routes/api/menuItems'));
app.use('/api/orders', require('./routes/api/orders'));
app.use('/admin', require('./routes/admin'));

// Default route
app.get('/', (req, res) => {
  res.redirect('/admin/login');
});

// Error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ success: false, error: err.message });
});

// Set port and start server
const PORT = process.env.PORT || 5000;
app.listen(PORT, '0.0.0.0', () => {
  console.log(`Server running on port ${PORT}`);
});