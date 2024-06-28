package order.get;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import net.datafaker.Faker;
import order.OrderMetods;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserMetods;


import static base.Constants.BASE_URL;
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetOrder200Test {
    OrderMetods orderMetods = new OrderMetods();
    UserMetods userMetods = new UserMetods();
    Faker faker = new Faker();
    String token;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        //в поле пароль добавляется +"" для того, чтобы преобразовать в тип String
        ValidatableResponse user = userMetods.create(faker.name().fullName(), faker.number().numberBetween(5, 10) + "", faker.internet().emailAddress());
        token = user.extract().body().path("accessToken");
    }

    @Test
    @DisplayName("get orders with authorization")
    @Description("status code 200 OK, success = true")
    public void getOrder200() {
        orderMetods
                .get(token)
                .statusCode(SC_OK)
                .body("orders", notNullValue());

    }

    @After
    public void deleteData() {
        userMetods.
                delete(token).statusCode(SC_ACCEPTED)
                .body("success", equalTo(true));
    }
}
