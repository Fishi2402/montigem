How would a generated E2E Test look like? 

```
@SpringBootTest(classes = ${backend}.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(value = Abstract${domainName}E2ETest.PortConfig.class)
public abstract class Abstract${domainName}E2ETest extends AbstractPlayWrightE2ETest {
  @Component
  static class PortConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    public int port = -1;

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
      port = AbstractPromoDBE2ETest.backendServerPort;
      System.out.println("Setting port to " + port);
      factory.setPort(port);
    }
  }
}
```
