${signature("decoratedAST", "glex", "hwPath", "cdGenerator", "schemaGenerator", "beDecorator", "commandDecorator", "requestDecorator", "topDecorator")}
${schemaGenerator.addClassToSQLTypeMapping("java.time.Instant", "TIMESTAMP WITH TIME ZONE")}
${schemaGenerator.addClassToSQLTypeMapping("java.time.Duration", "BIGINT")}
${schemaGenerator.addClassToSQLTypeMapping("java.net.InetAddress", "VARCHAR")}