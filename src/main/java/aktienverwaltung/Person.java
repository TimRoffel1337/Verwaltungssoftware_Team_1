package aktienverwaltung;
public class Person {
    private String firtName;
    private String lastName;
    private String birthdate;
    private String email;
    private String phonenumber;

    //Konstruktor
    public Person(String firstName, String lastName, String email, String birthdate, String phonenumber) {
        this.firtName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.email = email;
        this.phonenumber = phonenumber;
    }

    //name
    public String getFirstName() {
        return this.firtName;
    }

    public String getLastName() {
        return this.lastName;
    }
    
    public void setFirstName(String name) {
        this.firtName = name;
    }

    public void setLastName(String name) {
        this.lastName = name;
    }

    //birthdate
    public String getBirthdate() {
        return birthdate;
    }
    
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    //email
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
      
    //phonenumber
    public String getPhonenumber() {
        return phonenumber;
    }
    
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
