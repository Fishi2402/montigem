<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public Iterator<${targetName}> iterator${name?cap_first}()")}
return Collections.unmodifiableList(_internal_get${name?cap_first}GemList()).listIterator();
