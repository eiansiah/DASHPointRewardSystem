/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dashpointrewardsystem;

/**
 *
 * @author vivia
 */
public class EventRoom extends Reward {

    private String roomNum;
    private String roomType;
    private int paxAllowed;

    //Constructor
    public EventRoom() {
        super();
    }

    public EventRoom(String roomNum, String rewardName, String roomType, int paxAllowed, int ptRequired) {
        super(rewardName, ptRequired);
        this.roomNum = roomNum;
        this.roomType = roomType;
        this.paxAllowed = paxAllowed;
    }

    //Getter
    public String getRoomNum() {
        return roomNum;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getPaxAllowed() {
        return paxAllowed;
    }

    //Setter
    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public void setPaxAllowed(int paxAllowed) {
        this.paxAllowed = paxAllowed;
    }

    @Override
    public String toString() {
        return "\nEvent Room Number: " + roomNum + "\nRoom Name: " + super.getRewardName() + "\nRoom Type: " + roomType + "\nPoint Required: " + super.getPtRequired() + "\nNumber of Pax Allowed: " + paxAllowed;
    }

}
