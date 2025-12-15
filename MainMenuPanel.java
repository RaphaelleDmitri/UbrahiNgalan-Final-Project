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
        try {
            backgroundImage = ImageIO.read(new File("front.png"));
        } catch (IOException e) {
            System.out.println("Background image 404, using color instead");
        }

        setLayout(new BorderLayout());
        setBackground(new Color(30,30,30));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15,0,15,0);

        JLabel title = new JLabel("BRAHI'S WANDER-SPELL");
        title.setFont(GameFonts.pressBold(50f));
        title.setForeground(new Color(0xf6ecc6)); 
        centerPanel.add(title, gbc);

        JButton start = new JButton("START GAME");
        JButton exit  = new JButton("EXIT");
        styleButton(start); styleButton(exit);

        gbc.gridy = 1; centerPanel.add(start, gbc);
        gbc.gridy = 2; centerPanel.add(exit, gbc);

        add(centerPanel, BorderLayout.CENTER);

        JLabel footer = new JLabel("Ubrahi Ngalan");
        footer.setFont(GameFonts.pressBold(16f));
        footer.setForeground(new Color(0xa1c47d)); 
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setBorder(new EmptyBorder(10, 0, 20, 0));
        add(footer, BorderLayout.SOUTH);

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
        btn.setBackground(new Color(0x5a7a3d)); 
        btn.setForeground(new Color(0xf6ecc6));
        btn.setFont(GameFonts.pressBold(30f));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(20, 50, 20, 50));    
    }
}
