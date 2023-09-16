<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type")}
${cd4c.method("public String enumToCapitalizedString(String value)")}
if (!value) return '';
let res = value.toString().toLowerCase();
return res.charAt(0).toUpperCase() + (res.length > 1? res.slice(1) : '');
