${tc.signature("attrName")}
${defineHookPoint("Setter:Before")}
self.${attrName}.set_gemid(${attrName})
${defineHookPoint("Setter:After")}
