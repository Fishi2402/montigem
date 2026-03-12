<#-- (c) https://github.com/MontiCore/monticore -->
${signature("statement","columnName","enumType")}
${statement}.get(${columnName}) != null ? ${enumType}.valueOf(${statement}.get(${columnName}).asString()) : null
