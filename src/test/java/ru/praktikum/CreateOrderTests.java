package ru.praktikum;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.praktikum.OrderSteps;
import ru.praktikum.Orders;

import java.util.Arrays;
import java.util.Collection;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CreateOrderTests {
    private final String[] color;
    private final int statusCode;
    private Orders order;
    private Integer trackId;
    private OrderSteps orderSteps;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        orderSteps = new OrderSteps();
        order = new Orders(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                "4",
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                null
        );
    }

    @After
    public void tearDown() {
        try {
            if (trackId != null) {
                Response cancelResponse = given()
                        .log().all()
                        .contentType(ContentType.JSON)
                        .put("/api/v1/orders/cancel?track=" + trackId);
                if (cancelResponse.getStatusCode() == 200) {
                    System.out.println("Заказ успешно отменен, trackId: " + trackId);
                } else {
                    System.out.println("Не удалось отменить заказ. Код: " + cancelResponse.getStatusCode());
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при отмене заказа: " + e.getMessage());
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getColorData() {
        return Arrays.asList(new Object[][] {
                {new String[]{"BLACK"}, 201},
                {new String[]{"GREY"}, 201},
                {new String[]{"BLACK", "GREY"}, 201},
                {null, 201},
                {new String[]{}, 201}
        });
    }

    public CreateOrderTests(String[] color, int statusCode) {
        this.color = color;
        this.statusCode = statusCode;
    }

    @Test
    public void createOrderTest() {
        order.setColor(color);
        Response response = orderSteps.createOrder(order);
        response.then().log().all();
        response.then().log().all();
        assertEquals(statusCode, response.getStatusCode());
        response.then().assertThat().body("track", notNullValue());
        trackId = response.jsonPath().getInt("track");
    }
}
