${tc.signature()}
${cd4c.method("protected void configure(HttpSecurity http) throws Exception")}

// ToDo: insecure!!!!
http.csrf().disable();

http.authorizeRequests((requests) -> requests.anyRequest().authenticated());
http.formLogin()
    .failureHandler(new SimpleUrlAuthenticationFailureHandler() {
      // Note: The HTML error information will not be shown, as spring does not allow the re-use of its error page
      @Override
      public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (!MediaType.parseMediaTypes(request.getHeader(HttpHeaders.ACCEPT)).contains(MediaType.APPLICATION_JSON)) {
          super.onAuthenticationFailure(request, response, exception); // delegate to default* behavior
          return;
        }
        // Reply with JSON object
        Map<String, Object> data = new HashMap<>();
        data.put("success", false);
        data.put("reason", exception.getMessage()); // reason might be localized
        sendJSONResponse(response, data);
      }
    })
    .successHandler(new SavedRequestAwareAuthenticationSuccessHandler() {
      @Override
      public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (!MediaType.parseMediaTypes(request.getHeader(HttpHeaders.ACCEPT)).contains(MediaType.APPLICATION_JSON)) {
          super.onAuthenticationSuccess(request, response, authentication); // delegate to default* behavior
          return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        sendJSONResponse(response, data);
      }
    })
    ;
