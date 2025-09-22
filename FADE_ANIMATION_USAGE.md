# Fade Animation System Usage

The StateProcessor now handles fade animations between states, providing smooth transitions instead of instant state changes.

## How to Use Fade Transitions

### Basic Usage

Instead of using direct state changes:
```java
// Old way (instant transition)
Engine.instance().stateManager.setState(new NewState());
```

Use fade transitions:
```java
// New way (smooth fade transition)
Engine.instance().stateProcessor.setState(new NewState());
```

### Parameters

- `newState`: The state to transition to
- **Automatic fade settings**: Uses default black fade with 0.5 second duration

### Example Implementations

#### MainMenuState to CharacterSelectState
```java
startButton.setOnClick(() -> {
    Engine.instance().stateProcessor.setState(new CharacterSelectState());
});
```

#### Push/Pop States
```java
// Push state onto stack with fade
Engine.instance().stateProcessor.pushState(new NewState());

// Pop current state with fade
Engine.instance().stateProcessor.popState();
```

### Checking Transition Status

You can check if a transition is in progress:
```java
if (Engine.instance().stateProcessor.isTransitioning()) {
    // Don't process input or start new transitions
    return;
}
```

### Advanced Usage

For debugging or advanced control, you can access the current fade state:
```java
FadeState currentFade = Engine.instance().stateProcessor.getCurrentFade();
if (currentFade != null) {
    // Access fade information for debugging
    System.out.println("Fade type: " + currentFade.getFadeType());
    System.out.println("Fade complete: " + currentFade.isComplete());
}
```

## How It Works

1. **Fade Out**: The current state fades to black (0.5s)
2. **State Switch**: When fade out completes, the state switches
3. **Fade In**: The new state fades in from black (0.5s)

The StateProcessor automatically handles the timing and rendering of these transitions, so states don't need to implement their own fade logic.

## Special Cases

### SplashState Internal Fades
The SplashState has its own internal fade system for transitions between splash images:
- **Fade Out**: Current image fades to black (0.5s)
- **Image Switch**: Load next image while screen is black
- **Fade In**: New image fades in from black (0.5s)

This works independently of the StateProcessor's fade system.

## Benefits

- **Smooth Transitions**: No jarring instant state changes
- **Professional Look**: Polished user experience
- **Easy to Use**: Simple API for developers
- **Automatic**: Default settings work for most cases
- **Non-blocking**: States can still update during transitions
