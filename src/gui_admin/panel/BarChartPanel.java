package gui_admin.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.HashMap;

/**
 * Composant personnalisé pour afficher un diagramme en barres
 * Affiche les statistiques des tables de la base de données
 */
public class BarChartPanel extends JPanel {

    private Map<String, Integer> data;
    private int maxValue;
    private final Color barColor = new Color(241, 196, 15); // Couleur unique pour tous les bâtons

    public BarChartPanel() {
        this.data = new HashMap<>();
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1000, 600)); // Augmenter la hauteur: 450->600 pour plus d'espace vertical
    }

    public void updateData(Map<String, Integer> newData) {
        this.data = newData;
        this.maxValue = data.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        // Arrondir à la dizaine supérieure ou minimum 10
        this.maxValue = Math.max(10, ((maxValue / 10) + 1) * 10);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (data.isEmpty()) {
            drawLoadingMessage(g);
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Marges
        int marginLeft = 80;
        int marginRight = 40;
        int marginTop = 40;
        int marginBottom = 80;

        // Zone de dessin
        int chartWidth = width - marginLeft - marginRight;
        int chartHeight = height - marginTop - marginBottom;

        // Dessiner les axes
        drawAxes(g2d, marginLeft, marginTop, chartWidth, chartHeight);

        // Dessiner les graduations de l'axe Y
        drawYAxisLabels(g2d, marginLeft, marginTop, chartHeight);

        // Dessiner les barres
        drawBars(g2d, marginLeft, marginTop, chartWidth, chartHeight);

        // Dessiner les labels de l'axe X
        drawXAxisLabels(g2d, marginLeft, marginTop, chartWidth, chartHeight);

        g2d.dispose();
    }

    private void drawLoadingMessage(Graphics g) {
        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        FontMetrics fm = g.getFontMetrics();
        String message = "Chargement des données...";
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = getHeight() / 2;
        g.drawString(message, x, y);
    }

    private void drawAxes(Graphics2D g2d, int marginLeft, int marginTop, int chartWidth, int chartHeight) {
        g2d.setColor(new Color(73, 80, 87));
        g2d.setStroke(new BasicStroke(2));

        // Axe Y (vertical)
        g2d.drawLine(marginLeft, marginTop, marginLeft, marginTop + chartHeight);

        // Axe X (horizontal)
        g2d.drawLine(marginLeft, marginTop + chartHeight, marginLeft + chartWidth, marginTop + chartHeight);
    }

    private void drawYAxisLabels(Graphics2D g2d, int marginLeft, int marginTop, int chartHeight) {
        g2d.setColor(new Color(108, 117, 125));
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();

        // Dessiner les graduations de 0 à maxValue par pas de 2
        int step = Math.max(2, maxValue / 10);
        for (int i = 0; i <= maxValue; i += step) {
            int y = marginTop + chartHeight - (int) ((double) i / maxValue * chartHeight);
            String label = String.valueOf(i);
            int labelWidth = fm.stringWidth(label);

            // Ligne de grille horizontale
            g2d.setColor(new Color(233, 236, 239));
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f}, 0.0f));
            g2d.drawLine(marginLeft, y, marginLeft + chartHeight, y);

            // Label
            g2d.setColor(new Color(108, 117, 125));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawString(label, marginLeft - labelWidth - 10, y + fm.getAscent() / 2);
        }
    }

    private void drawBars(Graphics2D g2d, int marginLeft, int marginTop, int chartWidth, int chartHeight) {
        if (data.isEmpty()) return;

        int barCount = data.size();
        int spacing = 20; // Augmenter l'espacement entre les barres (était 10px)
        int barWidth = (chartWidth - (barCount + 1) * spacing) / barCount;
        barWidth = Math.max(30, Math.min(barWidth, 200)); // Augmenter la largeur min et max des barres (était 20-80px)

        int totalBarsWidth = barCount * barWidth + (barCount - 1) * spacing;
        int startX = marginLeft + (chartWidth - totalBarsWidth) / 2;

        int barIndex = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            String label = entry.getKey();
            int value = entry.getValue();

            // Calculer la hauteur de la barre
            int barHeight = (int) ((double) value / maxValue * chartHeight);
            int x = startX + barIndex * (barWidth + spacing); // Utiliser le nouvel espacement
            int y = marginTop + chartHeight - barHeight;

            // Dessiner la barre avec dégradé
            GradientPaint gradient = new GradientPaint(
                x, y, barColor,
                x, y + barHeight, barColor.darker()
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(x, y, barWidth, barHeight, 5, 20);

            // Contour de la barre
            g2d.setColor(barColor.darker());
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(x, y, barWidth, barHeight, 5, 5);

            // Afficher la valeur au-dessus de la barre
            g2d.setColor(new Color(73, 80, 87));
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            FontMetrics fm = g2d.getFontMetrics();
            String valueStr = String.valueOf(value);
            int valueWidth = fm.stringWidth(valueStr);
            g2d.drawString(valueStr, x + (barWidth - valueWidth) / 2, y - 5);

            barIndex++;
        }
    }

    private void drawXAxisLabels(Graphics2D g2d, int marginLeft, int marginTop, int chartWidth, int chartHeight) {
        if (data.isEmpty()) return;

        g2d.setColor(new Color(108, 117, 125));
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        FontMetrics fm = g2d.getFontMetrics();

        int barCount = data.size();
        int spacing = 20; // Utiliser le même espacement que les barres
        int barWidth = (chartWidth - (barCount + 1) * spacing) / barCount;
        barWidth = Math.max(30, Math.min(barWidth, 120)); // Utiliser les mêmes paramètres que les barres

        int totalBarsWidth = barCount * barWidth + (barCount - 1) * spacing;
        int startX = marginLeft + (chartWidth - totalBarsWidth) / 2;

        int labelIndex = 0;
        for (String label : data.keySet()) {
            int x = startX + labelIndex * (barWidth + spacing); // Utiliser le nouvel espacement
            int labelWidth = fm.stringWidth(label);

            // Centrer le label sous la barre
            int labelX = x + (barWidth - labelWidth) / 2;
            int labelY = marginTop + chartHeight + 20;

            // Rotation du texte si nécessaire pour les labels longs
            if (labelWidth > barWidth) {
                Graphics2D g2dRotated = (Graphics2D) g2d.create();
                g2dRotated.rotate(-Math.PI / 4, labelX, labelY);
                g2dRotated.drawString(label, labelX, labelY);
                g2dRotated.dispose();
            } else {
                g2d.drawString(label, labelX, labelY);
            }

            labelIndex++;
        }
    }
}
