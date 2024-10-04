# PerfectTrip-Server Project

## Introduction

This is a simple booking website application built using Spring Boot. It provides basic functionality for adding products, removing them, and viewing the product's contents. This project serves as a starting point for building more complex booking website applications.

## Features (To Be Supplemented)

- Register new users or companies
- Login for existing users or companies
- Add products to the cart
- Remove products from the cart
- View the contents of the cart

## Technologies Used

- Spring Boot
- Spring Data JPA
- Maven (for dependency management)
- MySQL

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.9.5 or higher

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/iorlvm/PerfectTrip-Service.git
   cd PerfectTrip-Service
   ```

2. Build the project:
   ```
   mvn clean install
   ```

3. Run the application:
   ```
   mvn spring-boot:run
   ```

4. Open your browser and navigate to http://localhost:8080.

**Startup Notes**

The files cloned from Git are missing db.properties because this file contains confidential database connection information. Please obtain it from the team leader.

After you have the file, place it into the resources directory. The freshly cloned project might not have this folder, so please create it yourself if necessary.

```
ProjectFolder/src/main/resources/db.properties
```

## Usage (To Be Supplemented)

### Register user
To add a user to PerfectTrip-Server, send a POST request to:
```
http://localhost:8080/users/register
```

with a JSON body containing the product details, for example:
```json
{
  "username": "test@gmail.com",
  "password": "123456"
}
```

### Login user
To login to PerfectTrip-Server, send a POST request to:
```
http://localhost:8080/users/login
```

with a JSON body containing the product details, for example:
```json
{
  "username": "test@gmail.com",
  "password": "123456"
}
```

### Get User
To get user details, send a GET request to:
```
http://localhost:8080/users/{userId}
```

### Get All Users
To get all users details, send a GET request to:
```
http://localhost:8080/users
```

### Update User
To update user details, send a PUT request to:
```
http://localhost:8080/users/{userId}
```
with a JSON body containing the product details, for example:
```json
{
  "password": "123456"
}
```

### Delete User
To delete user details, send a DELETE request to:
```
http://localhost:8080/users/{userId}
```

## Project Structure (To Be Supplemented)

1. src/main/java/idv/tia201/g1 - Main application package
2. src/main/java/idv/tia201/g1/core - Core components package
3. src/main/resources/application.properties - Application configuration file

## Contributing

Contributions are welcome! Please fork the repository and create a pull request with your changes. Make sure to follow the project’s coding standards and write tests for any new features or bug fixes.

## License

N/A

## Acknowledgements (To Be Supplemented)

1. Spring Boot - for providing the framework

## Contact

If you have any questions or suggestions, feel free to reach out to me at [xxx@gmail.com](xxx@gmail.com).

Happy coding!