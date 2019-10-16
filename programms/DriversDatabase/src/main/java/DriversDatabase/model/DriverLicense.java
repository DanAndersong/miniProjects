package DriversDatabase.model;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class DriverLicense {
    private Person person;

    private String idLicense;
    private List<Categories> categories;
    private List<Restrictions> restrictions;
    private Date dateOfIssue;
    private LocalDateTime expiration;
    private boolean otherLicense;
    private boolean donor;
    private boolean veteran;

    public DriverLicense(Person person, Categories ... categories) {
        this.categories = new ArrayList<>();
        Collections.addAll(this.categories, categories);

        this.person = person;
        this.idLicense = categories[0] + person.getPersonId();
        this.dateOfIssue = new Date();
        this.expiration = LocalDateTime.from(dateOfIssue.toInstant()).plusYears(5);
        this.veteran = person.getVeteran();
        this.donor = person.getDonor();
    }

    public void addCategories(Categories ... categories) {
        Collections.addAll(this.categories, categories);
    }

    public void removeCategory(Categories ... categoriesForRemove) {
        for (Categories categoryForRemove : categoriesForRemove) {
            this.categories.remove(categoryForRemove);
        }
    }

    public void addRestrictions(Restrictions ... restrictions) {
        Collections.addAll(this.restrictions, restrictions);
    }

    public void removeRestrictions(Restrictions ... restrictionsForRemove) {
        for (Restrictions restrictionForRemove : restrictionsForRemove) {
            this.restrictions.remove(restrictionForRemove);
        }
    }

    public void setOtherLicense(boolean otherLicense) {
        this.otherLicense = otherLicense;
    }

    public void setDonor(boolean donor) {
        this.donor = donor;
    }

    public void setVeteran(boolean veteran) {
        this.veteran = veteran;
    }

    public Person getPerson() {
        return person;
    }

    public String getIdLicense() {
        return idLicense;
    }

    public List<Categories> getCategories() {
        return categories;
    }

    public List<Restrictions> getRestrictions() {
        return restrictions;
    }

    public Date getDateOfIssue() {
        return dateOfIssue;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public boolean isOtherLicense() {
        return otherLicense;
    }

    public boolean isDonor() {
        return donor;
    }

    public boolean isVeteran() {
        return veteran;
    }
}