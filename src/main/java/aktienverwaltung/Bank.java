package aktienverwaltung;

public class Bank {
    private float money;
    
    public Bank(float money) {
        this.money = money;
    }

    public float getMoney() {
        return this.money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public void addMoney(float amount) {
        this.money += amount;
    }

    public void removeMoney(float amount) {
        this.money -= amount;
    }
}