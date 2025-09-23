#!/bin/bash

# Great Dreamer Stories - Distribution Builder
# This script creates an app-image distribution using jpackage

set -e  # Exit on any error

# Set PATH to use Java 25
export PATH="/c/Program Files/Java/jdk-25/bin:$PATH"

# Configuration
GAME_NAME="GreatDreamerStories"
VERSION="0.0.1"
VENDOR="AtomicCode"
MAIN_CLASS="atomiccode.greatDreamerStories.Launcher"
OUTPUT_DIR="dist"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo
echo -e "${GREEN}== ============================ ==${NC}"
echo -e "${GREEN}==    Great Dreamer Stories     ==${NC}"
echo -e "${GREEN}==     Distribution Builder     ==${NC}"
echo -e "${GREEN}== ============================ ==${NC}"
echo

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo -e "${RED}Error: Java is not installed or not in PATH${NC}"
    exit 1
fi

# Check if jpackage is available (requires JDK 14+)
if ! command -v jpackage &> /dev/null; then
    echo -e "${RED}Error: jpackage is not available. Please use JDK 14 or later${NC}"
    exit 1
fi

echo -ne "${YELLOW}Cleaning previous builds... ${NC}"
rm -rf "${OUTPUT_DIR}"
echo -e "${GREEN}Done ✓${NC}"
echo

echo -ne "${YELLOW}Creating output directory... ${NC}"
mkdir -p "${OUTPUT_DIR}/classes"
mkdir -p "${OUTPUT_DIR}/input"
echo -e "${GREEN}Done ✓${NC}"
echo

# Create classpath with all LWJGL JARs
echo -ne "${YELLOW}Compiling Java sources... ${NC}"
# Set classpath separator based on OS
if [[ "$OSTYPE" == "msys" ]]; then
    SEP=";"
else
    SEP=":"
fi
LWJGL_CP="lib/lwjgl/lwjgl.jar${SEP}lib/lwjgl/lwjgl-openal.jar${SEP}lib/lwjgl/lwjgl-stb.jar"
LWJGL_WIN_NATIVES=";lib/lwjgl/lwjgl-natives-windows.jar;lib/lwjgl/lwjgl-openal-natives-windows.jar;lib/lwjgl/lwjgl-stb-natives-windows.jar"
LWJGL_LINUX_NATIVES=":lib/lwjgl/lwjgl-natives-linux.jar:lib/lwjgl/lwjgl-openal-natives-linux.jar:lib/lwjgl/lwjgl-stb-natives-linux.jar"
LWJGL_MAC_NATIVES=":lib/lwjgl/lwjgl-natives-macos.jar:lib/lwjgl/lwjgl-openal-natives-macos.jar:lib/lwjgl/lwjgl-stb-natives-macos.jar"
if [[ "$OSTYPE" == "msys" ]]; then
    LWJGL_CP="${LWJGL_CP}${LWJGL_WIN_NATIVES}"
elif [[ "$OSTYPE" == "linux" ]]; then
    LWJGL_CP="${LWJGL_CP}${LWJGL_LINUX_NATIVES}"
elif [[ "$OSTYPE" == "darwin" ]]; then
    LWJGL_CP="${LWJGL_CP}${LWJGL_MAC_NATIVES}"
fi
echo -e "${GREEN}Done ✓${NC}"
echo

# Compile all Java files
find src -name "*.java" -print0 | xargs -0 javac -d "${OUTPUT_DIR}/classes" -cp "${LWJGL_CP}"

# Create JAR with all compiled classes
echo -ne "${YELLOW}Creating JAR file... ${NC}"
jar cf "${OUTPUT_DIR}/input/${GAME_NAME}.jar" -C "${OUTPUT_DIR}/classes" .
echo -e "${GREEN}Done ✓${NC}"
echo

# Copy LWJGL native libraries to the input directory
echo -ne "${YELLOW}Copying LWJGL native libraries... ${NC}"
mkdir -p "${OUTPUT_DIR}/input/lib"
cp lib/lwjgl/*.jar "${OUTPUT_DIR}/input/lib/"
echo -e "${GREEN}Done ✓${NC}"
echo

echo -ne "${YELLOW}Copying game resources... ${NC}"
# Copy game resources to the input directory
cp -r res "${OUTPUT_DIR}/input/"
echo -e "${GREEN}Done ✓${NC}"
echo

echo -e "${YELLOW}Building Windows app-image with jpackage... ${NC}"
jpackage \
    --type app-image \
    --name "${GAME_NAME}" \
    --app-version "${VERSION}" \
    --description "${GAME_NAME} - ${VENDOR}" \
    --vendor "${VENDOR}" \
    --main-class "${MAIN_CLASS}" \
    --main-jar "${GAME_NAME}.jar" \
    --input "${OUTPUT_DIR}/input" \
    --dest "${OUTPUT_DIR}" \
    --java-options "-Xms512m" \
    --java-options "-Xmx2g" \
    --java-options "-Djava.library.path=lib" \
    --java-options "-Dorg.lwjgl.librarypath=lib" \
    --verbose

    ## "Xms" is the initial memory allocation for the JVM
    ## "Xmx" is the maximum memory allocation for the JVM
echo

# Cleaning up
echo -ne "${YELLOW}Cleaning up... ${NC}"
rm -rf "${OUTPUT_DIR}/classes"
rm -rf "${OUTPUT_DIR}/input"
echo -e "${GREEN}Done ✓${NC}"
echo

echo -e "${GREEN}Windows distribution created successfully!${NC}"
echo
echo -e "${BLUE}Output location: ${OUTPUT_DIR}/${GAME_NAME}/${NC}"
echo
echo -e "${YELLOW}Note: The app-image can be run directly or packaged into an installer${NC}"
