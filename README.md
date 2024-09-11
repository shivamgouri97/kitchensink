
# KitchenSink

This Spring Boot 3 application was developed with Java 21 and Maven 3.3. The application uses MongoDB as the primary database and is an example of a fully functional backend service with various features and best practices. The migration to Spring Boot provides enhanced flexibility, ease of development, and a modernized architecture.

## Prerequisites

- **Java 21**: Ensure you have JDK 21 installed on your machine.
  - [Download JDK 21](https://jdk.java.net/21/)
  
- **Maven 3.3 or later**: Ensure you have Maven 3.3 or later installed.
  - [Download Maven](https://maven.apache.org/download.cgi)
  
- **An IDE** (e.g., IntelliJ IDEA, Eclipse, VS Code)

- **MongoDb Community Edition**:
   -[official MongoDB website](https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-os-x/#std-label-install-mdb-community-macos)

- **Spring Boot**:
  - version 3.3.3

## Getting Started

Follow these steps to set up and run the application locally:

### 1. Clone the Repository

Clone the repository from GitHub to your local machine:
```bash
git clone https://github.com/shivamgouri97/kitchensink.git
```

### 2. Setup and Installation

#### Update Configuration

The default configuration is set to connect to a MongoDB instance running on `localhost` with the following settings:
- Hostname: `localhost`
- Port: `27017`
- Database Name: `testdb`

#### Setup MongoDB

To set up MongoDB on your local machine, follow these steps:

##### Install MongoDB
- **macOS**: Use Homebrew to install MongoDB:
    ```bash
    brew tap mongodb/brew
    brew install mongodb-community
    ```
- **Windows**: Download the MongoDB installer from the [official MongoDB website](https://www.mongodb.com/try/download/community) and follow the installation instructions.

##### Start MongoDB Service
- **macOS** (using Homebrew services):
    ```bash
    brew services start mongodb-community
    ```
- **Windows**:
    Open Command Prompt or PowerShell as Administrator and run:
    ```bash
    net start MongoDB
    ```

#### Verify MongoDB is Running

Use the following command to connect to the MongoDB shell and verify the service is running:
```bash
mongo
```

#### Update MongoDB Connection Details

Update the MongoDB connection details in `application.yml/ application.properties` located in `src/main/resources/`:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/testdb
```

### 3. Navigate to Project Directory
```bash
cd kitchensink
```

### 4. Build the Application

Use Maven to build the application:
```bash
mvn clean install
```
This command will compile the source code, run unit tests, and package the application into a runnable JAR file.

### 5. Run the Application

Run the application using Maven:
```bash
mvn spring-boot:run
```

### 6. Running Tests

To run unit tests, execute the following Maven command:
```bash
mvn test
```

### 7. Access the Application

Once the application is running, access the API at:
```text
http://localhost:8081
```
Use tools like Postman or curl to interact with the endpoints.

## API Documentation

The API documentation is available via Swagger UI. You can explore and test the API endpoints directly at:
```text
http://localhost:8081/kitchensink/swagger-ui/index.html
```

## API Endpoints

Below is a list of the main RESTful API endpoints provided by the KitchenSink application.

### 1. Get Member by ID
Retrieve a member record by ID.

**Endpoint**: `GET /kitchensink/rest/members/{id}`  
**Example**: `http://localhost:8081/kitchensink/rest/members/1`

### 2. Add New Member
Create a new member record.

**Endpoint**: `POST /kitchensink/rest/members`  
**Request Body**:
```json
{
    "name": "John Doe",
    "email": "john.d@example.com",
    "phoneNumber": "78787878781"
}
```

### 3. Get All Members (Sorted by Name)

**Endpoint**: `GET /kitchensink/rest/members`

### 4. Delete Member by ID
To delete a member by ID, use the following curl command:
```bash
curl --location --request DELETE 'http://localhost:8081/kitchensink/rest/members/{id}' --header 'Authorization: Basic YWRtaW46YWRtaW4=' --header 'Cookie: JSESSIONID=001B7FA34313816BD7578E60584DE6D7'
```

## API Security

APIs are secured through Basic Authentication.

### Accessible by USER and ADMIN Roles:
1. Add New Member
2. Get All Members (Sorted by Name)
3. Get Member by ID

### Accessible by ADMIN Role Only:
1. Delete Member by ID
