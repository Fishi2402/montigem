<#--This template adds a login check to every pages ngOnInit-->

// From the login module: Redirect if not logged in
<#--// TODO: A better way to exclude Login/Register/etc pages-->
/*
 To exclude this page from the Login system:
 Define a singleton type and add it as a parameter to your page named "doesNotRequireLogin"
 - we check for the existence of a getDoesNotRequireLogin property
*/
if (!('getDoesNotRequireLogin' in this) && LoginSecManager.shouldAbortOnInit(this._router, this._route)) {
  return;
}
// end login module system
