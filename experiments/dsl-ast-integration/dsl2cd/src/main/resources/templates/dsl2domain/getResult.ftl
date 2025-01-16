${tc.signature()}
${cd4c.method("public <T> T getResult()")}
if (stack.size() != 1)
  throw new IllegalStateException("Unhappy stack size " + stack.size());

return (T) stack.pop();
