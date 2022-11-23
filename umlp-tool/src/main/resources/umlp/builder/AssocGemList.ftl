<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "builder")}
${cd4c.method("public ${builder} ${name}GemListIds(List<Long> ids)")}

this.${name}Present = true;
this.${name}._internal_add(this.${name}.size(), ids);
return this;

