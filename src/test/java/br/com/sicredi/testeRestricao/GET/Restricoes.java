package br.com.sicredi.testeRestricao.GET;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class Restricoes {

  Faker faker = new Faker();
  String cpf_SemRestricao = faker.number().digits(11);
  String cpf_Restrito = "97093236014";
  Response response;

  @BeforeClass
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080/";
    RestAssured.basePath = "/api/v1/restricoes/";
  }

  public Response getSimulacao(String cpf) {
    return RestAssured.given()
        .contentType(ContentType.JSON)
        .body("{\"cpf\":\"" + cpf + "\"}")
        .when()
        .get(cpf);
  }

  @Test
  public void testConsultarPortCpfComRestricaoRetornoStatus200() {
    response = getSimulacao(cpf_Restrito);
    assertEquals(200, response.getStatusCode());
    assertEquals("O CPF 97093236014 tem problema", response.jsonPath().getString("mensagem"));

    response.then().log().all().assertThat().statusCode(200);

  }

  @Test
  public void testConsultarPortCpfSemRestricaoRetornoStatus204() {
    response = getSimulacao(cpf_SemRestricao);
    assertEquals(204, response.getStatusCode());

    response.then().log().all().assertThat().statusCode(204);
  }

}