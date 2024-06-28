package user.create;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import net.datafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import user.UserMetods;

import static base.Constants.BASE_URL;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserWithoutFieldTest {
    UserMetods userMetods = new UserMetods();
    static Faker faker = new Faker();
    @Parameterized.Parameter(0)
    public String testName;
    @Parameterized.Parameter(1)
    public String name;
    @Parameterized.Parameter(2)
    public String password;
    @Parameterized.Parameter(3)
    public String email;

    @Parameterized.Parameters(name = "{index}.{0}")
    public static Object[][] data() {
        return new Object[][]{
                {"test without password", faker.name().firstName(), null, faker.internet().emailAddress()},
                {"test without login", null, faker.number().numberBetween(0, 9999) + "", faker.internet().emailAddress()},
                {"test is empty", null, null, null}
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("create courier without field")
    @Description("status code 403, success = false, message = Email, password and name are required fields")
    public void createCourier() {
        userMetods
                .create(name, password, email)
                .statusCode(SC_FORBIDDEN)
                .assertThat()
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
