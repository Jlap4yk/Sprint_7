package ru.praktikum;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderSteps {
    private static final String ORDERS_ENDPOINT = "/api/v1/orders";
    private static final String CANCEL_ENDPOINT = "/api/v1/orders/cancel";

    @Step("Создание заказа")
    public Response createOrder(Orders order) {
        return given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(order)
                .when()
                .post(ORDERS_ENDPOINT);
    }

    @Step("Отмена заказа")
    public Response cancelOrder(int trackId) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .queryParam("track", trackId)
                .when()
                .put(CANCEL_ENDPOINT);
    }
}