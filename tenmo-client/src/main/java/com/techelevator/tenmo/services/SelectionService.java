package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

public class SelectionService {

    public void printArray(User[] users){
        for(int i = 0; i < users.length; i++){
            int count = 1;
            System.out.println(count + ": " + users[i].getUsername());
            count++;
        }
    }

    public void printArray(Transfer[] transfers){
        for(int i = 0; i < transfers.length; i++){
            System.out.println("Transfer ID: " + transfers[i].getId() + " " + transfers[i].getFromUser() + " sent " +
                    transfers[i].getToUser() + " $" + transfers[i].getAmount() + ".");
        }
    }

    public void printTransfer(Transfer transfers){
        System.out.println("Transfer ID: " + transfers.getId() + " " + transfers.getFromUser() + " sent " +
                transfers.getToUser() + " $" + transfers.getAmount() + ".");
    }

}