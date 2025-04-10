/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dashpointrewardsystem;

/**
 *
 * @author vivia
 */
public class PointRedemption {

    private String recordID;
    private Customer customer;
    private Reward reward;
    private static int numOfRedemption;

    //Constructor
    public PointRedemption() {
        numOfRedemption++;
    }

    public PointRedemption(String recordID, Customer customer, Reward reward) {
        this.recordID = recordID;
        this.customer = customer;
        this.reward = reward;
        numOfRedemption++;
    }

    //Getter
    public String getRecordID() {
        return recordID;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Reward getReward() {
        return reward;
    }

    public static int getNumOfRedemption() {
        return numOfRedemption;
    }

    //Setter
    public void setRecordID(String recordID) {
        this.recordID = recordID;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public static void setNumOfRedemption(int numOfRedemption) {
        PointRedemption.numOfRedemption = numOfRedemption;
    }

    //toString
    @Override
    public String toString() {
        return "PointRedemption{" + "recordID=" + recordID + ", customer=" + customer + ", reward=" + reward + '}';
    }

    public int calcPtRedeemed() {
        return reward.getPtRequired();
    }

}
