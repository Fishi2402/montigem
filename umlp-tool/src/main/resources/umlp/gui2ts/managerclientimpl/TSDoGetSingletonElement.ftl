<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public Promise<${name}> _doGet${name}()")}
if(this.${name?uncap_first}Instance == null){
  this.${name?uncap_first}Instance =
    CommandManager.executeCommand(new ${name}_get(-1)).then(v => {
      if (v.isStatusResult()) {
        return null;
      }
      let res = v.asSimpleResult();
      if (res.isSuccessful()) {
        let ${name?uncap_first}: ${name} = res.get();
        if (!${name?uncap_first}.containsObserver(this.the${name}CommandObserver)) {
          ${name?uncap_first}.addObserver(this.the${name}CommandObserver);
        }
        console.debug("Setting instance of ${name?uncap_first} ", LogUtil.getLoggerName());
        return ${name?uncap_first};
      }
      return null;
    });
}
return this.${name?uncap_first}Instance;
