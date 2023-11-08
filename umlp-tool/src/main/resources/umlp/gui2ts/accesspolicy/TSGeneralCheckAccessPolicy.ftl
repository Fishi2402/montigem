<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "command")}
${cd4c.method("public boolean checkAccessPolicy()")}

let ap: ${type}AccessPolicy = new ${type}AccessPolicy();
return ap.check${command}Permission(this);
