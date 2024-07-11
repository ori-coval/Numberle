import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.*;

class GraphPanel extends JPanel {
    private Map<Double, Double> dataPoints; // Map of data points to plot
    private static final int padding = 50; // Padding for axes and labels

    // Constructor
    public GraphPanel(Map<Double, Double> dataPoints) {
        this.dataPoints = new HashMap<>();
        // Convert int keys and values to Double
        for (Map.Entry<Double, Double> entry : dataPoints.entrySet()) {
            this.dataPoints.put(entry.getKey().doubleValue(), entry.getValue().doubleValue());
        }
    }

    // Override paintComponent to draw the line graph
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Set rendering hints for better drawing quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Determine data range for x and y axes
        double minX = Collections.min(dataPoints.keySet());
        double maxX = Collections.max(dataPoints.keySet());
        double minY = Collections.min(dataPoints.values());
        double maxY = Collections.max(dataPoints.values());

        // Calculate dimensions based on data range
        int width = getWidth() - 2 * padding;
        int height = getHeight() - 2 * padding;

        // Draw x and y axes
        g2d.setColor(Color.BLACK);
        g2d.drawLine(padding, height + padding, width + padding, height + padding); // x-axis
        g2d.drawLine(padding, padding, padding, height + padding); // y-axis

        // Draw ticks and labels on x-axis
        for (double i = minX; i <= maxX; i += (maxX - minX) / 5) {
            int x = (int) (padding + (i - minX) / (maxX - minX) * width);
            g2d.drawLine(x, height + padding - 5, x, height + padding + 5);
            g2d.drawString(String.format("%.1f", i), x - 10, height + padding + 20);
        }

        // Draw ticks and labels on y-axis
        for (double i = minY; i <= maxY; i += (maxY - minY) / 5) {
            int y = (int) (height + padding - (i - minY) / (maxY - minY) * height);
            g2d.drawLine(padding - 5, y, padding + 5, y);
            g2d.drawString(String.format("%.1f", i), padding - 30, y + 5);
        }

        // Plot points and connect with lines
        g2d.setColor(Color.RED);
        List<Double> xValues = new ArrayList<>(dataPoints.keySet());
        Collections.sort(xValues); // Sort x values for consistent plotting order

        for (int i = 0; i < xValues.size(); i++) {
            double x = xValues.get(i);
            double y = dataPoints.get(x);

            int px = (int) (padding + (x - minX) / (maxX - minX) * width);
            int py = (int) (height + padding - (y - minY) / (maxY - minY) * height);

            // Plot point
            g2d.fillOval(px - 3, py - 3, 6, 6);

            // Connect points with lines
            if (i > 0) {
                double prevX = xValues.get(i - 1);
                double prevY = dataPoints.get(prevX);

                int prevPx = (int) (padding + (prevX - minX) / (maxX - minX) * width);
                int prevPy = (int) (height + padding - (prevY - minY) / (maxY - minY) * height);

                g2d.drawLine(prevPx, prevPy, px, py);
            }
        }
    }
}
