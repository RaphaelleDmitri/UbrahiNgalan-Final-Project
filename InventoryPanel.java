import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class InventoryPanel extends JPanel {

    private Player player;

    private JLabel healthLabel, coinsLabel, attackLabel, defenseLabel;
    private JLabel equippedWeaponLabel, equippedArmorLabel, potionLabel;

    private JList<Weapon> weaponList;
    private JList<Armor> armorList;

    private DefaultListModel<Weapon> weaponModel;
    private DefaultListModel<Armor> armorModel;

    public InventoryPanel(Player player) {
        this.player = player;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(25, 25, 25));
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 100), 3));
        setPreferredSize(new Dimension(600, 500));

        // ===== TOP STATS =====
        JPanel statsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        statsPanel.setBackground(new Color(25, 25, 25));

        healthLabel = createLabel("Health: " + player.getHealth());
        coinsLabel = createLabel("Coins: " + player.coins);
        attackLabel = createLabel("Attack: " + player.attackPower);
        defenseLabel = createLabel("Defense: " + player.defense);
        equippedWeaponLabel = createLabel("Weapon: " + (player.equippedWeapon != null ? player.equippedWeapon.name : "None"));
        equippedArmorLabel = createLabel("Armor: " + (player.equippedArmor != null ? player.equippedArmor.name : "None"));
        potionLabel = createLabel("Potions: " + player.potionAmount);

        statsPanel.add(healthLabel);
        statsPanel.add(coinsLabel);
        statsPanel.add(attackLabel);
        statsPanel.add(defenseLabel);
        statsPanel.add(equippedWeaponLabel);
        statsPanel.add(equippedArmorLabel);
        statsPanel.add(potionLabel);

        add(statsPanel, BorderLayout.NORTH);

        // ===== INVENTORY LISTS =====
        weaponModel = new DefaultListModel<>();
        armorModel = new DefaultListModel<>();

        for (Weapon w : player.weapons) weaponModel.addElement(w);
        for (Armor a : player.armors) armorModel.addElement(a);

        weaponList = new JList<>(weaponModel);
        armorList = new JList<>(armorModel);

        styleList(weaponList);
        styleList(armorList);

        JPanel inventoryPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        inventoryPanel.setBackground(new Color(25, 25, 25));
        inventoryPanel.add(createListPanel("Weapons", weaponList));
        inventoryPanel.add(createListPanel("Armors", armorList));

        add(inventoryPanel, BorderLayout.CENTER);

        // ===== EQUIP BUTTONS =====
        JButton equipWeaponBtn = createButton("Equip Weapon");
        JButton equipArmorBtn = createButton("Equip Armor");

        equipWeaponBtn.addActionListener(e -> equipWeapon());
        equipArmorBtn.addActionListener(e -> equipArmor());

        JPanel equipPanel = new JPanel();
        equipPanel.setBackground(new Color(25, 25, 25));
        equipPanel.add(equipWeaponBtn);
        equipPanel.add(equipArmorBtn);

        // ===== SORT BUTTONS =====
        JButton sortByDamageBtn = createButton("Sort Weapons by Damage");
        JButton sortByNameBtn = createButton("Sort Weapons by Name");
        JButton sortArmorDefenseBtn = createButton("Sort Armors by Defense");
        JButton sortArmorNameBtn = createButton("Sort Armors by Name");

        sortByDamageBtn.addActionListener(e -> sortWeaponsByDamage());
        sortByNameBtn.addActionListener(e -> sortWeaponsByName());
        sortArmorDefenseBtn.addActionListener(e -> sortArmorsByDefense());
        sortArmorNameBtn.addActionListener(e -> sortArmorsByName());

        JPanel sortPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        sortPanel.setBackground(new Color(25, 25, 25));
        sortPanel.add(sortByDamageBtn);
        sortPanel.add(sortByNameBtn);
        sortPanel.add(sortArmorDefenseBtn);
        sortPanel.add(sortArmorNameBtn);

        // ===== BOTTOM PANEL =====
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        bottomPanel.setBackground(new Color(25, 25, 25));
        bottomPanel.add(equipPanel);
        bottomPanel.add(sortPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        updateStats();
    }

    // ================= METHODS =================

    private void equipWeapon() {
        Weapon selected = weaponList.getSelectedValue();
        if (selected == null) return;
        player.equipWeapon(selected);
        updateStats();
    }

    private void equipArmor() {
        Armor selected = armorList.getSelectedValue();
        if (selected == null) return;
        player.equipArmor(selected);
        updateStats();
    }

    // ===== Weapon Sorting (Bubble Sort) =====
    private void sortWeaponsByName() {
        ArrayList<Weapon> list = player.weapons;
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j).name.compareToIgnoreCase(list.get(j + 1).name) > 0) {
                    Weapon temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
        refreshWeaponList();
    }

    private void sortWeaponsByDamage() {
        ArrayList<Weapon> list = player.weapons;
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j).damage < list.get(j + 1).damage) {
                    Weapon temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
        refreshWeaponList();
    }

    private void refreshWeaponList() {
        weaponModel.clear();
        for (Weapon w : player.weapons) weaponModel.addElement(w);
    }

    // ===== Armor Sorting (Insertion Sort) =====
    private void sortArmorsByName() {
        ArrayList<Armor> list = new ArrayList<>(player.armors);
        int n = list.size();
        for (int i = 1; i < n; i++) {
            Armor key = list.get(i);
            int j = i - 1;
            while (j >= 0 && list.get(j).name.compareToIgnoreCase(key.name) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
        player.armors = list;
        refreshArmorList();
    }

    private void sortArmorsByDefense() {
        ArrayList<Armor> list = new ArrayList<>(player.armors);
        int n = list.size();
        for (int i = 1; i < n; i++) {
            Armor key = list.get(i);
            int j = i - 1;
            while (j >= 0 && list.get(j).defense < key.defense) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
        player.armors = list;
        refreshArmorList();
    }

    private void refreshArmorList() {
        armorModel.clear();
        for (Armor a : player.armors) armorModel.addElement(a);
    }

    // ===== Update Stats =====
    public void updateStats() {
        healthLabel.setText("Health: " + player.getHealth());
        coinsLabel.setText("Coins: " + player.coins);
        attackLabel.setText("Attack: " + player.attackPower);
        defenseLabel.setText("Defense: " + player.defense);
        equippedWeaponLabel.setText("Weapon: " + (player.equippedWeapon != null ? player.equippedWeapon.name : "None"));
        equippedArmorLabel.setText("Armor: " + (player.equippedArmor != null ? player.equippedArmor.name : "None"));
        potionLabel.setText("Potions: " + player.potionAmount);
        repaint();
    }

    // ===== Helper Methods =====
    private JPanel createListPanel(String title, JList<?> list) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 25, 25));
        JLabel label = createLabel(title);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(230, 205, 70));
        label.setFont(new Font("Consolas", Font.BOLD, 18));
        return label;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(new Color(240, 220, 140));
        button.setFont(new Font("Consolas", Font.BOLD, 18));
        button.setFocusPainted(false);
        return button;
    }

    private void styleList(JList<?> list) {
        list.setBackground(new Color(35, 35, 35));
        list.setForeground(new Color(240, 220, 140));
        list.setFont(new Font("Consolas", Font.PLAIN, 16));
        list.setSelectionBackground(new Color(100, 80, 30));
    }
}