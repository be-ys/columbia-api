
package com.almerys.columbia.api.services;

import com.almerys.columbia.api.ColumbiaConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class ApplicationContextService implements ApplicationContextAware {
  private static ApplicationContext context;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    context = applicationContext;
  }

  public static ColumbiaConfiguration getColumbiaConfiguration() {
    return context.getBean(ColumbiaConfiguration.class);
  }

}
