package gui_util;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class StyleUtil {
    public static void styliserTableHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(255, 231, 77));
        header.setForeground(Color.BLACK);

        // rendre l'en-tête non déplaçable
        header.setReorderingAllowed(false);

        // définir une police personnalisée
        header.setFont(new Font("Arial", Font.BOLD, 12));
    }
}