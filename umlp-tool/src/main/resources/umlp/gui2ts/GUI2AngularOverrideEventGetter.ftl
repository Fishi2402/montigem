<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "getterName")}
<#assign tsType = cdPrinter.printType(type)>
<#assign tmpVar = "tmp_$">

public override ${getterName}(): ${tsType} {
  return ${tmpVar} => {
    super.${getterName}();
    this.${name}.emit(${tmpVar});
  }
}