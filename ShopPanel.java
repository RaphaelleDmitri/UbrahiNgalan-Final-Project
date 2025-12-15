import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ShopPanel extends JPanel {
    private Image backgroundImage;
    private Main game;
    private Player player;
    private JTextArea log;
    private JButton weaponBtn;
    private JButton armorBtn;
    // Using Queue interface instead of LinkedList
    private Queue<Weapon> weaponQueue;
    private Queue<Armor> armorQueue;

    
    public ShopPanel(Main game, Player player) {
        this.game = game;
        this.player = player;


        weaponQueue = player.availableWeapons != null ? player.availableWeapons : new LinkedList<>();
        armorQueue = player.availableArmor != null ? player.availableArmor : new LinkedList<>();
        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("shop.png"));
        } catch (IOException e) {
            System.out.println("Shop background image not found, using color instead");
        }

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(25, 25, 25));
        // Keep default opacity; background image + optional tint will be painted manually

        // Left panel container for vertical centering
        JPanel leftContainer = new JPanel(new GridBagLayout());
        leftContainer.setOpaque(false);
        
        // Left panel for buttons
        JPanel leftPanel = new JPanel(new GridLayout(5, 1, 20, 25));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(620, 500));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));

        // Weapon button
        weaponBtn = styledButton("");
        updateWeaponButton();
        weaponBtn.setFont(GameFonts.press(14f));
        leftPanel.add(weaponBtn);

        // Armor button
        armorBtn = styledButton("");
        updateArmorButton();
        armorBtn.setFont(GameFonts.press(12f));
        leftPanel.add(armorBtn);

        // Potion button
        JButton potionBtn = styledButton("Health Potion (+20 HP) - 10 Gold");
        potionBtn.setFont(GameFonts.press(15f));
        leftPanel.add(potionBtn);

        // Exit button
        JButton exitBtn = styledButton("EXIT SHOP");
        exitBtn.setFont(GameFonts.press(22f));
        leftPanel.add(exitBtn);

        leftContainer.add(leftPanel);
        add(leftContainer, BorderLayout.WEST);

        // Log area in the center
        log = new JTextArea();
        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setOpaque(false);
        log.setForeground(Color.WHITE);
        log.setFont(GameFonts.press(18f));
        log.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JScrollPane scroll = new JScrollPane(log);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null); // Remove border
        scroll.setPreferredSize(new Dimension(700, 0));
        add(scroll, BorderLayout.CENTER);

        log.setText("Welcome to the shop!\n\n Coins: " + player.coins + "\n\n");

        // Button actions
        weaponBtn.addActionListener(e -> buyWeapon());
        armorBtn.addActionListener(e -> buyArmor());
        potionBtn.addActionListener(e -> buyPotion());
        exitBtn.addActionListener(e -> game.returnToMap());
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.SrcOver.derive(0.6f));
                g2.setColor(new Color(60, 60, 60));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(new Color(240, 220, 140));
        btn.setFocusPainted(false);
        return btn;
    }

    private void updateWeaponButton() {
        if (!weaponQueue.isEmpty()) {
            Weapon nextWeapon = weaponQueue.peek();
            weaponBtn.setText(nextWeapon.name + " (+" + nextWeapon.damage + " Attack) - " + nextWeapon.price + " Gold");
            weaponBtn.setEnabled(true);
        } else {
            weaponBtn.setText("No more weapons available");
            weaponBtn.setEnabled(false);
        }
    }

    private void updateArmorButton() {
        if (!armorQueue.isEmpty()) {
            Armor nextArmor = armorQueue.peek();
            armorBtn.setText(nextArmor.name + " (+" + nextArmor.defense + " Defense) - " + nextArmor.price + " Gold");
            armorBtn.setEnabled(true);
        } else {
            armorBtn.setText("No more armor available");
            armorBtn.setEnabled(false);
        }
    }
private void buyWeapon() {
    if (!weaponQueue.isEmpty()) {
        Weapon nextWeapon = weaponQueue.peek();

        if (player.coins >= nextWeapon.price) {
            player.coins -= nextWeapon.price;

            player.weapons.add(nextWeapon);

            log.append(
                "\nYou bought " + nextWeapon.name + "!\n" +
                "Added to inventory.\n" +
                "Coins: " + player.coins + "\n\n"
            );

            weaponQueue.poll();
            updateWeaponButton();

        } else {
            log.append(
                "\nNot enough coins to buy " + nextWeapon.name + "!\n" +
                "Coins: " + player.coins + "\n\n"
            );
        }
    }
}


    private void buyArmor() {
    if (!armorQueue.isEmpty()) {
        Armor nextArmor = armorQueue.peek();

        if (player.coins >= nextArmor.price) {
            player.coins -= nextArmor.price;

            player.armors.add(nextArmor);

            log.append(
                "\nYou bought " + nextArmor.name + "!\n" +
                "Added to inventory.\n" +
                "Coins: " + player.coins + "\n\n"
            );

            armorQueue.poll();
            updateArmorButton();
        } else {
            log.append(
                "\nNot enough coins to buy " + nextArmor.name + "!\n" +
                "Coins: " + player.coins + "\n\n"
            );
        }
    }
}
    

    private void buyPotion() {
        if (player.coins >= 10) {
            player.coins -= 10;
            player.potionAmount++;

            log.append(
                "\nYou bought a Health Potion!\n" +
                "(+20 HP)\n" +
                "Coins: " + player.coins + "\n\n"
            );
        } else {
            log.append(
                "\nNot enough coins to buy a potion!\n" +
                "Coins: " + player.coins + "\n\n"
            );
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        // Optional translucent tint over the background image
        if (bgAlpha > 0f) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.SrcOver.derive(bgAlpha));
            g2.setColor(bgTintColor);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    // === Background tint controls ===
    private float bgAlpha = 0.0f; // 0.0 (no tint) to 1.0 (fully opaque)
    private Color bgTintColor = new Color(0, 0, 0); // default: black tint

    /**
     * Set a semi-transparent tint over the shop background.
     * @param color base tint color (RGB used)
     * @param alpha 0.0f (no tint) .. 1.0f (fully opaque)
     */
    public void setBackgroundTint(Color color, float alpha) {
        if (color != null) {
            this.bgTintColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
        }
        this.bgAlpha = Math.max(0f, Math.min(1f, alpha));
        repaint();
    }
}
