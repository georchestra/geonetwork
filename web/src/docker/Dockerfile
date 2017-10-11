FROM jetty:9.3-jre8

ADD . /

RUN java -jar "$JETTY_HOME/start.jar" --add-to-startd=jmx,jmx-remote,stats

# Temporary switch to root
USER root

RUN apt-get update && \
    apt-get install -y unzip && \
    rm -rf /var/lib/apt/lists/*


RUN unzip /var/lib/jetty/webapps/geonetwork.war -d /var/lib/jetty/webapps/geonetwork && \
  rm -f /var/lib/jetty/webapps/geonetwork.war && \
  chown -R jetty:jetty /var/lib/jetty/webapps/geonetwork

# Restore jetty user
USER jetty

VOLUME [ "/var/local/geonetwork" ]

ENTRYPOINT [ "/docker-entrypoint.sh" ]

CMD [ "sh", "-c", "exec java -Djava.io.tmpdir=/tmp/jetty                                                                           \
      -Dgeonetwork.jeeves.configuration.overrides.file=/var/lib/jetty/webapps/geonetwork/WEB-INF/config-overrides-georchestra.xml  \
      -Dgeonetwork.dir=/var/local/geonetwork                                                                                       \
      -Xmx${XMX:-3G} -Xms${XMX:-3G}                                                                                                \
      -Dgeonetwork.schema.dir=/var/lib/jetty/webapps/geonetwork/WEB-INF/data/config/schema_plugins                                 \
      ${ADD_JAVA_OPTS}                                                                                                             \
      -jar /usr/local/jetty/start.jar" ]
