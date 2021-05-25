package mmr.dto;

public class User {

    int id, birthyear;
    String username, sirname, lastname, email, password, roleType;

    String subscriptionTier;
    long premiumEndDate, cardNumber;


    public User(int birthyear, String username, String sirname, String lastname, String email, String password, String roleType, String subscriptionTier, long premiumEndDate, long cardNumber) {
        this.birthyear = birthyear;
        this.username = username;
        this.sirname = sirname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.roleType = roleType;
        this.subscriptionTier = subscriptionTier;
        this.premiumEndDate = premiumEndDate;
        this.cardNumber = cardNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBirthyear() {
        return birthyear;
    }

    public void setBirthyear(int birthyear) {
        this.birthyear = birthyear;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSirname() {
        return sirname;
    }

    public void setSirname(String sirname) {
        this.sirname = sirname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getSubscriptionTier() {
        return subscriptionTier;
    }

    public void setSubscriptionTier(String subscriptionTier) {
        this.subscriptionTier = subscriptionTier;
    }

    public long getPremiumEndDate() {
        return premiumEndDate;
    }

    public void setPremiumEndDate(long premiumEndDate) {
        this.premiumEndDate = premiumEndDate;
    }

    public long getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(long cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", birthyear=" + birthyear +
                ", username='" + username + '\'' +
                ", sirname='" + sirname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", roleType='" + roleType + '\'' +
                ", subscriptionTier='" + subscriptionTier + '\'' +
                ", premiumEndDate=" + premiumEndDate +
                ", cardNumber=" + cardNumber +
                '}';
    }
}
