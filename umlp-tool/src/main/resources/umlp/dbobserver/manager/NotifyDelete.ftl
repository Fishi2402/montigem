${tc.signature("cdName","initializationTypeName")}
${cd4c.method("public void notify${initializationTypeName}Deleted(Long gemId)")}
${cdName}DAOFactory.get${initializationTypeName}DAO().delete${initializationTypeName}(gemId);
