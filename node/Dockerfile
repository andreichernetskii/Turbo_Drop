FROM maven:3.8-openjdk-17-slim

ENV HOME=/home/app
WORKDIR $HOME

COPY ./settings.xml /usr/share/maven/ref/
ENV MVN_SETTINGS=/usr/share/maven/ref/settings.xml

WORKDIR $HOME/common

COPY ./common/pom.xml $HOME/common/
RUN mvn -s $MVN_SETTINGS clean package -DskipTests

RUN mvn instal:instal-file \
    -Dfile=$HOME/common/target/common-1.0-SNAPSHOT.jar \
    -DgroupId=com.example \
    -DartifactId=common \
    -Dversion=1.0-SNAPSHOT \
    -Dpackaging=jar

WORKDIR $HOME/node
COPY ./node/pom.xml $HOME/node/
COPY ./node/src $HOME/node/src

RUN mvn -s $MVN_SETTINGS clean package -DskipTests --debug

CMD ["java", "-jar", "/home/app/node/target/node-1.0-SNAPSHOT.jar"]