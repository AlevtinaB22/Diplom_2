package user.create;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
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
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateDoubleUserTest {
    UserMetods userMetods = new UserMetods();
    Faker faker = new Faker();
    //в поле пароль добавляется +"" для того, чтобы преобразовать в тип String
    private String password = faker.number().numberBetween(0, 9999) + "";
    private String name = faker.name().firstName() + "_" + faker.name().lastName();
    private String email = faker.internet().emailAddress();
    private String token;
    private String textError = "User already exists";

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("create double user")
    @Description("status code 403, message = User already exists")
    public void loginDublicateCourier() {
        createCourierForThisTest();
        createDublicateForThisTest();
    }

    @Step("Create user")
    public void createCourierForThisTest() {
        ValidatableResponse usersResponce =
                userMetods
                        .create(name, password, email)
                        .statusCode(SC_OK)
                        .assertThat()
                        .body("success", equalTo(true));
        token = usersResponce.extract().body().path("accessToken");
    }

    @Step("Create  double user")
    public void createDublicateForThisTest() {
        userMetods
                .create(name, password, email)
                .statusCode(SC_FORBIDDEN)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo(textError));
    }

    @After
    public void deleteData() {
        userMetods.
                delete(token).statusCode(SC_ACCEPTED)
                .body("success", equalTo(true));
    }
}
