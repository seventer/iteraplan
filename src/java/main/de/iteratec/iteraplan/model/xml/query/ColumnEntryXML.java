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
package de.iteratec.iteraplan.model.xml.query;

import java.util.Locale;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


@XmlType(name = "visibleColumn")
public class ColumnEntryXML extends AbstractXMLElement<ColumnEntry> {

  private String head;
  private String field;
  private String type;

  public void initFrom(ColumnEntry queryElement, Locale locale) {
    field = queryElement.getField();
    head = queryElement.getHead();
    type = queryElement.getEnumType().toString();
  }

  public void update(ColumnEntry queryElement, Locale locale) {
    throw new IteraplanTechnicalException(new UnsupportedOperationException(String.format("Update not possible. '%s' is immutable",
        ColumnEntry.class.getName())));
  }

  public void validate(Locale locale) {

    String errorMessage = "Mandatory field '%s' is empty";

    if (StringUtils.isEmpty(head)) {
      logError(String.format(errorMessage, "head"));
    }
    if (StringUtils.isEmpty(field)) {
      logError(String.format(errorMessage, "field"));
    }
    if (StringUtils.isEmpty(type)) {
      logError(String.format(errorMessage, "type"));
    }
  }

  @XmlElement
  public String getHead() {
    return head;
  }

  public void setHead(String head) {
    this.head = head;
  }

  @XmlElement
  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  @XmlElement
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

}
