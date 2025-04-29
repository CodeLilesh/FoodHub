# Food Ordering Platform

A comprehensive food ordering platform with a mobile app for customers and a web-based admin panel for restaurant management.

## System Architecture

The platform consists of two main components:

1. **Mobile App (Android/Kotlin)** - For customers to browse restaurants, place and track orders
2. **Admin Panel (Node.js/Express)** - Web interface for restaurant owners to manage menus and orders

Both systems share a common PostgreSQL database for data consistency.

## Tech Stack

### Mobile App (Customer-facing)
- **Language**: Kotlin
- **Platform**: Android
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Hilt
- **Database**: Room (local caching)
- **Networking**: Retrofit, OkHttp
- **Async**: Kotlin Coroutines, Flows
- **UI Components**: Material Components, RecyclerView
- **Real-time Updates**: WebSockets (OkHttp)

### Admin Panel (Restaurant management)
- **Backend**: Node.js with Express
- **View Engine**: EJS templates
- **Database**: PostgreSQL
- **Authentication**: JWT, bcrypt
- **API**: RESTful endpoints

## Setup Instructions

### Prerequisites
- Android Studio (latest version) for mobile app development
- Node.js (v14+) and npm for backend development
- PostgreSQL database server
- Git

### Database Setup
1. Install PostgreSQL on your system if not already installed
2. Create a new database:
   ```
   createdb food_ordering_app
   ```
3. Run the initialization script to set up tables and seed initial data:
   ```
   psql -d food_ordering_app -f backend/database/schema.sql
   ```

### Backend Setup (Admin Panel)
1. Navigate to the backend directory:
   ```
   cd backend
   ```
2. Install dependencies:
   ```
   npm install
   ```
3. Create a `.env` file in the backend directory with the following variables:
   ```
   PORT=5000
   DATABASE_URL=postgresql://username:password@localhost:5432/food_ordering_app
   JWT_SECRET=your_jwt_secret_key
   SESSION_SECRET=your_session_secret_key
   ```
4. Start the server:
   ```
   npm run dev
   ```
5. The admin panel will be available at: http://localhost:5000
   - Default admin credentials:
     - Email: admin@foodapp.com
     - Password: admin123

### Mobile App Setup
1. Open Android Studio
2. Select "Open an Existing Project" and navigate to the FoodOrderingApp directory
3. Wait for the Gradle sync to complete
4. Update the API endpoint in `app/src/main/java/com/example/foodorderingapp/api/RetrofitInstance.kt` to point to your backend server URL
5. Update the WebSocket URL in `app/src/main/java/com/example/foodorderingapp/data/socket/OrderTrackingSocket.kt` to point to your backend WebSocket endpoint
6. Run the app on an emulator or physical device:
   - Select a device from the dropdown menu in the toolbar
   - Click the "Run" button (green triangle)

## Project Structure

### Mobile App
```
FoodOrderingApp/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/example/foodorderingapp/
│           │   ├── api/                  # Retrofit API service interfaces
│           │   ├── data/
│           │   │   ├── db/               # Room database and DAOs
│           │   │   ├── models/           # Data classes for entities
│           │   │   ├── repository/       # Repositories for data handling
│           │   │   └── socket/           # WebSocket implementation
│           │   ├── di/                   # Dependency injection modules
│           │   ├── ui/
│           │   │   ├── adapters/         # RecyclerView adapters
│           │   │   ├── auth/             # Authentication screens
│           │   │   ├── cart/             # Cart management screens
│           │   │   ├── checkout/         # Order checkout flow
│           │   │   ├── home/             # Home screen with restaurants
│           │   │   ├── profile/          # User profile screens
│           │   │   ├── restaurant/       # Restaurant details
│           │   │   └── tracking/         # Order tracking screen
│           │   └── util/                 # Utility classes and extensions
│           └── res/                      # Resources (layouts, drawables, etc.)
└── build.gradle                          # Project and app level build files
```

### Backend
```
backend/
├── config/                  # Configuration files
├── database/                # Database scripts and initialization
├── middleware/              # Express middleware
├── models/                  # Data models
├── public/                  # Static assets
├── routes/                  # API routes
│   ├── admin/               # Admin panel routes
│   └── api/                 # API endpoints
├── views/                   # EJS templates
└── server.js                # Main server file
```

## Key Features

### Mobile App
- User authentication (login/register)
- Restaurant browsing with filters and search
- Menu browsing with categories
- Cart management
- Checkout process
- Order history
- Real-time order tracking with WebSockets

### Admin Panel
- Restaurant management
- Menu and item management
- Order processing
- Sales analytics
- User management

## Running in Production

For a production environment:

1. Set up a production PostgreSQL database
2. Configure environment variables for production settings
3. For the backend:
   ```
   npm run start
   ```
4. For the mobile app:
   - Generate a signed APK in Android Studio
   - Build → Generate Signed Bundle/APK

## Troubleshooting

If you encounter connection issues between the mobile app and backend:
1. Ensure the backend server is running
2. Check that the API URL in the mobile app is correctly pointing to your server
3. For emulator testing, use "10.0.2.2" instead of "localhost" for the server URL
4. For WebSocket connections, ensure port 5000 is accessible

## Setup Script

A setup script (`setup.sh`) is included to automate the installation process. Run:

```
chmod +x setup.sh
./setup.sh
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.