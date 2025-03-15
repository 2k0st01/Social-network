#!/bin/bash

set -e

services=(
    "api-gateway"
    "authentication-client"
    "eureka-client-messenger"
    "eureka-file-store"
    "eureka-friends-followers-service"
    "news-feed-service"
    "post-service"
    "rating-service"
    "search-history"
)

echo "⚙️  Started building all microservices..."


for service in "${services[@]}"; do
    echo "🔨 Build microservice: $service"
    (cd "$service" && mvn clean package)
done

echo "✅ Build successful!"