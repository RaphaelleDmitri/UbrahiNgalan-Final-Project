import javax.swing.*;

public class Ally extends Character {

    public Ally(String name, int health, int attackPower, int defense) {
        super(name, health, attackPower, defense, 0, 0); // Added missing parameters
        this.maxHealth = health;
    }

    @Override
    public void attack(Character target, JTextArea log) {
        int dmg = this.attackPower; // simple attack
        dmg = Math.max(0, dmg - target.defense / 4);
        target.health -= dmg;

        log.append("\n>> " + name + " attacks " + target.name + " for " + dmg + " damage!");
    }
}
