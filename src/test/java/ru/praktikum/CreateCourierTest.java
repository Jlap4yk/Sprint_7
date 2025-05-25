package ru.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.Courier;
import ru.praktikum.CourierSteps;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

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
                if (loginResponse.statusCode() == 200) {
                    courierId = loginResponse.jsonPath().getInt("id");
                    Response deleteResponse = courierCreate.sendRequestDeleteCourier(courierId);
                    if (deleteResponse.statusCode() == 200) {
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
                .statusCode(201)
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
                .statusCode(201)
                .body("ok", equalTo(true));
        courierCreate.sendPostRequestCourierCreate(new Courier(login, password, firstName))
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(409)
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
                .statusCode(400)
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
                .statusCode(400)
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
                .statusCode(201)
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
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}