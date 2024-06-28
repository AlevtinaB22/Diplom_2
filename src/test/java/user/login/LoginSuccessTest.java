package user.login;

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
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class LoginSuccessTest {
    UserMetods userMetods = new UserMetods();
    static Faker faker = new Faker();
    private String nameForTest = faker.name().firstName() + "_" + faker.name().lastName();
    //в поле пароль добавляется +"" для того, чтобы преобразовать в тип String
    private String passwordForTest = faker.number().numberBetween(0, 9999) + "";
    private String emailForTest = faker.internet().emailAddress();
    private String token;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

    }

    @Test
    @DisplayName("login user")
    @Description("status code 200, success = true")
    public void loginUser_200() {
        registerUser();
        loginUser();
    }

    @Step("Create courier")
    public void registerUser() {
        ValidatableResponse usersResponce = userMetods
                .create(nameForTest, passwordForTest, emailForTest)
                .statusCode(SC_OK);
        token = usersResponce.extract().body().path("accessToken");
    }

    @Step("Login courier")
    public void loginUser() {
        ValidatableResponse usersResponce = userMetods
                .login(nameForTest, passwordForTest, emailForTest)
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
