/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dashpointrewardsystem;

/**
 *
 * @author vivia
 */
public class Reward {

    private int ptRequired;
    private String rewardName;

    //Constructor
    public Reward() {

    }

    public Reward(String rewardName, int ptRequired) {
        this.ptRequired = ptRequired;
        this.rewardName = rewardName;
    }

    //Getter
    public int getPtRequired() {
        return ptRequired;
    }

    public String getRewardName() {
        return rewardName;
    }

    //Setter
    public void setPtRequired(int ptRequired) {
        this.ptRequired = ptRequired;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    @Override
    public String toString() {
        return "\nReward Name: " + rewardName + "\nPoints Required: " + ptRequired;
    }

}
