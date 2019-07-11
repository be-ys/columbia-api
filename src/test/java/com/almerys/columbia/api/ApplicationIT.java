package com.almerys.columbia.api;

import static org.assertj.core.api.Assertions.*;

import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaDefinition;
import com.almerys.columbia.api.domain.ColumbiaNewsletter;
import com.almerys.columbia.api.domain.ColumbiaTerm;
import com.almerys.columbia.api.domain.dto.DefinitionUpdater;
import com.almerys.columbia.api.domain.dto.NewsletterUpdater;
import com.almerys.columbia.api.domain.dto.TermUpdater;
import com.almerys.columbia.api.repository.ContextRepository;
import com.almerys.columbia.api.domain.dto.ContextUpdater;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationIT {

  @Autowired
  TestRestTemplate template;

  @Autowired
  ContextRepository repo;

  @Test
  public void AcontextLoads() {
    //Ignored
  }

  public static String username = "";
  public static String password = "";
  public static String token = "";

  @Test
  public void BSimpleAuth() {
    //Authentication
    ResponseEntity<String> authenticationRequest = template.getForEntity("/setup", String.class);
    assertThat(authenticationRequest.getStatusCode()).isEqualTo(HttpStatus.OK);

    username = new JSONObject(authenticationRequest.getBody()).getString("user");
    password = new JSONObject(authenticationRequest.getBody()).getString("password");

    //Login
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    LinkedMultiValueMap args = new LinkedMultiValueMap<>();
    args.add("username", username);
    args.add("password", password);
    args.add("domain", "local");

    ResponseEntity columbia_connect = template.postForEntity("/login", new HttpEntity<>(args, headers), String.class);
    token = columbia_connect.getHeaders()
                            .getFirst("Authorization");
  }

  @Test
  public void CshouldPersistContext() {

    // no data to start with
    ResponseEntity<String> results = template.getForEntity("/contexts", String.class);
    assertThat(results.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(new JSONObject(results.getBody()).getJSONArray("content")).isEmpty();

    // create a context
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", token);

    ObjectMapper objectMapper = new ObjectMapper();

    HttpEntity<String> entity = null;
    try {
      entity = new HttpEntity<>(objectMapper.writeValueAsString(new ColumbiaContext(null, "context1", "Description1", null)), headers);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    ResponseEntity<Void> r2 = template.postForEntity("/contexts", entity, Void.class);
    assertThat(r2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(Objects.requireNonNull(r2.getHeaders()
                                        .getLocation())
                      .toString()).endsWith("/contexts/1");

    // check that context exists.
    ResponseEntity<ColumbiaContext> context = template.getForEntity("/contexts/1", ColumbiaContext.class);
    assertThat(context.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(context.getBody()).isEqualToComparingFieldByFieldRecursively(new ColumbiaContext(1L, "context1", "Description1", null));

    // update
    try {
      entity = new HttpEntity<>(objectMapper.writeValueAsString(new ColumbiaContext(null, "misàjour", "update", null)), headers);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    template.put("/contexts/1", entity);

    // check that context exists.
    context = template.getForEntity("/contexts/1", ColumbiaContext.class);
    assertThat(context.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(context.getBody()).isEqualToComparingFieldByFieldRecursively(new ColumbiaContext(1L, "misàjour", "update", null));

    // delete
    HttpHeaders h = new HttpHeaders();
    h.set("Authorization", token);
    template.exchange("/contexts/1", HttpMethod.DELETE, new HttpEntity<>(null, h), Void.class, 1);

    // check that context does not exists.
    ResponseEntity<ColumbiaContext> contextfinal = template.getForEntity("/contexts/1", ColumbiaContext.class);
    assertThat(contextfinal.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void DshouldPersistTerms() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", token);

    //no data to start with
    ResponseEntity<String> results = template.getForEntity("/terms", String.class);
    assertThat(results.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(new JSONObject(results.getBody()).getJSONArray("content")).isEmpty();

    //Create term.
    ObjectMapper objectMapper = new ObjectMapper();

    HttpEntity<String> entity = null;
    try {
      entity = new HttpEntity<>(objectMapper.writeValueAsString(new ColumbiaTerm(null, "Bonjour")), headers);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    ResponseEntity<Void> r2 = template.postForEntity("/terms", entity, Void.class);
    assertThat(r2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(Objects.requireNonNull(r2.getHeaders()
                                        .getLocation())
                      .toString()).endsWith("/terms/1");

    // check that term exists.
    ResponseEntity<ColumbiaTerm> term = template.getForEntity("/terms/1", ColumbiaTerm.class);
    assertThat(term.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(term.getBody()).isEqualToComparingFieldByFieldRecursively(new ColumbiaTerm(1L, "Bonjour"));

    // update
    try {
      entity = new HttpEntity<>(objectMapper.writeValueAsString(new ColumbiaTerm(1L, "update")), headers);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    template.put("/terms/1", entity);

    term = template.getForEntity("/terms/1", ColumbiaTerm.class);
    assertThat(term.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(term.getBody()).isEqualToComparingFieldByFieldRecursively(new ColumbiaTerm(1L, "update"));

    // delete
    template.exchange("/terms/1", HttpMethod.DELETE, new HttpEntity<>(null, headers), Void.class, 1);

    // check that context does not exists.
    ResponseEntity<ColumbiaContext> contextfinal = template.getForEntity("/terms/1", ColumbiaContext.class);
    assertThat(contextfinal.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  // should persist definitions.
  @Test
  public void EshouldPersistDefinition() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", token);

    //Create term.
    ObjectMapper objectMapper = new ObjectMapper();

    HttpEntity<String> entity = null;
    try {
      entity = new HttpEntity<>(objectMapper.writeValueAsString(new ColumbiaTerm(null, "Bonjour")), headers);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    ResponseEntity<Void> r2 = template.postForEntity("/terms", entity, Void.class);
    assertThat(r2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(Objects.requireNonNull(r2.getHeaders()
                                        .getLocation())
                      .toString()).endsWith("/terms/2");

    // create a context
    objectMapper = new ObjectMapper();
    entity = null;
    try {
      entity = new HttpEntity<>(objectMapper.writeValueAsString(new ColumbiaContext(null, "context1", "Description1", null)), headers);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    ResponseEntity<Void> r3 = template.postForEntity("/contexts", entity, Void.class);
    assertThat(r3.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(Objects.requireNonNull(r3.getHeaders()
                                        .getLocation())
                      .toString()).endsWith("/contexts/2");

    // create a definition
    DefinitionUpdater definitionUpdater = new DefinitionUpdater();
    definitionUpdater.setTerm(new TermUpdater(2L, null, null));
    definitionUpdater.setContext(new ContextUpdater(2L, null, null, null));
    definitionUpdater.setDefinition("Je suis une définition");

    objectMapper = new ObjectMapper();
    entity = null;
    try {
      entity = new HttpEntity<>(objectMapper.writeValueAsString(definitionUpdater), headers);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    ResponseEntity<Void> r4 = template.postForEntity("/contexts/2/terms", entity, Void.class);
    assertThat(r4.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(Objects.requireNonNull(r4.getHeaders()
                                        .getLocation())
                      .toString()).endsWith("/contexts/2/terms/2");

    // get term list in context
    ColumbiaTerm[] ter = new ColumbiaTerm[] { new ColumbiaTerm(2L, "Bonjour") };

    ResponseEntity<String> term = template.getForEntity("/contexts/2/terms", String.class);
    assertThat(term.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(new JSONObject(term.getBody()).getInt("totalElements")).isEqualTo(1);

    // get definition detail
    ResponseEntity<ColumbiaDefinition> def = template.getForEntity("/contexts/2/terms/2", ColumbiaDefinition.class);
    assertThat(def.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(def.getBody()
                  .getTerm()
                  .getId()).isEqualTo(2L);
    assertThat(def.getBody()
                  .getContext()
                  .getId()).isEqualTo(2L);
    assertThat(def.getBody()
                  .getDefinition()).isEqualTo("Je suis une définition");

    //update
    definitionUpdater.setDefinition("update");
    try {
      entity = new HttpEntity<>(objectMapper.writeValueAsString(definitionUpdater), headers);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    template.put("/terms/2/contexts/2", entity);

    // get definition detail
    def = template.getForEntity("/contexts/2/terms/2", ColumbiaDefinition.class);
    assertThat(def.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(def.getBody()
                  .getTerm()
                  .getId()).isEqualTo(2L);
    assertThat(def.getBody()
                  .getContext()
                  .getId()).isEqualTo(2L);
    assertThat(def.getBody()
                  .getDefinition()).isEqualTo("update");

    // delete
    template.exchange("/terms/2/contexts/2", HttpMethod.DELETE, new HttpEntity<>(null, headers), Void.class, 1);

    // check that context does not exists.
    ResponseEntity<ColumbiaContext> contextfinal = template.getForEntity("/terms/2/contexts/2", ColumbiaContext.class);
    assertThat(contextfinal.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  public void FshouldTestStatsAndFeed() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", token);

    ResponseEntity<ColumbiaContext> contextfinal = template.getForEntity("/stats", ColumbiaContext.class);
    assertThat(contextfinal.getStatusCode()).isEqualTo(HttpStatus.OK);

    contextfinal = template.getForEntity("/feed", ColumbiaContext.class);
    assertThat(contextfinal.getStatusCode()).isEqualTo(HttpStatus.OK);

    contextfinal = template.getForEntity("/stats/contexts/2", ColumbiaContext.class);
    assertThat(contextfinal.getStatusCode()).isEqualTo(HttpStatus.OK);

    contextfinal = template.getForEntity("/feed/contexts/2", ColumbiaContext.class);
    assertThat(contextfinal.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  public void GshouldTestNewsletter() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    //Create a newsletter
    ObjectMapper objectMapper = new ObjectMapper();
    HttpEntity entity = null;
    try {
      entity = new HttpEntity<>(objectMapper.writeValueAsString(new NewsletterUpdater("hello@localhost.fr", null)), headers);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    ResponseEntity<Void> r4 = template.postForEntity("/newsletters", entity, Void.class);
    assertThat(r4.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    String loc = r4.getHeaders()
                   .getLocation()
                   .toString();

    //Get newsletter
    ResponseEntity<ColumbiaNewsletter> ns = template.getForEntity(loc, ColumbiaNewsletter.class);
    assertThat(ns.getStatusCode()).isEqualTo(HttpStatus.OK);

    //Delete newsletter
    template.exchange(loc, HttpMethod.DELETE, new HttpEntity<>(null, null), Void.class, 1);
    ns = template.getForEntity(loc, ColumbiaNewsletter.class);
    assertThat(ns.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  public void HshouldTestHistory() {
    ResponseEntity<Object> obj = template.getForEntity("/history/contexts", Object.class);
    assertThat(obj.getStatusCode()).isEqualTo(HttpStatus.OK);

    obj = template.getForEntity("/history/definitions", Object.class);
    assertThat(obj.getStatusCode()).isEqualTo(HttpStatus.OK);

    obj = template.getForEntity("/history/contexts/2", Object.class);
    assertThat(obj.getStatusCode()).isEqualTo(HttpStatus.OK);

    obj = template.getForEntity("/history/contexts/2/terms", Object.class);
    assertThat(obj.getStatusCode()).isEqualTo(HttpStatus.OK);

    obj = template.getForEntity("/history/contexts/2/terms/2", Object.class);
    assertThat(obj.getStatusCode()).isEqualTo(HttpStatus.OK);

    obj = template.getForEntity("/history/terms", Object.class);
    assertThat(obj.getStatusCode()).isEqualTo(HttpStatus.OK);

    obj = template.getForEntity("/history/terms/2", Object.class);
    assertThat(obj.getStatusCode()).isEqualTo(HttpStatus.OK);

    obj = template.getForEntity("/history/terms/2/contexts", Object.class);
    assertThat(obj.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

}
