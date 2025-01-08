/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.util;

import standalone.com.sun.tools.javac.code.Flags;
import standalone.com.sun.tools.javac.code.Symbol;
import standalone.com.sun.tools.javac.code.Type;
import standalone.com.sun.tools.javac.code.TypeTag;
import standalone.com.sun.tools.javac.tree.JCTree;
import standalone.com.sun.tools.javac.tree.TreeMaker;
import standalone.com.sun.tools.javac.util.Context;
import standalone.com.sun.tools.javac.util.List;
import standalone.com.sun.tools.javac.util.Name;
import standalone.com.sun.tools.javac.util.Names;

import javax.tools.JavaFileManager;
import javax.tools.ToolProvider;

public class SunTypesHelper {

  protected TreeMaker treeMaker;
  protected Names sunNames;
  protected standalone.com.sun.tools.javac.code.Symbol.ClassSymbol behaviorSubject;

  public SunTypesHelper() {
    Context sunContext = new Context();
    sunContext.put(JavaFileManager.class, ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null));
    this.treeMaker = TreeMaker.instance(sunContext);
    this.sunNames = Names.instance(sunContext);
    Name sN = sunNames.fromString("String");
    this.behaviorSubject = new Symbol.ClassSymbol(Flags.PUBLIC, sN,
        null);
    this.behaviorSubject.type = new Type.ClassType(Type.noType, standalone.com.sun.tools.javac.util.List.nil(),
        new Symbol.ClassSymbol(1073741825, sN, new Symbol.PackageSymbol(sunNames.fromString("java.util"), null)));
    this.behaviorSubject.type.tsym = this.behaviorSubject;
    this.behaviorSubject.members_field = standalone.com.sun.tools.javac.code.Scope.WriteableScope.create(this.behaviorSubject);
  }

  public standalone.com.sun.tools.javac.tree.JCTree.JCVariableDecl getNewParameter(String variableName) {
    standalone.com.sun.tools.javac.util.List<JCTree.JCExpression> type = standalone.com.sun.tools.javac.util.List.nil();
    standalone.com.sun.tools.javac.tree.JCTree.JCTypeApply behSub = treeMaker.TypeApply(treeMaker.Ident(sunNames.fromString("rxjs.BehaviorSubject")), type);
    behSub.type = this.behaviorSubject.type;
    behSub.clazz.type = behSub.type;
    behSub.clazz.pos = 42;
    behSub.pos = 42;

    JCTree.JCNewClass init = treeMaker.NewClass(null,
        standalone.com.sun.tools.javac.util.List.nil(),  // type arguments
        behSub,
        standalone.com.sun.tools.javac.util.List.of(treeMaker.Literal(TypeTag.BOT, null)), // (null) is the initial value
        null
    );
    init.args.get(0).pos = 42;
    init.args.get(0).type = Type.noType;
    init.type = new Type.MethodType(standalone.com.sun.tools.javac.util.List.of(Type.noType),
        behaviorSubject.type,
        standalone.com.sun.tools.javac.util.List.nil(),
        behaviorSubject.type.tsym);
    init.constructor = new Symbol.MethodSymbol(Flags.MethodFlags | Flags.PUBLIC, behaviorSubject.name, init.type, behaviorSubject);
    init.setPos(42);

    // Content is irrelevant. Is identified via name and printed in GUIComponentAdapter
    Name name = sunNames.fromString(variableName);
    JCTree.JCVariableDecl jcVariableDecl = treeMaker.VarDef(
        treeMaker.Modifiers(Flags.PUBLIC, List.nil()),
        name,
        behSub,
        init
    );

    jcVariableDecl.setPos(42);
    jcVariableDecl.startPos = 42;
    jcVariableDecl.type = this.behaviorSubject.type;
    if (jcVariableDecl.type == null || jcVariableDecl.type.tsym == null)
      throw new RuntimeException("type must not be null");
    jcVariableDecl.sym = new Symbol.VarSymbol(Flags.PUBLIC | Flags.HASINIT, name, this.behaviorSubject.type,
        null);

    return jcVariableDecl;
  }

  public standalone.com.sun.tools.javac.tree.JCTree.JCLiteral getNewLiteral(String name) {
    JCTree.JCLiteral result = treeMaker.Literal(TypeTag.CLASS, name);
    result.pos = 42;
    result.type = this.behaviorSubject.type;
    return result;
  }

  public standalone.com.sun.tools.javac.tree.JCTree.JCIdent getNewIdent(String name) {
    Name sunName = sunNames.fromString(name);
    JCTree.JCIdent result = treeMaker.Ident(sunName);
    Name sN = sunNames.fromString("String");
    this.behaviorSubject = new Symbol.ClassSymbol(Flags.PUBLIC, sN,
        null);
    Type.ClassType type = new Type.ClassType(Type.noType, List.nil(),
        new Symbol.ClassSymbol(1073741825, sN,
            new Symbol.PackageSymbol(sunNames.fromString("java.util"), null)));
    result.sym = new Symbol.VarSymbol(Flags.PRIVATE, sunName, type, null);
    result.pos = 42;
    result.type = this.behaviorSubject.type;
    return result;
  }
}
