${tc.signature("astnode", "nodeDomainName", "domain", "body")}
${cd4c.method("public void handle(${astnode} node)")}
int pre_size = stack.size();

var nodeBuilder = ${domain}Manager.${nodeDomainName?uncap_first}Builder();
stack.push(nodeBuilder);

${body}

if (pre_size + 1 != stack.size()) {
  throw new IllegalStateException("Unexpected stack size: " + pre_size + " / " + stack.size());
}
nodeBuilder.build();
