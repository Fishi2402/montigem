package security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Bean
  public CustomUserDetailsService customUserDetailsService() {
    return new CustomUserDetailsService();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    // ToDo: insecure!!!!
    http.csrf().disable();

    http.authorizeRequests((requests) -> requests.anyRequest().authenticated());
    http.formLogin()
        .failureHandler((request, response, exception) -> {
          Map<String, Object> data = new HashMap<>();
          data.put("success", false);
          data.put("reason", exception.getMessage());
          sendJSONResponse(response, data);
        })
        .successHandler((request, response, authentication) -> {
          Map<String, Object> data = new HashMap<>();
          data.put("success", true);
          sendJSONResponse(response, data);
        });
    http.httpBasic();
  }

  protected void sendJSONResponse(HttpServletResponse response, Map<String, Object> data) throws IOException {
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    PrintWriter out = response.getWriter();
    new ObjectMapper().writeValue(out, data);
    out.flush();
  }

}
