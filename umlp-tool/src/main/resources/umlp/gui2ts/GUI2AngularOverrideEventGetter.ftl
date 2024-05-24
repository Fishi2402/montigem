<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "getterName")}

public override ${getterName}(): ${type} {
  const res = super.${getterName}();
  this.${name}.emit(null);
  return res;
}