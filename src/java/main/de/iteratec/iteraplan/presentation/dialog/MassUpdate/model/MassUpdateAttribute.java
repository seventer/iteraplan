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
package de.iteratec.iteraplan.presentation.dialog.MassUpdate.model;

import java.util.List;

import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.BBAttribute;


public class MassUpdateAttribute extends MassUpdateItem implements Comparable<MassUpdateAttribute> {

  /** Serialization version. */
  private static final long       serialVersionUID = 3460982768620516470L;
  /** The component Model for the GUI. **/
  private MassUpdateAttributeItem massUpdateAttributeItem;
  /** All possible values the attribute can have. Needed for enums. **/
  private List<AttributeValue>    attributeValues;
  /**
   * The attribute ID as comming from the GUI (consisting of the id of the attribute and the
   * attribute type (enum, string, ...). See
   * {@link de.iteratec.iteraplan.model.attribute.BBAttribute#getStringId()}
   **/
  private String                  selectedAttributeId;
  /** The type of the atttibute as String **/
  private String                  type;
  /** Is it a multiline textbox? **/
  private boolean                 multiline;
  /** The id of the column this attribute will be displayed on the GUI **/
  private int                     positon;

  public MassUpdateAttribute(int positon) {
    super();
    this.positon = positon;
  }

  public MassUpdateAttributeItem getMassUpdateAttributeItem() {
    return massUpdateAttributeItem;
  }

  public List<AttributeValue> getAttributeValues() {
    return attributeValues;
  }

  public void setMassUpdateAttributeItem(MassUpdateAttributeItem massUpdateAttributeItem) {
    this.massUpdateAttributeItem = massUpdateAttributeItem;
    massUpdateAttributeItem.setType(type);
  }

  public void setAttributeValues(List<AttributeValue> attributeValues) {
    this.attributeValues = attributeValues;
  }

  public void setSelectedAttributeId(String selectedAttributeId) {
    this.selectedAttributeId = selectedAttributeId;
  }

  public String getSelectedAttributeId() {
    return selectedAttributeId;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setMultilineInput(boolean multiline) {
    this.multiline = multiline;
  }

  public boolean isMultiline() {
    return multiline;
  }

  public void setMultiline(boolean multiline) {
    this.multiline = multiline;
  }

  public int compareTo(MassUpdateAttribute o) {
    if (this.getPosition() == o.getPosition()) {
      return 0;
    }
    else if (this.getPosition() < o.getPosition()) {
      return -1;
    }
    else {
      return 1;
    }
  }

  public boolean equals(Object obj) {
    if (obj instanceof MassUpdateAttribute) {
      return 0 == compareTo((MassUpdateAttribute) obj);
    }
    return super.equals(obj);
  }

  public int hashCode() {
    return this.getPosition();
  }

  private int getPosition() {
    return this.positon;
  }

  public Integer getAttributeTypeId() {
    return BBAttribute.getIdByStringId(this.selectedAttributeId);
  }
}
