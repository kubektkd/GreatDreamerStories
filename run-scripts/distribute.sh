#!/bin/bash

set -e

GAME_NAME="GreatDreamerStories"
VERSION="0.0.1"
VENDOR="AtomicCode"
MAIN_JAR="${GAME_NAME}.jar"
INPUT_DIR="build/jpackage-input"
OUTPUT_DIR="dist"

case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) PLATFORM="windows" ;;
    Linux*) PLATFORM="linux" ;;
    Darwin*) PLATFORM="macos" ;;
    *) PLATFORM="unknown" ;;
esac

echo
echo "Great Dreamer Stories distribution builder"
echo "Target platform: ${PLATFORM}"
echo

if ! command -v jpackage >/dev/null 2>&1; then
    echo "Error: jpackage is not available. Use JDK 17 or newer."
    exit 1
fi

./gradlew clean stageJpackage

rm -rf "${OUTPUT_DIR}/${PLATFORM}"
mkdir -p "${OUTPUT_DIR}/${PLATFORM}"

jpackage \
    --type app-image \
    --name "${GAME_NAME}" \
    --app-version "${VERSION}" \
    --description "${GAME_NAME} - ${VENDOR}" \
    --vendor "${VENDOR}" \
    --main-jar "${MAIN_JAR}" \
    --main-class "atomiccode.greatDreamerStories.Launcher" \
    --input "${INPUT_DIR}" \
    --dest "${OUTPUT_DIR}/${PLATFORM}" \
    --java-options "-Xms512m" \
    --java-options "-Xmx2g" \
    --verbose

echo
echo "Distribution created at ${OUTPUT_DIR}/${PLATFORM}/${GAME_NAME}"
echo "Run this script on Windows, Linux, and macOS to create native app images for each OS."
