package user.update;

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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import user.UserMetods;

import static base.Constants.BASE_URL;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class UpdateOkTest {
    UserMetods userMetods = new UserMetods();
    static Faker faker = new Faker();
    private static String nameForTest = faker.name().firstName() + "_" + faker.name().lastName();
    private static String updateNameForTest = faker.name().firstName() + "_" + faker.name().lastName();
    //в поле пароль добавляется +"" для того, чтобы преобразовать в тип String
    private static String passwordForTest = faker.number().numberBetween(0, 9999) + "";
    private static String emailForTest = faker.internet().emailAddress();
    private static String updateEmailForTest = faker.internet().emailAddress();
    private String tokenForTest;
    @Parameterized.Parameter(0)
    public String nameTest;
    @Parameterized.Parameter(1)
    public String name;
    @Parameterized.Parameter(2)
    public String password;
    @Parameterized.Parameter(3)
    public String email;

    @Parameterized.Parameters(name = "{index}.{0}")
    public static Object[][] data() {
        return new Object[][]{
                {"update name",updateNameForTest, passwordForTest, emailForTest},
                {"update email",nameForTest, passwordForTest, updateEmailForTest}
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

    }

    @Test
    @DisplayName("login user")
    @Description("status code 200, success = true")
    public void update() {
        registerUser();
        updateUser();
        getInformationAboutUser();
    }

    @Step("Create user")
    public void registerUser() {
        ValidatableResponse usersResponce = userMetods
                .create(nameForTest, passwordForTest, emailForTest)
                .statusCode(SC_OK);
        tokenForTest = usersResponce.extract().body().path("accessToken");
    }

    @Step("Update user")
    public void updateUser() {
        userMetods
                .update(tokenForTest, name, password, email)
                .statusCode(SC_OK)
                .assertThat()
                .body("success", equalTo(true));
    }

    @Step("Check information about user")
    public void getInformationAboutUser() {
        userMetods
                .get(tokenForTest)
                .statusCode(SC_OK)
                .assertThat()
                .body("success", equalTo(true))
                .body("user.name", equalTo(name))
                .body("user.email", equalTo(email));
    }

    @After
    public void deleteData() {
        userMetods.
                delete(tokenForTest).statusCode(SC_ACCEPTED)
                .body("success", equalTo(true));
    }
}
