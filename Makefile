BTAG=4.4.x-SNAPSHOT

deb:
	mvn package deb:package -pl web -PdebianPackage,datahub-integration -DskipTests ${DEPLOY_OPTS}

docker-build: war
	cd web; \
	mvn -Pdocker,datahub-integration -DskipTests package -DdockerImageTags=${BTAG},latest

war:
	mvn clean install -Pdatahub-integration -DskipTests
