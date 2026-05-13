<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "portConfigClassName", "testConfigClassName")}

@SpringBootTest(classes = ${cdName}ServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "umlp.e2etest=true")
@Import(value = {
  ${portConfigClassName}.class, // choose random port for backend server
  ${testConfigClassName}.class} // clean database before each test case
)
@jsweet.lang.Erased
