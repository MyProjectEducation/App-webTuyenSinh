package ui.icon;

import javax.swing.*;
import java.awt.*;

// Tạo một Icon tự vẽ bằng thuật toán đồ họa
public class DrawHamburger implements Icon {
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Bật chế độ chống răng cưa giúp nét vẽ mịn hơn
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK); // Bạn có thể đổi màu icon tại đây
        g2d.setStroke(new BasicStroke(2.5f)); // Độ dày của sọc ngang

        // Vẽ 3 đường thẳng (Tọa độ: x1, y1, x2, y2)
        g2d.drawLine(x + 2, y + 3, x + 18, y + 3); // Sọc trên
        g2d.drawLine(x + 2, y + 9, x + 18, y + 9); // Sọc giữa
        g2d.drawLine(x + 2, y + 15, x + 18, y + 15); // Sọc dưới

        g2d.dispose();
    }

    @Override
    public int getIconWidth() {
        return 20;
    } // Chiều rộng icon

    @Override
    public int getIconHeight() {
        return 18;
    } // Chiều cao icon
}
