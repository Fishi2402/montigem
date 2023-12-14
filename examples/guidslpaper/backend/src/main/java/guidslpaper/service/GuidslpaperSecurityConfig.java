package guidslpaper.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.Collection;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class GuidslpaperSecurityConfig extends GuidslpaperSecurityConfigTOP {
    protected Collection<Map.Entry<HttpMethod, String>> getNoAuthRoutes(){
      return Map.of(HttpMethod.POST, "/server_status/*").entrySet();
    }
}
