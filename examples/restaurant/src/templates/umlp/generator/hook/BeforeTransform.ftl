${signature("decoratedAST", "glex", "hwPath", "cdGenerator", "schemaGenerator", "beDecorator", "commandDecorator", "requestDecorator", "topDecorator")}
${schemaGenerator.addClassToSQLTypeMapping("java.time.LocalDate", "DATE")}
${beDecorator.addSupportedDeSer("java.time.LocalDate")}
${commandDecorator.addSupportedDeSer("java.time.LocalDate")}