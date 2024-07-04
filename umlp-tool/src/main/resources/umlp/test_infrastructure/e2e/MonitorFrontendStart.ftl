<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("protected void monitorFrontendStart(Process frontendProcess) throws IOException")}

boolean available = false;

long start = System.currentTimeMillis();
List<String> lines = new ArrayList<>();
BufferedReader stdOutreader = new BufferedReader(new InputStreamReader(frontendProcess.getInputStream()));
BufferedReader stderrReader = new BufferedReader(new InputStreamReader(frontendProcess.getErrorStream()));
while (frontendProcess.isAlive()) {
  if ((System.currentTimeMillis() - start > 1000 * 60)) {
    System.out.println("Frontend not available after 60 seconds. Aborting.");
    break;
  }

  String line = stdOutreader.readLine();
  lines.add(line);
  System.out.println("[FRONTEND SERVER] " + line);
  if (line != null && line.contains("localhost:" + frontendServerPort)) {
    available = true;
    break;
  } else if (line != null && line.contains("already in use")) {
    break;
  }
}

if (!available) {
  String line;
  System.out.println("NPM stdout was:");
  for (String s : lines) {
    System.out.println(s);
  }
  while ((line = stdOutreader.readLine()) != null) {
    System.out.println(line);
  }

  System.out.println("NPM stderr was:");
  while ((line = stderrReader.readLine()) != null) {
    System.out.println(line);
  }

  throw new IllegalStateException("Frontend server can not be started");
}
