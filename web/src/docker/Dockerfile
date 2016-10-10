FROM jetty:9.3-jre8

RUN java -jar "$JETTY_HOME/start.jar" --add-to-startd=jmx,jmx-remote,stats

ADD . /

VOLUME [ "/var/local/geonetwork", "/tmp" ]

ENTRYPOINT [ "/docker-entrypoint.sh" ]

CMD ["sh", "-c", "exec java \
-Djava.io.tmpdir=/tmp/jetty \
-Djava.util.prefs.userRoot=/tmp/userPrefs \
-Djava.util.prefs.systemRoot=/tmp/systemPrefs \
-Dgeorchestra.datadir=/etc/georchestra \
-Dgeonetwork.jeeves.configuration.overrides.file=/etc/georchestra/geonetwork/config/config-overrides-georchestra.xml \
-Dgeonetwork.dir=/var/local/geonetwork \
-Xmx$XMX -Xms$XMX \
-XX:-UsePerfData \
-jar /usr/local/jetty/start.jar"]
