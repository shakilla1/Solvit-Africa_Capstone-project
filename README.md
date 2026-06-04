# Solvit Africa Capstone Project

## Project Overview

PhishGuard Pro is a full-stack phishing awareness platform built for enterprise security training. It provides administrators with campaign management, email simulation, target user tracking, training progress, and real-time reporting, while employees can complete learning tasks, review notifications, and improve security awareness.

## Problem Statement

Many organizations struggle to train employees against phishing attacks and to measure the effectiveness of their security awareness programs. Without a centralized platform, security teams lack:

- a way to run controlled phishing campaigns,
- tracking for clicks, submissions, and user behavior,
- targeted training progress management,
- notifications for security actions,
- a single view of campaign performance.

## Solution

This repository delivers a SQL-based backend and modern frontend UI for managing phishing awareness:

- `phishguard-backend`
  - Spring Boot API with JPA/Hibernate
  - MySQL database support
  - JWT authentication and role-based access
  - entities for admins, target users, campaigns, notifications, training progress, and simulation events
  - seed data support and validation

- `Build Functional Laptop Design`
  - frontend application built with Vite and React/TypeScript
  - admin and user pages for campaign control and learning experience

- `Build Functional Laptop Design/vue-app`
  - alternate frontend built with Vue
  - reusable components, routing, and auth store

## Repository Structure

- `Build Functional Laptop Design/` — React/Vite frontend
- `Build Functional Laptop Design/vue-app/` — Vue frontend
- `phishguard-backend/` — Java Spring Boot backend
- `phishguard-backend/schema.sql` — MySQL schema definition and indexes

## Prerequisites

- Java 21
- Maven
- Node.js 18+ and npm
- MySQL server

## Backend Setup

1. Open a terminal in `phishguard-backend`
2. Update `src/main/resources/application.properties`:
   - `spring.datasource.username`
   - `spring.datasource.password`
3. Create the database manually or allow Spring Boot auto-create it:
   ```bash
   mysql -u root -p < schema.sql
   ```
4. Run the backend:
   ```bash
   mvn clean spring-boot:run
   ```

The backend runs on `http://localhost:8080` by default.

## Frontend Setup

Choose one frontend app to run:

### React frontend

1. Open a terminal in `Build Functional Laptop Design`
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the dev server:
   ```bash
   npm run dev
   ```

### Vue frontend

1. Open a terminal in `Build Functional Laptop Design/vue-app`
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the dev server:
   ```bash
   npm run dev
   ```

The frontend is configured to connect to `http://localhost:8080` and may run on port `5173`.

## Notes

- The backend uses MySQL and Spring JPA, not NoSQL.
- The `schema.sql` file defines the database tables and indexes.
- If you run into duplicate index issues, make sure the database does not already have partially applied schema artifacts.

## Contact

If you need help running the project, update connection settings, or choose which frontend to use, just ask.
