# Backend README

This README provides information on how to set up and run the backend portion of the Ticket Management System.

## Prerequisites
- **Java** (version 17 or higher)
- **Maven** (for building the Spring Boot application)

---

## Installation and Setup

1. **Navigate to the Backend Directory**
   ```bash
   cd backend
   ```

2. **Build the Application**
   ```bash
   mvn clean install
   ```

3. **Run the Spring Boot Application**
   ```bash
   mvn spring-boot:run
   ```
   The backend will be running at [http://localhost:9095](http://localhost:9095).

---

## API Endpoints

### **Configuration Endpoints**
| **Method** | **Endpoint**       | **Description**                   |
|------------|-------------------|-----------------------------------|
| POST       | `/api/config`      | Sets ticket system configuration   |

### **Control Endpoints**
| **Method** | **Endpoint**       | **Description**                   |
|------------|-------------------|-----------------------------------|
| POST       | `/api/start`       | Starts the ticket system           |
| POST       | `/api/stop`        | Stops the ticket system            |

### **Ticket Endpoints**
| **Method** | **Endpoint**       | **Description**                   |
|------------|-------------------|-----------------------------------|
| GET        | `/api/tickets/count` | Returns available ticket count    |

### **Log Endpoints**
| **Method** | **Endpoint**       | **Description**                   |
|------------|-------------------|-----------------------------------|
| GET        | `/api/log`         | Fetches system logs                |
| DELETE     | `/api/log`         | Clears system logs                 |

---

## Project Structure

```
backend/
├── src/main/java/com/yourpackage/   # Source code for API and logic
├── src/main/resources/              # Configuration files (e.g., application.properties)
```

---

## Troubleshooting

| **Issue**         | **Solution**                              |
|------------------|------------------------------------------|
| Port in use      | Ensure port 9095 is free.                  |
| Backend errors   | Check console logs for stack traces.      |

---

## License

This project is licensed under the **MIT License**.
