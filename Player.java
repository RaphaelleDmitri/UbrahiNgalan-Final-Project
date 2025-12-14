import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class Player extends Character {

    private Random rand = new Random();
    private Main game;

    // ===== INVENTORY =====
    public ArrayList<Weapon> weapons = new ArrayList<>();
    public ArrayList<Armor> armors = new ArrayList<>();

    // ===== EQUIPMENT =====
    public Weapon equippedWeapon;
    public Armor equippedArmor;

    // ===== SHOP ITEMS (QUEUE) =====
    public LinkedList<Weapon> availableWeapons = new LinkedList<>();
    public LinkedList<Armor> availableArmor = new LinkedList<>();

    // ===== BASE STATS =====
    private int baseAttack;
    private int baseDefense;

    public Player(String name, int health, int attackPower, int defense,
                  int potionAmount, int coins, Main game) {

        super(name, health, attackPower, defense, potionAmount, coins);
        this.game = game;

        this.baseAttack = attackPower;
        this.baseDefense = defense;

        // Initialize shop items ONCE
        availableWeapons.add(new Weapon("Iron Sword", 5, 50));
        availableWeapons.add(new Weapon("Steel Sword", 10, 100));
        availableWeapons.add(new Weapon("Mythril Sword", 15, 200));
        availableWeapons.add(new Weapon("Diamond Sword", 25, 400));
        availableWeapons.add(new Weapon("Sword of Demacia", 40, 800));

        availableArmor.add(new Armor("Leather Armor", 8, 30));
        availableArmor.add(new Armor("Chainmail Armor", 12, 60));
        availableArmor.add(new Armor("Steel Armor", 16, 120));
        availableArmor.add(new Armor("Diamond Armor", 24, 200));
        availableArmor.add(new Armor("Armor of Retribution", 30, 300));


    }

    // ================= COMBAT =================

    @Override
    public void attack(Character target, JTextArea log) {
        int weaponBonus = equippedWeapon != null ? equippedWeapon.damage : 0;

        int dmg = baseAttack + weaponBonus + rand.nextInt(5);
        dmg -= (target.defense / 4);

        String weaponName = equippedWeapon != null
                ? equippedWeapon.getName()
                : "Rusty Sword";

        target.health -= dmg;

        log.append("\n\nYou strike " + target.getName() +
                   " with your " + weaponName + "!");
        log.append("\nYou deal " + dmg + " damage!");
    }

    public void defend(JTextArea log) {
        int armorBonus = equippedArmor != null ? equippedArmor.defense : 0;
        int block = armorBonus + rand.nextInt(5);

        log.append("\n\nYou brace yourself, blocking " + block + " damage!");
    }

    public void heal(JTextArea log) {
        if (potionAmount > 0) {
            int healAmount = 15 + rand.nextInt(10);
            health = Math.min(health + healAmount, 150);
            potionAmount--;

            log.append("\n\nYou heal " + healAmount +
                       " HP! Potions left: " + potionAmount);
        } else {
            log.append("\nNo potions left!");
        }
    }

    public void flee(JTextArea log) {
        if (rand.nextInt(100) > 60) {
            Timer t = new Timer(200, e -> {
                log.append("\n\n>> You fled safely!");
                Timer t2 = new Timer(1500, ev -> game.returnToMap());
                t2.setRepeats(false);
                t2.start();
            });
            t.setRepeats(false);
            t.start();
        } else {
            log.append("\nYou failed to flee!");
        }
    }

    // ================= EQUIPMENT =================

    public void equipWeapon(Weapon weapon) {
        equippedWeapon = weapon;
        recalcStats();
    }

    public void equipArmor(Armor armor) {
        equippedArmor = armor;
        recalcStats();
    }

    public void unequipWeapon() {
        equippedWeapon = null;
        recalcStats();
    }

    public void unequipArmor() {
        equippedArmor = null;
        recalcStats();
    }

    private void recalcStats() {
        attackPower = baseAttack +
                (equippedWeapon != null ? equippedWeapon.damage : 0);

        defense = baseDefense +
                (equippedArmor != null ? equippedArmor.defense : 0);
    }

    // ================= SELLING =================

    /**
     * Sells a weapon if tradable, adds 70% of price to coins, and replenishes it in the shop.
     * Returns the amount earned, or 0 if the weapon cannot be sold.
     */
    public int sellWeapon(Weapon weapon) {
        if (!weapon.tradable) {
            return 0; // Cannot sell non-tradable (legendary) items
        }
        if (!weapons.contains(weapon)) {
            return 0; // Player doesn't own this weapon
        }

        int sellPrice = (int) Math.round(weapon.price * 0.7);
        weapons.remove(weapon);
        coins += sellPrice;
        
        // Replenish the weapon in the shop
        availableWeapons.add(weapon);

        return sellPrice;
    }

    /**
     * Sells armor if tradable, adds 70% of price to coins, and replenishes it in the shop.
     * Returns the amount earned, or 0 if the armor cannot be sold.
     */
    public int sellArmor(Armor armor) {
        if (!armor.tradable) {
            return 0; // Cannot sell non-tradable (legendary) items
        }
        if (!armors.contains(armor)) {
            return 0; // Player doesn't own this armor
        }

        int sellPrice = (int) Math.round(armor.price * 0.7);
        armors.remove(armor);
        coins += sellPrice;
        
        // Replenish the armor in the shop
        availableArmor.add(armor);

        return sellPrice;
    }
}
