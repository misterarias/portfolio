#!/usr/bin/env bash

cd /etc/yum.repos.d/
wget http://public-repo-1.hortonworks.com/ambari/centos6/2.x/updates/2.0.1/ambari.repo

sudo yum update --disablerepo="*" --enablerepo="Updates-ambari-2.0.1"
yum install ambari-server -y
ambari-server setup -s
ambari-server start
