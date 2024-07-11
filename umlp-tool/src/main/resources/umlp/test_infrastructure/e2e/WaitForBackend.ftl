<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("protected void waitForBackend()")}

try {
  Thread.sleep(200);
} catch (InterruptedException e) {
  throw new RuntimeException("Failed to wait for backend", e);
}
