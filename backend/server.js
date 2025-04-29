const express = require('express');
const path = require('path');
const morgan = require('morgan');
const cors = require('cors');
const http = require('http');
const WebSocket = require('ws');
const { Pool } = require('pg');
require('dotenv').config();

// Initialize Express app
const app = express();
const PORT = process.env.PORT || 5000;

// Database connection
const pool = new Pool({
  connectionString: process.env.DATABASE_URL || 'postgresql://localhost:5432/food_ordering_app'
});

// Test database connection
pool.query('SELECT NOW()', (err, res) => {
  if (err) {
    console.error('Error connecting to PostgreSQL database:', err);
  } else {
    console.log('Connected to PostgreSQL database');
  }
});

// Middleware
app.use(morgan('dev'));
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Static files
app.use(express.static(path.join(__dirname, 'public')));

// View engine
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

// Routes
app.get('/', (req, res) => {
  res.redirect('/admin/login');
});

// Admin routes
app.get('/admin/login', (req, res) => {
  res.render('admin/login');
});

app.get('/admin/dashboard', (req, res) => {
  res.render('admin/dashboard');
});

// API routes
app.get('/api/health', (req, res) => {
  res.status(200).json({ status: 'ok' });
});

// API route for admin
app.use('/api/admin', require('./routes/api/admin'));

// WebSocket server for real-time order updates
const server = http.createServer(app);
const wss = new WebSocket.Server({ server, path: '/ws' });

wss.on('connection', (ws, req) => {
  console.log('Client connected to WebSocket');
  
  // Parse URL to extract order ID
  const url = new URL(req.url, 'http://localhost');
  const pathSegments = url.pathname.split('/');
  
  // Check if this is an order tracking connection
  if (pathSegments.includes('orders') && pathSegments.includes('track')) {
    const orderId = pathSegments[pathSegments.indexOf('orders') + 1];
    console.log(`WebSocket tracking connection for order: ${orderId}`);
    
    // Send initial status
    ws.send(JSON.stringify({
      type: 'status_update',
      status: 'received',
      estimatedMinutes: 30,
      message: 'Order received and being processed'
    }));
    
    // For demo purposes, simulate status updates
    let statusIndex = 0;
    const statuses = ['received', 'preparing', 'ready', 'picked_up', 'in_transit', 'delivered'];
    const messages = [
      'Order received and being processed',
      'Restaurant is preparing your food',
      'Your order is ready for pickup',
      'Driver has picked up your order',
      'Your order is on the way',
      'Order delivered successfully!'
    ];
    
    // Send status update every 10 seconds (for demo)
    const statusInterval = setInterval(() => {
      statusIndex++;
      
      if (statusIndex < statuses.length) {
        ws.send(JSON.stringify({
          type: 'status_update',
          status: statuses[statusIndex],
          estimatedMinutes: 30 - (statusIndex * 5),
          message: messages[statusIndex]
        }));
      } else {
        clearInterval(statusInterval);
      }
    }, 10000);
    
    // Clear interval on disconnect
    ws.on('close', () => {
      console.log(`WebSocket connection closed for order: ${orderId}`);
      clearInterval(statusInterval);
    });
  }
  
  // Handle messages from clients
  ws.on('message', (message) => {
    console.log(`Received message: ${message}`);
  });
});

// Start server
server.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});

// Error handling
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).send('Something broke!');
});

module.exports = { app, server };