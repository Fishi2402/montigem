${tc.signature("name", "obj", "param")}

for o in self._observer:
[[[indent]]]
  await o.maybeNotify${name}(${obj}=self, _old_value=_old_value, ${param}=${param}, scope=scope)
[[[deindent]]]
