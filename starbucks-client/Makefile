all: clean

clean:
	mvn clean

compile:
	mvn compile

run: compile
	mvn spring-boot:run

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

docker-build: build
	docker build --platform=linux/amd64 -t spring-cashier .
	docker images

starbucks-network:
	docker network create --driver bridge starbucks

docker-run: docker-build
	docker run --platform=linux/amd64 --network starbucks -e "MYSQL_HOST=mysql" --name spring-cashier -td -p 8080:8080 spring-cashier

docker-clean:
	docker stop spring-cashier
	docker rm spring-cashier
	docker rmi spring-cashier

docker-shell:
	docker exec -it spring-cashier bash


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


