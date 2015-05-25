TOPDIR?=$(shell pwd)
SHELL=/bin/bash
SYMFONY_DIR=${TOPDIR}/symfony
LOGDIR=${TOPDIR}/logs ${SYMFONY_DIR}/logs

USER=$(shell whoami)

MAKE:=$(MAKE) -s

BLUECOLOR=\\033[1;34m
GREENCOLOR=\\033[1;32m
REDCOLOR=\\033[1;31m
ENDCOLOR=\\033[39;0m

DOMAIN=devarias.com
PROJECT_HOST:=project.${DOMAIN}
BIO_HOST:=bio.${DOMAIN}
ES_MASTER:=master.${DOMAIN}
HTTP_PORT=8080
ES_PORT=9200

ARCH=$(shell uname -s)

all: env-start

env-start:
	@echo -e "${GREENCOLOR}+++ Starting development environment${ENDCOLOR}"
	@docker-compose up -d

env-stop: 
	@echo -e "${REDCOLOR}--- Stopping development environment${ENDCOLOR}"
	@docker-compose stop

env-restart:
	@echo -e "${GREENCOLOR}+++ Restarting development environment${ENDCOLOR}"
	@docker-compose stop
	@docker-compose up -d

clean: env-stop cleanlogs

# Run the behat tests.
test:
	@$(MAKE) clean ;\
	$(MAKE) env-start
	$(MAKE) test-frontend

test-frontend:
	@(cd ${TOPDIR}/symfony && bin/behat --format pretty,junit --out ,junit)

info:
	@echo -e "${BLUECOLOR}Scraper UI${ENDCOLOR}: http://$(PROJECT_HOST):$(HTTP_PORT)"
	@echo -e "${BLUECOLOR}Data UI${ENDCOLOR}: http://$(PROJECT_HOST):$(HTTP_PORT)"
	@echo -e "${BLUECOLOR}Bio UI${ENDCOLOR}: http://$(BIO_HOST):$(HTTP_PORT)"
	@echo -e "${BLUECOLOR}Elastic Search${ENDCOLOR}: http://$(ES_MASTER):$(ES_PORT)"

help:
	@echo -e "${BLUECOLOR}make all${ENDCOLOR} - create and bring up environment"
	@echo -e "${BLUECOLOR}make info${ENDCOLOR} - list ports and commands to access the environment"
	@echo -e "${BLUECOLOR}make test${ENDCOLOR} - Clean and run tests"
	@echo -e "---------------------------------------"
	@echo -e "${BLUECOLOR}make docker-cleanup${ENDCOLOR} - Delete all Docker images and containers"
	@echo -e "${BLUECOLOR}make docker-compose-rebuild${ENDCOLOR} - Recreate images in docker-compose.yml"

cleanlogs:
	@for file in $(shell ls $(LOGDIR)) ; do > $(LOGDIR)/$$file ; done

# To be used if we have way too many images and containers unused: use --rm as much as possible when containers are not meant to persist
docker-cleanup:
	@echo "${REDCOLOR}--- Removing ALL docker images${ENDCOLOR}"
	@docker stop $(shell docker ps -a -q) || exit 0
	@docker rm -f $(shell docker ps -a -q) || exit 0
	@docker rmi -f $(shell docker images -a -q) || exit 0

docker-compose-rebuild:
	@echo -e "${GREENCOLOR}+++ Rebuilding docker images with docker-compose${ENDCOLOR}"
	@docker-compose build

# Targets for easier life
cluster-start:
	@${TOPDIR}/vagrant/snapshot.sh go 1 3 RUNNING_SSH_SETUP

cluster-stop:
	@(cd ${TOPDIR}/vagrant && vagrant suspend)

composer-update:
	(cd ${SYMFONY_DIR} && composer update)

# Includes
include ${TOPDIR}/elastic/elastic.mk
