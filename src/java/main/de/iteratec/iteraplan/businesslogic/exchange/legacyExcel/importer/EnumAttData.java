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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * Represents imported entry for the enumeration attribute. 
 * @author sip
 */
public class EnumAttData extends AttData {
  /** attribute multiple. */
  private CellValueHolder              multiple;
  /** The attribute values */
  private List<EnumAttributeValueData> values;

  /**
   * @param name
   * @param oldName
   * @param description
   * @param groupName
   * @param mandatory
   * @param active
   * @param multiple
   * @param values
   */
  public EnumAttData(CellValueHolder name, CellValueHolder oldName, CellValueHolder description, CellValueHolder groupName,
      CellValueHolder mandatory, CellValueHolder active, CellValueHolder multiple, List<EnumAttributeValueData> values) {
    super(name, oldName, description, groupName, mandatory, active);
    this.multiple = multiple;
    this.values = values;
  }

  public CellValueHolder getMultiple() {
    return multiple;
  }

  public void setMultiple(CellValueHolder multiple) {
    this.multiple = multiple;
  }

  public List<EnumAttributeValueData> getValues() {
    return values;
  }

  public void setValues(List<EnumAttributeValueData> values) {
    this.values = values;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    toStringBuilder.append("name", getName().getAttributeValue());
    toStringBuilder.append("oldName", getOldName().getAttributeValue());
    toStringBuilder.append("description", getDescription().getAttributeValue());
    toStringBuilder.append("groupName", getGroupName().getAttributeValue());
    toStringBuilder.append("mandatory", getMandatory().getAttributeValue());
    toStringBuilder.append("active", getActive().getAttributeValue());
    toStringBuilder.append("multiple", getMultiple().getAttributeValue());
    toStringBuilder.append("values", getValues().toString());
    return toStringBuilder.toString();
  }
}
