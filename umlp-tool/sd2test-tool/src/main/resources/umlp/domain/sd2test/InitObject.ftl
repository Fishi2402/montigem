<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "matchingStrategy", "processor")}

${name} = create${name?cap_first}();
${processor}.declare(${name}, ${matchingStrategy});
