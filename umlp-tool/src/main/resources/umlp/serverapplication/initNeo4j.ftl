${tc.signature("cdName")}
${cd4c.method("public void init() throws IOException")}

Log.enableFailQuick(false);
Log.warn("I AM ALIVE!");

DatabaseAccessManager4Neo4j.init();
${cdName}ManagerImpl.init();
