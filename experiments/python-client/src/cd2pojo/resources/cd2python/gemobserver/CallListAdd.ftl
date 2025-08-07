${tc.signature("name", "obj", "param")}

for o in self._observer:
[[[indent]]]
  await o.maybeNotify${name}(${obj}=self, ${param}=${param}, index_in_list=index_in_list, scope=scope)
[[[deindent]]]
