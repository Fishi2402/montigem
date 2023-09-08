<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type")}
${cd4c.method("public String enumToCapitalizedString(String value)")}
if (value == null) return value;
return org.gradle.internal.impldep.org.codehaus.plexus.util.StringUtils.capitalise(value);
