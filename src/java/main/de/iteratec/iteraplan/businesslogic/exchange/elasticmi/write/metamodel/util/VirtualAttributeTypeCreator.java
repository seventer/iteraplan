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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticmi.io.mapper.xls.XlsMappingUtil;
import de.iteratec.iteraplan.elasticmi.metamodel.common.impl.atomic.AtomicDataType;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RValueTypeExpression;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.TextAT;


/**
 *In an excel file, a string describes an {@link AttributeType} the {@link VirtualAttributeTypeCreator} creates out of that string a {@link VirtualAttributeType} which can later used
 */
public final class VirtualAttributeTypeCreator {

  private static final Set<String> NUMBER_AT_TYPES = Sets.newHashSet(BigInteger.class.getName(), BigDecimal.class.getName(), AtomicDataType.INTEGER
                                                       .type().getPersistentName(), AtomicDataType.DECIMAL.type().getPersistentName());
  private static final Set<String> DATE_AT_TYPES   = Sets.newHashSet(Date.class.getName(), AtomicDataType.DATE.type().getPersistentName(),
                                                       AtomicDataType.DATE_TIME.type().getPersistentName());
  private static final Set<String> TEXT_AT_TYPES   = Sets.newHashSet(String.class.getName(), AtomicDataType.STRING.type().getPersistentName(),
                                                       AtomicDataType.RICH_TEXT.type().getPersistentName());

  private VirtualAttributeTypeCreator() {
    //nothing to do here
  }

  //Pattern which the string has to match, to be a valid AttributeType definition
  //Note: The pattern is iteraplan-version specific, but for now we only have one version to support.
  //Once more than one excel version is available, the pattern should be provided by the strategy
  //which supports the version of excel being imported.
  private static final Pattern  ATTRIBUTE_TYPE_DEFINITION = Pattern.compile(".*\\[[0,1]\\.\\.[1,\\*]\\]:.*");

  private static final String[] PREDEFINED_FEATURES       = { "id", "name", "description", "runtimePeriod", "position", "typeOfStatus",
      "lastModificationTime", "lastModificationUser", "parent", "iteraplan_InformationSystemInterfaceID", "availableForInterfaces" };

  /**
   * Create a {@link VirtualAttributeType} out of a String otherwise return null
   * @param attributeTypeDefinition is the String that defines an {@link AttributeType}
   * @return {@link VirtualAttributeType} or <code>null</code> if <b>attributeTypeDefinition</b> is invalid
   */
  public static VirtualAttributeType createAttributeType(String attributeTypeDefinition) {
    if (!attributeTypeDefinition.matches(ATTRIBUTE_TYPE_DEFINITION.pattern())) {
      return null;
    }
    return createVirtualAttributeTypeFromDefinition(attributeTypeDefinition);
  }

  /**
   * Creates a virtual attribute type from a property expression.
   * @param fromProperty
   *    The source property expression.
   * @return
   *    The resulting virtual attribute type, or <b>null</b>
   *    if the value type of the property can not be mapped to
   *    an attribute type, or if the property is predefined.
   */
  public static VirtualAttributeType createAttributeType(RPropertyExpression fromProperty) {
    Class<? extends AttributeType> clazz = resolveAtClass(fromProperty.getType());
    if (clazz == null || isPredefinedFeature(fromProperty.getPersistentName())) {
      return null;
    }
    return new VirtualAttributeType(clazz, fromProperty.getPersistentName(), fromProperty.getLowerBound() > 0, fromProperty.getUpperBound() > 1);
  }

  private static boolean isPredefinedFeature(String name) {
    return Arrays.asList(PREDEFINED_FEATURES).contains(name);
  }

  private static Class<? extends AttributeType> resolveAtClass(RValueTypeExpression<?> vte) {
    return resolveAtClass(vte.getPersistentName());
  }

  private static Class<? extends AttributeType> resolveAtClass(String typePersistentName) {
    if (NUMBER_AT_TYPES.contains(typePersistentName)) {
      return NumberAT.class;
    }
    else if (DATE_AT_TYPES.contains(typePersistentName)) {
      return DateAT.class;
    }
    else if (TEXT_AT_TYPES.contains(typePersistentName)) {
      return TextAT.class;
    }
    else if (typePersistentName.matches(".*" + EnumAT.class.getName() + ".*")) {
      return EnumAT.class;
    }
    return null;
  }

  /**
   * Method to create a {@link VirtualAttributeType} out of a String
   * @param attributeTypeDefinition
   * @return {@link AttributeType}
   */
  private static VirtualAttributeType createVirtualAttributeTypeFromDefinition(String attributeTypeDefinition) {

    String attributeName = XlsMappingUtil.INSTANCE.extractPersistentNameFromTechnicalFeatureString(attributeTypeDefinition);
    //Do not import attribute if it is a predefined feature like e.g. id or runtimePeriod
    if (isPredefinedFeature(attributeName)) {
      return null;
    }
    String typeDefinition = attributeTypeDefinition.substring(attributeTypeDefinition.lastIndexOf("]:") + 2).trim();
    Class<? extends AttributeType> typeOfAttribute = resolveAtClass(typeDefinition);
    if (typeOfAttribute == null) {
      return null;
    }
    boolean mandatory = XlsMappingUtil.INSTANCE.isFeatureMandatory(attributeTypeDefinition);
    boolean multivalue = XlsMappingUtil.INSTANCE.isFeatureMultivalue(attributeTypeDefinition);
    return new VirtualAttributeType(typeOfAttribute, attributeName, mandatory, multivalue);
  }

}
