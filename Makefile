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
AMBARI_MASTER=c6401.ambari.apache.org:8080

ARCH=$(shell uname -s)

all: help info

env-start:
	@echo -e "${GREENCOLOR}+++ Starting development environment${ENDCOLOR}" 
	@docker-compose up -d
	@$(MAKE) composer-update

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

# Run the behat tests...faster ;)
test-fast:
	$(MAKE) test-frontend

test-frontend:
	(cd ${TOPDIR}/symfony && bin/behat --format pretty --suite=project --profile=project)
	(cd ${TOPDIR}/symfony && bin/behat --format pretty --suite=bio --profile=bio)

info:
	@echo -e "\n${GREENCOLOR}Useful links${ENDCOLOR}:"
	@echo -e "${BLUECOLOR}Scraper UI${ENDCOLOR}:\t\thttp://$(PROJECT_HOST):$(HTTP_PORT)/scraper"
	@echo -e "${BLUECOLOR}Data UI${ENDCOLOR}:\t\thttp://$(PROJECT_HOST):$(HTTP_PORT)/data"
	@echo -e "${BLUECOLOR}Bio UI${ENDCOLOR}:\t\t\thttp://$(BIO_HOST):$(HTTP_PORT)"
	@echo -e "${BLUECOLOR}Elastic Search${ENDCOLOR}:\t\thttp://$(ES_MASTER):$(ES_PORT)"
	@echo -e "${BLUECOLOR}Ambari panel${ENDCOLOR}:\t\thttp://${AMBARI_MASTER}"

help:
	@echo -e "\n${GREENCOLOR}Useful targets${ENDCOLOR}:"
	@echo -e "${BLUECOLOR}make full-start${ENDCOLOR} - create an environment with a 3-node Hadoop cluster"
	@echo -e "${BLUECOLOR}make full-stop${ENDCOLOR} - create an environment with a 3-node Hadoop cluster"
	@echo -e "${BLUECOLOR}make env-start${ENDCOLOR} - create and bring up environment"
	@echo -e "${BLUECOLOR}make env-restart${ENDCOLOR} - reset environment"
	@echo -e "${BLUECOLOR}make clean${ENDCOLOR} - Stop env and clean logs"
	@echo -e "${BLUECOLOR}make info${ENDCOLOR} - list ports and commands to access the environment"
	@echo -e "${BLUECOLOR}make test${ENDCOLOR} - Clean and run tests"
	@echo -e "${BLUECOLOR}make test-fast${ENDCOLOR} - Clean and run tests w/o rebuilding environment"
	@echo -e "---------------------------------------"
	@echo -e "${BLUECOLOR}make docker-cleanup${ENDCOLOR} - Delete all Docker images and containers"
	@echo -e "${BLUECOLOR}make docker-compose-rebuild${ENDCOLOR} - Recreate images in docker-compose.yml"
	@echo -e "---------------------------------------"
	@echo -e "${BLUECOLOR}make cluster-start${ENDCOLOR} - Start the 3-node Ambari cluster from spanpshot"
	@echo -e "${BLUECOLOR}make cluster-stop${ENDCOLOR} - Leave the cluster in a suspended state"
	@echo -e "---------------------------------------"
	@echo -e "${BLUECOLOR}make symfony-cache-clear${ENDCOLOR} - Clear symfony cache"
	@echo -e "${BLUECOLOR}make symfony-assets-install${ENDCOLOR} - Install ALL Bundles' assets as under 'web'"
	@echo -e "${BLUECOLOR}make composer-update${ENDCOLOR} - Updates all symfony's libraries"
	@echo -e "---------------------------------------"
	@echo -e "${BLUECOLOR}make prod-launch${ENDCOLOR} - To be used in production ONLY"

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

full-start: env-start cluster-start

full-stop:	env-stop cluster-stop

symfony-cache-clear:
	(cd ${SYMFONY_DIR} && php app/console cache:clear)

symfony-assets-install:
	(cd ${SYMFONY_DIR} && php app/console assets:install)

composer-update:
	(cd ${SYMFONY_DIR} && composer update)

# Includes
include ${TOPDIR}/vagrant/build.mk
include ${TOPDIR}/elastic/build.mk
