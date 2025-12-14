public class Weapon {
    public String name;
    public int damage;
    public int price; // optional, used in ShopPanel
    public boolean tradable; // whether this weapon can be sold

    public Weapon(String name, int damage, int price) {
        this(name, damage, price, true);
    }

    public Weapon(String name, int damage, int price, boolean tradable) {
        this.name = name;
        this.damage = damage;
        this.price = price;
        this.tradable = tradable;
    }

    public String getName(){
        return name;
    }

    @Override
public String toString() {
    return name + " (+" + damage + " ATK)";
}
}