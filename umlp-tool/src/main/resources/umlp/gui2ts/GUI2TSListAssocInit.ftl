<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "getterExpression", "ownerGetterExpression", "keys", "initList")}
<#assign paramString = genHelper.params2String(keys)>
<#assign tmpVar = "tmp_$">

${cd4c.method("public void init${name?cap_first}(${paramString})")}
if (!${ownerGetterExpression}.containsObserver(this)) {
  ${ownerGetterExpression}.addObserver(this);
}
${getterExpression}.then(${tmpVar} => {
  ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
});