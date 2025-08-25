${signature("originalCd", "decoratedCd", "astWithStereo", "returnType", "params")}
${cd4c.method("public ${cdPrinter.printType(returnType)} get${astWithStereo.name[1..]?cap_first}List()")}
return List.of("Hello", "World");
