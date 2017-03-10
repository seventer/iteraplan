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
package de.iteratec.iteraplan.model.fulltextsearch;

import java.io.Serializable;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;


/**
 * <p>
 * This Bridge maps the {@link de.iteratec.iteraplan.model.BuildingBlock#getDocumentId()}. It should
 * not be necessary, because getDocumentId() already returns a String that includes the id and the
 * active Datasource. However, Hibernate Search sometimes still uses the persistent id of the Element
 * (which is an int). And tries to use the default StringBridge that then fails. Therefore within the
 * objectToString method either the String is returned or the Integer id param is converted to a String
 * that contains the active datasource.
 * </p>
 * <p>
 * This implementation is threadsafe as long as only Strings are used as params. When an Integer
 * param (for objectToString() or set()) is used from within a Thread, the objectToString() method
 * will cause an Exception because the activeDatasource can not be resolved from within a Thread.
 * </p>
 * 
 * @author afe
 */
public class BuildingBlockBridge implements TwoWayFieldBridge, Serializable {

  private static final long serialVersionUID = 2406864250760764420L;

  public Object get(String fieldName, Document doc) {
    // if the id is not within the index, the user did not update to the latest index-schema. Error
    // msg contains instructions how to fix this problem
    if (doc.getField(fieldName) == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.INDEX_FILES_DEPRECATED);
    }
    return doc.getField(fieldName).stringValue();
  }

  /**
   * <p>
   * This method must only be called with an Integer as a param, when called from the main Thread.
   * It is fine to be called from a subthread when the param is already in String form.
   * </p>
   * <p>
   * To achieve this, the IteraFullTextSessionImpl and IteraFullTextIndexEventListener have been
   * patched to call this method and convert the id from integer to its string form (also containing
   * the active datasource) within the main thread. The worker threads later on will still call this
   * method, but the param will already be string and thus be returned unmodified.
   * </p>
   * 
   * @param obj
   *          The object to index.
   * @return string (index) representation of the specified object. Must not be <code>null</code>,
   *         but can be empty.
   */
  public String objectToString(Object obj) {
    if (obj instanceof Integer) {
      Integer id = (Integer) obj;
      return UserContext.getActiveDatasource() + "_" + id;
    }
    return (String) obj;
  }

  public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
    String indexedString = objectToString(value);

    // only set if not null
    if (indexedString != null) {
      luceneOptions.addFieldToDocument(name, indexedString, document);
    }
  }

}
