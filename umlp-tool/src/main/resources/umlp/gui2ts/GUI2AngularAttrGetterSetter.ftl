<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "getter", "setter")}
<#assign tsType = cdPrinter.printType(type)>
<#assign arg = "val">

public get ${name}(): ${tsType} {
  return this.${getter}();
}

@Input()
public set ${name}(${arg}: ${tsType}) {
  this.${setter}(${arg});
}