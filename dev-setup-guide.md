---
# 🛠 Dev Setup Guide

This guide helps you quickly set up **MySQL**, **Redis**, **Kafka**, generate a **JWT token**, and configure an **AWS S3 bucket** for file storage.

---

## 🔧 1. Docker Compose: MySQL, Redis, Kafka

Open a `docker-compose.yml` in the folder.

Find 

```yaml
      CLUSTER_ID: 'your_cluster_ID'
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
