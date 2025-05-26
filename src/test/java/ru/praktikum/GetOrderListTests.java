package ru.praktikum;

import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.notNullValue;
import static org.apache.http.HttpStatus.*;

public class GetOrderListTests {
    private OrderSteps orderSteps;

    @Before
    public void setUp() {
        orderSteps = new OrderSteps();
    }

    @Test
    public void getOrderListWithNonExistentCourierIdTest() {
        Response response = orderSteps.getOrderList();
        response.then().assertThat().statusCode(SC_OK);
        response.then().assertThat().body("orders", notNullValue());
    }
}