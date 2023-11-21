package org.vaadin.example.service;


import org.vaadin.example.model.UserData;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    public static List<UserData> getAllUsers(){
        UserData userData = new UserData("Azhar", "Ali", "john.doe@example.com");
        UserData userData2 = new UserData("Anas", "Tayyab", "john.doe2@2222example.com");

        List<UserData> users = new ArrayList<>();
        users.add(userData);
        users.add(userData2);

        return users;
    }

}
