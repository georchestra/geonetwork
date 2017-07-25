FROM jetty:9.3-jre8

ENV XMS=1G XMX=6G

RUN java -jar "$JETTY_HOME/start.jar" --add-to-startd=jmx,jmx-remote,stats

ADD . /

RUN mkdir /mnt/geonetwork_datadir && \
    chown jetty:jetty /etc/georchestra /mnt/geonetwork_datadir && \
    chown -R jetty:jetty /var/lib/jetty/webapps

VOLUME [ "/mnt/geonetwork_datadir", "/tmp", "/run/jetty" ]

ENTRYPOINT [ "/docker-entrypoint.sh" ]

CMD ["sh", "-c", "exec java \
-Djava.io.tmpdir=/tmp/jetty \
-Djava.util.prefs.userRoot=/tmp/userPrefs \
-Djava.util.prefs.systemRoot=/tmp/systemPrefs \
-Dgeorchestra.datadir=/etc/georchestra \
-Dgeonetwork.jeeves.configuration.overrides.file=/etc/georchestra/geonetwork/config/config-overrides-georchestra.xml \
-Dgeonetwork.dir=/mnt/geonetwork_datadir \
-Xms$XMS -Xmx$XMX \
-XX:-UsePerfData \
${JAVA_OPTIONS} \
-jar /usr/local/jetty/start.jar"]
