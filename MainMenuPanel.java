import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class MainMenuPanel extends JPanel {
    private Image backgroundImage;

    public MainMenuPanel(Main game) {
        // Load background image (optional - if file doesn't exist, it will use color)
        try {
            backgroundImage = ImageIO.read(new File("front.png"));
        } catch (IOException e) {
            System.out.println("Background image not found, using color instead");
        }

        setLayout(new GridBagLayout());
        setBackground(new Color(30,30,30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15,0,15,0);

        JLabel title = new JLabel("A Java RPG by Ubrahi Ngalan");
        title.setFont(GameFonts.pressBold(40f));
        title.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0;
        add(title, gbc);

        

        JButton start = new JButton("START GAME");
        JButton exit  = new JButton("EXIT");
        styleButton(start); styleButton(exit);

        gbc.gridy = 1; add(start, gbc);
        gbc.gridy = 2; add(exit, gbc);

        start.addActionListener(e -> game.showGamePanel());
        exit.addActionListener(e -> System.exit(0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void styleButton(JButton btn){
        btn.setBackground(new Color(60,60,60));
        btn.setForeground(new Color(240,220,140));
        btn.setFont(GameFonts.pressBold(30f));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setMargin(new Insets(10, 28, 10, 28));
        btn.setFocusPainted(false);
    }
}
