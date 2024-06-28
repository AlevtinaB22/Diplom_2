package order.create;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import net.datafaker.Faker;
import order.OrderRequest;
import order.OrderMetods;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserMetods;

import java.util.Collections;
import java.util.List;

import static base.Constants.BASE_URL;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderTest {
    private final OrderMetods orderMetods = new OrderMetods();
    private final UserMetods userMetods = new UserMetods();
    private final Faker faker = new Faker();
    private String token;
    private OrderRequest orderRequestForFirstAndLastTest;
    private OrderRequest orderRequestForSecondTest;
    private OrderRequest orderRequestForThreeTest;
    private List<String> ingredients;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        //в поле пароль добавляется +"" для того, чтобы преобразовать в тип String
        ValidatableResponse user = userMetods.create(faker.name().fullName(), faker.number().numberBetween(100000, 900000) + "",
                faker.internet().emailAddress());
        token = user.extract().body().path("accessToken");
        ingredients = orderMetods.getAllIngredients();
        Collections.shuffle(ingredients);
        orderRequestForFirstAndLastTest = new OrderRequest(List.of(ingredients.get(3), ingredients.get(7), ingredients.get(9)));
        orderRequestForSecondTest = new OrderRequest(List.of(faker.food().ingredient(), faker.food().ingredient()));
        orderRequestForThreeTest = new OrderRequest(List.of());
    }

    @Test
    @DisplayName("create order with ingredients")
    @Description("status code 200 OK, success = true")
    public void createOrder() {
        orderMetods
                .create(orderRequestForFirstAndLastTest, token)
                .statusCode(SC_OK)
                .body("order.number", notNullValue());

    }

    @Test
    @DisplayName("create order with wrong hash ingredients")
    @Description("status code 500")
    public void createOrderWithWrongHashIngredients() {
        orderMetods
                .create(orderRequestForSecondTest, token)
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("create order without ingredients")
    @Description("status code 400, message = Ingredient ids must be provided")
    public void createOrderWithoutIngredients() {
        orderMetods
                .create(orderRequestForThreeTest, token)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Ingredient ids must be provided"));

    }

    @Test
    @DisplayName("create order without authorization")
    @Description("status code 200, success = true")
    public void createOrderWithoutAuthorization() {
        orderMetods
                .create(orderRequestForFirstAndLastTest, "")
                .statusCode(SC_OK)
                .body("success", equalTo(true));

    }

    @After
    public void deleteData() {
        userMetods.
                delete(token)
                .statusCode(SC_ACCEPTED)
                .body("success", equalTo(true));
    }
}
