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
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.ElasticMiIteraplanMapping;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.HbMappedClass;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.HbMappedProperty;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.WMetamodelExport;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticmi.diff.model.CreateDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.PropertyChange;
import de.iteratec.iteraplan.elasticmi.diff.model.PropertyInit;
import de.iteratec.iteraplan.elasticmi.diff.model.UpdateDiff;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.common.impl.atomic.AtomicDataType;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.model.ValueExpression;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.Release;
import de.iteratec.iteraplan.model.Transport;


public class BuiltinPropertySetter extends AIteraplanInstanceWriter implements CreateOrUpdateDiffHandler {

  private static final Logger               LOGGER                      = Logger.getIteraplanLogger(BuiltinPropertySetter.class);

  private static final Set<String>          INAPPLICABLE_PROPERTY_NAMES = Sets.newHashSet(ElasticMiConstants.PERSISTENT_NAME_LAST_MODIFICATION_USER,
                                                                            ElasticMiConstants.PERSISTENT_NAME_LAST_MODIFICATION_TIME,
                                                                            ElasticMiConstants.PERSISTENT_NAME_ID,
                                                                            WMetamodelExport.INFORMATION_FLOW_ISI_ID);

  private final BuildingBlockServiceLocator buildingBlockServiceLocator;

  public BuiltinPropertySetter(ElasticMiIteraplanMapping mapping, Map<BigInteger, BuildingBlock> id2bbMap,
      BuildingBlockServiceLocator buildingBlockServiceLocator, MessageListener listener) {
    super(mapping, id2bbMap, listener);
    this.buildingBlockServiceLocator = buildingBlockServiceLocator;
  }

  @Override
  protected Logger getLogger() {
    return LOGGER;
  }

  public boolean handleDiff(CreateOrUpdateDiff diff) {
    //TODO merge methods if possible
    return diff.isLeft() ? handleCreateDiff(diff.getLeft()) : handleUpdateDiff(diff.getRight());
  }

  /**
   * Sets the built-in properties from a {@link CreateDiff} (meaning for newly created elements).
   */
  private boolean handleCreateDiff(CreateDiff diff) {
    LOGGER.debug("Setting built-in properties for \"{0}\"...", diff.getObjectExpression());
    ObjectExpression expression = diff.getObjectExpression();
    Object instance = getInstanceForExpression(expression);
    if (isInformationflowCase(diff)) {
      RPropertyExpression directionProp = diff.getStructuredType().findPropertyByPersistentName("direction");
      ElasticValue<ValueExpression> vals = directionProp.apply(expression);
      ValueExpression directionValue = vals.isOne() ? vals.getOne() : null;
      setInformationFlowDirectionProperty(expression, diff.getStructuredType(), directionProp, directionValue);
    }
    else if (instance != null) {
      HbMappedClass hbClass = getHbClass(diff.getStructuredType());

      for (PropertyInit propInit : diff.getPropertyInits()) {
        if (isApplicableProperty(diff.getStructuredType(), propInit.getProperty())) {
          Object newVal = checkValues(propInit.getProperty().getType(), propInit.getValue());
          newVal = processPropertyValue(newVal, propInit.getProperty(), diff.getStructuredType());
          setBuiltinProperty(newVal, propInit.getProperty(), instance, hbClass);

          if (isNameProperty(propInit.getProperty()) && instance instanceof Release) {
            setVersionForRelease((Release) instance, propInit.getValue().getOne().asString());
          }
        }
      }
      LOGGER.debug("Built-in properties for \"{0}\" set.", expression);
    }
    else {
      LOGGER.debug("No instance found for expression \"{0}\". Ignoring.", expression);
    }
    return true;
  }

  /**
   * Sets the built-in properties from a {@link UpdateDiff}.
   */
  private boolean handleUpdateDiff(UpdateDiff diff) {
    LOGGER.debug("Built-in properties to be set for \"{0}\".", diff.getLeftObjectExpression());
    ObjectExpression expression = diff.getLeftObjectExpression();
    HbMappedClass hbClass = getHbClass(diff.getStructuredType());
    Object instance = getInstanceForExpression(expression);

    if (isInformationflowCase(diff)) {
      for (PropertyChange change : diff.getPropertyChanges()) {
        PropertyChange filteredChange = getMergeStrategy().filterPropertyChange(diff, change);
        if (filteredChange.isActualChange() && filteredChange.getProperty().getPersistentName().equals("direction")) {
          ValueExpression directionVal = filteredChange.getRightValue().isOne() ? filteredChange.getRightValue().getOne() : null;
          setInformationFlowDirectionProperty(expression, diff.getStructuredType(), filteredChange.getProperty(), directionVal);
        }
      }
    }
    else {
      for (PropertyChange change : diff.getPropertyChanges()) {
        PropertyChange filteredChange = getMergeStrategy().filterPropertyChange(diff, change);
        RPropertyExpression prop = filteredChange.getProperty();
        if (filteredChange.isActualChange() && isApplicableProperty(diff.getStructuredType(), prop)) {
          ElasticValue<ValueExpression> rightValue = filteredChange.getRightValue();
          if (isNameProperty(prop) && instance instanceof Release) {
            setVersionForRelease((Release) instance, (String) checkValues(prop.getType(), rightValue));
          }
          Object newValue = processPropertyValue(checkValues(prop.getType(), rightValue), prop, diff.getStructuredType());
          setBuiltinProperty(newValue, prop, instance, hbClass);
        }
      }
    }

    LOGGER.debug("Built-in properties for \"{0}\" set.", diff.getLeftObjectExpression());
    return true;
  }

  private void setInformationFlowDirectionProperty(ObjectExpression expression, RStructuredTypeExpression type,
                                                   RPropertyExpression directionProperty, ValueExpression directionValue) {
    Direction direction = (Direction) checkValue(directionProperty.getType(), directionValue);
    if (direction == null) {
      direction = Direction.NO_DIRECTION;
    }
    Object infoFlow = getInstanceForExpression(expression);
    if (infoFlow != null) {
      Transport transport = (Transport) infoFlow;
      transport.setDirection(direction);
      return;
    }

    InformationSystemInterface isi = getIsiForInfoFlow(expression, type);
    if (isi != null) {
      isi.setInterfaceDirection(direction);
    }
    else {
      logWarning("Could not find InformationSystemInterface for InformationFlow \"{0}\". Direction could not be set.", expression);
    }
  }

  private InformationSystemInterface getIsiForInfoFlow(ObjectExpression expression, RStructuredTypeExpression type) {
    InformationSystemInterface isi = null;
    RRelationshipEndExpression toISI = type.findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_ISI);
    if (toISI != null) {
      ElasticValue<ObjectExpression> isis = toISI.apply(expression);
      isi = isis.isOne() ? (InformationSystemInterface) getInstanceForExpression(isis.getOne()) : null;
    }

    if (isi == null) {
      RPropertyExpression isiIdProp = type.findPropertyByPersistentName(WMetamodelExport.INFORMATION_FLOW_ISI_ID);
      if (isiIdProp != null) {
        Integer isiID = (Integer) checkValues(isiIdProp.getType(), isiIdProp.apply(expression));
        isi = buildingBlockServiceLocator.getIsiService().loadObjectByIdIfExists(isiID);
      }
    }
    return isi;
  }

  private boolean isNameProperty(RPropertyExpression property) {
    return property.getPersistentName().equals(ElasticMiConstants.PERSISTENT_NAME_NAME);
  }

  private void setVersionForRelease(Release release, String versionedName) {
    if (versionedName.indexOf('#') != -1) {
      String[] nameAndVersion = versionedName.split("#");
      if (nameAndVersion.length == 2) {
        String version = nameAndVersion[1].trim();
        release.setVersion(version);
        return;
      }
    }
    release.setVersion("");
  }

  private Object processPropertyValue(Object value, RPropertyExpression property, RStructuredTypeExpression typeExpression) {
    if (isNameProperty(property)) {
      return processNames(value, typeExpression);
    }
    else if (property.getPersistentName().equals("interfaceDirection") && value == null) {
      return Direction.NO_DIRECTION;
    }
    else if (property.getType().equals(AtomicDataType.STRING.type()) && value == null) {
      return "";
    }
    return value;
  }

  private String processNames(Object value, RStructuredTypeExpression typeExpression) {
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

  private boolean isApplicableProperty(RStructuredTypeExpression type, RPropertyExpression property) {
    return !getIteraplanMapping().isDerivedFromAT(wProperty(type, property)) && !INAPPLICABLE_PROPERTY_NAMES.contains(property.getPersistentName());
  }

  private void setBuiltinProperty(Object newValue, RPropertyExpression property, Object instance, HbMappedClass hbClass) {
    LOGGER.debug("Setting value \"{0}\" for property \"{1}\".", newValue, property);
    HbMappedProperty hbProp = findHbMappedProperty(hbClass, property);
    Object owningInstance = findOwningInstance(hbClass, hbProp, instance);

    try {
      hbProp.getSetMethod().invoke(owningInstance, newValue);
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
