FROM openjdk:8-jdk

ENV SBT_VERSION  0.13.13
ENV SBT_JAR      https://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/$SBT_VERSION/sbt-launch.jar

ADD  $SBT_JAR  /usr/local/bin/sbt-launch.jar  
COPY sbt.sh    /usr/local/bin/sbt

RUN echo "==> fetch all sbt jars from Maven repo..."       && \
    echo "==> [CAUTION] this may take several minutes!!!"  && \
    sbt

WORKDIR /app

# Define default command.
ENTRYPOINT ["sbt"]
CMD ["--version"]

