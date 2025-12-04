# 📋 AuditService

A Spring Boot microservice application for managing and querying audit logs with multi-tenant support and flexible validation.

## 🎯 Overview

AuditService is a RESTful API that provides comprehensive audit logging capabilities for the complyVault system. It enables tracking of events across different networks (email, Slack, etc.) with support for multiple tenants, advanced filtering, and pagination.

## ✨ Features

- **👥 Multi-Tenant Support**: Isolate audit logs by tenant ID with tenant-enforced queries
- **📊 Event Tracking**: Log and retrieve events with types including NOTIFICATION_SENT, VALIDATION_SUCCESS, ID_GENERATION_SUCCESS, and 20+ more event types
- **🔍 Advanced Querying**: Filter audit logs by:
  - Tenant ID
  - Message ID
  - Event Type
  - Timestamp ranges
  - Individual Audit ID
- **📄 Pagination Support**: Query audit logs with customizable page size and sorting (descending by timestamp)
- **🔗 Validation Chain Pattern**: Extensible, rule-based validation using chain of responsibility design pattern
- **⚠️ Comprehensive Error Handling**: Global exception handler with structured error responses
- **🗄️ MongoDB Integration**: Persistent storage with MongoDB
- **📝 Logging**: SLF4J logging throughout the application

## 🛠️ Technology Stack

- **☕ Java Version**: 21
- **🍃 Framework**: Spring Boot 3.5.5
- **🔨 Build Tool**: Maven
- **🗄️ Database**: MongoDB
- **📦 Additional Libraries**:
  - Lombok (for reducing boilerplate code)
  - Spring Data MongoDB (for database operations)
  - TestContainers (for containerized testing)
  - JUnit 5 (for testing)

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/smarsh/auditService/
│   │   ├── AuditServiceApplication.java          # 🚀 Main entry point
│   │   ├── controller/
│   │   │   └── AuditController.java              # 🎮 REST endpoints
│   │   ├── service/
│   │   │   ├── AuditService.java                 # 💼 Service interface
│   │   │   └── AuditServiceImpl.java              # 💼 Service implementation
│   │   ├── model/
│   │   │   └── AuditLog.java                     # 📊 MongoDB document model
│   │   ├── repository/
│   │   │   └── AuditRepository.java              # 🗄️ MongoDB repository interface
│   │   ├── dto/
│   │   │   ├── AuditLogRequest.java              # 📨 Request DTO
│   │   │   └── ErrorResponse.java                # ❌ Error response DTO
│   │   ├── validator/
│   │   │   ├── ValidationRule.java               # ✅ Validation rule interface
│   │   │   ├── AbstractValidationRule.java       # ✅ Base validation rule
│   │   │   ├── AuditValidator.java               # ✅ Main validator
│   │   │   ├── ValidationChainBuilder.java       # 🔗 Builds validation chain
│   │   │   ├── NotNullValidationRule.java        # ✅ Null checks
│   │   │   ├── MessageIdValidationRule.java      # ✅ Message ID validation
│   │   │   ├── NetworkValidationRule.java        # ✅ Network validation
│   │   │   ├── EventTypeValidationRule.java      # ✅ Event type validation
│   │   │   ├── ServiceValidationRule.java        # ✅ Service validation
│   │   │   └── InstantTimestampValidationRule.java # ✅ Timestamp validation
│   │   └── exception/
│   │       ├── AuditException.java               # ⚠️ Custom audit exception
│   │       ├── AuditNotFoundException.java       # 🔍 Not found exception
│   │       ├── ValidationException.java          # ❌ Validation exception
│   │       └── GlobalExceptionHandler.java       # 🛡️ Global exception handler
│   └── resources/
│       └── application.properties                 # ⚙️ Configuration
└── test/
    ├── java/com/smarsh/auditService/
    │   ├── AuditServiceApplicationTests.java     # 🧪 Application tests
    │   ├── config/
    │   │   └── TestMongoConfig.java              # 🧪 Test MongoDB config
    │   ├── controller/
    │   │   └── AuditControllerTest.java          # 🧪 Controller tests
    │   ├── service/
    │   │   └── AuditServiceImplTest.java         # 🧪 Service tests
    │   ├── validator/
    │   │   ├── AuditValidatorTest.java           # 🧪 Validator tests
    │   │   └── ValidationRuleTests.java          # 🧪 Validation rule tests
    │   ├── exception/
    │   │   └── GlobalExceptionHandlerTest.java   # 🧪 Exception handler tests
    │   └── integration/
    │       └── AuditIntegrationTest.java         # 🧪 Integration tests
    └── resources/
        └── application.properties                 # ⚙️ Test configuration
```

## 📋 Prerequisites

- ☕ Java 21 or higher
- 🔨 Maven 3.6+
- 🗄️ MongoDB 4.4+ (running on `localhost:27017`)

## 🚀 Getting Started

### 📦 Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/avnesh-smarsh/AuditService.git
   cd AuditService
   ```

2. **Ensure MongoDB is running**:
   ```bash
   # On Windows (if using Docker)
   docker run -d -p 27017:27017 --name mongodb mongo
   
   # Or use your local MongoDB installation
   ```

3. **Build the project**:
   ```bash
   mvn clean install
   ```

4. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

   The service will start on `http://localhost:8085`

## ⚙️ Configuration

The application is configured via `src/main/resources/application.properties`:

```properties
spring.application.name=auditService
server.port=8085
spring.jackson.serialization.write-dates-as-timestamps=false
spring.data.mongodb.uri=mongodb://localhost:27017/complyVault
spring.data.mongodb.database=complyVault
```

**Key Configuration**:
- **🌐 Server Port**: 8085
- **🗄️ MongoDB URI**: `mongodb://localhost:27017/complyVault`
- **📚 Database**: `complyVault`
- **📅 JSON Timestamp Format**: ISO 8601 (not Unix timestamps)

## 🔌 API Endpoints

### ➕ Create Audit Log
**POST** `/api/audit/{tenantId}`

Creates a new audit log entry for a specific tenant.

**Request Body**:
```json
{
  "messageId": "msg-123",
  "network": "email",
  "eventType": "INGESTED",
  "service": "IngestionAndValidationApp",
  "timestamp": "2025-12-04T10:30:00Z",
  "details": {
    "key": "value"
  }
}
```

**Response**: 201 Created with the saved AuditLog object

---

### 📥 Get Audit Logs by Tenant
**GET** `/api/audit/{tenantId}`

Retrieves all audit logs for a tenant with optional pagination.

**Query Parameters**:
- `page` (optional, default: 0): Page number (0 = no pagination, returns all)
- `size` (optional, default: 50): Page size when pagination is enabled

**Response**: List of AuditLog objects (sorted by timestamp descending when paginated)

---

### 🔍 Get Audit Logs by Message ID
**GET** `/api/audit/{tenantId}/message/{messageId}`

Retrieves all audit logs associated with a specific message ID within a tenant.

**Response**: List of AuditLog objects

---

### 📊 Get Audit Logs by Event Type
**GET** `/api/audit/{tenantId}/event-type/{eventType}`

Retrieves all audit logs of a specific event type within a tenant.

**Supported Event Types**: 
- From ComplianceService: `NOTIFICATION_SENT`, `NOTIFICATION_FAILED`, `POLICY_VIOLATION`, `POLICY_PASSED`, `POLICY_EVALUATION_ERROR`, `COMPLIANCE_PROCESSING_COMPLETE`, `COMPLIANCE_PROCESSING_FAILED`, `FLAG_STORED_TO_DB`, `FLAG_STORE_FAILED`
- From CanonicalService: `ID_GENERATION_SUCCESS`, `ID_GENERATION_FAILED`, `VALIDATION_SUCCESS`, `VALIDATION_FAILED`, `DUPLICATE_DETECTED`, `UNIQUE_MESSAGE`, `DUPLICATE_CHECK_FAILED`, `CANONICAL_PROCESSING_SUCCESS`, `CANONICAL_PROCESSING_FAILED`, `ES_STORAGE_SUCCESS`, `ES_STORAGE_FAILED`, `RAW_STORAGE_SUCCESS`, `RAW_STORAGE_FAILED`, `PUBLISH_TO_KAFKA_SUCCESS`, `PUBLISH_TO_KAFKA_FAILED`

**Response**: List of AuditLog objects

---

### 🎯 Get Specific Audit Log
**GET** `/api/audit/{tenantId}/{auditId}`

Retrieves a specific audit log by ID within a tenant.

**Response**: Single AuditLog object

**Error**: 404 Not Found if audit log doesn't exist

---

## 📊 Data Model

### AuditLog

```java
{
  "auditId": "string",           // 🆔 UUID (generated by MongoDB)
  "tenantId": "string",          // 👥 Tenant identifier
  "messageId": "string",         // 📨 Message identifier (can be null)
  "network": "string",           // 🌐 Network type (e.g., "email", "slack")
  "eventType": "string",         // 📢 Event type (see supported event types)
  "service": "string",           // 🔧 Source service name
  "timestamp": "ISO 8601",       // ⏰ Event timestamp (Instant)
  "details": {                   // 📝 Arbitrary metadata key-value pairs
    "key": "value"
  }
}
```

### AuditLogRequest

Input DTO for creating audit logs (same fields as AuditLog, except `auditId` which is auto-generated).

### ErrorResponse

```java
{
  "status": "HTTP_STATUS",       // ❌ HTTP status code
  "message": "string",           // ❌ Error message
  "path": "string"               // 🔗 Request path
}
```

## ✅ Validation

The application uses a chain of responsibility pattern for request validation. Each validation rule is independent and extensible:

- **✅ NotNullValidationRule**: Ensures request is not null
- **✅ MessageIdValidationRule**: Validates message ID format
- **✅ NetworkValidationRule**: Validates network type
- **✅ EventTypeValidationRule**: Validates event type against allowed values (supports 24 different event types)
- **✅ ServiceValidationRule**: Validates service name
- **✅ InstantTimestampValidationRule**: Validates timestamp format

Validation errors return HTTP 400 with detailed field error messages.

## ⚠️ Exception Handling

The application implements comprehensive exception handling via `GlobalExceptionHandler`:

- **⚠️ AuditException**: Custom audit-specific exceptions with HTTP status
- **❌ ValidationException**: Validation rule violations
- **🔍 AuditNotFoundException**: Audit log not found (404)
- **⚠️ Generic Exceptions**: Caught and returned as 500 Internal Server Error

All exceptions are logged and return structured `ErrorResponse` objects.

## 🧪 Testing

The project includes comprehensive test coverage using JUnit 5 and TestContainers:

### Run Tests

```bash
mvn test
```

### Test Structure

- **🧪 Unit Tests**: Service, controller, validator, and exception handler logic
- **🧪 Integration Tests**: End-to-end API testing with embedded MongoDB
- **⚙️ Configuration**: Test MongoDB configuration using TestContainers

## 📦 Building and Deployment

### Build JAR
```bash
mvn clean package
```

Generated JAR: `target/auditService-0.0.1-SNAPSHOT.jar`

### Run JAR
```bash
java -jar target/auditService-0.0.1-SNAPSHOT.jar
```

### Maven Profiles (if needed)

Build with custom properties:
```bash
mvn clean install -Dspring.data.mongodb.uri=mongodb://your-host:27017/complyVault
```

## 📚 Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| spring-boot-starter-web | 3.5.5 | 🌐 REST API framework |
| spring-boot-starter-data-mongodb | 3.5.5 | 🗄️ MongoDB data access |
| spring-boot-starter-test | 3.5.5 | 🧪 Testing framework |
| lombok | Latest | 📦 Boilerplate code reduction |
| testcontainers-mongodb | Latest | 🐳 Containerized MongoDB for testing |
| testcontainers-junit-jupiter | Latest | 🧪 JUnit 5 integration with TestContainers |
| junit-platform-suite-api | 1.10.0 | 🧪 JUnit 5 test suite support |

## 📝 Logging

The application uses SLF4J for logging. Key components log:
- 🎮 Controller request/response
- 💼 Service operations
- ⚠️ Exception events
- ❌ Validation failures

Configure logging level in `application.properties`:
```properties
logging.level.com.smarsh.auditService=DEBUG
```

## ⚡ Performance Considerations

- **📄 Pagination**: Use pagination for large result sets (recommended page size: 50)
- **🔍 Indexing**: Consider MongoDB indexes on frequently queried fields:
  - `tenantId`
  - `messageId`
  - `eventType`
  - `timestamp`
- **👥 Multi-Tenancy**: Queries are always tenant-scoped for data isolation

## 🤝 Contributing

1. 🍃 Follow Spring Boot conventions
2. 🧪 Add tests for new features
3. ✅ Ensure validation rules are added to the validation chain
4. 📝 Update this README for new endpoints or features

## 📜 License

This project is part of the complyVault platform by Smarsh.

## 💬 Support

For issues or questions, please refer to the project repository or contact the development team.

---

**Version**: 0.0.1-SNAPSHOT  
**Last Updated**: December 2025  
**☕ Java Version**: 21  
**📋 Status**: ✅ Production Ready
