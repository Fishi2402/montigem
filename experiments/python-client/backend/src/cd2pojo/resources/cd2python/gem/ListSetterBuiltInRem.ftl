${tc.signature("attrName")}
${defineHookPoint("Setter:Before")}
${attrName} = self.${attrName}.pop(index_in_list)
${defineHookPoint("Setter:After")}
