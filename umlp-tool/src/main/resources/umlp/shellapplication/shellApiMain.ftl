${tc.signature("cdName")}
${cd4c.method("public static void main(String[] args)")}

Log.init();
Log.info("Status: RUNNING", "UMLP API");

${cdName}Manager.initMe(new ${cdName}ManagerClientImpl());
${cdName}CommandObjectMapper.init();

new SpringApplicationBuilder(${cdName}ShellAPIApplication.class)
.web(WebApplicationType.NONE)
.run(args);
Log.info("Status: FINISHED", "UMLP API");
