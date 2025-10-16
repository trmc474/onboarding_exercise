# Onboarding Exercise: Person Management System

A comprehensive Spring Boot application demonstrating professional event-driven microservices architecture with advanced
Kafka patterns, batch processing, and modern deployment practices.

## Project Overview

This application manages "Person" entities with full CRUD operations, advanced search capabilities, and sophisticated
event-driven architecture. All write operations are processed asynchronously through Kafka, demonstrating
enterprise-grade patterns including non-blocking retries, batch processing, and manual acknowledgment strategies.

### Key Features

- **Event-Driven Architecture**: All CUD operations processed via Kafka events
- **Advanced Batch Processing**: Both blocking and non-blocking retry strategies
- **Manual Message Acknowledgment**: Full control over Kafka offset commits
- **Non-Blocking Retries**: Failed messages don't block processing pipeline
- **Dead Letter Topics**: Comprehensive error handling for failed messages
- **Tax Calculation Events**: Real-time tax debt updates via Kafka
- **Professional Search**: Dynamic filtering, sorting, and pagination
- **Container-Ready**: Full Docker and Kubernetes deployment support

## Core Technologies

- **Backend**: Spring Boot 3.5.6, Java 25
- **Database**: PostgreSQL with Liquibase migrations
- **Messaging**: Apache Kafka with Confluent Platform
- **Containerization**: Docker & Docker Compose
- **Orchestration**: Kubernetes (Docker Desktop)
- **Build Tool**: Maven 3.9+
- **Documentation**: OpenAPI 3 (Swagger)

## Prerequisites

- Java 25+ (JDK)
- Maven 3.9+
- Docker Desktop with Kubernetes enabled
- kubectl CLI tool

## Quick Start

In the root folder:

* Create `.env` file based on `.env.example`


* **Option 1:** Deploy local on Docker

```bash
./scripts/deploy-docker-local.sh
 ```

* **Option 2:** Deploy local on Kubernetes

```bash
./scripts/deploy-k8s-local.sh`
```

* Shutdown the project:

```bash
./scripts/shutdown.sh
```
