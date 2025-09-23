package atomiccode.cthulhuEngine.inputsOutputs.windowing;

import javax.swing.*;

import atomiccode.cthulhuEngine.engineMain.engine.EngineConfigs;

import java.awt.*;

public class Window {
    public enum Mode {
        FULLSCREEN,
        MAXIMIZED,
        WINDOWED
    }

    private JFrame frame;
    private Canvas canvas;

    private String title;
    private int width, height;
    private Dimension minResolution;

    private GraphicsDevice gd;

    public Window(EngineConfigs configs) {
        this.minResolution = new Dimension(configs.minResolution.getWidth(), configs.minResolution.getHeight());
        this.title = configs.windowTitle;
        this.width = configs.resolution.getWidth();
        this.height = configs.resolution.getHeight();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gd = ge.getDefaultScreenDevice();

        createWindow();
        setMode(configs.windowMode); // default mode
    }

    private void createWindow() {
        canvas = new Canvas();
        canvas.setMinimumSize(minResolution);
        // canvas.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()));
        // canvas.setMaximumSize(new Dimension(width, height));
        canvas.setFocusable(false);

        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.add(canvas);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    public void setMode(Mode mode) {
        gd.setFullScreenWindow(null);
        frame.dispose();

        switch (mode) {
            case FULLSCREEN:
                frame.setUndecorated(true);
                frame.setResizable(false);
                frame.setVisible(true);
                gd.setFullScreenWindow(frame);
                break;

            case MAXIMIZED:
                frame.setUndecorated(false);
                frame.setResizable(true);
                frame.setMinimumSize(minResolution);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                break;

            case WINDOWED:
                frame.setUndecorated(false);
                frame.setResizable(true);
                frame.setSize(width, height);
                frame.setMinimumSize(minResolution);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                break;
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public JFrame getFrame() {
        return frame;
    }
}
