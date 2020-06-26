package de.rtner.security.auth.spi;

public interface LoggerPlugin {
  void init(String paramString);
  
  boolean isTraceEnabled();
  
  void trace(Object paramObject);
  
  void trace(Object paramObject, Throwable paramThrowable);
  
  boolean isDebugEnabled();
  
  void debug(Object paramObject);
  
  void debug(Object paramObject, Throwable paramThrowable);
  
  boolean isInfoEnabled();
  
  void info(Object paramObject);
  
  void info(Object paramObject, Throwable paramThrowable);
  
  void warn(Object paramObject);
  
  void warn(Object paramObject, Throwable paramThrowable);
  
  void error(Object paramObject);
  
  void error(Object paramObject, Throwable paramThrowable);
  
  void fatal(Object paramObject);
  
  void fatal(Object paramObject, Throwable paramThrowable);
}
