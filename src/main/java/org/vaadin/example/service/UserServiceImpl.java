package org.vaadin.example.service;

import lombok.Getter;
import org.vaadin.example.model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class UserServiceImpl implements UserService{

    public final Map<String, UserData> userMap = new HashMap<>();

    public UserServiceImpl() {
        getAllInMemoryUsers();
    }

    public List<UserData> getAllUsers() {
        UserData userData = new UserData(1,"Azhar", "Ali", "john.doe@example.com", "Streat   asas","Lahore","Pakistan","03424341036","Test Addreess","123456");
        UserData userData2 = new UserData(2, "Tayyab", "Maqsood", "john22.doe@example.com", "Awan Town","Kashmir","India","0363602222","Test 22222","123456");

        List<UserData> users = new ArrayList<>();
        users.add(userData);
        users.add(userData2);

        return users;
    }

    @Override
    public Map<String, UserData> getAllInMemoryUsers() {

        userMap.put("426e2ccb-0227-46a1-903f-637772d8cb5b", new UserData(1,"Azhar", "Ali", "john.doe@example.com", "Streat   asas","Lahore","Pakistan","03424341036","Test Addreess","426e2ccb-0227-46a1-903f-637772d8cb5b"));
        userMap.put("ea144ee7-8f99-4c6f-9ca6-8e2e8c4a2ba7", new UserData(2,"Haszim", "Tandoor", "john.doe222@example.com", "Streat   asas","Lahore","Pakistan","03424341037","Test Addreess","ea144ee7-8f99-4c6f-9ca6-8e2e8c4a2ba7"));
        userMap.put("5ad6df9a-8bf7-4bb5-b270-cd72c7bfca32", new UserData(3, "David", "Malik", "john.doe333@example.com", "Streat   asas","Lahore","Pakistan","03424341038","Test Addreess","5ad6df9a-8bf7-4bb5-b270-cd72c7bfca32"));

        return userMap;
    }


}
