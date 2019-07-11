package com.almerys.columbia.api.services;

import com.almerys.columbia.api.repository.ContextRepository;
import com.almerys.columbia.api.repository.DefinitionRepository;
import com.almerys.columbia.api.repository.TermRepository;
import com.almerys.columbia.api.repository.UserRepository;
import io.jsonwebtoken.lang.Assert;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatsService {
  private final ContextRepository contextRepository;
  private final DefinitionRepository definitionRepository;
  private final TermRepository termRepository;
  private final UserRepository userRepository;

  public StatsService(ContextRepository contextRepository, DefinitionRepository definitionRepository, TermRepository termRepository,
      UserRepository userRepository) {
    this.contextRepository = contextRepository;
    this.definitionRepository = definitionRepository;
    this.termRepository = termRepository;
    this.userRepository = userRepository;
  }

  public Map<String, Long> getStats() {
    HashMap<String, Long> stats = new HashMap<>();
    stats.put("definitionsNumber", definitionRepository.count());
    stats.put("termsNumber", termRepository.count());
    stats.put("usersNumber", userRepository.count());
    stats.put("contextsNumber", contextRepository.count());

    return stats;
  }

  public Map<String, Long> getStatsForContext(Long contextId) {
    Assert.notNull(contextId, "contextId cannot be null.");

    HashMap<String, Long> stats = new HashMap<>();
    stats.put("definitionsNumber", definitionRepository.countDefinitionByContextId(contextId));
    return stats;
  }
}
