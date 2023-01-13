${tc.signature("className")}
${cd4c.method("public void notify${className}Deleted(long gemId)")}
  ${className?uncap_first}DAO.delete${className}(gemId);
