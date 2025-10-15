${signature("decoratedAST", "glex", "hwPath", "cdGenerator", "schemaGenerator", "beDecorator", "commandDecorator", "requestDecorator", "topDecorator")}
${schemaGenerator.addClassToSQLTypeMapping("java.time.Duration", "BIGINT")}
${schemaGenerator.addClassToSQLTypeMapping("java.net.URI", "VARCHAR")}
${beDecorator.addSupportedDeSer("java.time.Duration", "java.net.URI")}
${commandDecorator.addSupportedDeSer("java.time.Duration", "java.net.URI")}
