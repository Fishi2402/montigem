<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("nameAlias", "pageName")}

${cd4c.method("public Consumer<Void> ${nameAlias}()")}
return $ -> {
	AppRouter.navigateTo${pageName}();
};
