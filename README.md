# 🧠 Distributed Workflow Engine (Airflow-inspired)

A fully event-driven workflow orchestration system built to understand how distributed schedulers like Airflow work under the hood.

---

## 🚀 Overview

This system executes workflows defined as **Directed Acyclic Graphs (DAGs)** using a decoupled, event-driven architecture.

Instead of tightly coupled services, execution is driven by **state transitions and Kafka events**.

---

## ⚙️ Architecture

```
Workflow Service → Kafka → Scheduler → Kafka → Worker
                                      ↑
                                (task-completed)
```

---

## 🧩 Core Components

### 1. Workflow Service

* Creates workflows and tasks
* Defines DAG (task dependencies)
* Publishes `workflow-started` event

---

### 2. Scheduler Service (Decision Engine)

* Consumes `workflow-started`
* Finds executable tasks using DAG constraints
* Marks tasks as READY
* Publishes `task-ready` events
* Handles `task-completed` to trigger next tasks
* Detects workflow completion

---

### 3. Worker Service (Execution Engine)

* Consumes `task-ready`
* Executes tasks
* Updates status (RUNNING → SUCCESS / FAILED)
* Publishes `task-completed`
* Ensures idempotent execution

---

### 4. Common Libs

* Shared event contracts
* DTOs for inter-service communication

---

## 🔁 Execution Flow

1. Workflow is started
2. Scheduler identifies root tasks
3. Tasks are sent to workers via Kafka
4. Workers execute tasks
5. Completion events trigger next tasks
6. Loop continues until workflow completes

---

## 🧠 Key Design Concepts

### 🔹 DAG-based Execution

Tasks execute only when all dependencies are satisfied.

---

### 🔹 Event-Driven Architecture

No direct service-to-service calls.
Everything is coordinated via Kafka.

---

### 🔹 Idempotency

Prevents duplicate task execution due to Kafka re-delivery.

---

### 🔹 Fault Tolerance

* Retry handling implemented
* Failures are isolated at task level

---

### 🔹 Stateless Scheduler

All execution state is stored in the database.

---

## 💡 Example DAG

```
fetch-order → process-payment → send-notification
```

---

## ⚡ Tech Stack

* Java 21
* Spring Boot
* Apache Kafka (KRaft)
* MySQL
* Docker

---

## 🚀 How to Run

### 1. Start Kafka

```bash
docker-compose up -d
```

---

### 2. Run Services

* workflow-service
* scheduler-service
* worker-service

---

### 3. Create Workflow via API

```
POST /api/workflows
POST /api/tasks
POST /api/dependencies
```

---

### 4. Start Workflow

```
POST /api/workflows/{id}/start
```

---

## 📊 Features

✔ Event-driven orchestration
✔ DAG-based execution
✔ Retry + failure handling
✔ Idempotent task execution
✔ Workflow completion detection

---

## 🚧 Future Improvements

* Dead Letter Queue (DLQ)
* Task timeouts
* Distributed tracing
* Real task execution (API calls, jobs)
* UI dashboard

---

## 🧠 What I Learned

This project helped me understand:

* How schedulers actually work internally
* Difference between execution and orchestration
* Designing for failure in distributed systems
* Event-driven system design

---

## 📌 Author

Built as a deep dive into distributed systems and orchestration engines.
