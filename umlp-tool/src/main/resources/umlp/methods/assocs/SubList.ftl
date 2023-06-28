<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public List<${targetName}> subList${name?cap_first}(int startIdxIncl, int endIdxExcl)")}
return Collections.unmodifiableList(_internal_get${name?cap_first}GemList()).subList(startIdxIncl, endIdxExcl);
