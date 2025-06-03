# Squad2 COBOL API: Secure Banking Operations with JWT Authentication

A Spring Boot-based banking API that provides secure account management and transaction processing with JWT authentication. The API enables customer management, account operations, and transaction tracking while maintaining robust security through token-based authentication.

The project implements a modern banking system with RESTful endpoints, utilizing Spring Security for authentication and authorization. It features comprehensive account management capabilities, transaction processing, and customer data handling with proper validation and error handling. The API is fully documented using OpenAPI/Swagger specifications and includes extensive test coverage.

## Repository Structure
```
squad2-cobol/
├── src/                              # Source files root directory
│   ├── main/java/com/squad2/        # Main application code
│   │   └── Squad2_cobol/
│   │       ├── config/              # Configuration files for security, OpenAPI, and timezone
│   │       ├── exceptions/          # Global exception handling
│   │       ├── jwt/                 # JWT authentication implementation
│   │       ├── models/              # Data models and enums
│   │       ├── repositories/        # Data access layer
│   │       ├── services/           # Business logic implementation
│   │       └── web/                # Controllers and DTOs
│   └── test/                        # Test files
├── pom.xml                          # Maven project configuration
└── .mvn/                            # Maven wrapper configuration
```

## Usage Instructions
### Prerequisites
- Java JDK 17 or higher
- Maven 3.9.9 or higher (included via wrapper)
- H2 Database (embedded)

### Installation

1. Clone the repository:
```bash
git clone [repository-url]
cd squad2-cobol
```

2. Build the project using Maven wrapper:
```bash
# For Unix-based systems:
./mvnw clean install

# For Windows:
mvnw.cmd clean install
```

3. Run the application:
```bash
# For Unix-based systems:
./mvnw spring-boot:run

# For Windows:
mvnw.cmd spring-boot:run
```

### Quick Start

1. Create a new customer:
```bash
curl -X POST http://localhost:8080/api/v1/customers/create \
-H "Content-Type: application/json" \
-d '{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123",
  "birthdate": "1990-01-01"
}'
```

2. Login to get JWT token:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
-H "Content-Type: application/json" \
-d '{
  "email": "john@example.com",
  "password": "securePassword123"
}'
```

3. Create an account (requires JWT token):
```bash
curl -X POST http://localhost:8080/api/v1/accounts/create \
-H "Authorization: Bearer <your-jwt-token>" \
-H "Content-Type: application/json" \
-d '{
  "customerId": 1,
  "accountType": "CHECKING"
}'
```

### More Detailed Examples

#### Managing Transactions
```bash
# Create a new transaction
curl -X POST http://localhost:8080/api/v1/transactions/create \
-H "Authorization: Bearer <your-jwt-token>" \
-H "Content-Type: application/json" \
-d '{
  "accountId": 1,
  "amount": 100.00,
  "type": "DEPOSIT"
}'
```

### Troubleshooting

1. JWT Token Issues
- Problem: "JWT Token is invalid or expired"
  - Verify token hasn't expired (default 10 minutes)
  - Ensure "Bearer " prefix is included
  - Check if token is blacklisted

2. Database Connection
- Problem: "Cannot connect to database"
  - Verify H2 console is enabled in application.properties
  - Check database URL and credentials
  - Ensure H2 dependency is in classpath

3. Authorization Issues
- Problem: "Access denied"
  - Verify user has correct role
  - Check if token is valid
  - Ensure endpoint permissions are correctly configured

## Data Flow
The application follows a layered architecture for processing banking operations.

```ascii
Client Request → JWT Filter → Controller → Service → Repository → H2 Database
     ↑                           ↓
     └─────────── Response ─────┘
```

Component interactions:
- JWT Filter authenticates all secured endpoints
- Controllers validate input and manage HTTP responses
- Services implement business logic and transaction management
- Repositories handle data persistence
- DTOs manage data transfer between layers
- Global exception handler provides consistent error responses