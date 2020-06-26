package de.rtner.security.auth.spi;

public class NullLoggerPlugin implements LoggerPlugin {
  public void init(String paramString) {}
  
  public boolean isTraceEnabled() {
    return false;
  }
  
  public void trace(Object paramObject) {}
  
  public void trace(Object paramObject, Throwable paramThrowable) {}
  
  public boolean isDebugEnabled() {
    return false;
  }
  
  public void debug(Object paramObject) {}
  
  public void debug(Object paramObject, Throwable paramThrowable) {}
  
  public boolean isInfoEnabled() {
    return false;
  }
  
  public void info(Object paramObject) {}
  
  public void info(Object paramObject, Throwable paramThrowable) {}
  
  public void error(Object paramObject) {}
  
  public void error(Object paramObject, Throwable paramThrowable) {}
  
  public void fatal(Object paramObject) {}
  
  public void fatal(Object paramObject, Throwable paramThrowable) {}
  
  public void warn(Object paramObject) {}
  
  public void warn(Object paramObject, Throwable paramThrowable) {}
}
