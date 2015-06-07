#!/bin/bash

# Create a display for firefox to connect
Xvfb :1 -screen 0 1024x768x16 &> xvfb.log &
export DISPLAY=:1.0

# Get hosts' IP from the netstat command (it's current gateway)
HOST_IP=$(netstat -nr | grep '^0\.0\.0\.0' | awk '{print $2}')

# Use it with our currently defined subdomains 
echo "$HOST_IP project.devarias.com bio.devarias.com" >> /etc/hosts 

# start a foreground Selenium server on default port (4444)
chmod 755 selenium-server*
xvfb-run java -jar selenium-server*
