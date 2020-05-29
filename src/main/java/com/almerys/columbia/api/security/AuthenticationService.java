package com.almerys.columbia.api.security;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaUser;
import com.almerys.columbia.api.domain.dto.UserUpdater;
import com.almerys.columbia.api.repository.UserRepository;
import com.almerys.columbia.api.services.UserService;
import com.almerys.columbia.api.services.Utilities;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
public class AuthenticationService {

  private Utilities utilities;
  private UserService userService;
  private UserRepository userRepository;
  private ColumbiaConfiguration columbiaConfiguration;

  public AuthenticationService(Utilities utilities, UserService userService, ColumbiaConfiguration columbiaConfiguration, UserRepository userRepository) {
    this.utilities = utilities;
    this.userService = userService;
    this.userRepository = userRepository;
    this.columbiaConfiguration = columbiaConfiguration;
  }

  //Local Authentication
  public String getBearer(String username, String password, String domain) {
    if (!domain.equalsIgnoreCase("local")) {
      throw new IllegalArgumentException("Invalid domain for local connexion");
    }

    ColumbiaUser user = userService.getByUsernameAndDomain(username, domain);

    if (user == null) {
      throw new IllegalArgumentException("User does not exist");
    }

    if (!user.getActiv()) {
      throw new IllegalArgumentException("User is not activ");
    }

    if (!utilities.checkPassword(password, user.getPassword())) {
      throw new IllegalArgumentException("Invalid password");
    }

    return createBearer(user);
  }

  //OAuth2 Authentication
  public String getBearer(String token, String domain) {
    //Connecting to OAuth2 server
    JSONObject object;
    try {
      RestTemplate senderTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("Authorization", token);

      object = new JSONObject(senderTemplate.exchange(columbiaConfiguration.getOauth2UserinfoUrl(), HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody());
    } catch (Exception e) {
      throw new IllegalArgumentException("Unavailable to connect to OAuth2.");
    }

    String uuid = object.getString("sub");
    String username = object.getString("preferred_username");

    ColumbiaUser columbiaUser = userService.getByUsernameAndDomain(username, domain.toLowerCase());

    if (columbiaUser == null) {
      //Création
      UserUpdater userUpdater = new UserUpdater();
      userUpdater.setActiv(true);
      userUpdater.setId(uuid);
      userUpdater.setUsername(username);
      userUpdater.setRole(columbiaConfiguration.getUserRoleName());
      userUpdater.setDomain(domain.toLowerCase());
      columbiaUser = userService.createUser(userUpdater);
    } else if (columbiaUser.getId() == null || !columbiaUser.getId().equals(uuid)) {
      //UUID changé, màj.
      userService.delete(columbiaUser.getId());
      columbiaUser.setId(uuid);
      columbiaUser = userRepository.save(columbiaUser);
    }

    if (columbiaUser == null) {
      throw new IllegalArgumentException("Critical error ! Abort.");
    }


    return createBearer(columbiaUser);

  }

  private String createBearer(ColumbiaUser columbiaUser) {
    Map<String, Object> map = new HashMap<>();
    Collection<String> collection = new HashSet<>();
    (new CustomUserDetails(columbiaUser)).getAuthorities().forEach(e -> collection.add(e.getAuthority()));

    map.put("rights", collection);
    map.put("id", columbiaUser.getId());
    map.put("username", columbiaUser.getUsername());

    String token = Jwts.builder()
                       .setClaims(map)
                       .setId(columbiaUser.getId())
                       .setSubject(columbiaUser.getUsername())
                       .setExpiration(new Date(System.currentTimeMillis() + columbiaConfiguration.getTokenLifetime()))
                       .signWith(SignatureAlgorithm.HS512, columbiaConfiguration.getTokenSecret())
                       .compact();

    return columbiaConfiguration.getTokenPrefix() + token;
  }
}
