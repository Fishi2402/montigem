<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "getter", "setter", "isRTELib")}
<#assign tsType = cdPrinter.printAsyncType(type)>
<#assign arg = "val_$">

<#--Angular Getters were removed since they were never used in JSweet (gui2java2ts) -->

<#-- This is mostly never used and should be removed to avoid confusion -->
public get ${name}(): ${tsType} {
  return this.${getter}();
}

<#if tsType?starts_with("IUMLPList") || tsType?starts_with("Array")>
  <#assign typeArg = tsType?keep_after("<")?keep_before_last(">")>
  <#assign splitType = "IUMLPList<${typeArg}> | Array<${typeArg}>">
<#else>
  <#assign splitType = tsType>
</#if>


@Input()
public set ${name}(${arg}: ${splitType}) {
  <#if tsType?starts_with("IUMLPList") && !isRTELib>
    if(Array.isArray(val_$)) {
      new AsyncList<${typeArg}>().__AsyncListConstructor(${arg}).then(arr => this.${setter}(arr));
    } else {
      // Pass IUMLPList instances through to next component
      this.${setter}(${arg});
    }
  <#elseif tsType?starts_with("Array") && isRTELib>
    GemListUtil.toArraySync(${arg}).then(list => this.${setter}(list));
  <#else>
    this.${setter}(${arg});
  </#if>
}
