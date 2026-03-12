${tc.signature("attrName")}
${defineHookPoint("Setter:Before")}
self.${attrName}.insert(index_in_list, ${attrName})
${defineHookPoint("Setter:After")}
