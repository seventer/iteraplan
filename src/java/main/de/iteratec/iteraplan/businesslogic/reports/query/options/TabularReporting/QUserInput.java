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
import java.util.Locale;

import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.TechnicalComponentReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.model.attribute.BBAttribute;


/**
 * This class holds the data of one particular query form.<br>
 * A query form contains the individual query conditions for a particular {@code Type}.
 * <p>
 * <ul>
 * <li>The query conditions for the fixed and user defined attributes may be retrieved calling
 * {@link #getQueryFirstLevels()}.</li>
 * <li>Query conditions regarding status and timespan (if applicable) may be retrieved by calling
 * {@link #getStatusQueryData()} or {@link #getTimespanQueryData()}.</li>
 * </ul>
 */
public class QUserInput implements Serializable {
  private static final long serialVersionUID = -677335752517363441L;

  private List<QFirstLevel> queryFirstLevels = new ArrayList<QFirstLevel>();
  private Boolean           noAssignements   = Boolean.FALSE;

  private IQStatusData      statusQueryData;
  private QSealStatus       sealQueryData;
  private QTimespanData     timespanQueryData;
  private MassUpdateData    massUpdateData;

  /**
   * Default constructor.
   */
  public QUserInput() {
    initQueryFirstLevels();
  }

  /**
   * Initializes or resets the model that holds the query criterias regarding fixed or user defined
   * attributes.
   */
  private void initQueryFirstLevels() {
    QFirstLevel qfl = new QFirstLevel();
    QPart qp = new QPart();
    qfl.getQuerySecondLevels().add(qp);
    this.queryFirstLevels.add(qfl);
  }

  public void initStatusQueryData(Type<?> type) {
    if (type instanceof InformationSystemReleaseTypeQu) {
      statusQueryData = new QStatusDataInformationSystemRelease();
    }
    else if (type instanceof TechnicalComponentReleaseTypeQu) {
      statusQueryData = new QStatusDataTechnicalComponentRelease();
    }
  }

  public void initSealQueryData(Type<?> type) {
    sealQueryData = new QSealStatus();
  }

  public void initTimespanQueryData(Locale locale) {
    timespanQueryData = new QTimespanData(locale);
  }

  /**
   * Returns a list of {@link QFirstLevel} objects, that again contain a list of {@link QPart}
   * objects. This structure takes on all user entered data. All QFirstLevel instances are treated
   * as being AND connected.
   * 
   * @return list of first level queries.
   */
  public List<QFirstLevel> getQueryFirstLevels() {
    return queryFirstLevels;
  }

  /**
   * Set the list of first level queries.
   * 
   * @param firstLevels
   * @see #getQueryFirstLevels()
   */
  public void setQueryFirstLevels(List<QFirstLevel> firstLevels) {
    queryFirstLevels = firstLevels;
  }

  public IQStatusData getStatusQueryData() {
    return statusQueryData;
  }

  public QSealStatus getSealQueryData() {
    return sealQueryData;
  }

  public QTimespanData getTimespanQueryData() {
    return timespanQueryData;
  }

  /**
   * If the user set this to true, no instances of building block type this form is about must be
   * assigned to the building block instances the query applies to. Applies only to report forms
   * which represent an extension.
   * 
   * @return true if the current building block type must not be assigned.
   */
  public Boolean getNoAssignements() {
    return noAssignements;
  }

  /**
   * Sets the flag whether instances of the current building block type should be assigned.
   * 
   * @param notAssigned
   *          Whether instances of the current building block type should be assigned.
   * @see #getNoAssignements()
   */
  public void setNoAssignements(Boolean notAssigned) {
    this.noAssignements = notAssigned;
  }

  /**
   * Checks the model contained in this class to see whether the user has made any input regarding
   * status, timespan or attributes of the current building block type.
   * 
   * @return true iff there are any query criteries for the current building block type.
   * @throws de.iteratec.iteraplan.common.error.IteraplanTechnicalException
   */
  public boolean isUserInputAvailable() {
    if (statusQueryData != null || isTimespanSet() || isSealStateSet()) {
      return true;
    }

    if (queryFirstLevels != null) {
      for (QFirstLevel level : queryFirstLevels) {
        List<QPart> secondLevels = level.getQuerySecondLevels();
        if (secondLevels != null) {
          for (QPart part : secondLevels) {
            String chosenAttrId = part.getChosenAttributeStringId();
            String chosenAttrType = BBAttribute.getTypeByStringId(chosenAttrId);
            if (chosenAttrType != null && !chosenAttrType.equals(BBAttribute.BLANK_ATTRIBUTE_TYPE)) {
              return true;
            }
          }
        }
      }
    }

    return false;
  }

  private boolean isTimespanSet() {
    return timespanQueryData != null && (timespanQueryData.isStartDateSet() || timespanQueryData.isEndDateSet());
  }
  
  /**
   * Returns {@code true} if the {@link #sealQueryData} is set and contains at least one selected item.
   * 
   * @return a flag indicating if the seal query data is set
   */
  private boolean isSealStateSet() {
    return sealQueryData != null && !sealQueryData.getSelectedStatus().isEmpty();
  }

  public void intiMassUpdateData(MassUpdateType type) {
    this.massUpdateData = new MassUpdateDataImpl(type);
  }

  public MassUpdateData getMassUpdateData() {
    return massUpdateData;
  }

  public void setMassUpdateData(MassUpdateData data) {
    this.massUpdateData = data;
  }
}
