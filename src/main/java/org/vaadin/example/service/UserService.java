package org.vaadin.example.service;


import org.vaadin.example.model.UserData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface UserService {

    List<UserData> getAllUsers();

    Map<String, UserData> getAllInMemoryUsers();

}
