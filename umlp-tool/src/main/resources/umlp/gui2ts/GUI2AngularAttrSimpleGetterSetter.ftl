<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "attr")}
<#assign tsType = cdPrinter.printType(type)>
<#assign arg = "val_$">

public get ${name}(): ${tsType} {
  return this.${attr};
}

@Input()
public set ${name}(${arg}: ${tsType}) {
  this.${attr} = ${arg};
}
