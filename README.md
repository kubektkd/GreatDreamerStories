<p align="center">
  <img src="res/logos/game-logo-white.png" alt="Great Dreamer Stories Logo" width="300" />
</p>

Great Dreamer Stories is a Visual Novel game project enriched with a turn-based combat system and classic RPG mechanics inspired by Call of Cthulhu. Set in a Nordic Noir atmosphere, the story begins in modern times in a small Scandinavian town, where you play as the chief of the local police station. As you investigate mysterious events and unravel dark secrets, you'll navigate both the psychological and supernatural, blending narrative-driven choices with strategic combat and RPG progression.

## 🎮 Overview

Great Dreamer Stories is powered by a custom-built game engine, the CthulhuEngine, which has been developed specifically for this project to support both visual novel storytelling and turn-based RPG mechanics. The engine is designed for flexibility and extensibility, allowing for advanced features such as dynamic narrative branching, AI-driven character behaviors, and seamless integration of audio-visual assets. This foundation enables the game to deliver a unique blend of narrative depth and strategic gameplay. The engine allows for expansion of the game by adding additional stories that the Player's character can explore in sequence. Each story acts as a single RPG session, after which the Player can improve their character's stats.

## 🛠️ Development Requirements

### Java Development Kit (JDK)
- **Recommended**: Java 25
- **Minimum**: Java 17+
- **Download**: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)

### Audio Libraries
- **Recommended**: OpenAL (LWJGL) for OGG audio playback and advanced audio features
- **Minimum**: Java Sound API (built into Java) for WAV/AIFF/AU playback
- **Download**: [LWJGL OpenAL](https://www.lwjgl.org/) (included in `lib/lwjgl/`)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd GreatDreamerStories
```

### 2. Compile and Run

**Simply use Cursor's launch configuration:**
- Press **Ctrl+F5** to run
- Press **F5** to debug

## 📦 Creating Distributions

### Build All Platforms
```bash
build_distribution.bat
```

### Build Specific Platform
```bash
build_distribution.bat windows  # Windows only
build_distribution.bat linux    # Linux only
build_distribution.bat mac      # Mac only
```

### Distribution Output
- **Windows**: `dist/GreatDreamerStories-Windows/`
- **Linux**: `dist/GreatDreamerStories-Linux/`
- **Mac**: `dist/GreatDreamerStories-Mac/`

Each distribution includes:
- Compiled game files
- Game resources (sounds, images)
- Platform-specific launcher
- Setup instructions (README.txt)
- Audio support (WAV, OGG)

## 🔧 Configuration

### Cursor Settings Config
The project includes `.vscode/settings.json` for proper libraries integration:

### Cursor Launch Config
The project includes `.vscode/launch.json` for easy debugging and running.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test on multiple platforms
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- [Tabletop Audio](https://tabletopaudio.com/) for audio files
- JavaFX team for cross-platform media support
- OpenJDK community for Java development tools
- Game development community for inspiration and feedback
