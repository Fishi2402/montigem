<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type")}
${cd4c.method("public String enumToCapitalizedString(String value)")}
if (value == null) return value;
return de.se_rwth.commons.StringTransformations.capitalize(value);
