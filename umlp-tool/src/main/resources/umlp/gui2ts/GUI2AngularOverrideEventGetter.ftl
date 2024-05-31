<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "getterName")}
<#assign tsType = cdPrinter.printType(type)>

public override ${getterName}(): ${tsType} {
  const res = super.${getterName}();
  this.${name}.emit(null);
  return res;
}