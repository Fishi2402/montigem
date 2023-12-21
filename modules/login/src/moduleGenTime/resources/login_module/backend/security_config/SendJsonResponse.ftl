${tc.signature()}
${cd4c.method("protected void sendJSONResponse(HttpServletResponse response, Map<String, Object> data) throws IOException")}
response.setCharacterEncoding(StandardCharsets.UTF_8.name());
response.setContentType(MediaType.APPLICATION_JSON_VALUE);
PrintWriter out = response.getWriter();
new ObjectMapper().writeValue(out, data);
out.flush();
