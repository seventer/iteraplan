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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.ElasticMiIteraplanMapping;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticmi.diff.model.CreateDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.PropertyChange;
import de.iteratec.iteraplan.elasticmi.diff.model.PropertyInit;
import de.iteratec.iteraplan.elasticmi.diff.model.UpdateDiff;
import de.iteratec.iteraplan.elasticmi.exception.ModelException;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.metamodel.common.ValueTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.REnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WEnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WPropertyExpression;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.model.ValueExpression;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;
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


public class AttributeValueSetter extends AIteraplanInstanceWriter implements CreateOrUpdateDiffHandler {

  private static final Logger         LOGGER = Logger.getIteraplanLogger(AttributeValueSetter.class);

  private final AttributeValueService avService;

  public AttributeValueSetter(ElasticMiIteraplanMapping mapping, Map<BigInteger, BuildingBlock> id2bbMap, AttributeValueService avService,
      MessageListener listener) {
    super(mapping, id2bbMap, listener);
    this.avService = avService;
  }

  @Override
  protected Logger getLogger() {
    return LOGGER;
  }

  public boolean handleDiff(CreateOrUpdateDiff diff) {
    return diff.isLeft() ? handleCreateDiff(diff.getLeft()) : handleUpdateDiff(diff.getRight());
  }

  /**
   * Sets the attribute values from a {@link CreateDiff}.
   */
  protected boolean handleCreateDiff(CreateDiff diff) {
    ObjectExpression expression = diff.getObjectExpression();
    LOGGER.debug("Setting attribute values for \"{0}\"...", expression);

    Object instance = getInstanceForExpression(expression);

    if (instance != null) {
      for (PropertyInit propInit : diff.getPropertyInits()) {
        if (getIteraplanMapping().isDerivedFromAT(wProperty(diff.getStructuredType(), propInit.getProperty()))) {
          LOGGER.debug("Setting value of attribute \"{0}\"...", propInit.getProperty());
          setAttributeValue(propInit.getValue(), wProperty(diff.getStructuredType(), propInit.getProperty()), instance);
        }
      }
      LOGGER.debug("Attribute values set.");
    }
    else {
      LOGGER.debug("No instance found for expression \"{0}\". Ignoring.", expression);
    }
    return true;
  }

  /**
   * Sets the attribute values from a {@link UpdateDiff}.
   */
  protected boolean handleUpdateDiff(UpdateDiff diff) {
    ObjectExpression instanceExpression = diff.getLeftObjectExpression();
    LOGGER.debug("Setting attribute values for \"{0}\"...", instanceExpression);

    Object instance = getInstanceForExpression(instanceExpression);

    for (PropertyChange change : diff.getPropertyChanges()) {
      PropertyChange filteredChange = getMergeStrategy().filterPropertyChange(diff, change);
      if (filteredChange.isActualChange()) {
        RPropertyExpression property = filteredChange.getProperty();
        if (getIteraplanMapping().isDerivedFromAT(wProperty(diff.getStructuredType(), property))) {
          LOGGER.debug("Setting value of attribute \"{0}\" to \"{1}\"...", property, filteredChange.getRightValue());
          setAttributeValue(filteredChange.getRightValue(), wProperty(diff.getStructuredType(), property), instance);
        }
      }
    }

    LOGGER.debug("Attribute values set.");
    return true;
  }

  private void setAttributeValue(ElasticValue<ValueExpression> newValue, WPropertyExpression property, Object instance) {
    AttributeType attributeType = getIteraplanMapping().resolveAdditionalProperty(property);

    if (instance instanceof BuildingBlock) {
      BuildingBlock bb = (BuildingBlock) instance;

      if (attributeType instanceof TextAT) {
        handleTextAT(bb, checkValues(property.getType(), newValue), (TextAT) attributeType);
      }
      else if (attributeType instanceof NumberAT) {
        handleNumberAT(bb, checkValues(property.getType(), newValue), (NumberAT) attributeType);
      }
      else if (attributeType instanceof DateAT) {
        handleDateAT(bb, checkValues(property.getType(), newValue), (DateAT) attributeType);
      }
      else if (attributeType instanceof ResponsibilityAT) {
        handleResponsibilityAT(newValue, (ResponsibilityAT) attributeType, bb);
      }
      else if (attributeType instanceof EnumAT) {
        handleEnumAT(newValue, property.getType(), (EnumAT) attributeType, bb);
      }
    }
  }

  private void handleTextAT(BuildingBlock bb, Object newValue, TextAT textAT) {
    LOGGER.debug("Handle value \"{0}\" for AT \"{1}\"...", newValue, textAT);
    TextAV textAV = null;
    if (newValue != null && !newValue.toString().trim().isEmpty()) {
      String textValue = newValue.toString();
      textAV = new TextAV(textAT, textValue);
      avService.saveOrUpdate(textAV);
      textAT.getAttributeValues().add(textAV);
    }
    avService.setValue(bb, textAV, textAT);
  }

  private void handleNumberAT(BuildingBlock bb, Object newValue, NumberAT numberAT) {
    LOGGER.debug("Handle value \"{0}\" for AT \"{1}\"...", newValue, numberAT);
    if (checkTimeseries(numberAT, bb)) {
      return;
    }
    NumberAV numberAV = null;
    if (newValue != null) {
      numberAV = new NumberAV(numberAT, (BigDecimal) newValue);
      numberAT.getAttributeValues().add(numberAV);
      avService.saveOrUpdate(numberAV);
    }
    avService.setValue(bb, numberAV, numberAT);
  }

  private void handleDateAT(BuildingBlock bb, Object newValue, DateAT dateAT) {
    LOGGER.debug("Handle value \"{0}\" for AT \"{1}\"...", newValue, dateAT);
    DateAV dateAV = null;
    if (newValue != null) {
      dateAV = new DateAV(dateAT, (Date) newValue);
      dateAT.getAttributeValues().add(dateAV);
      avService.saveOrUpdate(dateAV);
    }
    avService.setValue(bb, dateAV, dateAT);
  }

  private void handleResponsibilityAT(ElasticValue<ValueExpression> newValue, ResponsibilityAT respAT, BuildingBlock bb) {
    LOGGER.debug("Handle value \"{0}\" for AT \"{1}\"...", newValue, respAT);
    Collection<ResponsibilityAV> newValues = Lists.newArrayList();
    if (respAT.isMultiassignmenttype()) {
      LOGGER.debug("Multi-assignment attribute type.");
      Collection<String> values = Sets.newHashSet();
      for (ValueExpression value : newValue) {
        values.add(value.asString().trim());
      }
      for (ResponsibilityAV respAV : respAT.getAttributeValues()) {
        if (values.contains(respAV.getUserEntity().getIdentityString())) {
          newValues.add(respAV);
        }
      }

    }
    else if (newValue.isOne()) {
      String value = newValue.getOne().asString().trim();
      for (ResponsibilityAV respAV : respAT.getAttributeValues()) {
        if (value.equals(respAV.getUserEntity().getIdentityString())) {
          newValues.add(respAV);
          continue;
        }
      }
    }
    else if (newValue.isMany()) {
      throw new ModelException(ModelException.INCONVERTIBLE_TYPES, "Collection assigned to responsibility attribute \"" + respAT.getName()
          + "\" which doesn't allow multiassignments.");
    }
    avService.setReferenceValues(bb, newValues, respAT.getId());
  }

  @SuppressWarnings("unchecked")
  private void handleEnumAT(ElasticValue<ValueExpression> newValue, ValueTypeExpression<?> rEnumeration, EnumAT enumAT, BuildingBlock bb) {
    LOGGER.debug("Handle value \"{0}\" for AT \"{1}\"...", newValue, enumAT);
    if (checkTimeseries(enumAT, bb)) {
      return;
    }

    Collection<EnumAV> newEnumAVs = Lists.newArrayList();
    if (enumAT.isMultiassignmenttype()) {
      LOGGER.debug("Multi-assignment attribute type.");
      Collection<REnumerationLiteralExpression> newLiterals = (Collection<REnumerationLiteralExpression>) ValueExpression.flatten(newValue.getMany());
      for (REnumerationLiteralExpression literal : newLiterals) {
        EnumAV enumAV = getEnumAVFromLiteral(wLiteral(rEnumeration, literal));
        if (enumAV != null) {
          newEnumAVs.add(enumAV);
        }
        else {
          logWarning("Could not assign literal \"{0}\" to \"{1}\" {2} \"{3}\" because it was not found.", literal.getName(), enumAT.getName(), bb
              .getClass().getSimpleName(), bb);
        }
      }
    }
    else if (newValue.isOne()) {
      REnumerationLiteralExpression literal = newValue.getOne().asEnumerationLiteral();
      EnumAV enumAV = getEnumAVFromLiteral(wLiteral(rEnumeration, literal));
      if (enumAV != null) {
        newEnumAVs.add(enumAV);
      }
      else {
        logWarning("Could not assign literal \"{0}\" to \"{1}\" {2} \"{3}\" because it was not found.", literal.getName(), enumAT.getName(), bb
            .getClass().getSimpleName(), bb);
      }
    }
    else if (newValue.isMany()) {
      throw new ModelException(ModelException.INCONVERTIBLE_TYPES, "Collection assigned to enumeration attribute \"" + enumAT.getName()
          + "\" which doesn't allow multiassignments.");
    }
    //    bb.removeAVAsHashSafeById(enumAT.getId());
    //    bbServiceLocator.getService(bb.getTypeOfBuildingBlock()).saveOrUpdate(bb);
    avService.setReferenceValues(bb, newEnumAVs, enumAT.getId());
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

  private EnumAV getEnumAVFromLiteral(WEnumerationLiteralExpression literal) {
    return getIteraplanMapping().getAdditionalEnumerationLiterals().get(literal);
  }

}
