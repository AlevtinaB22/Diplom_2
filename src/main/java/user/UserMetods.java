package user;

import io.restassured.response.ValidatableResponse;
import user.dto.UserRequest;

import static base.Constants.*;
import static io.restassured.RestAssured.given;

public class UserMetods {
    public ValidatableResponse create(String name, String password, String email) {
        UserRequest userRequest = new UserRequest();
        userRequest.setName(name);
        userRequest.setPassword(password);
        userRequest.setEmail(email);
        return given()
                .header("Content-Type", "application/json")
                .and()
                .body(userRequest)
                .when()
                .post(API_FOR_CREATE_USER)
                .then();
    }

    public ValidatableResponse login(String name, String password, String email) {
        UserRequest userRequest = new UserRequest();
        userRequest.setName(name);
        userRequest.setPassword(password);
        userRequest.setEmail(email);
        return given()
                .header("Content-Type", "application/json")
                .and()
                .body(userRequest)
                .when()
                .post(API_FOR_LOGIN_USER)
                .then();
    }

    public ValidatableResponse update(String token, String name, String password, String email) {
        String newToken = "";
        if (token.contains("Bearer ")) {
            newToken = token.replace("Bearer ", "");
        } else {
            newToken = token;
        }
        UserRequest userRequest = new UserRequest();
        userRequest.setName(name);
        userRequest.setPassword(password);
        userRequest.setEmail(email);
        return given()
                .auth().oauth2(newToken)
                .and()
                .header("Content-Type", "application/json")
                .and()
                .body(userRequest)
                .when()
                .patch(API_FOR_GET_UPDATE_DELETE_USER)
                .then();
    }

    public ValidatableResponse get(String token) {
        String newToken = "";
        if (token.contains("Bearer ")) {
            newToken = token.replace("Bearer ", "");
        } else {
            newToken = token;
        }
        return given()
                .auth().oauth2(newToken)
                .and()
                .header("Content-Type", "application/json")
                .when()
                .get(API_FOR_GET_UPDATE_DELETE_USER)
                .then();
    }

    public ValidatableResponse delete(String token) {
        String newToken = "";
        if (token.contains("Bearer ")) {
            newToken = token.replace("Bearer ", "");
        } else {
            newToken = token;
        }
        return given()
                .auth().oauth2(newToken)
                .when()
                .delete(API_FOR_GET_UPDATE_DELETE_USER)
                .then();
    }
}

