package de.rtner.security.auth.spi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Logger implements Serializable {
  static final long serialVersionUID = 4232175575988879434L;
  
  protected static String PLUGIN_CLASS_PROP = "org.jboss.logging.Logger.pluginClass";
  
  protected static final String LOG4J_PLUGIN_CLASS_NAME = "org.jboss.logging.Log4jLoggerPlugin";
  
  protected static Class pluginClass = null;
  
  protected static String pluginClassName = null;
  
  private final String name;
  
  protected transient LoggerPlugin loggerDelegate = null;
  
  public static String getPluginClassName() {
    return pluginClassName;
  }
  
  public static void setPluginClassName(String paramString) {
    pluginClassName = paramString;
  }
  
  protected Logger(String paramString) {
    this.name = paramString;
    this.loggerDelegate = getDelegatePlugin(paramString);
  }
  
  public String getName() {
    return this.name;
  }
  
  public LoggerPlugin getLoggerPlugin() {
    return this.loggerDelegate;
  }
  
  public boolean isTraceEnabled() {
    return this.loggerDelegate.isTraceEnabled();
  }
  
  public void trace(Object paramObject) {
    this.loggerDelegate.trace(paramObject);
  }
  
  public void trace(Object paramObject, Throwable paramThrowable) {
    this.loggerDelegate.trace(paramObject, paramThrowable);
  }
  
  public boolean isDebugEnabled() {
    return this.loggerDelegate.isDebugEnabled();
  }
  
  public void debug(Object paramObject) {
    this.loggerDelegate.debug(paramObject);
  }
  
  public void debug(Object paramObject, Throwable paramThrowable) {
    this.loggerDelegate.debug(paramObject, paramThrowable);
  }
  
  public boolean isInfoEnabled() {
    return this.loggerDelegate.isInfoEnabled();
  }
  
  public void info(Object paramObject) {
    this.loggerDelegate.info(paramObject);
  }
  
  public void info(Object paramObject, Throwable paramThrowable) {
    this.loggerDelegate.info(paramObject, paramThrowable);
  }
  
  public void warn(Object paramObject) {
    this.loggerDelegate.warn(paramObject);
  }
  
  public void warn(Object paramObject, Throwable paramThrowable) {
    this.loggerDelegate.warn(paramObject, paramThrowable);
  }
  
  public void error(Object paramObject) {
    this.loggerDelegate.error(paramObject);
  }
  
  public void error(Object paramObject, Throwable paramThrowable) {
    this.loggerDelegate.error(paramObject, paramThrowable);
  }
  
  public void fatal(Object paramObject) {
    this.loggerDelegate.fatal(paramObject);
  }
  
  public void fatal(Object paramObject, Throwable paramThrowable) {
    this.loggerDelegate.fatal(paramObject, paramThrowable);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (pluginClass == null)
      init(); 
    this.loggerDelegate = getDelegatePlugin(this.name);
  }
  
  public static Logger getLogger(String paramString) {
    return new Logger(paramString);
  }
  
  public static Logger getLogger(String paramString1, String paramString2) {
    return new Logger(paramString1 + "." + paramString2);
  }
  
  public static Logger getLogger(Class paramClass) {
    return new Logger(paramClass.getName());
  }
  
  public static Logger getLogger(Class paramClass, String paramString) {
    return new Logger(paramClass.getName() + "." + paramString);
  }
  
  protected static LoggerPlugin getDelegatePlugin(String paramString) {
    LoggerPlugin loggerPlugin = null;
    try {
      loggerPlugin = pluginClass.newInstance();
    } catch (Throwable throwable) {
      loggerPlugin = new NullLoggerPlugin();
    } 
    try {
      loggerPlugin.init(paramString);
    } catch (Throwable throwable) {
      System.err.println("Failed to initalize plugin: " + loggerPlugin);
      loggerPlugin = new NullLoggerPlugin();
    } 
    return loggerPlugin;
  }
  
  protected static void init() {
    try {
      if (pluginClassName == null)
        pluginClassName = System.getProperty(PLUGIN_CLASS_PROP, "org.jboss.logging.Log4jLoggerPlugin"); 
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      pluginClass = classLoader.loadClass(pluginClassName);
    } catch (Throwable throwable) {
      System.out.println("Logger not found");
    } 
  }
  
  static {
    init();
  }
}
