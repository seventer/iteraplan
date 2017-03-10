/*
 * iteraplan is an IT Governance web application developed by iteratec, GmbH
 * Copyright (C) 2004 - 2014 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact iteratec GmbH headquarters at Inselkammerstr. 4
 * 82008 Munich - Unterhaching, Germany, or at email address info@iteratec.de.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "iteraplan" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by iteraplan".
 */
package de.iteratec.iteraplan.businesslogic.common;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.IteraplanProperties;


/**
 * This class implements audit logging functionality to trace changes to an object.
 * Effectively, it prints out which method (group) has been invoked with with parameters.
 * Parameters are printed out with their type and their toString() value.
 * <p>
 * For instance, when an <code>updateEntity</code> method is invoked, its domain object's 
 * class and name will be printed, and the object's business version (prior to the change).
 * The usefulness of that information for the user or administrator is therefore very limited. 
 * </p>
 */
public class AuditLogger extends BaseInterceptor {
  /** is audit logging activated? */
  private final boolean activated;

  protected String[] getPatterns() {
    return new String[] { "save", "update", "delete" };
  }

  public AuditLogger() {
    super();
    this.activated = IteraplanProperties.getProperties().propertyIsSetToTrue(IteraplanProperties.PROP_AUDIT_LOGGING_ENABLED);
  }

  /* 
   * (non-Javadoc)
   * @see org.springframework.aop.interceptor.AbstractTraceInterceptor#invokeUnderTrace()
   */
  @Override
  protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
    try {
      StringBuilder builder = new StringBuilder();
      builder.append("User [").append(UserContext.getCurrentUserContext().getLoginName()).append("] doing [").append(appendTypeOfAction(invocation))
          .append("] with Types|Values (separated by ++++): [").append(appendArgumentTypesAndValues(invocation) + "]");
      logger.debug(builder.toString());
      return invocation.proceed();
    } catch (Throwable ex) { //NOPMD - errors should be caught here as well!
      logger.error("Exception thrown in: " + invocation.getMethod().getName());
      throw ex;
    }
  }

  /**
   * Overridden to check if a method processes data or merely reads it.
   * 
   * @see org.springframework.aop.interceptor.AbstractTraceInterceptor#isInterceptorEnabled(MethodInvocation, Log)
   */
  @Override
  protected boolean isInterceptorEnabled(MethodInvocation invocation, Log logger) {
    String methodName = invocation.getMethod().getName();
    if (!this.activated || methodName.startsWith("updateAttributeValues")) { // TODO could be made configurable
      return false;
    }
    else {
      return (super.isInterceptorEnabled(invocation, logger) && checkMethodNameWithPatterns(methodName));
    }
  }

  /**
   * Checks whether the log level is sufficiently high, in this case DEBUG or higher.
   * Must be override here, because the base class even requires TRACE level, 
   * while iteraplan configuration convention is to demand DEBUG level logging. 
   */
  @Override
  protected boolean isLogEnabled(Log logger) {
    return logger.isDebugEnabled();
  }
}