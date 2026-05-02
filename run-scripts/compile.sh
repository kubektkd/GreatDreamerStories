#!/bin/bash

echo -e "\nBuilding Great Dreamer Stories..."

echo -e "\nCompiling Java source files with Gradle/LibGDX..."
./gradlew compileJava

if [ $? -eq 0 ]; then
    echo ""
    echo "Compilation successful!"
    echo ""
    echo "To run the game use below command:"
    echo "  ./gradlew run"
else
    echo ""
    echo "Compilation failed!"
    echo "Check the error messages above."
fi
echo ""
