package order.get;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import order.OrderMetods;
import org.junit.Before;
import org.junit.Test;

import static base.Constants.BASE_URL;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetOrder401Test {
    OrderMetods orderMetods = new OrderMetods();

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("get orders without authority")
    @Description("status code 401, message = You should be authorised")
    public void getOrder401() {
        orderMetods
                .get("")
                .statusCode(SC_UNAUTHORIZED)
                .body("message", equalTo("You should be authorised"));

    }
}
