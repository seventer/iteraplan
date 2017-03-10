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

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.MessageFormat;

import com.google.common.collect.BiMap;

import de.iteratec.iteraplan.businesslogic.exchange.common.ResultMessages;
import de.iteratec.iteraplan.businesslogic.exchange.common.ResultMessages.ErrorLevel;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.MetamodelExport;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedClass;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedProperty;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.BaseDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.LeftSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.RightSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.TwoSidedDiff;
import de.iteratec.iteraplan.model.BusinessMapping;


public abstract class IteraplanChangeOperation {

  private static final Logger                           LOGGER               = Logger.getIteraplanLogger(IteraplanChangeOperation.class);

  private static final String                           BM_ISR_REL_END_NAME  = "informationSystemRelease";
  private static final String                           BM_BP_REL_END_NAME   = "businessProcess";
  private static final String                           BM_BU_REL_END_NAME   = "businessUnit";
  private static final String                           BM_PROD_REL_END_NAME = "product";

  private final IteraplanMapping                        mapping;
  private final BiMap<Object, UniversalModelExpression> instanceMapping;
  private final ResultMessages                          result;

  public IteraplanChangeOperation(IteraplanMapping mapping, BiMap<Object, UniversalModelExpression> instanceMapping) {
    this.mapping = mapping;
    this.instanceMapping = instanceMapping;
    this.result = new ResultMessages();
  }

  public ResultMessages getResult() {
    return result;
  }

  /**
   * Execute this operation on the given BaseDiff
   * @param diff
   */
  public void execute(BaseDiff diff) {
    if (diff instanceof RightSidedDiff) {
      handleRightSidedDiff((RightSidedDiff) diff);
    }
    else if (diff instanceof LeftSidedDiff) {
      handleLeftSidedDiff((LeftSidedDiff) diff);
    }
    else if (diff instanceof TwoSidedDiff) {
      handleTwoSidedDiff((TwoSidedDiff) diff);
    }
    else {
      LOGGER.error("Illegal Diff-Class: {0}", diff.getClass().getSimpleName());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }
  }

  /**
   * How this operation handles {@link RightSidedDiff}s.
   * Standard is "not supported", overwrite for specific behavior.
   * @param rightSidedDiff
   */
  protected void handleRightSidedDiff(RightSidedDiff rightSidedDiff) {
    LOGGER.error("Diff-Class \"{0}\" not supported.", rightSidedDiff.getClass().getSimpleName());
    throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
  }

  /**
   * How this operation handles {@link LeftSidedDiff}s.
   * Standard is "not supported", overwrite for specific behavior.
   * @param leftSidedDiff
   */
  protected void handleLeftSidedDiff(LeftSidedDiff leftSidedDiff) {
    LOGGER.error("Diff-Class \"{0}\" not supported.", leftSidedDiff.getClass().getSimpleName());
    throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
  }

  /**
   * How this operation handles {@link TwoSidedDiff}s.
   * Standard is "not supported", overwrite for specific behavior.
   * @param twoSidedDiff
   */
  protected void handleTwoSidedDiff(TwoSidedDiff twoSidedDiff) {
    LOGGER.error("Diff-Class \"{0}\" not supported.", twoSidedDiff.getClass().getSimpleName());
    throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
  }

  /**
   * Find {@link HbMappedClass} for a {@link UniversalTypeExpression}.
   * @return the {@link HbMappedClass}
   */
  protected final HbMappedClass getHbClass(UniversalTypeExpression typeExpression) {
    LOGGER.debug("Getting HbMappedClass for \"{0}\"...", typeExpression);
    HbMappedClass hbClass = null;
    if (typeExpression instanceof SubstantialTypeExpression) {
      hbClass = mapping.getSubstantialTypes().get(typeExpression);
      if (hbClass.hasReleaseClass()) {
        hbClass = hbClass.getReleaseClass();
      }
    }
    else {
      hbClass = mapping.getRelationshipTypes().get(typeExpression);
    }
    LOGGER.debug("\"{0}\" => \"{1}\"", typeExpression, result);
    return hbClass;
  }

  /**
   * Find {@link HbMappedProperty} property for a {@link FeatureExpression}.
   * @return the {@link HbMappedProperty}
   */
  protected final HbMappedProperty findHbMappedProperty(HbMappedClass hbClass, FeatureExpression<?> expression) {
    LOGGER.debug("Finding HbMappedProperty for \"{0}\" on \"{1}\"...", expression, hbClass);
    String persistentName = expression.getPersistentName();

    HbMappedProperty hbProp = hbClass.getProperty(persistentName);
    if (hbProp == null && hbClass.isReleaseClass() && hbClass.getReleaseBase().getProperty(persistentName) != null) {
      LOGGER.debug("Special case: release - \"{0}\"", persistentName);
      return hbClass.getReleaseBase().getProperty(persistentName);
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
   * @param value
   *          the value to check
   * @return the checked value 
   */
  protected final Object checkValue(Object value) {
    if (value instanceof BigInteger) {
      return Integer.valueOf(((BigInteger) value).intValue());
    }
    else if (value instanceof EnumerationLiteralExpression) {
      return mapping.getEnumerationLiterals().get(value);
    }
    return value;
  }

  protected IteraplanMapping getIteraplanMapping() {
    return mapping;
  }

  protected Object getInstanceForExpression(UniversalModelExpression modelExpression) {
    return instanceMapping.inverse().get(modelExpression);
  }

  protected void putInstanceForExpression(UniversalModelExpression modelExpression, Object instance) {
    instanceMapping.put(instance, modelExpression);
  }

  protected void removeInstanceMapping(UniversalModelExpression modelExpression, Object instance) {
    if (modelExpression.equals(instanceMapping.get(instance))) {
      instanceMapping.remove(instance);
    }
  }

  protected boolean isInformationflowCase(BaseDiff diff) {
    return MetamodelExport.INFORMATION_FLOW.equals(diff.getType().getPersistentName());
  }

  protected boolean isInvalidBusinessMapping(UniversalTypeExpression businessMappingType, UniversalModelExpression bmExpression) {
    if (BusinessMapping.class.getSimpleName().equals(businessMappingType.getPersistentName())) {
      RelationshipEndExpression isrEnd = businessMappingType.findRelationshipEndByName(BM_ISR_REL_END_NAME);
      UniversalModelExpression isr = isrEnd == null ? null : bmExpression.getConnected(isrEnd);
      RelationshipEndExpression bpEnd = businessMappingType.findRelationshipEndByName(BM_BP_REL_END_NAME);
      UniversalModelExpression bp = bpEnd == null ? null : bmExpression.getConnected(bpEnd);
      RelationshipEndExpression buEnd = businessMappingType.findRelationshipEndByName(BM_BU_REL_END_NAME);
      UniversalModelExpression bu = buEnd == null ? null : bmExpression.getConnected(buEnd);
      RelationshipEndExpression prodEnd = businessMappingType.findRelationshipEndByName(BM_PROD_REL_END_NAME);
      UniversalModelExpression prod = prodEnd == null ? null : bmExpression.getConnected(prodEnd);

      if (isr == null || (bp == null && bu == null && prod == null)) {
        String isrName = isr == null ? null : (String) isr.getValue(MixinTypeNamed.NAME_PROPERTY);
        String bpName = bp == null ? null : (String) bp.getValue(MixinTypeNamed.NAME_PROPERTY);
        String buName = bu == null ? null : (String) bu.getValue(MixinTypeNamed.NAME_PROPERTY);
        String prodName = prod == null ? null : (String) prod.getValue(MixinTypeNamed.NAME_PROPERTY);
        logError("BusinessMapping (IS: {0} / BP: {1} / BU: {2} / Prod: {3}) is invalid and cannot be saved.", isrName, bpName, buName, prodName);
        return true;
      }
    }
    return false;
  }

  protected Logger getLogger() {
    return LOGGER;
  }

  protected void logInfo(String format, Object... params) {
    String message = MessageFormat.format(format, params);
    getLogger().info(message);
    result.addMessage(ErrorLevel.INFO, message);
  }

  protected void logWarning(String format, Object... params) {
    String message = MessageFormat.format(format, params);
    getLogger().warn(message);
    result.addMessage(ErrorLevel.WARNING, message);
  }

  protected void logError(String format, Object... params) {
    String message = MessageFormat.format(format, params);
    getLogger().error(message);
    result.addMessage(ErrorLevel.ERROR, message);
  }

}
