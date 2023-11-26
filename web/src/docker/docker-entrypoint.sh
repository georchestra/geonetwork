#!/bin/bash

DIR=/docker-entrypoint.d

# Executing custom scripts located in CUSTOM_SCRIPTS_DIRECTORY if environment variable is set
if [[ -z "${CUSTOM_SCRIPTS_DIRECTORY}" ]]; then
  echo "[INFO] No CUSTOM_SCRIPTS_DIRECTORY env variable set"
else
  echo "[INFO] CUSTOM_SCRIPTS_DIRECTORY env variable set to ${CUSTOM_SCRIPTS_DIRECTORY}"
  # Regex is needed in jetty9 images, but not alpine's ones.
  cp -v "${CUSTOM_SCRIPTS_DIRECTORY}"/* "$DIR"
  echo "[INFO] End moving custom scripts"
fi

if [[ -d "$DIR" ]]
then
  /bin/run-parts --verbose "$DIR" --regex='.*'
fi

exec "$@"
