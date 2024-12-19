FROM maven:3.8-openjdk-17-slim

ENV HOME=/home/app
RUN mkdir -p $HOME

COPY ./settings.xml /usr/share/maven/ref/
ENV MVN_SETTINGS=/usr/share/maven/ref/settings.xml

WORKDIR $HOME/dispatcher
COPY ./dispatcher/pom.xml $HOME/dispatcher/
COPY ./dispatcher/src $HOME/dispatcher/src

RUN mvn -s $MVN_SETTINGS clean package -DskipTests --debug

CMD ["java", "-jar", "/home/app/dispatcher/target/dispatcher-1.0-SNAPSHOT.jar"]
