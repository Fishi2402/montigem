/* (c) https://github.com/MontiCore/monticore */
package security;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import loginmodule.LoginModuleManager;
import loginmodule.LoginModuleUserBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Public facing endpoints
 */
@RestController
@Service
public class LoginModuleRestService {

  @PostMapping(value = "/loginmodule/public/reset_password", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  @CrossOrigin
  public ResponseEntity<String> resetPassword(@RequestBody String raw) {
    var json = JsonParser.parseJsonObject(raw);

    if (json == null) {
      return ResponseEntity.badRequest().build();
    }

    // Return result
    return onPasswordReset.publicReset(json);
  }

  public OnPasswordReset onPasswordReset = json -> {

    if (!LoginModuleManager.getLoginSecManager().isAllowPasswordReset()) {
      return ResponseEntity.badRequest().body("{\"error\":\"disabled\"}");
    }

    if (!json.hasStringMember("email")) {
      return ResponseEntity.badRequest().build();
    }

    String email = json.getStringMember("email");
    var user = LoginModuleManager.getLoginModuleUserList().stream().filter(u -> u.getEmail().equals(email)).findFirst();

    if (user.isEmpty()) {
      // Return the "okay" response no matter what, to not leak the emails
      return ResponseEntity.ok("{\"status\":\"ok\"}");
    }

    // TODO: Send email with a password reset link

    // For now, we just set the password to be the email
    user.get().setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(email));

    return ResponseEntity.ok("{\"status\":\"ok\"}");
  };

  public interface OnPasswordReset {
    ResponseEntity<String> publicReset(de.monticore.symboltable.serialization.json.JsonObject jsonObject);
  }

  @PostMapping(value = "/loginmodule/public/signup", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  @CrossOrigin
  public ResponseEntity<String> publicSignup(@RequestBody String raw) {
    var json = JsonParser.parseJsonObject(raw);

    if (json == null || !json.hasStringMember("email") || !json.hasStringMember("password")) {
      return ResponseEntity.badRequest().build();
    }

    // Return result
    return this.onSignup.publicSignup(json);
  }

  // A list of additional things to be called when calling the builder by the default action
  public List<BiConsumer<JsonObject, LoginModuleUserBuilder>> onSignupAdditionals = new ArrayList<>();

  public OnSignup onSignup = json -> {

    if (!LoginModuleManager.getLoginSecManager().isAllowPublicSignUp()) {
      return ResponseEntity.badRequest().body("{\"error\":\"disabled\"}");
    }

    if (!json.hasStringMember("email") || !json.hasStringMember("password") || !json.hasStringMember("username")) {
      return ResponseEntity.badRequest().build();
    }

    if (LoginModuleManager.getLoginModuleUserList().stream().anyMatch(u -> u.getEmail().equals(json.getStringMember("email")))) {
      return ResponseEntity.badRequest().body("{\"error\":\"duplicate_email\"}");
    }
    if (LoginModuleManager.getLoginModuleUserList().stream().anyMatch(u -> u.getUsername().equals(json.getStringMember("username")))) {
      return ResponseEntity.badRequest().body("{\"error\":\"duplicate_username\"}");
    }

    var builder = LoginModuleManager.loginModuleUserBuilder();
    builder.email(json.getStringMember("email"));
    builder.password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(json.getStringMember("password")));
    builder.registrationDate(LocalDateTime.now());
    builder.username(json.getStringMember("username"));

    onSignupAdditionals.forEach(c -> c.accept(json, builder));

    if (!builder.isBuildable()) {
      // TODO: Show Log Findings somehow?
      return ResponseEntity.badRequest().body("{\"error\":\"not_buildable\"}");
    }

    if (builder.build().isEmpty()) {
      return ResponseEntity.badRequest().body("{\"error\":\"failed_to_build\"}");
    }

    // TODO: Send a welcome email?
    // Similar to User_create

    return ResponseEntity.ok("{\"success\":true}");
  };

  public interface OnSignup {
    ResponseEntity<String> publicSignup(de.monticore.symboltable.serialization.json.JsonObject jsonObject);
  }
}
