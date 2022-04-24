package aktienverwaltung;

public class Account extends Person {
    private String hashedPassword;
    private boolean darkMode = false;
    private Portfolio portfolio;
    private Bank bank;

    //Konstruktor
    public Account(String firstName, String lastName, String email, String hashedPassword, String birthdate, String phonenumber, Portfolio portfolio, Bank bank) {
        super(firstName, lastName, email, birthdate, phonenumber);
        this.hashedPassword = hashedPassword;
        this.portfolio = portfolio;
        this.bank = bank;
    }

    //password
    public String getHashedPassword() {
        return hashedPassword;
    }
    
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public boolean getDarkmode() {
        return darkMode;
    }

    public void setDarkmode(boolean value) {
        this.darkMode = value;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }
}
