package coverage.components;

import coveragedashboard.CoverageSourceFile;
import mc.fenix.editor.monacoeditortypes.LineClickEvent;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

import static def.dom.Globals.setTimeout;

@Component
public class SourceMapViewer extends SourceMapViewerTOP{

  public SourceMapViewer(){
    super();
    this.setTestDisabled(true);
    this.setSourceContent(
        """
        package app;

        import umlp.guiutil.Router;

        public class AppRouter {

        public static void navigateToGuiStart()
          {
            Router.navigateTo("gui/Start");
          }

        public static void navigateToGuiTestArrayStreamAdapterTest()
          {
            Router.navigateTo("gui/test/ArrayStreamAdapterTest");
          }

        public static void navigateToGuiTestDomainStreamAdapterTest()
          {
            Router.navigateTo("gui/test/DomainStreamAdapterTest");
          }

        public static void navigateToGuiTestFutureAdapterTest()
          {
            Router.navigateTo("gui/test/FutureAdapterTest");
          }

        public static void navigateToGuiTestGlobalstateTest0()
          {
            Router.navigateTo("gui/test/globalstate/Test0");
          }

        public static void navigateToGuiTestGlobalstateTestGlobalState()
          {
            Router.navigateTo("gui/test/globalstate/TestGlobalState");
          }

        public static void navigateToGuiTestLocalDateTime()
          {
            Router.navigateTo("gui/test/LocalDateTime");
          }

        public static void navigateToGuiTestNavigateSource()
          {
            Router.navigateTo("gui/test/NavigateSource");
          }

        public static void navigateToGuiTestNavigateTargetWithId(      long val_0
        )
          {
            Router.navigateTo("gui/test/NavigateTargetWithId/"+val_0);
          }

        public static void navigateToGuiTestNavigateTargetWithIds(      long val_0,long val_1
        )
          {
            Router.navigateTo("gui/test/NavigateTargetWithIds/"+val_0+"/"+val_1);
          }

        public static void navigateToGuiTestOptionalAdapterTest()
          {
            Router.navigateTo("gui/test/OptionalAdapterTest");
          }

        public static void navigateToGuiTestStringSplit()
          {
            Router.navigateTo("gui/test/StringSplit");
          }

        public static void navigateToGuiTestTest0()
          {
            Router.navigateTo("gui/test/Test0");
          }

        public static void navigateToGuiTestTest1()
          {
            Router.navigateTo("gui/test/Test1");
          }

        public static void navigateToGuiTestTest2()
          {
            Router.navigateTo("gui/test/Test2");
          }

        public static void navigateToGuiTestTestC1(      long val_0
        )
          {
            Router.navigateTo("gui/test/TestC1/"+val_0);
          }

        public static void navigateToGuiTestTestC1List()
          {
            Router.navigateTo("gui/test/TestC1List");
          }

        public static void navigateToGuiTestTestCustomUpdate()
          {
            Router.navigateTo("gui/test/TestCustomUpdate");
          }

        public static void navigateToGuiTestTestEmptyGem(      long val_0
        )
          {
            Router.navigateTo("gui/test/TestEmptyGem/"+val_0);
          }

        public static void navigateToGuiTestTestEnum()
          {
            Router.navigateTo("gui/test/TestEnum");
          }

        public static void navigateToGuiTestTestIterate()
          {
            Router.navigateTo("gui/test/TestIterate");
          }

        public static void navigateToGuiTestTestListObserver()
          {
            Router.navigateTo("gui/test/TestListObserver");
          }

        public static void navigateToGuiTestTestLocalDateTime()
          {
            Router.navigateTo("gui/test/TestLocalDateTime");
          }

        public static void navigateToGuiTestTestMethodInvocation()
          {
            Router.navigateTo("gui/test/TestMethodInvocation");
          }

        public static void navigateToGuiTestTestNestedList()
          {
            Router.navigateTo("gui/test/TestNestedList");
          }

        public static void navigateToGuiTestTestUpdatableBoundPage()
          {
            Router.navigateTo("gui/test/TestUpdatableBoundPage");
          }

        public static void navigateToGuiTestTestUpdatablePage(      long val_0
        )
          {
            Router.navigateTo("gui/test/TestUpdatablePage/"+val_0);
          }

        public static void navigateToGuiTestTestUtilArrays()
          {
            Router.navigateTo("gui/test/TestUtilArrays");
          }
        }
        """
    );

    this.setTargetContent(
        """
        /* Generated from Java with JSweet 3.2.2-UMLP-SNAPSHOT - http://www.jsweet.org */
        import { GemListUtil } from '@umlp/commonj2ts';

        import { Injectable, inject } from '@angular/core';
        import { Router } from '@angular/router';

        export class AppRouter {
          public static navigateToGuiStart(router:Router){
              router.navigate(["gui/Start"]);
          }

          public static navigateToGuiTestArrayStreamAdapterTest(router:Router){
              router.navigate(["gui/test/ArrayStreamAdapterTest"]);
          }

          public static navigateToGuiTestDomainStreamAdapterTest(router:Router){
              router.navigate(["gui/test/DomainStreamAdapterTest"]);
          }

          public static navigateToGuiTestFutureAdapterTest(router:Router){
              router.navigate(["gui/test/FutureAdapterTest"]);
          }

          public static navigateToGuiTestGlobalstateTest0(router:Router){
              router.navigate(["gui/test/globalstate/Test0"]);
          }

          public static navigateToGuiTestGlobalstateTestGlobalState(router:Router){
              router.navigate(["gui/test/globalstate/TestGlobalState"]);
          }

          public static navigateToGuiTestLocalDateTime(router:Router){
              router.navigate(["gui/test/LocalDateTime"]);
          }

          public static navigateToGuiTestNavigateSource(router:Router){
              router.navigate(["gui/test/NavigateSource"]);
          }

          public static navigateToGuiTestNavigateTargetWithId(val_0: number, router:Router){
              router.navigate(["gui/test/NavigateTargetWithId/" + val_0]);
          }

          public static navigateToGuiTestNavigateTargetWithIds(val_0: number, val_1: number, router:Router){
              router.navigate(["gui/test/NavigateTargetWithIds/" + val_0 + "/" + val_1]);
          }

          public static navigateToGuiTestOptionalAdapterTest(router:Router){
              router.navigate(["gui/test/OptionalAdapterTest"]);
          }

          public static navigateToGuiTestStringSplit(router:Router){
              router.navigate(["gui/test/StringSplit"]);
          }

          public static navigateToGuiTestTest0(router:Router){
              router.navigate(["gui/test/Test0"]);
          }

          public static navigateToGuiTestTest1(router:Router){
              router.navigate(["gui/test/Test1"]);
          }

          public static navigateToGuiTestTest2(router:Router){
              router.navigate(["gui/test/Test2"]);
          }

          public static navigateToGuiTestTestC1(val_0: number, router:Router){
              router.navigate(["gui/test/TestC1/" + val_0]);
          }

          public static navigateToGuiTestTestC1List(router:Router){
              router.navigate(["gui/test/TestC1List"]);
          }

          public static navigateToGuiTestTestCustomUpdate(router:Router){
              router.navigate(["gui/test/TestCustomUpdate"]);
          }

          public static navigateToGuiTestTestEmptyGem(val_0: number, router:Router){
              router.navigate(["gui/test/TestEmptyGem/" + val_0]);
          }

          public static navigateToGuiTestTestEnum(router:Router){
              router.navigate(["gui/test/TestEnum"]);
          }

          public static navigateToGuiTestTestIterate(router:Router){
              router.navigate(["gui/test/TestIterate"]);
          }

          public static navigateToGuiTestTestListObserver(router:Router){
              router.navigate(["gui/test/TestListObserver"]);
          }

          public static navigateToGuiTestTestLocalDateTime(router:Router){
              router.navigate(["gui/test/TestLocalDateTime"]);
          }

          public static navigateToGuiTestTestMethodInvocation(router:Router){
              router.navigate(["gui/test/TestMethodInvocation"]);
          }

          public static navigateToGuiTestTestNestedList(router:Router){
              router.navigate(["gui/test/TestNestedList"]);
          }

          public static navigateToGuiTestTestUpdatableBoundPage(router:Router){
              router.navigate(["gui/test/TestUpdatableBoundPage"]);
          }

          public static navigateToGuiTestTestUpdatablePage(val_0: number, router:Router){
              router.navigate(["gui/test/TestUpdatablePage/" + val_0]);
          }

          public static navigateToGuiTestTestUtilArrays(router:Router){
              router.navigate(["gui/test/TestUtilArrays"]);
          }
        }
        AppRouter["__class"] = "app.AppRouter";
        """
    );
    this.setJumpSourceFileLine(0);
  }

  @Override
  public String getSourceLanguage(CoverageSourceFile targetFile){
    return "java";
  }


  @Override
  public Consumer<Object> onTargetClick(){
    return (Object o)-> {
      if(o instanceof LineClickEvent) {
        // cast via instance of can not be transpiled via JSweet...
        LineClickEvent lineClickEvent = (LineClickEvent) o;

        this.setJumpSourceFileLine(lineClickEvent.getLineNumber());

      }
    };
  }

  @Override
  public String getTargetLanguage(CoverageSourceFile targetFile) {
    return "typescript";
    /*
    int dotIndex = targetFile.getPath().lastIndexOf('.');
    String extension;
    if (dotIndex > 0 && dotIndex < targetFile.getPath().length() - 1)
      extension = targetFile.getPath().substring(dotIndex + 1);
    else
      return "java";

    switch(extension){
      case ".js":
      case ".jsx":
        return "javascript";
      case "ts":
      case "tsx":
        return "typescript";
      case ".java":
      case ".jav":
        return "java";
      default:
        return "java";
    }
    */

  }
}
