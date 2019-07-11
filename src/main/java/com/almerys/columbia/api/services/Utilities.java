package com.almerys.columbia.api.services;

import com.almerys.columbia.api.ColumbiaConfiguration;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;

@Service
public class Utilities {
  private final ColumbiaConfiguration columbiaConfiguration;

  @Autowired
  public Utilities(ColumbiaConfiguration columbiaConfiguration) {
    this.columbiaConfiguration = columbiaConfiguration;
  }

  public String getScheme() {
    return columbiaConfiguration.getEnforceHttps() ? "https" : "http";
  }

  public String cryptEmail(@Email String email) {
    BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
    textEncryptor.setPassword(columbiaConfiguration.getCryptPassword());
    return textEncryptor.encrypt(email);
  }

  public String decryptEmail(String crypted) {
    BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
    textEncryptor.setPassword(columbiaConfiguration.getCryptPassword());
    return textEncryptor.decrypt(crypted);
  }

  public String cryptPassword(String password) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(columbiaConfiguration.getCryptPower());
    return passwordEncoder.encode(password);
  }

  public Boolean checkPassword(String password, String encodedPassword){
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(columbiaConfiguration.getCryptPower());
    return passwordEncoder.matches(password, encodedPassword);
  }

  public static String escapeHtmlTags(String str) {
    if (str == null) {
      return null;
    }
    return str.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
  }

  public static boolean isEmptyOrNull(String str) {
    return (str == null || str.trim().equals(""));
  }
}
