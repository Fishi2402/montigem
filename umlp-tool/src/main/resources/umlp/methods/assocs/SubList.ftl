<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public List<${targetName}> subList${name?cap_first}(int startIdxIncl, int endIdxExcl)")}
return Collections.unmodifiableList(${name}).subList(startIdxIncl, endIdxExcl);
