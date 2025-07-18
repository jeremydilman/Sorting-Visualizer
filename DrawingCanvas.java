// DrawingCanvas.java
import javax.swing.*;
import java.awt.*;

public class DrawingCanvas extends JPanel {
    private int[] array;
    private int[] groups = null;
    private int highlightIndex1 = -1;
    private int highlightIndex2 = -1;

    private static final Color[] COLOR_PALETTE = {
        Color.BLUE, Color.MAGENTA, Color.GREEN, Color.ORANGE,
        Color.PINK, Color.CYAN, Color.YELLOW, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.BLACK
    };

    public DrawingCanvas(int[] array) {
        this.array = array;
    }

    public void setArray(int[] array) {
        this.array = array;
    }

    public void setGroups(int[] groups) {
        this.groups = groups;
        repaint();
    }

    public void setHighlightIndices(int index1, int index2) {
        this.highlightIndex1 = index1;
        this.highlightIndex2 = index2;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (array == null) return;

        int width = getWidth();
        int height = getHeight();
        int barWidth = width / array.length;

        for (int i = 0; i < array.length; i++) {
            if (groups != null) {
                g.setColor(COLOR_PALETTE[groups[i] % COLOR_PALETTE.length]);
            } else if (i == highlightIndex1 || i == highlightIndex2) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.GRAY);
            }

            int barHeight = array[i];
            g.fillRect(i * barWidth + 10, height - barHeight - 30, barWidth - 20, barHeight);
        }
    }
}