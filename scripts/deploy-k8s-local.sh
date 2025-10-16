#!/bin/bash
set -e

NAMESPACE="onboarding-exercise"

echo "Building Spring Boot image for Kubernetes..."
docker build -t onboarding-exercise:latest .

echo "Deploying all manifests to Kubernetes..."
kubectl apply -f k8s/

echo ""
echo "Waiting for pods to start..."
kubectl wait --for=condition=Ready pods --all -n $NAMESPACE --timeout=120s || true

echo ""
echo "All components deployed in namespace '$NAMESPACE'"
kubectl get pods -n $NAMESPACE
echo ""
echo "Access points:"
echo "- App:          http://localhost:30080"
echo "- Kafka UI:     http://localhost:30081"

