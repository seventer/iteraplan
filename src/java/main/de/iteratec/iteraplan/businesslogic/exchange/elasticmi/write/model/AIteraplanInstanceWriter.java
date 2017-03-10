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

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.common.SimpleMessage;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.ElasticMiIteraplanMapping;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.HbMappedClass;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.HbMappedProperty;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.WMetamodelExport;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticmi.diff.model.MergeStrategy;
import de.iteratec.iteraplan.elasticmi.diff.model.ObjectDiff;
import de.iteratec.iteraplan.elasticmi.messages.Message.Severity;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.common.ValueTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.REnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RFeatureExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression.OriginalWType;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WEnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WNominalEnumerationExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WUniversalTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.model.ValueExpression;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.RuntimePeriod;


public abstract class AIteraplanInstanceWriter {

  private static final Logger                  LOGGER = Logger.getIteraplanLogger(AIteraplanInstanceWriter.class);

  private final ElasticMiIteraplanMapping      mapping;
  private final Map<BigInteger, BuildingBlock> id2bbMap;
  private MergeStrategy                        strategy;
  private final MessageListener                listener;

  public AIteraplanInstanceWriter(ElasticMiIteraplanMapping mapping, Map<BigInteger, BuildingBlock> id2bbMap, MessageListener listener) {
    this.mapping = mapping;
    this.id2bbMap = id2bbMap;
    this.listener = listener;
  }

  public void setMergeStrategy(MergeStrategy strategy) {
    this.strategy = strategy;
  }

  protected MergeStrategy getMergeStrategy() {
    return this.strategy;
  }

  /**
   * Find {@link HbMappedClass} for a {@link RStructuredTypeExpression}.
   * @return the {@link HbMappedClass}
   */
  protected final HbMappedClass getHbClass(RStructuredTypeExpression typeExpression) {
    LOGGER.debug("Getting HbMappedClass for \"{0}\"...", typeExpression);
    HbMappedClass hbClass = null;
    WUniversalTypeExpression wUTE = getIteraplanMapping().getMetamodel().findUniversalTypeByPersistentName(typeExpression.getPersistentName());
    if (OriginalWType.RELATIONSHIP.equals(typeExpression.getOriginalWType())) {
      hbClass = mapping.getRelationshipTypes().get(wUTE);
    }
    else {
      hbClass = mapping.getSubstantialTypes().get(wUTE);
      if (hbClass.hasReleaseClass()) {
        hbClass = hbClass.getReleaseClass();
      }
    }
    //LOGGER.debug("\"{0}\" => \"{1}\"", typeExpression, result);
    return hbClass;
  }

  /**
   * Find {@link HbMappedProperty} property for a {@link RPropertyExpression}.
   * @return the {@link HbMappedProperty}
   */
  protected final HbMappedProperty findHbMappedProperty(HbMappedClass hbClass, RFeatureExpression<?, ?> expression) {
    LOGGER.debug("Finding HbMappedProperty for \"{0}\" on \"{1}\"...", expression, hbClass);
    String persistentName = expression.getPersistentName();

    HbMappedProperty hbProp = hbClass.getProperty(persistentName);
    if (hbProp == null && hbClass.isReleaseClass() && hbClass.getReleaseBase().getProperty(persistentName) != null) {
      LOGGER.debug("Special case: release - \"{0}\"", persistentName);
      return hbClass.getReleaseBase().getProperty(persistentName);
    }
    else if (InformationSystemInterface.class.getSimpleName().equals(hbClass.getClassName())
        && WMetamodelExport.INFORMATION_FLOW_IS1.equals(persistentName)) {
      LOGGER.debug("Special case: interface - \"{0}\"", persistentName);
      hbProp = hbClass.getProperty("informationSystemReleaseA");
    }
    else if (InformationSystemInterface.class.getSimpleName().equals(hbClass.getClassName())
        && WMetamodelExport.INFORMATION_FLOW_IS2.equals(persistentName)) {
      LOGGER.debug("Special case: interface - \"{0}\"", persistentName);
      hbProp = hbClass.getProperty("informationSystemReleaseB");
    }
    return hbProp;
  }

  /**
   * Returns the object the given {@link HbMappedProperty property} is owned by, which is either
   * the given instance or its base-class instance (i.e. InformationSystem for InformationSystemRelease).
   * @param hbClass
   *          {@link HbMappedClass} for the given instance 
   * @param prop
   *          {@link HbMappedProperty} for the property to set
   * @param instance
   *          The object the property is supposed to be set on
   * @return Either the given {@code instance} or its base-class instance (i.e. InformationSystem for InformationSystemRelease),
   *         whichever is applicable.
   */
  protected final Object findOwningInstance(HbMappedClass hbClass, HbMappedProperty prop, Object instance) {
    LOGGER.debug("Finding owning instance for property \"{0}\" based on \"{1}\"...", prop, instance);
    Object propertyHolder = instance;
    Class<?> propContainingClass = prop.getContainingClass().getMappedClass();
    if (propContainingClass != null && !propContainingClass.isInstance(propertyHolder) && hbClass.isReleaseClass()) {
      LOGGER.debug("Special case release...");
      propertyHolder = getReleaseBase(hbClass, propertyHolder);
    }
    LOGGER.debug("Found owning instance \"{0}\".", propertyHolder);
    return propertyHolder;
  }

  /**
   * Returns the release base for the given release
   * @param hbClass
   *          {@link HbMappedClass} of the given release
   * @param release
   *          The release to get the base for
   * @return The release base
   */
  protected final Object getReleaseBase(HbMappedClass hbClass, Object release) {
    HbMappedProperty correctInstanceProperty = hbClass.getReleaseBaseProperty();
    try {
      return correctInstanceProperty.getGetMethod().invoke(release);
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

  /**
   * Checks the given property value and, if necessary, converts it to the right type for setting
   * into iteraplan instances.
   * @param valueType
   *          the type of this value expression
   * @param valueExpression
   *          the value to check
   * @return the checked value 
   */
  protected final Object checkValue(ValueTypeExpression<?> valueType, ValueExpression valueExpression) {
    if (valueExpression == null) {
      return null;
    }
    else if (valueExpression.isInteger()) {
      return Integer.valueOf(valueExpression.asInteger().intValue());
    }
    else if (valueExpression.isEnumerationLiteral()) {
      WEnumerationLiteralExpression wLiteral = wLiteral(valueType, valueExpression.asEnumerationLiteral());
      Object result = mapping.getEnumerationLiterals().get(wLiteral);
      if (result != null) {
        return result;
      }
      return getIteraplanMapping().getAdditionalEnumerationLiterals().get(wLiteral);
    }
    else if (valueExpression.isDuration()) {
      return new RuntimePeriod(valueExpression.asDuration().getStart(), valueExpression.asDuration().getEnd());
    }
    return valueExpression.getValue();
  }

  protected final Object checkValues(ValueTypeExpression<?> valueType, ElasticValue<ValueExpression> values) {
    if (values.isNone()) {
      return null;
    }
    else if (values.isOne()) {
      return checkValue(valueType, values.getOne());
    }
    Set<Object> vals = Sets.newHashSet();
    for (ValueExpression ve : values.getMany()) {
      Object checkedValue = checkValue(valueType, ve);
      if (checkedValue != null) {
        vals.add(checkedValue);
      }
    }
    return vals;
  }

  protected ElasticMiIteraplanMapping getIteraplanMapping() {
    return mapping;
  }

  protected BuildingBlock getInstanceForExpression(ObjectExpression modelExpression) {
    return modelExpression == null ? null : id2bbMap.get(modelExpression.getId());
  }

  protected void putInstanceForExpression(ObjectExpression modelExpression, BuildingBlock instance) {
    id2bbMap.put(modelExpression.getId(), instance);
  }

  protected void removeInstanceMapping(ObjectExpression modelExpression, Object instance) {
    id2bbMap.remove(modelExpression.getId());
  }

  protected boolean isInformationflowCase(ObjectDiff diff) {
    return WMetamodelExport.INFORMATION_FLOW.equals(diff.getStructuredType().getPersistentName());
  }

  protected static RPropertyExpression nameProperty(RStructuredTypeExpression type) {
    return type.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
  }

  protected Logger getLogger() {
    return LOGGER;
  }

  protected void logInfo(String format, Object... params) {
    String message = MessageFormat.format(format, params);
    getLogger().info(message);
    listener.onMessage(new SimpleMessage(Severity.INFO, message));
  }

  protected void logWarning(String format, Object... params) {
    String message = MessageFormat.format(format, params);
    getLogger().warn(message);
    listener.onMessage(new SimpleMessage(Severity.WARNING, message));
  }

  protected void logError(String format, Object... params) {
    String message = MessageFormat.format(format, params);
    getLogger().error(message);
    listener.onMessage(new SimpleMessage(Severity.ERROR, message));
  }

  protected WEnumerationLiteralExpression wLiteral(ValueTypeExpression<?> enumeration, REnumerationLiteralExpression rLiteral) {
    return ((WNominalEnumerationExpression) getIteraplanMapping().getMetamodel().findValueTypeByPersistentName(enumeration.getPersistentName()))
        .findLiteralByPersistentName(rLiteral.getPersistentName());
  }

  protected WPropertyExpression wProperty(RStructuredTypeExpression rType, RPropertyExpression rProperty) {
    return getIteraplanMapping().getMetamodel().findUniversalTypeByPersistentName(rType.getPersistentName())
        .findPropertyByPersistentName(rProperty.getPersistentName());
  }

}
