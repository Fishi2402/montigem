<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "returnType", "replaced", "args")}
${cd4c.method("public ${returnType} ${methodName}(${args})")}
    return <#if args.size() == 0>this.</#if>${replaced};