<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public Promise<GemList<${name}>> _doGet${name}List()")}
<#--This method is providing compability-->
return this._doGet${name}().then(inst => {
     let a: number[] = [inst.getGemId()];
     let list: GemListBuilder<${name}>  = new ${name}GemList(a);
     return list.build();
});
