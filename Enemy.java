import java.util.Random;
import javax.swing.JTextArea;

public class Enemy extends Character {
    Random rand = new Random();
    public int maxHealth;
    public Enemy(String name, int health, int attackPower, int defense, int potionAmount, int coins){
        super(name, health, attackPower, defense, potionAmount, coins);
        this.maxHealth = health;
    }

    @Override
    public void attack(Character target, JTextArea log) {
        Random rand = new Random();
        int rawDamage = attackPower + rand.nextInt(5);
    
        // Subtract target defense and any active block
        int totalDefense = (target.defense / 4) + target.tempBlock;
        int dmg = rawDamage - totalDefense;
    
        if (dmg < 0) dmg = 0;
    
        // Apply damage
        target.health -= dmg;

        target.tempBlock = 0;

        log.append("\n\n" + name + " deals " + dmg + " damage!");
    }

}
