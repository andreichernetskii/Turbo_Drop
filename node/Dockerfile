FROM maven:3.8-openjdk-17-slim

ENV HOME=/home/app
RUN mkdir -p $HOME

ADD ./settings.xml /usr/share/maven/ref/
ENV MVN_SETTINGS=/usr/share/maven/ref/settings.xml

RUN mkdir -p $HOME/common
ADD ./common $HOME/common/

RUN mvn -f $HOME/common/pom.xml -s $MVN_SETTINGS clean package -DskipTests
RUN mvn org.apache.maven.plagins:maven-install-plugin:2.5.2:install-file -Dfile=$HOME/common/target/common-1.0-SNAPSHOT.jar -DskipTests --debug

RUN mkdir -p $HOME/node
ADD ./node/pom.xml $HOME/node/
ADD ./node/src $HOME/node/src
RUN mvn -f $HOME/node/pom.xml -s $MVN_SETTINGS clean package -DskipTests -- debug

CMD ["java", "-jar", "/home/app/node/target/node-1.0-SNAPSHOT.jar"]