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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QPart;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.common.util.NamedId;
import de.iteratec.iteraplan.model.attribute.BBAttribute;


/**
 * XML dto for (un)marshalling {@link QPart}s
 *
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 */
@XmlType(name = "queryPart")
public class QPartXML extends AbstractXMLElement<QPart> {

  private String  chosenAttributeStringId;

  private Integer chosenOperationId;

  private String  freeTextCriteria;

  private String  existingCriteria;

  private Boolean freeTextCriteriaSelected;

  private Boolean dateATSelected;

  private Boolean selectFieldsDisabled;

  /**
   * {@inheritDoc}
   */
  public void initFrom(QPart queryElement, Locale locale) {

    this.chosenAttributeStringId = queryElement.getChosenAttributeStringId();
    this.freeTextCriteriaSelected = queryElement.getFreeTextCriteriaSelected();
    this.dateATSelected = queryElement.getDateATSelected();
    this.chosenOperationId = queryElement.getChosenOperationId();
    this.selectFieldsDisabled = queryElement.getSelectFieldsDisabled();

    if (StringUtils.isNotEmpty(chosenAttributeStringId)) {
      String type = BBAttribute.getTypeByStringId(chosenAttributeStringId);

      handleAttribute(queryElement, locale, type);
    }
  }

  private void handleAttribute(QPart queryElement, Locale locale, String type) {
    if (freeTextCriteriaSelected.booleanValue()) {
      if (StringUtils.isNotEmpty(queryElement.getFreeTextCriteria())) {
        this.freeTextCriteria = getProcessedCriteria(queryElement.getFreeTextCriteria(), locale, type);
      }
    }
    else {
      if (StringUtils.isNotEmpty(queryElement.getExistingCriteria())) {
        this.existingCriteria = getProcessedCriteria(queryElement.getExistingCriteria(), locale, type);
      }
    }
  }

  private String getProcessedCriteria(String criteria, Locale locale, String type) {
    // numeric attribute -> convert to unlocalised value
    if (BBAttribute.USERDEF_NUMBER_ATTRIBUTE_TYPE.equals(type)) {
      return getUnlocalizedNumber(criteria, locale);
    }
    // date attribute -> convert to unlocalised value
    else if (BBAttribute.USERDEF_DATE_ATTRIBUTE_TYPE.equals(type) || BBAttribute.FIXED_ATTRIBUTE_DATETYPE.equals(type)) {
      this.dateATSelected = Boolean.TRUE;
      return getUnlocalizedTime(criteria, locale);
    }
    // remaining attributes -> simply copy the values
    else {
      return criteria;
    }
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#update(java.lang.Object,
   * java.util.Locale)
   */
  public void update(QPart queryElement, Locale locale) {
    // copy from XML dto to component model
    queryElement.setChosenAttributeStringId(chosenAttributeStringId);
    queryElement.setChosenOperationId(chosenOperationId);
    queryElement.setFreeTextCriteria(freeTextCriteria);
    queryElement.setExistingCriteria(existingCriteria);
    queryElement.setFreeTextCriteriaSelected(freeTextCriteriaSelected);
    queryElement.setDateATSelected(dateATSelected);
    queryElement.setSelectFieldsDisabled(selectFieldsDisabled);
  }

  @XmlElement
  public String getChosenAttributeStringId() {
    return chosenAttributeStringId;
  }

  @XmlElement
  public Integer getChosenOperationId() {
    return chosenOperationId;
  }

  @XmlElement
  public String getFreeTextCriteria() {
    return freeTextCriteria;
  }

  @XmlElement
  public String getExistingCriteria() {
    return existingCriteria;
  }

  @XmlElement
  public Boolean getFreeTextCriteriaSelected() {
    return freeTextCriteriaSelected;
  }

  @XmlElement
  public Boolean getDateATSelected() {
    return dateATSelected;
  }

  public void setChosenAttributeStringId(String chosenAttributeStringId) {
    this.chosenAttributeStringId = chosenAttributeStringId;
  }

  public void setChosenOperationId(Integer chosenOperationId) {
    this.chosenOperationId = chosenOperationId;
  }

  public void setFreeTextCriteria(String freeTextCriteria) {
    this.freeTextCriteria = freeTextCriteria;
  }

  public void setExistingCriteria(String existingCriteria) {
    this.existingCriteria = existingCriteria;
  }

  public void setFreeTextCriteriaSelected(Boolean freeTextCriteriaSelected) {
    this.freeTextCriteriaSelected = freeTextCriteriaSelected;
  }

  public void setDateATSelected(Boolean dateATSelected) {
    this.dateATSelected = dateATSelected;
  }

  public void setSelectFieldsDisabled(Boolean selectFieldsDisabled) {
    this.selectFieldsDisabled = selectFieldsDisabled;
  }

  /**
   * {@inheritDoc}
   */
  public void validate(Locale locale) {
    if (StringUtils.isEmpty(chosenAttributeStringId)) {
      return;
    }

    if (!BBAttribute.isStringIdNameValid(chosenAttributeStringId, true)) {
      logError(chosenAttributeStringId + " is not a valid attribute");
    }
    String type = BBAttribute.getTypeByStringId(chosenAttributeStringId);

    if (BBAttribute.USERDEF_DATE_ATTRIBUTE_TYPE.equals(type) || BBAttribute.FIXED_ATTRIBUTE_DATETYPE.equals(type)) {
      validateDateAttribute(locale, type);
      this.dateATSelected = Boolean.TRUE;
    }
    else {
      this.dateATSelected = Boolean.FALSE;

      if (BBAttribute.USERDEF_NUMBER_ATTRIBUTE_TYPE.equals(type)) {
        validateNumberAttribute(locale, type);
      }
      else if (BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE.equals(type)) {
        // validate enum attribute
        validateOperation(chosenOperationId, DynamicQueryFormData.getAllUserdefEnumOperators(), type);
      }
      else if (BBAttribute.USERDEF_RESPONSIBILITY_ATTRIBUTE_TYPE.equals(type)) {
        // validate responsibility attribute
        validateOperation(chosenOperationId, DynamicQueryFormData.getAllUserdefResponsibilityOperators(), type);
      }
      else if (BBAttribute.USERDEF_TEXT_ATTRIBUTE_TYPE.equals(type)) {
        // validate text attribute
        validateOperation(chosenOperationId, DynamicQueryFormData.getAllUserdefTextOperators(), type);
      }
      else if (BBAttribute.FIXED_ATTRIBUTE_TYPE.equals(type)) {
        // validate fixed attribute
        validateOperation(chosenOperationId, DynamicQueryFormData.getAllFixedOperators(), type);
      }
    }
  }

  private void validateNumberAttribute(Locale locale, String type) {
    validateOperation(chosenOperationId, DynamicQueryFormData.getAllUserdefNumberOperators(), type);
    if (freeTextCriteriaSelected.booleanValue()) {
      if (StringUtils.isNotEmpty(freeTextCriteria)) {
        this.freeTextCriteria = getLocalizedNumber(freeTextCriteria, locale);
      }
    }
    else {
      if (StringUtils.isNotEmpty(existingCriteria)) {
        this.existingCriteria = getLocalizedNumber(existingCriteria, locale);
      }
    }
  }

  private void validateDateAttribute(Locale locale, String type) {
    validateOperation(chosenOperationId, DynamicQueryFormData.getAllUserdefDateOperators(), type);
    if (freeTextCriteriaSelected.booleanValue()) {
      if (StringUtils.isNotEmpty(freeTextCriteria)) {
        this.freeTextCriteria = getLocalizedDate(freeTextCriteria, locale);
      }
    }
    else {
      if (StringUtils.isNotEmpty(existingCriteria)) {
        this.existingCriteria = getLocalizedDate(existingCriteria, locale);
      }
    }
  }

  private String getLocalizedNumber(String unlocalizedNumber, Locale locale) {
    try {
      BigDecimal bigDecimal = new BigDecimal(unlocalizedNumber);
      return BigDecimalConverter.format(bigDecimal, locale);
    } catch (Exception e) {
      logError("Illegal number value '" + unlocalizedNumber + "' found for attribute ", e);
    }
    return ""; // will never be reached but needed here to satisfy the compiler
  }

  private String getLocalizedDate(String unlocalizedDate, Locale locale) {
    try {
      Date date = new Date(Long.parseLong(unlocalizedDate));
      return DateUtils.formatAsString(date, locale);
    } catch (Exception e) {
      logError("Illegal date value '" + unlocalizedDate + "' found for attribute ", e);
    }
    return ""; // will never be reached but needed here to satisfy the compiler
  }

  private String getUnlocalizedTime(String localizedDate, Locale locale) {
    Date date = DateUtils.parseAsDate(localizedDate, locale);
    return Long.toString(date.getTime());
  }

  private String getUnlocalizedNumber(String localizedNumber, Locale locale) {
    BigDecimal number = BigDecimalConverter.parse(localizedNumber, locale);
    return number.toString();
  }

  private void validateOperation(Integer operationId, List<NamedId> allOperators, String type) {
    for (NamedId nameId : allOperators) {
      if (nameId.getId().equals(operationId)) {
        return;
      }
    }
    logError("Operation id '" + operationId + "' not valid for type '" + type + "'");
  }
}
