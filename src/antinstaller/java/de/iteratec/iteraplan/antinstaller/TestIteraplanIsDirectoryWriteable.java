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
package de.iteratec.iteraplan.antinstaller;

import java.io.File;
import java.util.Map;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.tp23.antinstaller.InstallerContext;
import org.tp23.antinstaller.input.Validator;
import org.tp23.antinstaller.ValidationException;


/**
 * Validates input fields in mask "Hibernate Search index" and checks if the provided Path is a
 * valid directory and writeable.
 */
class TestIteraplanIsDirectoryWriteable implements Validator {
  
  private static final Logger LOGGER = Logger.getLogger(TestIteraplanIsDirectoryWriteable.class);

  public void validate(final String a_strText, final InstallerContext a_oInstallerContext)
      throws Exception {
    
    final Map oMapProperties = TestUtil.getProperties( a_oInstallerContext );
    final String strPath = TestUtil.getProperty( oMapProperties, "hibernate.search.index", "Directory for search indexes" );
    
    File f = new File(strPath);

    try {
      f.mkdirs();
    } catch (SecurityException e) {
      LOGGER.error("Can't write to directory" + a_strText, e);
      throw new ValidationException("Can't write to directory " + a_strText);
    }
    if (!f.isDirectory()) {
      LOGGER.error("The provided Path is not a directory: " + a_strText);
      throw new ValidationException("The provided Path is not a directory: " + a_strText);
    }
    if (!f.canWrite()) {
      LOGGER.error("Can't write to directory " + a_strText);
      throw new ValidationException("Can't write to direcotry " + a_strText);
    }
  }

  public String getErrorMessage(final Throwable a_oThrowable, final Locale a_oLocale) {
    if (a_oThrowable instanceof ValidationException) {
      return a_oThrowable.getLocalizedMessage();
    }
    LOGGER.debug(a_oThrowable + "not instance of ValidationException; unexpected error: '" + a_oThrowable.getLocalizedMessage() + "'");
    return "unexpected error: '" + a_oThrowable.getLocalizedMessage() + "'";
  }
}
