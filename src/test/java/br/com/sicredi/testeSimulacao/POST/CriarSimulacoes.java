package br.com.sicredi.testeSimulacao.POST;

import org.junit.BeforeClass;
import org.junit.Test;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static org.junit.Assert.*;

public class CriarSimulacoes {

  Faker faker = new Faker();
  String nome = faker.name().firstName();
  String cpf = faker.number().digits(11);
  String email = faker.internet().emailAddress();
  Double valor = faker.number().randomDouble(2, 1000, 40000);
  Integer parcelas = faker.number().numberBetween(2, 48);
  Boolean seguro = true;
  String cpf_Existente = "66414919004";
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

  @Test
  public void testCriarUmaSimulacaoComValoresValidosRetornoStatus201() {
    response = postSimulacao(nome, cpf, email, valor, parcelas, seguro);
    assertEquals(201, response.getStatusCode());
    response.then().log().all().assertThat().statusCode(201);
  }

  @Test
  public void testCriarUmaSimulacaoParaUmMesmoCPFRetornoStatus400() {
    response = postSimulacao(nome, cpf_Existente, email, valor, parcelas, seguro);

    assertEquals(400, response.getStatusCode());
    assertEquals("CPF duplicado", response.jsonPath().getString("mensagem"));

    response.then().log().all().assertThat().statusCode(400);
  }

  @Test
  // Possíveis BUGS
  // Não deve criar uma simulação com valor abaixo de mil.
  // Deveria retorna status code 400 conforme regra de negocio.
  // Porén está retornando status code 201
  public void testCriarUmaSimulacaoComValorMenorQueMilRetornoStatus400() {
    response = postSimulacao(nome, cpf, email, 500.00, parcelas, seguro);
    assertEquals(400, response.getStatusCode());
    response.then().log().all().assertThat().statusCode(400);
  }

  @Test
  public void testCriarUmaSimulacaoComValorEntreMileQuarentaMilRetornoStatus201() {
    response = postSimulacao(nome, cpf, email, valor, parcelas, seguro);
    assertEquals(201, response.getStatusCode());
    response.then().log().all().assertThat().statusCode(201);
  }

  @Test
  public void testCriarUmaSimulacaoComValorMaiorQueQuarentaMilRetornoStatus400() {
    response = postSimulacao(nome, cpf, email, 50000.00, parcelas, seguro);
    assertEquals(400, response.getStatusCode());
    assertEquals("Valor deve ser menor ou igual a R$ 40.000", response.jsonPath().getString("erros.valor"));

    response.then().log().all().assertThat().statusCode(400);
  }

  @Test
  public void testCriarUmaSimulacaoComParcelasEntreDoiseQuarentaeOitoRetornoStatus201() {
    response = postSimulacao(nome, cpf, email, valor, parcelas, seguro);
    assertEquals(201, response.getStatusCode());
    response.then().log().all().assertThat().statusCode(201);
  }

  // Possíveis BUGS
  // Não deve criar uma simulação parcelas maiores que 48.
  @Test
  public void testCriarUmaSimulacaoComParcelasMaiorQueQuarentaeOitoRetronoStatus400() {
    response = postSimulacao(nome, cpf, email, valor, 60, seguro);

    assertEquals(400, response.getStatusCode());
    assertEquals("Parcelas deve ser igual ou menor que 48", response.jsonPath().getString("erros.parcelas"));

    response.then().log().all().assertThat().statusCode(400);
  }

}
