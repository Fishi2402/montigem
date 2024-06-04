<#-- (c) https://github.com/MontiCore/monticore -->
${signature("statement","columnName","enumType")}
${statement}.getString(${columnName}) != null ? ${enumType}.valueOf(${statement}.getString(${columnName})) : null
