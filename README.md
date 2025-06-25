# Eduscript Compiler

## Index

1. Features

## Features

* ANTLR4 Grammar
* Javadoc
* Maven modules
* Kafka
* STOMP (ws)
* Redis

## Todo

### Backend

* [x] Setup gRPC
* [x] Receive `job` requests with gRPC
* [x] Handle anonymous user auth (header - random user ID)

* [x] Setup Kafka
* [x] Implement backend `job` producer
* [x] Implement worker `job` consumer
* [x] Implement worker `log` asynchronous batch producer (ExecutorService)
* [x] Implement backend `log` consumer

* [x] Setup WebSockets
* [x] Handle anonymous user auth on handshake (cookie - random user ID)
* [x] Deliver logs via WebSocket to user
* [ ] Keep user session metadata up-to-date if a reconnection happens

* [x] Setup Redis
* [x] Handle/store user session and job metadata
* [x] Handle replica identification
* [x] Implement message forwarding in backend with gRPC (replicas -> correct user session connection)
* [x] Handle `job` cancellation with redis flag
* [ ] Add redis TTL (where needed)

* [x] Setup Prometheus
* [ ] Send backend/worker metrics

---

### DSL

* [x] Create ANTLR grammar <<
* [x] Parse and load execution plan
* [x] Setup Kubernetes cluster (Minikube)
* [x] Call Kubernetes client API during `job` processing
* [x] Track logs (broker)

---

### Infrastructure

* [ ] Config kubernetes readiness probe. Assure kafka partitions are assigned to workers
* [ ] Add CI jobs (backend/worker)
* [ ] Setup HAProxy
* [ ] Add TLS termination
* [ ] Configure load balancer
* [ ] Configure proxy sticky session
* [ ] Configure proxy rate limiting
* [ ] Integrate system components to send metrics to observability system (broker, cache, etc.)
* [ ] Integrate proxy metrics with observability system (latency, errors, etc.)
* [ ] Configure Kubernetes + Prometheus
* [ ] Configure Grafana panel
* [ ] Create k6 tests and generate reports

---

### Enhancements/Refactors

* [ ] Implement job scheduler
* [ ] Change the project name
* [ ] Change `common` module name to `lib` | `core` to `dsl`
* [ ] Add proper error handling
* [ ] Persist job, user and history info with MongoDB
* [ ] Create priority job queueing
* [ ] Add real user management, user web UI and AdminOps panel
* [ ] Centralized logging (ELK or Loki)
* [ ] Setup Helm
* [ ] Build DevContainer setup or Docker Compose for contributors
* [ ] Add Terraform script for provisioning optional cloud infra (bonus points)
* [ ] Split `job` topics in `job-requests`, `job-events`, `job-logs` and `job-results`

### Documentation

* [ ] Build README
* [ ] Write a blog-style system design explanation
* [ ] Include a “demo mode” with fake workers so people can try it locally
* [ ] Add screenshots/GIFs of the UI in action
* [ ] Create JavaDocs
* [ ] Draw app flow diagram
* [ ] Draw infra diagram

## Metrics

### Broker

* Consumer lag time
* Broker resource utilization (CPU/Memory)
* Topic and partition metrics (msgs produced, msgs consumed)

### Cache

### Web Server

### Kubernetes

### HAProxy / Load Balancer

### WebSocket Server

### Job Execution Logic
