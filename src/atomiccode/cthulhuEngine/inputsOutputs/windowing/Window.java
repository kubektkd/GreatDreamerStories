package atomiccode.cthulhuEngine.inputsOutputs.windowing;

import javax.swing.*;
import java.awt.*;

public class Window {
    private JFrame frame;
    private Canvas canvas;

    private String title;
    private int width, height;

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;

        createWindow();
    }

    private void createWindow() {
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // optional configs
        frame.setResizable(false); // don't allow to resize the window
        frame.setLocationRelativeTo(null); // position frame in the screen center
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setVisible(true); // show JFrame

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setMinimumSize(new Dimension(width, height));
        canvas.setFocusable(false);

        frame.add(canvas);
        frame.pack();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public JFrame getFrame() {
        return frame;
    }
}
