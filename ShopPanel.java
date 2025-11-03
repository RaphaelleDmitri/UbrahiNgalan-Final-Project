import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class ShopPanel extends JPanel {
    private Main game;
    private Player player;
    private JTextArea log;
    private StatPanel statPanel; // live stats

    private Queue<Weapon> weaponQueue;
    private JButton weaponBtn;

    private Queue<Armor> armorQueue;
    private JButton armorBtn;

    public ShopPanel(Main game, Player player) {
        this.game = game;
        this.player = player;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(25, 25, 25));

        // Stat panel on the right
        statPanel = new StatPanel(player);
        add(statPanel, BorderLayout.EAST);

        // Left panel for buttons
        JPanel leftPanel = new JPanel(new GridLayout(5, 1, 20, 20));
        leftPanel.setBackground(new Color(25, 25, 25));
        leftPanel.setPreferredSize(new Dimension(600, 0));

        // Weapon queue
        weaponQueue = new LinkedList<>();
        weaponQueue.add(new Weapon("Iron Sword", 8, 50));
        weaponQueue.add(new Weapon("Steel Sword", 12, 100));
        weaponQueue.add(new Weapon("Mythril Sword", 20, 200));

        weaponBtn = styledButton("");
        updateWeaponButton();
        leftPanel.add(weaponBtn);

        // Armor queue
        armorQueue = new LinkedList<>();
        armorQueue.add(new Armor("Leather Armor", 8, 30));
        armorQueue.add(new Armor("Chainmail Armor", 16, 60));
        armorQueue.add(new Armor("Steel Armor", 24, 120));

        armorBtn = styledButton("");
        updateArmorButton();
        leftPanel.add(armorBtn);

        // Potion button
        JButton potionBtn = styledButton("Health Potion (+20 HP) - 10 Gold");
        leftPanel.add(potionBtn);

        // Exit button
        JButton exitBtn = styledButton("Exit Shop");
        leftPanel.add(exitBtn);

        add(leftPanel, BorderLayout.WEST);

        // Log area in the center (big font)
        log = new JTextArea();
        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setBackground(new Color(40, 40, 40));
        log.setForeground(Color.WHITE);
        log.setFont(new Font("Consolas", Font.BOLD, 28));
        log.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 100), 3));

        JScrollPane scroll = new JScrollPane(log);
        scroll.setPreferredSize(new Dimension(700, 0));
        add(scroll, BorderLayout.CENTER);

        log.setText("Welcome to the shop!\nCoins: " + player.coins);

        // Button actions
        weaponBtn.addActionListener(e -> buyWeapon());
        armorBtn.addActionListener(e -> buyArmor());
        potionBtn.addActionListener(e -> buyPotion());
        exitBtn.addActionListener(e -> game.returnToMap());
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(60, 60, 60));
        btn.setForeground(new Color(240, 220, 140));
        btn.setFont(new Font("Consolas", Font.BOLD, 26));
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
                player.attackPower += nextWeapon.damage;
                player.equippedWeapon = nextWeapon; // update equipped weapon
                log.append("\nYou bought " + nextWeapon.name + "! (+" + nextWeapon.damage + " Attack)\nCoins: " + player.coins);
                weaponQueue.poll();
                updateWeaponButton();
                statPanel.updateStats(); // update live stats
            } else {
                log.append("\nNot enough coins to buy " + nextWeapon.name + "! Coins: " + player.coins);
            }
        }
    }

    private void buyArmor() {
        if (!armorQueue.isEmpty()) {
            Armor nextArmor = armorQueue.peek();
            if (player.coins >= nextArmor.price) {
                player.coins -= nextArmor.price;
                player.defense += nextArmor.defense;
                player.equippedArmor = nextArmor; // update equipped armor
                log.append("\nYou bought " + nextArmor.name + "! (+" + nextArmor.defense + " Defense)\nCoins: " + player.coins);
                armorQueue.poll();
                updateArmorButton();
                statPanel.updateStats(); // update live stats
            } else {
                log.append("\nNot enough coins to buy " + nextArmor.name + "! Coins: " + player.coins);
            }
        }
    }

    private void buyPotion() {
        if (player.coins >= 10) {
            player.coins -= 10;
            player.potionAmount++;
            log.append("\nYou bought a Health Potion! (+20 HP) Coins: " + player.coins);
            statPanel.updateStats(); // update live stats
        } else {
            log.append("\nNot enough coins to buy a potion! Coins: " + player.coins);
        }
    }
}
