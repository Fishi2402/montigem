${tc.signature("managerName", "userclassName")}
${cd4c.method("public UserDetailsService customUserDetailsService()")}

return (username) -> {
      GemList<${userclassName}> profilList = ${managerName}.get${userclassName}List();
      ${userclassName} profil = null;
      for (${userclassName} x : profilList) {
        if (String.valueOf(x.getUsername()).equals(username)) {
          profil = x;
        }
      }
      if (profil == null) {
        throw new IllegalStateException("Username not found");
      }

      Collection<SimpleGrantedAuthority> foundAuthorities = new ArrayList<>();
      <#-- TODO: Authorities from rights? -->
      return new org.springframework.security.core.userdetails.User(profil.getUsername(), profil.getPassword(), foundAuthorities);
    };
