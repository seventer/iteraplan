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
package de.iteratec.iteraplan.businesslogic.exchange.xmi.importer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * Resolves import conflicts by replacing the XMI IDs with the IDs from the conflicting DB instance
 * 
 * @author mba
 */
public class XmiImportConflictResolver {

  private String              fileName;

  private static final Logger LOGGER = Logger.getIteraplanLogger(XmiImportConflictResolver.class);

  public XmiImportConflictResolver(String fileName) {
    this.fileName = fileName;
  }

  /**
   * Resolves all conflicts between EObjects of the XMI file and the DB instances
   */
  public void resolveConflicts(List<String[]> conflictList) {
    List<String[]> conflicts = conflictList;

    StringBuffer buf = new StringBuffer();
    try {
      FileReader reader = new FileReader(fileName);

      while (reader.ready()) {
        buf.append((char) reader.read());
      }
      reader.close();
      String newXmiContent = buf.toString();

      for (String[] conflict : conflicts) {
        if (conflict.length != 4) {
          throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
        }
        newXmiContent = newXmiContent.replaceAll(conflict[0], conflict[1]);
      }
      FileWriter writer = new FileWriter(fileName);
      writer.write(newXmiContent);
      writer.close();
    } catch (IOException e) {
      LOGGER.error(e);
    }
  }
}
