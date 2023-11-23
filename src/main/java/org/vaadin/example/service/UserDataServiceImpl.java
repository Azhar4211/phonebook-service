package org.vaadin.example.service;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import lombok.Getter;
import org.vaadin.example.model.UserData;

import java.util.*;

@Getter
public class UserDataServiceImpl implements UserDataService{

    public final Map<String, UserData> userMap = new HashMap<>();

    public UserDataServiceImpl() {
        InitializeInMemoryData();
    }

    public void InitializeInMemoryData() {

        userMap.put("426e2ccb-0227-46a1-903f-637772d8cb5b", new UserData(1,"Azhar", "Ali", "john.doe@example.com", "Streat   asas","Lahore","Pakistan","03424341036","Test Addreess","426e2ccb-0227-46a1-903f-637772d8cb5b",0));
        userMap.put("ea144ee7-8f99-4c6f-9ca6-8e2e8c4a2ba7", new UserData(2,"Haszim", "Tandoor", "john.doe222@example.com", "Streat   asas","Lahore","Pakistan","03424341037","Test Addreess","ea144ee7-8f99-4c6f-9ca6-8e2e8c4a2ba7",0));
        userMap.put("5ad6df9a-8bf7-4bb5-b270-cd72c7bfca32", new UserData(3, "David", "Malik", "john.doe333@example.com", "Streat   asas","Lahore","Pakistan","03424341038","Test Addreess","5ad6df9a-8bf7-4bb5-b270-cd72c7bfca32",0));

    }


    public Optional<UserData> find(String userId) {
        return Optional.of( userMap.get(userId));
    }

    public void delete(UserData userData) {
        if(userMap.remove(userData.getUserId()) == null) {
            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("Data deleted");
            dialog.setText(new Html(
                    "<p>This data has already deleted By another user" +
                            "</p>"));

            dialog.setConfirmText("OK");
            dialog.open();
        }
    }

    public Map<String, UserData> getMap(){
        return  userMap;
    }

    public void persist(UserData item) {
        String uuid;

        if (item.getUserId() == null) {
            uuid = UUID.randomUUID().toString();
            item.setUserId(uuid);
            item.setVersion(0);
            userMap.put(uuid, item);
        } else {
            Optional<UserData> userData = find(item.getUserId());
            if(userData.isPresent()) {

                if(userData.get().getVersion().equals(item.getVersion())) {
                    item.setVersion(item.getVersion()+1);
                    userMap.replace(userData.get().getUserId(), item);
                } else {
                    ConfirmDialog dialog = new ConfirmDialog();
                    dialog.setHeader("User rights Violation");
                    dialog.setText(new Html(
                            "<p>This data has already modified from another user" +
                                    "</p>"));

                    dialog.setConfirmText("OK");

                    dialog.setCancelable(true);
                    dialog.setRejectable(true);
                    dialog.setRejectText("Discard");

                    dialog.setConfirmText("Overwrite");
                    dialog.addConfirmListener(
                            confirmEvent -> userMap.replace(userData.get().getUserId(), item)
                    );

                    dialog.open();
                }

            }
        }
    }
}
