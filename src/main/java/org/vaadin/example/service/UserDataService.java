package org.vaadin.example.service;

import lombok.Getter;
import org.vaadin.example.model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class UserDataService {

    public final Map<String, UserData> userMap = new HashMap<>();

    public UserDataService() {
        InitiliazInMemoryData();
    }

    public Map<String, UserData> InitiliazInMemoryData() {

        userMap.put("426e2ccb-0227-46a1-903f-637772d8cb5b", new UserData(1,"Azhar", "Ali", "john.doe@example.com", "Streat   asas","Lahore","Pakistan","03424341036","Test Addreess","426e2ccb-0227-46a1-903f-637772d8cb5b",0));
        userMap.put("ea144ee7-8f99-4c6f-9ca6-8e2e8c4a2ba7", new UserData(2,"Haszim", "Tandoor", "john.doe222@example.com", "Streat   asas","Lahore","Pakistan","03424341037","Test Addreess","ea144ee7-8f99-4c6f-9ca6-8e2e8c4a2ba7",0));
        userMap.put("5ad6df9a-8bf7-4bb5-b270-cd72c7bfca32", new UserData(3, "David", "Malik", "john.doe333@example.com", "Streat   asas","Lahore","Pakistan","03424341038","Test Addreess","5ad6df9a-8bf7-4bb5-b270-cd72c7bfca32",0));

        return userMap;
    }


    


}
