#!/bin/bash
set -e

echo "Building Spring Boot image..."
docker build -t onboarding-exercise:latest .

echo "Starting all services using Docker Compose..."
docker-compose up -d

echo "Docker services are up!"
echo ""
echo "Access points:"
echo "- App:         http://localhost:8080"
echo "- Kafka UI:    http://localhost:8081"
