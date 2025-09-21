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
Engine.instance().startFadeTransition(new NewState(), Color.BLACK, 1.0f);
```

### Parameters

- `newState`: The state to transition to
- `fadeColor`: The color to fade to/from (usually Color.BLACK or Color.WHITE)
- `fadeDuration`: Duration of the fade in seconds (e.g., 1.0f for 1 second)

### Example Implementations

#### MainMenuState to CharacterSelectState
```java
startButton.setOnClick(() -> {
    Engine.instance().startFadeTransition(new CharacterSelectState(), Color.BLACK, 1.0f);
});
```

#### Different Fade Colors
```java
// Fade to white
Engine.instance().startFadeTransition(new NewState(), Color.WHITE, 0.5f);

// Fade to custom color
Engine.instance().startFadeTransition(new NewState(), new Color(100, 50, 200), 2.0f);
```

### Checking Transition Status

You can check if a transition is in progress:
```java
if (Engine.instance().isTransitioning()) {
    // Don't process input or start new transitions
    return;
}
```

### Advanced Usage

For debugging or advanced control, you can access the current fade state:
```java
FadeState currentFade = Engine.instance().getCurrentFade();
if (currentFade != null) {
    float progress = currentFade.getProgress(); // 0.0 to 1.0
    System.out.println("Fade progress: " + (progress * 100) + "%");
}
```

## How It Works

1. **Fade Out**: The current state fades to the specified color
2. **State Switch**: When fade out completes, the state switches
3. **Fade In**: The new state fades in from the specified color

The StateProcessor automatically handles the timing and rendering of these transitions, so states don't need to implement their own fade logic.

## Benefits

- **Smooth Transitions**: No jarring instant state changes
- **Professional Look**: Polished user experience
- **Easy to Use**: Simple API for developers
- **Flexible**: Customizable colors and durations
- **Non-blocking**: States can still update during transitions
