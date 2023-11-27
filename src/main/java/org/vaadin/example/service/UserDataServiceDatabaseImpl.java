package org.vaadin.example.service;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.vaadin.example.database.DatabaseConnectionUtil;
import org.vaadin.example.model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class UserDataServiceDatabaseImpl implements UserDataService{

    public final Map<String, UserData> userMap = new ConcurrentHashMap<>();

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
                user.setVersion(resultSet.getInt("version"));
                user.setEditModeFlag(resultSet.getBoolean("edit_mode_flag"));


                userMap.put(user.getUserId(), user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<UserData> find(String userId) {
        return Optional.of(userMap.get(userId));
    }

    public Map<String, UserData> getMap() {
        return userMap;
    }

    public boolean delete(UserData userData) {
        String query = "delete from user_data where user_id= ?";
        try {
            Connection connection = DatabaseConnectionUtil.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, userData.getUserId());

            if(preparedStatement.executeUpdate() <= 0) {
                connection.commit();
                connection.setAutoCommit(true);
                return false;
            } else {
                userMap.remove(userData.getUserId());
                connection.commit();
                connection.setAutoCommit(true);
                return true;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean persist(UserData item) {
        String uuid;

        if (item.getUserId() == null) {
            uuid = UUID.randomUUID().toString();
            item.setUserId(uuid);
            item.setVersion(0);
            item.setEditModeFlag(false);
            userMap.put(uuid, item);
            addUser(item);
        } else {
            Optional<UserData> userData = find(item.getUserId());
            if(userData.isPresent()) {

                if(userData.get().getVersion().equals(item.getVersion())) {
                    item.setVersion(item.getVersion()+1);
                    item.setEditModeFlag(false);
                    if(updateUser(item)){
                        userMap.replace(userData.get().getUserId(), item);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean addUser(UserData userData) {


        String query = "INSERT INTO user_data (name, last_name, phone_number, email, street, city, country, address, user_id, version, edit_mode_flag) values (?,?,?,?,?,?,?,?,?,?,?)";
        try {
            Connection connection = DatabaseConnectionUtil.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, userData.getName());
            preparedStatement.setString(2, userData.getLastName());
            preparedStatement.setString(3, userData.getPhoneNumber());
            preparedStatement.setString(4, userData.getEmail());
            preparedStatement.setString(5, userData.getStreet());
            preparedStatement.setString(6, userData.getCity());
            preparedStatement.setString(7, userData.getCountry());
            preparedStatement.setString(8, userData.getAddress());
            preparedStatement.setString(9, userData.getUserId());
            preparedStatement.setInt(10, userData.getVersion());
            preparedStatement.setBoolean(11, userData.isEditModeFlag());

            if(preparedStatement.executeUpdate() > 0 ) {
                connection.commit();
                connection.setAutoCommit(true);
                return true;
            }
            else return false;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateUser(UserData userData) {

        String query = "UPDATE user_data SET name=?, last_name=?, phone_number=?, email=?, street=?, city=?, country=?, address=?, user_id=?, version=?, edit_mode_flag=? WHERE user_id=?";
        try {
            Connection connection = DatabaseConnectionUtil.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userData.getName());
            preparedStatement.setString(2, userData.getLastName());
            preparedStatement.setString(3, userData.getPhoneNumber());
            preparedStatement.setString(4, userData.getEmail());
            preparedStatement.setString(5, userData.getStreet());
            preparedStatement.setString(6, userData.getCity());
            preparedStatement.setString(7, userData.getCountry());
            preparedStatement.setString(8, userData.getAddress());
            preparedStatement.setString(9, userData.getUserId());
            preparedStatement.setInt(10, userData.getVersion());
            preparedStatement.setBoolean(11, userData.isEditModeFlag());

            preparedStatement.setString(12, userData.getUserId());
            if(preparedStatement.executeUpdate() > 0 ) {
                connection.commit();
                connection.setAutoCommit(true);
                return true;
            }
            else return false;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean cancelItem(UserData item) {
        if(ObjectUtils.allNull(item.getUserId())) {
            return false;
        }
        Optional<UserData> userData = find(item.getUserId());
        if(userData.isPresent()){
            UserData oldObject = userData.get();
            oldObject.setEditModeFlag(false);
            userMap.replace(item.getUserId(), oldObject);
        }
        return true;
    }
}
