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
package de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Property;
import de.iteratec.iteraplan.businesslogic.reports.query.type.SimpleAssociation;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;


/**
 * The backing bean for mass update configuration 
 */
public class MassUpdateDataImpl implements MassUpdateData, Serializable {

  /** Serialization version. */
  private static final long    serialVersionUID = 8093161374240516667L;
  private Map<String, Boolean> properties       = Maps.newHashMap();
  private Map<String, Boolean> associations     = Maps.newHashMap();
  private Map<String, Boolean> attributes       = Maps.newHashMap();

  public MassUpdateDataImpl(MassUpdateType type) {
    for (Property property : type.getProperties()) {
      properties.put(property.getNameAsID(), Boolean.FALSE);
    }
    for (SimpleAssociation association : type.getMassUpdateAssociations()) {
      associations.put(association.getName(), Boolean.FALSE);
    }
  }

  /**
   * {@inheritDoc}
   */
  public List<String> getSelectedPropertiesList() {
    List<String> ret = new ArrayList<String>();
    for (Map.Entry<String, Boolean> val : properties.entrySet()) {
      if (val.getValue().booleanValue()) {
        ret.add(val.getKey());
      }
    }
    return ret;
  }

  /**
   * {@inheritDoc}
   */
  public void setSelectedPropertiesList(List<String> selectedProperties) {
    // first set all elements to false
    for (String prop : properties.keySet()) {
      properties.put(prop, Boolean.FALSE);
    }
    if (selectedProperties == null) {
      // nothing to do
      return;
    }
    // set selected elements to true
    for (String propertyString : selectedProperties) {
      properties.put(propertyString, Boolean.TRUE);
    }
  }

  /**
   * {@inheritDoc}
   */
  public List<String> getSelectedAttributesList() {
    List<String> ret = new ArrayList<String>();
    for (Map.Entry<String, Boolean> val : attributes.entrySet()) {
      if (val.getValue().booleanValue()) {
        ret.add(val.getKey());
      }
    }
    return ret;
  }

  /**
   * {@inheritDoc}
   */
  public void setSelectedAttributesList(List<String> selectedAttributes) {
    // first set all elements to false
    for (String att : attributes.keySet()) {
      attributes.put(att, Boolean.FALSE);
    }
    if (selectedAttributes == null) {
      // nothing to do
      return;
    }
    // set selected elements to true
    for (String attributeString : selectedAttributes) {
      attributes.put(attributeString, Boolean.TRUE);
    }
  }

  /**
   * {@inheritDoc}
   */
  public List<String> getSelectedAssociationsList() {
    List<String> ret = new ArrayList<String>();
    for (Map.Entry<String, Boolean> val : associations.entrySet()) {
      if (val.getValue().booleanValue()) {
        ret.add(val.getKey());
      }
    }
    return ret;
  }

  /**
   * {@inheritDoc}
   */
  public void setSelectedAssociationsList(List<String> selectedAssociations) {
    // first set all elements to false
    for (String asoc : associations.keySet()) {
      associations.put(asoc, Boolean.FALSE);
    }
    if (selectedAssociations == null) {
      // nothing to do
      return;
    }
    // set selected elements to true
    for (String associationString : selectedAssociations) {
      associations.put(associationString, Boolean.TRUE);
    }
  }

  public Boolean getPropertySelected(String key) {
    return properties.get(key);
  }

  public Boolean getAttributeSelected(String key) {
    return attributes.get(key);
  }

  public Boolean getAssociationSelected(String key) {
    return associations.get(key);
  }

  public void setPropertySelected(String key, Boolean value) {
    properties.put(key, value);
  }

  public void setAttributeSelected(String key, Boolean value) {
    attributes.put(key, value);
  }

  public void setAssociationSelected(String key, Boolean value) {
    associations.put(key, value);
  }

  /**
   * {@inheritDoc}
   */
  public void validate() throws IteraplanBusinessException {
    // no validation supported here
  }

}
