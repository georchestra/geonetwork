FROM jetty:9.3-jre8

ADD . /

VOLUME [ "/var/local/geonetwork" ]

ENTRYPOINT [ "/docker-entrypoint.sh" ]
CMD [ "java","-Djava.io.tmpdir=/tmp/jetty", "-Dgeorchestra.datadir=/etc/georchestra",                                         \
      "-Dgeonetwork.jeeves.configuration.overrides.file=/etc/georchestra/geonetwork/config/config-overrides-georchestra.xml", \
      "-Dgeonetwork.dir=/var/local/geonetwork", "-jar","/usr/local/jetty/start.jar" ]

