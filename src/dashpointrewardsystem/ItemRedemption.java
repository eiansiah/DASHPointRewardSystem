/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dashpointrewardsystem;

import java.time.LocalDateTime;

/**
 *
 * @author vivia
 */
public class ItemRedemption extends PointRedemption {

    private LocalDateTime redeemDate;
    private int redeemedQtyThisRecord;
    private static int numOfItemRedemption;

    //Constructor
    public ItemRedemption() {
        super();
        numOfItemRedemption++;
    }

    public ItemRedemption(String recordID, LocalDateTime redeemDate, Customer customer, Reward reward, int redeemedQtyThisRecord) {
        super(recordID, customer, reward);
        this.redeemDate = redeemDate;
        this.redeemedQtyThisRecord = redeemedQtyThisRecord;
        numOfItemRedemption++;
    }

    //Getter
    public LocalDateTime getRedeemDate() {
        return redeemDate;
    }

    public int getRedeemedQtyThisRecord() {
        return redeemedQtyThisRecord;
    }

    public static int getNumOfItemRedemption() {
        return numOfItemRedemption;
    }

    //Setter
    public void setRedeemDate(LocalDateTime redeemDate) {
        this.redeemDate = redeemDate;
    }

    public void setRedeemedQtyThisRecord(int redeemedQtyThisRecord) {
        this.redeemedQtyThisRecord = redeemedQtyThisRecord;
    }

    public static void setNumOfItemRedemption(int numOfItemRedemption) {
        ItemRedemption.numOfItemRedemption = numOfItemRedemption;
    }

    @Override
    public String toString() {
        return super.toString() + "\nRedeem Date: " + redeemDate + "\nRedeemed Qty for This Record: " + redeemedQtyThisRecord;
    }

    //Calculate
    @Override
    public int calcPtRedeemed() {
        return super.getReward().getPtRequired() * redeemedQtyThisRecord;
    }

}
