package org.vaadin.example.service;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import lombok.Getter;
import org.vaadin.example.database.DatabaseConnectionUtil;
import org.vaadin.example.model.UserData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Getter
public class UserDataServiceDatabaseImpl implements UserDataService{

    public final Map<String, UserData> userMap = new HashMap<>();

    private static final String TABLE_NAME = "user_data";
    public UserDataServiceDatabaseImpl() {
        InitializeDbData();
    }

    public void InitializeDbData() {

        String query = "select * from user_data";
        try (PreparedStatement prepareStatement = DatabaseConnectionUtil.getConnection().prepareStatement(query)) {
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                UserData user = new UserData();
                user.setPhoneNumber(resultSet.getString("phone_number"));
                user.setName(resultSet.getString("name"));
                user.setLastName(resultSet.getString("last_name"));

                user.setEmail(resultSet.getString("email"));
                user.setStreet(resultSet.getString("street"));
                user.setCity(resultSet.getString("city"));
                user.setCountry(resultSet.getString("country"));
                user.setUserId(resultSet.getString("user_id"));
                userMap.put(user.getUserId(), user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Optional<UserData> find(String userId) {
        return Optional.of(userMap.get(userId));
    }

    public boolean delete(UserData userData) {
        String query = "delete from user_data where user_id= ?";
        try (PreparedStatement preparedStatement = DatabaseConnectionUtil.getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, userData.getUserId());

            if(preparedStatement.executeUpdate() <= 0) {
                return false;
           } else {
                userMap.remove(userData.getUserId());
                return true;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, UserData> getMap() {
        return userMap;
    }

    public boolean persist(UserData item) {
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
                    dialog.open();
                }

            }
        }
        return false;
    }

    @Override
    public boolean cancelItem(UserData item) {
        return false;
    }
}
