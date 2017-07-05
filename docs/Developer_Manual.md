# Developer Documentation

## Setting up your workspace
* Download and install Java Development Kit (http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Download and install Eclipse IDE for Java Developers (http://www.eclipse.org/downloads/eclipse-packages/)
* Clone the ABM repository
  * `git clone git@github.com:nguyenLisa/abm.git abm`
  * or with the Eclipse Git client
* Start Eclipse with the workspace directory set to "abm/eclipse-workspace"
* Install bndtools from the Market Place
* Optional: Install Webclipse (Angular) from the Market Place
* Import existing projects -> point to abm/bnd-workspace -> select all projects except de.fraunhofer.abm.collection.dao.jdbc

## Setting up the database
* Install MySQL Community Server >= 5.5 (https://dev.mysql.com/downloads/)
  * root password should be "password" (since this is only a development machine)
  * Create a database abm
	* `mysql -u root -ppassword`
	* `create database abm;`
	* `quit;`
* Create useradmin datatables
  * `cd abm/bnd-workspace`
  * `mysql -u root -ppassword -D abm < de.fraunhofer.abm.useradmin.dao.jdbc/useradmin_ddl.sql`
* Generate the rest of the database
  * In Eclipse:
    * Open de.fraunhofer.abm.collection.dao.jpa/persistence.xml
    * uncomment <property name="javax.persistence.schema-generation.database.action" value="drop-and-create" />
    (this line enables the database generation from the JPA entities. this has to be
    done only when the JPA entities change or for the setup)
    * launch ABM (see launching)
    * comment <property name="javax.persistence.schema-generation.database.action" value="drop-and-create" /> again
    
## Launching
To be able to use the full application, ensure the following before launching:
* If it is not already running, start mysql: `service mysql start`. You can check the status with `service mysql status`
* The docker images for Maven and SPT should show up in `docker images`. If this is not the case, set up docker again.
* Docker should be running: `docker run -it ubuntu`
Launching the application:
* Open de.fraunhofer.abm.app/de.fraunhofer.abm.bndrun
* Click on Run OSGi in the top right corner

## User management
Users can login with a local user or a Google account. All users are managed by the OSGi UserAdmin
service. You can access the management console by browsing to http://localhost:8080/system/console
when ABM is started. The password for system console is defined in de.fraunhofer.abm.app/de.fraunhofer.abm.bndrun under "Runtime Properties".
* browse to http://localhost:8080/system/console
* user/pass is root/password
* go to OSGi -> Users
* Create a group Admin
* Create a group RegisteredUser
* Create a group UserAdmin
* Create a user "demo"
  * Move it to RegisteredUser
  * Select the user "demo"
  * Under credentials add an entry:
    * Key `password`
    * Type `String`
    * Value `K25eHhV5v/qByG8oTP2VySQ4iPv4ZPFr0Bkf3uTnTwA=$lSghSVxaYoDynPR7B3LChprsbvvYP2M8lEKI9SWH52g=`
    * this is a salted password hash containing the password "demo"
    * if you want to create your own password, you can use the class
      Password in de.fraunhofer.abm.security, see main method

## Configuration
* Open Configuration.java in de.fraunhofer.abm.suitebuilder
  * Adjust "Workspace Root" to fit your development machine
* Open Configuration.java in de.fraunhofer.abm.repoarchive.local
  * Adjust "Directory" to fit your development machine
* These changes can also be made in the system console under OSGi -> Configuration , but I think
  that is not permanent, i.e. they are rest after a restart
  
## Setting up docker
* Download and install Docker Community Edition (https://www.docker.com/community-edition)
* Add your user to the docker group, so that you have access to docker
* Create local volumes:
  `docker volume create --name IVY_REPO -d local`
  `docker volume create --name M2_REPO -d local`
* Create the sbt build image:
  * `cd abm/docker_files/abm-sbt-0.13.13-jdk-8`
  * `docker build -t abm/sbt:01313-jdk-8 .`
* Create the maven build image:
  * `cd abm/docker_files/abm-maven-3-jdk-7`
  * `docker build -t abm/maven:3-jdk-7 .`  
  
## Updating the Database
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
