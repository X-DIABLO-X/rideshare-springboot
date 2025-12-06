# Rideshare Application

A Spring Boot based ridesharing application backend that connects passengers with drivers. This application uses MongoDB for data storage and JWT for secure authentication.

**Student - Harshit Tiwari**

**Roll - 24BCS10277**

## Features

*   **User Authentication:** Secure registration and login using JWT (JSON Web Tokens).
*   **Role-Based Access:** Separate functionality for Passengers (Users) and Drivers.
*   **Ride Management:**
    *   Passengers can request rides.
    *   Drivers can view pending ride requests.
    *   Drivers can accept rides.
    *   Ride status tracking (Created, Accepted, Completed).
*   **Ride History:** Users can view their past rides.

## Technologies Used

*   **Java:** 25
*   **Framework:** Spring Boot 3.x
*   **Database:** MongoDB
*   **Security:** Spring Security, JWT (jjwt)
*   **Build Tool:** Maven
*   **Other Libraries:** Lombok, Jakarta Validation

## Prerequisites

*   Java Development Kit (JDK) 25 or compatible version.
*   Maven.
*   MongoDB installed and running locally on port `27017`.

## Configuration

The application is configured via `src/main/resources/application.yaml`.

*   **Server Port:** `8081`
*   **Database URI:** `mongodb://localhost:27017/rideshare`
*   **JWT Secret:** Configured in `application.yaml`
*   **JWT Expiration:** 24 hours

## Installation and Setup

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd rideshare
    ```

2.  **Build the project:**
    ```bash
    mvn clean install
    ```

3.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    Or run the `RideshareApplication` class from your IDE.

## API Documentation

### Authentication

#### 1. Register User
Register a new user (Passenger or Driver).

*   **Endpoint:** `POST /api/auth/register`
*   **Body:**
    ```json
    {
      "username": "john_doe",
      "password": "password123",
      "role": "USER" // or "DRIVER"
    }
    ```
*   **Response:** `200 OK` - "User registered successfully"

#### 2. Login
Login to receive a JWT token.

*   **Endpoint:** `POST /api/auth/login`
*   **Body:**
    ```json
    {
      "username": "john_doe",
      "password": "password123"
    }
    ```
*   **Response:** `200 OK`
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiJ9..."
    }
    ```

---

### Passenger (User) Endpoints

**Note:** These endpoints require the `Authorization` header with the value `Bearer <token>`.

#### 1. Create a Ride
Request a new ride.

*   **Endpoint:** `POST /api/v1/rides`
*   **Body:**
    ```json
    {
      "pickupLocation": "Central Station",
      "dropLocation": "Airport"
    }
    ```
*   **Response:** `200 OK` (Ride details)

#### 2. Get User Rides
Retrieve the history of rides for the logged-in user.

*   **Endpoint:** `GET /api/v1/user/rides`
*   **Response:** `200 OK` (List of rides)

#### 3. Complete Ride
Mark a ride as completed.

*   **Endpoint:** `POST /api/v1/rides/{rideId}/complete`
*   **Response:** `200 OK` (Updated ride details)

---

### Driver Endpoints

**Note:** These endpoints require the `Authorization` header with the value `Bearer <token>`.

#### 1. Get Pending Rides
View all rides that are waiting for a driver.

*   **Endpoint:** `GET /api/v1/driver/rides/requests`
*   **Response:** `200 OK` (List of pending rides)

#### 2. Accept Ride
Accept a specific ride request.

*   **Endpoint:** `POST /api/v1/driver/rides/{rideId}/accept`
*   **Response:** `200 OK` (Updated ride details with driver assigned)
