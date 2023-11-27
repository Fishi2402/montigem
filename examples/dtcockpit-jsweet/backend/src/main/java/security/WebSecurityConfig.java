package security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
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
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    // ToDo: insecure!!!!
    http.csrf().disable();

    http.authorizeRequests((requests) -> requests.anyRequest().authenticated());
    http.formLogin()
        .failureHandler(new SimpleUrlAuthenticationFailureHandler() {
          // Note: The HTML error information will not be shown, as spring does not allow the re-use of its error page
          @Override
          public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            if (!MediaType.parseMediaTypes(request.getHeader(HttpHeaders.ACCEPT)).contains(MediaType.APPLICATION_JSON)) {
              super.onAuthenticationFailure(request, response, exception); // delegate to default* behavior
              return;
            }
            // Reply with JSON object
            Map<String, Object> data = new HashMap<>();
            data.put("success", false);
            data.put("reason", exception.getMessage()); // reason might be localized
            sendJSONResponse(response, data);
          }
        })
        .successHandler(new SavedRequestAwareAuthenticationSuccessHandler() {
          @Override
          public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            if (!MediaType.parseMediaTypes(request.getHeader(HttpHeaders.ACCEPT)).contains(MediaType.APPLICATION_JSON)) {
              super.onAuthenticationSuccess(request, response, authentication); // delegate to default* behavior
              return;
            }
            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            sendJSONResponse(response, data);
          }
        })
        ;
  }

  /**
   * Reply with the data, given as a {@link Map}, serialized as JSON
   */
  protected void sendJSONResponse(HttpServletResponse response, Map<String, Object> data) throws IOException {
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    PrintWriter out = response.getWriter();
    new ObjectMapper().writeValue(out, data);
    out.flush();
  }

}
