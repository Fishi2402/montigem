import { Routes } from "@angular/router";

export const routes: Routes = [
  // default path like in other routing files
  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "login/LoginPage"
  },
  {
    path: "login",
    loadChildren: () => import("../../build/appgui/gui/login/LoginPageModule").then(m => m.LoginpageModule)
  },
  {
    path: "login/forgotpassword",
    loadChildren: () => import("../../build/appgui/gui/login/ForgotPasswordPageModule").then(m => m.ForgotpasswordpageModule)
  },
  {
    path: "login/registration",
    loadChildren: () => import("../../build/appgui/gui/login/RegistrationPageModule").then(m => m.RegistrationpageModule)
  },
  {
    path: "start",
    loadChildren: () => import("../../build/appgui/gui/StartModule").then(m => m.StartModule)
  },
  {
    path: "notes",
    loadChildren: () => import("../../build/appgui/gui/NotesPageModule").then(m => m.NotespageModule)
  },
  {
    path: "project",
    loadChildren: () => import("../../build/appgui/gui/ProjectFileViewModule").then(m => m.ProjectfileviewModule)
  },
  {
    path: "projects",
    loadChildren: () => import("../../build/appgui/gui/SehubMainPageModule").then(m => m.SehubmainpageModule)
  },
  {
    path: "artifacts",
    loadChildren: () => import("../../build/appgui/gui/ProjectPageModule").then(m => m.ProjectpageModule)
  },
  {
    path: "bingo",
    loadChildren: () => import("../../build/appgui/gui/BingoModule").then(m => m.BingoModule)
  },
  {
    path: "usersettings",
    loadChildren: () => import("../../build/appgui/gui/usersettings/UserSettingsModule").then(m => m.UsersettingsModule)
  },
 {
    path: "issue-tracker",
    loadChildren: () => import("../../build/appgui/gui/issues/IssueTrackerPageModule").then(m => m.IssuetrackerpageModule)
  },
  {
    path: '404',
    loadChildren: () => import("../../build/appgui/gui/PageNotFoundModule").then(m => m.PagenotfoundModule)
  },
  {
    path: 'CD2GUI',
    redirectTo: 'CD2GUI/Navigation'
  },
  {
    path: 'CD2GUI/Navigation',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/CD2GUIDashboardModule").then(m => m.Cd2guidashboardModule)
  },
  {
      path: 'CD2GUI/UserOverview',
      loadChildren: () => import("../../../umlp/fe2/cd2gui/models/UserOverviewModule").then(m => m.UseroverviewModule)
  },
  {
    path: 'CD2GUI/UserForm',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/UserFormModule").then(m => m.UserformModule)
  },
  {
    path: 'CD2GUI/UserDetails',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/UserDetailsModule").then(m => m.UserdetailsModule)
  },
  {
    path: 'CD2GUI/ProjectOverview',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/ProjectOverviewModule").then(m => m.ProjectoverviewModule)
  },
  {
    path: 'CD2GUI/ProjectForm',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/ProjectFormModule").then(m => m.ProjectformModule)
  },
  {
    path: 'CD2GUI/ProjectDetails',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/ProjectDetailsModule").then(m => m.ProjectdetailsModule)
  },
  {
    path: 'CD2GUI/PostOverview',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/PostOverviewModule").then(m => m.PostoverviewModule)
  },
  {
    path: 'CD2GUI/PostForm',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/PostFormModule").then(m => m.PostformModule)
  },
  {
    path: 'CD2GUI/PostDetails',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/PostDetailsModule").then(m => m.PostdetailsModule)
  },
  {
    path: 'CD2GUI/IssueOverview',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/IssueOverviewModule").then(m => m.IssueoverviewModule)
  },
  {
    path: 'CD2GUI/IssueForm',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/IssueFormModule").then(m => m.IssueformModule)
  },
  {
    path: 'CD2GUI/IssueDetails',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/IssueDetailsModule").then(m => m.IssuedetailsModule)
  },
  {
    path: 'CD2GUI/FileArtifactOverview',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/FileArtifactOverviewModule").then(m => m.FileartifactoverviewModule)
  },
  {
    path: 'CD2GUI/FileArtifactForm',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/FileArtifactFormModule").then(m => m.FileartifactformModule)
  },
  {
    path: 'CD2GUI/FileArtifactDetails',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/FileArtifactDetailsModule").then(m => m.FileartifactdetailsModule)
  },
  {
    path: 'CD2GUI/DirectoryOverview',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/DirectoryOverviewModule").then(m => m.DirectoryoverviewModule)
  },
  {
    path: 'CD2GUI/DirectoryForm',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/DirectoryFormModule").then(m => m.DirectoryformModule)
  },
  {
    path: 'CD2GUI/DirectoryDetails',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/DirectoryDetailsModule").then(m => m.DirectorydetailsModule)
  },
  {
    path: 'CD2GUI/BingoEntryOverview',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/BingoEntryOverviewModule").then(m => m.BingoentryoverviewModule)
  },
  {
    path: 'CD2GUI/BingoEntryForm',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/BingoEntryFormModule").then(m => m.BingoentryformModule)
  },
  {
    path: 'CD2GUI/BingoEntryDetails',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/BingoEntryDetailsModule").then(m => m.BingoentrydetailsModule)
  },
  {
    path: 'CD2GUI/BingoBoardOverview',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/BingoBoardOverviewModule").then(m => m.BingoboardoverviewModule)
  },
  {
    path: 'CD2GUI/BingoBoardForm',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/BingoBoardFormModule").then(m => m.BingoboardformModule)
  },
  {
    path: 'CD2GUI/BingoBoardDetails',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/BingoBoardDetailsModule").then(m => m.BingoboarddetailsModule)
  },
  {
    path: 'CD2GUI/ArtifactOverview',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/ArtifactOverviewModule").then(m => m.ArtifactoverviewModule)
  },
  {
    path: 'CD2GUI/ArtifactForm',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/ArtifactFormModule").then(m => m.ArtifactformModule)
  },
  {
    path: 'CD2GUI/ArtifactDetails',
    loadChildren: () => import("../../../umlp/fe2/cd2gui/models/ArtifactDetailsModule").then(m => m.ArtifactdetailsModule)
  },


  //Woldcard Route for 404 Page:
  {
    path: '**',
    redirectTo: '404'
  }
]
