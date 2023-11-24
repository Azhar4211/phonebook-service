package org.vaadin.example.service;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Getter;
import org.vaadin.example.model.UserData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class UserDataServiceImpl implements UserDataService{

    public final Map<String, UserData> userMap = new ConcurrentHashMap<>();

    public UserDataServiceImpl() {
        InitializeInMemoryData();
    }

    public void InitializeInMemoryData() {

        userMap.put("426e2ccb-0227-46a1-903f-637772d8cb5b", new UserData(1,"Azhar", "Ali", "john.doe@example.com", "Streat   asas","Lahore","Pakistan","03424341036","Test Addreess","426e2ccb-0227-46a1-903f-637772d8cb5b",0, false));
        userMap.put("ea144ee7-8f99-4c6f-9ca6-8e2e8c4a2ba7", new UserData(2,"Haszim", "Tandoor", "john.doe222@example.com", "Streat   asas","Lahore","Pakistan","03424341037","Test Addreess","ea144ee7-8f99-4c6f-9ca6-8e2e8c4a2ba7",0, false));
        userMap.put("5ad6df9a-8bf7-4bb5-b270-cd72c7bfca32", new UserData(3, "David", "Malik", "john.doe333@example.com", "Streat   asas","Lahore","Pakistan","03424341038","Test Addreess","5ad6df9a-8bf7-4bb5-b270-cd72c7bfca32",0, false));

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
        return userMap;
    }

    public boolean persist(UserData item) {
        String uuid;

        if (item.getUserId() == null) {
            uuid = UUID.randomUUID().toString();
            item.setUserId(uuid);
            item.setVersion(0);
            item.setEditModeFlag(false);
            userMap.put(uuid, item);
            return true;
        } else {
            Optional<UserData> userData = find(item.getUserId());
            if(userData.isPresent()) {

                if(userData.get().getVersion().equals(item.getVersion())) {
                    item.setVersion(item.getVersion()+1);
                    item.setEditModeFlag(false);
                    userMap.replace(userData.get().getUserId(), item);
                    System.out.println("User map: "+userData);
                    return true;

                } else {
                    showAlreadyModifiedWarningNotification();
                    item.setVersion(item.getVersion()+1);
                    item.setEditModeFlag(false);
                    userMap.replace(userData.get().getUserId(), item);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void cancelItem(UserData item) {
        Optional<UserData> userData = find(item.getUserId());
        UserData oldObject = userData.get();
        oldObject.setEditModeFlag(false);
        userMap.replace(item.getUserId(), oldObject);
    }

    private void showAlreadyModifiedWarningNotification(){
        Div text = new Div(
                new Text("This Data has already updated by another user"),
                new HtmlComponent("br"));

        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.setAriaLabel("Close");
        closeButton.addClickListener(event2 -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();

    }

}
