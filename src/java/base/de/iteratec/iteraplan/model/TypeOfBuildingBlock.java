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
package de.iteratec.iteraplan.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.common.util.StringEnumReflectionHelper;
import de.iteratec.iteraplan.model.interfaces.StatusEntity;


/**
 * Persistent enumeration class for types of building blocks. The string passed into the constructor
 * is the key for an internationalized value.
 */
public enum TypeOfBuildingBlock {

  ARCHITECTURALDOMAIN("architecturalDomain.singular"), BUSINESSDOMAIN("businessDomain.singular"), BUSINESSFUNCTION("global.business_function"), BUSINESSOBJECT(
      "businessObject.singular"), BUSINESSPROCESS("businessProcess.singular"), BUSINESSMAPPING("businessMapping.singular"), BUSINESSUNIT(
      "businessUnit.singular"), INFORMATIONSYSTEM("informationSystem.singular"), INFORMATIONSYSTEMRELEASE("informationSystemRelease.singular"), INFORMATIONSYSTEMDOMAIN(
      "informationSystemDomain.singular"), INFORMATIONSYSTEMINTERFACE("interface.singular"), INFRASTRUCTUREELEMENT("infrastructureElement.singular"), PRODUCT(
      "global.product"), PROJECT("project.singular"), TECHNICALCOMPONENT("technicalComponent.singular"), TECHNICALCOMPONENTRELEASE(
      "technicalComponentRelease.singular"), TRANSPORT("global.transport"), TCR2IEASSOCIATION(
      "technicalComponentRelease.association.infrastructureElement"), ISR2BOASSOCIATION("informationSystemRelease.association.businessObject"),

  // Dummy for the presentation tier
  DUMMY("");

  private static final Map<Class<? extends BuildingBlock>, TypeOfBuildingBlock> CLASS_TO_BB_MAP       = CollectionUtils.hashMap();
  static {
    CLASS_TO_BB_MAP.put(ArchitecturalDomain.class, ARCHITECTURALDOMAIN);
    CLASS_TO_BB_MAP.put(BusinessDomain.class, BUSINESSDOMAIN);
    CLASS_TO_BB_MAP.put(BusinessFunction.class, BUSINESSFUNCTION);
    CLASS_TO_BB_MAP.put(BusinessObject.class, BUSINESSOBJECT);
    CLASS_TO_BB_MAP.put(BusinessProcess.class, BUSINESSPROCESS);
    CLASS_TO_BB_MAP.put(BusinessMapping.class, BUSINESSMAPPING);
    CLASS_TO_BB_MAP.put(BusinessUnit.class, BUSINESSUNIT);
    CLASS_TO_BB_MAP.put(InformationSystem.class, INFORMATIONSYSTEM);
    CLASS_TO_BB_MAP.put(InformationSystemRelease.class, INFORMATIONSYSTEMRELEASE);
    CLASS_TO_BB_MAP.put(InformationSystemDomain.class, INFORMATIONSYSTEMDOMAIN);
    CLASS_TO_BB_MAP.put(InformationSystemInterface.class, INFORMATIONSYSTEMINTERFACE);
    CLASS_TO_BB_MAP.put(InfrastructureElement.class, INFRASTRUCTUREELEMENT);
    CLASS_TO_BB_MAP.put(Product.class, PRODUCT);
    CLASS_TO_BB_MAP.put(Project.class, PROJECT);
    CLASS_TO_BB_MAP.put(TechnicalComponent.class, TECHNICALCOMPONENT);
    CLASS_TO_BB_MAP.put(TechnicalComponentRelease.class, TECHNICALCOMPONENTRELEASE);
    CLASS_TO_BB_MAP.put(Transport.class, TRANSPORT);
    CLASS_TO_BB_MAP.put(Tcr2IeAssociation.class, TCR2IEASSOCIATION);
    CLASS_TO_BB_MAP.put(Isr2BoAssociation.class, ISR2BOASSOCIATION);
  }

  private static final Map<String, TypeOfBuildingBlock>                         PROPERTY_TO_BB_MAP    = CollectionUtils.hashMap();
  static {
    PROPERTY_TO_BB_MAP.put(Constants.BB_ARCHITECTURALDOMAIN, ARCHITECTURALDOMAIN);
    PROPERTY_TO_BB_MAP.put(Constants.BB_ARCHITECTURALDOMAIN_PLURAL, ARCHITECTURALDOMAIN);
    PROPERTY_TO_BB_MAP.put(Constants.BB_BUSINESSDOMAIN, BUSINESSDOMAIN);
    PROPERTY_TO_BB_MAP.put(Constants.BB_BUSINESSDOMAIN_PLURAL, BUSINESSDOMAIN);
    PROPERTY_TO_BB_MAP.put(Constants.BB_BUSINESSFUNCTION, BUSINESSFUNCTION);
    PROPERTY_TO_BB_MAP.put(Constants.BB_BUSINESSFUNCTION_PLURAL, BUSINESSFUNCTION);
    PROPERTY_TO_BB_MAP.put(Constants.BB_BUSINESSMAPPING, BUSINESSMAPPING);
    PROPERTY_TO_BB_MAP.put(Constants.BB_BUSINESSMAPPING_PLURAL, BUSINESSMAPPING);
    PROPERTY_TO_BB_MAP.put(Constants.BB_BUSINESSOBJECT, BUSINESSOBJECT);
    PROPERTY_TO_BB_MAP.put(Constants.BB_BUSINESSOBJECT_PLURAL, BUSINESSOBJECT);
    PROPERTY_TO_BB_MAP.put(Constants.BB_BUSINESSPROCESS, BUSINESSPROCESS);
    PROPERTY_TO_BB_MAP.put(Constants.BB_BUSINESSPROCESS_PLURAL, BUSINESSPROCESS);
    PROPERTY_TO_BB_MAP.put(Constants.BB_BUSINESSUNIT, BUSINESSUNIT);
    PROPERTY_TO_BB_MAP.put(Constants.BB_BUSINESSUNIT_PLURAL, BUSINESSUNIT);
    PROPERTY_TO_BB_MAP.put(Constants.BB_INFORMATIONSYSTEMDOMAIN, INFORMATIONSYSTEMDOMAIN);
    PROPERTY_TO_BB_MAP.put(Constants.BB_INFORMATIONSYSTEMDOMAIN_PLURAL, INFORMATIONSYSTEMDOMAIN);
    PROPERTY_TO_BB_MAP.put(Constants.BB_INFORMATIONSYSTEMINTERFACE, INFORMATIONSYSTEMINTERFACE);
    PROPERTY_TO_BB_MAP.put(Constants.BB_INFORMATIONSYSTEMINTERFACE_PLURAL, INFORMATIONSYSTEMINTERFACE);
    PROPERTY_TO_BB_MAP.put(Constants.BB_INFORMATIONSYSTEMRELEASE, INFORMATIONSYSTEMRELEASE);
    PROPERTY_TO_BB_MAP.put(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, INFORMATIONSYSTEMRELEASE);
    PROPERTY_TO_BB_MAP.put(Constants.BB_INFRASTRUCTUREELEMENT, INFRASTRUCTUREELEMENT);
    PROPERTY_TO_BB_MAP.put(Constants.BB_INFRASTRUCTUREELEMENT_PLURAL, INFRASTRUCTUREELEMENT);
    PROPERTY_TO_BB_MAP.put(Constants.BB_PRODUCT, PRODUCT);
    PROPERTY_TO_BB_MAP.put(Constants.BB_PRODUCT_PLURAL, PRODUCT);
    PROPERTY_TO_BB_MAP.put(Constants.BB_PROJECT, PROJECT);
    PROPERTY_TO_BB_MAP.put(Constants.BB_PROJECT_PLURAL, PROJECT);
    PROPERTY_TO_BB_MAP.put(Constants.BB_TECHNICALCOMPONENTRELEASE, TECHNICALCOMPONENTRELEASE);
    PROPERTY_TO_BB_MAP.put(Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL, TECHNICALCOMPONENTRELEASE);
    PROPERTY_TO_BB_MAP.put(Constants.ASSOC_TECHNICALCOMPONENTRELEASE_TO_INFRASTRUCTUREELEMENT, TCR2IEASSOCIATION);
    PROPERTY_TO_BB_MAP.put(Constants.ASSOC_INFORMATIONSYSTEMRELEASE_TO_BUSINESSOBJECT, ISR2BOASSOCIATION);
  }

  private static final Map<String, TypeOfBuildingBlock>                         INIT_CAP_TO_BB_MAP    = CollectionUtils.hashMap();
  static {
    INIT_CAP_TO_BB_MAP.put(Constants.BB_BUSINESSDOMAIN_INITIALCAP, BUSINESSDOMAIN);
    INIT_CAP_TO_BB_MAP.put(Constants.BB_BUSINESSPROCESS_INITIALCAP, BUSINESSPROCESS);
    INIT_CAP_TO_BB_MAP.put(Constants.BB_BUSINESSFUNCTION_INITIALCAP, BUSINESSFUNCTION);
    INIT_CAP_TO_BB_MAP.put(Constants.BB_PRODUCT_INITIALCAP, PRODUCT);
    INIT_CAP_TO_BB_MAP.put(Constants.BB_BUSINESSUNIT_INITIALCAP, BUSINESSUNIT);
    INIT_CAP_TO_BB_MAP.put(Constants.BB_BUSINESSMAPPING_INITIALCAP, BUSINESSMAPPING);
    INIT_CAP_TO_BB_MAP.put(Constants.BB_BUSINESSOBJECT_INITIALCAP, BUSINESSOBJECT);
    INIT_CAP_TO_BB_MAP.put(Constants.BB_INFORMATIONSYSTEMDOMAIN_INITIALCAP, INFORMATIONSYSTEMDOMAIN);
    INIT_CAP_TO_BB_MAP.put(Constants.BB_INFORMATIONSYSTEMRELEASE_INITIALCAP, INFORMATIONSYSTEMRELEASE);
    INIT_CAP_TO_BB_MAP.put(Constants.BB_INFORMATIONSYSTEMINTERFACE_INITIALCAP, INFORMATIONSYSTEMINTERFACE);
    INIT_CAP_TO_BB_MAP.put(Constants.BB_ARCHITECTURALDOMAIN_INITIALCAP, ARCHITECTURALDOMAIN);
    INIT_CAP_TO_BB_MAP.put(Constants.BB_TECHNICALCOMPONENTRELEASE_INITIALCAP, TECHNICALCOMPONENTRELEASE);
    INIT_CAP_TO_BB_MAP.put(Constants.BB_INFRASTRUCTUREELEMENT_INITIALCAP, INFRASTRUCTUREELEMENT);
    INIT_CAP_TO_BB_MAP.put(Constants.BB_PROJECT_INITIALCAP, PROJECT);
    //  TCR2IEASSOCIATION intentionally left out
    //  ISR2BOASSOCIATION intentionally left out
  }

  /**
   * Maps TypeOfBuildingBlock to a list of connected TypeOfBuildingBlock, including itself, if self-references are possible
   * for this type. In case the ToBB is connected to BusinessMapping, the BusinessMapping's remaining types of building block
   * are listed instead of BusinessMapping.
   */
  private static final Map<TypeOfBuildingBlock, List<TypeOfBuildingBlock>>      CONNECTED_TYPES_OF_BB = CollectionUtils.hashMap();
  static {
    CONNECTED_TYPES_OF_BB.put(ARCHITECTURALDOMAIN, ImmutableList.of(ARCHITECTURALDOMAIN, TECHNICALCOMPONENTRELEASE));
    CONNECTED_TYPES_OF_BB.put(BUSINESSDOMAIN,
        ImmutableList.of(BUSINESSDOMAIN, BUSINESSPROCESS, BUSINESSFUNCTION, BUSINESSOBJECT, BUSINESSUNIT, PRODUCT));
    CONNECTED_TYPES_OF_BB.put(BUSINESSFUNCTION, ImmutableList.of(BUSINESSFUNCTION, BUSINESSOBJECT, BUSINESSDOMAIN, INFORMATIONSYSTEMRELEASE));
    CONNECTED_TYPES_OF_BB.put(BUSINESSOBJECT,
        ImmutableList.of(BUSINESSOBJECT, BUSINESSFUNCTION, INFORMATIONSYSTEMRELEASE, BUSINESSDOMAIN, INFORMATIONSYSTEMINTERFACE));
    CONNECTED_TYPES_OF_BB.put(BUSINESSPROCESS, ImmutableList.of(BUSINESSPROCESS, BUSINESSDOMAIN, INFORMATIONSYSTEMRELEASE, BUSINESSUNIT, PRODUCT));
    CONNECTED_TYPES_OF_BB.put(BUSINESSUNIT, ImmutableList.of(BUSINESSUNIT, BUSINESSDOMAIN, INFORMATIONSYSTEMRELEASE, BUSINESSPROCESS, PRODUCT));
    CONNECTED_TYPES_OF_BB.put(BUSINESSMAPPING, ImmutableList.of(INFORMATIONSYSTEMRELEASE, BUSINESSPROCESS, BUSINESSUNIT, PRODUCT));
    CONNECTED_TYPES_OF_BB.put(INFORMATIONSYSTEMDOMAIN, ImmutableList.of(INFORMATIONSYSTEMDOMAIN, INFORMATIONSYSTEMRELEASE));
    CONNECTED_TYPES_OF_BB.put(INFORMATIONSYSTEMINTERFACE, ImmutableList.of(INFORMATIONSYSTEMRELEASE, TECHNICALCOMPONENTRELEASE, BUSINESSOBJECT));
    CONNECTED_TYPES_OF_BB.put(INFORMATIONSYSTEMRELEASE, ImmutableList.of(INFORMATIONSYSTEMRELEASE, INFORMATIONSYSTEMDOMAIN,
        TECHNICALCOMPONENTRELEASE, BUSINESSPROCESS, BUSINESSUNIT, PRODUCT, BUSINESSFUNCTION, INFRASTRUCTUREELEMENT, BUSINESSOBJECT, PROJECT,
        INFORMATIONSYSTEMINTERFACE));
    CONNECTED_TYPES_OF_BB.put(INFRASTRUCTUREELEMENT, ImmutableList.of(INFRASTRUCTUREELEMENT, INFORMATIONSYSTEMRELEASE, TECHNICALCOMPONENTRELEASE));
    CONNECTED_TYPES_OF_BB.put(PRODUCT, ImmutableList.of(PRODUCT, BUSINESSDOMAIN, INFORMATIONSYSTEMRELEASE, BUSINESSPROCESS, BUSINESSUNIT));
    CONNECTED_TYPES_OF_BB.put(PROJECT, ImmutableList.of(PROJECT, INFORMATIONSYSTEMRELEASE));
    CONNECTED_TYPES_OF_BB
        .put(TECHNICALCOMPONENTRELEASE, ImmutableList.of(TECHNICALCOMPONENTRELEASE, ARCHITECTURALDOMAIN, INFRASTRUCTUREELEMENT,
            INFORMATIONSYSTEMRELEASE, INFORMATIONSYSTEMINTERFACE));
    CONNECTED_TYPES_OF_BB.put(TCR2IEASSOCIATION, ImmutableList.of(TECHNICALCOMPONENTRELEASE, INFRASTRUCTUREELEMENT));
    CONNECTED_TYPES_OF_BB.put(ISR2BOASSOCIATION, ImmutableList.of(INFORMATIONSYSTEMRELEASE, BUSINESSOBJECT));
  }

  @SuppressWarnings("unchecked")
  public <T extends BuildingBlock> Class<T> getAssociatedClass() {
    for (Class<? extends BuildingBlock> clazz : CLASS_TO_BB_MAP.keySet()) {
      if (this.equals(CLASS_TO_BB_MAP.get(clazz))) {
        return (Class<T>) clazz;
      }
    }
    return null;
  }

  private static final Logger                   LOGGER = Logger.getIteraplanLogger(TypeOfBuildingBlock.class);

  // All, except the DUMMY constant.
  public static final List<TypeOfBuildingBlock> ALL;
  /**
   * A list of all user-visible building block types. For those, it's possible to assign write
   * permissions for roles.
   */
  public static final List<TypeOfBuildingBlock> DISPLAY;

  static {
    ALL = Collections.unmodifiableList(Arrays.asList(new TypeOfBuildingBlock[] { ARCHITECTURALDOMAIN, BUSINESSFUNCTION, BUSINESSOBJECT,
      BUSINESSPROCESS, BUSINESSMAPPING, INFORMATIONSYSTEMINTERFACE, INFRASTRUCTUREELEMENT, BUSINESSDOMAIN, INFORMATIONSYSTEM,
      INFORMATIONSYSTEMDOMAIN, INFORMATIONSYSTEMRELEASE, PROJECT, BUSINESSUNIT, PRODUCT, TECHNICALCOMPONENT, TECHNICALCOMPONENTRELEASE, TRANSPORT,
      TCR2IEASSOCIATION, ISR2BOASSOCIATION }));

    DISPLAY = Collections.unmodifiableList(Arrays.asList(new TypeOfBuildingBlock[] { ARCHITECTURALDOMAIN, BUSINESSFUNCTION, BUSINESSOBJECT,
      BUSINESSPROCESS, BUSINESSMAPPING, INFORMATIONSYSTEMINTERFACE, INFRASTRUCTUREELEMENT, BUSINESSDOMAIN, INFORMATIONSYSTEMDOMAIN,
      INFORMATIONSYSTEMRELEASE, PROJECT, BUSINESSUNIT, TECHNICALCOMPONENTRELEASE, PRODUCT }));
  }

  private final String                          typeOfBuildingBlock;

  private TypeOfBuildingBlock(String typeOfBuildingBlock) {
    this.typeOfBuildingBlock = typeOfBuildingBlock;
  }

  /**
   * Delegates to <tt>toString()</tt>. The presentation tier uses JSP Expression Language to access
   * application data stored in JavaBean components. Thus a JavaBean-style getter-method has to be
   * provided to display the value of an Enum instance on the GUI.
   * 
   * @return See method description.
   */
  public String getValue() {
    return this.toString();
  }

  public String getPluralValue() {
    String singularValue = this.toString();
    return (singularValue.startsWith("global") ? singularValue.concat("s") : singularValue.replace("singular", "plural"));
  }

  public String getAbbreviationValue() {
    String singularValue = getValue();
    if (singularValue.startsWith("global")) {
      return singularValue.substring(7).concat(".abbr");
    }
    else {
      return singularValue.replace("singular", "abbr");
    }
  }

  /**
   * Returns the current string value stored in the Enum.
   * <p>
   * Required for correct reflection behaviour (see <tt>StringEnumReflectionHelper</tt>).
   * 
   * @return See method description.
   */
  @Override
  public String toString() {
    return this.typeOfBuildingBlock;
  }

  /**
   * Returns the elements of this Enum class, except DUMMY.
   * 
   * @return See method description.
   */
  public static List<TypeOfBuildingBlock> getEnumConstants() {
    // returned list of Arrays.asList cannot be modified, thus it is wrapped in another ArrayList.
    List<TypeOfBuildingBlock> list = new ArrayList<TypeOfBuildingBlock>(Arrays.asList(TypeOfBuildingBlock.class.getEnumConstants()));
    list.remove(TypeOfBuildingBlock.DUMMY);
    return list;
  }

  /**
   * @return List of {@code TypeOfBuildingBlock}s connected to this type
   */
  public List<TypeOfBuildingBlock> getConnectedTypesOfBuildingBlocks() {
    if (CONNECTED_TYPES_OF_BB.containsKey(this)) {
      return CONNECTED_TYPES_OF_BB.get(this);
    }
    else {
      return null;
    }
  }

  /**
   * Returns the Enum instance for the specified string value.
   * 
   * @param value
   *          The string value for which the Enum instance shall be returned.
   * @return See method description.
   */
  public static TypeOfBuildingBlock getTypeOfBuildingBlockByString(String value) {
    if (value == null) {
      return null;
    }

    String key = value;
    if (value.endsWith(".plural")) {
      key = value.substring(0, value.length() - 7) + ".singular";
    }
    else if (value.endsWith("s")) {
      key = value.substring(0, value.length() - 1);
    }
    String name = StringEnumReflectionHelper.getNameFromValue(TypeOfBuildingBlock.class, key);
    try {
      return Enum.valueOf(TypeOfBuildingBlock.class, name);
    } catch (IllegalArgumentException ex) {
      LOGGER.error("This enum has no constant with the specified name " + name + " from key : " + key);
    }
    return null;
  }

  /**
   * Maps the given building block type string (InitialCapitalized format) (see also
   * {@link de.iteratec.iteraplan.common.Constants}) to the corresponding TypeOfBuildingBlock instance.
   * 
   * @param initCapString
   *          String that contains building block type in InitialCapitalized format.
   * @return The corresponding TypeOfBuildingBlock instance.
   */
  public static TypeOfBuildingBlock fromInitialCapString(String initCapString) {
    TypeOfBuildingBlock tob = INIT_CAP_TO_BB_MAP.get(initCapString);

    if (tob == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }
    return tob;

  }

  /**
   * Maps the given building block type string (singular or plural) (see also
   * de.iteratec.iteraplan.common.Constants) to the corresponding TypeOfBuildingBlock instance.
   * 
   * @param tobString
   *          String that serves as the GUI key for building block types.
   * @return The corresponding TypeOfBuildingBlock instance.
   */
  public static TypeOfBuildingBlock fromPropertyString(String tobString) {
    TypeOfBuildingBlock tob = PROPERTY_TO_BB_MAP.get(tobString);

    if (tob == null) {
      LOGGER.error("the given tobString String is invalid: '" + tobString + "'!");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }
    return tob;
  }

  /**
   * @return List of property-keys for the self-references of this type of building block.
   */
  public List<String> getSelfReferencesPropertyKeys() {
    if (INFORMATIONSYSTEMRELEASE == this) {
      final String prefix = "graphicalReport." + Constants.BB_INFORMATIONSYSTEMRELEASE_BASE;
      return ImmutableList.of(prefix + "parent", prefix + "children", prefix + "predecessors", prefix + "successors", prefix + "parentComponents",
          prefix + "baseComponents");
    }
    else if (TECHNICALCOMPONENTRELEASE == this) {
      final String prefix = "graphicalReport." + Constants.BB_TECHNICALCOMPONENTRELEASE_BASE;
      return ImmutableList.of(prefix + "predecessors", prefix + "successors", prefix + "parentComponents", prefix + "baseComponents");
    }
    else if (BUSINESSOBJECT == this) {
      final String prefix = getValue().replace(".singular", ".");
      return ImmutableList.of(prefix + "parent", prefix + "children", prefix + "generalisation", prefix + "specialisations");
    }
    else if (INFRASTRUCTUREELEMENT == this) {
      final String prefix = getValue().replace(".singular", ".");
      return ImmutableList.of(prefix + "parent", prefix + "children", prefix + "parentComponents.short", prefix + "baseComponents.short");
    }
    else if (INFORMATIONSYSTEMINTERFACE != this && BUSINESSMAPPING != this && TRANSPORT != this) {
      final String prefix = getValue().replace("global", "graphicalReport").replace(".singular", "");
      return ImmutableList.of(prefix + ".parent", prefix + ".children");
    }
    else {
      return new ArrayList<String>();
    }
  }

  /**
   * Checks whether the class represented by this TypeOfBuildingBlock implements the Status Entity interface.
   * @return
   *    true if this type of building block has a status property.
   */
  public boolean hasStatusAttribute() {
    return StatusEntity.class.isAssignableFrom(getAssociatedClass());
  }

  /**
   * Checks whether the class represented by this TypeOfBuildingBlock implements the Runtime Period Delegate interface.
   * @return
   *    true if this type of building block has a runtime period property.
   */
  public boolean hasRuntimePeriod() {
    return RuntimePeriodDelegate.class.isAssignableFrom(getAssociatedClass());
  }

  public static TypeOfBuildingBlock typeOfBuildingBlockForClass(Class<?> clazz) {
    return CLASS_TO_BB_MAP.get(clazz);
  }
}
