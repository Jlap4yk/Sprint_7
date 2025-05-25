package ru.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

public class LoginCourierTest {
    private String login;
    private String password;
    private CourierSteps courierLogin;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        login = RandomStringUtils.randomAlphanumeric(3,20);
        password = RandomStringUtils.randomAlphanumeric(8,20);
        courierLogin = new CourierSteps();
        courierLogin.sendPostRequestCourierCreate(new Courier(login,password))
                .then()
                .statusCode(SC_CREATED)
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Login Courier Test")
    @Description("Создание учетной записи и авторизация курьера, ручка /api/v1/courier/login")
    public void loginCourierTest() {
        courierLogin.sendPostRequestCourierLogin(new Courier(login,password))
                .then()
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Login Courier Without Login Test")
    @Description("Авторизация курьера без логина, ручка /api/v1/courier/login")
    public void loginCourierWithoutLoginTest() {
        courierLogin.sendPostRequestCourierLogin(new Courier("",password))
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Login Courier Without Password Test")
    @Description("Авторизация курьера без пароля, ручка /api/v1/courier/login")
    public void loginCourierWithoutPasswordTest() {
        courierLogin.sendPostRequestCourierLogin(new Courier(login,""))
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Login Courier With Non Existent Login Test")
    @Description("Авторизация курьера с несуществующим логином, ручка /api/v1/courier/login")
    public void loginCourierWithNonExistentLoginTest() {
        courierLogin.sendPostRequestCourierLogin(new Courier("qwerty",password))
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Login Courier With Non Existent Password Test")
    @Description("Авторизация курьера с несуществующим паролем, ручка /api/v1/courier/login")
    public void loginCourierWithNonExistentPasswordTest() {
        courierLogin.sendPostRequestCourierLogin(new Courier(login,"qwerty"))
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Login Courier With Wrong Login Test")
    @Description("Авторизация курьера с неправильным логином, ручка /api/v1/courier/login")
    public void loginCourierWithWrongLoginTest() {
        String wrongLogin = login + "_";
        courierLogin.sendPostRequestCourierLogin(new Courier(wrongLogin, password))
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Login Courier With Wrong Password Test")
    @Description("Авторизация курьера с неправильным паролем, ручка /api/v1/courier/login")
    public void loginCourierWithWrongPasswordTest() {
        String wrongPassword = password + "_";
        courierLogin.sendPostRequestCourierLogin(new Courier(login, wrongPassword))
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @After
    public void tearDown(){
        Integer id = courierLogin.sendPostRequestCourierLogin(new Courier(login,password))
                .then()
                .extract()
                .path("id");
        if (id != null) {
            courierLogin.sendRequestDeleteCourier(id)
                    .then()
                    .statusCode(SC_OK);
        }
    }
}