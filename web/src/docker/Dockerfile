FROM jetty:latest

ADD . /

RUN java -jar "$JETTY_HOME/start.jar" --add-to-startd=jmx,jmx-remote,stats

CMD [ "java","-Djava.io.tmpdir=/tmp/jetty",                                                                                   \
      "-Dgeorchestra.datadir=/etc/georchestra",                                                                               \
      "-Dgeonetwork.jeeves.configuration.overrides.file=/etc/georchestra/geonetwork/config/config-overrides-georchestra.xml", \
      "-jar","/usr/local/jetty/start.jar" ]

