package ru.praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class CourierSteps extends BaseApi {
    private static final String COURIER_CREATE = "/api/v1/courier";
    private static final String COURIER_LOGIN = "/api/v1/courier/login";

    @Step("Создание курьера")
    public Response sendPostRequestCourierCreate(Courier courier) {
        return given()
                .spec(getBaseSpec())
                .log().ifValidationFails()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post(COURIER_CREATE);
    }

    @Step("Авторизация курьера, получение ID")
    public Response sendPostRequestCourierLogin(Courier courier) {
        return given()
                .spec(getBaseSpec())
                .log().ifValidationFails()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post(COURIER_LOGIN);
    }

    @Step("Удаление курьера")
    public Response sendRequestDeleteCourier(int id) {
        return given()
                .spec(getBaseSpec())
                .log().ifValidationFails()
                .contentType(ContentType.JSON)
                .pathParams("id", id)
                .when()
                .delete("/api/v1/courier/{id}");
    }
}