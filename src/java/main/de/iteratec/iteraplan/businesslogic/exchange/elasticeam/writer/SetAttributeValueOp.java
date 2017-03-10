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
package de.iteratec.iteraplan.businesslogic.exchange.elasticeam.writer;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffPart;
import de.iteratec.iteraplan.elasticeam.model.compare.RightSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.TwoSidedDiff;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.model.attribute.TimeseriesType;


/**
 * Sets the attribute values of an iteraplan instance according to a {@link de.iteratec.iteraplan.elasticeam.model.compare.BaseDiff Diff}.
 */
public class SetAttributeValueOp extends IteraplanChangeOperation {

  private static final Logger         LOGGER = Logger.getIteraplanLogger(SetAttributeValueOp.class);

  private final AttributeValueService avService;

  public SetAttributeValueOp(IteraplanMapping mapping, BiMap<Object, UniversalModelExpression> instanceMapping, AttributeValueService avService) {
    super(mapping, instanceMapping);
    this.avService = avService;
  }

  protected Logger getLogger() {
    return LOGGER;
  }

  /**
   * Sets the attribute values from a {@link RightSidedDiff}.
   */
  protected void handleRightSidedDiff(RightSidedDiff diff) {
    UniversalModelExpression expression = diff.getExpression();
    LOGGER.debug("Setting attribute values for \"{0}\"...", expression);

    Object instance = getInstanceForExpression(expression);

    if (instance != null) {
      for (PropertyExpression<?> property : diff.getType().getProperties()) {
        if (getIteraplanMapping().isDerivedFromAT(property)) {
          LOGGER.debug("Setting value of attribute \"{0}\"...", property);
          Object newValue;
          if (property.getUpperBound() > 1) {
            newValue = expression.getValues(property);
          }
          else {
            newValue = expression.getValue(property);
          }

          if (newValue != null) {
            setAttributeValue(newValue, property, instance);
          }
        }
      }

      LOGGER.debug("Attribute values set.");
    }
    else {
      LOGGER.debug("No instance found for expression \"{0}\". Ignoring.", expression);
    }
  }

  /**
   * Sets the attribute values from a {@link TwoSidedDiff}.
   */
  protected void handleTwoSidedDiff(TwoSidedDiff diff) {
    UniversalModelExpression instanceExpression = diff.getLeftExpression();
    LOGGER.debug("Setting attribute values for \"{0}\"...", instanceExpression);

    Object instance = getInstanceForExpression(instanceExpression);

    for (DiffPart diffPart : diff.getDiffParts()) {
      FeatureExpression<?> feature = diffPart.getFeature();
      if (feature instanceof PropertyExpression) {
        PropertyExpression<?> property = (PropertyExpression<?>) feature;
        if (getIteraplanMapping().isDerivedFromAT(property)) {
          LOGGER.debug("Setting value of attribute \"{0}\" to \"{1}\"...", property, diffPart.getRightValue());
          setAttributeValue(diffPart.getRightValue(), property, instance);
        }
      }
    }

    LOGGER.debug("Attribute values set.");
  }

  private void setAttributeValue(Object newValue, PropertyExpression<?> property, Object instance) {
    AttributeType attributeType = getIteraplanMapping().resolveAdditionalProperty(property);

    if (instance instanceof BuildingBlock) {
      BuildingBlock bb = (BuildingBlock) instance;

      if (newValue != null && !newValue.toString().isEmpty()) {
        if (attributeType instanceof TextAT) {
          handleTextAT(bb, newValue, (TextAT) attributeType);
        }
        else if (attributeType instanceof NumberAT) {
          handleNumberAT(bb, newValue, (NumberAT) attributeType);
        }
        else if (attributeType instanceof DateAT) {
          handleDateAT(bb, newValue, (DateAT) attributeType);
        }
        else if (attributeType instanceof ResponsibilityAT) {
          handleResponsibilityAT(newValue, (ResponsibilityAT) attributeType, bb);
        }
        else if (attributeType instanceof EnumAT) {
          handleEnumAT(newValue, (EnumAT) attributeType, bb);
        }
      }
      else {
        avService.setValue(bb, null, attributeType);
        logInfo("Value of AT \"{0}\" for building block \"{1}\" unset.", attributeType, bb);
      }
    }
  }

  private void handleTextAT(BuildingBlock bb, Object newValue, TextAT textAT) {
    LOGGER.debug("Handle value \"{0}\" for AT \"{1}\"...", newValue, textAT);
    String textValue = newValue.toString();
    if (textValue.isEmpty()) {
      return; // don't create text AVs for empty strings
    }
    TextAV textAV = new TextAV(textAT, textValue);
    textAT.getAttributeValues().add(textAV);
    avService.saveOrUpdate(textAV);
    avService.setValue(bb, textAV, textAT);
    logInfo("Value of AT \"{0}\" for building block \"{1}\" set to \"{2}\".", textAT, bb, textAV.getValueString());
  }

  private void handleNumberAT(BuildingBlock bb, Object newValue, NumberAT numberAT) {
    LOGGER.debug("Handle value \"{0}\" for AT \"{1}\"...", newValue, numberAT);
    if (checkTimeseries(numberAT, bb)) {
      return;
    }
    NumberAV numberAV = new NumberAV(numberAT, (BigDecimal) newValue);
    numberAT.getAttributeValues().add(numberAV);
    avService.saveOrUpdate(numberAV);
    avService.setValue(bb, numberAV, numberAT);
    logInfo("Value of AT \"{0}\" for building block \"{1}\" set to \"{2}\".", numberAT, bb, numberAV.getValueString());
  }

  private void handleDateAT(BuildingBlock bb, Object newValue, DateAT dateAT) {
    LOGGER.debug("Handle value \"{0}\" for AT \"{1}\"...", newValue, dateAT);
    DateAV dateAV = new DateAV(dateAT, (Date) newValue);
    dateAT.getAttributeValues().add(dateAV);
    avService.saveOrUpdate(dateAV);
    avService.setValue(bb, dateAV, dateAT);
    logInfo("Value of AT \"{0}\" for building block \"{1}\" set to \"{2}\".", dateAT, bb, dateAV.getValueString());
  }

  @SuppressWarnings("unchecked")
  private void handleResponsibilityAT(Object newValue, ResponsibilityAT respAT, BuildingBlock bb) {
    LOGGER.debug("Handle value \"{0}\" for AT \"{1}\"...", newValue, respAT);
    Collection<ResponsibilityAV> newValues = Lists.newArrayList();
    if (newValue instanceof Collection) {
      if (respAT.isMultiassignmenttype()) {
        LOGGER.debug("Multi-assignment attribute type.");
        Collection<String> values = (Collection<String>) newValue;
        for (ResponsibilityAV respAV : respAT.getAttributeValues()) {
          if (values.contains(respAV.getUserEntity().getIdentityString())) {
            newValues.add(respAV);
          }
        }
      }
      else {
        throw new ModelException(ModelException.INCONVERTIBLE_TYPES, "Collection assigned to responsibility attribute \"" + respAT.getName()
            + "\" which doesn't allow multiassignments.");
      }
    }
    else {
      String value = newValue.toString();
      for (ResponsibilityAV respAV : respAT.getAttributeValues()) {
        if (value.equals(respAV.getUserEntity().getIdentityString())) {
          newValues.add(respAV);
          continue;
        }
      }
    }
    avService.setReferenceValues(bb, newValues, respAT.getId());
    logInfo("Values of AT \"{0}\" for {1} \"{2}\" set to \"{3}\".", respAT, bb.getClass().getSimpleName(), bb,
        GeneralHelper.makeConcatenatedNameStringForAvCollection(newValues));
  }

  @SuppressWarnings("unchecked")
  private void handleEnumAT(Object newValue, EnumAT enumAT, BuildingBlock bb) {
    LOGGER.debug("Handle value \"{0}\" for AT \"{1}\"...", newValue, enumAT);
    if (checkTimeseries(enumAT, bb)) {
      return;
    }
    Collection<EnumAV> newEnumAVs = Lists.newArrayList();
    if (newValue instanceof Collection) {
      if (enumAT.isMultiassignmenttype()) {
        LOGGER.debug("Multi-assignment attribute type.");
        Collection<EnumerationLiteralExpression> newLiterals = (Collection<EnumerationLiteralExpression>) newValue;
        for (EnumerationLiteralExpression literal : newLiterals) {
          EnumAV enumAV = getEnumAVFromLiteral(literal);
          if (enumAV != null) {
            newEnumAVs.add(enumAV);
          }
          else {
            logWarning("Could not assign literal \"{0}\" to \"{1}\" {2} \"{3}\" because it was not found.", literal.getName(), enumAT.getName(), bb
                .getClass().getSimpleName(), bb);
          }
        }
      }
      else {
        throw new ModelException(ModelException.INCONVERTIBLE_TYPES, "Collection assigned to enumeration attribute \"" + enumAT.getName()
            + "\" which doesn't allow multiassignments.");
      }
    }
    else {
      EnumerationLiteralExpression literal = (EnumerationLiteralExpression) newValue;
      EnumAV enumAV = getEnumAVFromLiteral(literal);
      if (enumAV != null) {
        newEnumAVs.add(enumAV);
      }
      else {
        logWarning("Could not assign literal \"{0}\" to \"{1}\" {2} \"{3}\" because it was not found.", literal.getName(), enumAT.getName(), bb
            .getClass().getSimpleName(), bb);
      }
    }

    avService.setReferenceValues(bb, newEnumAVs, enumAT.getId());
    logInfo("Value of AT \"{0}\" for building block \"{1}\" set to \"{2}\".", enumAT, bb,
        GeneralHelper.makeConcatenatedNameStringForAvCollection(newEnumAVs));
  }

  private boolean checkTimeseries(AttributeType attributeType, BuildingBlock bb) {
    if (attributeType instanceof TimeseriesType && ((TimeseriesType) attributeType).isTimeseries()) {
      logWarning("Attempt to change value in Timeseries: \"{0}\" of Building Block: \"{1}\". Ignoring changes in Timeseries.",
          attributeType.getName(), bb.getNonHierarchicalName());
      return true;
    }
    else {
      return false;
    }
  }

  private EnumAV getEnumAVFromLiteral(EnumerationLiteralExpression literal) {
    return getIteraplanMapping().getAdditionalEnumerationLiterals().get(literal);
  }

}
