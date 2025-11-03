import javax.swing.*;
import java.awt.*;

public class StatPanel extends JPanel {
    private Player player;
    private JLabel healthLabel;
    private JLabel coinsLabel;
    private JLabel weaponLabel;
    private JLabel armorLabel;
    private JLabel damageLabel;

    public StatPanel(Player player) {
        this.player = player;

        setLayout(new GridLayout(6, 1, 10, 10));
        setBackground(new Color(25, 25, 25));
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 100), 3));
        setPreferredSize(new Dimension(500, 0));

        // Labels
        healthLabel = createLabel("Health: " + player.getHealth());
        coinsLabel = createLabel("Coins: " + player.coins);
        
        weaponLabel = createLabel("Weapon: " + (player.equippedWeapon != null ? player.equippedWeapon.name : "None")+ "\n (+" + (player.attackPower - 10) + " Attack)");
        armorLabel = createLabel("Armor: " + (player.equippedArmor != null ? player.equippedArmor.name : "None") + "\n (+" + player.defense + " Defense)");
        damageLabel = createLabel("Attack: " + player.attackPower);

        add(healthLabel);
        add(coinsLabel);
        add(weaponLabel);
        add(armorLabel);
        add(damageLabel);

        // Optional: Close button for stat panel
        JButton closeBtn = new JButton("Close Stats");
        closeBtn.setBackground(new Color(60, 60, 60));
        closeBtn.setForeground(new Color(240, 220, 140));
        closeBtn.setFont(new Font("Consolas", Font.BOLD, 20));
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> this.setVisible(false));
        add(closeBtn);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(230, 205, 70));
        label.setFont(new Font("Consolas", Font.BOLD, 22));
        return label;
    }

    public void updateStats() {
        healthLabel.setText("Health: " + player.getHealth());
        coinsLabel.setText("Coins: " + player.coins);
        weaponLabel.setText("Weapon: " + (player.equippedWeapon != null ? player.equippedWeapon.name : "None" + "\n (+" + (player.attackPower - 10) + " Attack)"));
        armorLabel.setText("Armor: " + (player.equippedArmor != null ? player.equippedArmor.name : "None") + " (+" + player.defense + " Defense)");
        damageLabel.setText("Attack: " + player.attackPower);
        repaint();
    }
}
