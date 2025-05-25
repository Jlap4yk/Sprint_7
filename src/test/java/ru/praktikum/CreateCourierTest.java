package ru.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;

public class CreateCourierTest {
    private String login;
    private String password;
    private String firstName;
    private CourierSteps courierCreate;
    private int courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        login = RandomStringUtils.randomAlphanumeric(3, 20);
        password = RandomStringUtils.randomAlphanumeric(8, 20);
        firstName = RandomStringUtils.randomAlphanumeric(4, 28);
        courierCreate = new CourierSteps();
    }

    @After
    public void tearDown() {
        try {
            if (login != null && password != null) {
                Response loginResponse = courierCreate.sendPostRequestCourierLogin(
                        new Courier(login, password, firstName));
                if (loginResponse.statusCode() == SC_OK) {
                    courierId = loginResponse.jsonPath().getInt("id");
                    Response deleteResponse = courierCreate.sendRequestDeleteCourier(courierId);
                    if (deleteResponse.statusCode() == SC_OK) {
                        System.out.println("Курьер успешно удален, ID: " + courierId);
                    } else {
                        System.out.println("Не удалось удалить курьера. Код: " +
                                deleteResponse.statusCode());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при удалении курьера: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Create Courier Test")
    @Description("Создание учетной записи курьера, ручка /api/v1/courier")
    public void createCourierTest() {
        courierCreate.sendPostRequestCourierCreate(new Courier(login, password, firstName))
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Courier Conflict Test")
    @Description("Проверка статус кода и сообщения об ошибке, при создании курьера с не уникальным логином")
    public void createDuplicateCourierTest() {
        courierCreate.sendPostRequestCourierCreate(new Courier(login, password, firstName))
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
        courierCreate.sendPostRequestCourierCreate(new Courier(login, password, firstName))
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Create Courier Without Login")
    @Description("Создание учетной записи курьера без логина, ручка /api/v1/courier")
    public void createCourierWithoutLogin() {
        courierCreate.sendPostRequestCourierCreate(new Courier(null, password, firstName))
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Create Courier Without Password")
    @Description("Создание учетной записи курьера без пароля, ручка /api/v1/courier")
    public void createCourierWithoutPassword() {
        courierCreate.sendPostRequestCourierCreate(new Courier(login, null, firstName))
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Create Courier Without First Name")
    @Description("Создание учетной записи курьера без имени, ручка /api/v1/courier")
    public void createCourierWithoutFirstName() {
        courierCreate.sendPostRequestCourierCreate(new Courier(login, password, null))
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Create Courier Without Login And Password")
    @Description("Создание учетной записи курьера без логина и пароля, ручка /api/v1/courier")
    public void createCourierWithoutLoginAndPassword() {
        courierCreate.sendPostRequestCourierCreate(new Courier(null, null, firstName))
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}