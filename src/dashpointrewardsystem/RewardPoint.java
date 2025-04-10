package dashpointrewardsystem;

import java.time.LocalDateTime;

public class RewardPoint {

    private String rewardPtID;
    private LocalDateTime earnDate;
    private Customer customer;
    private Staff staff;
    private int ptEarned;
    private static int numOfRewardPoint;

    public RewardPoint() {
        numOfRewardPoint++;
    }

    public RewardPoint(String rewardPtID, LocalDateTime earnDate, Customer customer, Staff staff, int ptEarned) {
        this.rewardPtID = rewardPtID;
        this.earnDate = earnDate;
        this.customer = customer;
        this.staff = staff;
        this.ptEarned = ptEarned;
        numOfRewardPoint++;
    }

    //getter
    public String getRewardPtID() {
        return rewardPtID;
    }

    public LocalDateTime getEarnDate() {
        return earnDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Staff getStaff() {
        return staff;
    }

    public int getPtEarned() {
        return ptEarned;
    }

    public static int getNumOfRewardPoint() {
        return numOfRewardPoint;
    }

    //setter
    public void setEarnDate(LocalDateTime earnDate) {
        this.earnDate = earnDate;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public void setPtEarned(int ptEarned) {
        this.ptEarned = ptEarned;
    }

    public void setRewardPtID(String rewardPtID) {
        this.rewardPtID = rewardPtID;
    }

    public static void setNumOfRewardPoint(int numOfRewardPoint) {
        RewardPoint.numOfRewardPoint = numOfRewardPoint;
    }

    @Override
    public String toString() {
        return ("Reward Point ID: " + rewardPtID + "\n" + "Points earned date: " + earnDate + "\n" + "Customer Details: " + customer + "\n" + "Staff Details: " + staff + "\n" + "Points earned: " + ptEarned + "\n");
    }

}
