---
# 🛠 Dev Setup Guide

This guide helps you quickly set up **MySQL**, **Redis**, **Kafka**, generate a **JWT token**, and configure an **AWS S3 bucket** for file storage.

---

## 🔧 1. Docker Compose: MySQL, Redis, Kafka

Create a `docker-compose.yml` with the following content:

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8
    container_name: mysql_container
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: testdb
      MYSQL_USER: user
      MYSQL_PASSWORD: password
    ports:
      - "3307:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - my_network

  redis:
    image: redis:latest
    container_name: redis_container
    restart: always
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - my_network

  kafka1:
    image: apache/kafka:3.8.0
    container_name: kafka1
    hostname: kafka1
    ports:
      - "9092:9092"
    environment:
      KAFKA_NODE_ID: 1
      KAFKA_CONTROLLER_LISTENER_NAMES: 'CONTROLLER'
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: 'CONTROLLER:PLAINTEXT,INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT'
      KAFKA_LISTENERS: 'INTERNAL://0.0.0.0:29092,CONTROLLER://kafka1:29093,EXTERNAL://0.0.0.0:9092'
      KAFKA_ADVERTISED_LISTENERS: 'INTERNAL://kafka1:29092,EXTERNAL://localhost:9092'
      KAFKA_INTER_BROKER_LISTENER_NAME: 'INTERNAL'
      KAFKA_CONTROLLER_QUORUM_VOTERS: '1@kafka1:29093'
      KAFKA_PROCESS_ROLES: 'broker,controller'
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 3
      CLUSTER_ID: 'your_cluster_ID'
      KAFKA_LOG_DIRS: '/tmp/kraft-combined-logs'
    volumes:
      - kafka_data:/var/lib/kafka/data
    networks:
      - my_network

volumes:
  mysql_data:
  redis_data:
  kafka_data:

networks:
  my_network:
```

### 🧠 How to generate the `CLUSTER_ID`:

```bash
uuidgen
# OR
openssl rand -hex 16
```

Copy the result and replace `'your_cluster_ID'`.

---

### 🔐 2. Generate a JWT Secret Key

You need a secret key to sign and validate JWT tokens in your authentication service.

To generate a secure key:

```bash
openssl rand -hex 64
```

This will return a random 128-character string (512 bits), which is ideal for HMAC algorithms (like `HS512`).

Add it to your config:

```properties
settings for .env and common.properties

JWT_SECRET=PASTE_GENERATED_SECRET_HERE
```

> ✅ Make sure to **never share this secret** publicly or commit it to Git.  
> For production, store it securely using AWS Secrets Manager, Vault, or environment variables.

---

## ☁ 3. How to Create an AWS S3 Bucket

1. Go to [AWS S3 Console](https://console.aws.amazon.com/s3/)
2. Click **"Create bucket"**
3. Fill in:
   - **Bucket name**: `your-bucket-name`
   - **Region**: e.g. `us-east-1`
   - (Optional) Disable "Block all public access" if required for file viewing
4. Click **Create Bucket**

### 🔐 Generate AWS Credentials:

1. Open [IAM Console](https://console.aws.amazon.com/iam/)
2. Go to **Users → [your-user] → Security credentials**
3. Click **Create access key**
4. Save:
   - `accessKeyId`
   - `secretAccessKey`

Add them to your environment config:

```properties
settings for .env and common.properties

AWS_KEY=YOUR_KEY
AWS_SECRET=YOUR_SECRET
AWS_STORAGE=your-bucket-name
aws.region=us-east-1
```
---

You're now ready to run all core services required for local development!

---
