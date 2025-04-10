/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dashpointrewardsystem;

import java.time.LocalDate;
import Util.GeneralFunction;
/**
 *
 * @author vivia
 */
public class EventRoomReservation extends PointRedemption {

    private LocalDate reservationDate;
    private String status;
    private int numOfExtraPax;
    private static int numOfReservation;

    //Constructor
    public EventRoomReservation() {
        super();
        numOfReservation++;
    }

    public EventRoomReservation(String recordID, Reward reward, LocalDate reservationDate, String status, Customer customer, int numOfExtraPax) {
        super(recordID, customer, reward);
        this.reservationDate = reservationDate;
        this.status = status;
        this.numOfExtraPax = numOfExtraPax;
        numOfReservation++;
    }

    //Getter
    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public String getStatus() {
        return status;
    }

    public int getNumOfExtraPax() {
        return numOfExtraPax;
    }

    public static int getNumOfReservation() {
        return numOfReservation;
    }

    //Setter
    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNumOfExtraPax(int numOfExtraPax) {
        this.numOfExtraPax = numOfExtraPax;
    }

    public static void setNumOfReservation(int numOfReservation) {
        EventRoomReservation.numOfReservation = numOfReservation;
    }

    @Override
    public String toString() {
        if (status.equals("Expired")) {
            return "Reservation Record ID: " + super.getRecordID() + "\nEvent Room Numner: " + ((EventRoom) super.getReward()).getRoomNum() + "\nEvent Room Name: " + super.getReward().getRewardName() +"\nReservation Date: "+reservationDate+ GeneralFunction.color.ANSI_YELLOW + "\nStatus: " + status + GeneralFunction.color.ANSI_RESET + "\nNumber of Extra Pax: " + numOfExtraPax;
        } else if (status.equals("Cancelled")) {
            return "Reservation Record ID: " + super.getRecordID() + "\nEvent Room Numner: " + ((EventRoom) super.getReward()).getRoomNum() + "\nEvent Room Name: " + super.getReward().getRewardName() +"\nReservation Date: "+reservationDate+ GeneralFunction.color.ANSI_RED + "\nStatus: " + status + GeneralFunction.color.ANSI_RESET + "\nNumber of Extra Pax: " + numOfExtraPax;
        } else if (status.equals("Available")) {
            return "Reservation Record ID: " + super.getRecordID() + "\nEvent Room Numner: " + ((EventRoom) super.getReward()).getRoomNum() + "\nEvent Room Name: " + super.getReward().getRewardName() +"\nReservation Date: "+reservationDate+ GeneralFunction.color.ANSI_GREEN + "\nStatus: " + status + GeneralFunction.color.ANSI_RESET + "\nNumber of Extra Pax: " + numOfExtraPax;
        } else {
            return "Reservation Record ID: " + super.getRecordID() + "\nEvent Room Numner: " + ((EventRoom) super.getReward()).getRoomNum() + "\nEvent Room Name: " + super.getReward().getRewardName() +"\nReservation Date: "+reservationDate+ "\nStatus: " + status + "\nNumber of Extra Pax: " + numOfExtraPax;
        }
    }

    @Override
    public int calcPtRedeemed() {
        return super.getReward().getPtRequired() + 60 * numOfExtraPax;
    }
}
