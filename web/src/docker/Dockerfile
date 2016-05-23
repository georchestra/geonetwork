FROM jetty:9.3-jre8

ADD . /

RUN java -jar "$JETTY_HOME/start.jar" --add-to-startd=jmx,jmx-remote,stats

RUN apt-get update && \
    apt-get install -y unzip && \
    rm -rf /var/lib/apt/lists/*

VOLUME [ "/var/local/geonetwork" ]

RUN unzip /var/lib/jetty/webapps/geonetwork.war -d /var/lib/jetty/webapps/geonetwork && \
  rm -f /var/lib/jetty/webapps/geonetwork.war && \
  chown -R jetty:jetty /var/lib/jetty/webapps/geonetwork

ENTRYPOINT [ "/docker-entrypoint.sh" ]
CMD [ "java","-Djava.io.tmpdir=/tmp/jetty",                                                                                             \
      "-Dgeonetwork.jeeves.configuration.overrides.file=/var/lib/jetty/webapps/geonetwork/WEB-INF/config-overrides-georchestra.xml",    \
      "-Dgeonetwork.dir=/var/local/geonetwork",                                                                                         \
      "-Dgeonetwork-private.schema.dir=/var/lib/jetty/webapps/geonetwork/WEB-INF/data/config/schema_plugins",                           \
      "-jar","/usr/local/jetty/start.jar" ]
