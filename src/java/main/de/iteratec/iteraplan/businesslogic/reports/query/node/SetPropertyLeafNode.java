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
package de.iteratec.iteraplan.businesslogic.reports.query.node;

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;

import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ProjectQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.TechnicalComponentReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.attribute.BBAttribute;


/**
 * Node class representing queries for building blocks that meet certain restrictions regarding
 * fixed properties.
 */
public class SetPropertyLeafNode extends AbstractLeafNode {

  private static final Logger LOGGER              = Logger.getIteraplanLogger(SetPropertyLeafNode.class);

  /** The name of the property. */
  private String              propertyName;

  /** The name of the elements property */
  private String              elementPropertyName = "loginName";

  /** The comparator to use for this node type. */
  private Comparator          comparator;

  public SetPropertyLeafNode(Type<?> resultType, Extension extension, String propertyName, Comparator comparator, Object pattern) {
    super(resultType, extension, pattern);
    this.propertyName = propertyName;
    this.comparator = comparator;

    init();
    logCreationMsg();
  }

  private void logCreationMsg() {
    if (LOGGER.isDebugEnabled()) {
      StringBuilder builder = new StringBuilder();
      builder.append("Created a PropertyLeafNode: ");
      builder.append("\nResult Type: " + getResultType().getTypeNameDB());
      builder.append("\nExtension Type: " + ((getExtension() == null) ? null : getExtension().getRequestedType().getTypeNameDB()));
      builder.append("\nProperty: " + this.propertyName);
      builder.append("\nComparator: " + this.comparator);
      builder.append("\nPattern: " + getPattern());

      LOGGER.debug(builder.toString());
    }
  }

  /**
   * Checks if the node is affected of the inheritance mechanism or special insome way and sets the
   * appropriate test strategy.
   */
  private void init() {
    if (getLeafType().isSpecialProperty(getPropertyName())) {
      setAdditionalQueryRequired(true);
    }

    // set appropriate test for this leaf node:
    if (getLeafType() instanceof InformationSystemReleaseTypeQu) {
      if (InformationSystemReleaseTypeQu.PROPERTY_STARTDATE.equals(getPropertyName())) {
        setTest(new DateEntityStartDateTest(getComparator(), (Date) getPattern()));
      }
      else if (InformationSystemReleaseTypeQu.PROPERTY_ENDDATE.equals(getPropertyName())) {
        setTest(new DateEntityEndDateTest(getComparator(), (Date) getPattern()));
      }
    }
    else if (getLeafType() instanceof ProjectQueryType) {
      if (ProjectQueryType.PROPERTY_STARTDATE.equals(getPropertyName())) {
        setTest(new DateEntityStartDateTest(getComparator(), (Date) getPattern()));
      }
      else if (ProjectQueryType.PROPERTY_ENDDATE.equals(getPropertyName())) {
        setTest(new DateEntityEndDateTest(getComparator(), (Date) getPattern()));
      }
    }
    else if (getLeafType() instanceof TechnicalComponentReleaseTypeQu) {
      if (TechnicalComponentReleaseTypeQu.PROPERTY_STARTDATE.equals(getPropertyName())) {
        setTest(new DateEntityStartDateTest(getComparator(), (Date) getPattern()));
      }
      else if (TechnicalComponentReleaseTypeQu.PROPERTY_ENDDATE.equals(getPropertyName())) {
        setTest(new DateEntityEndDateTest(getComparator(), (Date) getPattern()));
      }
    }
    if (Type.PROPERTY_LAST_MODIFICATION_DATE.equals(getPropertyName())) {
      // date has to be parsed
      Date date = DateUtils.parseAsDate((String) getPattern(), UserContext.getCurrentLocale());

      if ((date == null) && (comparator != Comparator.NO_ASSIGNMENT) && (comparator != Comparator.ANY_ASSIGNMENT)) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.QUERY_NO_VALUE_PROVIDED);
      }
      setPattern(date);
      // setTest(new DateEntityStartDateTest(getComparator(), date));
    }
  }

  /**
   * @return Returns the propertyName.
   */
  public final String getPropertyName() {
    return propertyName;
  }

  /**
   * Returns the comparator used in this leaf node.
   */
  public final Comparator getComparator() {
    return comparator;
  }

  /** {@inheritDoc} */
  @Override
  DetachedCriteria getFromCriteriaForInheritance(DetachedCriteria criteria) {
    return getFromCriteria(criteria);
  }

  /** {@inheritDoc} */
  @Override
  DetachedCriteria getWhereCriteriaFromInheritance(DetachedCriteria criteria) {
    if (!getLeafType().isSpecialProperty(getPropertyName())) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    if (getComparator().equals(Comparator.ANY_ASSIGNMENT) || getComparator().equals(Comparator.NO_ASSIGNMENT)) {
      setPattern("");
      return getWhereCriteria(criteria);
    }

    String[] inheritHQL = getLeafType().getSpecialPropertyHQL(getPropertyName());
    comparator = Comparator.fromString(inheritHQL[0]);
    setPattern(inheritHQL[1]);

    return getWhereCriteria(criteria);
  }

  /** {@inheritDoc} */
  @Override
  DetachedCriteria getWhereCriteria(DetachedCriteria criteria) {
    final String associationPath = String.format("%s.%s", getResultTypeDBNameShortWithSuffix(), getPropertyName());
    final String alias = String.format("%sAlias", getPropertyName());
    final String effectivePropertyName = String.format("%s.%s", alias, elementPropertyName);

    final Criterion criterion = getCriterionForComparator(effectivePropertyName, getComparator(), BBAttribute.FIXED_ATTRIBUTE_SET);
    criteria.createAlias(associationPath, alias, Criteria.LEFT_JOIN);
    criteria.add(criterion);

    return criteria;
  }

  String getAlias() {
    return getPropertyName() + "_a";
  }

  /** {@inheritDoc} */
  @Override
  public boolean isToBeRemoved(Object element) throws IteraplanException {
    if (getExtensionSize() == 0) {
      if (!isMatch(element)) {
        return true;
      }
    }
    else {
      if (getLeafType().isSpecialProperty(getPropertyName()) && !containsMatch(getExtension().getConditionElements(element))) {
        return true;
      }
    }
    return false;
  }
}
