package org.vaadin.example.service;

import org.vaadin.example.model.UserData;

import java.util.Map;
import java.util.Optional;

public interface UserDataService {

    Optional<UserData> find(String userId);

    void delete(UserData userData);

    Map<String, UserData> getMap();

    void persist(UserData item);

    void cancelItem(UserData item);



}
