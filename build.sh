#!/bin/bash

echo "Building Linkly Backend..."

# Build the application
./mvnw clean package -DskipTests

echo "Build complete!"