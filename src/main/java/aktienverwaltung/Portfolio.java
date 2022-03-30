package aktienverwaltung;

public class Portfolio {
    private float money;
    private Stock[] stocks;

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

    public Stock[] getStocks() {
        return this.stocks;
    }

    public void setStocks(Stock[] stocks) {
        this.stocks = stocks;
    }

    public void addStock(Stock stock, int amount) {
        if (this.stocks == null) {
            for (int i = 0; i < amount; i++) {
                this.stocks = new Stock[1];
                this.stocks[0] = stock;
            }
        } else {
            for (int j = 0; j < amount; j++) {
                Stock[] temp = new Stock[this.stocks.length + 1];
                for (int i = 0; i < this.stocks.length; i++) {
                    temp[i] = this.stocks[i];
                }
                temp[temp.length - 1] = stock;
                this.stocks = temp;
            }
        }
    }

    //remove a stock
    public void removeStock(Stock stock, int amount) {
        if (this.stocks == null) {
            return;
        } else {
            for (int k = 0; k < amount; k++) {
                Stock[] temp = new Stock[this.stocks.length - 1];
                int j = 0;
                for (int i = 0; i < this.stocks.length; i++) {
                    if (this.stocks[i] != stock) {
                        temp[j] = this.stocks[i];
                        j++;
                    }
                }
                this.stocks = temp;
            }
        }
    }
}