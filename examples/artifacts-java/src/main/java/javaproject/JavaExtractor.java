/* (c) https://github.com/MontiCore/monticore */

package javaproject;

import baseartifactstest.Artifact;
import baseartifactstest.BaseArtifactsTestManager;
import baseartifactstest.BaseArtifactsTestManagerImpl;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import de.se_rwth.artifacts.static_extractors.ContainerFileExtractor;
import de.se_rwth.artifacts.static_extractors.commons.BaseExtractor;
import de.se_rwth.artifacts.static_extractors.commons.ExtractorUtils;
import de.se_rwth.artifacts.static_extractors.commons.RootAwareFile;
import de.se_rwth.commons.logging.Log;
import javaproject.JavaLinesOfCodeMetric;
import javaproject.JavaPackage;
import javaproject.JavaProjectManager;
import javaproject.JavaProjectManagerImpl;
import javaproject.JavaView;
import javaproject.JavaViewBuilder;
import umlp.backendrte.common.GemIdentifiable;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class JavaExtractor extends BaseExtractor {

  @Override
  public Set<GemIdentifiable> doExtract(Map<String, Collection<RootAwareFile>> filetype2Files) {
    ContainerFileExtractor baseExtractor = new ContainerFileExtractor();

    Set<GemIdentifiable> extractedObjects = new HashSet<>();
    extractedObjects.addAll(baseExtractor.doExtract(filetype2Files));

    if(filetype2Files.containsKey(".java")) {
      Collection<RootAwareFile> allJavaFiles = filetype2Files.get(".java");
      extractedObjects.addAll(extractJavaArtifactData(allJavaFiles));
    }

    return extractedObjects;
  }

  protected Collection<GemIdentifiable> extractJavaArtifactData(Collection<RootAwareFile> javaFiles) {
    Collection<GemIdentifiable> artifactData = new HashSet<>();
    for (RootAwareFile javaFile : javaFiles) {
      artifactData.addAll(extractJavaFile(javaFile));
    }

    return artifactData;
  }


  /**
   * Process a single java file
   * @param javafile the java file which is under inspection for extraction
   * @return extraction results of this java source file
   */
  private Collection<GemIdentifiable> extractJavaFile(RootAwareFile javafile) {
    Collection<GemIdentifiable> runtimes = new ArrayList<>();
    Artifact artifact =
        BaseArtifactsTestManager.getArtifactList().stream().filter(a ->
            (a.getRoot().getFullName()+"/"+a.getFullName()).equals(ExtractorUtils.transformWindowsPathToLinuxStyle(javafile.getAbsolutePath()))).findFirst().get();
    CompilationUnit cu = null;
    try {
      cu = StaticJavaParser.parse(javafile);
    }
    catch (com.github.javaparser.ParseProblemException parseProblemException) {
      Log.warn("Could not parse javafile : " + javafile.getAbsolutePath());
      return runtimes;
    } catch (FileNotFoundException e) {
      Log.warn("Could not find javafile : " + javafile.getAbsolutePath());
      return runtimes;
    }

    //Types
    List<ClassOrInterfaceDeclaration> classesOrInterfaces = new ArrayList<>(
        cu.findAll(ClassOrInterfaceDeclaration.class));

    JavaViewBuilder javaViewBuilder = JavaProjectManager.javaViewBuilder();
    javaViewBuilder.artifact(artifact);

    JavaView javaView = javaViewBuilder.build().get();

    runtimes.add(javaViewBuilder);

    //Imports
    NodeList<ImportDeclaration> imports = cu.getImports();
    //Package Name
    String packageName = "None";
    if (cu.getPackageDeclaration().isPresent()) {
      packageName = cu.getPackageDeclaration().get().getName().asString();
      Optional<JavaPackage> jp = findAlredayBuildJavaPackages(packageName);
      if (jp.isEmpty()) {
        jp = JavaProjectManager.javaPackageBuilder().name(cu.getPackageDeclaration().get().getNameAsString()).build();
      }
      jp.get().addJavaView(javaView);
      runtimes.add(jp.get());
    }

    JavaLinesOfCodeMetric locMetric = JavaProjectManager.javaLinesOfCodeMetricBuilder().loc(0).build().get();
    runtimes.add(locMetric);
    runtimes.add(javaView);
    return runtimes;
  }

  Optional<JavaPackage> findAlredayBuildJavaPackages(String packageName) {
    return JavaProjectManager.getJavaPackageList().stream().filter(
        javaPackage -> javaPackage.getName().equals(packageName)
    ).findFirst();
  }
}
