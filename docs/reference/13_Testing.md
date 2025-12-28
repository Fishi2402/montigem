# 13. Testing

MontiGem supports various testing strategies to ensure the quality of your application.

## 13.1 E2E Testing with Playwright

End-to-End (E2E) tests verify the application flow from the user's perspective. MontiGem integrates with [Playwright](https://playwright.dev/) for robust browser automation.

### Setup
Enable Playwright in your `frontend/build.gradle`:
```groovy
generateTestInfrastructure {
  usePlaywright.set(true)
}
```

### Writing Tests
Tests are written in Java and located in `frontend/src/test/java`. They extend `AbstractPlaywrightE2ETest`.

```java
public class MyE2ETest extends AbstractPlaywrightE2ETest {
  @Test
  public void testLogin() {
    page.navigate("http://localhost:4200");
    page.fill("#username", "admin");
    page.click("#loginButton");
    assertThat(page.locator("#welcomeMessage")).isVisible();
  }
}
```

## 13.2 Simulated GUI Tests

For faster feedback on GUI logic without a full browser, MontiGem generates "Simulated GUI" classes. These allow you to test component interaction (clicks, input) in a pure Java environment.

For more details, see [Testing in MontiGem](../unstructured/Testing.md).

---
[< Previous: Deployment](12_Deployment.md) | [Back to Index](../MontiGem_Documentation.md)
