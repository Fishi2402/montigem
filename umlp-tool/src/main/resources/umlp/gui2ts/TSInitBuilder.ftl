<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("builder", "type", "manager")}
${manager}.${type}Builder().then(gemPromiseRes => {
  this.${builder} = gemPromiseRes;
  ${tc.include("umlp.gui2java.InitBuilderBody")}
});