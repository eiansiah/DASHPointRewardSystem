package dashpointrewardsystem;

import java.time.LocalDate;

public abstract class User {

    private String userID;
    private String username;
    private String password;
    private String contactNum;
    private LocalDate registrationDate;

    //Constructor
    protected User() {

    }

    protected User(String userID, String username, String password, String contactNum, LocalDate registrationDate) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.contactNum = contactNum;
        this.registrationDate = registrationDate;
    }

    //region Getter and Setter
    //Getter
    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public String getContactNum() {
        return contactNum;
    }

    //Setter
    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    //endregion
    @Override
    public String toString() {
        return "User{\nUser ID: " + userID + "\nUsername: " + username + "\nPassword: " + password + "\nContactNum: " + contactNum + "\nRegistrationDate: " + registrationDate + '}';
    }

    public abstract String displayProfileInfo();
}
