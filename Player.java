import java.util.Random;
import javax.swing.JTextArea;

public class Player extends Character {
    private Random rand = new Random();

    public Weapon equippedWeapon;
    public Armor equippedArmor;

    public Player(String name, int health, int attackPower, int defense, int potionAmount, int coins) {
        super(name, health, attackPower, defense, potionAmount, coins);
        this.equippedWeapon = null;
        this.equippedArmor = null;
    }

    @Override
    public void attack(Character target, JTextArea log) {
        int weaponBonus = equippedWeapon != null ? equippedWeapon.damage : 0;
        int dmg = attackPower + weaponBonus + rand.nextInt(10); // base attack + weapon + random
        target.health -= dmg;
        log.append("\nYou attack " + target.getName() + " for " + dmg + " damage!");
    }

    public void defend(JTextArea log) {
        int block = (equippedArmor != null ? equippedArmor.defense : 0) + rand.nextInt(15);
        this.defense += block;
        log.append("\nYou raise your shield, temporarily increasing your defense by " + block + "!");
    }

    public void heal(JTextArea log) {
        if (potionAmount > 0) {
            int healAmount = 10 + rand.nextInt(7); // random healing
            health += healAmount;
            potionAmount--;
            log.append("\nYou use a potion and heal " + healAmount + " HP! Potions left: " + potionAmount);
        } else {
            log.append("\nNo potions left! You cannot heal.");
        }
    }

    // Optional helper methods to equip weapons/armor
    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
        attackPower += weapon.damage;
    }

    public void equipArmor(Armor armor) {
        this.equippedArmor = armor;
        defense += armor.defense;
    }
}
