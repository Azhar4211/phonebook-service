package org.vaadin.example.model;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserData implements Cloneable {

    private Integer id;
    private String name;
    private String lastName;
    private String email;
    private String street;
	private String city;
	private String country;
	private String phoneNumber;
    private String address;
    private String userId;
    private Integer version;


    public UserData(String name, String lastName, String email, String userId) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.userId = userId;
    }

    @Override
    public UserData clone() {
        try {
            UserData clone = (UserData) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
