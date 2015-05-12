FROM ubuntu:trusty

ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update && apt-get install -y \
    php5-json \
    php5-intl \
	apache2 php5 vim \
    && apt-get clean

COPY start.sh .
COPY apache.conf /etc/apache2/sites-available/

RUN a2dissite 000-default
RUN a2ensite apache
RUN a2enmod rewrite

RUN sed -i /etc/php5/cli/php.ini -e 's/;date.timezone.*/date.timezone = Europe\/Madrid/g'
