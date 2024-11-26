${signature("attrName")}
${cd4c.method("public boolean isPresent${attrName?cap_first}()")}

return this.get${attrName?cap_first}Gem().getGemId() > 0;
