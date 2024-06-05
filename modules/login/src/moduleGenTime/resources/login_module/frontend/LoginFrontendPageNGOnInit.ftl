<#--This template adds a login check to every pages ngOnInit-->

// From the login module: Redirect if not logged in
<#--// TODO: A better way to exclude Login/Register/etc pages-->
/*
 To exclude this page from the Login system:
 Define a singleton type and add it as a parameter to your page named "doesNotRequireLogin"
 - we check for the existence of a getDoesNotRequireLogin property
*/
let routerForLoginCheckModule = this._router; // this may become never
if (!(LoginSecManager.isSignedIn() || LoginSecManager.isCurrentlyLoggingIn()) && !('getDoesNotRequireLogin' in this)) {
  alert("Not logged in\nThe login module will redirect you to a login page now");
  routerForLoginCheckModule.navigate(['gui', 'login', 'LoginPage']); // TODO: Store path in global var
  return;
}
// end login module system
