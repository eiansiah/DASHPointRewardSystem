package dashpointrewardsystem;

import java.time.LocalDate;

public class Customer extends User {

    private String email;
    private String loyaltyTier;
    private int balancePoint;
    private int tierPoint;
    private LocalDate tierRefreshDate;

    //Constructor
    public Customer() {
        super();
    }

    /*public Customer(ArrayList<String> data) {
        super(data.get(0), data.get(1), data.get(7), data.get(3), LocalDate.parse(data.get(4), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        this.email = data.get(2);
        this.loyaltyTier = data.get(5);

        if (data.get(8) != null) {
            this.tierPoint = Integer.parseInt(data.get(8));
        } else {
            this.tierPoint = 0;
        }

        this.balancePoint = Integer.parseInt(data.get(6));
        this.tierRefreshDate = LocalDate.parse(data.get(9), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }*/

    public Customer(String userID, String username, String email, String contactNum, LocalDate registrationDate, String loyaltyTier, int balancePoint, String password, int tierPoint, LocalDate tierRefreshDate){
        super(userID, username, password, contactNum, registrationDate);

        this.email = email;
        this.loyaltyTier = loyaltyTier;
        this.tierPoint = tierPoint;
        this.balancePoint = balancePoint;
        this.tierRefreshDate = tierRefreshDate;
    }

    //region Setter Getter
    //Getter
    public String getEmail() {
        return email;
    }

    public String getLoyaltyTier() {
        return loyaltyTier;
    }

    public int getBalancePoint() {
        return balancePoint;
    }

    public int getTierPoint() {
        return tierPoint;
    }

    public LocalDate getTierRefreshDate() {
        return tierRefreshDate;
    }

    //Setter
    public Customer setEmail(String email) {
        this.email = email;
        return this;
    }

    public Customer setLoyaltyTier(String loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
        return this;
    }

    public Customer setBalancePoint(int balancePoint) {
        this.balancePoint = balancePoint;
        return this;
    }

    public Customer setTierPoint(int tierPoint) {
        this.tierPoint = tierPoint;
        return this;
    }

    public void setTierRefreshDate(LocalDate tierRefreshDate) {
        this.tierRefreshDate = tierRefreshDate;
    }

    @Override
    public String toString() {
        return "Customer{\nUser ID: " + super.getUserID() + "\nUsername: " + super.getUsername() + "\nEmail: " + email + "\nContact Number: " + super.getContactNum() + "\nCurrent Loyalty Tier: " + loyaltyTier + "\nCurrent Balance Point: " + balancePoint + "\nCurrent Tier Point: " + tierPoint + "\nTier Refresh Date: " + tierRefreshDate + "}";
    }

    public String displayProfileInfo() {
        if (loyaltyTier.equals("Silver")) {
            return "\nCustomer ID: " + super.getUserID() + "\nCustomer Name: " + super.getUsername() + "\nEmail: " + email + "\nContact Number: " + super.getContactNum() + "\nCurrent Loyalty Tier: " + loyaltyTier + "\nCurrent Balance Point: " + balancePoint + "\nCurrent Tier Point: " + tierPoint + "\n" + (888 - tierPoint) + " more points to next tier." + "\nTier Refresh Date: " + tierRefreshDate + "\nYour first day with us is on " + super.getRegistrationDate() + ".";
        } else if (loyaltyTier.equals("Bronze")) {
            return "\nCustomer ID: " + super.getUserID() + "\nCustomer Name: " + super.getUsername() + "\nEmail: " + email + "\nContact Number: " + super.getContactNum() + "\nCurrent Loyalty Tier: " + loyaltyTier + "\nCurrent Balance Point: " + balancePoint + "\nCurrent Tier Point: " + tierPoint + "\n" + (666 - tierPoint) + " more points to next tier." + "\nTier Refresh Date: " + tierRefreshDate + "\nYour first day with us is on " + super.getRegistrationDate() + ".";
        } else if (loyaltyTier.equals("Member")) {
            return "\nCustomer ID: " + super.getUserID() + "\nCustomer Name: " + super.getUsername() + "\nEmail: " + email + "\nContact Number: " + super.getContactNum() + "\nCurrent Loyalty Tier: " + loyaltyTier + "\nCurrent Balance Point: " + balancePoint + "\nCurrent Tier Point: " + tierPoint + "\n" + (20 - tierPoint) + " more points to next tier." + "\nTier Refresh Date: " + tierRefreshDate + "\nYour first day with us is on " + super.getRegistrationDate() + ".";
        } else {
            return "\nCustomer ID: " + super.getUserID() + "\nCustomer Name: " + super.getUsername() + "\nEmail: " + email + "\nContact Number: " + super.getContactNum() + "\nCurrent Loyalty Tier: " + loyaltyTier + "\nCurrent Balance Point: " + balancePoint + "\nCurrent Tier Point: " + tierPoint + "\nTier Refresh Date: " + tierRefreshDate + "\nYour first day with us is on " + super.getRegistrationDate() + ".";
        }
    }
    //endregion
}
