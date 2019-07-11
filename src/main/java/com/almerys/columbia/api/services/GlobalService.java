package com.almerys.columbia.api.services;

import com.almerys.columbia.api.domain.ColumbiaTerm;
import io.jsonwebtoken.lang.Assert;
import org.springframework.stereotype.Service;

@Service
public class GlobalService {
  private final ContextService contextService;
  private final DefinitionService definitionService;
  private final NewsletterService newsletterService;
  private final UserService userService;
  private final TermService termService;

  public GlobalService(ContextService service, DefinitionService definitionService, NewsletterService newsletterService,
      TermService termService, UserService userService) {
    this.definitionService = definitionService;
    this.newsletterService = newsletterService;
    this.termService = termService;
    this.contextService = service;
    this.userService = userService;
  }

  public void deleteContext(Long contextId) {
    Assert.notNull(contextId, "contextId cannot be null.");

    //Check if context exists and if it's not a parent.
    if (contextService.getById(contextId) == null || contextService.isParent(contextId)) {
      throw new IllegalArgumentException("ColumbiaContext does not exist, or is a parent. Could not remove it.");
    }

    userService.removeAllRightsFromSpecificContext(contextId);
    definitionService.deleteAllByContextId(contextId);
    newsletterService.removeAllNewsletterFromSpecificContext(contextId);
    contextService.delete(contextId);
  }

  public void deleteTerm(Long termId) {
    Assert.notNull(termId, "termId cannot be null.");

    ColumbiaTerm term = termService.getById(termId);

    if (term == null) {
      throw new IllegalArgumentException("ColumbiaTerm does not exist.");
    }

    definitionService.removeAllReferences(term);
    definitionService.deleteAllByTermId(termId);
    termService.delete(termId);
  }
}
