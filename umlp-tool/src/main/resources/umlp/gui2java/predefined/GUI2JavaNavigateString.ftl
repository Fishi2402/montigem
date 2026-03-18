<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("nameAlias", "routingURL")}

${cd4c.method("public Consumer<Void> ${nameAlias}(String routingUrl)")}
return $ -> {
	Router.navigate(routingUrl);
};
