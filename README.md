# Healthcare Management System

A microservices-based healthcare management application built with Java, Spring Boot, Spring Cloud, MongoDB, and React. The backend is organized into independent services for authentication, patient management, doctor management, and appointments, with centralized routing through an API Gateway and service discovery using Eureka.

## Project Overview

The system is designed to support healthcare workflows through independently deployable backend services and REST APIs.

### Architecture

```text
                 React Frontend
                 (Vite + React)
                 In Progress
                       |
                       v
              API Gateway :8080
                       |
                       v
               Eureka Server :8761
                       |
        +--------------+--------------+
        |              |              |
        v              v              v
   Auth Service   Patient Service  Doctor Service
      :8081           :8082          :8083
                       |
                       v
               Appointment Service
                      :8084
                       |
                       v
                    MongoDB
```

*Note: The React frontend has been initialized. Full frontend-to-backend integration is still in progress.*

## Technology Stack

### Backend
- Java 21
- Spring Boot
- Spring Cloud
- Spring MVC REST APIs
- Spring Cloud Gateway Server WebMVC
- Spring Cloud Netflix Eureka
- OpenFeign for service-to-service communication
- Spring Security and JWT authentication
- MongoDB
- Maven

### Frontend
- React
- Vite
- JavaScript
- HTML and CSS
- ESLint

### Testing and Code Quality
- JUnit
- Spring Boot Test
- MockMvc and Mockito where applicable
- Postman for API testing
- Spotless with Google Java Format
- GitHub Actions CI

## Backend Services

| Service | Port | Responsibility |
|---|---:|---|
| Auth Service | 8081 | Authentication and JWT generation |
| Patient Service | 8082 | Patient-related APIs |
| Doctor Service | 8083 | Doctor-related APIs |
| Appointment Service | 8084 | Appointment-related APIs and communication with patient/doctor services |
| Eureka Server | 8761 | Service registration and discovery |
| API Gateway | 8080 | Central entry point for backend API requests |

## Implemented Features

### Authentication and Security
- REST APIs for authentication.
- JWT-based authentication.
- Token validation by protected backend services.
- Role-based access controls for supported operations.
- Password hashing using BCrypt in the authentication service.

### Patient and Doctor Management
- Separate patient and doctor services.
- REST APIs for supported patient and doctor operations.
- Independent service structure for maintainability.

### Appointment Management
- Dedicated appointment service.
- Communication with patient and doctor services through OpenFeign.
- Forwarding of the Authorization header for authenticated inter-service requests.
- Business validation for supported appointment operations.

### Service Discovery
- Eureka server for registering and discovering services.
- Backend services and API Gateway configured to use Eureka.

### API Gateway
- Centralized routing for authentication, patients, doctors, and appointments.
- Load-balanced routing through service discovery.
- Standardized service-unavailable responses for supported gateway failures.

### Error Handling and Validation
- Global exception handling in implemented services.
- Standardized API error responses.
- Request validation and appointment business-rule checks.

### Testing and Code Quality
- Unit and integration tests for implemented backend functionality.
- Gateway route and error-handling tests.
- Spotless configured with Google Java Format.
- GitHub Actions workflow builds and tests all six Maven projects independently.

## Frontend Status

The initial React application has been created using Vite.

Completed:
- React and Vite project initialization.
- Development server configuration.
- Initial application component.
- Basic page styling.
- Browser title and description metadata.
- ESLint configuration.

Planned:
- Login form and authentication integration.
- JWT storage and authenticated requests.
- Patient and doctor views.
- Appointment management UI.
- API integration through the Gateway.
- Loading states, error messages, and form validation.

## Project Structure

```text
healthcare-microservices/
├── auth-service/
├── patient-service/
├── doctor-service/
├── appointment-service/
├── eureka-server/
├── api-gateway/
├── healthcare-frontend/
│   ├── public/
│   ├── src/
│   │   ├── assets/
│   │   ├── App.jsx
│   │   ├── App.css
│   │   ├── index.css
│   │   └── main.jsx
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── .github/
│   └── workflows/
│       └── ci.yml
└── README.md
```

## Running the Project

### Prerequisites
- Java 21
- Maven or IntelliJ IDEA's Maven integration
- Node.js 22 or a version compatible with the installed Vite release
- MongoDB
- Git

### Start the Backend
1. Start MongoDB.
2. Start the Eureka Server on port `8761`.
3. Start the Auth Service on port `8081`.
4. Start the Patient Service on port `8082`.
5. Start the Doctor Service on port `8083`.
6. Start the Appointment Service on port `8084`.
7. Start the API Gateway on port `8080`.

Ensure each service's configuration and database settings are correct before starting it.

Eureka Dashboard: http://localhost:8761

### Start the Frontend

Open a terminal in the frontend directory:

```bash
cd healthcare-frontend
npm install
npm run dev
```

Open the local URL printed by Vite, usually:

http://localhost:5173

The frontend is currently a starter application and is not yet fully integrated with the backend.

## Continuous Integration

GitHub Actions runs the Maven build and test lifecycle independently for each of the six backend projects:
- Auth Service
- Patient Service
- Doctor Service
- Appointment Service
- Eureka Server
- API Gateway

The workflow runs on pushes and pull requests targeting the `main` or `master` branches.

## Future Improvements

- Complete the React frontend.
- Integrate frontend authentication with the API Gateway.
- Build patient, doctor, and appointment screens.
- Add more end-to-end tests.
- Add Docker and Docker Compose support.
- Introduce Kafka-based asynchronous communication if needed.
- Add architecture diagrams and screenshots.

## Project Goal

Build a maintainable healthcare application that demonstrates REST API development, microservices architecture, authentication and authorization, service discovery, API gateway routing, automated testing, CI, and frontend integration.
