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
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QTimespanData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QUserInput;


@XmlType(name = "queryUserInput", propOrder = { "noAssignements", "queryFirstLevels", "statusQueryData", "timeSpanQueryData", "sealQueryData" })
public class QUserInputXML extends AbstractXMLElement<QUserInput> {

  private List<QFirstLevelXML> queryFirstLevels = new ArrayList<QFirstLevelXML>();
  private QTimespanDataXML     timeSpanQueryData;
  private QStatusXML           statusQueryData;
  private QSealXML             sealQueryData;
  private Boolean              noAssignements   = Boolean.FALSE;

  @XmlElementWrapper(name = "queryFirstLevels")
  @XmlElement(name = "queryFirstLevel")
  public List<QFirstLevelXML> getQueryFirstLevels() {
    return queryFirstLevels;
  }

  @XmlElement(name="timeSpanQueryData")
  public QTimespanDataXML getTimeSpanQueryData() {
    return timeSpanQueryData;
  }

  @XmlElement(name="statusQueryData")
  public QStatusXML getStatusQueryData() {
    return statusQueryData;
  }

  @XmlElement(name="sealQueryData")
  public QSealXML getSealQueryData() {
    return sealQueryData;
  }

  @XmlElement(required = true)
  public Boolean getNoAssignements() {
    return noAssignements;
  }

  public void setNoAssignements(Boolean noAssignements) {
    this.noAssignements = noAssignements;
  }

  public void setTimeSpanQueryData(QTimespanDataXML timeSpanQueryData) {
    this.timeSpanQueryData = timeSpanQueryData;
  }

  public void setQueryFirstLevels(List<QFirstLevelXML> queryFirstLevels) {
    this.queryFirstLevels = queryFirstLevels;
  }

  public void setStatusQueryData(QStatusXML statusQueryData) {
    this.statusQueryData = statusQueryData;
  }

  public void setSealQueryData(QSealXML sealQueryData) {
    this.sealQueryData = sealQueryData;
  }

  /** {@inheritDoc} */
  public void initFrom(QUserInput queryElement, Locale locale) {
    this.queryFirstLevels = new ArrayList<QFirstLevelXML>();
    this.noAssignements = queryElement.getNoAssignements();

    for (QFirstLevel firstLevel : queryElement.getQueryFirstLevels()) {
      QFirstLevelXML firstLevelXML = new QFirstLevelXML();
      firstLevelXML.initFrom(firstLevel, locale);
      this.queryFirstLevels.add(firstLevelXML);
    }

    QTimespanData timespanData = queryElement.getTimespanQueryData();
    if (queryElement.getTimespanQueryData() != null) {
      this.timeSpanQueryData = new QTimespanDataXML();
      this.timeSpanQueryData.setStartDate(timespanData.getStartDateAsString(), locale, timespanData.getStartTimeFunction());
      this.timeSpanQueryData.setEndDate(timespanData.getEndDateAsString(), locale, timespanData.getEndTimeFunction());
    }

    if (queryElement.getStatusQueryData() != null) {
      this.statusQueryData = new QStatusXML();
      this.statusQueryData.initFrom(queryElement.getStatusQueryData(), locale);
    }

    if (queryElement.getSealQueryData() != null) {
      this.sealQueryData = new QSealXML();
      this.sealQueryData.initFrom(queryElement.getSealQueryData(), locale);
    }
  }

  /** {@inheritDoc} */
  public void update(QUserInput userInput, Locale locale) {
    if (queryFirstLevels != null && queryFirstLevels.size() > 0) {
      List<QFirstLevel> firstLevels = new ArrayList<QFirstLevel>();
      for (QFirstLevelXML firstLevelXML : queryFirstLevels) {
        QFirstLevel newFirstLevel = new QFirstLevel();
        firstLevelXML.update(newFirstLevel, locale);
        firstLevels.add(newFirstLevel);
      }
      userInput.setQueryFirstLevels(firstLevels);
    }

    if (this.timeSpanQueryData != null && userInput.getTimespanQueryData() != null) {
      timeSpanQueryData.update(userInput.getTimespanQueryData(), locale);
    }

    if (this.statusQueryData != null) {
      statusQueryData.update(userInput.getStatusQueryData(), locale);
    }

    if (this.sealQueryData != null) {
      sealQueryData.update(userInput.getSealQueryData(), locale);
    }

    userInput.setNoAssignements(this.noAssignements);
  }

  /** {@inheritDoc} */
  public void validate(Locale locale) {
    if (queryFirstLevels != null) {
      for (QFirstLevelXML firstLevelXML : queryFirstLevels) {
        firstLevelXML.validate(locale);
      }
    }

    if (timeSpanQueryData != null) {
      timeSpanQueryData.validate(locale);
    }

    if (statusQueryData != null) {
      statusQueryData.validate(locale);
    }

    if (sealQueryData != null) {
      sealQueryData.validate(locale);
    }
  }

}
