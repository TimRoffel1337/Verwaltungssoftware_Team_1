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

    public int getStockAmount(String stock) {
        int amount = 0;
        for (String st : stocks) {
            if (st.equals(stock)) {
                amount++;
            }
        }

        return amount;
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

    public void removeStock(String stock, int amount) {
        if (this.stocks == null) {
            return;
        } else {
            int counter = 0;

            for (String st : this.stocks) {
                if (st.equals(stock)) {
                    counter++;
                }
            }

            String[] temp = new String[this.stocks.length - amount];
            if (counter == amount) {
                int j = 0;
                for (int i = 0; i < this.stocks.length; i++) {
                    if (!this.stocks[i].equals(stock)) {
                        temp[j] = this.stocks[i];
                        j++;
                    }
                }

                this.stocks = temp;

                return;
            }

            for (int i = 0; i < counter - amount; i++) {
                temp[i] = stock;
            }

            for (int i = 0; i < this.stocks.length; i++) {
                if (!this.stocks[i].equals(stock)) {
                    temp[counter - amount] = this.stocks[i];
                    counter++;
                }
            }

            this.stocks = temp;
        }
    }   
}