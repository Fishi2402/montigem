${tc.signature("attrName")}
${defineHookPoint("Setter:Before")}
self.${attrName}.add(index_in_list, ${attrName})
${defineHookPoint("Setter:After")}
