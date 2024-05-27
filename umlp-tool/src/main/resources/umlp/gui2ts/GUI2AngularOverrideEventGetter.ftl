<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "getterName")}
<#assign type = cdPrinter.printType(mcType)>

public override ${getterName}(): ${type} {
  const res = super.${getterName}();
  this.${name}.emit(null);
  return res;
}