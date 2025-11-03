import javax.swing.JTextArea;

public abstract class Character {
    protected String name;
    protected int health;
    protected int attackPower;
    protected int defense;
    protected int potionAmount;
    protected int coins;

    public Character(String name, int health, int attackPower, int defense, int potionAmount,int coins){
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
        this.defense = defense;
        this.potionAmount = potionAmount;
        this.coins = coins;
    }

    public abstract void attack(Character target, JTextArea log);

    public boolean isAlive(){ return health > 0; }
    public int getHealth(){ return health; }
    public String getName(){ return name; }
}
