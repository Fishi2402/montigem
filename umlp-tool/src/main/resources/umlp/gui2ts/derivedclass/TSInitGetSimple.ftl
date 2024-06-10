<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("clazz")}

this.calc${ast.name?cap_first}().then(res => {
  this.${ast.name} = res;
});