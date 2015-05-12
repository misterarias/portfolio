#!/bin/bash

usermod -u 1000 www-data
chown -R www-data:www-data /var/www/symfony/app/cache
chown -R www-data:www-data /var/www/symfony/app/logs

service apache2 stop
/usr/sbin/apache2ctl -D FOREGROUND
