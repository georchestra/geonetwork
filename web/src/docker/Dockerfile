FROM georchestra/base

ADD . /

CMD [ "java","-Djava.io.tmpdir=/tmp/jetty",                                                                                   \
      "-Dgeorchestra.datadir=/etc/georchestra",                                                                               \
      "-Dgeonetwork.jeeves.configuration.overrides.file=/etc/georchestra/geonetwork/config/config-overrides-georchestra.xml", \
      "-jar","/usr/local/jetty/start.jar" ]

