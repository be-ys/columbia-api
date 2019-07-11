package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.domain.ColumbiaNewsletter;
import com.almerys.columbia.api.domain.dto.NewsletterUpdater;
import com.almerys.columbia.api.services.NewsletterService;
import com.almerys.columbia.api.services.Utilities;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/newsletters")
public class NewsletterController extends AbstractRestController {

  private final NewsletterService service;
  private final Utilities utilities;

  public NewsletterController(NewsletterService service, Utilities utilities) {
    this.service = service;
    this.utilities = utilities;
  }

  //---------- GET
  //Récupère le statut d'inscription de l'email.
  @GetMapping(value = "/{token}")
  public ResponseEntity getNewsletter(@NotNull @PathVariable("token") String token) {
    ColumbiaNewsletter result = service.getByToken(token);

    return (result == null)
        ? forbidden()
        : ResponseEntity.status(HttpStatus.OK).body(result);
  }

  //---------- POST
  //Inscrit une adresse mail.
  @PostMapping(value = { "/", "" })
  public ResponseEntity createNewsletter(@NotNull @Validated @RequestBody NewsletterUpdater updater, UriComponentsBuilder ucb) {
    ColumbiaNewsletter result = service.createFromUpdater(updater);
    ucb = ucb.scheme(utilities.getScheme());


    return (result == null)
        ? badRequest()
        : ResponseEntity.created(getLocation(ucb, "/newsletters/{token}", result.getToken())).build();
  }

  //---------- PUT
  //Met à jour une adresse mail.
  @PutMapping(value = "/{token}")
  public ResponseEntity updateNewsletter(@NotNull @Validated @RequestBody NewsletterUpdater updater,
      @PathVariable @NotNull String token) {

    ColumbiaNewsletter result = service.update(updater, token);

    return (result == null)
        ? badRequest()
        : ResponseEntity.ok().build();
  }

  //---------- DELETE
  //Supprime tout les enregistrements d'une adresse mail.
  @DeleteMapping(value = "/{token}")
  public ResponseEntity<Void> removeNewsletter(@NotNull @PathVariable("token") String token) {
    service.deleteFromToken(token);

    return ResponseEntity.status(HttpStatus.OK).build();
  }
}