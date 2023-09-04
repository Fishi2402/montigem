<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainVar", "domainVarType", "manager")}
this.update${domainVarType}s();
${manager}.addObserver(this.updateObserver);
