import java.util.Random;
import javax.swing.*;

public class BossEnemyWitch extends Enemy {
    private Random rand = new Random();

    private int patternIndex = 0;
    private boolean chargeActive = false;
    private boolean punishActive = false;
    private boolean phase2 = false;

    private final int[] pattern = new int[]{0,1,2,3};

    private MinionEnemy solomon;       
    private boolean minionAdded = false; 

    public BossEnemyWitch(String name, int health, int attackPower, int defense, int potionAmount, int coins) {
        super(name, health, attackPower, defense, potionAmount, coins);
        this.maxHealth = health;
    }

    // Called each boss turn
    public void bossTurn(Player player, JTextArea log, int lastPlayerAction) {
        // Punish triggers
        if (lastPlayerAction == 2 || lastPlayerAction == 3) {
            punishActive = true;
            log.append("\n" + name + " narrows her eyes — she smells weakness.");
        }

        // Phase 2 trigger
        if (!phase2 && this.health <= maxHealth / 2) {
            phase2 = true;
            log.append("\n>> " + name + " enters a furious second phase!");
        }

        // Pattern attack
        Character target = player;

        // Punishment attack has priority
        if (punishActive) {
            performPunishment(target, log);
            return;
        }

        // Execute pattern move
        int move = pattern[patternIndex];
        switch(move){
            case 0 -> rapidJab(target, log);
            case 1 -> weakJab(target, log);
            case 2 -> chargeUp(log);
            case 3 -> falseWindup(target, log);
        }

        if (chargeActive && move != 2) {
            heavyStrike(target, log);
            chargeActive = false;
        }

        if (phase2) {
            if (rand.nextInt(100) < 50) quickHit(target, log); // 50% chance 
            if (rand.nextInt(100) < 30) heavyStrike(target, log); // 30% chance 
        }

        patternIndex = (patternIndex + 1) % pattern.length;
        
        if (!minionAdded) {
            solomon = new MinionEnemy("Solomon, the Witch's Minion", 50, attackPower/2, 5, 0, 0);
        }
    }

    // Getter for BattlePanel to add the minion only once
    public MinionEnemy getPendingSummon() {
        if (!minionAdded && solomon != null) {
            minionAdded = true; // mark as already given
            return solomon;
        }
        return null;
    }

    private void rapidJab(Character target, JTextArea log){
        int dmg = attackPower + rand.nextInt(9);
        dmg = Math.max(0, dmg - target.defense / 4);
        target.health -= dmg;
        log.append("\n\n" + name + " sends balls of flames dealing " + dmg + " damage!");
    }

    private void weakJab(Character target, JTextArea log){
        int dmg = 2 + rand.nextInt(5);
        dmg = Math.max(0, dmg - target.defense / 4);
        target.health -= dmg;
        log.append("\n\n" + name + " dances across the battlefield creating a path of flames dealing " + dmg + " damage!");
    }

    private void chargeUp(JTextArea log){
        chargeActive = true;
        log.append("\n\n" + name + " prepares a dangerous spell (charging)!");
    }

    private void falseWindup(Character target, JTextArea log){
        int dmg = 1 + rand.nextInt(3);
        dmg = Math.max(0, dmg - target.defense / 4);
        target.health -= dmg;
        log.append("\n\n" + name + " fails to cast, she grazes you dealing " + dmg + " damage!");
    }

    private void heavyStrike(Character target, JTextArea log){
        int dmg = attackPower + 15 + rand.nextInt(10) - target.defense / 5;
        dmg = Math.max(0, dmg);
        target.health -= dmg;
        log.append("\n\n" + name + " delivers a HEAVY blast of magic dealing " + dmg + " damage!");
    }

    private void quickHit(Character target, JTextArea log){
        int dmg = 3 + rand.nextInt(5);
        dmg = Math.max(0, dmg - target.defense / 4);
        target.health -= dmg;
        log.append("\n\n" + name + " summons black holes around you dealing " + dmg + " damage!");
    }

    private void performPunishment(Character target, JTextArea log){
        int dmg = (attackPower / 2) + rand.nextInt(5)- 10;
        dmg = Math.max(0, dmg - target.defense / 8);
        target.health -= dmg;
        log.append("\n\n" + name + " PUNISHES you for your insolence, dealing " + dmg + " damage!");
    }

    public void clearPunish() { punishActive = false; }
    public boolean isPunishActive() { return punishActive; }
    public boolean isPhase2() { return phase2; }
}
