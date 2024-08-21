<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "getter", "setter", "replaceByUMLPArrayList")}
<#assign tsType = cdPrinter.printType(type)>
<#assign arg = "val_$">

public get ${name}(): ${tsType} {
  return this.${getter}()<#if replaceByUMLPArrayList>.toArrayDeep()</#if>;
}

@Input()
public set ${name}(${arg}: ${tsType}) {
  this.${setter}(<#if replaceByUMLPArrayList>new UMLPArrayList(</#if>${arg}<#if replaceByUMLPArrayList>).transformInternalArrays()</#if>);
}
