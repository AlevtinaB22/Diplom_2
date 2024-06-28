package order;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import java.util.List;

import static base.Constants.API_FOR_CREATE_ORDER;
import static base.Constants.API_FOR_GET_INGREDIENTS;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;

public class OrderMetods {

    public ValidatableResponse create(OrderRequest ingredients, String token) {
        String newToken = "";
        if (token.contains("Bearer ")) {
            newToken = token.replace("Bearer ", "");
        } else {
            newToken = token;
        }
        return given()
                .auth().oauth2(newToken)
                .contentType(ContentType.JSON)
                .body(ingredients)
                .when()
                .post(API_FOR_CREATE_ORDER)
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
                .get(API_FOR_CREATE_ORDER)
                .then();
    }

    public List<String> getAllIngredients() {
        return given()
                .contentType(ContentType.JSON)
                .get(API_FOR_GET_INGREDIENTS)
                .then()
                .statusCode(SC_OK)
                .extract().path("data._id");
    }
}
