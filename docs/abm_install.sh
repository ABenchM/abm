#!/usr/bin/env bash

$HERMES_CONFIG_DIR=${HERMES_CONFIG_DIR:-/opt/abm}


echo ABM INSTALLATION >> log.txt

echo Cloning git ABM repository >> log.txt

git clone -b master https://github.com/nguyenLisa/abm.git &&

mysqlpass=${mysqlpass:-password}

sudo apt-get update &&

#This script installs mysql (latest build)
#Install MYSQL Server

export DEBIAN_FRONTEND=noninteractive &&
sudo debconf-set-selections <<< 'mysql-server mysql-server/root_password password password' &&
sudo debconf-set-selections <<< 'mysql-server mysql-server/root_password_again password password' &&
sudo apt-get -y install mysql-server &&

#Restart
/usr/bin/mysql_secure_installation &&
sudo service mysql restart &&
echo "MySQL Installation and Configuration is Complete." >> log.txt

#MySql Installation 

echo Creating ABM Database >> log.txt

mysql_config_editor set --login-path=abm --host=localhost --user=root --password &&

echo 'alias myabm="mysql --login-path=abm"' >> ~/.bashrc  &&

source ~/.bashrc &&

mysql --login-path=abm <<MYSQL_SCRIPT

create database abm;

MYSQL_SCRIPT

echo Creating ABM tables  >> log.txt

mysql --login-path=abm -D abm < abm/bnd-workspace/de.fraunhofer.abm.useradmin.dao.jdbc/useradmin_ddl.sql &&

mysql --login-path=abm -D abm < abm/docs/abm.sql &&


##Docker Installation 

echo Starting Docker Installation >> log.txt
echo Installing curl >> log.txt

sudo apt-get install curl &&

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add - &&

echo Adding docker into repository >> log.txt

sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" &&

echo updating the repository >> log.txt

sudo apt-get update &&

echo Installing docker >> log.txt

sudo apt-get install -y docker-ce &&

echo Starting Docker >> log.txt

zero=0

sudo systemctl status docker >> log.txt  &&

if [ $? -eq $zero ]; then

echo Adding user to docker group >> log.txt

sudo usermod -aG docker ${USER} &&

echo logging out from the current user >>log.txt

su - ${USER} <<EOSU

echo checking the user is there in docker group >> log.txt

id -nG 

EOSU

echo Adding ABM Docker volumes >> log.txt

docker volume create --name IVY_REPO -d local &&

docker volume create --name M2_REPO -d local 
 
fi

echo Installing Hermes Docker Image >> log.txt

docker pull opalj/sbt_scala_javafx &&


echo Installing Maven docker image >> log.txt

cd abm/docker_files/abm-maven-3-jdk-7 ; docker build -t abm/maven:3-jdk-7 . &&

echo Installing sbt docker image >> log.txt

cd ../abm-sbt-0.13.13-jdk-8 ; docker build -t abm/sbt:01313-jdk-8 . &&

echo Installing Java 8 >> log.txt

cd ../../../ ; sudo apt-get install default-jdk &&

echo creating Hermes configuration directory >> log.txt

sudo mkdir $HERMES_CONFIG_DIR &&

sudo mv abm/hermes_config/*  $HERMES_CONFIG_DIR/  &&

echo Installing Eclipse >> log.txt

wget -O eclipse.tar.gz https://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/oxygen/2/eclipse-jee-oxygen-2-linux-gtk-x86_64.tar.gz\&r=1 &&

tar -xvf eclipse.tar.gz &&

sudo mv eclipse /opt &&

echo "Adding entry in eclipse.desktop" >> log.txt

cat >eclipse.desktop  <<EOL

[Desktop Entry]

Name=Eclipse

Type=Application

Exec=/opt/eclipse/eclipse

Terminal=false

Icon=/opt/eclipse/icon.xpm

Comment=Integrated Development Environment

NoDisplay=false

Categories=Development;IDE;

Name[en]=eclipse.desktop

EOL

sudo mv eclipse.desktop /usr/share/applications/  &&

sudo desktop-file-install /usr/share/applications/eclipse.desktop &&

echo ABM INSTALLation finished >> log.txt


