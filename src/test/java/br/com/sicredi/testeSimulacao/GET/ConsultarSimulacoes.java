package br.com.sicredi.testeSimulacao.GET;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ConsultarSimulacoes {

  Faker faker = new Faker();
  String nome = faker.name().firstName();
  String cpf = faker.number().digits(11);
  String cpf_Existente = "66414919004";
  String cpf_NaoCastrado = "00000000000";
  String email = faker.internet().emailAddress();
  Double valor = faker.number().randomDouble(2, 1000, 40000);
  Integer parcelas = faker.number().numberBetween(2, 48);
  Boolean seguro = true;
  Response response;

  @BeforeClass
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080/";
    RestAssured.basePath = "/api/v1/simulacoes/";
  }

  public Response getSimulacao(
      String nome,
      String cpf,
      String email,
      Double valor,
      Integer parcelas,
      Boolean seguro) {
    return RestAssured.given()
        .contentType(ContentType.JSON)
        .body("{\"nome\":\"" + nome + "\",\"cpf\":\"" + cpf + "\",\"email\":\"" + email + "\",\"valor\":\"" + valor
            + "\",\"parcelas\":\"" + parcelas + "\",\"seguro\":\"" + seguro + "\"}")
        .when()
        .get(cpf);
  }

  @Test
  public void testConsultarTodasSimulacoesRetornoStatus200() {
    RestAssured.given()
        .contentType(ContentType.JSON)
        .when()
        .get()
        .then()
        .log().all()
        .statusCode(200);
  }

  @Test
  public void testConsultarPorCpfValidoRetornoStatus200() {
    response = getSimulacao(nome, cpf_Existente, email, valor, parcelas, seguro);
    response.then().log().all().assertThat().statusCode(200);
  }

  @Test
  public void testConsultarPorCpfQueNaoEstaCadastradoRetornoStatus404() {
    response = getSimulacao(nome, cpf_NaoCastrado, email, valor, parcelas, seguro);

    assertEquals("CPF 00000000000 não encontrado", response.jsonPath().getString("mensagem"));
    assertEquals(404, response.getStatusCode());

    response.then().log().all().assertThat().statusCode(404);
  }

}
