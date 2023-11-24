package org.vaadin.example.service;

import org.vaadin.example.model.UserData;

import java.util.Map;
import java.util.Optional;

public interface UserDataService {

    Optional<UserData> find(String userId);

    boolean delete(UserData userData);

    Map<String, UserData> getMap();

    boolean persist(UserData item);

    boolean cancelItem(UserData item);


}
