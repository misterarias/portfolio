FROM ubuntu:trusty

ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update && \
	apt-get install -y wget openjdk-7-jre xvfb firefox

RUN wget http://selenium-release.storage.googleapis.com/2.46/selenium-server-standalone-2.46.0.jar

ADD start.sh .
