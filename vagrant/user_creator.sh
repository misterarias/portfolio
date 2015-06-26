USER=vagrant
sudo adduser ${USER} > /dev/null 2>&1
sudo -u hdfs hdfs dfs -mkdir -p /user/${USER}
sudo -u hdfs hdfs dfs -chown ${USER}:${USER} /user/${USER}
