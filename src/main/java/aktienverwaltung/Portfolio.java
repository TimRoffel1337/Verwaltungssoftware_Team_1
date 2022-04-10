package aktienverwaltung;

public class Portfolio {
    private float money;
    private String[] stocks;

    public Portfolio(float money) {
        this.money = money;
    }
    
    public void setMoney(float money) {
        this.money = money;
    }

    public float getMoney() {
        return this.money;
    }

    public void removeMoney(float money) {
        this.money -= money;
    }

    public void addMoney(float money) {
        this.money += money;
    }

    public String[] getStocks() {
        return this.stocks;
    }

    public void setStocks(String[] stocks) {
        this.stocks = stocks;
    }

    public void addStock(String stock, int amount) {
        if (this.stocks == null) {
            for (int i = 0; i < amount; i++) {
                this.stocks = new String[1];
                this.stocks[0] = stock;
            }
        } else {
            for (int j = 0; j < amount; j++) {
                String[] temp = new String[this.stocks.length + 1];
                for (int i = 0; i < this.stocks.length; i++) {
                    temp[i] = this.stocks[i];
                }
                temp[temp.length - 1] = stock;
                this.stocks = temp;
            }
        }
    }

    //remove a stock
    public void removeStock(String stock, int amount) {
        if (this.stocks == null) {
            return;
        } else {
            String[] newStocks = new String[this.stocks.length - amount];
            for (int i = 0; i < this.stocks.length; i++) {
                
            }
        }
    }
}