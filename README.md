Portfolio/Project
==================

This Symfony 2.6 project contains the Bootstrap templates for:

* A personal portfolio project, courtesy of the [Agency][2] template.
* A personal project page, based on Bootstrap's [Jumbotron][3] template.

## The author

Me!

## Routing
In order to work with these, I've decided to go for subdomain routing, both in 
production and in development, so you need to add proper "bio.*" and "project.*" entries
in your Hosting and in your ```/etc/hosts```, along with the proper _domain_ variable in the parameters configuration.

For details on how to download and get started with Symfony, see the
[Installation][1] chapter of the Symfony Documentation.

## Docker 
An Apache server in Ubuntu has been configured as a Docker container, ready to be launched and used, 
with support for live code editing, just like the embedded symfony server.

Since the plan is to have more containers as the project grows, I've started using [Docker composer][5]

You can find details here on how to install [docker][4] and [composer][6]. They're not complicated to setup, 
If you follow my recomendation you'd stay away from Python packages and Ubuntu defaults, and stick to the links
in the docker site.

### boot2docker
Support pending, it *should* work, now that I'm using a data volume; but it's a tricky beast.

## Future plans

* Adding some nice graphs using d3.js
* Content!

[1]:  http://symfony.com/doc/2.6/book/installation.html
[2]:  http://startbootstrap.com/template-overviews/agency/  
[3]:  http://getbootstrap.com/examples/jumbotron/
[4]:  https://docs.docker.com/installation/ubuntulinux/
[5]:  https://github.com/docker/compose/
[6]:  https://docs.docker.com/compose/install/
