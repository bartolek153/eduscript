DOCKER_COMPOSE = docker-compose -f docker-compose.yml
MVN = mvn -B
MINIKUBE = minikube

.PHONY: all up build package minikube start

all: up build package minikube

up:
	@echo "🔄 Starting Docker Compose..."
	$(DOCKER_COMPOSE) up -d

build:
	@echo "🛠️  Compiling with Maven..."
	$(MVN) compile

package:
	@echo "📦 Packaging with Maven..."
	$(MVN) clean package

minikube:
	@echo "🚀 Starting Minikube..."
	$(MINIKUBE) start --driver docker --static-ip 192.168.200.200

stop:
	@echo "🛑 Stopping Docker Compose and Minikube..."
	$(DOCKER_COMPOSE) down
	$(MINIKUBE) stop

clean:
	@echo "🧹 Cleaning Maven and Docker..."
	$(MVN) clean
	$(DOCKER_COMPOSE) down -v --remove-orphans
