# Login module

A module that extends a generated Fenix system with login functionality.
The Spring backend is extended with Spring-Security and a default service and route for the login.
The passwords are saved after hashing with BCrypt.
The frontend is provided with a default login page and will automatically reroute a logged-out user to it.

TODO: Rechte/Rollen?
TODO: login_module.backend.security_config.ConfigureHttpSecurity contains insecure code

## Dependencies
A User class with fields `String passwort` and `String name`.
- TODO: Solve with CDMerge

## Configuration
- TODO: Replace login page
- TODO: Replace user class, identifier, or password storage
- TODO: Route for login
- TODO: Reset button available
- unsecured routes can be configured by overriding `getNoAuthRoutes` of `XSecurityConfig` via the TOP-Mechanism

## Effects
Backend:
- TODO: Dependency Spring Security is added as dependency to backend
- All routes + websocket are secured
- Login endpoint is added and accessible before login

Frontend
- TODO: Page is generated from LoginPage
- TODO: Routing is added for LoginPage
- TODO: All pages redirect to LoginPage if not logged-in
