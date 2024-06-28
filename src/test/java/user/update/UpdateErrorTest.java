package user.update;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import net.datafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import user.UserMetods;

import static base.Constants.BASE_URL;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateErrorTest {
    UserMetods userMetods = new UserMetods();
    static Faker faker = new Faker();
    private static String nameForFirstUser = faker.name().firstName() + "_" + faker.name().lastName();
    private static String updateNameForUser = faker.name().firstName() + "_" + faker.name().lastName();
    //в поле пароль добавляется +"" для того, чтобы преобразовать в тип String
    private static String passwordForFirstUser = faker.number().numberBetween(0, 9999) + "";
    private static String emailForFirstUser = faker.internet().emailAddress();
    private static String nameForSecondUser = faker.name().firstName() + "_" + faker.name().lastName();
    //в поле пароль добавляется +"" для того, чтобы преобразовать в тип String
    private static String passwordForSecondUser = faker.number().numberBetween(0, 9999) + "";
    private static String emailForSecondUser = faker.internet().emailAddress();
    private String tokenForFirstUser;
    private String tokenForSecondUser;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

    }

    @Test
    @DisplayName("login user")
    @Description("status code 401, message = You should be authorised")
    public void updateWithoutToken() {
        registerUser();
        updateUserForFirstTest();
        deleteData(tokenForFirstUser);
    }

    @Step("Create user")
    public void registerUser() {
        ValidatableResponse usersResponce = userMetods
                .create(nameForFirstUser, passwordForFirstUser, emailForFirstUser)
                .statusCode(SC_OK);
        tokenForFirstUser = usersResponce.extract().body().path("accessToken");
    }

    @Step("Update user")
    public void updateUserForFirstTest() {
        userMetods
                .update("", updateNameForUser, passwordForFirstUser, emailForFirstUser)
                .statusCode(SC_UNAUTHORIZED)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Step("Clear data")
    public void deleteData(String token) {
        userMetods.
                delete(token).statusCode(SC_ACCEPTED)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("login user")
    @Description("status code 403, message = User with such email already exists")
    public void updateWithExistEmail() {
        registerUser();
        registerNewUser();
        updateUserForSecondTest();
        deleteData(tokenForFirstUser);
        deleteData(tokenForSecondUser);
    }

    @Step("Create new user")
    public void registerNewUser() {
        ValidatableResponse usersResponce = userMetods
                .create(nameForSecondUser, passwordForSecondUser, emailForSecondUser)
                .statusCode(SC_OK);
        tokenForSecondUser = usersResponce.extract().body().path("accessToken");
    }

    @Step("Update courier")
    public void updateUserForSecondTest() {
        userMetods
                .update(tokenForFirstUser, nameForFirstUser, passwordForFirstUser, emailForSecondUser)
                .statusCode(SC_FORBIDDEN)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));
    }
}
