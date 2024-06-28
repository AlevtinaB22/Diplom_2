package user.login;

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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import user.UserMetods;

import static base.Constants.BASE_URL;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class LoginWrongDataTest {
    UserMetods userMetods = new UserMetods();
    static Faker faker = new Faker();
    public static String nameForTest = faker.name().firstName() + "_" + faker.name().lastName();
    //в поле пароль добавляется +"" для того, чтобы преобразовать в тип String
    public static String passwordForTest = faker.number().numberBetween(0, 9999) + "";
    public static String emailForTest = faker.internet().emailAddress();
    @Parameterized.Parameter(0)
    public String testName;
    @Parameterized.Parameter(1)
    public String name;
    @Parameterized.Parameter(2)
    public String password;
    @Parameterized.Parameter(3)
    public String email;
    @Parameterized.Parameter(4)
    public int statusCode;
    private String token;
    private final String textErrorFor401 = "email or password are incorrect";

    @Parameterized.Parameters(name = "{index}.{0}")
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        ValidatableResponse usersResponce = userMetods
                .create(nameForTest, passwordForTest, emailForTest)
                .statusCode(SC_OK);
        token = usersResponce.extract().body().path("accessToken");
    }

    @Parameterized.Parameters(name = "{index}.{0}")
    public static Object[][] data() {
        return new Object[][]{
                {"test with wrong password", nameForTest, passwordForTest + 12345, emailForTest,SC_UNAUTHORIZED},
                {"test with empty password", nameForTest, null, emailForTest,SC_UNAUTHORIZED},
                {"test with wrong email", nameForTest, passwordForTest, emailForTest+123,SC_UNAUTHORIZED},
                {"test with empty email", nameForTest, passwordForTest, null,SC_UNAUTHORIZED}
        };
    }

    @Test
    @DisplayName("login with wrong fields")
    @Description("status code 401, message = email or password are incorrect")
    public void loginWrongData() {
        userMetods
                .login(name, password, email)
                .statusCode(statusCode)
                .body("message", equalTo(textErrorFor401));
    }

    @After
    public void deleteData() {
        userMetods.
                delete(token).statusCode(SC_ACCEPTED)
                .body("success", equalTo(true));
    }
}
