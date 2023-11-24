package org.vaadin.example.database;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

@NoArgsConstructor
public class DatabaseConnectionUtil {
    @Getter
    private static Connection connection;


    static {
        try {
            FileReader file = new FileReader(Objects.requireNonNull(DatabaseConnectionUtil.class.getClassLoader().getResource("application.properties")).getFile());
            Properties properties = new Properties();
            properties.load(file);
            connection = DriverManager.getConnection(properties.getProperty("database.url"), properties.getProperty("database.username"), properties.getProperty("database.password"));
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
