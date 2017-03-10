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

import org.apache.commons.lang.StringUtils;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.common.util.NamedId;
import de.iteratec.iteraplan.model.attribute.BBAttribute;


/**
 * Represents one 'line' of a query form. This includes the chosen attribute, the chosen operation
 * (i.e. 'contains', 'starts with', etc.) and the value (pattern) the attribute should match. The
 * latter one is either comes from a free text field or from a drop down list with existing values.
 */
public class QPart implements Serializable {

  private static final long serialVersionUID = -62150555764659166L;

  /** @see #getChosenAttributeStringId() */
  private String            chosenAttributeStringId;

  /** @see #getChosenOperationId() */
  private Integer           chosenOperationId;

  /** @see #getFreeTextCriteria() */
  private String            freeTextCriteria;

  /** @see #getExistingCriteria() */
  private String            existingCriteria;

  /** @see #getFreeTextCriteriaSelected() */
  private Boolean           freeTextCriteriaSelected;

  /** @see #getDateATSelected() */
  private Boolean           dateATSelected;

  /** @see #getNumberATSelected() */
  private Boolean           numberATSelected;

  /** @see #getSelectFieldsDisabled() */
  private Boolean           selectFieldsDisabled;

  /**
   * Default constructor.
   */
  public QPart() {
    freeTextCriteriaSelected = Boolean.TRUE;
    dateATSelected = Boolean.FALSE;
    selectFieldsDisabled = Boolean.FALSE;
  }

  /**
   * The string id of the chosen BBAttribute. Each attribute (fixed or user defined) has an id. The
   * string representation of the id of the attribute for which the query criterias in this class
   * apply is returned by this method. The available attributes are held in the enclosing
   * DynamicQueryFormData instance.
   * 
   * @return the string id of the chosen BBAttribute.
   */
  public String getChosenAttributeStringId() {
    return chosenAttributeStringId;
  }

  /**
   * Returns the id of the chosen operation (also called operators). The list of available
   * operations are held in the enclosing DynamicQueryFormData instance.
   * 
   * @return id of the chosen operation
   */
  public Integer getChosenOperationId() {
    return chosenOperationId;
  }

  /**
   * Creates a copy of this instance.
   * 
   * @return a copy.
   */
  public QPart getCopy() {
    QPart newQpart = new QPart();
    newQpart.setChosenAttributeStringId(chosenAttributeStringId);
    newQpart.setChosenOperationId(chosenOperationId);
    newQpart.setFreeTextCriteria(freeTextCriteria);
    newQpart.setExistingCriteria(existingCriteria);
    newQpart.setFreeTextCriteriaSelected(freeTextCriteriaSelected);
    newQpart.setDateATSelected(dateATSelected);
    newQpart.setSelectFieldsDisabled(selectFieldsDisabled);
    return newQpart;
  }

  /**
   * @return true if a dateAT is selected
   */
  public Boolean getDateATSelected() {
    return dateATSelected;
  }

  /**
   * @return true if a numberAT is selected
   */
  public Boolean getNumberATSelected() {
    return numberATSelected;
  }

  /**
   * The criteria the user has chosen from the drop down list. This is one of the existing attribute
   * values of the currently chosen attribute.
   * 
   * @return an existing attribute value the user has chosen
   */
  public String getExistingCriteria() {
    return existingCriteria;
  }

  /**
   * Gets the criteria an attribute value of the currently chosen attribute should have.
   * 
   * @return the user entered free text criteria.
   */
  public String getFreeTextCriteria() {
    return freeTextCriteria;
  }

  /**
   * The currently set free text critera should be evaluated. If this is false, the currently set
   * existing criteria value is evaluated.
   * 
   * @return true if the currently chosen free text criteria should be evaluted.
   * @see #getFreeTextCriteria()
   * @see #getExistingCriteria()
   */
  public Boolean getFreeTextCriteriaSelected() {
    return freeTextCriteriaSelected;
  }

  /**
   * Decides whether the selection fields in the jsp (DynamicForm.jsp) should be disabled
   * 
   * @return true if the selection fields should be disabled
   * @see #updateSelectFieldsDisabled()
   */
  public Boolean getSelectFieldsDisabled() {
    return selectFieldsDisabled;
  }

  /**
   * Returns the list of possible operators for the currently chosen attribute type. For example, if
   * a number attribute was chosen, operations relevant for this type are returned. The available
   * operators are held in the enclosing instance of {@link DynamicQueryFormData}.
   * 
   * @return See method description.
   */
  public List<NamedId> getOperators() {

    String typeString = BBAttribute.getTypeByStringId(getChosenAttributeStringId());

    if (BBAttribute.FIXED_ATTRIBUTE_ENUM.equals(typeString)) {
      return DynamicQueryFormData.getAllFixedEnumOperators();
    }
    else if (BBAttribute.FIXED_ATTRIBUTE_TYPE.equals(typeString)) {
      return DynamicQueryFormData.getAllFixedOperators();
    }
    else if (BBAttribute.FIXED_ATTRIBUTE_DATETYPE.equals(typeString)) {
      return DynamicQueryFormData.getAllUserdefDateOperators();
    }
    else if (BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE.equals(typeString)) {
      return DynamicQueryFormData.getAllUserdefEnumOperators();
    }
    else if (BBAttribute.USERDEF_NUMBER_ATTRIBUTE_TYPE.equals(typeString)) {
      return DynamicQueryFormData.getAllUserdefNumberOperators();
    }
    else if (BBAttribute.USERDEF_TEXT_ATTRIBUTE_TYPE.equals(typeString)) {
      return DynamicQueryFormData.getAllUserdefTextOperators();
    }
    else if (BBAttribute.USERDEF_DATE_ATTRIBUTE_TYPE.equals(typeString)) {
      return DynamicQueryFormData.getAllUserdefDateOperators();
    }
    else if (BBAttribute.USERDEF_RESPONSIBILITY_ATTRIBUTE_TYPE.equals(typeString)) {
      return DynamicQueryFormData.getAllUserdefResponsibilityOperators();
    }
    else if (BBAttribute.FIXED_ATTRIBUTE_SET.equals(typeString)) {
      return DynamicQueryFormData.getAllFixedOperators();
    }

    return new ArrayList<NamedId>();
  }

  /**
   * Returns the pattern the user entered, enriched with wildcard characters corresponding to the
   * chosen operation (start with, contains, etc..). The enrichment is only done for text attributes
   * (i.e.: not for number attributes).
   * 
   * @return the enriched pattern the user entered.
   */
  public String getPattern() {
    if (this.freeTextCriteriaSelected.booleanValue()) {
      return this.getProcessedCriteria(getFreeTextCriteria());
    }
    return this.getProcessedCriteria(getExistingCriteria());
  }

  /**
   * Set the chose attribute string id and set the flag dateATSelected dependent on the
   * chosenAttributeStringId to show or to hide the pop-up calendar in the jsp.
   * 
   * @param chosenAttributeStringId
   *          the string id of the chosen attribute.
   * @see #getChosenAttributeStringId()
   */
  public void setChosenAttributeStringId(String chosenAttributeStringId) {
    this.chosenAttributeStringId = chosenAttributeStringId;
    if ((null != this.chosenAttributeStringId)
        && ((this.chosenAttributeStringId.contains(BBAttribute.USERDEF_DATE_ATTRIBUTE_TYPE)) || (this.chosenAttributeStringId
            .contains(BBAttribute.FIXED_ATTRIBUTE_DATETYPE)))) {
      this.dateATSelected = Boolean.TRUE;
    }
    else {
      this.dateATSelected = Boolean.FALSE;
    }

    if ((null != this.chosenAttributeStringId) && this.chosenAttributeStringId.contains(BBAttribute.USERDEF_NUMBER_ATTRIBUTE_TYPE)) {
      this.numberATSelected = Boolean.TRUE;
    }
    else {
      this.numberATSelected = Boolean.FALSE;
    }
  }

  /**
   * Sets the id of the chosen operation. Updates selectFieldsDisabled
   * 
   * @param chosenOperationId
   *          id of the chosen operation
   * @see #updateSelectFieldsDisabled()
   */
  public void setChosenOperationId(Integer chosenOperationId) {
    this.chosenOperationId = chosenOperationId;
    updateSelectFieldsDisabled();
  }

  public void setDateATSelected(Boolean dateATSelected) {
    this.dateATSelected = dateATSelected;
  }

  /**
   * Sets the value for the disable instruction of the selecting fields in the jsp (DynamicForm.jsp)
   * 
   * @param selectFieldsDisabled
   */
  public void setSelectFieldsDisabled(Boolean selectFieldsDisabled) {
    this.selectFieldsDisabled = selectFieldsDisabled;
  }

  /**
   * Method to decide whether the selecting fields in DynamicForm.jsp are disabled or not
   */
  private void updateSelectFieldsDisabled() {
    if (chosenOperationId == null) {
      selectFieldsDisabled = Boolean.FALSE;
      return;
    }
    else {
      if (chosenOperationId.equals(Constants.OPERATION_ANYENTRIES_ID) || chosenOperationId.equals(Constants.OPERATION_NOENTRIES_ID)) {
        selectFieldsDisabled = Boolean.TRUE;
        return;
      }
      selectFieldsDisabled = Boolean.FALSE;
    }
  }

  /**
   * Set an existing attribute value of the currently chosen attribute.
   * 
   * @param existingCriteria
   *          set an existing attribute value of the currently chosen attribute.
   * @see #getExistingCriteria()
   */
  public void setExistingCriteria(String existingCriteria) {
    this.existingCriteria = existingCriteria;
  }

  /**
   * Set a criteria an attribute value of the currently chosen attribute should have. If the current
   * attribute is a numberAT, localize the number.
   * 
   * @param freeTextCriteria
   *          a free text critera
   */
  public void setFreeTextCriteria(String freeTextCriteria) {
    if (Boolean.TRUE.equals(numberATSelected) && !StringUtils.isEmpty(freeTextCriteria)) {
      Locale locale = UserContext.getCurrentLocale();
      try {
        this.freeTextCriteria = BigDecimalConverter.format(BigDecimalConverter.parse(freeTextCriteria, true, locale), true, locale);
      } catch (IteraplanBusinessException e) {
        if (e.getErrorCode() == IteraplanErrorMessages.INCORRECT_BIGDECIMAL_FORMAT) {
          this.freeTextCriteria = "";
        }
        else {
          throw e;
        }
      }
    }
    else {
      this.freeTextCriteria = freeTextCriteria;
    }
  }

  /**
   * Set true or false depending on whether the free text or the existing criteria value should be
   * evaluated.
   * 
   * @param freeTextCriteriaSelected
   *          true or false depending on which criteria value should be evaluated.
   */
  public void setFreeTextCriteriaSelected(Boolean freeTextCriteriaSelected) {
    this.freeTextCriteriaSelected = freeTextCriteriaSelected;
  }

  /**
   * Returns true, if the pattern string is empty.
   * 
   * @return True, if the pattern string is empty. Otherwise, false.
   */
  public boolean isPatternEmpty(String pattern) {
    return (pattern.length() == 0) || ((pattern.length() == 1) && (pattern.charAt(0) == '*'))
        || ((pattern.charAt(0) == '*') && (pattern.charAt(1) == '*'));
  }

  /**
   * Depending on which operation was chosen, the free text entry is post-processed to include
   * wildcards as necessary.
   * 
   * @return processed text criteria
   */
  private String getProcessedCriteria(String criteria) {
    String wc = Constants.GUI_WILDCARD;
    if (Constants.OPERATION_CONTAINS_ID.equals(chosenOperationId)) {
      return wc + criteria + wc;
    }
    else if (Constants.OPERATION_CONTAINSNOT_ID.equals(chosenOperationId)) {
      return wc + criteria + wc;
    }
    else if (Constants.OPERATION_ENDSWITH_ID.equals(chosenOperationId)) {
      return wc + criteria;
    }
    else if (Constants.OPERATION_STARTSWITH_ID.equals(chosenOperationId)) {
      return criteria + wc;
    }
    return criteria;
  }

}
