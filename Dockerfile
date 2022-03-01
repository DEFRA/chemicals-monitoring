FROM defradigital/java:latest-jre

USER root

ARG BUILD_VERSION
RUN echo "Building reach-monitoring ${BUILD_VERSION} ...."

RUN mkdir -p /usr/src/reach-monitoring
WORKDIR /usr/src/reach-monitoring

COPY ./target/reach-monitoring-${BUILD_VERSION}.jar /usr/src/reach-monitoring/reach-monitoring.jar
COPY ./target/agent/applicationinsights-agent.jar /usr/src/reach-monitoring/applicationinsights-agent.jar
COPY ./target/classes/applicationinsights.json /usr/src/reach-monitoring/applicationinsights.json

RUN chown jreuser /usr/src/reach-monitoring
USER jreuser

EXPOSE 8096

CMD java -javaagent:/usr/src/reach-monitoring/applicationinsights-agent.jar \
-Xmx${JAVA_MX:-512M} -Xms${JAVA_MS:-512M} \
-jar reach-monitoring.jar
