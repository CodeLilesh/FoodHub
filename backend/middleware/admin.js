// Middleware to check if user is admin
module.exports = (req, res, next) => {
  // Check if user exists and is an admin
  if (!req.user || req.user.role !== 'admin') {
    return res.status(403).json({ success: false, message: 'Unauthorized: Admin access required' });
  }
  
  next();
};
