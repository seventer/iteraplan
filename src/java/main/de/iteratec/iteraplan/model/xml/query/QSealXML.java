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
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QSealStatus;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.SealState;


@XmlType(name = "querySeal")
public class QSealXML extends AbstractXMLElement<QSealStatus> {

  private static final Logger LOGGER = Logger.getIteraplanLogger(QSealXML.class);

  private List<BooleanKeyValueXML> values = new ArrayList<BooleanKeyValueXML>();

  @XmlElementWrapper(name = "values")
  @XmlElement(name = "value")
  public List<BooleanKeyValueXML> getValues() {
    return values;
  }
  
  public void setValues(List<BooleanKeyValueXML> values) {
    this.values = values;
  }

  /** {@inheritDoc} */
  public void initFrom(QSealStatus queryElement, Locale locale) {
    values = Lists.newArrayList();
    for (Map.Entry<?, Boolean> entry : queryElement.getStatusMap().entrySet()) {
      BooleanKeyValueXML val = new BooleanKeyValueXML();
      val.setKey(entry.getKey().toString());
      val.setValue(entry.getValue());
      values.add(val);
    }
  }

  /** {@inheritDoc} */
  public void update(QSealStatus statusQueryData, Locale locale) {
    for (BooleanKeyValueXML keyValue : values) {
      try {
        statusQueryData.setStatus(keyValue.getKey(), keyValue.getValue());
      } catch (Exception e) {
        // Simply ignore a saved status that cannot be found. This can only be the case when someone 
        // changed the XML file manually. 
        LOGGER.error("Unknown status '" + keyValue.getKey() + "' of type '" + statusQueryData + "' retrieved from XML file", e);
      }
    }
  }

  /** {@inheritDoc} */
  public void validate(Locale locale) {
      for (BooleanKeyValueXML val : values) {
        boolean found = false;
        for (SealState sealState : SealState.values()) {
          if (sealState.toString().equals(val.getKey())) {
            found = true;
            break;
          }
        }
        
        if (!found) {
          logError("Illegal state found for '" + val.getKey() + "'");
        }
      }
  }
}
