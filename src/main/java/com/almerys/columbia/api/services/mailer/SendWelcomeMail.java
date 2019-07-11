package com.almerys.columbia.api.services.mailer;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaNewsletter;
import com.almerys.columbia.api.services.Utilities;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SendWelcomeMail {

  private final TemplateEngine templateEngine;
  private final JavaMailSender javaMailSender;
  private final Utilities utilities;
  private final ColumbiaConfiguration columbiaConfiguration;

  private static final Logger logger = Logger.getLogger("newsletter");

  public SendWelcomeMail(TemplateEngine templateEngine, JavaMailSender javaMailSender, Utilities utilities, ColumbiaConfiguration columbiaConfiguration) {
    this.templateEngine = templateEngine;
    this.javaMailSender = javaMailSender;
    this.utilities = utilities;
    this.columbiaConfiguration = columbiaConfiguration;
  }

  public String build(ColumbiaNewsletter columbiaNewsletter) {
    Context context = new Context();
    context.setVariable("email", utilities.decryptEmail(columbiaNewsletter.getEmail()));
    context.setVariable("url", columbiaConfiguration.getFrontURL() + "#/newsletter/" + columbiaNewsletter.getToken());

    StringBuilder contextes = new StringBuilder();

    for (ColumbiaContext e : columbiaNewsletter.getSubscribedContexts()) {
      contextes.append(e.getName()).append(", ");
    }
    if (contextes.length() > 0) {
      context.setVariable("contextes", contextes.subSequence(0, contextes.length() - 2));
    }

    return templateEngine.process("welcomeMail", context);
  }

  public void prepareAndSend(ColumbiaNewsletter columbiaNewsletter) {
    MimeMessagePreparator messagePreparator = mimeMessage -> {
      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
      messageHelper.setTo(utilities.decryptEmail(columbiaNewsletter.getEmail()));
      messageHelper.setSubject("Inscription Newsletter");
      String content = build(columbiaNewsletter);
      messageHelper.setText(content, true);
    };
    try {
      javaMailSender.send(messagePreparator);
    } catch (MailException e) {
      logger.log(Level.SEVERE, "Could not send welcome email to " + utilities.decryptEmail(columbiaNewsletter.getEmail()) + " ! Exception is : " + e.toString());
    }
  }

}
