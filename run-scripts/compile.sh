#!/bin/bash

echo "Building Great Dreamer Stories..."

# Create output directory
rm -rf out
mkdir -p out

# Compile all Java files
echo "Compiling Java source files..."
javac -cp "lib/lwjgl/*" -d out -sourcepath src src/atomiccode/greatDreamerStories/Launcher.java

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Compilation successful!"
    echo ""
    echo "To run the game:"
    echo "  java -cp \"out:lib/lwjgl/*\" -Djava.library.path=lib atomiccode.greatDreamerStories.Launcher"
    echo ""
    echo "Or use Cursor's launch configuration (Ctrl+F5)"
else
    echo ""
    echo "❌ Compilation failed!"
    echo "Check the error messages above."
fi
