package br.com.sicredi.testeSimulacao.DELETE;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class RemoveSimulacao {

  Faker faker = new Faker();
  String nome = faker.name().firstName();
  String cpf = faker.number().digits(11);
  String email = faker.internet().emailAddress();
  Double valor = faker.number().randomDouble(2, 1000, 40000);
  Integer parcelas = faker.number().numberBetween(2, 48);
  Boolean seguro = true;
  String Barra = "/";
  Response response;

  @BeforeClass
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080/";
    RestAssured.basePath = "/api/v1/simulacoes";
  }

  public Response postSimulacao(
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
        .post();
  }

  public void NewID() {
    response = postSimulacao(nome, cpf, email, valor, parcelas, seguro);
    String id = response.jsonPath().getString("id");
    System.out.println(id);
  }

  @Test
  // Deveria retorna status code 204 conforme regra de negocio.
  // Porén está retornando status code 200
  public void testDeletarUmaSimulacaoPorIdRetronoStatus200() {
    NewID();
    response = RestAssured.given()
        .when()
        .delete(Barra + response.jsonPath().getString("id"));
    assertEquals(200, response.getStatusCode());
    response.then().log().all().assertThat().statusCode(200);
  }

  @Test
  // Deveria retorna status code 404 com a mensagem "Simulação não encontrada"
  // caso não encontre o id informado.
  public void testDeletarUmaSimulacaoPorIdInexistenteRetronoStatus404() {
    response = RestAssured.given()
        .when()
        .delete(Barra + 00000000000);
    assertEquals(404, response.getStatusCode());
    assertEquals("Simulação não encontrada", response.jsonPath().getString("mensagem"));
    response.then().log().all().assertThat().statusCode(404);
  }

}
