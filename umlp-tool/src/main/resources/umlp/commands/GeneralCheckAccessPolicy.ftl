<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "command")}
${cd4c.method("public boolean checkAccessPolicy()")}

var ap = new ${type}AccessPolicy();
return ap.check${command}Permission(this);
