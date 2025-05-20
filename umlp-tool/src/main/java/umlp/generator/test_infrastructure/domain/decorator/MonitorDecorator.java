package umlp.generator.test_infrastructure.domain.decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cd4codebasis._ast.ASTCDParameterTOP;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.ManagerImplDecorator;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MonitorDecorator extends AbstractDecorator {

  public static final String TEST_INFRASTRUCTURE_PACKAGE = "test_infrastructure";
  public static final String MONITOR_SUFFIX = "Monitor";

  public enum Stereotype {
    /**
     * Implies that all overridable methods need to be overridden with monitor methods
     */
    NEEDS_MONITOR("needs-monitor"),
    /**
     * Implies that _doFooBuilderFromId methods should return a FooBuilderMonitor instead of a
     * FooBuilder. In conjunction with {@link #NEEDS_MONITOR} this method will also be monitored.
     */
    USE_MONITOR_BUILDERS("use-monitor-builders"),
    /**
     * Implies that the class is a manager and according static methods should be generated
     */
    MANAGER("manager");

    /**
     * Get a {@link Collection} of all {@link Stereotype}s present on an {@link ASTCDClass}.
     *
     * @param astcdClass An {@link ASTCDClass}
     * @return All present {@link Stereotype}s
     */
    public static Collection<Stereotype> getStereotypesOf(ASTCDClass astcdClass) {
      return Arrays
          .stream(values())
          .filter(stereotype -> stereotype.isPresentOn(astcdClass))
          .collect(Collectors.toList());
    }

    /**
     * Checks whether the {@link ASTCDClass} has any defines {@link Stereotype}s
     *
     * @param astcdClass An {@link ASTCDClass}
     * @return Whether any {@link Stereotype}s are present
     */
    public static boolean hasStereotypes(ASTCDClass astcdClass) {
      return !getStereotypesOf(astcdClass).isEmpty();
    }

    private final String name;

    Stereotype(String name) {
      this.name = name;
    }

    /**
     * Add this stereotype to the provided {@link ASTCDClass}.
     *
     * @param astcdClass An {@link ASTCDClass} to add the stereotype to
     */
    public void addTo(ASTCDClass astcdClass) {
      var modifier = astcdClass.getModifier();
      var stereotypeValue = UMLPMill.stereoValueBuilder().setName(name).build();

      if (modifier.isPresentStereotype()) {
        modifier.getStereotype().addValues(stereotypeValue);
      } else {
        var stereotype = UMLPMill.stereotypeBuilder().addValues(stereotypeValue).build();
        modifier.setStereotype(stereotype);
      }
    }

    public boolean isPresentOn(ASTCDClass astcdClass) {
      var modifier = astcdClass.getModifier();
      return modifier.isPresentStereotype() && modifier.getStereotype().contains(name);
    }

    @Override
    public String toString() {
      return name;
    }
  }

  private final ASTCDCompilationUnit redecoratedCD;

  public MonitorDecorator(GlobalExtensionManagement glex, ASTCDCompilationUnit redecoratedCD) {
    super(glex);
    this.redecoratedCD = redecoratedCD;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    var targetPackage = getPackage(origCD, decoratedCD, TEST_INFRASTRUCTURE_PACKAGE);

    redecoratedCD
        .getCDDefinition()
        .getCDClassesList()
        .stream()
        .filter(Stereotype::hasStereotypes)
        .forEach(astcdClass -> targetPackage.addCDElement(withOrigin(createMonitor(astcdClass,
                                                                                   origCD,
                                                                                   redecoratedCD))));
  }

  private ASTCDElement createMonitor(ASTCDClass decoratedBuilderClass,
      ASTCDCompilationUnit origCd,
      ASTCDCompilationUnit redecoratedCD) {
    var builderName = decoratedBuilderClass.getName();
    var packageName = getPackageName(origCd, DEFAULT_PACKAGE);
    var monitorClass = UMLPMill
        .cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setName(builderName + MONITOR_SUFFIX)
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(builderName))
        .build();

    var cd4C = getCD4C();

    //constructors
    addSuperConstructors(monitorClass, decoratedBuilderClass);

    var methods = new ArrayList<MethodToAdd>();

    var monitorMethods = Stereotype.NEEDS_MONITOR.isPresentOn(decoratedBuilderClass);
    var returnBuilderMonitors = Stereotype.USE_MONITOR_BUILDERS.isPresentOn(decoratedBuilderClass);
    var generateStaticManagerMethods = Stereotype.MANAGER.isPresentOn(decoratedBuilderClass);

    var overridableMethods = getOverridableMethods(decoratedBuilderClass, redecoratedCD);

    if (monitorMethods) {
      //monitor all the generated methods
      methods.addAll(overridableMethods
                         .stream()
                         .map(MethodToAdd::monitor)
                         .collect(Collectors.toList()));
    }
    if (returnBuilderMonitors) {
      var domainClasses = ManagerImplDecorator
          .classesOfProvidedBuilders(origCd)
          .collect(Collectors.toList());

      //add _doFooBuilderMonitorFromId methods
      methods.addAll(domainClasses
                         .stream()
                         .map(MethodToAdd::builderFromId)
                         .collect(Collectors.toList()));
      if (monitorMethods) {
        //for every domain class find a monitor method with the name _doFooBuilderFromId and
        // point the actual implementation to _doFooBuilderMonitorFromId
        domainClasses.forEach(domainClass -> {
          var domainClassName = domainClass.getName();
          var nameOfMethodToOverride = "_do" + domainClassName + "BuilderFromId";
          var actualCall = "_do" + domainClassName + "BuilderMonitorFromId(gemId)";
          methods
              .stream()
              .filter(method -> method instanceof MethodToAdd.Monitor)
              .map(MethodToAdd.Monitor.class::cast)
              .filter(monitor -> monitor.methodToMonitor().getName().equals(nameOfMethodToOverride))
              .findAny()
              .orElseThrow()
              .withActualCall(actualCall);
        });
      } else {
        //just add overrides which point _doFooBuilderFromId to _doFooBuilderMonitorFromId
        // for every domain class
        methods.addAll(domainClasses
                           .stream()
                           .map(MethodToAdd::delegateBuilderFromId)
                           .collect(Collectors.toList()));
      }
    }
    if (generateStaticManagerMethods) {
      methods.add(MethodToAdd.managerMethods(monitorClass));
    }

    //override methods
    methods.forEach(method -> method.addTo(cd4C, monitorClass));

    //imports
    cd4C.addImport(monitorClass, Joiners.DOT.join(packageName, "*"));
    cd4C.addImport(monitorClass, Joiners.DOT.join(PathUtil.getBackendRTECommonPath(), "Gem"));
    cd4C.addImport(monitorClass, Joiners.DOT.join(PathUtil.getBackendRTECommonPath(), "GemList"));
    cd4C.addImport(monitorClass,
                   Joiners.DOT.join(PathUtil.getBackendRTECommonPath(), "NotificationScope"));
    methods
        .stream()
        .flatMap(method -> method.requiredImports().stream())
        .collect(Collectors.toSet())
        .forEach(importSignature -> cd4C.addImport(monitorClass, importSignature));

    return monitorClass;
  }

  private Collection<ASTCDMethod> getOverridableMethods(ASTCDClass classToMonitor,
      ASTCDCompilationUnit decoratedCD) {
    return getOverridableSignatureComparedMethods(classToMonitor, decoratedCD)
        .stream()
        .map(SignatureComparedMethod::method)
        .collect(Collectors.toList()); //toList is acceptable here since duplicates should be
    // filtered out earlier on
  }

  private Collection<SignatureComparedMethod> getOverridableSignatureComparedMethods(ASTCDClass classToMonitor,
      ASTCDCompilationUnit decoratedCD) {
    var methods = classToMonitor.getCDMethodList().stream().filter(method -> {
      var modifier = method.getModifier();
      return !modifier.isStatic() && !modifier.isFinal() && !modifier.isPrivate();
    }).map(SignatureComparedMethod::new).collect(Collectors.toCollection(HashSet::new));

    //super class
    if (classToMonitor.isPresentCDExtendUsage()) {
      //use signatures for comparison, since return type Option<? extends T> and Option<T>
      // are not the same but may refer to the same method
      methods.addAll(classToMonitor
                         .getCDExtendUsage()
                         .getSuperclassList()
                         .stream()
                         .filter(type -> type instanceof ASTMCQualifiedType)
                         .map(type -> (ASTMCQualifiedType) type)
                         .map(type -> type.getMCQualifiedName().getBaseName())
                         .flatMap(superClassName -> decoratedCD
                             .getCDDefinition()
                             .getCDClassesList()
                             .stream()
                             .filter(cdClass -> cdClass.getName().equals(superClassName))
                             .flatMap(superClass -> getOverridableSignatureComparedMethods(
                                 superClass,
                                 decoratedCD).stream()))
                         .collect(Collectors.toList()));
    }

    //TODO default methods of interfaces are overridable (but very uncommon in our case)

    return methods;
  }

  private interface MethodToAdd {

    @SuppressWarnings("UnusedReturnValue")
    ASTCDMethodSignature addTo(CD4C cd4c, ASTCDClass clazz);

    Collection<String> requiredImports();

    static Monitor monitor(ASTCDMethod methodToMonitor) {
      return new Monitor(methodToMonitor);
    }

    static BuilderFromId builderFromId(ASTCDClass domainClass) {
      return new BuilderFromId(domainClass);
    }

    static DelegateBuilderFromId delegateBuilderFromId(ASTCDClass domainClass) {
      return new DelegateBuilderFromId(domainClass);
    }

    static Init managerMethods(ASTCDClass managerClass) {
      return new Init(managerClass);
    }

    final class Monitor implements MethodToAdd {

      private final ASTCDMethod methodToMonitor;
      private Optional<String> actualCall = Optional.empty();

      public Monitor(ASTCDMethod methodToMonitor) {
        this.methodToMonitor = methodToMonitor;
      }

      @SuppressWarnings("UnusedReturnValue")
      public Monitor withActualCall(String actualCall) {
        this.actualCall = Optional.of(actualCall);
        return this;
      }

      public ASTCDMethod methodToMonitor() {
        return methodToMonitor;
      }

      @Override
      public ASTCDMethodSignature addTo(CD4C cd4c, ASTCDClass clazz) {
        var returnType = methodToMonitor.getMCReturnType().printType();
        var name = methodToMonitor.getName();
        var params = methodToMonitor.getCDParameterList();
        var paramSignature = params
            .stream()
            .map(param -> param.getMCType().printType() + " " + param.getName())
            .collect(Collectors.joining(", "));
        var paramCallsite = params
            .stream()
            .map(ASTCDParameterTOP::getName)
            .collect(Collectors.joining(", "));
        var isVoid = returnType.equals("void");
        var actualCall = this.actualCall.orElseGet(() -> String.format("super.%s(%s)",
                                                                       name,
                                                                       paramCallsite));

        return cd4c.addMethod(clazz,
                              "umlp.monitor.monitor-method",
                              name,
                              returnType,
                              paramSignature,
                              isVoid,
                              actualCall);
      }

      @Override
      public Collection<String> requiredImports() {
        return List.of(Joiners.DOT.join(PathUtil.getTestInfrastructurePath(),
                                        "monitor",
                                        "MethodMonitor"));
      }
    }

    final class BuilderFromId implements MethodToAdd {

      private final ASTCDClass domainClass;

      public BuilderFromId(ASTCDClass domainClass) {
        this.domainClass = domainClass;
      }

      @Override
      public ASTCDMethodSignature addTo(CD4C cd4c, ASTCDClass clazz) {
        var name = domainClass.getName();
        return cd4c.addMethod(clazz, "umlp.monitor.DoMonitorBuilderParam", name);
      }

      @Override
      public Collection<String> requiredImports() {
        return List.of();
      }
    }

    final class DelegateBuilderFromId implements MethodToAdd {

      private final ASTCDClass domainClass;

      public DelegateBuilderFromId(ASTCDClass domainClass) {
        this.domainClass = domainClass;
      }

      @Override
      public ASTCDMethodSignature addTo(CD4C cd4c, ASTCDClass clazz) {
        var name = domainClass.getName();
        return cd4c.addMethod(clazz, "umlp.monitor.DoBuilderParam", name);
      }

      @Override
      public Collection<String> requiredImports() {
        return List.of();
      }
    }

    final class Init implements MethodToAdd {

      private final ASTCDClass managerClass;

      public Init(ASTCDClass managerClass) {
        this.managerClass = managerClass;
      }

      @Override
      public ASTCDMethodSignature addTo(CD4C cd4c, ASTCDClass clazz) {
        var name = managerClass.getName();
        return cd4c.addMethod(clazz, "umlp.monitor.manager.Init", name);
      }

      @Override
      public Collection<String> requiredImports() {
        return List.of();
      }
    }
  }

  private static final class SignatureComparedMethod {

    private final ASTCDMethod method;
    private final String methodName;
    private final List<String> parameterTypeNames;

    public SignatureComparedMethod(ASTCDMethod method) {
      this.method = method;
      this.methodName = method.getName();
      this.parameterTypeNames = method
          .getCDParameterList()
          .stream()
          .map(parameter -> parameter.getMCType().printType())
          .collect(Collectors.toList());
    }

    public ASTCDMethod method() {
      return method;
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      SignatureComparedMethod that = (SignatureComparedMethod) o;

      //TODO is there a better builtin way to compare two methods?
      return Objects.equals(methodName, that.methodName) &&
             Objects.equals(parameterTypeNames, that.parameterTypeNames);
    }

    @Override
    public int hashCode() {
      return Objects.hash(methodName, parameterTypeNames);
    }
  }
}
