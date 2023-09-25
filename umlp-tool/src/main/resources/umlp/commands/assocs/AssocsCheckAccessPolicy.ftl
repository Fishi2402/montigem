<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type1", "type2")}
${cd4c.method("public boolean checkAccessPolicy()")}

var ap1 = new ${type1}AccessPolicy();
var ap2 = new ${type2}AccessPolicy();
return ap1.checkPermission(this) && ap2.checkPermission(this);
