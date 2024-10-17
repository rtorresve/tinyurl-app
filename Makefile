.PHONY: build up run-local test clean-install ha-setting down logs createsuperuser
build:
	docker-compose build

up: ha-setting
	docker-compose up -d

run-local:
	mvn spring-boot:run

test:
	mvn test

clean-install:
	mvn clean install -DskipTests

ha-setting:
	./docker/haproxy/config/ha-settings.sh

down:
	docker-compose  down --volumes --rmi all

logs:
	docker-compose  logs -f

createsuperuser:
	docker-compose run tinyurl_admin python manage.py migrate
	docker-compose run tinyurl_admin python manage.py createsuperuser
