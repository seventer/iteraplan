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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import de.iteratec.iteraplan.model.attribute.BBAttribute;


/**
 * XML DTO for persisting enum attributes including associated colors/linetypes/...
 * 
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 */
@XmlType(name = "enumAttribute")
public class EnumAttributeXML {

  /** {@link #getAttributeName()} */
  private String       attributeName   = null;
  /** {@link #getAttributeValues()} */
  private List<String> attributeValues = new ArrayList<String>();
  /** {@link #getSelectedStyles()} */
  private List<String> selectedStyles  = new ArrayList<String>();
  /** {@link #getAttributeId()} */
  private Integer      attributeId;

  public String getAttributeName() {
    return attributeName;
  }

  @XmlElementWrapper(name = "attributeValues")
  @XmlElement(name = "value")
  public List<String> getAttributeValues() {
    return attributeValues;
  }

  @XmlElementWrapper(name = "selectedStyles")
  @XmlElement(name = "style")
  public List<String> getSelectedStyles() {
    return selectedStyles;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  public void setAttributeValues(List<String> attributeValues) {
    this.attributeValues = attributeValues;
  }

  public void setSelectedStyles(List<String> selectedStyle) {
    this.selectedStyles = selectedStyle;
  }

  public void initFrom(BBAttribute attribute, List<String> values, List<String> styles) {
    // this.attributeName = attribute.getStringIdName();
    this.attributeId = attribute.getId();
    copyStringList(this.attributeValues, values);
    copyStringList(this.selectedStyles, styles);
  }

  /**
   * Copies the values of one String list to another
   * 
   * @param dest
   *          The destination list
   * @param src
   *          The source list
   */
  private void copyStringList(List<String> dest, List<String> src) {
    if (src != null) {
      for (String str : src) {
        dest.add(str);
      }
    }
  }

  @XmlAttribute(required = true)
  public Integer getAttributeId() {
    return attributeId;
  }

  public void setAttributeId(Integer attributeId) {
    this.attributeId = attributeId;
  }

}
