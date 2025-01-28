package dtcockpit.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class DtcockpitSecurityConfig extends DtcockpitSecurityConfigTOP {
    protected Collection<Map.Entry<HttpMethod, String>> getNoAuthRoutes(){
      Collection<Map.Entry<HttpMethod, String>> routes = new ArrayList<>(super.getNoAuthRoutes());
      routes.addAll(Map.of(HttpMethod.POST, "/server_status/*").entrySet());
      return routes;
    }
}
