<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name","className","typeParameter")}
${cd4c.method("public void notifySet${name?cap_first}(${className} ${className?uncap_first}, ${typeParameter} ${name?uncap_first})")}

Session session = DatabaseAccessManager.getSession();
CriteriaBuilder builder = session.getCriteriaBuilder();
CriteriaUpdate<${className}> update = builder.createCriteriaUpdate(${className}.class);
    Root<${className}> root = update.from(${className}.class);
        update.set(root.get("${name}"),${name});
        update.where(builder.equal(root.get("gemId"), ${className?uncap_first}.getGemId()));
        session.createQuery(update).executeUpdate();