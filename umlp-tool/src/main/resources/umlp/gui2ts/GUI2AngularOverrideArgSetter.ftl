<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "params", "trigger")}
<#assign arg = "conv_val_$">
<#assign paramString = genHelper.tsParams2String(type, arg, params)>
<#assign argString = genHelper.params2ArgsString(arg, params)>

public override set${name?cap_first}(${paramString}): void {
  super.set${name?cap_first}(${argString});
  this.${trigger}.emit(this.convert${name?cap_first}(${arg}));
}
