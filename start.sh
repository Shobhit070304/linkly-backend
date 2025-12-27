#!/bin/bash

# Exit on error
set -e

echo "Starting Linkly Backend..."

# Find the JAR file dynamically
JAR_FILE=$(find target -name "*.jar" -type f | head -n 1)

# Check if JAR file exists
if [ -z "$JAR_FILE" ]; then
    echo "Error: No JAR file found in target directory!"
    echo "Please run './build.sh' first to build the application."
    exit 1
fi

echo "Found JAR: $JAR_FILE"
echo "Starting application..."

# Run the JAR file
java -jar "$JAR_FILE"