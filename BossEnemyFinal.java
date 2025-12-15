import java.util.Random;
import javax.swing.*;
import javax.swing.Timer;

public class BossEnemyFinal extends Enemy {

    private Random rand = new Random();

    private int patternIndex = 0;
    private boolean phase2 = false;
    private boolean rageCharged = false;

    private boolean introDone = false;
    private boolean luraSummoned = false;

    private final int[] pattern = {0, 1, 2, 3};

    private Ally lura;


    public BossEnemyFinal(String name, int health, int attackPower, int defense,
                          int potionAmount, int coins) {
        super(name, health, attackPower, defense, potionAmount, coins);
        this.maxHealth = health;
    }

    public void intro(JTextArea log, Player player) {
        if (introDone) return;
        introDone = true;

        log.append("\n>> Shadows spread across the battlefield…");
        log.append("\n>> Eum, The VoidMother, emerges!");
        log.append("\n>> Exhausted from the previous fight, you feel weaker!");

        player.attackPower = (int) (player.attackPower * 0.85);
        player.defense = (int) (player.defense * 0.95);
    }


    public void bossTurn(Player player, JTextArea log, int lastPlayerAction) {

        
        if (checkLuraSave(player, log)) return;

        // Phase 2 trigger
        if (!phase2 && health <= maxHealth / 2) {
            phase2 = true;
            log.append("\n>> Eum absorbs the darkness to strenghen her prowess!");
            log.append("\n>> PHASE 2 BEGINS!");
        }

        int move = pattern[patternIndex];

        switch (move) {
            case 0 -> shadowStrike(player, log);
            case 1 -> flameWave(player, log);
            case 2 -> powerCharge(log);
            case 3 -> misfire(player, log);
        }

        
        if (rageCharged && move != 2) {
            devastatingBlow(player, log);
            rageCharged = false;
        }

       
        if (checkLuraSave(player, log)) return;

        
        if (phase2 && rand.nextInt(100) < 40) {
            shadowStrike(player, log);
        }

        patternIndex = (patternIndex + 1) % pattern.length;

        // Lura auto-attack
        if (luraSummoned && lura.health > 0) {
            int dmg = lura.attackPower + rand.nextInt(5);
            log.append("\n>> Lura strikes Eum for " + dmg + " damage!");
            this.health -= dmg;
        }      
    }

    
       
    private boolean checkLuraSave(Player player, JTextArea log) {
        if (!luraSummoned && player.health > 0 && player.health <= player.maxHealth * 0.3) {
            luraSummoned = true;
            lura = new Ally("Lura, the Previous Hero", 150, 45, 10);
            log.append("\n>> Eum lands a crushing blow!");
            log.append("\n>> As your vision fades, you feel the darkness swallowing you");

            Timer luraSpawn = new Timer(1500, e -> {
                log.append("\n>> A blinding light shatters the darkness!");
                log.append("\n>> LURA, THE PREVIOUS HERO, STANDS BESIDE YOU!");
                log.append("\n>> Your wounds are healed by his light!");

                player.health = player.maxHealth / 2;
                player.defense = (int) (player.defense * 1.3);
                rageCharged = false;
                patternIndex = 0;
            });
            
            luraSpawn.setRepeats(false);
            luraSpawn.start();

            return true; // boss loses this turn
        }
        return false;
    }

    
    private void dealDamage(Player target, int dmg, JTextArea log) {
        dmg = Math.max(0, dmg);

        int newHP = target.health - dmg;

        if (!luraSummoned && newHP <= 0) {
            target.health = 1;
            return;
        }

        target.health = newHP;
        log.append(" (" + dmg + " dmg)");
    }

    
    private void shadowStrike(Player target, JTextArea log) {
        int dmg = attackPower + rand.nextInt(10) - target.defense / 4;
        log.append("\n\n" + name + " calls forth shadow blades to attack you!");
        dealDamage(target, dmg, log);
    }

    private void flameWave(Player target, JTextArea log) {
        int dmg = 5 + rand.nextInt(8) - target.defense / 4;
        log.append("\n\n" + name + " unleashes a wave of void flames that burns the battlefield!");
        dealDamage(target, dmg, log);
    }

    private void powerCharge(JTextArea log) {
        rageCharged = true;
        log.append("\n\n" + name + " gathers catastrophic energy!");
    }

    private void misfire(Player target, JTextArea log) {
        int dmg = 2 + rand.nextInt(4);
        log.append("\n\n" + name + " failed to maximise her power, dealing minor damage to you!");
        dealDamage(target, dmg, log);
    }

    private void devastatingBlow(Player target, JTextArea log) {
        int dmg = attackPower + 20 + rand.nextInt(15) - target.defense / 5;
        log.append("\n\n" + name + " unleashes a DEVASTATING BLOW of void energy!");
        dealDamage(target, dmg, log);
    }

    public Ally getAlly() {
        return lura;
    }

    public boolean isPhase2() {
        return phase2;
    }
}
