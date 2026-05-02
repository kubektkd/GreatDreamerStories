#!/bin/bash

echo "Building Great Dreamer Stories..."

echo "Compiling Java source files with Gradle/LibGDX..."
./gradlew compileJava

if [ $? -eq 0 ]; then
    echo ""
    echo "Compilation successful!"
    echo ""
    echo "To run the game:"
    echo "  ./gradlew run"
    echo ""
    echo "Or use Cursor's launch configuration (Ctrl+F5)"
else
    echo ""
    echo "Compilation failed!"
    echo "Check the error messages above."
fi
