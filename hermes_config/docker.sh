#!/bin/sh
id=$1
csv=$2
#docker run -d -v /var/lib/abm/workspace/3fc4a805-662c-45d8-8ec6-5fd1282dd808:/repodir -i --name $id opalj/sbt_scala_javafx  bash

#docker cp /opt/abm/hermes.json $id:/root/OPAL/DEVELOPING_OPAL/tools/src/main/resources

#docker cp /opt/abm/application.conf $id:/root/OPAL/DEVELOPING_OPAL/tools/src/main/resources

docker exec $id sbt "project OPAL-DeveloperTools"  "runMain org.opalj.hermes.HermesCLI src/main/resources/hermes.json -csv $csv.csv"		
