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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QFirstLevel;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QPart;


/**
 * XML dtos for {@link QFirstLevel}s
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 *
 */
@XmlType(name = "queryFirstLevel")
public class QFirstLevelXML extends AbstractXMLElement<QFirstLevel> {

  private List<QPartXML> querySecondLevels = new ArrayList<QPartXML>();

  public List<QPartXML> getQuerySecondLevels() {
    return querySecondLevels;
  }

  @XmlElementWrapper(name = "querySecondLevels")
  @XmlElement(name = "querySecondLevel")
  public void setQuerySecondLevels(List<QPartXML> querySecondLevels) {
    this.querySecondLevels = querySecondLevels;
  }

  /* (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#initFrom(java.lang.Object, java.util.Locale)
   */
  public void initFrom(QFirstLevel queryElement, Locale locale) {
    this.querySecondLevels = new ArrayList<QPartXML>();
    for (QPart secondLevelPart : queryElement.getQuerySecondLevels()) {
      QPartXML partXML = new QPartXML();
      partXML.initFrom(secondLevelPart, locale);
      this.querySecondLevels.add(partXML);
    }
  }

  /* (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#update(java.lang.Object, java.util.Locale)
   */
  public void update(QFirstLevel queryElement, Locale locale) {
    if (querySecondLevels != null && !querySecondLevels.isEmpty()) {
      List<QPart> qParts = new ArrayList<QPart>();
      for (QPartXML qPartXML : querySecondLevels) {
        QPart part = new QPart();
        qPartXML.update(part, locale);
        qParts.add(part);
      }
      queryElement.setQuerySecondLevels(qParts);
    }
  }

  /* (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#validate(java.util.Locale)
   */
  public void validate(Locale locale) {
    if (querySecondLevels != null) {
      for (QPartXML part : querySecondLevels) {
        part.validate(locale);
      }
    }
  }

}
