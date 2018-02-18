#!/usr/bin/env bash

####ABM INSTALLATION#####
#########################

LOGFILE=$HOME/`date "+%F_%T"`\_$SUDO_USER.log
sudo touch $LOGFILE
sudo chown $SUDO_USER $LOGFILE

echo "Starting installation of ABM."
echo "Logs can be found at: $LOGFILE"

######Git Repository######
##########################
# Testing purposes: sudo apt remove git

printf "Cloning ABM..."
bash -c "apt-get install git -y &>> $LOGFILE"
sudo -u $SUDO_USER bash -c "git clone --progress https://github.com/ABenchM/abm.git &>> $LOGFILE"
sudo -u $SUDO_USER bash -c "mkdir ./abm/eclipse_workspace &>> $LOGFILE"
echo " Done."

#######Docker Setup######
#########################
# Testing purposes:
# sudo apt-get purge docker-ce
# sudo rm -rf /var/lib/docker
# sudo rm -rf /etc/docker
# sudo groupdel docker

printf "Installing Docker..."
if hash docker 2>/dev/null
then
  echo "Docker already installed." >> "$LOGFILE" \
  && echo " Done."
else
 bash -c "apt-get remove --yes docker docker-engine docker.io &>> $LOGFILE" \
    && bash -c "apt-get update &>> $LOGFILE" \
    && bash -c "apt-get --yes --no-install-recommends install apt-transport-https ca-certificates &>> $LOGFILE" \
    && bash -c "wget --quiet --output-document=- https://download.docker.com/linux/ubuntu/gpg | apt-key add - &>> $LOGFILE" \
    && bash -c "add-apt-repository \"deb [arch=$(dpkg --print-architecture)] https://download.docker.com/linux/ubuntu $(lsb_release --codename --short) stable\" &>> $LOGFILE" \
    && bash -c "apt-get update &>> $LOGFILE" \
    && bash -c "apt-get --yes --no-install-recommends install docker-ce &>> $LOGFILE" \
    && bash -c "usermod --append --groups docker "$SUDO_USER" &>> $LOGFILE" \
    && bash -c "systemctl enable docker &>> $LOGFILE" \
    && echo "Docker successfully installed" >> "$LOGFILE" \
    && echo " Done."
printf "Waiting for Docker to start..."
sleep 3
echo " Done."
fi

# Docker Compose
# Testing purposes:
# sudo apt-get purge docker-compose

printf "Installing Docker Compose..."
if hash docker-compose 2>/dev/null
   then
    echo "Docker compose already installed." >> "$LOGFILE" \
    && echo " Done."
else
  bash -c "wget --output-document=/usr/local/bin/docker-compose https://github.com/docker/compose/releases/download/1.19.0/run.sh &>> $LOGFILE" \
    && bash -c "chmod +x /usr/local/bin/docker-compose &>> $LOGFILE" \
    && bash -c "wget --output-document=/etc/bash_completion.d/docker-compose \"https://raw.githubusercontent.com/docker/compose/$(docker-compose version --short)/contrib/completion/bash/docker-compose\" &>> $LOGFILE" \
    && echo "Docker Compose successfully installed" >> "$LOGFILE" \
    && echo " Done."
fi  

## Installing Images and Creating Volumes
    
printf "Installing Docker images and creating volumes. This step might take several minutes. Installation details can be found in the logs..."
bash -c "docker-compose up -d --no-recreate &>> $LOGFILE"
bash -c "docker volume create --name IVY_REPO -d local >/dev/null &>> $LOGFILE"
bash -c "docker volume create --name M2_REPO -d local &>> $LOGFILE"
echo " Done."

########MySQL Setup#####
########################
# Testing purposes:
# sudo apt-get purge docker-ce
# sudo rm -rf /var/lib/docker
# sudo rm -rf /etc/docker
# sudo groupdel docker

read -sp "Enter your root MySQL password: " MYSQL_ROOT_PASSWORD
echo ""

printf "Installing MySQL..."
if hash mysql 2>/dev/null
then
  echo "MySQL already installed." >> "$LOGFILE"
else
  export DEBIAN_FRONTEND=noninteractive
  echo debconf mysql-server/root_password password $MYSQL_ROOT_PASSWORD |  debconf-set-selections
  echo debconf mysql-server/root_password_again password $MYSQL_ROOT_PASSWORD |  debconf-set-selections
  bash -c "apt-get -qq install mysql-server > /dev/null &>> $LOGFILE"
  bash -c "apt-get -qq install expect > /dev/null &>> $LOGFILE"

  # Build Expect script
  tee ~/secure_our_mysql.sh > /dev/null << EOF
  spawn $(which mysql_secure_installation)

  expect "Enter password for user root:"
  send "$MYSQL_ROOT_PASSWORD\r"

  expect "Press y|Y for Yes, any other key for No:"
  send "n\r"

  expect "Change the password for root ? ((Press y|Y for Yes, any other key for No) :"
  send "n\r"

  expect "Remove anonymous users? (Press y|Y for Yes, any other key for No) :"
  send "y\r"

  expect "Disallow root login remotely? (Press y|Y for Yes, any other key for No) :"
  send "y\r"

  expect "Remove test database and access to it? (Press y|Y for Yes, any other key for No) :"
  send "y\r"

  expect "Reload privilege tables now? (Press y|Y for Yes, any other key for No) :"
  send "y\r"

EOF
  # Run Expect script.
  # This runs the "mysql_secure_installation" script which removes insecure defaults.
  expect ~/secure_our_mysql.sh >> "$LOGFILE"

  # Cleanup
  rm -v ~/secure_our_mysql.sh >> "$LOGFILE" # Remove the generated Expect script
  #sudo apt-get -qq purge expect > /dev/null # Uninstall Expect, commented out in case you need Expect

  echo "MySQL setup completed." >> "$LOGFILE"
fi
echo " Done."

printf "Initializing ABM tables..."
mysql -u root -p$MYSQL_ROOT_PASSWORD -e "create database abm;" &>> $LOGFILE
mysql -u root -p$MYSQL_ROOT_PASSWORD -D abm < abm/bnd-workspace/de.fraunhofer.abm.useradmin.dao.jdbc/useradmin_ddl.sql &>> $LOGFILE
mysql -u root -p$MYSQL_ROOT_PASSWORD -D abm < abm/docs/abm.sql &>> $LOGFILE
echo " Done."

#####Java Setup######
#####################

printf "Installing Java..."
if hash java 2>/dev/null
then
   JAVA_CHECK=`java -version 2>&1`
   echo $JAVA_CHECK >> "$LOGFILE"
else
  bash -c "apt-get install default-jdk -y &>> $LOGFILE"
fi
echo " Done."

####Eclipse Set up######
########################

printf "Installing Eclipse..."
if [ -d "/opt/eclipse" ]; then
  echo "Eclipse already installed." >> "$LOGFILE"
else
  bash -c "apt-get install eclipse -y 2>/dev/null &>> $LOGFILE"
  bash -c "wget -O eclipse.tar.gz https://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/oxygen/2/eclipse-jee-oxygen-2-linux-gtk-x86_64.tar.gz\&r=1 &>> $LOGFILE" \
  && bash -c "tar -xvf eclipse.tar.gz &>> $LOGFILE" \
  && bash -c "mv eclipse /opt &>> $LOGFILE" \
  && bash -c "cp eclipsed /usr/share/applications/eclipse.desktop && desktop-file-install /usr/share/applications/eclipse.desktop &>> $LOGFILE"
fi
echo " Done."

#########Finished########
#########################
echo "ABM installed. To complete your setup, follow the remaining instructions at: https://github.com/ABenchM/abm/blob/master/docs/Developer_Manual.md#installation-scripts-linux-only"
