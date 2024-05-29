<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "builder")}
${cd4c.method("public ${builder} ${name}GemListIds(List<Long> ids)")}

this._${name}._internal_add(this._${name}.size(), ids);
return this.realBuilder;

