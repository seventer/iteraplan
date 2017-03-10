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

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.TimelineFeature;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


@XmlType(name = "timelineFeature", propOrder = { "runtimePeriod", "position", "name" })
public class TimelineFeatureXML extends AbstractXMLElement<TimelineFeature> {

  private boolean runtimePeriod;
  private int     position;
  private String  name = null;

  @XmlElement(name = "runtimePeriod", required = true)
  public boolean isRuntimePeriod() {
    return runtimePeriod;
  }

  public void setRuntimePeriod(boolean runtimePeriod) {
    this.runtimePeriod = runtimePeriod;
  }

  @XmlElement(name = "position", required = true)
  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  @XmlElement(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**{@inheritDoc}**/
  public void initFrom(TimelineFeature queryElement, Locale locale) {
    this.runtimePeriod = queryElement.isRuntimePeriod();
    this.position = queryElement.getPosition();
    if (queryElement.isRuntimePeriod()) {
      this.name = "runtimePeriodTimeline";
    }
    else {
      this.name = queryElement.getName();
    }
  }

  /**{@inheritDoc}**/
  public void update(TimelineFeature queryElement, Locale locale) {
    // in this direction it can not work here, since it needs the services
    throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
  }

  /**{@inheritDoc}**/
  public void validate(Locale locale) {
    // nothing here

  }

}