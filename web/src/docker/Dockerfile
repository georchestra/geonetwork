FROM jetty:9.3-jre8

ENV XMS=1G XMX=4G

RUN java -jar "$JETTY_HOME/start.jar" --add-to-startd=jmx,jmx-remote,stats

ADD . /

# Temporary switch to root
USER root

RUN mkdir /mnt/geonetwork_datadir && \
    chown jetty:jetty /mnt/geonetwork_datadir

RUN apt-get update && \
    apt-get install -y unzip && \
    rm -rf /var/lib/apt/lists/*

RUN unzip /var/lib/jetty/webapps/geonetwork.war -d /var/lib/jetty/webapps/geonetwork && \
  rm -f /var/lib/jetty/webapps/geonetwork.war && \
  chown -R jetty:jetty /var/lib/jetty/webapps/geonetwork

# Restore jetty user
USER jetty

VOLUME [ "/mnt/geonetwork_datadir", "/tmp", "/run/jetty" ]

ENTRYPOINT [ "/docker-entrypoint.sh" ]

CMD [ "sh", "-c", "exec java \
-Djava.io.tmpdir=/tmp/jetty \
-Djava.util.prefs.userRoot=/tmp/userPrefs \
-Djava.util.prefs.systemRoot=/tmp/systemPrefs \
-Dgeonetwork.jeeves.configuration.overrides.file=/var/lib/jetty/webapps/geonetwork/WEB-INF/config-overrides-georchestra.xml \
-Dgeonetwork.schema.dir=/var/lib/jetty/webapps/geonetwork/WEB-INF/data/config/schema_plugins \
-Dgeonetwork.dir=/mnt/geonetwork_datadir \
-XMS${XMS} -XMX${XMX} \
-XX:-UsePerfData \
${JAVA_OPTIONS} \
-jar /usr/local/jetty/start.jar" ]

