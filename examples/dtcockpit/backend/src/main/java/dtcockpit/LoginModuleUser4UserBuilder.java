package dtcockpit;

import loginmodule.LoginModuleUser;
import loginmodule.LoginModuleUserBuilder;
import loginmodule.LoginModuleUserObserver;
import umlp.backendrte.common.NotificationScope;

import java.time.LocalDateTime;
import java.util.Optional;

// TODO: Generate me when concretizised
//

/**
 * Replaced with the generated LoginModuleUser4UserDelegatedBuilder
 * {@link LoginModuleUser4UserDelegatedBuilder}
 */
@Deprecated
public class LoginModuleUser4UserBuilder extends LoginModuleUserBuilder {
  protected final UserBuilder delegate;

  public LoginModuleUser4UserBuilder(UserBuilder delegate) {
    super(delegate.getGemId());
    this.delegate = delegate;
  }

  @Override
  public boolean isBuildFinished() {
    return delegate.isBuildFinished();
  }

  @Override
  public void setBuildFinished(boolean buildFinished) {
    delegate.setBuildFinished(buildFinished);
  }

  @Override
  public boolean isINTERNAL_deser_mode() {
    return delegate.isINTERNAL_deser_mode();
  }

  @Override
  public void setINTERNAL_deser_mode(boolean INTERNAL_deser_mode) {
    delegate.setINTERNAL_deser_mode(INTERNAL_deser_mode);
  }

  @Override
  public LoginModuleUserBuilder username(String username) {
    delegate.username(username);
    return this;
  }

  @Override
  public LoginModuleUserBuilder email(String email) {
    delegate.email(email);
    return this;
  }

  @Override
  protected boolean checkUsernamePresent() {
    return delegate.checkUsernamePresent();
  }

  @Override
  protected boolean checkEmailPresent() {
    return delegate.checkEmailPresent();
  }

  @Override
  public LoginModuleUserBuilder password(String password) {
    delegate.password(password);
    return this;
  }

  @Override
  protected boolean checkPasswordPresent() {
    return delegate.checkPasswordPresent();
  }

  @Override
  public LoginModuleUserBuilder registrationDate(LocalDateTime registrationDate) {
    delegate.registrationDate(registrationDate);
    return this;
  }

  @Override
  protected boolean checkRegistrationDatePresent() {
    return delegate.checkRegistrationDatePresent();
  }

  @Override
  public Optional<LoginModuleUser> build() {
    return delegate.build().map(u->u);
  }

  @Override
  public boolean isBuildable() {
    return delegate.isBuildable();
  }

  @Override
  public long getGemId() {
    return delegate.getGemId();
  }

  @Override
  public void addObserver(LoginModuleUserObserver o) {
    delegate.addObserver(o);
  }

  @Override
  public void removeObserver(LoginModuleUserObserver o) {
    delegate.removeObserver(o);
  }

  @Override
  public boolean containsObserver(LoginModuleUserObserver o) {
    return delegate.containsObserver(o);
  }

  @Override
  public boolean equals(Object o) {
    return delegate.equals(o);
  }

  @Override
  public boolean TESTING_deepEquals(Object o) {
    return delegate.TESTING_deepEquals(o);
  }

  @Override
  public String getUsername() {
    return delegate.getUsername();
  }

  @Override
  public void setUsername(String _username) {
    delegate.setUsername(_username);
  }

  @Override
  public void setUsername(String _username, NotificationScope scope) {
    delegate.setUsername(_username, scope);
  }

  @Override
  public void setUsernamePresent(boolean val) {
    delegate.setUsernamePresent(val);
  }

  @Override
  public String getPassword() {
    return delegate.getPassword();
  }

  @Override
  public void setPassword(String _password) {
    delegate.setPassword(_password);
  }

  @Override
  public void setPassword(String _password, NotificationScope scope) {
    delegate.setPassword(_password, scope);
  }

  @Override
  public void setPasswordPresent(boolean val) {
    delegate.setPasswordPresent(val);
  }

  @Override
  public String getEmail() {
    return delegate.getEmail();
  }

  @Override
  public void setEmail(String _email) {
    delegate.setEmail(_email);
  }

  @Override
  public void setEmail(String _email, NotificationScope scope) {
    delegate.setEmail(_email, scope);
  }

  @Override
  public void setEmailPresent(boolean val) {
    delegate.setEmailPresent(val);
  }

  @Override
  protected boolean checkEmail() {
    return delegate.checkEmail();
  }

  @Override
  public LocalDateTime getRegistrationDate() {
    return delegate.getRegistrationDate();
  }

  @Override
  public void setRegistrationDate(LocalDateTime _registrationDate) {
    delegate.setRegistrationDate(_registrationDate);
  }

  @Override
  public void setRegistrationDate(LocalDateTime _registrationDate, NotificationScope scope) {
    delegate.setRegistrationDate(_registrationDate, scope);
  }

  @Override
  public void setRegistrationDatePresent(boolean val) {
    delegate.setRegistrationDatePresent(val);
  }

  @Override
  protected boolean checkUsername() {
    return delegate.checkUsername();
  }

  @Override
  protected boolean checkPassword() {
    return delegate.checkPassword();
  }

  @Override
  protected boolean checkRegistrationDate() {
    return delegate.checkRegistrationDate();
  }
}
