<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("public static BeanPostProcessor addAfterDbConnectionTask()")}

return new BeanPostProcessor() {
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) {
    // Only way to hook into the correct lifecycle phase of Spring
    if (bean instanceof ${cdName}ServerApplication) {
      ((${cdName}ServerApplication) bean).addAfterDbConnectionTask(
          () -> DatabaseAccessManager.executeStatement("DROP ALL OBJECTS"));
    }
    return bean;
  }
};
