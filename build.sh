#!/bin/bash

# Exit on error
set -e

echo "Building Linkly Backend..."

# Check if Maven wrapper exists
if [ ! -f "./mvnw" ]; then
    echo "Error: Maven wrapper (mvnw) not found!"
    exit 1
fi

# Make Maven wrapper executable
chmod +x ./mvnw

# Build the application
echo "Running Maven build..."
./mvnw clean package -DskipTests

# Verify JAR was created
if [ ! -f target/*.jar ]; then
    echo "Error: Build failed - JAR file not found in target directory!"
    exit 1
fi

echo "✓ Build complete successfully!"