# Quick access
* [Install ABM](#install-abm)
  * [Installation scripts (Linux only)](#installation-scripts-linux-only)
  * [From the source code (Linux and MacOS)](#from-the-source-code-linux-and-macos)
    * [Set up the ABM workspace](#set-up-the-abm-workspace)
    * [Set up Docker](#set-up-docker)
    * [Set up the database](#set-up-the-database)
    * [Set up Eclipse](#set-up-eclipse)
    * [Launch ABM](#launch-abm)
    * [Create the demo user](#create-the-demo-user)
* [Configuration files](#configuration-files)
* [Launching or bouncing the application on production server](#launching-or-bouncing-the-application-on-production-server)
* [Running Hermes manually](#running-hermes-manually)
* [Updating the database](#updating-the-database)
* [Changing email notification settings](#changing-email-notification-settings)

# Install ABM

## Installation scripts (Linux only)
* Run the script found at abm/docs/abm_install.sh to install ABM.
  ```
  $ sudo chmod 755 abm_install.sh
  $ sudo ./abm_install.sh
  ```
* You will be prompted for the mysql root password. It is "password". You can modify it afterwards.
* Once the installation finishes, log out and log in again.
* Run the script again to finish the installation.
* Make sure that the [configuration files](#configuration-files) of ABM contain the correct information.
* You can [launch ABM](#launch-abm).
  
## From the source code (Linux and MacOS)
The installation of ABM is not recommended on Windows. We advise Windows users to dual boot their installation with Linux or to use a VM. Note that ABM tends to be a bit slow on a VM, depending on the capabilities of the host machine.

### Set up the ABM workspace
* Install a package manager
  * Linux: apt-get
  ```
  $ sudo apt-get install
  $ sudo apt-get update
  ```
  * MacOS: Homebrew (https://brew.sh/)
  ```
  $ /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
  ```
* Install Java
  * Linux
  ```
  $ sudo apt-get install default-jre
  $ sudo apt-get install default-jdk
  ```
  * MacOS `$ brew cask install java`
* Clone the ABM repository in the directory of your choice: ${DIRECTORY}
  ```
  $ cd ${DIRECTORY}
  $ git clone https://github.com/nguyenLisa/abm.git
  ```
* Copy the files contained in ${DIRECTORY}/abm/hermes_config into /opt/abm and give your user ${USER} read, write, and execute rights on them.
  ```
  $ sudo mkdir /opt/abm
  $ sudo cp ${DIRECTORY}/abm/hermes_config/* /opt/abm
  $ sudo chown -R ${USER}:${USER} /opt/abm
  ```
* Create the ABM workspace and give your user ${USER} read, write, and execute rights on them.
  ```
  $ sudo mkdir /var/lib/abm/
  $ sudo mkdir /var/lib/abm/repo
  $ sudo mkdir /var/lib/abm/workspace
  $ chown -R ${USER}:${USER} /var/lib/abm
  ```  
  
### Set up Docker
* Download and install Docker Community Edition
  * Linux
  ```
  $ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
  $ sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
  $ sudo apt-get install -y docker-ce
  ```
  * MacOS `$ brew install docker`
* Add your user to the docker group, so that you have access to docker: `$ sudo usermod -aG docker ${USER}`
* Create local volumes:
  ```
  $ docker volume create --name IVY_REPO -d local
  $ docker volume create --name M2_REPO -d local
  ```
* Create the sbt build image:
  ```
  $ cd ${DIRECTORY}/abm/docker_files/abm-sbt-0.13.13-jdk-8
  $ docker build -t abm/sbt:01313-jdk-8 .
  ```
* Create the maven build image:
  ```
  $ cd ${DIRECTORY}/abm/docker_files/abm-maven-3-jdk-7
  $ docker build -t abm/maven:3-jdk-7 .
  ```
* Pull the Hermes image: `$ docker pull opalj/sbt_scala_javafx`
* Verify that the three images are running. You should see abm/sbt, abm/maven, and opalj/sbt_scala_javafx when running `$ docker images`

### Set up the database
* Install MySQL Community Server >= 5.5
  * Linux
  ```
  $ sudo apt-get install mysql-server
  $ mysql_secure_installation
  ```
  * MacOS `$ brew install mysql`
  * **On Linux, you will be prompted for a root password. On MacOS, the root password appears in a popup at the end of the installation. In both cases, note it down.**
* Optional: If you do not want ABM to use the mysql root account, you can create your own account and grant it all privileges.
  ```
  $ mysql -uroot -p
  $ CREATE USER 'newuser'@'localhost' IDENTIFIED BY 'password';
  $ GRANT ALL PRIVILEGES ON * . * TO 'newuser'@'localhost';
  $ FLUSH PRIVILEGES;
  $ exit
  ```
* Create the abm database
  ```
  $ mysql -uroot -p
  $ `create database abm;`
  $ exit
  ```
* Create the useradmin datatables
  ```
  $ cd ${DIRECTORY}/abm/bnd-workspace
  $ mysql -u root -p -D abm < de.fraunhofer.abm.useradmin.dao.jdbc/useradmin_ddl.sql
  ```
  
### Set up Eclipse 
* Download and install Eclipse for **Java Developers** (http://www.eclipse.org/downloads/)
* Install bndtools from the Market Place
* Install Webclipse (Angular) from the Market Place
* Import existing projects
  
* Start Eclipse with the workspace directory set to "abm/eclipse-workspace"
* Install bndtools from the Market Place (Help > Eclipse Marketplace)
* Optional: Install Webclipse (Angular) from the Market Place (Help > Eclipse Marketplace)
* Import the source code of ABM 
  * File > Import > General > Existing projects into Workspace
  * Point the root directory to ${DIRECTORY}/abm/bnd-workspace
  * Select all projects.
  * Uncheck "Copy projects into workspace"
* Wait for the workspace to finish building. If compilation errors appear in the code (except for the test project), they should be solved before continuing.
* Open the file abm/bnd-workspace/de.fraunhofer.abm.collection.dao.jpa/configuration/configuration.json and modify the database configurations: replace the user and password by the mysql user and password that you have created when [installing the database](#set-up-the-database). If you haven't created a user, you can use "root" as the user and the root password as the configuration password.
* Open the file abm/bnd-workspace/de.fraunhofer.abm.app/de.fraunhofer.abm.bndrun and modify the felix.webconsole.username and the felix.webconsole.password
* Generate the rest of the database:
  * Open the file de.fraunhofer.abm.collection.dao.jpa/persistence.xml
  * Uncomment <property name="javax.persistence.schema-generation.database.action" value="drop-and-create" /> (this line enables the database generation from the JPA entities. this has to be done only when the JPA entities change or for the setup)
  * Launch ABM (see [Launch ABM](#launch-abm))
  * Close ABM by clicking on the red square in the Eclipse Console.
  * Comment <property name="javax.persistence.schema-generation.database.action" value="drop-and-create" /> back.

### Launch ABM
* Before launching, make sure that:
  * mysql should be running `service mysql status`
    * If it is not the case, start mysql: `service mysql start` 
  * The docker images for Maven, SBT, and Hermes should show up in `docker images` (abm/maven, abm/sbt, opalj/sbt_scala_javafx). 
    * If this is not the case, set up docker again.
    * If docker is not running, launch it `docker run -it ubuntu`
* Launch the application:
  * Open de.fraunhofer.abm.app/de.fraunhofer.abm.bndrun
  * Click on Run OSGi in the top right corner
  * The website can then be accessed at http://localhost:8080/de.fraunhofer.abm/index.html#/
* **If it is your first time launching the application, close it by clicking on the red square in the Eclipse console, and comment <property name="javax.persistence.schema-generation.database.action" value="drop-and-create" /> back in the file de.fraunhofer.abm.collection.dao.jpa/persistence.xml**

### Create the demo user
All users are managed by the OSGi UserAdmin service, which can be accessed through the system console.
* Launch ABM (see [Launch ABM](#launch-abm))
* Open your browser and access the system console http://localhost:8080/system/console
* The username and password to access the user console are defined in de.fraunhofer.abm.app/de.fraunhofer.abm.bndrun under "Runtime Properties". You have modified them when [setting up Eclipse](#set-up-eclipse) (felix.webconsole.password)
* Go to OSGi -> Users
* Create a group Admin
* Create a group RegisteredUser
* Create a group UserAdmin
* Create a user "demo"
  * Move it to RegisteredUser
  * Select the user "demo"
  * Under credentials, add an entry:
    * Key `password`
    * Type `String`
    * Value `K25eHhV5v/qByG8oTP2VySQ4iPv4ZPFr0Bkf3uTnTwA=$lSghSVxaYoDynPR7B3LChprsbvvYP2M8lEKI9SWH52g=` This is a salted password hash containing the password "demo" if you want to create your own password, you can use the main method of the class Password in de.fraunhofer.abm.security. 
    
* **You are done with the installation. Congratulations!** 

# Configuration files
* The file Configuration.java in de.fraunhofer.abm.suitebuilder contains "Workspace Root" which you can adjust to fit your development machine. Make sure that your ${USER} has read, write, and execute accesses to this directory and its sub-directories.
* The file Configuration.java in de.fraunhofer.abm.repoarchive.local contains "Directory" which you can adjust to fit your development machine. Make sure that your ${USER} has read, write, and execute accesses to this directory and its sub-directories.
* These changes can also be made in the system console under OSGi -> Configuration , but it is not permanent, i.e. they are rest after a restart.
* The file configuration.json in de.fraunhofer.abm.collection.dao.jpa contains the sql credentials that ABM uses to access the tables. Make sure that they match existing mysql credentials.
* The username and password of the web console can be modified in the file de.fraunhofer.abm.bndrun in de.fraunhofer.abm.app (felix.webconsole.username and felix.webconsole.password).

# Launching or bouncing the application on production server
* Install ABM as shown in the installation section
* Go to the /opt/abm
* Run the command in start file residing at that location
* Check the process is running or not through the following command -> ps -ef | grep abm

* Note: In order to avoid the details of the REST APIs to be displayed on the server when an error is created, the Apache configurations (/etc/apache2/sites-enabled/default-ssl.conf) can be modified to add the following lines:
  ```
  ProxyErrorOverride On
  ErrorDocument 500 "Error 500"
  ErrorDocument 404 "Error 404"
  ```

# Running Hermes manually
* Install docker toolbox on windows or Linux as given above.
* Make sure you have configured your docker machine with enough memory as Hermes application requires a good amount of memory.
* You can check the limit of your machine using following command - docker-machine inspect
* You can create your machine using following command to allocate enough memory to your machine.
docker-machine create -d virtualbox --virtualbox-memory 8192 default (you can choose your machine name)
* Command to remove the existing docker-machine 
docker-machine rm default (machine name is default)
* Pull the Docker Opal Image using following command 
docker pull opalj/sbt_scala_javafx (Refer the link :- https://hub.docker.com/r/opalj/sbt_scala_javafx/ in case of any help)
* Run the Docker image using following command
docker run -it --rm opalj/sbt_scala_javafx
* Result of run command will take you to the following prompt 
     root@743e2cf42ff1:~/OPAL#
* run sbt command 
* change the project to OPAL-DeveloperTools using following command - project OPAL-DeveloperTools
* Run the hermes application using following command 
runMain org.opalj.hermes.HermesCLI  -config src/main/resources/hermes.json -statistics $csv.csv (You can give any name you want for CSV file)
* You will get the csv file in DEVELOPING_OPAL/tools directory or can specify the directory where you want.
  
# Updating the Database
While the database is automatically generated during setup, when a installation of ABM is updated some modifications to the local database may be needed.
The changes can be made by reinitilizing the database, but that results in the loss of all data stored on the database.
To avoid this, make the following changes to your local database for each of the new commits you are applying.
* Commit 983a572 (Jun 9, 2017 / Added simple public collection feature)
  * To table "collection":
    * Add column "privateStatus" (type tinyint, default 0)
    * Add column "creation_date" (type datetime)
    * Commit 983a572 (Jun 9, 2017 / Added simple public collection feature)
* Commit f5fa5cd (Jul 5, 2017 / Added Pinning Collections and Simple Build Sidebar)
  * Add new table "collectionPin" with the following colunms:
    * Column "user" (type varchar(255))
    * Column "id" (type varchar(255))
  * Add new table "filterPin" with the following colunms:
    * Column "user" (type varchar(255))
    * Column "id" (type varchar(255))
* Commit 162dff7 (Aug 4, 2017 / Bugfixes and UI changes)
  * Add new table "user" with the following colunms:
    * Column "name" (type varchar(255))
    * Column "password" (type varchar(255))
    * Column "approved" (type tinyint)
* Commit 7364e14 (Nov 27, 2017/ Registration activation)
  * To table "user":
    * Column "token" (type varchar(50))

# Changing email notification settings
All the settings that control the email notification system are at the top of the file EmailConfiguration.java in the package de.fraunhofer.abm.app. You can change these to control the host the program connects to, the email and credentals it uses, and who it notifies when a new account is registered.
