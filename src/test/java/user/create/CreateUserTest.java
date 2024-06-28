package user.create;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserMetods;

import static base.Constants.BASE_URL;
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest {
    UserMetods userMetods = new UserMetods();
    Faker faker = new Faker();
    //в поле пароль добавляется +"" для того, чтобы преобразовать в тип String
    private String password = faker.number().numberBetween(0, 9999) + "";
    private String name = faker.name().firstName();
    private String email = name + "@gmail.com";
    private String token;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("create user")
    @Description("status code 200, success = true")
    public void createCourier() {
        ValidatableResponse usersResponce =
                userMetods
                        .create(name, password, email)
                        .statusCode(SC_OK)
                        .assertThat()
                        .body("success", equalTo(true));
        token = usersResponce.extract().body().path("accessToken");
    }

    @After
    public void deleteData() {
        userMetods.
                delete(token).statusCode(SC_ACCEPTED)
                .body("success", equalTo(true));
    }
}
