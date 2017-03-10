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
package de.iteratec.iteraplan.persistence.elasticeam.model.diff;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemService;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentService;
import de.iteratec.iteraplan.common.DefaultSpringApplicationContext;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedClass;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedProperty;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
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


/**
 * Sets property values
 */
public final class IteraplanInstanceHelper {

  private static final Logger LOGGER = Logger.getIteraplanLogger(IteraplanInstanceHelper.class);

  private IteraplanInstanceHelper() {
    // do nothing
  }

  /**
   * Creates an iteraplan entity instance.
   * @param hbClass
   *          {@link HbMappedClass} for the class of the entity to create
   * @param sourceModelExpression
   *          {@link UniversalModelExpression} describing the entity to create
   * @return the iteraplan entity instance
   */
  public static BuildingBlock createIteraplanInstance(HbMappedClass hbClass, UniversalModelExpression sourceModelExpression) {
    Class<?> clazz = hbClass.getMappedClass();
    try {
      Method factoryConstructor = BuildingBlockFactory.class.getMethod("create" + clazz.getSimpleName());
      BuildingBlock bb = (BuildingBlock) factoryConstructor.invoke(null);

      if (hbClass.isReleaseClass()) {
        //in case of TCRs or ISRs, we need to check for TCs and ISs
        Set<String> names = Sets.newHashSet(sourceModelExpression.getValue(MixinTypeNamed.NAME_PROPERTY).toString());

        if (TechnicalComponentRelease.class.equals(hbClass.getMappedClass())) {
          addBaseToTCR(bb, names);
        }
        else if (InformationSystemRelease.class.equals(hbClass.getMappedClass())) {
          addBaseToISR(bb, names);
        }
        else {
          throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, hbClass);
        }
      }
      LOGGER.info("Created new " + clazz.getSimpleName() + ".");
      return bb;
    } catch (SecurityException e) {
      LOGGER.error(e);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
    } catch (NoSuchMethodException e) {
      LOGGER.error(e);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
    } catch (IllegalArgumentException e) {
      LOGGER.error(e);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
    } catch (IllegalAccessException e) {
      LOGGER.error(e);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
    } catch (InvocationTargetException e) {
      LOGGER.error(e);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
    }
  }

  private static void addBaseToTCR(BuildingBlock bb, Set<String> names) {
    TechnicalComponentRelease release = (TechnicalComponentRelease) bb;
    TechnicalComponentService service = (TechnicalComponentService) DefaultSpringApplicationContext.getSpringApplicationContext().getBean(
        "technicalComponentService");

    List<TechnicalComponent> list = service.findByNames(names);
    TechnicalComponent base = null;
    if (list.isEmpty()) {
      base = BuildingBlockFactory.createTechnicalComponent();
    }
    else {
      base = list.get(0);
    }
    base.addRelease(release); // adds it two way
  }

  private static void addBaseToISR(BuildingBlock bb, Set<String> names) {
    InformationSystemRelease release = (InformationSystemRelease) bb;
    InformationSystemService service = (InformationSystemService) DefaultSpringApplicationContext.getSpringApplicationContext().getBean(
        "informationSystemService");
    List<InformationSystem> list = service.findByNames(names);
    InformationSystem base = null;
    if (list.isEmpty()) {
      base = BuildingBlockFactory.createInformationSystem();
    }
    else {
      base = list.get(0);
    }
    base.addRelease(release); //adds it two way
  }

  /**
   * Sets the value of a property for a given iteraplan instance.
   * @param newValue
   *          The new value to set
   * @param property
   *          {@link PropertyExpression} of the property to set
   * @param instance
   *          The instance to set the new value for.
   * @param hbClass
   *          {@link HbMappedClass} for {@code instance}
   * @param mapping
   *          Metamodel {@link IteraplanMapping}
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public static void setPropertyValue(Object newValue, PropertyExpression<?> property, Object instance, HbMappedClass hbClass,
                                      IteraplanMapping mapping) throws IllegalAccessException, InvocationTargetException {

    Object validValue = getValidValue(newValue, property);

    if (mapping.isDerivedFromAT(property)) {
      setCustomAT(validValue, property, instance, mapping);
    }
    else {
      setStandardAttribute(validValue, property, instance, hbClass, mapping);
    }
  }

  private static void setStandardAttribute(Object newValue, PropertyExpression<?> property, Object instance, HbMappedClass hbClass,
                                           IteraplanMapping mapping) throws IllegalAccessException, InvocationTargetException {
    HbMappedProperty hbProp = HbClassHelper.findHbMappedProperty(hbClass, property);
    Object owningInstance = HbClassHelper.getOwningInstance(hbClass, hbProp, instance);

    if ((property.getType() instanceof EnumerationExpression) && (newValue instanceof EnumerationLiteralExpression)) {
      Enum<?> newLiteral = mapping.getEnumerationLiterals().get(newValue);
      hbProp.getSetMethod().invoke(owningInstance, newLiteral);
    }
    else {
      hbProp.getSetMethod().invoke(owningInstance, newValue);
    }
  }

  private static void setCustomAT(Object newValue, PropertyExpression<?> property, Object instance, IteraplanMapping mapping) {
    AttributeType attributeType = mapping.resolveAdditionalProperty(property);

    if (instance instanceof BuildingBlock) {
      BuildingBlock bb = (BuildingBlock) instance;
      AttributeValueService avs = (AttributeValueService) DefaultSpringApplicationContext.getSpringApplicationContext().getBean(
          "attributeValueService");
      AttributeValue newAttributeValue = null;

      if (newValue != null) {
        if (attributeType instanceof TextAT) {
          newAttributeValue = handleTextAT(newValue, avs, (TextAT) attributeType);
        }
        else if (attributeType instanceof NumberAT) {
          newAttributeValue = handleNumberAT(newValue, avs, (NumberAT) attributeType);
        }
        else if (attributeType instanceof DateAT) {
          newAttributeValue = handleDateAT(newValue, avs, (DateAT) attributeType);
        }
        else if (attributeType instanceof ResponsibilityAT) {
          newAttributeValue = handleResponsibilityAT(newValue, avs, (ResponsibilityAT) attributeType, bb);
        }
        else if (attributeType instanceof EnumAT) {
          newAttributeValue = handleEnumAT(newValue, avs, (EnumAT) attributeType, bb, mapping);
        }
      }

      if (newAttributeValue != null) {
        avs.setValue(bb, newAttributeValue, attributeType);
      }
    }
  }

  private static AttributeValue handleTextAT(Object newValue, AttributeValueService avs, TextAT textAT) {
    TextAV textAV = new TextAV(textAT, newValue.toString());
    textAT.getAttributeValues().add(textAV);
    avs.saveOrUpdate(textAV);
    return textAV;
  }

  private static AttributeValue handleNumberAT(Object newValue, AttributeValueService avs, NumberAT numberAT) {
    NumberAV numberAV = new NumberAV(numberAT, (BigDecimal) newValue);
    numberAT.getAttributeValues().add(numberAV);
    avs.saveOrUpdate(numberAV);
    return numberAV;
  }

  private static AttributeValue handleDateAT(Object newValue, AttributeValueService avs, DateAT dateAT) {
    DateAV dateAV = new DateAV(dateAT, (Date) newValue);
    dateAT.getAttributeValues().add(dateAV);
    avs.saveOrUpdate(dateAV);
    return dateAV;
  }

  private static AttributeValue handleResponsibilityAT(Object newValue, AttributeValueService avs, ResponsibilityAT respAT, BuildingBlock bb) {
    if (respAT.isMultiassignmenttype()) {
      Collection<String> values = (Collection<String>) newValue;
      Collection<ResponsibilityAV> newValues = Lists.newArrayList();
      for (ResponsibilityAV respAV : respAT.getAttributeValues()) {
        if (values.contains(respAV.getUserEntity().getIdentityString())) {
          newValues.add(respAV);
        }
      }
      // for multi value, set the new values here and return null, so the new value isn't saved as only value for the attribute
      avs.setReferenceValues(bb, newValues, respAT.getId());
      return null;
    }
    else {
      String value = newValue.toString();
      for (ResponsibilityAV respAV : respAT.getAttributeValues()) {
        if (value.equals(respAV.getUserEntity().getIdentityString())) {
          return respAV;
        }
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private static AttributeValue handleEnumAT(Object newValue, AttributeValueService avs, EnumAT enumAT, BuildingBlock bb, IteraplanMapping mapping) {
    if (newValue instanceof Collection) {
      if (enumAT.isMultiassignmenttype()) {
        Collection<EnumerationLiteralExpression> newLiterals = (Collection<EnumerationLiteralExpression>) newValue;
        Collection<EnumAV> newEnumAVs = Lists.newArrayList();
        for (EnumerationLiteralExpression literal : newLiterals) {
          EnumAV enumAV = getEnumAVFromLiteral(mapping, literal);
          newEnumAVs.add(enumAV);
        }
        // for multi value, set the new values here and return null, so the new value isn't saved as only value for the attribute
        avs.setReferenceValues(bb, newEnumAVs, enumAT.getId());
        return null;
      }
      else {
        throw new ModelException(ModelException.INCONVERTIBLE_TYPES, "Collection assigned to enumeration attribute \"" + enumAT.getName()
            + "\" which doesn't allow multiassignments.");
      }
    }
    else {
      EnumerationLiteralExpression newLiteral = (EnumerationLiteralExpression) newValue;
      return (getEnumAVFromLiteral(mapping, newLiteral));
    }
  }

  private static EnumAV getEnumAVFromLiteral(IteraplanMapping mapping, EnumerationLiteralExpression literal) {
    return mapping.getAdditionalEnumerationLiterals().get(literal);
  }

  /**
   * Checks whether the given value is an enumeration literal or collection of literals and if yes,
   * returns the according literal(s) of the given {@link PropertyExpression}'s metamodel.
   * Else just returns the given value.
   * @param value
   *          The property value to check
   * @param property
   *          The {@link PropertyExpression} for the property the value is for.
   * @return The fitting enumeration literal(s) from the given {@link PropertyExpression}'s metamodel,
   *         or the original given value if not an enumeration.
   */
  @SuppressWarnings("unchecked")
  private static Object getValidValue(Object value, PropertyExpression<?> property) {
    Object result = value;
    if (result != null) {
      if (property.getType() instanceof EnumerationExpression) {
        EnumerationExpression enumExpression = (EnumerationExpression) property.getType();
        if (result instanceof Collection) {
          Collection<EnumerationLiteralExpression> fromOtherMetamodel = (Collection<EnumerationLiteralExpression>) result;
          Collection<EnumerationLiteralExpression> fromCurrentMetamodel = Lists.newArrayList();
          for (EnumerationLiteralExpression sourceEnum : fromOtherMetamodel) {
            fromCurrentMetamodel.add(enumExpression.findLiteralByPersistentName(sourceEnum.getPersistentName()));
          }
          result = fromCurrentMetamodel;
        }
        else {
          EnumerationLiteralExpression fromOtherMetamodel = (EnumerationLiteralExpression) value;
          result = enumExpression.findLiteralByPersistentName(fromOtherMetamodel.getPersistentName());
        }
      }
      else {
        if (BuiltinPrimitiveType.INTEGER.equals(property.getType())) {
          result = Integer.valueOf(((BigInteger) value).intValue());
        }
      }
    }
    if (property.getUpperBound() > 1) {
      return result instanceof Collection ? result : Collections.singletonList(result);
    }
    else {
      return result;
    }
  }

}
