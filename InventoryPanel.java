import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class InventoryPanel extends JPanel {

    private Main game;
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
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 100), 2));
        setPreferredSize(new Dimension(1000, 400));

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
        inventoryPanel.add(createListPanel("WEAPONS", weaponList));
        inventoryPanel.add(createListPanel("ARMORS", armorList));

        add(inventoryPanel, BorderLayout.CENTER);

        // ===== EQUIP BUTTONS =====
        JButton equipWeaponBtn = createButton("Equip Weapon");
        JButton equipArmorBtn = createButton("Equip Armor");
        JButton sellWeaponBtn = createButton("Sell Weapon");
        JButton sellArmorBtn = createButton("Sell Armor");

        equipWeaponBtn.addActionListener(e -> equipWeapon());
        equipArmorBtn.addActionListener(e -> equipArmor());
        sellWeaponBtn.addActionListener(e -> sellWeapon());
        sellArmorBtn.addActionListener(e -> sellArmor());

        JPanel equipPanel = new JPanel();
        equipPanel.setBackground(new Color(25, 25, 25));
        equipPanel.add(equipWeaponBtn);
        equipPanel.add(equipArmorBtn);
        equipPanel.add(sellWeaponBtn);
        equipPanel.add(sellArmorBtn);

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

    private void sellWeapon() {
        Weapon selected = weaponList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a weapon to sell.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!selected.tradable) {
            JOptionPane.showMessageDialog(this, selected.name + " is soulbound and cannot be sold!", "Cannot Sell", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this, 
            "Sell " + selected.name + " for " + (int) Math.round(selected.price * 0.7) + " coins?",
            "Confirm Sale", 
            JOptionPane.YES_NO_OPTION);

        if (ok == JOptionPane.YES_OPTION) {
            int earned = player.sellWeapon(selected);
            if (earned > 0) {
                refreshWeaponList();
                updateStats();
                JOptionPane.showMessageDialog(this, "Sold for " + earned + " coins!", "Sale Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void sellArmor() {
        Armor selected = armorList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select armor to sell.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!selected.tradable) {
            JOptionPane.showMessageDialog(this, selected.name + " is soulbound and cannot be sold!", "Cannot Sell", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this, 
            "Sell " + selected.name + " for " + (int) Math.round(selected.price * 0.7) + " coins?",
            "Confirm Sale", 
            JOptionPane.YES_NO_OPTION);

        if (ok == JOptionPane.YES_OPTION) {
            int earned = player.sellArmor(selected);
            if (earned > 0) {
                refreshArmorList();
                updateStats();
                JOptionPane.showMessageDialog(this, "Sold for " + earned + " coins!", "Sale Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        }
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
    // Set the font once
    Font labelFont = GameFonts.press(16f);
    
        
        healthLabel.setFont(labelFont);
        healthLabel.setText("Health: " + player.getHealth());
        healthLabel.setBorder(new EmptyBorder(20,20,5,0));
        coinsLabel.setFont(labelFont);
        coinsLabel.setText("Coins: " + player.coins);
        coinsLabel.setBorder(new EmptyBorder(20,0,5,0));
        attackLabel.setFont(labelFont);
        attackLabel.setText("Attack: " + player.attackPower);
        attackLabel.setBorder(new EmptyBorder(0,20,0,0));
        defenseLabel.setFont(labelFont);
        defenseLabel.setText("Defense: " + player.defense);
        equippedWeaponLabel.setFont(labelFont);
        equippedWeaponLabel.setText("Weapon: " + (player.equippedWeapon != null ? player.equippedWeapon.name : "None"));
        equippedWeaponLabel.setBorder(new EmptyBorder(0,20,0,0));
        equippedArmorLabel.setFont(labelFont);
        equippedArmorLabel.setText("Armor: " + (player.equippedArmor != null ? player.equippedArmor.name : "None"));
        potionLabel.setFont(labelFont);
        potionLabel.setText("Potions: " + player.potionAmount);
        potionLabel.setBorder(new EmptyBorder(0,20,5,0));
    
        repaint();
}
    // ===== Helper Methods =====
    private JPanel createListPanel(String title, JList<?> list) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 25, 25));
        JLabel label = createLabel(title);
        label.setFont(GameFonts.pressBold(26f));
        label.setBorder(new EmptyBorder(0,0,10,0));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(230, 205, 70));
        label.setFont(GameFonts.jettsBold(18f));
        return label;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(new Color(240, 220, 140));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));        
        button.setFont(GameFonts.pressBold(20f));
        button.setFocusPainted(false);
        return button;
    }

    private void styleList(JList<?> list) {
        list.setBackground(new Color(35, 35, 35));
        list.setForeground(new Color(240, 220, 140));
        list.setFont(GameFonts.press(18f));
        list.setSelectionBackground(new Color(100, 80, 30));
        list.setFixedCellHeight(35);
        list.setBorder(new EmptyBorder(5,20,0,0));

    }
}