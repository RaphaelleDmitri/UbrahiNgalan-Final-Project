import javax.swing.JTextArea;

public abstract class Character {
    protected String name;
    protected int health;
    protected int maxHealth;
    protected int attackPower;
    protected int defense;
    protected int baseDefense; 
    protected int potionAmount;
    protected int coins;
    protected int tempBlock = 0;
    public Character(String name, int health, int attackPower, int defense, int potionAmount, int coins) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.attackPower = attackPower;
        this.defense = defense;
        this.baseDefense = defense; 
        this.potionAmount = potionAmount;
        this.coins = coins;
    }

    public abstract void attack(Character target, JTextArea log);

    public boolean isAlive() { 
        return health > 0; 
    }

    public int getHealth() { 
        return health; 
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public String getName() { 
        return name; 
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getDefense() {
        return defense;
    }

    public int getPotionAmount() {
        return potionAmount;
    }

    public int getCoins() {
        return coins;
    }

    public void setHealth(int health) {
        this.health = Math.min(health, maxHealth); 
    }

    public void addCoins(int amount) {
        this.coins += amount;
    }

    public void addPotion() {
        this.potionAmount++;
    }

    public void resetDefense() {
        this.defense = this.baseDefense;
    }
}