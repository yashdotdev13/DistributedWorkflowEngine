# Design Decisions

## Why Event-Driven?
To decouple services and improve scalability.

## Why Kafka?
Reliable event streaming with at-least-once delivery.

## Why Stateless Scheduler?
Easier scaling and recovery.

## Why DB-driven DAG evaluation?
Simplifies dependency resolution using SQL instead of in-memory graph traversal.