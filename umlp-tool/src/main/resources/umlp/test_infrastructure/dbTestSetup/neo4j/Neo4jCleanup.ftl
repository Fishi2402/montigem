${tc.signature("cdName")}
${cd4c.method("public void _cleanup() throws Exception")}
DatabaseAccessManager4Neo4j.executeStatement("MATCH(n) DETACH DELETE n");
DatabaseAccessManager4Neo4j.closeConnection();
DatabaseAccessManager4Neo4j.clearLocalGemIds();
