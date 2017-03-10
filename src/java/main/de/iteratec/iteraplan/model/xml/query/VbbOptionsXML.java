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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.vbb.VbbOptionsBean;
import de.iteratec.iteraplan.common.Constants;


@XmlType(name = "vbbOption", propOrder = { "viewpointConfig", "selectedGraphicFormat" })
public class VbbOptionsXML extends AbstractXMLElement<VbbOptionsBean> {

  private List<KeyValueXML>   viewpointConfig;
  private String              selectedGraphicFormat = Constants.REPORTS_EXPORT_GRAPHICAL_SVG;

  /**
   * Empty constructor needed for JAXB. DO NOT USE WITHIN THE APPLICATION!
   */
  public VbbOptionsXML() {
    // empty constructor needed for JAXB. DO NOT USE
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#initFrom(java.lang.Object,
   * java.util.Locale)
   */
  public void initFrom(VbbOptionsBean queryElement, Locale locale) {
    this.selectedGraphicFormat = queryElement.getSelectedGraphicFormat();
    this.viewpointConfig = Lists.newArrayList();
    
    Map<String, String> viewpointConfigMap = queryElement.getViewpointConfigMap();
    for (Map.Entry<String, String> entry : viewpointConfigMap.entrySet()) {
      this.viewpointConfig.add(new KeyValueXML(entry.getKey(), entry.getValue()));
    }
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#update(java.lang.Object,
   * java.util.Locale)
   */
  public void update(VbbOptionsBean queryElement, Locale locale) {
    Map<String, String> viewpointConfigMap = new HashMap<String, String>();
    
    for (KeyValueXML entry : this.viewpointConfig) {
      viewpointConfigMap.put(entry.getKey(), entry.getValue());
    }
    
    queryElement.setViewpointConfigMap(viewpointConfigMap);
    queryElement.setSelectedGraphicFormat(this.selectedGraphicFormat);
  }

  public void validate(Locale locale) {
    // nothing to do yet
  }
  
  @XmlElementWrapper(name = "viewpointConfig")
  @XmlElement(name = "entry")
  public List<KeyValueXML> getViewpointConfig() {
    return this.viewpointConfig;
  }
  
  public void setViewpointConfig(List<KeyValueXML> viewpointConfig) {
    this.viewpointConfig = viewpointConfig;
  }

  @XmlElement
  public String getSelectedGraphicFormat() {
    return this.selectedGraphicFormat;
  }

  public void setSelectedGraphicFormat(String selectedGraphicsFormat) {
    this.selectedGraphicFormat = selectedGraphicsFormat;
  }
}
