<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("protected void startFrontend() throws IOException")}

String npmPath = getNpmPath();
File frontendGenDirectory = getFrontendGenDirectory();
Process process = startNpmProcess(npmPath, frontendServerPort, frontendGenDirectory);
frontendProcess = Optional.of(process);
monitorFrontendStart(process);
