<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "getter", "setter")}
<#assign arg = "val">
<#assign type = cdPrinter.printType(mcType)>

public get ${name}(): ${type} {
  return this.${getter}();
}

@Input()
public set ${name}(${arg}: ${type}) {
  this.${setter}(${arg});
}