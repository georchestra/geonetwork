FROM jetty:9.3-jre8

ADD . /

RUN java -jar "$JETTY_HOME/start.jar" --add-to-startd=jmx,jmx-remote,stats

VOLUME [ "/var/local/geonetwork" ]

ENTRYPOINT [ "/docker-entrypoint.sh" ]

CMD ["sh", "-c", "exec java -Djava.io.tmpdir=/tmp/jetty -Dgeorchestra.datadir=/etc/georchestra                           \
    -Dgeonetwork.jeeves.configuration.overrides.file=/etc/georchestra/geonetwork/config/config-overrides-georchestra.xml \
    ${ADD_JAVA_OPTS}                                                                                                     \
    -Dgeonetwork.dir=/var/local/geonetwork -Xmx$XMX -Xms$XMX -jar /usr/local/jetty/start.jar" ]

