package dashpointrewardsystem;

import java.time.LocalDate;

public class Staff extends User {

    private String accessLevel;

    //Constructor
    public Staff() {
        super();
    }

    public Staff(String userID, String username, String accessLevel, String contactNum, LocalDate registrationDate, String password){
        super(userID, username, password, contactNum, registrationDate);

        this.accessLevel = accessLevel;
    }

    //Getter
    public String getAccessLevel() {
        return accessLevel;
    }

    //Setter
    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String displayProfileInfo() {
        return "\nStaff ID: " + super.getUserID() + "\n" + "Staff Name: " + super.getUsername() + "\n" + "Contact Number: " + super.getContactNum() + "\n" + "Access Level: " + accessLevel + "\nYour first day with us is on " + super.getRegistrationDate() + ".";
    }

    @Override
    public String toString() {
        return "Staff{" + super.toString() + "\nAccess Level: " + accessLevel + '}';
    }

}
