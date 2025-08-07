${tc.signature("attrName")}
${defineHookPoint("Setter:Before")}
self.${attrName} = ${attrName}
${defineHookPoint("Setter:After")}
