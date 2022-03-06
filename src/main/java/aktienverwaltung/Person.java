package aktienverwaltung;
public class Person {
    String name;
    String birthdate;
    String email;
    String phonenumber;

    //Konstruktor
    public Person(String name, String birthdate, String email, String phonenumber) {
        this.name = name;
        this.birthdate = birthdate;
        this.email = email;
        this.phonenumber = phonenumber;
    }

    //name
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
      
    //phonenumberr
    public String getPhonenumber() {
        return phonenumber;
    }
    
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
