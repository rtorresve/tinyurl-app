.PHONY:
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

remove:
	docker-compose  down --volumes --rmi all

down:
	docker-compose  down
	
logs:
	docker-compose  logs -f

restart:
	docker-compose restart

createsuperuser:
	docker-compose run tinyurl_admin python manage.py migrate
	docker-compose run tinyurl_admin python manage.py createsuperuser
