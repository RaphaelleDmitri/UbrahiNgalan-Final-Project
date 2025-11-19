import java.util.LinkedList;
import java.util.Random;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class Player extends Character {
    private Random rand = new Random();
    private Main game;

    public Weapon equippedWeapon;
    public Armor equippedArmor;

    // Shop persistence: store remaining items
    public LinkedList<Weapon> availableWeapons = new LinkedList<>();
    public LinkedList<Armor> availableArmor = new LinkedList<>();

    public Player(String name, int health, int attackPower, int defense, int potionAmount, int coins, Main game) {
        super(name, health, attackPower, defense, potionAmount, coins);
        this.equippedWeapon = null;
        this.equippedArmor = null;
        this.game = game;

        // Initialize shop items once
        availableWeapons.add(new Weapon("Iron Sword", 5, 50));
        availableWeapons.add(new Weapon("Steel Sword", 10, 100));
        availableWeapons.add(new Weapon("Mythril Sword", 15, 200));
        availableWeapons.add(new Weapon("Diamond Sword", 25,400));
        availableWeapons.add(new Weapon("Sword of Demacia", 40,800));
        

        availableArmor.add(new Armor("Leather Armor", 8, 30));
        availableArmor.add(new Armor("Chainmail Armor", 12, 60));
        availableArmor.add(new Armor("Steel Armor", 16, 120));
        availableArmor.add(new Armor("Diamond Armor", 24, 200));
        availableArmor.add(new Armor("Armor of Retribution", 30, 300));

    }

    @Override
    public void attack(Character target, JTextArea log) {
        int weaponBonus = equippedWeapon != null ? equippedWeapon.damage : 0;
        int dmg = attackPower + weaponBonus + rand.nextInt(5);
        dmg = dmg - (target.defense / 4);
        String weaponName = equippedWeapon != null ? equippedWeapon.getName() : "Rusty Sword";
        target.health -= dmg;

        log.append("\n\nYou strike " + target.getName() + " with your " + weaponName + "!");
        log.append("\nYou deal " + dmg + " damage!");
    }

    public void defend(JTextArea log) {
        int block = (equippedArmor != null ? equippedArmor.defense : 0) + rand.nextInt(5);
        this.defense += block;
        log.append("\n\nYou raise your shield, temporarily increasing your defense by " + block + "!");
    }

    public void heal(JTextArea log) {
        if (potionAmount > 0) {
            int healAmount = 15 + rand.nextInt(10);
            health += healAmount;

            if (health > 150) {
                health = 150;
                log.append("\n\nHealth has reached maximum! Future healing won't be effective.");
            }

            potionAmount--;
            log.append("\n\nYou use a potion and heal " + healAmount + " HP! Potions left: " + potionAmount);
        } else {
            log.append("\nNo potions left! You cannot heal.");
        }
    }

    public void flee(JTextArea log) {
        int fleeChance = rand.nextInt(100);
        if (fleeChance > 60) {
            Timer t = new Timer(200, e -> {
                log.append("\n\n>> You fled safely!");
                Timer t2 = new Timer(1500, ev -> game.returnToMap());
                t2.setRepeats(false);
                t2.start();
            });
            t.setRepeats(false);
            t.start();
        } else {
            log.append("You failed to flee!");
        }
    }

    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
        attackPower += weapon.damage;
    }

    public void equipArmor(Armor armor) {
        this.equippedArmor = armor;
        defense += armor.defense;
    }
}
