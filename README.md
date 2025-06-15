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
* [ ] Handle anonymous user auth (header - random user ID)

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
* [ ] Create `log` (in-memory/DLQ ?) buffer for retries (reconnections)
* [ ] Handle `job` cancellation with redis flag
* [ ] Set redis TTL handling
* [ ] Add deduplication lock
* [ ] Implement backpressure support (buffer filling?)

* [ ] Setup Prometheus and OTEL collector/SDK
* [ ] Send backend/worker metrics

---

### DSL

* [ ] Create ANTLR grammar
* [ ] Parse and load execution plan
* [ ] Setup Kubernetes cluster (Minikube)
* [ ] Call Kubernetes client API during `job` processing
* [ ] Track logs (broker)

---

### Infrastructure

* [ ] Add CI jobs (backend/worker)
* [ ] Setup HAProxy
* [ ] Add TLS termination
* [ ] Configure load balancer
* [ ] Configure proxy sticky session
* [ ] Configure proxy rate limiting
* [ ] Integrate system components to send metrics to observability system (broker, cache, etc.)
* [ ] Integrate proxy metrics with observability system (latency, errors, etc.)
* [ ] Configure Grafana panel
* [ ] Create k6 tests and generate reports

---

### Enhancements/Refactors

* [ ] Change the project name
* [ ] Change `common` module name to `lib` (?)
* [ ] Add proper error handling
* [ ] Persist job and history info
* [ ] Create priority job queueing
* [ ] Add real user management, user web UI and AdminOps panel
* [ ] Centralized logging (ELK or Loki)
* [ ] Setup Helm
* [ ] Build DevContainer setup or Docker Compose for contributors
* [ ] Add Terraform script for provisioning optional cloud infra (bonus points)

### Documentation

* [ ] Build README
* [ ] Write a blog-style system design explanation
* [ ] Include a “demo mode” with fake workers so people can try it locally
* [ ] Add screenshots/GIFs of the UI in action
* [ ] Create JavaDocs
* [ ] Draw app flow diagram
* [ ] Draw infra diagram
