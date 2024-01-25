<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
<#assign router="router">
public constructor(${router}: Router) {
  super();
  super.${router} = ${router};
}
