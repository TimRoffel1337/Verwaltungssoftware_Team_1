package aktienverwaltung;

public class Account extends Person {
    private String hashedPassword;

    //Konstruktor
    public Account(String firstName, String lastName, String email, String hashedPassword, String birthdate, String phonenumber) {
        super(firstName, lastName, email, birthdate, phonenumber);
        this.hashedPassword = hashedPassword;
    }

    //password
    public String getHashedPassword() {
        return hashedPassword;
    }
    
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}
