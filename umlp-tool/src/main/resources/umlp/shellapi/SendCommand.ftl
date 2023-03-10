<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("protected final static String sendCommand(Command command)")}
  if (url == null) {
    Log.warn("Command cannot be sent: URL is not set. Set the url first with 'set-url {url}'");
    return "";
  }
  String json = CommandObjectMapper.serializeCommand(command);
  Log.info("Send command to server: " + json, "UMLP API");
  WebClient client = WebClient.create(String.format("%s:%s/%s/",url, port, applicationName));
  WebClient.ResponseSpec retrieve = client.post().uri(apiName).bodyValue(json)
    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    .accept(MediaType.APPLICATION_JSON)
    .retrieve();
  String result = retrieve.bodyToMono(String.class).block();
  Log.info("Resceived response: " + result, "UMLP API");
  return result;