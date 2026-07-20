FROM jetty:9.4.57-jre11

RUN java -jar "$JETTY_HOME/start.jar" --create-startd --add-to-start=jmx,jmx-remote,stats

COPY --chown=jetty:jetty . /

# Temporary switch to root
USER root

RUN mkdir -p /docker-entrypoint.d
RUN chown jetty:jetty /docker-entrypoint.d
RUN mkdir -p /mnt/geonetwork_datadir && \
    chown jetty:jetty /mnt/geonetwork_datadir

# Removes extra javax.mail library provided by Jetty
RUN rm -rf /usr/local/jetty/lib/mail

# Restore jetty user
USER jetty

VOLUME [ "/mnt/geonetwork_datadir", "/tmp", "/run/jetty" ]

# OCI image labels (https://github.com/opencontainers/image-spec/blob/main/annotations.md)
LABEL org.opencontainers.image.title="GeoNetwork (geOrchestra)" \
      org.opencontainers.image.description="GeoNetwork metadata catalog, geOrchestra distribution" \
      org.opencontainers.image.source="https://github.com/georchestra/geonetwork" \
      org.opencontainers.image.url="https://github.com/georchestra/geonetwork" \
      org.opencontainers.image.documentation="https://docs.georchestra.org/" \
      org.opencontainers.image.vendor="geOrchestra" \
      org.opencontainers.image.licenses="GPL-2.0-only"

ENTRYPOINT [ "/docker-entrypoint.sh" ]

CMD ["sh", "-c", "exec java \
-Djava.io.tmpdir=/tmp/jetty \
-Djava.util.prefs.userRoot=/tmp/userPrefs \
-Djava.util.prefs.systemRoot=/tmp/systemPrefs \
-Djetty.httpConfig.requestHeaderSize=65536 \
-Dgeorchestra.datadir=/etc/georchestra \
-Dgeonetwork.jeeves.configuration.overrides.file=/etc/georchestra/geonetwork/config/config-overrides-georchestra.xml \
-Dgeonetwork.dir=/mnt/geonetwork_datadir \
--add-opens=jdk.management/com.sun.management.internal=ALL-UNNAMED \
-XX:MaxRAMPercentage=80 -XX:+UseParallelGC \
-XX:-UsePerfData \
${JAVA_OPTIONS} \
-Djetty.httpConfig.sendServerVersion=false \
-Djetty.jmxremote.rmiregistryhost=0.0.0.0 \
-Djetty.jmxremote.rmiserverhost=0.0.0.0 \
-jar /usr/local/jetty/start.jar"]
