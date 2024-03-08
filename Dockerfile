FROM sapmachine:17

RUN apt-get update \
  && apt-get install -y ca-certificates curl git openssh-client rsync --no-install-recommends \
  && rm -rf /var/lib/apt/lists/*

# common for all images
ENV MAVEN_HOME /usr/share/maven

COPY --from=maven:3.9.6-eclipse-temurin-11 ${MAVEN_HOME} ${MAVEN_HOME}
COPY --from=maven:3.9.6-eclipse-temurin-11 /usr/local/bin/mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
COPY --from=maven:3.9.6-eclipse-temurin-11 /usr/share/maven/ref/settings-docker.xml /usr/share/maven/ref/settings-docker.xml

RUN ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn

ARG MAVEN_VERSION=3.9.6
ARG USER_HOME_DIR="/root"
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app/
COPY / /usr/src/app
RUN git clone https://github.com/mnunezpoggi/chatbot.git
WORKDIR /usr/src/app/chatbot
RUN mvn -T 1C clean install && rm -rf target
WORKDIR /usr/src/app/
COPY src /usr/src/app/src
#RUN mvn clean package
RUN mvn clean compile
#ENTRYPOINT ["java"]
#CMD ["-jar", "/usr/src/app/target/rsyncbot-jar-with-dependencies.jar"]

ENTRYPOINT ["/usr/local/bin/mvn-entrypoint.sh"]
CMD ["mvn", "exec:java", "-Dexec.mainClass=xyz.kraftwork.rsyncbot.Rsyncbot"]