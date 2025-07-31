package ru.praktikum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private int renTime;
    private String deliveryDate;
    private String comment;
    private String[] color;
}