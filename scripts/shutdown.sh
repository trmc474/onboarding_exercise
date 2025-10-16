#!/bin/bash
set -e

NAMESPACE="onboarding-exercise"

echo "Stopping Docker Compose services..."
docker-compose down -v || true

echo "Removing Kubernetes resources..."
kubectl delete -f k8s/ --ignore-not-found=true || true

echo "Deleting namespace (if exists)..."
kubectl delete namespace $NAMESPACE --ignore-not-found=true || true

echo ""
echo "All resources stopped and cleaned up!"
