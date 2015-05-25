TOPDIR?=$(shell pwd)
SHELL=/bin/bash
LOGDIR?=${TOPDIR}/logs
SYMFONY_DIR=${TOPDIR}/symfony

USER=$(shell whoami)

MAKE:=$(MAKE) -s

DOMAIN=devarias.com
PROJECT_HOST:=project.${DOMAIN}
BIO_HOST:=bio.${DOMAIN}
ES_HOST:=db.${DOMAIN}

HTTP_PORT=8080
ES_PORT=9200

ARCH=$(shell uname -s)

all: env-start

env-start:
	@echo -e "\\033[1;35m+++ Starting development environment\\033[39;0m"
	@docker-compose up -d

env-stop: 
	@echo -e "\\033[1;34m+++ Stopping development environment\\033[39;0m"
	@docker-compose stop

clean: env-stop cleanlogs

# Run the behat tests.
test:
	@$(MAKE) clean ;\
	$(MAKE) env-start
	$(MAKE) test-frontend

test-frontend:
	@(cd ${TOPDIR}/symfony && bin/behat --format pretty,junit --out ,junit)

info:
	@echo -e "\\033[1;35m Scraper UI\\033[39;0m: http://$(PROJECT_HOST):$(HTTP_PORT)"
	@echo -e "\\033[1;35m Data UI\\033[39;0m: http://$(PROJECT_HOST):$(HTTP_PORT)"
	@echo -e "\\033[1;35m Bio UI\\033[39;0m: http://$(BIO_HOST):$(HTTP_PORT)"
	@echo -e "\\033[1;35m Elastic Search\\033[39;0m: http://$(ES_HOST):$(ES_PORT)"
	@echo

help:
	@echo "make all - create and bring up environment"
	@echo "make info - list ports and commands to access the environment"
	@echo "------"
	@echo "make test - Clean and run tests"

cleanlogs:
	@for file in $(shell ls $(LOGDIR)) ; do > $(LOGDIR)/$$file ; done

# To be used if we have way too many images and containers unused: use --rm as much as possible when containers are not meant to persist
docker-cleanup:
	@docker stop $(shell docker ps -a -q) || exit 0
	@docker rm -f $(shell docker ps -a -q) || exit 0
	@docker rmi -f $(shell docker images -a -q) || exit 0

docker-compose-rebuild:
	@echo -e "\\033[1;35m+++ Rebuilding docker images with docker-compose\\033[39;0m"
	@docker-compose build

# Targets for easier life
cluster-start:
	@${TOPDIR}/vagrant/snapshot.sh go 1 3 RUNNING_SSH_SETUP

cluster-stop:
	@(cd ${TOPDIR}/vagrant && vagrant suspend)

composer-update:
	(cd ${SYMFONY_DIR} && composer update)
