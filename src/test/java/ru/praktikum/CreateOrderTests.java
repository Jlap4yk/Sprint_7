package ru.praktikum;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.*;
import java.util.Arrays;
import java.util.Collection;
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
        orderSteps = new OrderSteps();
        order = new Orders(
                "Alexey",
                "Petrov",
                "Moscow, 25 Lenina str.",
                "7",
                "+7 999 123 45 67",
                5,
                "2023-08-15",
                "Please deliver quickly",
                null
        );
    }

    @After
    public void tearDown() {
        try {
            if (trackId != null) {
                Response cancelResponse = orderSteps.cancelOrder(trackId);
                if (cancelResponse.getStatusCode() == SC_OK) {
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
                {new String[]{"BLACK"}, SC_CREATED},
                {new String[]{"GREY"}, SC_CREATED},
                {new String[]{"BLACK", "GREY"}, SC_CREATED},
                {null, SC_CREATED},
                {new String[]{}, SC_CREATED}
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
        assertEquals(statusCode, response.getStatusCode());
        response.then().assertThat().body("track", notNullValue());
        trackId = response.jsonPath().getInt("track");
    }
}