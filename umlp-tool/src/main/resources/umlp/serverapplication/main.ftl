<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("public static void main(String[] args) throws IOException")}

  Log.enableFailQuick(false);
  Log.warn("I AM ALIVE!");
  ${cdName}ManagerImpl.init();

Optional<URL> optionalURL = Optional.ofNullable(${cdName}ServerApplication.class.getResource("/${cdName}.sql"));
  if(optionalURL.isEmpty()) {
  Log.error("0xAAAAAA Could not find schema for database.");
  }
  String schema = FileReaderWriter.readFromFile(optionalURL.get());
  Log.warn("Loading schema: " + schema);
  DatabaseAccessManager.initSchema(schema);
  SpringApplication.run(${cdName}ServerApplication.class, args);

