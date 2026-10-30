# IntelliOps – Intelligent Log Analysis Platform

> An AI-powered platform for intelligent log analysis, semantic search, automated anomaly detection, and incident root-cause analysis.

IntelliOps is an intelligent log analysis and incident management platform designed to help developers and DevOps teams understand application failures faster.

The system ingests application logs, processes them into searchable chunks, generates vector embeddings, stores them in Redis for semantic and hybrid search, automatically detects abnormal error spikes, and uses Generative AI to analyze incidents and identify probable root causes.

---

## 🚀 Key Features

### 1. Intelligent Log Ingestion
- Upload application log files through the REST API.
- Logs are divided into manageable chunks.
- Each chunk is processed asynchronously through a Redis-based ingestion queue.
- Log metadata such as source, file name, chunk number, and job ID is maintained.

### 2. Semantic Log Search
- Converts log information into vector embeddings.
- Stores embeddings in Redis.
- Uses vector similarity search to retrieve semantically relevant logs.
- Allows users to search for logs using natural-language queries instead of exact keywords.

### 3. Hybrid Search
Combines:
- Keyword-based filtering
- Vector similarity search

This improves search accuracy when both exact technical terms and semantic meaning are important.

### 4. AI-Powered Root Cause Analysis
Generative AI analyzes relevant log evidence and produces:

- Root cause summary
- Probable root cause
- Business/system impact
- Recommended actions
- Confidence level
- Supporting log evidence

Example query:

```text
Why did the payment processing fail for order ORD-1001?
```

The system retrieves relevant logs and sends the evidence to the AI service for analysis.

### 5. Automatic Anomaly Detection
IntelliOps continuously monitors ingested log chunks.

When an abnormal increase in error messages is detected, the system can automatically create an incident.

For example:

```text
Error spike detected
        ↓
Anomaly detected
        ↓
Incident automatically created
        ↓
Severity assigned
        ↓
Root cause information recorded
        ↓
Incident displayed on dashboard
```

### 6. Incident Management
The platform provides APIs for managing detected incidents.

Each incident contains information such as:

- Incident ID
- Title
- Severity
- Description
- Source query
- Root cause
- Impact
- Recommended actions
- Confidence level
- Status
- Created/updated timestamps

Supported incident statuses:

```text
OPEN
IN_PROGRESS
RESOLVED
```

### 7. Interactive Dashboard
A React-based dashboard provides a visual interface for monitoring:

- Incidents
- Severity
- Incident status
- Root cause information
- Log analysis results
- System information

---

# 🏗️ System Architecture

```text
                         ┌──────────────────────┐
                         │      React UI        │
                         │      Dashboard       │
                         └──────────┬───────────┘
                                    │
                                    │ REST APIs
                                    ▼
                    ┌──────────────────────────────┐
                    │       Spring Boot Backend    │
                    │                              │
                    │  Controllers                 │
                    │  Services                    │
                    │  Incident Management         │
                    │  Search & AI Analysis        │
                    │  Anomaly Detection           │
                    └───────┬─────────┬────────────┘
                            │         │
                ┌───────────┘         └──────────────┐
                ▼                                    ▼
       ┌──────────────────┐                 ┌──────────────────┐
       │      Redis       │                 │   PostgreSQL     │
       │                  │                 │                  │
       │ Ingestion Queue  │                 │    Incidents     │
       │ Vector Embedding │                 │    Metadata      │
       │ Semantic Search  │                 │    Application   │
       │ RediSearch       │                 │    Data          │
       └────────┬─────────┘                 └──────────────────┘
                │
                ▼
       ┌──────────────────┐
       │   AI / Gemini    │
       │                  │
       │ Root Cause       │
       │ Analysis         │
       │ Recommendations  │
       └──────────────────┘
```

---

# 🔄 Application Workflow

## Log Analysis Workflow

```text
Log File
   │
   ▼
Log Upload API
   │
   ▼
Log Chunking
   │
   ▼
Redis Ingestion Queue
   │
   ▼
Background Ingestion Worker
   │
   ├───────────────► Generate Embeddings
   │                       │
   │                       ▼
   │                  Redis Vector Store
   │
   ▼
Error Analysis
   │
   ▼
Anomaly Detection
   │
   ▼
Incident Creation
```

## Root Cause Analysis Workflow

```text
User Query
    │
    ▼
Search API
    │
    ▼
Semantic / Hybrid Search
    │
    ▼
Relevant Log Evidence
    │
    ▼
Gemini AI
    │
    ▼
Root Cause Analysis
    │
    ├── Root Cause
    ├── Impact
    ├── Recommended Actions
    └── Confidence
    │
    ▼
Incident / Dashboard
```

---

# 🛠️ Technology Stack

## Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Maven
- REST APIs

## Database

- PostgreSQL

## Search & Processing

- Redis
- RediSearch
- Vector Embeddings
- KNN Vector Search
- Hybrid Search

## Artificial Intelligence

- Generative AI
- Gemini API
- AI-powered Root Cause Analysis

## Frontend

- React
- JavaScript
- Vite
- HTML
- CSS

## Development Tools

- IntelliJ IDEA / VS Code
- Git
- GitHub
- Postman
- pgAdmin

---

# 📁 Project Structure

```text
IntelliOps-Intelligent-Log-Analysis-Platform/
│
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── assets/
│   │   ├── App.jsx
│   │   ├── App.css
│   │   ├── index.css
│   │   └── main.jsx
│   ├── package.json
│   └── vite.config.js
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── incidentiq/
│   │   │           ├── config/
│   │   │           ├── controller/
│   │   │           ├── model/
│   │   │           ├── repository/
│   │   │           ├── service/
│   │   │           └── util/
│   │   │
│   │   └── resources/
│   │       └── application.yaml
│   │
│   └── test/
│
├── pom.xml
├── docker-compose.yml
├── mvnw
├── mvnw.cmd
├── .gitignore
└── README.md
```

---

# 🔌 REST API Endpoints

## Log Upload

Upload application logs for processing.

```http
POST /logs/upload
```

Content type:

```text
multipart/form-data
```

---

## Semantic Search

Search logs using semantic similarity.

```http
POST /search/semantic
```

Example request:

```json
{
  "query": "payment database connection failure",
  "topK": 5
}
```

---

## Hybrid Search

Perform combined keyword and semantic search.

```http
POST /search/hybrid
```

---

## Root Cause Analysis

Analyze an incident or log-related problem using AI.

```http
POST /insights/root-cause
```

Example request:

```json
{
  "query": "Why did the payment processing fail for order ORD-1001?",
  "topK": 3
}
```

Example response structure:

```json
{
  "query": "Why did the payment processing fail for order ORD-1001?",
  "rootCauseSummary": "Payment processing failed because of repeated database connection timeouts.",
  "probableRootCause": "Database connection timeout prevented PaymentService from completing validation and processing.",
  "impact": "The order could not complete payment processing.",
  "recommendedActions": [
    "Check database health",
    "Inspect database connection pool",
    "Check network connectivity",
    "Investigate long-running queries"
  ],
  "confidenceLevel": "HIGH"
}
```

---

## Incident Management

### Get all incidents

```http
GET /incidents
```

### Get incident by ID

```http
GET /incidents/{id}
```

### Create an incident

```http
POST /incidents
```

### Update incident status

```http
PUT /incidents/{id}/status
```

### Delete an incident

```http
DELETE /incidents/{id}
```

### Filter by status

```http
GET /incidents/status/{status}
```

Supported values:

```text
OPEN
IN_PROGRESS
RESOLVED
```

### Filter by severity

```http
GET /incidents/severity/{severity}
```

---

# ⚙️ Prerequisites

Make sure the following are installed:

- Java 21+
- Maven or Maven Wrapper
- Node.js
- npm
- PostgreSQL
- Redis with RediSearch support
- Git

You also need a Gemini API key for AI-powered analysis.

---

# 🔐 Environment Variables

Sensitive credentials should **never be committed to GitHub**.

Configure the following environment variables locally:

```text
DB_PASSWORD=your_postgresql_password
GEMINI_API_KEY=your_gemini_api_key
```

The application configuration uses environment variables instead of storing credentials directly in source code.

Example:

```yaml
spring:
  datasource:
    username: postgres
    password: ${DB_PASSWORD}
```

And the AI configuration uses:

```text
GEMINI_API_KEY
```

> Never commit your actual API key, database password, `.env` file, or other credentials to GitHub.

---

# 🗄️ Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE intelliops;
```

Make sure PostgreSQL is running and configure the database credentials through environment variables.

The application uses PostgreSQL for persistent application and incident data.

---

# 🔴 Redis Setup

Redis is required for:

- Log ingestion queue
- Vector embeddings
- Semantic search
- Hybrid search
- RediSearch indexes

Make sure Redis is running on your configured Redis host and port.

Default Redis configuration:

```text
Host: localhost
Port: 6379
```

---

# ▶️ Running the Backend

From the project root:

### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

Or build the project first:

```powershell
.\mvnw.cmd clean package
```

Then run the generated application.

The backend runs on:

```text
http://localhost:8080
```

---

# ▶️ Running the Frontend

Navigate to the frontend directory:

```powershell
cd frontend
```

Install dependencies:

```powershell
npm install
```

Start the development server:

```powershell
npm run dev
```

Vite will display the local frontend URL in the terminal.

---

# 🧪 Testing the Application

You can test the REST APIs using:

- Postman
- Browser
- React Dashboard

A typical testing flow is:

```text
1. Start PostgreSQL
        ↓
2. Start Redis
        ↓
3. Start Spring Boot backend
        ↓
4. Start React frontend
        ↓
5. Upload logs
        ↓
6. Verify log ingestion
        ↓
7. Perform semantic/hybrid search
        ↓
8. Run AI root-cause analysis
        ↓
9. Generate/verify incidents
        ↓
10. Monitor incidents from dashboard
```

---

# 🧠 Example Use Case

Suppose an application generates the following errors:

```text
PaymentService - Database connection timeout
PaymentService - Failed to acquire database connection
PaymentService - Payment processing failed
```

Instead of manually searching thousands of log lines, a developer can ask:

```text
Why did the payment processing fail?
```

IntelliOps:

1. Searches relevant logs.
2. Retrieves semantically related evidence.
3. Sends the evidence to the AI analysis layer.
4. Identifies the probable root cause.
5. Determines the potential impact.
6. Generates recommended actions.
7. Displays the result through the dashboard.

This reduces the time required for manual log investigation and helps developers identify production issues faster.

---

# 📊 Incident Severity

The platform supports incident severity levels such as:

```text
CRITICAL
WARNING
INFO
MEDIUM
```

Automatically detected error spikes can generate high-severity incidents requiring investigation.

---

# 🔍 Search Architecture

IntelliOps supports two major search approaches.

### Semantic Search

Uses vector embeddings to identify logs based on their meaning.

For example:

```text
Query:
"Why can't the application connect to the database?"

Can retrieve:
"PaymentService failed to acquire database connection"
```

Even though the exact words are different, the semantic meaning is similar.

### Hybrid Search

Combines:

```text
Keyword matching
       +
Vector similarity
       =
More relevant results
```

This is useful when searching for technical identifiers such as:

```text
ORD-1001
PaymentService
DatabaseTimeoutException
```

while still considering the semantic meaning of the query.

---

# 🎯 Project Objectives

The main objectives of IntelliOps are:

- Automate application log analysis.
- Reduce manual log investigation.
- Provide semantic log search.
- Detect abnormal error patterns.
- Automatically generate incidents.
- Use Generative AI for root-cause analysis.
- Provide actionable recommendations.
- Provide a centralized incident monitoring dashboard.

---

# 🚀 Future Enhancements

Potential future improvements include:

- Prometheus-based application metrics
- Grafana monitoring dashboards
- Real-time alerting
- Email/Slack notifications
- Advanced anomaly detection algorithms
- Distributed log ingestion
- Role-based authentication and authorization
- Historical incident analytics
- Support for additional AI models
- Kubernetes deployment
- Cloud deployment

---

# 👥 Project Type

**Academic / Final Year Project**

**Project:** IntelliOps – Intelligent Log Analysis Platform

**Domain:** Artificial Intelligence + Backend Engineering + DevOps

**Primary Technologies:** Java, Spring Boot, PostgreSQL, Redis, React, Generative AI

---

# 📌 Conclusion

IntelliOps combines traditional backend engineering, distributed log processing, vector search, anomaly detection, and Generative AI into a single platform for intelligent incident analysis.

The system aims to move application troubleshooting from:

```text
Manual Log Searching
        ↓
Manual Error Identification
        ↓
Manual Root Cause Investigation
```

to:

```text
Automated Log Processing
        ↓
Semantic Search
        ↓
Anomaly Detection
        ↓
AI Root Cause Analysis
        ↓
Actionable Incident Insights
```

This makes IntelliOps a practical platform for improving the speed and efficiency of application troubleshooting and incident management.
