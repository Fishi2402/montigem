<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("@AfterAll protected static void stopFrontend()")}

frontendProcess.filter(Process::isAlive).ifPresentOrElse(process -> {
  System.out.println("Stopping frontend process");
  process.descendants().forEach(ProcessHandle::destroy);
  process.destroy();
}, () -> {
  System.out.println("Can not stop frontend process");
});

