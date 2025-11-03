import java.util.Random;
import javax.swing.JTextArea;
import javax.swing.Timer;
public class Player extends Character {
    private Random rand = new Random();
    private Main game;
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
        int dmg = attackPower + weaponBonus + rand.nextInt(5); // base attack + weapon + random
        dmg = dmg - (target.defense/4);
        String weaponName = equippedWeapon != null ? equippedWeapon.getName() : "Rusty Sword";
        target.health -= dmg; 
        
        log.append("\nYou strike "+target.getName()+ " with your " + weaponName +"!");
        log.append("\nYou deal " + dmg+ " damage!");
    }

    public void defend(JTextArea log) {
        int block = (equippedArmor != null ? equippedArmor.defense : 0) + rand.nextInt(15) + 5;
        this.defense += block;
        log.append("\nYou raise your shield, temporarily increasing your defense by " + block + "!");
    }

    public void heal(JTextArea log) {
        if (potionAmount > 0) {
            int healAmount = 15 + rand.nextInt(10); // random healing
            health += healAmount;
            potionAmount--;
            log.append("\nYou use a potion and heal " + healAmount + " HP! Potions left: " + potionAmount);
        } else {
            log.append("\nNo potions left! You cannot heal.");
        }
    }

    public void flee(JTextArea log){
        int fleeChance = 0 + rand.nextInt(100);
        if(fleeChance > 60){
            Timer t = new Timer(200, e -> {
                log.append("\n\n>> You fled safely!");
                // delay to return to map
                Timer t2 = new Timer(1500, ev -> game.returnToMap());
                t2.setRepeats(false);
                t2.start();
            });
            t.setRepeats(false);
            t.start();
        }else{
            log.append("You failed to flee!");
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
