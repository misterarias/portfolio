FROM ubuntu:trusty

# Install stuff  as soon as possible
RUN apt-get update 
RUN apt-get install -y curl php5 php5-intl vim
#RUN (curl -sS https://getcomposer.org/installer | php) && mv composer.phar /usr/local/bin/composer

RUN mkdir -p /var/www/symfony/web
# OSX mounting madness :(
RUN usermod -a -G staff www-data

COPY apache.conf /etc/apache2/sites-available/

RUN a2dissite 000-default
RUN a2ensite apache
RUN a2enmod rewrite
EXPOSE 80

RUN service apache2 restart
# Prepare symfony to run
# This should also install dev assets and warmup cache
#RUN (cd /var/www/symfony && composer install)
