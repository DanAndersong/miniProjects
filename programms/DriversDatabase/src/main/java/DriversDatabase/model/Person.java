package DriversDatabase.model;
import java.util.GregorianCalendar;

public class Person {
    private String name;
    private String lastName;
    private String personId;
    private GregorianCalendar birth;
    private boolean sex; //true - male
    private String secondName;
    private Boolean donor;
    private Boolean veteran;

    public Person(String name, String lastName, String personId, GregorianCalendar birth) {
        this.name = name;
        this.lastName = lastName;
        this.personId = personId;
        this.birth = birth;
    }
    //add second name
    public Person(String name, String lastName, String personId, String secondName, GregorianCalendar birth) {
        this.name = name;
        this.lastName = lastName;
        this.secondName = secondName;
        this.personId = personId;
        this.birth = birth;
    }
    //add sex
    public Person(String name, String lastName, String personId, GregorianCalendar birth, Boolean sex) {
        this.name = name;
        this.lastName = lastName;
        this.personId = personId;
        this.birth = birth;
        this.sex = sex;
    }
    //add secondName & sex
    public Person(String name, String lastName, String secondName, String personId, GregorianCalendar birth, Boolean sex) {
        this.name = name;
        this.lastName = lastName;
        this.secondName = secondName;
        this.personId = personId;
        this.birth = birth;
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPersonId() {
        return personId;
    }

    public void setId(String personId) {
        this.personId = personId;
    }

    public GregorianCalendar getBirth() {
        return birth;
    }

    public void setBirth(GregorianCalendar birth) {
        this.birth = birth;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Boolean getDonor() {
        return donor;
    }

    public void setDonor(Boolean donor) {
        this.donor = donor;
    }

    public Boolean getVeteran() {
        return veteran;
    }

    public void setVeteran(Boolean veteran) {
        this.veteran = veteran;
    }
}
