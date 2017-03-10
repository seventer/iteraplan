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
package de.iteratec.iteraplan.presentation.dialog.AttributeType;

import java.util.ArrayList;
import java.util.List;

import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.DummyAT;
import de.iteratec.iteraplan.presentation.memory.ColumnDefinition;
import de.iteratec.iteraplan.presentation.memory.SearchDialogMemory;


/**
 * Dialog Memory for the AttributeType management page.
 */
public class AttributeTypeDialogMemory extends SearchDialogMemory {

  private static final long serialVersionUID = 1L;

  /**
   * The TypeOfAttribute as String that should be used for the create attribute type session. This
   * is not all to tidy...
   */
  private String            attributeTypeToCreate;

  private String            attributeName;

  @Override
  public List<Criterion> getCriteria() {
    List<Criterion> attributeTypeCriteria = new ArrayList<Criterion>();
    attributeTypeCriteria.add(new Criterion("attributeName", "attribute.search.label.nameField", "attribute.search.hint.nameField"));

    return attributeTypeCriteria;
  }

  @Override
  public List<ColumnDefinition> getInitialColumnDefinitions() {
    List<ColumnDefinition> props = new ArrayList<ColumnDefinition>();
    props.add(new ColumnDefinition("global.attribute", "name", "", true));
    props.add(new ColumnDefinition("global.description", "description", "", true));
    props.add(new ColumnDefinition("global.attributegroup", "attributeTypeGroup.name", "attributeTypeGroup", true));
    props.add(new ColumnDefinition("manageAttributes.type", "typeOfAttribute.localizedName", "", true));
    return props;
  }

  public String getAttributeTypeToCreate() {
    return attributeTypeToCreate;
  }

  public void setAttributeTypeToCreate(String attributeTypeToCreate) {
    this.attributeTypeToCreate = attributeTypeToCreate;
  }

  public String getAttributeName() {
    return attributeName;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  public AttributeType toAttributeType() {
    AttributeType result = new DummyAT();
    result.setName(this.getAttributeName());
    return result;
  }

  /**{@inheritDoc}**/
  @Override
  public String getIconCss() {
    return ""; // no icon for this entity
  }

  @Override
  public int hashCode() {
    return calculateHashCode(31, super.hashCode(), attributeName, attributeTypeToCreate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    AttributeTypeDialogMemory other = (AttributeTypeDialogMemory) obj;
    if (attributeName == null) {
      if (other.attributeName != null) {
        return false;
      }
    }
    else if (!attributeName.equals(other.attributeName)) {
      return false;
    }
    if (attributeTypeToCreate == null) {
      if (other.attributeTypeToCreate != null) {
        return false;
      }
    }
    else if (!attributeTypeToCreate.equals(other.attributeTypeToCreate)) {
      return false;
    }
    return true;
  }

}
