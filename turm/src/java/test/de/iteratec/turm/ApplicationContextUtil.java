package de.iteratec.turm;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;


public class ApplicationContextUtil {

  private static GenericXmlApplicationContext applicationContext;

  public static synchronized ConfigurableApplicationContext getApplicationContext() {
    if (applicationContext != null) {
      return applicationContext;
    }
    applicationContext = new GenericXmlApplicationContext();

    applicationContext.load(getSpringConfigFiles());
    applicationContext.refresh();
    return applicationContext;
  }

  public static String[] getSpringConfigFiles() {
    return new String[] { "applicationContext-iturm.xml", "applicationContext-spring-security.xml" };
  }

}
