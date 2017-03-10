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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi;

import java.awt.Color;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.elasticmi.exception.ElasticMiException;
import de.iteratec.iteraplan.elasticmi.metamodel.common.impl.atomic.AtomicDataType;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WEnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WFeatureExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMixinTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WNamedExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WNominalEnumerationExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WRelationshipExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WRelationshipTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WSortalTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WTypeGroup;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WUniversalTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WValueTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.impl.WMetamodelImpl;
import de.iteratec.iteraplan.model.AbstractAssociation;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.Seal;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;


@SuppressWarnings("PMD.TooManyMethods")
public class WMetamodelExport {
  private static final Logger              LOGGER                         = Logger.getLogger(WMetamodelExport.class);

  public static final Package              MODEL                          = BusinessMapping.class.getPackage();

  private static final Set<String>         IGNORED_ATTRIBUTES             = Sets.newHashSet("olVersion", "state");
  public static final String               JOINED_RELEASE_ATTRIBUTE       = "version";
  private static final Set<String>         IGNORED_RELATIONSHIPS          = Sets.newHashSet("InformationSystem.releases",
                                                                              "TechnicalComponent.releases",
                                                                              "InformationSystemRelease.informationSystem",
                                                                              "TechnicalComponentRelease.technicalComponent",
                                                                              "InformationSystemInterface.informationSystemReleaseA",
                                                                              "InformationSystemInterface.informationSystemReleaseB");

  public static final String               INFORMATION_FLOW               = "InformationFlow";
  public static final String               ISI_INFORMATION_FLOWS          = "informationFlows";
  public static final String               INFORMATION_FLOW_ISI           = "informationSystemInterface";
  public static final String               INFORMATION_FLOW_IS1           = "informationSystemRelease1";
  public static final String               INFORMATION_FLOW_IS2           = "informationSystemRelease2";
  public static final String               INFORMATION_FLOW_IS1_OPP       = "informationFlows1";
  public static final String               INFORMATION_FLOW_IS2_OPP       = "informationFlows2";
  public static final String               INFORMATION_FLOW_ISI_ID        = "iteraplan_InformationSystemInterfaceID";

  //type groups
  private static final String              TG_BUSINESS_ARCHITECTURE       = "Businessarchitecture";
  private static final String              TG_APPLICATION_ARCHITECTURE    = "Applicationarchitecture";
  private static final String              TG_TECHNOLOGY_ARCHITECTURE     = "Technologyarchitecture";
  private static final String              TG_INFRASTRUCTURE_ARCHITECTURE = "Infrastructurearchitecture";
  private static final String              TG_ENTERPRISE_CONTEXT          = "Enterprisecontext";

  private static final Map<String, String> TYPE_GROUPS                    = Maps.newHashMap();
  static {
    TYPE_GROUPS.put(BusinessDomain.class.getSimpleName(), TG_BUSINESS_ARCHITECTURE);
    TYPE_GROUPS.put(BusinessProcess.class.getSimpleName(), TG_BUSINESS_ARCHITECTURE);
    TYPE_GROUPS.put(BusinessUnit.class.getSimpleName(), TG_BUSINESS_ARCHITECTURE);
    TYPE_GROUPS.put(Product.class.getSimpleName(), TG_BUSINESS_ARCHITECTURE);
    TYPE_GROUPS.put(BusinessFunction.class.getSimpleName(), TG_BUSINESS_ARCHITECTURE);
    TYPE_GROUPS.put(BusinessObject.class.getSimpleName(), TG_BUSINESS_ARCHITECTURE);
    TYPE_GROUPS.put(BusinessMapping.class.getSimpleName(), TG_BUSINESS_ARCHITECTURE);

    TYPE_GROUPS.put(InformationSystemDomain.class.getSimpleName(), TG_APPLICATION_ARCHITECTURE);
    TYPE_GROUPS.put(InformationSystemRelease.class.getSimpleName(), TG_APPLICATION_ARCHITECTURE);
    TYPE_GROUPS.put(Transport.class.getSimpleName(), TG_APPLICATION_ARCHITECTURE);
    TYPE_GROUPS.put(InformationSystemInterface.class.getSimpleName(), TG_APPLICATION_ARCHITECTURE);
    TYPE_GROUPS.put(Isr2BoAssociation.class.getSimpleName(), TG_APPLICATION_ARCHITECTURE);

    TYPE_GROUPS.put(ArchitecturalDomain.class.getSimpleName(), TG_TECHNOLOGY_ARCHITECTURE);
    TYPE_GROUPS.put(TechnicalComponentRelease.class.getSimpleName(), TG_TECHNOLOGY_ARCHITECTURE);
    TYPE_GROUPS.put(Tcr2IeAssociation.class.getSimpleName(), TG_TECHNOLOGY_ARCHITECTURE);

    TYPE_GROUPS.put(InfrastructureElement.class.getSimpleName(), TG_INFRASTRUCTURE_ARCHITECTURE);

    TYPE_GROUPS.put(Project.class.getSimpleName(), TG_ENTERPRISE_CONTEXT);
  }

  private static final List<String>        UNIVERSAL_TYPES                = new ArrayList<String>();
  static {
    UNIVERSAL_TYPES.add("BusinessDomain");
    UNIVERSAL_TYPES.add("BusinessProcess");
    UNIVERSAL_TYPES.add("BusinessUnit");
    UNIVERSAL_TYPES.add("Product");
    UNIVERSAL_TYPES.add("BusinessFunction");
    UNIVERSAL_TYPES.add("BusinessObject");
    UNIVERSAL_TYPES.add("BusinessMapping");
    UNIVERSAL_TYPES.add("InformationSystemDomain");
    UNIVERSAL_TYPES.add("InformationSystem");
    UNIVERSAL_TYPES.add("Transport");
    UNIVERSAL_TYPES.add("InformationSystemInterface");
    UNIVERSAL_TYPES.add("ArchitecturalDomain");
    UNIVERSAL_TYPES.add("TechnicalComponent");
    UNIVERSAL_TYPES.add("InfrastructureElement");
    UNIVERSAL_TYPES.add("Project");
  }

  private final Map<String, HbMappedClass> hbClassData;
  private final AttributeTypeGroupDAO      attributeTypeGroupDAO;

  public WMetamodelExport(Map<String, HbMappedClass> hbClassData, AttributeTypeGroupDAO attributeTypeGroupDAO) {
    this.hbClassData = Preconditions.checkNotNull(hbClassData);
    this.attributeTypeGroupDAO = attributeTypeGroupDAO;
  }

  public ElasticMiIteraplanMapping loadMetamodel() {
    return new ExportRun(hbClassData, attributeTypeGroupDAO);
  }

  private static final class ExportRun extends ElasticMiIteraplanMapping {
    private final Map<String, HbMappedClass> hbClassData;
    private final AttributeTypeGroupDAO      attributeTypeGroupDAO;

    public ExportRun(Map<String, HbMappedClass> hbClassData, AttributeTypeGroupDAO attributeTypeGroupDAO) {
      super(new WMetamodelImpl());
      this.hbClassData = hbClassData;
      this.attributeTypeGroupDAO = attributeTypeGroupDAO;
      createTypeGroups();
      createMixinTypes();
      createEnumerations();
      createUniversalTypes();
      addMixinRestrictions();
      createRelationships();
    }

    private void createTypeGroups() {
      WMetamodel metamodel = getMetamodel();
      metamodel.createTypeGroup(TG_BUSINESS_ARCHITECTURE);
      metamodel.createTypeGroup(TG_APPLICATION_ARCHITECTURE);
      metamodel.createTypeGroup(TG_TECHNOLOGY_ARCHITECTURE);
      metamodel.createTypeGroup(TG_INFRASTRUCTURE_ARCHITECTURE);
      metamodel.createTypeGroup(TG_ENTERPRISE_CONTEXT);
    }

    /*
     * CAUTION: The Model validator expects all User-defined-properties (i.e. AttributeTypes) are defined in Mixins; 
     * thereby FeatureGroups with fragment kind "mixin" are created; 
     * this allows the ModelValidator to check for cardinality constraint violations and create
     *  * errors for missing values for predifined properties with lower bound 1 (e.g. name) 
     *  * warnings for missing values for user-defined properties with lower bound 1 (so for those attributes, a lower bound of 1 is not treated as "hard" requirement) 
     */
    private void createMixinTypes() {
      for (AttributeTypeGroup group : attributeTypeGroupDAO.loadElementList(null)) {
        createMixin(group);
      }
    }

    private void createMixin(AttributeTypeGroup group) {
      getMetamodel().createMixin(group.getName());
      for (AttributeType at : group.getAttributeTypes()) {
        createAttributeType(at);
      }
    }

    private void createEnumerations() {
      for (HbMappedClass hbClass : hbClassData.values()) {
        if (needsToBeProcessed(hbClass)) {
          for (HbMappedProperty hbEnumProp : hbClass.getAllEnums()) {
            if (needsToBeProcessed(hbEnumProp)) {
              @SuppressWarnings("unchecked")
              Class<? extends Enum<?>> enumm = (Class<? extends Enum<?>>) hbEnumProp.getType();
              WNominalEnumerationExpression enumeration = resolve(enumm);
              if (enumeration == null) {
                enumeration = getMetamodel().createEnumeration(enumm.getName(), true);
                applyName(enumeration, enumm);
                //Note: literal colors are the default VBB colors for now.
                List<String> defaultColors = SpringGuiFactory.getInstance().getVbbClusterColors();
                int cntLiterals = 0;
                for (Enum<?> literal : enumm.getEnumConstants()) {
                  WEnumerationLiteralExpression ele = enumeration.createEnumerationLiteral(literal.name(),
                      Color.decode("#" + defaultColors.get(cntLiterals % defaultColors.size())));
                  cntLiterals += 1;
                  //Name currently fix to persistent name for all locales, since iteraplan offers no further data.
                  applyName(ele, literal);
                  add(ele, literal);

                }

                add(enumeration, enumm);
              }
            }
          }
        }
      }
    }

    private void createUniversalTypes() {
      Map<String, HbMappedClass> hbClasses = Maps.newHashMap();

      // First: create a list over all possible universalTypes
      for (HbMappedClass hbClass : hbClassData.values()) {
        if (needsToBeProcessed(hbClass)) {
          hbClasses.put(hbClass.getClassName(), hbClass);
        }
      }

      // Second: processed first, a defined set of universal types
      for (String hbClassName : UNIVERSAL_TYPES) {
        HbMappedClass hbClass = hbClasses.remove(hbClassName);
        if (hbClass != null) {
          createUniversalTypeFor(hbClass);
        }
      }

      // Third: process all other 
      for (HbMappedClass hbClass : hbClasses.values()) {
        createUniversalTypeFor(hbClass);
      }
    }

    private void createUniversalTypeFor(HbMappedClass hbClass) {
      Class<?> clazz = resolveClass(hbClass);
      WTypeGroup group = getMetamodel().findTypeGroupByPersistentName(TYPE_GROUPS.get(clazz.getSimpleName()));
      WUniversalTypeExpression ute = null;
      if (AbstractAssociation.class.equals(clazz.getSuperclass()) || BusinessMapping.class.equals(clazz)) {
        WRelationshipTypeExpression rte = getMetamodel().createRelationshipType(transformName(hbClass.getClassName()), group);
        add(rte, hbClass);
        if (BusinessMapping.class.equals(clazz)) {
          addNamesAndAbbreviations(rte, clazz);
        }
        else {
          addNamesAndAbbreviations(rte);
        }
        ute = rte;
      }
      else {
        WSortalTypeExpression ste = getMetamodel().createClassExpression(transformName(hbClass.getClassName()), group);
        add(ste, hbClass);
        addNamesAndAbbreviations(ste, clazz);
        ute = ste;
      }

      createProperties(ute, hbClass);
      if (INFORMATION_FLOW.equals(ute.getPersistentName())) {
        WPropertyExpression isiId = ute.createProperty(INFORMATION_FLOW_ISI_ID, 0, 1, false, false, false, true, AtomicDataType.INTEGER.type());
        applyNameAndDescription(isiId, INFORMATION_FLOW_ISI_ID, "");
      }
    }

    private void addMixinRestrictions() {
      for (AttributeTypeGroup group : attributeTypeGroupDAO.loadElementList(null)) {
        WMixinTypeExpression mixin = resolve(group);
        Multimap<BuildingBlockType, WFeatureExpression<?>> restrictions = HashMultimap.create();
        for (AttributeType at : group.getAttributeTypes()) {
          for (BuildingBlockType bbt : at.getBuildingBlockTypes()) {
            restrictions.put(bbt, mixin.findFeatureByPersistentName(at.getName()));
          }
        }
        for (BuildingBlockType bbt : restrictions.keySet()) {
          HbMappedClass bbHbClass = hbClassData.get(bbt.getTypeOfBuildingBlock().getAssociatedClass().getCanonicalName());
          if (bbHbClass.isReleaseClass()) {
            bbHbClass = bbHbClass.getReleaseBase();
          }
          WUniversalTypeExpression ute = getMetamodel().findUniversalTypeByPersistentName(bbHbClass.getClassName());
          getMetamodel().addMixin(ute, mixin, restrictions.get(bbt));
        }
      }
    }

    private void createAttributeType(AttributeType at) {
      int lower = at.isMandatory() ? 1 : 0;
      int upper = 1;
      String name = at.getName();
      boolean timeseries = false;
      WValueTypeExpression<?> type = null;
      if (at instanceof TextAT) {
        if (((TextAT) at).isMultiline()) {
          type = AtomicDataType.RICH_TEXT.type();
        }
        else {
          type = AtomicDataType.STRING.type();
        }
      }
      else if (at instanceof NumberAT) {
        type = AtomicDataType.DECIMAL.type();
        timeseries = ((NumberAT) at).isTimeseries();
      }
      else if (at instanceof DateAT) {
        type = AtomicDataType.DATE.type();
      }
      else if (at instanceof ResponsibilityAT) {
        type = AtomicDataType.STRING.type();
        if (((ResponsibilityAT) at).isMultiassignmenttype()) {
          upper = WFeatureExpression.UNLIMITED;
        }
      }
      WPropertyExpression prop = null;
      WMixinTypeExpression mixin = getMetamodel().findMixinByPersistentName(at.getAttributeTypeGroup().getName());

      if (type == null && (at instanceof EnumAT)) {
        timeseries = ((EnumAT) at).isTimeseries();
        WNominalEnumerationExpression enumm = resolve((EnumAT) at);
        if (enumm == null) {
          EnumAT enumAT = (EnumAT) at;
          enumm = getMetamodel().createEnumeration(enumAT.getClass().getCanonicalName() + "." + enumAT.getName(), true);
          applyNameAndDescription(enumm, enumAT);
          add(enumm, enumAT);
          for (EnumAV enumAV : enumAT.getSortedAttributeValues()) {
            Color literalDefaultColor = null;
            try {
              literalDefaultColor = Color.decode("#" + enumAV.getDefaultColorHex());
            } catch (NumberFormatException e) {
              LOGGER.error("Failed to decode color: " + enumAV.getDefaultColorHex());
            }
            WEnumerationLiteralExpression literal = enumm.createEnumerationLiteral(enumAV.getIdentityString(), literalDefaultColor);
            applyNameAndDescription(literal, enumAV);
            add(literal, enumAV);
          }
        }
        if (((EnumAT) at).isMultiassignmenttype()) {
          upper = WFeatureExpression.UNLIMITED;
        }
        prop = mixin.createProperty(name, lower, upper, false, false, timeseries, true, enumm);
        add(prop, at);
      }
      else if (type != null) {
        prop = mixin.createProperty(name, lower, upper, false, false, timeseries, true, type);
        add(prop, at);
      }
      applyNameAndDescription(prop, at);
    }

    private void createRelationships() {
      Set<Relationship> rels = determineRelationships();
      for (Relationship rel : rels) {
        rel.create(getMetamodel());
      }
    }

    private Set<Relationship> determineRelationships() {
      Set<Relationship> result = Sets.newHashSet();

      for (HbMappedClass hbClass : hbClassData.values()) {
        if (needsToBeProcessed(hbClass)) {
          for (HbMappedProperty hbRelation : hbClass.getAllRelations()) {
            if (needsToBeProcessed(hbRelation)) {
              HbMappedClass hbType = hbRelation.getHbType();
              if (hbType != null && hbType.isReleaseClass()) {
                hbType = hbClassData.get(hbType.getMappedClass().getCanonicalName().replace("Release", ""));
              }

              HbMappedProperty opposite = hbRelation.getOpposite();
              if (opposite != null) {
                HbMappedClass oppositeType = opposite.getHbType();
                if (oppositeType.isReleaseClass()) {
                  oppositeType = hbClassData.get(oppositeType.getMappedClass().getCanonicalName().replace("Release", ""));
                }

                boolean isEnd0Defining = AbstractAssociation.class.equals(oppositeType.getMappedClass().getSuperclass())
                    || BusinessMapping.class.equals(oppositeType.getMappedClass());
                boolean isEnd1Defining = AbstractAssociation.class.equals(hbType.getMappedClass().getSuperclass())
                    || BusinessMapping.class.equals(hbType.getMappedClass());

                Relationship.RelationshipEnd end0 = new Relationship.RelationshipEnd(transformName(hbRelation.getName()), hbRelation.isOptional() ? 0
                    : 1, hbRelation.isMany() ? WFeatureExpression.UNLIMITED : 1, transformName(hbType.getClassName()), isEnd0Defining);
                Relationship.RelationshipEnd end1 = new Relationship.RelationshipEnd(transformName(opposite.getName()),
                    opposite.isOptional() ? 0 : 1, opposite.isMany() ? WFeatureExpression.UNLIMITED : 1, transformName(oppositeType.getClassName()),
                    isEnd1Defining);
                result.add(new Relationship(end0, end1));
              }
            }
          }
        }
      }

      Relationship.RelationshipEnd end0 = new Relationship.RelationshipEnd(INFORMATION_FLOW_IS1_OPP, 0, WFeatureExpression.UNLIMITED,
          INFORMATION_FLOW, false);
      Relationship.RelationshipEnd end1 = new Relationship.RelationshipEnd(INFORMATION_FLOW_IS1, 0, 1, "InformationSystem", true);
      result.add(new Relationship(end0, end1));
      end0 = new Relationship.RelationshipEnd(INFORMATION_FLOW_IS2_OPP, 0, WFeatureExpression.UNLIMITED, INFORMATION_FLOW, false);
      end1 = new Relationship.RelationshipEnd(INFORMATION_FLOW_IS2, 0, 1, "InformationSystem", true);
      result.add(new Relationship(end0, end1));

      return result;
    }

    private void applyName(WNominalEnumerationExpression enumeration, Class<? extends Enum<?>> enumm) {
      applyNameAndDescription(enumeration, enumm.getName(), "");
    }

    private void applyName(WEnumerationLiteralExpression ele, Enum<?> literal) {
      applyNameAndDescription(ele, literal.name(), "");
    }

    private void applyNameAndDescription(WNamedExpression enumm, AttributeType at) {
      applyNameAndDescription(enumm, at.getName(), at.getDescription());
    }

    private void applyNameAndDescription(WEnumerationLiteralExpression literal, EnumAV av) {
      applyNameAndDescription(literal, av.getIdentityString(), av.getDescription());
    }

    private void applyNameAndDescription(WNamedExpression named, String name, String description) {
      for (String localeStr : Constants.LOCALES) {
        Locale locale = new Locale(localeStr);
        named.setName(name, locale);
        named.setDescription(description, locale);
      }
    }

    private Class<?> resolveClass(HbMappedClass hbClass) {
      return hbClass.hasReleaseClass() ? hbClass.getReleaseClass().getMappedClass() : hbClass.getMappedClass();
    }

    private void createProperties(WUniversalTypeExpression ute, HbMappedClass hbClass) {
      for (HbMappedProperty hbProp : hbClass.getAllProperties()) {
        if (needsToBeProcessed(hbProp)) {
          String name = transformName(hbProp.getName());
          if (DEFAULT_ATTRIBUTES.contains(name)) {
            WPropertyExpression property = ute.findPropertyByPersistentName(name);
            add(ute, property, hbProp.getGetMethod());
          }
          else {
            WValueTypeExpression<?> type = getOrCreatePrimitiveType(hbProp.getType());
            createProperty(ute, hbProp, type);
          }
        }
      }
      for (HbMappedProperty hbEnumProp : hbClass.getAllEnums()) {
        if (needsToBeProcessed(hbEnumProp)) {
          @SuppressWarnings("unchecked")
          Class<? extends Enum<?>> type = (Class<? extends Enum<?>>) hbEnumProp.getType();
          WNominalEnumerationExpression enumExpr = resolve(type);
          createProperty(ute, hbEnumProp, enumExpr);
        }
      }
    }

    private void createProperty(WUniversalTypeExpression ute, HbMappedProperty hbProp, WValueTypeExpression<?> type) {
      int lower = hbProp.isOptional() ? 0 : 1;
      int upper = hbProp.isMany() ? WFeatureExpression.UNLIMITED : 1;
      WPropertyExpression property = ute.createProperty(transformName(hbProp.getName()), lower, upper, false, hbProp.isUnique(), false, !hbProp
          .getName().equals("position"), type);
      addNames(property, resolveClass(hbProp.getContainingClass()));
      add(ute, property, hbProp.getGetMethod());
    }

    private WValueTypeExpression<?> getOrCreatePrimitiveType(Class<?> clazz) {
      Class<?> dataTypeClass = clazz;
      if (clazz.equals(Integer.class)) {
        dataTypeClass = BigInteger.class;
      }
      else if (boolean.class.equals(clazz)) {
        dataTypeClass = Boolean.class;
      }

      WValueTypeExpression<?> adte = resolveVT(dataTypeClass);
      if (adte != null) {
        return adte;
      }

      adte = getMetamodel().findValueTypeByPersistentName(dataTypeClass.getName());
      if (adte != null) {
        return adte;
      }

      throw new ElasticMiException(ElasticMiException.GENERAL_ERROR, "No primitive type defined for class " + dataTypeClass);
    }

    private void addNames(WPropertyExpression property, Class<?> bbClass) {
      TypeOfBuildingBlock tob = TypeOfBuildingBlock.typeOfBuildingBlockForClass(bbClass);
      String bbKey = tob == null ? null : getBBKey(tob);
      for (String localeString : Constants.LOCALES) {
        Locale locale = new Locale(localeString);
        String name = MessageAccess.getStringOrNull(bbKey + "." + property.getPersistentName(), locale);
        if (name == null) {
          name = MessageAccess.getStringOrNull("global." + property.getPersistentName(), locale);
        }
        if (name != null) {
          property.setName(name, locale);
        }
        else {
          property.setName(property.getPersistentName(), locale);
        }
      }
    }

    /**
    * Add naming information (names and abbreviations) for all available {@link Locale}s
    * 
    * @param ste 
    * @param bbClass the iteraplan model {@link Class}
    */
    private void addNamesAndAbbreviations(WUniversalTypeExpression ute, Class<?> bbClass) {
      TypeOfBuildingBlock tob = TypeOfBuildingBlock.typeOfBuildingBlockForClass(bbClass);
      if (tob == null) {
        return;
      }
      String bbKey = getBBKey(tob);
      for (String localeString : Constants.LOCALES) {
        Locale locale = new Locale(localeString);
        ute.setName(MessageAccess.getStringOrNull(tob.getValue(), locale), locale);
        ute.setPluralName(MessageAccess.getStringOrNull(tob.getPluralValue(), locale), locale);
        ute.setAbbreviation(MessageAccess.getStringOrNull(bbKey + ".abbr", locale), locale);
        ute.setDescription(MessageAccess.getStringOrNull("glossary." + bbKey, locale), locale);
      }
    }

    private void addNamesAndAbbreviations(WRelationshipTypeExpression rte) {
      for (String localeString : Constants.LOCALES) {
        Locale locale = new Locale(localeString);
        rte.setName(rte.getPersistentName(), locale);
        rte.setPluralName(rte.getPersistentName(), locale);
        String abbr = rte.getPersistentName().replace("Association", "");
        abbr = abbr.replace(INFORMATION_FLOW, "IF");
        rte.setAbbreviation(abbr, locale);
      }
    }

    private static String getBBKey(TypeOfBuildingBlock tob) {
      //FIXME This hack is necessary due to inconsistent naming of keys according to issue ITERAPLAN-454
      String[] tmp = tob.getValue().split("\\.");
      return "global".equals(tmp[0]) ? unescapeGlobalName(tmp[1]) : tmp[0];
    }

    private static String unescapeGlobalName(String name) {
      //This method is solely needed due to the inconsistent naming of keys according to issue ITERAPLAN-454.
      String[] tmp = name.split("_");
      StringBuffer result = new StringBuffer(tmp[0]);
      for (int i = 1; i < tmp.length; i++) {
        result.append(StringUtils.capitalize(tmp[i]));
      }
      return result.toString();
    }

    private boolean needsToBeProcessed(HbMappedClass hbClass) {
      if (hbClass == null) {
        return false;
      }
      else {
        return (hbClass.getMappedClass() != null && MODEL.equals(hbClass.getMappedClass().getPackage())
            && !hbClass.getClassName().contains("History") && !hbClass.hasSubClasses() && !hbClass.isReleaseClass()
            && !Seal.class.equals(hbClass.getMappedClass()) && !BuildingBlockType.class.equals(hbClass.getMappedClass()));
      }
    }

    private boolean needsToBeProcessed(HbMappedProperty hbProp) {
      if (hbProp == null) {
        return false;
      }
      if (!hbProp.isPrimitive() && !hbProp.isEnum()) {
        HbMappedProperty opposite = hbProp.getOpposite();
        if (opposite != null
            && (IGNORED_RELATIONSHIPS.contains(hbProp.getContainingClass().getClassName() + "." + hbProp.getName()) || IGNORED_RELATIONSHIPS
                .contains(opposite.getContainingClass().getClassName() + "." + opposite.getName()))) {
          return false;
        }
      }
      if (hbProp.getContainingClass().isReleaseClass() && JOINED_RELEASE_ATTRIBUTE.equals(hbProp.getName())) {
        return false;
      }
      return !IGNORED_ATTRIBUTES.contains(hbProp.getName());
    }

    /**
    * Handle special naming conventions here
    * @param name of the type/feature in the iteraplan metamodel
    * @return name of the type/feature in the elasticeam metamodel
    */
    private static String transformName(String name) {
      if (name == null) {
        return null;
      }
      String res = name;
      res = res.replace("Transport", "InformationFlow");
      res = res.replace("transport", "informationFlow");
      return res;
    }

    private static final class Relationship {
      private static final int      PRIME = 31;

      private final RelationshipEnd end0;
      private final RelationshipEnd end1;

      public Relationship(RelationshipEnd end0, RelationshipEnd end1) {
        this.end0 = end0;
        this.end1 = end1;
      }

      private void create(WMetamodel metamodel) {
        if (end0.typePersistentName.equals(end1.typePersistentName)) {
          WUniversalTypeExpression type = metamodel.findUniversalTypeByPersistentName(end0.typePersistentName);
          WRelationshipExpression relationship = metamodel.createRelationship(type, end0.name, end0.lower, end0.upper, end1.name, end1.lower,
              end1.upper, isAcyclic());
          for (WRelationshipEndExpression ree : relationship.getRelationshipEnds()) {
            addNamesAndAbbreviations(ree);
          }
        }
        else {
          WUniversalTypeExpression end0Type = metamodel.findUniversalTypeByPersistentName(end0.typePersistentName);
          WUniversalTypeExpression end1Type = metamodel.findUniversalTypeByPersistentName(end1.typePersistentName);
          int end0Lower = end0.lower;
          int end0Upper = end0.upper;
          int end1Lower = end1.lower;
          int end1Upper = end1.upper;
          if (ISI_INFORMATION_FLOWS.equals(end0.name) && INFORMATION_FLOW_ISI.equals(end1.name)) {
            end0Lower = 1;
            end0Upper = WFeatureExpression.UNLIMITED;
            end1Lower = 0;
            end1Upper = 1;
          }
          else if (ISI_INFORMATION_FLOWS.equals(end1.name) && INFORMATION_FLOW_ISI.equals(end0.name)) {
            end0Lower = 0;
            end0Upper = 1;
            end1Lower = 1;
            end1Upper = WFeatureExpression.UNLIMITED;
          }
          else if ("businessMappings".equals(end0.name) && "informationSystemRelease".equals(end1.name)) {
            end0Lower = 0;
            end0Upper = WFeatureExpression.UNLIMITED;
            end1Lower = 1;
            end1Upper = 1;
          }
          else if ("businessMappings".equals(end1.name) && "informationSystemRelease".equals(end0.name)) {
            end0Lower = 1;
            end0Upper = 1;
            end1Lower = 0;
            end1Upper = WFeatureExpression.UNLIMITED;
          }
          else if ("businessObject".equals(end0.name) && ISI_INFORMATION_FLOWS.equals(end1.name)) {
            end0Lower = 0;
            end0Upper = 1;
            end1Lower = 0;
            end1Upper = WFeatureExpression.UNLIMITED;
          }
          else if ("businessObject".equals(end1.name) && ISI_INFORMATION_FLOWS.equals(end0.name)) {
            end0Lower = 0;
            end0Upper = WFeatureExpression.UNLIMITED;
            end1Lower = 0;
            end1Upper = 1;
          }
          WRelationshipExpression relationship = metamodel.createRelationship(end1Type, end0.name, end0Lower, end0Upper, end0.isDefining(), end0Type,
              end1.name, end1Lower, end1Upper, end1.isDefining());
          for (WRelationshipEndExpression ree : relationship.getRelationshipEnds()) {
            addNamesAndAbbreviations(ree);
          }
        }
      }

      private static void addNamesAndAbbreviations(WRelationshipEndExpression ree) {
        for (String localeString : Constants.LOCALES) {
          Locale locale = new Locale(localeString);
          WRelationshipEndExpression ree1 = ree;
          WUniversalTypeExpression ute1 = ree1.getOpposite().getType();
          WUniversalTypeExpression ute2 = ree1.getType();
          //set names
          String name1 = transformName(ree1.getName(locale));
          if (name1 != null && !StringUtils.isEmpty(name1)) {
            ree1.setName(name1, locale);
          }
          else {
            ree1.setName(ree1.getPersistentName(), locale);
          }
          if (!ute1.equals(ute2)) {
            //set abbreviations
            String abbr1 = ute2.getAbbreviation(locale).toLowerCase();
            ree1.setAbbreviation(abbr1, locale);
          }
        }
      }

      private boolean isAcyclic() {
        return StringUtils.containsIgnoreCase(end0.name, "successor") || StringUtils.containsIgnoreCase(end0.name, "predecessor")
            || StringUtils.containsIgnoreCase(end0.name, "children") || StringUtils.containsIgnoreCase(end0.name, "parent");
      }

      /**{@inheritDoc}**/
      @Override
      public int hashCode() {
        int result = 1;
        result = PRIME * result + end0.hashCode() + end1.hashCode();
        return result;
      }

      /**{@inheritDoc}**/
      @Override
      public boolean equals(Object obj) {
        if (this == obj) {
          return true;
        }
        if (obj == null) {
          return false;
        }
        if (getClass() != obj.getClass()) {
          return false;
        }
        Relationship other = (Relationship) obj;

        if (end0.equals(other.end0)) {
          return end1.equals(other.end1);
        }
        else if (end0.equals(other.end1)) {
          return end1.equals(other.end0);
        }
        return false;
      }

      /**{@inheritDoc}**/
      @Override
      public String toString() {
        return end0.toString() + " / " + end1.toString();
      }

      private static final class RelationshipEnd {
        private final String  name;
        private final int     lower;
        private final int     upper;
        private final String  typePersistentName;
        private final boolean defining;

        /**
         * Default constructor.
         */
        public RelationshipEnd(String name, int lower, int upper, String typePersistentName, boolean isDefining) {
          this.name = name;
          this.lower = lower;
          this.upper = upper;
          this.typePersistentName = typePersistentName;
          this.defining = isDefining;
        }

        private boolean isDefining() {
          return defining;
        }

        /**{@inheritDoc}**/
        @Override
        public int hashCode() {
          int result = 1;
          result = PRIME * result + lower;
          result = PRIME * result + name.hashCode();
          result = PRIME * result + typePersistentName.hashCode();
          result = PRIME * result + upper;
          return result;
        }

        /**{@inheritDoc}**/
        @Override
        public boolean equals(Object obj) {
          if (this == obj) {
            return true;
          }
          if (obj == null) {
            return false;
          }
          if (getClass() != obj.getClass()) {
            return false;
          }
          RelationshipEnd other = (RelationshipEnd) obj;
          if (lower != other.lower) {
            return false;
          }
          if (name == null) {
            if (other.name != null) {
              return false;
            }
          }
          else if (!name.equals(other.name)) {
            return false;
          }
          if (typePersistentName == null) {
            if (other.typePersistentName != null) {
              return false;
            }
          }
          else if (!typePersistentName.equals(other.typePersistentName)) {
            return false;
          }
          if (upper != other.upper) {
            return false;
          }
          return true;
        }

        /**{@inheritDoc}**/
        @Override
        public String toString() {
          return name + "(" + lower + "," + upper + "->" + typePersistentName + ")";
        }
      }
    }
  }
}
