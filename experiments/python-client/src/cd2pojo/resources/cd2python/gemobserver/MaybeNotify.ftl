${tc.signature("name", "args")}

if scope.contains(self.getScope()):
[[[indent]]]
  await self.${name}(${args?join(", ")})
[[[deindent]]]
