package com.almerys.columbia.api.services.mailer;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaUser;
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
public class SendLostPasswordMail {

  private final TemplateEngine templateEngine;
  private final JavaMailSender javaMailSender;
  private final Utilities utilities;
  private final ColumbiaConfiguration columbiaConfiguration;

  private static final Logger logger = Logger.getLogger("user");

  public SendLostPasswordMail(TemplateEngine templateEngine, JavaMailSender javaMailSender, Utilities utilities, ColumbiaConfiguration columbiaConfiguration) {
    this.templateEngine = templateEngine;
    this.javaMailSender = javaMailSender;
    this.utilities = utilities;
    this.columbiaConfiguration = columbiaConfiguration;
  }

  public String build(ColumbiaUser columbiaUser) {
    Context context = new Context();
    context.setVariable("username", columbiaUser.getUsername());
    context.setVariable("url", columbiaConfiguration.getFrontURL() + "lostPassword/"  + columbiaUser.getActivationKey());

    return templateEngine.process("lostPasswordMail", context);
  }

  public void prepareAndSend(ColumbiaUser columbiaUser) {
    MimeMessagePreparator messagePreparator = mimeMessage -> {
      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
      messageHelper.setFrom("noreply@columbia.almerys.com");
      messageHelper.setTo(utilities.decryptEmail(columbiaUser.getEmail()));
      messageHelper.setSubject("Réinitialisation de mot de passe");
      String content = build(columbiaUser);
      messageHelper.setText(content, true);
    };
    try {
      javaMailSender.send(messagePreparator);
    } catch (MailException e) {
      logger.log(Level.SEVERE, "Could not send email to " + utilities.decryptEmail(columbiaUser.getEmail()) + " ! Exception is : " + e.toString());
    }
  }

}
