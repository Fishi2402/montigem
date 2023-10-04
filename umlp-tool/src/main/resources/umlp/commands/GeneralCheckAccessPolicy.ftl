<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type")}
${cd4c.method("public boolean checkAccessPolicy()")}

var ap = new ${type}AccessPolicy();
return ap.checkPermission(this);
