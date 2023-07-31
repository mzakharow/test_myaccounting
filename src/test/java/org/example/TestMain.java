package org.example;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class TestMain {

    @Test
    public void main(){
        createUser();
        String jwt = signinUser();
        Long id = createTransfers(jwt);
        getAllTransfers(jwt);
        deleteTransfer(id, jwt);
    }

    private static void createUser() throws JSONException {
        String baseURI = "http://localhost:8080/api/auth/signup";
        JSONObject requestBody = new JSONObject()
                .put("username", "test_name")
                .put("firstname", "testname")
                .put("lastname", "test-last-name")
                .put("email", "test@mail.com")
                .put("password", "Qwerty123")
                .put("confirmPassword", "Qwerty123");

        RequestSpecification request = given();
        request.header("Content-Type", "application/json");
        request.body(requestBody.toString());
        Response response = request.post(baseURI);
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);
        String message = response.getBody().path("message");
        Assert.assertEquals(message, "User registered successfully!");
        System.out.println("Message: " + message + " Code: " + statusCode);
    }

    private static String signinUser() throws JSONException {
        String baseURI = "http://localhost:8080/api/auth/signin";
        JSONObject requestBody = new JSONObject()
                .put("username", "test@mail.com")
                .put("password", "Qwerty123");

        RequestSpecification request = given();
        request.header("Content-Type", "application/json");
        request.body(requestBody.toString());
        Response response = request.post(baseURI);
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);
        Assert.assertTrue(response.getBody().path("success"));
        System.out.println("Code: " + statusCode);
        return response.getBody().path("token");
    }

    private static Long createTransfers(String jwt) throws JSONException {
        String baseURI = "http://localhost:8080/api/transfer/create";
        JSONObject requestBody = new JSONObject()
                .put("category", "test category")
                .put("comment", "test comment")
                .put("sum", 1000);

        RequestSpecification request = given()
                .header("Authorization", jwt);
        request.header("Content-Type", "application/json");
        request.body(requestBody.toString());
        Response response = request.post(baseURI);
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);
        int id = response.getBody().path("id");
        Long idLong = Long.parseLong(String.valueOf(id));
        System.out.println("Create transfer id=" + id + " Code: " + statusCode);
        return idLong;
    }

    private static void getAllTransfers(String jwt) throws JSONException {
        RestAssured.baseURI = "http://localhost:8080/api/transfer";
        given()
                .header("Authorization", jwt)
                .param("foo1", "bar1")
                .when()
                .get("/user/transfers")
                .then()
                .statusCode(200);
    }

    private static void deleteTransfer(Long id, String jwt) throws JSONException {
        String baseURI = "http://localhost:8080/api/transfer/{id}/delete";
        baseURI = baseURI.replace("{id}", id.toString());

        RestAssured.baseURI = baseURI;
        RequestSpecification httpRequest = RestAssured.given()
                .header("Authorization", jwt);
        Response response = httpRequest.request(Method.DELETE);
        int statusCode = response.getStatusCode();
        System.out.println("Status code is: " + statusCode);
        Assert.assertEquals(statusCode, 200);
    }
}
