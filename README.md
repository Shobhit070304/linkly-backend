# Linkly - URL Shortener Backend 🔗

Modern URL shortener service built with Spring Boot, MongoDB, and Redis.

## Features ✨

- 🔗 URL Shortening with custom aliases
- 📱 QR Code generation
- 📊 Click tracking
- ⏰ Expiry dates
- 🔢 Click limits (one-time links)
- 🌐 Metadata extraction (title, favicon, description)
- ⚡ Redis caching for fast lookups
- 🔒 Firebase Authentication
- 🛡️ Rate limiting (100 req/15min)
- 🔐 CORS protection

## Tech Stack 🛠️

- **Framework:** Spring Boot 3.2.x
- **Database:** MongoDB
- **Cache:** Redis (Upstash)
- **Auth:** Firebase Admin SDK
- **Build:** Maven
- **Java:** 17+

## API Endpoints 📡

### Authentication
- `POST /api/user/login` - User login with Firebase token

### URL Operations
- `POST /api/url/shorten` - Create short URL
- `POST /api/url/original` - Get original URL
- `GET /api/url/me` - Get user's URLs
- `POST /api/url/delete` - Delete URL
- `GET /{shortUrl}` - Redirect to original URL

### System
- `GET /api/health` - Health check

## Setup Instructions 🚀

### Prerequisites
- Java 17 or higher
- Maven
- MongoDB Atlas account
- Upstash Redis account
- Firebase project

### Environment Variables

Create `.env` file or set environment variables:
```env
# MongoDB
MONGO_URI=mongodb+srv://username:password@cluster.mongodb.net/linkly

# Redis (Upstash)
REDIS_HOST=your-redis-host.upstash.io
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password

# Application
PORT=8000
BACKEND_URL=http://localhost:8000
```

### Firebase Setup

1. Download Firebase service account JSON
2. Place it at: `src/main/resources/firebase-service-account.json`

### Run Application
```bash
# Install dependencies
./mvnw clean install

# Run application
./mvnw spring-boot:run
```

Application will start at `http://localhost:8000`

### Run Tests
```bash
./mvnw test
```

## Usage Examples 💡

### Shorten URL
```bash
curl -X POST http://localhost:8000/api/url/shorten \
  -H "Authorization: Bearer YOUR_FIREBASE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "longUrl": "https://github.com",
    "customShort": "my-github",
    "maxClicks": 100,
    "expiresAt": "2024-12-31T23:59:59"
  }'
```

**Response:**
```json
{
  "status": true,
  "shortUrl": "http://localhost:8000/my-github",
  "qrCode": "data:image/png;base64,...",
  "message": "URL shortened successfully"
}
```

### Redirect

Simply visit in browser:
```
http://localhost:8000/my-github
```

Automatically redirects to `https://github.com` ✅

## Project Structure 📁
```
src/main/java/com/linkly/backend/
├── config/          # Configuration classes
├── controllers/     # REST controllers
├── dto/            # Data Transfer Objects
├── filters/        # Authentication filters
├── models/         # MongoDB entities
├── repositories/   # Database repositories
├── services/       # Business logic
└── utils/          # Helper utilities
```

## Features in Detail 🔍

### QR Code Generation
Every shortened URL gets a QR code automatically.

### Metadata Extraction
Automatically fetches title, description, and favicon from the target URL.

### Click Tracking
Track how many times each short URL has been clicked.

### Expiry Options
- **Date-based:** URL expires after a specific date
- **Click-based:** URL expires after N clicks
- **One-time links:** maxClicks = 1

### Security
- Firebase authentication
- Rate limiting (100 requests per 15 minutes)
- CORS protection
- Input validation
- Reserved keyword protection

## Contributing 🤝

Contributions are welcome! Please feel free to submit a Pull Request.

## License 📄

MIT License

## Author ✍️

Built with ❤️ by [Your Name]

---

**Happy URL Shortening! 🎉**