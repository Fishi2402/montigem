<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("protected void cleanupBackend(Page page, BrowserContext browserContext)")}

page.close();
browserContext.close();

CommandWebSocketService service = this.authWebsocketService!= null ?
    this.authWebsocketService : this.noAuthWebsocketService;

// Check in the Backend if there is still an open connection and if so wait
while(!service.getClientIds().isEmpty()) {
  try {
    Thread.sleep(200);
    } catch (InterruptedException e) {
      throw new RuntimeException("Failed to wait for backend", e);
  }
}
Log.info("Clean up of Backend", this.getClass().getName());
DatabaseAccessManager.executeStatement("DROP ALL OBJECTS");

