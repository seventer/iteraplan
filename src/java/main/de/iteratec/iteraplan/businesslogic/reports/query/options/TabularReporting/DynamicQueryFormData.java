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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Collections2;

import de.iteratec.iteraplan.businesslogic.reports.query.type.IPresentationExtension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.common.util.NamedId;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.util.TimeseriesHelper;


/**
 * Model for all query conditions concerning a certain BuildingBlock
 * {@link de.iteratec.iteraplan.businesslogic.reports.query.type.Type}. <br>
 * <br>
 * The <code>AND</code> and <code>OR</code> connected query conditions are held in the
 * {@link #getQueryUserInput()} field. The other fields in this class hold meta data about the query
 * as well as constants that are used to create the GUI form.
 * 
 * @param <T>
 *          The type of BuildingBlock for which the query is constructed.
 * @see de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportBeanBase
 */
public class DynamicQueryFormData<T extends BuildingBlock> implements Serializable {
  private static final long             serialVersionUID                     = 3013661019284565912L;

  /** @see #getQueryUserInput() */
  private QUserInput                    queryUserInput                       = null;

  /** @see #getType() */
  private Type<T>                       type                                 = null;

  /** @see #getMassUpdateType() */
  private MassUpdateType                massUpdateType                       = null;

  /** @see #getExtension() */
  private IPresentationExtension        extension                            = null;

  /** @see #getTimeSpanDescription() */
  private String                        timeSpanDescription                  = null;

  /** @see #getAvailableAttributes() */
  private final List<BBAttribute>       availableAttributes;

  /** @see #getDimensionAttributes() */
  private List<BBAttribute>             dimensionAttributes                  = new ArrayList<BBAttribute>();

  /** @see #getAllFixedOperators() */
  private static final List<NamedId>    ALL_FIXED_OPERATORS                  = new ArrayList<NamedId>();

  /** @see #getAllUserdefEnumOperators() */
  private static final List<NamedId>    ALL_USERDEF_ENUM_OPERATORS           = new ArrayList<NamedId>();

  /** @see #getAllUserdefResponsibilityOperators() */
  private static final List<NamedId>    ALL_USERDEF_RESPONSIBILITY_OPERATORS = new ArrayList<NamedId>();

  /** @see #getAllUserdefNumberOperators() */
  private static final List<NamedId>    ALL_USERDEF_NUMBER_OPERATORS         = new ArrayList<NamedId>();

  /** @see #getAllUserdefDateOperators() */
  private static final List<NamedId>    ALL_USERDEF_DATE_OPERATORS           = new ArrayList<NamedId>();

  /** @see #getAllUserdefTextOperators() */
  private static final List<NamedId>    ALL_USERDEF_TEXT_OPERATORS           = new ArrayList<NamedId>();

  /** @see #getAllFixedEnumOperators() */
  private static final List<NamedId>    ALL_FIXED_ENUM_OPERATORS             = new ArrayList<NamedId>();

  /** @see #getAvailableAttributeValues() */
  private Map<String, List<NamedId>>    availableAttributeValues             = new HashMap<String, List<NamedId>>();

  /** @see #getSecondLevelQueryForms() */
  private List<DynamicQueryFormData<?>> secondLevelQueryForms                = new ArrayList<DynamicQueryFormData<?>>();

  static {

    // The {@code NamedId} class is used to represent operations. It carries the ID and the name
    // of the operation. The field 'decription' is used to mark 'not' operations. These are
    // currently
    // not provided for queries that span 'invisible' types, i.e. types that cannot be queried via
    // the
    // GUI e.g. {@code Transports}s. Operations that are flagged as such will be ommitted by the JSP
    // if this form represents a critical extension.
    String notOperationFlag = "notOperation";

    NamedId containsOp = new NamedId(Constants.OPERATION_CONTAINS_ID, Constants.OPERATION_CONTAINS_NAME, null);
    NamedId containsNotOp = new NamedId(Constants.OPERATION_CONTAINSNOT_ID, Constants.OPERATION_CONTAINSNOT_NAME, notOperationFlag);
    NamedId startsWithOp = new NamedId(Constants.OPERATION_STARTSWITH_ID, Constants.OPERATION_STARTSWITH_NAME, null);
    NamedId endsWithOp = new NamedId(Constants.OPERATION_ENDSWITH_ID, Constants.OPERATION_ENDSWITH_NAME, null);
    NamedId equalsOp = new NamedId(Constants.OPERATION_EQUALS_ID, Constants.OPERATION_EQUALS_NAME, null);
    NamedId equalsNotOp = new NamedId(Constants.OPERATION_EQUALSNOT_ID, Constants.OPERATION_EQUALSNOT_NAME, notOperationFlag);
    NamedId noEntriesOp = new NamedId(Constants.OPERATION_NOENTRIES_ID, Constants.OPERATION_NOENTRIES_NAME, null);
    NamedId anyEntriesOp = new NamedId(Constants.OPERATION_ANYENTRIES_ID, Constants.OPERATION_ANYENTRIES_NAME, null);
    NamedId gtOp = new NamedId(Constants.OPERATION_GT_ID, Constants.OPERATION_GT_NAME, null);
    NamedId geqOp = new NamedId(Constants.OPERATION_GEQ_ID, Constants.OPERATION_GEQ_NAME, null);
    NamedId eqOp = new NamedId(Constants.OPERATION_EQ_ID, Constants.OPERATION_EQ_NAME, null);
    NamedId leqOp = new NamedId(Constants.OPERATION_LEQ_ID, Constants.OPERATION_LEQ_NAME, null);
    NamedId ltOp = new NamedId(Constants.OPERATION_LT_ID, Constants.OPERATION_LT_NAME, null);

    NamedId on = new NamedId(Constants.OPERATION_ON_ID, Constants.OPERATION_ON_NAME, null);
    NamedId before = new NamedId(Constants.OPERATION_BEFORE_ID, Constants.OPERATION_BEFORE_NAME, null);
    NamedId after = new NamedId(Constants.OPERATION_AFTER_ID, Constants.OPERATION_AFTER_NAME, null);

    ALL_FIXED_OPERATORS.add(containsOp);
    ALL_FIXED_OPERATORS.add(containsNotOp);
    ALL_FIXED_OPERATORS.add(startsWithOp);
    ALL_FIXED_OPERATORS.add(endsWithOp);
    ALL_FIXED_OPERATORS.add(equalsOp);
    ALL_FIXED_OPERATORS.add(equalsNotOp);
    ALL_FIXED_OPERATORS.add(anyEntriesOp);
    ALL_FIXED_OPERATORS.add(noEntriesOp);

    ALL_USERDEF_ENUM_OPERATORS.add(containsOp);
    ALL_USERDEF_ENUM_OPERATORS.add(containsNotOp);
    ALL_USERDEF_ENUM_OPERATORS.add(startsWithOp);
    ALL_USERDEF_ENUM_OPERATORS.add(endsWithOp);
    ALL_USERDEF_ENUM_OPERATORS.add(equalsOp);
    ALL_USERDEF_ENUM_OPERATORS.add(equalsNotOp);
    ALL_USERDEF_ENUM_OPERATORS.add(noEntriesOp);
    ALL_USERDEF_ENUM_OPERATORS.add(anyEntriesOp);

    ALL_USERDEF_RESPONSIBILITY_OPERATORS.add(containsOp);
    ALL_USERDEF_RESPONSIBILITY_OPERATORS.add(containsNotOp);
    ALL_USERDEF_RESPONSIBILITY_OPERATORS.add(startsWithOp);
    ALL_USERDEF_RESPONSIBILITY_OPERATORS.add(endsWithOp);
    ALL_USERDEF_RESPONSIBILITY_OPERATORS.add(equalsOp);
    ALL_USERDEF_RESPONSIBILITY_OPERATORS.add(equalsNotOp);
    ALL_USERDEF_RESPONSIBILITY_OPERATORS.add(noEntriesOp);
    ALL_USERDEF_RESPONSIBILITY_OPERATORS.add(anyEntriesOp);

    ALL_USERDEF_NUMBER_OPERATORS.add(gtOp);
    ALL_USERDEF_NUMBER_OPERATORS.add(geqOp);
    ALL_USERDEF_NUMBER_OPERATORS.add(eqOp);
    ALL_USERDEF_NUMBER_OPERATORS.add(leqOp);
    ALL_USERDEF_NUMBER_OPERATORS.add(ltOp);
    ALL_USERDEF_NUMBER_OPERATORS.add(noEntriesOp);
    ALL_USERDEF_NUMBER_OPERATORS.add(anyEntriesOp);

    ALL_USERDEF_TEXT_OPERATORS.add(containsOp);
    ALL_USERDEF_TEXT_OPERATORS.add(containsNotOp);
    ALL_USERDEF_TEXT_OPERATORS.add(startsWithOp);
    ALL_USERDEF_TEXT_OPERATORS.add(endsWithOp);
    ALL_USERDEF_TEXT_OPERATORS.add(equalsOp);
    ALL_USERDEF_TEXT_OPERATORS.add(equalsNotOp);
    ALL_USERDEF_TEXT_OPERATORS.add(noEntriesOp);
    ALL_USERDEF_TEXT_OPERATORS.add(anyEntriesOp);

    ALL_USERDEF_DATE_OPERATORS.add(on);
    ALL_USERDEF_DATE_OPERATORS.add(before);
    ALL_USERDEF_DATE_OPERATORS.add(after);
    ALL_USERDEF_DATE_OPERATORS.add(noEntriesOp);
    ALL_USERDEF_DATE_OPERATORS.add(anyEntriesOp);

    ALL_FIXED_ENUM_OPERATORS.add(equalsOp);
    ALL_FIXED_ENUM_OPERATORS.add(equalsNotOp);
    ALL_FIXED_ENUM_OPERATORS.add(noEntriesOp);
    ALL_FIXED_ENUM_OPERATORS.add(anyEntriesOp);
  }

  /**
   * Constructor.
   * 
   * @param availableAttributes
   *          The attributes of the type represented by {@link BBAttribute}s.
   * @param type
   *          The <tt>Type</tt> for which this query form is constructed.
   */
  public DynamicQueryFormData(List<BBAttribute> availableAttributes, Type<T> type, Locale locale) {
    this.availableAttributes = availableAttributes;
    this.type = type;
    this.queryUserInput = new QUserInput();

    if (type.isHasStatus()) {
      this.queryUserInput.initStatusQueryData(type);
    }

    if (type.isHasSeal()) {
      this.queryUserInput.initSealQueryData(type);
    }

    if (type.isHasTimespan()) {
      this.queryUserInput.initTimespanQueryData(locale);
      this.timeSpanDescription = type.getTimespanPresentationKey();
    }
  }

  /**
   * Returns operators for fixed attributes, such as name and description. Currently these can only
   * be used for attributes of type String.
   * 
   * @return all fixed operators
   */
  public static List<NamedId> getAllFixedOperators() {
    return ALL_FIXED_OPERATORS;
  }

  /**
   * Returns operators for user defined enum attributes.
   * 
   * @return operators for user defined enum attributes
   */
  public static List<NamedId> getAllUserdefEnumOperators() {
    return ALL_USERDEF_ENUM_OPERATORS;
  }

  /**
   * Returns operators for fixed enum attributes.
   * 
   * @return operators for fixed enum attributes
   */
  public static List<NamedId> getAllFixedEnumOperators() {
    return ALL_FIXED_ENUM_OPERATORS;
  }

  /**
   * Returns operators for user defined responsibility attributes.
   * 
   * @return operators for user defined responsibility attributes
   */
  public static List<NamedId> getAllUserdefResponsibilityOperators() {
    return ALL_USERDEF_RESPONSIBILITY_OPERATORS;
  }

  /**
   * Returns operators for user defined number attributes.
   * 
   * @return operators for user defined number attributes.
   */
  public static List<NamedId> getAllUserdefNumberOperators() {
    return ALL_USERDEF_NUMBER_OPERATORS;
  }

  /**
   * Returns operators for user defined date attributes.
   * 
   * @return operators for user defined date attributes.
   */
  public static List<NamedId> getAllUserdefDateOperators() {
    return ALL_USERDEF_DATE_OPERATORS;
  }

  /**
   * Returns operators for user defined free text attributes.
   * 
   * @return operators for user defined free text attributes.
   */
  public static List<NamedId> getAllUserdefTextOperators() {
    return ALL_USERDEF_TEXT_OPERATORS;
  }

  /**
   * Returns a specific attribute from the list returned by {@link #getAvailableAttributes()}.
   * 
   * @param index
   *          The index of the attribute to get.
   * @return The attribute with the given index.
   */
  public BBAttribute getAvailableAttribute(String index) {
    if ((index == null) || (availableAttributes == null)) {
      return null;
    }
    int intIndex = Integer.parseInt(index);
    return getAvailableAttributes().get(intIndex);
  }

  /**
   * Returns a list of {@link BBAttribute}s that the user can select. These represent all attributes
   * (fixed and user defined) which are associated with the type this form is constructed for.
   * 
   * @return list of available attributes
   */
  public List<BBAttribute> getAvailableAttributes() {
    return availableAttributes;
  }

  public Collection<BBAttribute> getAvailableTimeseriesAttributes() {
    return Collections2.filter(getAvailableAttributes(), TimeseriesHelper.BBATTRIBUTE_TIMESERIES_PREDICATE);
  }

  public boolean isTimeseriesAttributesAvailable() {
    return getAvailableTimeseriesAttributes().size() > 1; // default entry is always available
  }

  /**
   * Returns a list of userdefined attributes
   * 
   * @return The user defined attributes
   */
  public List<BBAttribute> getAvailableUserDefinedAttributes() {
    List<BBAttribute> ret = new ArrayList<BBAttribute>();
    for (BBAttribute attribute : getAvailableAttributes()) {
      if (!attribute.getType().equals(BBAttribute.FIXED_ATTRIBUTE_TYPE) && !attribute.getType().equals(BBAttribute.BLANK_ATTRIBUTE_TYPE)
          && !attribute.getType().equals(BBAttribute.FIXED_ATTRIBUTE_DATETYPE)) {
        ret.add(attribute);
      }
    }
    return ret;
  }

  /**
   * Returns a list of {@link BBAttribute}s that the user can select. These represent all attributes
   * (fixed and user defined) which are associated with the type this form is constructed for and
   * which are available for dimension definition.
   * 
   * @return list of available attributes
   */
  public List<BBAttribute> getDimensionAttributes() {
    return dimensionAttributes;
  }

  /**
   * Returns a list of {@link BBAttribute}s as in {@link #getDimensionAttributes()}, but filters the
   * attributes with possible multiple values and returns only single value attributes.
   * 
   * @return list of available single value attributes
   */
  public List<BBAttribute> getSingleValueDimensionAttributes() {
    List<BBAttribute> availableDimensionAttributes = getDimensionAttributes();
    List<BBAttribute> singleValueAttributes = CollectionUtils.arrayList();
    for (BBAttribute attr : availableDimensionAttributes) {
      if (!attr.isMultiValue()) {
        singleValueAttributes.add(attr);
      }
    }
    return singleValueAttributes;
  }

  /**
   * Returns a specific attribute from the list returned by {@link #getDimensionAttributes()}.
   * 
   * @param index
   *          The index of the attribute to get.
   * @return The attribute with the given index.
   */
  public BBAttribute getDimensionAttribute(String index) {
    if ((index == null) || (dimensionAttributes == null)) {
      return null;
    }
    int intIndex = Integer.parseInt(index);
    return getDimensionAttributes().get(intIndex);
  }

  /**
   * @see #getDimensionAttributes()
   * @param dimensionAttributes
   *          list of available attributes
   */
  public void setDimensionAttributes(List<BBAttribute> dimensionAttributes) {
    this.dimensionAttributes = dimensionAttributes;
  }

  /**
   * Returns a map with maps attribute ids to a lists of Strings that are the possible values for
   * that attribute.
   * 
   * @return map of available attribute values.
   */
  public Map<String, List<NamedId>> getAvailableAttributeValues() {
    return availableAttributeValues;
  }

  /**
   * Sets the available attributes.
   * 
   * @param availableAttributeValues
   *          the map of available attribute values to set.
   * @see #getAvailableAttributeValues()
   */
  public void setAvailableAttributeValues(Map<String, List<NamedId>> availableAttributeValues) {
    this.availableAttributeValues = availableAttributeValues;
  }

  /**
   * Returns all user input regarding query conditions. This data is also used to generate a query
   * tree for this form which is passed to the business logic.
   * 
   * @return the user input regarding query conditions
   */
  public QUserInput getQueryUserInput() {
    return queryUserInput;
  }

  /**
   * If this form is an extension of the main form, the respective extension object is returned.
   * 
   * @return an extension or null
   */
  public IPresentationExtension getExtension() {
    return extension;
  }

  /**
   * Set the extension for the main form.
   * 
   * @param extension
   *          an extension or null.
   * @see #getExtension()
   */
  public void setExtension(IPresentationExtension extension) {
    this.extension = extension;
  }

  /**
   * If this form takes on a timespan, the I18N key of this timespan is returned.
   * 
   * @return The I18N key of the timespan description.
   */
  public String getTimeSpanDescription() {
    return timeSpanDescription;
  }

  /**
   * Returns the type of building block this form is about. Depending on the type and the associated
   * attributes, this form is created and rendered differently.
   * 
   * @return the type this form is about.
   */
  public Type<T> getType() {
    return type;
  }

  /**
   * Returns the MassUpdateType. In MassUpdateMode this type is needed to generate the list of
   * attributes and associations of the BuildingBlock the form is about
   * 
   * @return The MassUpdateType
   */
  public MassUpdateType getMassUpdateType() {
    return massUpdateType;
  }

  /**
   * Sets the MassUpdateType. In MassUpdateMode this type is needed to generate the list of
   * attributes and associations of the BuildingBlock the form is about
   * 
   * @param massUpdateType
   *          The MassUpdateType
   */
  public void setMassUpdateType(MassUpdateType massUpdateType) {
    this.massUpdateType = massUpdateType;
    this.queryUserInput.intiMassUpdateData(massUpdateType);
  }

  /**
   * This function set MassUpdate specific values. Utilized when a saved query is loaded and given
   * MassUpdate data must be transfered to the loaded element.
   * 
   * @param type
   *          type of MassUpdate report
   * @param data
   *          Whole MassUpdata specific user input.
   */
  public void setMassUpdateData(MassUpdateType type, MassUpdateData data) {
    this.setMassUpdateType(type);
    this.queryUserInput.setMassUpdateData(data);
  }

  /**
   * The list returned by this method is only filled for complex extensions for which it should be
   * possible to state additional conditions regarding other building blocks, e.g. the Business
   * Mapping extension.
   * 
   * @return list of second level query forms
   */
  public List<DynamicQueryFormData<?>> getSecondLevelQueryForms() {
    return secondLevelQueryForms;
  }

  /**
   * Set the list of second level query forms.
   * 
   * @param secondLevelQueryForms
   *          list of second level query forms
   * @see #getSecondLevelQueryForms()
   */
  public void setSecondLevelQueryForms(List<DynamicQueryFormData<?>> secondLevelQueryForms) {
    this.secondLevelQueryForms = secondLevelQueryForms;
  }

  /**
   * Adds a {@link DynamicQueryFormData} to the list contained in
   * {@link #getSecondLevelQueryForms()}.
   * 
   * @param secondLevelForm
   *          The second level query form to add.
   */
  public void addSecondLevelQueryForm(DynamicQueryFormData<?> secondLevelForm) {
    this.getSecondLevelQueryForms().add(secondLevelForm);
  }

  /**
   * Searches the list contained in {@link #getAvailableAttributes()} and returns the
   * {@link BBAttribute} with the given id.
   * 
   * @param id
   *          The id of the attribute to return.
   * @return the attribute with the given id.
   */
  public BBAttribute getBBAttributeByStringId(String id) {
    for (BBAttribute attr : availableAttributes) {
      if (attr.getStringId().equals(id)) {
        return attr;
      }
    }
    return null;
  }

  /**
   * Adds another first level to the query form ('and' part)
   */
  public void expandFirstLevel() {
    List<QFirstLevel> firstLevels = getQueryUserInput().getQueryFirstLevels();
    QFirstLevel newFirstLevel = new QFirstLevel();
    newFirstLevel.getQuerySecondLevels().add(new QPart());
    firstLevels.add(newFirstLevel);
  }

  /**
   * Adds another second level ('or' part) to the given first level ('and' part)
   * 
   * @param firstLevelId
   *          The id of the first level to expand.
   */
  public void expandSecondLevel(int firstLevelId) {
    List<QFirstLevel> firstLevels = getQueryUserInput().getQueryFirstLevels();
    QFirstLevel firstLevel = firstLevels.get(firstLevelId);
    QPart secondLevel = firstLevel.getQuerySecondLevels().get(firstLevel.getQuerySecondLevels().size() - 1);
    QPart newSecondLevel = secondLevel.getCopy();
    firstLevel.getQuerySecondLevels().add(newSecondLevel);
  }

  /**
   * This method is used for deciding whether to hide 'not' operations in the query form (see also
   * static initialiser in this class). This is true, if this form represents an extension and the
   * extension contains an association type.
   * 
   * @return true if the extension contains an association type
   */
  public boolean isCriticalExtension() {
    if ((extension != null) && extension.isWithAssociationType()) {
      return true;
    }
    return false;
  }
}
