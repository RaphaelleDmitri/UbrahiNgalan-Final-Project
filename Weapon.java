public class Weapon {
    public String name;
    public int damage;
    public int price; // optional, used in ShopPanel

    public Weapon(String name, int damage, int price) {
        this.name = name;
        this.damage = damage;
        this.price = price;
    }

    public String getName(){
        return name;
    }

    @Override
public String toString() {
    return name + " (+" + damage + " ATK)";
}
}