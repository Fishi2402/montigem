<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("protected Process startNpmProcess(String npmPath, int frontendServerPort, File frontendGenDirectory) throws IOException")}

Path nodeDir = Paths.get(npmPath).getParent();

System.out.println("Starting npm from " + npmPath + " on port " + frontendServerPort);
ProcessBuilder pb = new ProcessBuilder(npmPath, "run", "start", "--", "--port", "" + frontendServerPort);
pb.environment().put("PATH", nodeDir.toString() + File.pathSeparator + pb.environment().get("PATH"));
pb.environment().put("BACKEND_PROXY_ADDRESS", "http://localhost:" + backendServerPort);
return pb.directory(frontendGenDirectory).start();


