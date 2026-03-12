<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName","typeName", "attributes")}
${cd4c.method("public void ensureSelectAll${typeName}IdsCreated()")}
selectAllIds${typeName} = "SELECT id FROM ${tableName};";

