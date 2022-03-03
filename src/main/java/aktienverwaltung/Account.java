package aktienverwaltung;

public class Account extends Person {
    String username;
    String password;

    //Konstruktor
    public Account(String name, String birthdate, String email, String phonenumber, String username, String password) {
        super(name, birthdate, email, phonenumber);
        this.username = username;
        this.password = password;
    }

    //username
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    //password
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
