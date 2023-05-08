${tc.signature("className")}
${cd4c.method("public void notify${className}Deleted(Long gemId)")}
  ${className?uncap_first}DAO.delete${className}(gemId);
