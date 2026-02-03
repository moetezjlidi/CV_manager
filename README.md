# CV Manager - Gestion de CVs

A full-stack web application for managing Curriculum Vitae (CVs) with user authentication, activity management, and collaboration features.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Development](#development)
- [Authors](#authors)

## 🎯 Overview

CV Manager is a comprehensive web application that allows users to:
- Create and manage their professional CVs
- Add activities (professional experience, training, projects, etc.)
- Browse and search other CVs in the system
- Invite new users to create their CVs (co-optation system)
- Secure authentication with JWT tokens

## ✨ Features

### Core Functionality
- **User Authentication**: Secure login/logout system with JWT-based authentication
- **CV Management**: Create, read, update, and delete personal CV information
- **Activity Management**: Add various types of activities to your CV:
  - Professional Experience
  - Training/Education
  - Projects
  - Other activities
- **CV Browsing**: Search and view other users' CVs in the system
- **Invitation System**: Invite new users to join the platform via email
- **Responsive UI**: Modern Vue.js-based frontend with intuitive navigation

### Security Features
- JWT token-based authentication
- Spring Security integration
- Password encryption
- Role-based access control

## 🛠 Technology Stack

### Backend
- **Java 21**
- **Spring Boot 3.5.6**
  - Spring Data JPA (persistence)
  - Spring Security (authentication & authorization)
  - Spring Web (REST API)
  - Spring Mail (email notifications)
  - Spring Actuator (monitoring)
- **H2 Database** (embedded, file-based persistence)
- **Lombok** (reduce boilerplate code)
- **JWT** (JSON Web Tokens for authentication)
- **ModelMapper** (object mapping)
- **Maven** (dependency management)

### Frontend
- **Vue.js 3.5.18** (progressive JavaScript framework)
- **Vite 7.0.6** (build tool and dev server)
- **Axios 1.12.2** (HTTP client)
- **Node.js 20.19.0+** or **22.12.0+**

### Additional Tools
- **FakeSMTP** (local email testing)

## 📦 Prerequisites

Before running the application, ensure you have the following installed:

- **Java Development Kit (JDK) 21** or higher
- **Maven 3.6+** (or use the included Maven wrapper)
- **Node.js 20.19.0+** or **22.12.0+**
- **npm** (comes with Node.js)

## 🔧 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/moetezjlidi/CV_manager.git
cd CV_manager
```

### 2. Backend Setup

The backend uses Maven for dependency management. Dependencies will be downloaded automatically when you build the project.

```bash
cd gestion-de-cvs/backend
./mvnw clean install
```

### 3. Frontend Setup

Install the required npm packages:

```bash
cd gestion-de-cvs/frontend
npm install
```

## 🚀 Running the Application

The application requires both the backend server and the FakeSMTP server to be running.

### Option 1: Running the Pre-built WAR File (Quick Start)

**Step 1: Start the FakeSMTP Server**

Open a terminal and run:

```bash
java -jar fakeSMTP-2.0.jar -b -s -p 2525
```

This starts the fake SMTP server on port 2525 to handle email invitations.

**Step 2: Start the Application**

In a new terminal, run:

```bash
java -jar myapp.war
```

**Step 3: Access the Application**

Open your browser and navigate to:
```
http://localhost:8080/frontend/index.html#/
```

### Option 2: Running from Source (Development)

**Step 1: Start the FakeSMTP Server**

```bash
java -jar fakeSMTP-2.0.jar -b -s -p 2525
```

**Step 2: Build and Run the Backend**

```bash
cd gestion-de-cvs/backend
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`

**Step 3: Run the Frontend (Optional for Development)**

In a separate terminal:

```bash
cd gestion-de-cvs/frontend
npm run dev
```

This starts the Vite development server with hot-reload capabilities.

**Step 4: Access the Application**

- Production build: `http://localhost:8080/frontend/index.html#/`
- Development server: Check the terminal output for the Vite dev server URL (typically `http://localhost:5173`)

## 📁 Project Structure

```
CV_manager/
├── gestion-de-cvs/
│   ├── backend/                    # Spring Boot backend
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── com/architecture/gestion_de_cvs/
│   │   │   │   │       ├── config/         # Configuration classes
│   │   │   │   │       ├── dto/            # Data Transfer Objects
│   │   │   │   │       ├── exception/      # Custom exceptions
│   │   │   │   │       ├── model/          # JPA entities
│   │   │   │   │       ├── repository/     # Data repositories
│   │   │   │   │       ├── security/       # Security configuration
│   │   │   │   │       ├── service/        # Business logic
│   │   │   │   │       └── web/            # REST controllers
│   │   │   │   └── resources/
│   │   │   │       └── application.properties
│   │   │   └── test/                       # Unit and integration tests
│   │   └── pom.xml                         # Maven configuration
│   └── frontend/                            # Vue.js frontend
│       ├── src/
│       │   ├── components/
│       │   │   ├── auth/                   # Authentication components
│       │   │   ├── browse/                 # CV browsing components
│       │   │   ├── common/                 # Shared components
│       │   │   ├── cooptation/             # Invitation components
│       │   │   ├── invitation/             # Invitation acceptance
│       │   │   └── my-cv/                  # CV management components
│       │   ├── assets/                     # Static assets
│       │   ├── App.vue                     # Root component
│       │   ├── main.js                     # Application entry point
│       │   └── user.js                     # User state management
│       ├── index.html
│       ├── package.json                    # npm dependencies
│       └── vite.config.js                  # Vite configuration
├── fakeSMTP-2.0.jar                        # Fake SMTP server
├── myapp.war                               # Pre-built application package
└── README.md                               # This file
```

## 🔌 API Documentation

The backend exposes RESTful APIs for managing CVs and activities. Key endpoints include:

### Authentication
- `POST /api/users/login` - User login (returns JWT token)
- `POST /api/users/register` - User registration

### Person/CV Management
- `GET /api/persons` - Get all persons/CVs
- `GET /api/persons/{id}` - Get a specific person's CV
- `POST /api/persons` - Create a new person/CV
- `PUT /api/persons/{id}` - Update a person's CV
- `DELETE /api/persons/{id}` - Delete a person's CV

### Activity Management
- `GET /api/persons/{personId}/activities` - Get all activities for a person
- `POST /api/persons/{personId}/activities` - Add a new activity
- `PUT /api/activities/{id}` - Update an activity
- `DELETE /api/activities/{id}` - Delete an activity

### Invitation System
- `POST /api/invitations` - Send an invitation to join
- `GET /api/invitations/accept` - Accept an invitation

### Monitoring
- `GET /actuator/health` - Application health check
- `GET /h2-console` - H2 database console (development)

## 💻 Development

### Building the Frontend for Production

```bash
cd gestion-de-cvs/frontend
npm run build
```

The built files will be in the `dist/` directory.

### Building the Complete WAR Package

```bash
cd gestion-de-cvs/backend
./mvnw clean package
```

The WAR file will be in `target/gestion-de-cvs-0.0.1-SNAPSHOT.war`

### Running Tests

**Backend Tests:**
```bash
cd gestion-de-cvs/backend
./mvnw test
```

### Database Access

The application uses H2 database with file-based persistence. You can access the H2 console at:
```
http://localhost:8080/h2-console
```

Connection details:
- JDBC URL: `jdbc:h2:file:./data/cvdb`
- Username: `sa`
- Password: (leave empty)

## 👥 Authors

- **Reda Yahiaoui**
- **Moetez Jlidi**

## 📝 License

This project is part of an architecture course assignment.

---

For any questions or issues, please contact the authors or open an issue in the repository.
