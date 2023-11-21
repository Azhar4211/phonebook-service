package org.vaadin.example.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
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


    public UserData(String name, String lastName, String email, String userId) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.userId = userId;
    }

}
