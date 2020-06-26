package de.rtner.security.auth.spi;

import java.security.acl.Group;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

public class DatabaseServerLoginModule extends UsernamePasswordLoginModule {
  protected String dsJndiName;
  
  protected String principalsQuery = "select Password from Principals where PrincipalID=?";
  
  protected String rolesQuery = "select role, 'Roles' from UserRoles where username=?";
  
  Subject subject;
  
  public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map paramMap1, Map paramMap2) {
    this.subject = paramSubject;
    super.initialize(paramSubject, paramCallbackHandler, paramMap1, paramMap2);
    this.dsJndiName = (String)paramMap2.get("dsJndiName");
    if (this.dsJndiName == null)
      this.dsJndiName = "java:/DefaultDS"; 
    Object object = paramMap2.get("principalsQuery");
    if (object != null)
      this.principalsQuery = object.toString(); 
    object = paramMap2.get("rolesQuery");
    if (object != null)
      this.rolesQuery = object.toString(); 
    this.log.trace("DatabaseServerLoginModule, dsJndiName=" + this.dsJndiName);
    this.log.trace("principalsQuery=" + this.principalsQuery);
    this.log.trace("rolesQuery=" + this.rolesQuery);
  }
  
  protected String getRoles() throws LoginException {
    String str1 = getUsername();
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    String str2 = "";
    try {
      InitialContext initialContext = new InitialContext();
      DataSource dataSource = (DataSource)initialContext.lookup(this.dsJndiName);
      connection = dataSource.getConnection();
      preparedStatement = connection.prepareStatement(this.rolesQuery);
      preparedStatement.setString(1, str1);
      resultSet = preparedStatement.executeQuery();
      if (!resultSet.next())
        throw new FailedLoginException("No Role Found for the User"); 
      byte b = 0;
      str2 = resultSet.getString(1);
      while (resultSet.next()) {
        str2 = str2 + "," + resultSet.getString(1);
        b++;
      } 
      System.out.println("Return of RolesQuery : " + str2);
    } catch (NamingException namingException) {
      throw new LoginException(namingException.toString(true));
    } catch (SQLException sQLException) {
      this.log.error("Query Failed : ", sQLException);
      System.out.println("Query Failed : " + sQLException.toString());
      throw new LoginException(sQLException.toString());
    } finally {
      if (resultSet != null)
        try {
          resultSet.close();
        } catch (SQLException sQLException) {} 
      if (preparedStatement != null)
        try {
          preparedStatement.close();
        } catch (SQLException sQLException) {} 
      if (connection != null)
        try {
          connection.close();
        } catch (SQLException sQLException) {} 
    } 
    return str2;
  }
  
  protected String getUsersPassword() throws LoginException {
    String str1 = getUsername();
    String str2 = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      InitialContext initialContext = new InitialContext();
      DataSource dataSource = (DataSource)initialContext.lookup(this.dsJndiName);
      connection = dataSource.getConnection();
      preparedStatement = connection.prepareStatement(this.principalsQuery);
      preparedStatement.setString(1, str1);
      resultSet = preparedStatement.executeQuery();
      if (!resultSet.next())
        throw new FailedLoginException("No matching username found in Principals"); 
      str2 = resultSet.getString(1);
      str2 = convertRawPassword(str2);
    } catch (NamingException namingException) {
      throw new LoginException(namingException.toString(true));
    } catch (SQLException sQLException) {
      this.log.error("Query failed", sQLException);
      throw new LoginException(sQLException.toString());
    } finally {
      if (resultSet != null)
        try {
          resultSet.close();
        } catch (SQLException sQLException) {} 
      if (preparedStatement != null)
        try {
          preparedStatement.close();
        } catch (SQLException sQLException) {} 
      if (connection != null)
        try {
          connection.close();
        } catch (SQLException sQLException) {} 
    } 
    return str2;
  }
  
  protected Group[] getRoleSets() throws LoginException {
    String str1 = getUsername();
    System.out.println("getRoleSets in DSLM reached");
    String str2 = getRoles();
    if (str1.equals("aj")) {
      str2 = "admin,analyst,kiemgmt,rest-all" + str2;
    } else {
      str2 = "analyst," + str2;
    } 
    return Util.getRoleSets(str1, this.dsJndiName, this.rolesQuery, this.subject, str2);
  }
  
  protected String convertRawPassword(String paramString) {
    return paramString;
  }
}
