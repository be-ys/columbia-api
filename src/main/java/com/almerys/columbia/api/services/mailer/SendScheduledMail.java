package com.almerys.columbia.api.services.mailer;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaDefinition;
import com.almerys.columbia.api.domain.ColumbiaNewsletter;
import com.almerys.columbia.api.services.HistoryService;
import com.almerys.columbia.api.services.NewsletterService;
import com.almerys.columbia.api.services.TermService;
import com.almerys.columbia.api.services.Utilities;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SchedulerLock;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SendScheduledMail {

  private final TemplateEngine templateEngine;
  private final JavaMailSender javaMailSender;
  private final Utilities utilities;
  private final NewsletterService newsletterService;
  private final HistoryService historyService;
  private final TermService termService;
  private final ColumbiaConfiguration columbiaConfiguration;

  private static final Logger logger = Logger.getLogger("newsletter");

  public SendScheduledMail(TemplateEngine templateEngine, JavaMailSender javaMailSender, Utilities utilities,
      NewsletterService newsletterService, HistoryService historyService, TermService termService, ColumbiaConfiguration columbiaConfiguration) {
    this.templateEngine = templateEngine;
    this.javaMailSender = javaMailSender;
    this.utilities = utilities;
    this.newsletterService = newsletterService;
    this.historyService = historyService;
    this.termService = termService;
    this.columbiaConfiguration = columbiaConfiguration;
  }

  public String build(ColumbiaNewsletter columbiaNewsletter) {
    Context context = new Context();
    context.setVariable("email", utilities.decryptEmail(columbiaNewsletter.getEmail()));
    context.setVariable("url", columbiaConfiguration.getFrontURL() + "newsletter/" + columbiaNewsletter.getToken());

    StringBuilder modifications = new StringBuilder();

    for (ColumbiaContext con : columbiaNewsletter.getSubscribedContexts()) {

      List<ColumbiaDefinition> list = historyService.getPastWeekDefinitionModifications(con.getId());

      boolean addTitle = true;
      Collection<Long> col = new HashSet<>();
      for (ColumbiaDefinition d : list) {
        if (!col.contains(d.getId().getTermId())) {
          col.add(d.getId().getTermId());
          if (addTitle) {
            modifications.append("<h2>").append(con.getName()).append("</h2><ul>");
          }
          addTitle = false;
          modifications = new StringBuilder(modifications.toString() + "<li><p>Le terme \""
              + termService.getById(d.getId().getTermId()).getName()
              + "\" a désormais pour définition : <i>" + d.getDefinition() + "</i>.</li>");
        }
      }
      if (!addTitle) {
        modifications = new StringBuilder(modifications.toString().concat("</ul>"));
      }

    }

    context.setVariable("modifications", modifications.toString());

    if (modifications.length() == 0) {
      return null;
    }
    return templateEngine.process("newsletterMail", context);
  }

  @Bean
  public LockProvider lockProvider(DataSource dataSource) {
    return new JdbcTemplateLockProvider(dataSource);
  }

  //Lancement de la cron tout les vendredi à 3h00 du matin.
  @Scheduled(cron = "0 0 5 * * 5")
  @SchedulerLock(name = "TaskScheduler_scheduledTask", lockAtLeastForString = "PT25M", lockAtMostForString = "PT30M")
  public void prepareAndSend() {
    logger.log(Level.INFO, "Starting newsletter bulk send.");
    for (ColumbiaNewsletter n : newsletterService.getAll()) {
      String content = build(n);
      if (content != null) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
          MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
          messageHelper.setFrom("noreply@columbia.almerys.com");
          messageHelper.setTo(utilities.decryptEmail(n.getEmail()));
          messageHelper.setSubject("Newsletter - Récapitulatif de la semaine");
          messageHelper.setText(content, true);
        };
        try {
          javaMailSender.send(messagePreparator);
        } catch (MailException e) {
          logger.log(Level.WARNING, "Could not send scheduled email to " + utilities.decryptEmail(n.getEmail()) + " ! Exception is : " + e.toString());
        }
      }
    }

  }

}
