The GDELT Project - a Big Data study
=================================

## The author

[Juan Arias][8], I'm a Senior Software developer and Big Data wannabe, I code for fun and profit at Schibsted Classified Media.

## Description

The project page can be found [here][9], all the gory and boring details are there. This file describes the structure of the repository.

In order to manage my way around everything, I've added a Makefile with some targets to make my life easier, just type `make` on the root dir and you'll get them all.

### Web Project

The web page I've created for this project is a [Symfony 2.0][1] project, powered by Bootstrap templates and jQuery. Some features include:

* pretty basic Behat testing for the modules created,
* support for subdomain-level routing (even if it's unused, there's another page hosted in the same project)
* Reusable architecture, just remove the text, and start another webpage!
* Responsive design thanks to [Bootstrap][3] and some neat tricks.
* As most Symfony/PHP projects, libraries are managed using [composer][6].

#### Routing
In order to work with two pages at the same time, I've decided to go for subdomain routing, both in production and in development, so you need to add proper "bio.*" and "project.*" entries
in your Hosting and in your `/etc/hosts`,
```
127.0.0.1	project.devarias.com bio.devarias.com web.devarias.com master.devarias.com

```
along with the proper _domain_ variable in the parameters configuration.

For details on how to download and get started with Symfony, see the
[Installation][1] chapter of the Symfony Documentation.

### Docker Compose

Since the plan was to have more containers as the project grew, I've added a [Docker composer][5] file to manage them all

Currently it contains:

* An Apache server in Ubuntu ready to be launched and used,
with support for live code editing, just like the embedded symfony server.
* A Selenium server, linked to that Apache server, in order to run the tests without installing Selenium and PHPUnit on your computer (just don't)
* The [official docker image][7] for Elastic Search, in a setup with one master and two slaves.
* A Kibana server, linked to the above

#### Installation
You can find details here on how to install Docker [here][4].
If you follow my recommendation you'd stay away from Python packages and Ubuntu defaults, and stick to the links
in the docker site.

#### boot2docker support
It *should* work, but it's a tricky beast. Due to how boot2docker handles volumes, you are going to have problems when dumping assets with Symfony if you don't use static routes to them as in:
```
{% javascripts
  '@AFProjectBundle/Resources/public/js/navbarShrink.js'
  '@AFProjectBundle/Resources/public/js/project.js' %}
  <script src="{{ asset_url }}"></script>
  {% endjavascripts %}

```
Go figure.

### Ambari Cluster

Contains the Vagrantfile needed to create and configure a 3-node Ambari cluster.

Current specs for the machines are 3 core, 4GB of RAM Centos6 servers, so take care when doing `vagrant up` as you might come back and find your computer unusable.

Once the machines have been booted up and provisioned, you have to add the following entries to your `/etc/hosts` file:
```
# centos 6.4 hosts
192.168.64.101 c6401.ambari.apache.org c6401
192.168.64.102 c6402.ambari.apache.org c6402
192.168.64.103 c6403.ambari.apache.org c6403
```

Then you can navigate to c6401.ambari.apache.org, login as _ambari/ambari_  and configure your new cluster.

**Note** I've added code to snapshot current VM state, so it's much, MUCH faster to startup each time after the first. It's also super useful, since it's quite easy to screw up the cluster, and it makes it trivial to go back in time (but research first!!!)

### Spark project

Contains the Scala code for my Topic Modeling study, as described in the web of the project.

Uses SBT for managing the build system instead of Maven, in order to play with a new technology that was supposed to be Simple.

Adds the `sbt assembly` plugin in order to deploy complete JAR files to the cluster, as needed by Spark.

#### Configure the cluster

In the `docs/spark` folder I've added the configuration for Spark to be able to submit jobs in cluster mode to the Ambari cluster.

Make sure that the Spark distribution you download has embedded hadoop 2.6 jars, copy (and adapt if needed) the configuration to `$SPARK_HOME/conf` and try to connect with spark-shell to it.

#### Connect to Elastic Search

The Spark code assumes that Elastic search is running on the same machine, on the usual ports 9200/9300. This is true if you run everything from this project as it is, but since there is no configuration file yet, if you want to connect to a different ES instance you need to modify the code.


## Future plans

* Add configuration files.
* Add more controls to d3.js
* Add better launching scripts for the cluster
* Add AWS support to Spark scripts
* Orchestrate the AWS cluster from scripts using Packer and Vagrant

[1]:  http://symfony.com/doc/2.6/book/installation.html
[2]:  http://startbootstrap.com/template-overviews/agency/  
[3]:  http://getbootstrap.com/examples/jumbotron/
[4]:  https://docs.docker.com/installation/ubuntulinux/
[5]:  https://github.com/docker/compose/
[6]:  https://docs.docker.com/compose/install/
[7]:  https://registry.hub.docker.com/_/elasticsearch/
[8]:  https://blog.ariasfreire.com
[9]:  http://project.ariasfreire.com
