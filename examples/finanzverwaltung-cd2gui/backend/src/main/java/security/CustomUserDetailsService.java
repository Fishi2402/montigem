/* (c) https://github.com/MontiCore/monticore */
package security;


import finanzverwaltung.FinanzverwaltungManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import umlp.backendrte.common.GemList;

import java.util.ArrayList;
import java.util.Collection;
/**/

public class CustomUserDetailsService implements UserDetailsService {

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    GemList<finanzverwaltung.User> profilList = FinanzverwaltungManager.getUserList();
    finanzverwaltung.User profil = null;
    for (finanzverwaltung.User x : profilList) {
      if (String.valueOf(x.getUsername()).equals(username)) {
        profil = x;
      }
    }
    if (profil == null) {
      throw new UsernameNotFoundException("Username not found");
    }

    Collection<SimpleGrantedAuthority> foundAuthorities = new ArrayList<>();
    foundAuthorities.add(new SimpleGrantedAuthority("ROLE_" + profil.getRole().name().toUpperCase()));
    return new User(profil.getUsername(), profil.getPassword(), foundAuthorities);
  }
  /**/
}
