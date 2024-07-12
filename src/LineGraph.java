import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.*;

class GraphPanel extends JPanel {
    private List<Map<Double, Double>> dataPointsList; // List of maps for multiple graphs
    private static final int padding = 50; // Padding for axes and labels
    private static final Color[] COLORS = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA}; // Array of colors for different graphs

    // Constructor
    public GraphPanel(List<Map<Double, Double>> dataPointsList) {
        this.dataPointsList = new ArrayList<>();
        // Convert int keys and values to Double for each set of data points
        for (Map<Double, Double> dataPoints : dataPointsList) {
            Map<Double, Double> convertedMap = new HashMap<>();
            for (Map.Entry<Double, Double> entry : dataPoints.entrySet()) {
                convertedMap.put(entry.getKey().doubleValue(), entry.getValue().doubleValue());
            }
            this.dataPointsList.add(convertedMap);
        }
    }

    public GraphPanel(Map<Double, Double> dataPoints) {
        this.dataPointsList = new ArrayList<>();
        Map<Double, Double> convertedMap = new HashMap<>();
        for (Map.Entry<Double, Double> entry : dataPoints.entrySet()) {
            convertedMap.put(entry.getKey().doubleValue(), entry.getValue().doubleValue());
        }
        this.dataPointsList.add(convertedMap);
    }

    // Override paintComponent to draw the line graphs
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Set rendering hints for better drawing quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Determine data range for x and y axes
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Map<Double, Double> dataPoints : dataPointsList) {
            minX = Math.min(minX, Collections.min(dataPoints.keySet()));
            maxX = Math.max(maxX, Collections.max(dataPoints.keySet()));
            minY = Math.min(minY, Collections.min(dataPoints.values()));
            maxY = Math.max(maxY, Collections.max(dataPoints.values()));
        }

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

        // Plot each graph
        for (int index = 0; index < dataPointsList.size(); index++) {
            Map<Double, Double> dataPoints = dataPointsList.get(index);
            Color color = COLORS[index % COLORS.length]; // Cycle through predefined colors
            plotGraph(g2d, dataPoints, color, minX, maxX, minY, maxY, width, height);
        }
    }

    private void plotGraph(Graphics2D g2d, Map<Double, Double> dataPoints, Color color, double minX, double maxX, double minY, double maxY, int width, int height) {
        g2d.setColor(color);
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

    public static void main(String[] args) {
        // Sample data points
        Map<Double, Double> dataPoints1 = new HashMap<>();
        dataPoints1.put(1.0, 5.0);
        dataPoints1.put(2.0, 10.0);
        dataPoints1.put(3.0, 15.0);
        dataPoints1.put(4.0, 20.0);

        Map<Double, Double> dataPoints2 = new HashMap<>();
        dataPoints2.put(1.0, 20.0);
        dataPoints2.put(2.0, 15.0);
        dataPoints2.put(3.0, 10.0);
        dataPoints2.put(4.0, 5.0);

        Map<Double, Double> dataPoints3 = new HashMap<>();
        dataPoints3.put(1.0, 7.0);
        dataPoints3.put(2.0, 14.0);
        dataPoints3.put(3.0, 21.0);
        dataPoints3.put(4.0, 28.0);

        // Create list of data points maps
        List<Map<Double, Double>> dataPointsList = Arrays.asList(dataPoints1, dataPoints2, dataPoints3);

        // Create and set up the window
        JFrame frame = new JFrame("Graph Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Create and set up the content pane
        GraphPanel graphPanel = new GraphPanel(dataPointsList);
        frame.add(graphPanel);

        // Display the window
        frame.setVisible(true);
    }
}
