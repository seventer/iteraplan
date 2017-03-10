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

import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.IPresentationExtension;


/**
 * Holds the key of an extension that is part of the saved query. When unmarshalling, extensions are
 * set via QueryFormXML#updateExtensions, the update method of this class hence does not contain any
 * implementations.
 */
@XmlType(name = "presentationExtension")
public class PresentationExtensionXML extends AbstractXMLElement<IPresentationExtension> {
  private String name;

  @XmlElement(required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#initFrom(java.lang.Object,
   * java.util.Locale)
   */
  public void initFrom(IPresentationExtension queryElement, Locale locale) {
    this.setName(queryElement.getName());
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#update(java.lang.Object,
   * java.util.Locale)
   */
  public void update(IPresentationExtension queryElement, Locale locale) {
    // nothing to do here as extensions are updated via service method call to
    // de.iteratec.iteraplan.model.xml.query.QueryFormXML#updateExtensions
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#validate(java.util.Locale)
   */
  public void validate(Locale locale) {
    if (StringUtils.isEmpty(this.name)) {
      logError("Empty extension name not allowed");
    }
  }

  public void validateRelation(List<Extension> availableRelations) {
    for (Extension ex : availableRelations) {
      if (ex.getName().equals(name)) {
        return;
      }
    }
    StringBuffer buffer = new StringBuffer().append("Invalid relation: '").append(name).append("'. Relation has to be in: ( ");
    for (Extension ex : availableRelations) {
      buffer.append(ex.getName()).append(' ');
    }
    buffer.append(')');
    logError(buffer.toString());
  }

}
