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
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.MetamodelExport;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemInterfaceService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedClass;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedProperty;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffPart;
import de.iteratec.iteraplan.elasticeam.model.compare.RightSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.TwoSidedDiff;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.Release;
import de.iteratec.iteraplan.model.Transport;


/**
 * Sets the built-in properties of an iteraplan instance according to a {@link de.iteratec.iteraplan.elasticeam.model.compare.BaseDiff Diff}.
 */
public class SetBuiltinPropertiesOp extends IteraplanChangeOperation {

  private static final Logger                     LOGGER                      = Logger.getIteraplanLogger(SetBuiltinPropertiesOp.class);

  private static final Set<String>                INAPPLICABLE_PROPERTY_NAMES = Sets.newHashSet(
                                                                                  UniversalTypeExpression.ID_PROPERTY.getPersistentName(),
                                                                                  "lastModificationUser", "lastModificationTime",
                                                                                  MetamodelExport.INFORMATION_FLOW_ISI_ID);

  private final InformationSystemInterfaceService isiService;

  public SetBuiltinPropertiesOp(IteraplanMapping mapping, BiMap<Object, UniversalModelExpression> instanceMapping,
      InformationSystemInterfaceService isiService) {
    super(mapping, instanceMapping);
    this.isiService = isiService;
  }

  protected Logger getLogger() {
    return LOGGER;
  }

  /**
   * Sets the built-in properties from a {@link RightSidedDiff} (meaning for newly created elements).
   */
  protected void handleRightSidedDiff(RightSidedDiff diff) {
    UniversalModelExpression expression = diff.getExpression();
    LOGGER.debug("Setting built-in properties for \"{0}\"...", expression);
    Object instance = getInstanceForExpression(expression);
    if (isInformationflowCase(diff)) {
      Object directionValue = expression.getValue(diff.getType().findPropertyByPersistentName("direction"));
      setInformationFlowDirectionProperty(expression, diff.getType(), directionValue);
    }
    else if (instance != null) {
      HbMappedClass hbClass = getHbClass(diff.getType());

      for (PropertyExpression<?> property : diff.getType().getProperties()) {
        if (isApplicableProperty(property)) {
          Object newValue;
          if (property.getUpperBound() > 1) {
            newValue = expression.getValues(property);
          }
          else {
            newValue = processPropertyValue(expression.getValue(property), property, diff.getType());
          }
          setBuiltinProperty(newValue, property, instance, hbClass);

          if (isNameProperty(property) && instance instanceof Release) {
            setVersionForRelease((Release) instance, (String) expression.getValue(property));
          }
        }
      }
      LOGGER.debug("Built-in properties for \"{0}\" set.", expression);
    }
    else {
      LOGGER.debug("No instance found for expression \"{0}\". Ignoring.", expression);
    }
  }

  /**
   * Sets the built-in properties from a {@link TwoSidedDiff}.
   */
  protected void handleTwoSidedDiff(TwoSidedDiff diff) {
    LOGGER.debug("Built-in properties to be set for \"{0}\".", diff.getLeftExpression());

    UniversalModelExpression expression = diff.getLeftExpression();
    HbMappedClass hbClass = getHbClass(diff.getType());
    Object instance = getInstanceForExpression(expression);

    if (isInformationflowCase(diff)) {
      for (DiffPart diffPart : diff.getDiffParts()) {
        if (diffPart.getFeature() instanceof PropertyExpression && diffPart.getFeature().getPersistentName().equals("direction")) {
          Object directionValue = diffPart.getRightValue();
          setInformationFlowDirectionProperty(expression, diff.getType(), directionValue);
        }
      }
    }
    else {
      for (DiffPart diffPart : diff.getDiffParts()) {
        FeatureExpression<?> feature = diffPart.getFeature();
        if (feature instanceof PropertyExpression) {
          PropertyExpression<?> property = (PropertyExpression<?>) feature;
          if (isApplicableProperty(property)) {
            if (isNameProperty(property) && instance instanceof Release) {
              setVersionForRelease((Release) instance, (String) diffPart.getRightValue());
            }
            Object newValue = processPropertyValue(diffPart.getRightValue(), property, diff.getType());
            setBuiltinProperty(newValue, property, instance, hbClass);
          }
        }
      }
    }

    LOGGER.debug("Built-in properties for \"{0}\" set.", diff.getLeftExpression());
  }

  private void setInformationFlowDirectionProperty(UniversalModelExpression expression, UniversalTypeExpression type, Object directionValue) {
    Direction direction = (Direction) checkValue(directionValue);
    Object infoFlow = getInstanceForExpression(expression);
    if (infoFlow != null) {
      Transport transport = (Transport) infoFlow;
      transport.setDirection(direction);
      logInfo("Direction of Transport \"{0}\" set to \"{1}\".", transport, direction);
      return;
    }

    InformationSystemInterface isi = getIsiForInfoFlow(expression, type);
    if (isi != null) {
      isi.setInterfaceDirection(direction);
      logInfo("Direction of InformationSystemInterface \"{0}\" set to \"{1}\".", isi, direction);
    }
    else {
      logWarning("Could not find InformationSystemInterface for InformationFlow \"{0}\". Direction could not be set.", expression);
    }
  }

  private InformationSystemInterface getIsiForInfoFlow(UniversalModelExpression expression, UniversalTypeExpression type) {
    InformationSystemInterface isi = null;
    RelationshipEndExpression toISI = type.findRelationshipEndByPersistentName(MetamodelExport.INFORMATION_FLOW_ISI);
    if (toISI != null) {
      UniversalModelExpression isiExpression = expression.getConnected(toISI);
      isi = (InformationSystemInterface) getInstanceForExpression(isiExpression);
    }

    if (isi == null) {
      PropertyExpression<?> isiIdProp = type.findPropertyByPersistentName(MetamodelExport.INFORMATION_FLOW_ISI_ID);
      if (isiIdProp != null) {
        Integer isiID = (Integer) checkValue(expression.getValue(isiIdProp));
        isi = isiService.loadObjectByIdIfExists(isiID);
      }
    }
    return isi;
  }

  private boolean isNameProperty(PropertyExpression<?> property) {
    return property.getPersistentName().equals("name");
  }

  private void setVersionForRelease(Release release, String versionedName) {
    if (versionedName.indexOf('#') != -1) {
      String[] nameAndVersion = versionedName.split("#");
      if (nameAndVersion.length == 2) {
        String version = nameAndVersion[1].trim();
        release.setVersion(version);
        logInfo("Version of \"{0}\" set to \"{1}\".", release, version);
        return;
      }
    }
    release.setVersion("");
    logInfo("No version for \"{0}\" set.", release);
  }

  private Object processPropertyValue(Object value, PropertyExpression<?> property,
                                      UniversalTypeExpression typeExpression) {
    if (isNameProperty(property)) {
      return processNames(value, typeExpression);
    }
    else if (property.getPersistentName().equals("direction") && value == null) {
      return Direction.NO_DIRECTION;
    }
    else if (property.getType().equals(BuiltinPrimitiveType.STRING) && value == null) {
      return "";
    }
    return value;
  }

  private String processNames(Object value, UniversalTypeExpression typeExpression) {
    String name = value == null ? "" : (String) value;
    if (typeExpression.getPersistentName().equals("InformationSystemInterface")) {
      name = cleanUpInformationSystemInterfaceName(name);
      LOGGER.debug("Extracted information system interface name from \"{0}\" to \"{1}\".", value, name);
      return name;
    }
    if (typeExpression.getPersistentName().equals("InformationSystem") || typeExpression.getPersistentName().equals("TechnicalComponent")) {
      if (name.indexOf('#') != -1) {
        String[] split = name.split("#");
        name = split[0].trim();
        LOGGER.debug("Extracted release name from \"{0}\" to \"{1}\".", value, name);
      }
      return name;
    }
    return name;
  }

  private String cleanUpInformationSystemInterfaceName(String name) {

    // ignore everything from the last "[" to the end of the String
    String[] splitResult = name.split("\\[[^\\[]*\\]$");

    if (splitResult.length > 0) {
      return splitResult[0];
    }
    return "";
  }

  private boolean isApplicableProperty(PropertyExpression<?> property) {
    return !getIteraplanMapping().isDerivedFromAT(property) && !INAPPLICABLE_PROPERTY_NAMES.contains(property.getPersistentName());
  }

  private void setBuiltinProperty(Object newValue, PropertyExpression<?> property, Object instance, HbMappedClass hbClass) {
    LOGGER.debug("Setting value \"{0}\" for property \"{1}\".", newValue, property);
    Object valueToSet = checkValue(newValue);
    HbMappedProperty hbProp = findHbMappedProperty(hbClass, property);
    Object owningInstance = findOwningInstance(hbClass, hbProp, instance);

    try {
      hbProp.getSetMethod().invoke(owningInstance, valueToSet);
      logInfo("{0} of {1} \"{2}\" set to \"{3}\".", property.getPersistentName(), owningInstance.getClass().getSimpleName(), owningInstance,
          valueToSet);
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
}
