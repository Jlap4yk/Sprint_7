package ru.praktikum;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrderListTests {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    public void getOrderListWithNonExistentCourierIdTest() {
        Response response = given()
                .log().ifValidationFails()
                .header("Content-type", "application/json")
                .get("/api/v1/orders");
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body("orders", notNullValue());
    }
}