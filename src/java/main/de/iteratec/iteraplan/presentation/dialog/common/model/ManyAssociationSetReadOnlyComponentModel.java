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
package de.iteratec.iteraplan.presentation.dialog.common.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.model.interfaces.IdEntity;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * This component can be used as a base to display elements, which are 
 * associated to an element of type T. This component was introduced 
 * to display associated elements that were aggregated in some way and
 * which are not ment to be managed over this component.
 *
 * @param <T>
 */
public abstract class ManyAssociationSetReadOnlyComponentModel<F extends IdEntity, T extends Comparable<? super T> & IdEntity> extends
    AbstractComponentModelBase<F> {

  private static final long serialVersionUID  = 3065267894972636204L;
  private final String      tableHeaderKey;
  private final String[]    columnHeaderKeys;
  private final String[]    connectedElementsFields;
  private List<T>           connectedElements = new ArrayList<T>();
  private final Boolean[]   lookupLablesMode;

  /**
   * @param componentMode The component mode for this component.
   * @param htmlId The html id the JSP should use for this component. This facilitates 
   *               the writing of automated tests.
   * @param tableHeaderKey The I18N key the JSP should use as a heading for this component.
   * @param columnHeaderKeys The I18N keys the JSP should use as the headings for the columns
   *                         of the connected elements. The number of keys corresponds to the
   *                         number of columns.
   * @param connectedElementsFields The fields of the connected elements that can be displayed
   *                                by the JSP. The fields correspond to the columnHeaderKeys.
   *                                For example: If the first columnHeaderKey is 'global.name', the
   *                                first connectedElementsField could be 'name'.
   * @param lookupLablesMode This boolean list defines, which of the connectedElementsFields are
   *                         I18N keys that have to be resolved. For example, if the 
   *                         connectedElementsFields are: ['name', 'typeofstatus'] and the
   *                         lookupLablesMode are: [false, true], then the field value of name can be
   *                         displayed while the field value of typeofstatus is an I18N key that has
   *                         to be resolved. Set to null if not needed.
   */
  public ManyAssociationSetReadOnlyComponentModel(ComponentMode componentMode, String htmlId, final String tableHeaderKey,
      final String[] columnHeaderKeys, final String[] connectedElementsFields, Boolean[] lookupLablesMode) {
    super(componentMode, htmlId);

    this.tableHeaderKey = tableHeaderKey;
    this.columnHeaderKeys = (columnHeaderKeys == null) ? null : columnHeaderKeys.clone();
    this.connectedElementsFields = (connectedElementsFields == null) ? null : connectedElementsFields.clone();
    this.lookupLablesMode = (lookupLablesMode == null) ? null : lookupLablesMode.clone();

    boolean columnHeaderKeysNull = this.columnHeaderKeys == null && this.connectedElementsFields != null;
    boolean connectedElementsFieldsNull = this.columnHeaderKeys != null && this.connectedElementsFields == null;
    boolean bothNotNull = this.columnHeaderKeys != null && this.connectedElementsFields != null;
    if (columnHeaderKeysNull || connectedElementsFieldsNull || (bothNotNull && this.columnHeaderKeys.length != this.connectedElementsFields.length)) {
      throw new IllegalArgumentException("columnHeaderKeys and connectedElementsFields must have the same number of entries.");
    }

    if (this.lookupLablesMode != null && this.connectedElementsFields != null && this.connectedElementsFields.length != this.lookupLablesMode.length) {
      throw new IllegalArgumentException("lookUpLablesMode and connectedElementsFields must have the same number of entries.");
    }
  }

  public void configure(F target) throws IteraplanException {
    // this component is used for displaying read only data,
    // so it will not configure anything.
  }

  protected abstract List<T> getConnectedElementsToDisplay(F source);

  public void initializeFrom(F source) throws IteraplanException {
    this.connectedElements = getConnectedElementsToDisplay(source);
  }

  public void update() throws IteraplanException {
    // this component is used for displaying read only data,
    // so it does not need to be updated. It probably does
    // not even need to be displayed in edit or create mode.
  }

  public List<T> getConnectedElements() {
    return connectedElements;
  }

  public String[] getColumnHeaderKeys() {
    if (columnHeaderKeys == null) {
      return null;
    }
    else { // copy
      return columnHeaderKeys.clone();
    }
  }

  public String[] getConnectedElementsFields() {
    if (connectedElementsFields == null) {
      return null;
    }
    else { // copy
      return connectedElementsFields.clone();
    }
  }

  public String getTableHeaderKey() {
    return tableHeaderKey;
  }

  public Boolean[] getLookupLablesMode() {
    if (lookupLablesMode == null) {
      return null;
    }
    else { // copy
      return lookupLablesMode.clone();
    }
  }

  public void validate(Errors errors) {
    // do nothing, since it's read-only
  }

}
