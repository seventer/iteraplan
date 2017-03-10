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

import java.util.Date;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.interfaces.LastModificationEntity;


/**
 * This class implements lass modification functionality. It intercepts write access to the database
 * and updates the last modification time and user information for each building block.
 */
public class LastModificationUpdateInterceptor extends BaseInterceptor {

  private static final long serialVersionUID = 4593493792612663560L;

  @Override
  protected String[] getPatterns() {
    return new String[] { "save", "update", "createDeepCopy" };
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.aop.interceptor.AbstractTraceInterceptor#invokeUnderTrace()
   */
  @Override
  protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
    try {
      if (IteraplanProperties.getProperties().propertyIsSetToTrue(IteraplanProperties.PROP_LASTMODIFICATION_LOGGING_ENABLED)) {
        Date date = new Date();
        String userName = UserContext.getCurrentUserContext().getLoginName();
        for (Object parameter : invocation.getArguments()) {
          if (parameter instanceof LastModificationEntity) {
            LastModificationEntity lastModificationPersisentEntity = (LastModificationEntity) parameter;
            lastModificationPersisentEntity.setLastModificationTime(date);
            if (userName != null) {
              lastModificationPersisentEntity.setLastModificationUser(userName);
            }
          }
        }
        return invocation.proceed();
      }
      return invocation.proceed();
    } catch (Throwable ex) { // NOPMD - errors should be caught here as well!
      logger.error("Exception thrown in: " + invocation.getMethod().getName(), ex);
      throw ex;
    }
  }

  /**
   * Overriden to check if a method processes data or merely reads it.
   * 
   * {@inheritDoc}
   */
  @Override
  protected boolean isInterceptorEnabled(MethodInvocation invocation, Log logger) {
    String methodName = invocation.getMethod().getName();
    if (methodName.startsWith("updateAttributeValues")) {
      return false;
    }
    else {
      return checkMethodNameWithPatterns(methodName);
    }
  }
}
