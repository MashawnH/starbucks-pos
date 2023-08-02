all: clean

clean:
	mvn clean

compile:
	mvn compile

run: compile
	mvn spring-boot:run

run-local: compile
	mvn spring-boot:run -Dspring-boot.run.profiles=local


build:
	mvn package

build-skip:
	mvn package -Dmaven.test.skip



# MySQL DB

mysql:
	docker run --platform=linux/amd64 -d --network starbucks --name mysql -td -p 3306:3306 -e MYSQL_ROOT_PASSWORD=cmpe172 mysql:8.0

mysql-shell:
	docker exec -it mysql bash


# Redis DB

redis-local:
	docker run --platform=linux/amd64 --name redis --network starbucks -td -p 6379:6379 redis

redis-official:
	docker run --platform=linux/amd64 --name redis --network starbucks -td -p 6379:6379 redis:4.0

redis-shell:
	docker exec -it redis bash


# Jumpbox

jumpbox:
	docker run --platform=linux/amd64 --network starbucks --name jumpbox -t -d ubuntu

jumpbox-shell:
	docker exec -it jumpbox bash


# Docker

docker-build: docker-clean build
	docker build --platform=linux/amd64 -t spring-cashier .
	docker images

starbucks-network:
	docker network create --driver bridge starbucks

docker-run: docker-build
	docker run --platform=linux/amd64 --network starbucks -e "MYSQL_HOST=mysql" --name spring-cashier -td -p 8080:8080 spring-cashier

docker-clean:
	docker rmi spring-cashier

docker-shell:
	docker exec -it spring-cashier bash

docker-push:
	docker login
	docker build -t mashawnh/spring-cashier:latest .
	docker push mashawnh/spring-cashier:latest

#local pre-setup
local: mysql redis-local

#compose-up pre-setup
local-up: mysql-up redis-up

# Compose

network-ls:
	docker network ls

network-create:
	docker network create --driver bridge $(network)

network-prune:
	docker network prune

compose-up:
	docker-compose up --scale starbucks=2 -d

lb-up:
	docker-compose up -d lb

starbucks-up:
	docker-compose up -d starbucks

redis-up:
	docker-compose up -d redis

mysql-up:
	docker-compose up -d mysql

jumpbox-up:
	docker-compose up -d jumpbox

compose-down:
	docker-compose down

lb-stats:
	echo "user = admin | password = admin"
	open http://localhost:1936

lb-test:
	open http://localhost

# RabbitMQ

rabbit-shell:
	docker exec -it rabbitmq bash

rabbit:
	docker run --name rabbitmq \
	           --network starbucks \
			   -p 9091:15672 -p 4369:4369 -p 5672:5672 \
			   -d rabbitmq:3-management

rabbit-console:
	open http://localhost:9091

# Management Console: http://localhost:9091
# username and password of guest / guest:


