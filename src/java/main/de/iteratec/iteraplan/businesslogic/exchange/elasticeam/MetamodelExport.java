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
package de.iteratec.iteraplan.businesslogic.exchange.elasticeam;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.elasticeam.emfimpl.EMFMetamodel;
import de.iteratec.iteraplan.elasticeam.exception.ElasticeamException;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeaturePermissions;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitivePropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitiveTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypePermissions;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeHierarchic;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedClass;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedProperty;
import de.iteratec.iteraplan.model.AbstractAssociation;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.Seal;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.user.ContainsTypeOfFunctionalPermissionFilter;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;


/**
 * Class to provide all functionality to create a {@link de.iteratec.iteraplan.elasticeam.metamodel.Metamodel Metamodel}
 * instance that contains all {@link UniversalTypeExpression}s as defined by the current {@link TypeOfExchange}
 * 
 */
@SuppressWarnings("PMD.TooManyMethods")
public class MetamodelExport {

  private static final Logger              LOGGER                        = Logger.getIteraplanLogger(MetamodelExport.class);

  public static final String               HIST_PACKAGE_NAME             = "de.itertec.iteraplan.model.history";
  public static final String               PACKAGE_NAME                  = "model";
  public static final Package              MODEL                         = BusinessMapping.class.getPackage();
  public static final Package              USER                          = UserEntity.class.getPackage();

  public static final String               INFORMATION_FLOW              = "InformationFlow";
  public static final String               INFORMATION_FLOW_ISI          = "informationSystemInterface";
  public static final String               INFORMATION_FLOW_IS1          = "informationSystemRelease1";
  public static final String               INFORMATION_FLOW_IS2          = "informationSystemRelease2";
  public static final String               INFORMATION_FLOW_IS1_OPP      = "informationFlows1";
  public static final String               INFORMATION_FLOW_IS2_OPP      = "informationFlows2";
  public static final String               INFORMATION_FLOW_ISI_ID       = "iteraplan_InformationSystemInterfaceID";
  public static final String               INFORMATION_FLOW_ID           = "id";

  private static final Set<String>         IGNORED_ATTRIBUTES_CONCEPTUAL = Sets.newHashSet("olVersion", "state");
  public static final String               JOINED_RELEASE_ATTRIBUTE      = "version";
  private static final Set<String>         IGNORED_RELATIONSHIPS         = Sets.newHashSet("InformationSystem.releases",
                                                                             "TechnicalComponent.releases",
                                                                             "InformationSystemInterface.informationSystemReleaseA",
                                                                             "InformationSystemInterface.informationSystemReleaseB");

  private final Map<String, HbMappedClass> hbClassData;

  private final BuildingBlockTypeDAO       buildingBlockTypeDAO;
  private final SessionFactory             sessionFactory;

  /**
   * enum to distinguish between the different export and import types
   * Possible occurrences
   *    BACKUP
   *        consideres all db tables;
   *        purpose is to reinitialize the db from scratch
   *    CONCEPTUAL
   *        considers all {@link BuildingBlock} {@link Class}es
   *    USER_AND_ROLES
   *        considers all {@link Class}es of the de.iteratec.iteraplan.model.user {@link Package} 
   */
  public enum TypeOfExchange {
    BACKUP("backup"), CONCEPTUAL("conceptual"), USER_AND_ROLES("user");

    private final String typeOfStatus;

    TypeOfExchange(String typeOfStatus) {
      this.typeOfStatus = typeOfStatus;
    }

    /**
     * Returns the current string value stored in the Enum.
     * 
     * @return String value.
     */
    @Override
    public String toString() {
      return typeOfStatus;
    }
  }

  /**
   * 
   * Default constructor.
   * 
   * @param hbClassData
   *    Meta information about the iteraplan model classes
   * @param bbtDAO
   *    {@link BuildingBlockTypeDAO} for accessing {@link BuildingBlockType}s and their connected {@link AttributeType}s
   * @param sessionFactory
   *    {@link SessionFactory} for loading {@link Role}s of current (and MASTER) dataSource
   */
  public MetamodelExport(Map<String, HbMappedClass> hbClassData, BuildingBlockTypeDAO bbtDAO, SessionFactory sessionFactory) {
    this.hbClassData = Preconditions.checkNotNull(hbClassData);
    this.buildingBlockTypeDAO = Preconditions.checkNotNull(bbtDAO);
    this.sessionFactory = Preconditions.checkNotNull(sessionFactory);

  }

  /**
   * Creates {@link UniversalTypeExpression}s from the iteraplan db metamodel
   * 
   * @param selectedExportType
   * @return a {@link de.iteratec.iteraplan.elasticeam.metamodel.Metamodel Metamodel} containing all
   *        {@link UniversalTypeExpression}s that are defined for the selected {@link TypeOfExchange}
   */
  public IteraplanMapping loadMetamodel(TypeOfExchange selectedExportType) {
    return (new ExportRun(selectedExportType, hbClassData, this.buildingBlockTypeDAO, this.sessionFactory));
  }

  /**
   * Private subclass of {@link IteraplanMapping} which is responsible for transforming the iteraplan model classes 
   * into a valid {@link de.iteratec.iteraplan.elasticeam.metamodel.Metamodel Metamodel} and for providing mapping
   * information for the sources of the {@link de.iteratec.iteraplan.elasticeam.metamodel.Metamodel Metamodel}'s elements
   */
  private static final class ExportRun extends IteraplanMapping {

    /**
     * 
     * Default constructor.
     * @param toe {@link TypeOfExchange}
     * @param hbClassData {@link Map} of all {@link HbMappedClass}es
     */
    ExportRun(TypeOfExchange toe, Map<String, HbMappedClass> hbClassData, BuildingBlockTypeDAO buildingBlockTypeDAO, SessionFactory sessionFactory) {
      super(toe, new EMFMetamodel(toe.toString()));
      createEnumerations(hbClassData);
      createSubstantialTypes(hbClassData);
      createRelationshipTypes(hbClassData);
      createPropertiesForAttributeTypes(buildingBlockTypeDAO, hbClassData);
      addReadPermissionsForMasterDsRoles(sessionFactory);
    }

    /**
     * Create {@link EnumerationExpression}s for all {@link Enum}s that are used by 
     * {@link Enum}-attributes that are required for the current {@link TypeOfExchange}
     * 
     * @param hbClassData a {@link Map} of all {@link HbMappedClass}es
     */
    @SuppressWarnings("unchecked")
    protected void createEnumerations(Map<String, HbMappedClass> hbClassData) {
      LOGGER.debug("\n\n\n\t  ### CREATING ENUMERATIONS");
      long time = -System.currentTimeMillis();
      int cnt = 0;
      int cntLiterals = 0;

      for (HbMappedClass hbClass : hbClassData.values()) {
        if (needsToBeProcessed(hbClass)) {
          for (HbMappedProperty hbEnumProp : hbClass.getAllEnums()) {
            if (needsToBeProcessed(hbEnumProp)) {
              Class<? extends Enum<?>> enumm = (Class<? extends Enum<?>>) hbEnumProp.getType();
              EnumerationExpression enumeration = resolve(enumm);
              if (enumeration == null) {
                enumeration = getEMFMetamodel().createEnumeration(enumm.getName());
                enumeration.setName(enumm.getName());
                List<String> defaultColors = SpringGuiFactory.getInstance().getVbbClusterColors();
                for (Enum<?> literal : enumm.getEnumConstants()) {
                  EnumerationLiteralExpression ele = getEMFMetamodel().createEnumerationLiteral(enumeration, literal.name(),
                      Color.decode("#" + defaultColors.get(cntLiterals % defaultColors.size())));
                  add(ele, literal);
                  ele.setName(literal.name());
                  cntLiterals += 1;
                }
                LOGGER.debug("Created Enumeration {0} (literals#{1})", enumm.getName(), Integer.valueOf(enumm.getEnumConstants().length));
                cnt += 1;
                add(enumeration, enumm);
              }
            }
          }
        }
      }
      LOGGER.debug("... finished creation of {0} enumerations with {1} literals in {2}ms\n\n\n", Integer.valueOf(cnt), Integer.valueOf(cntLiterals),
          BigInteger.valueOf(System.currentTimeMillis() + time));
    }

    /**
     * Creates {@link SubstantialTypeExpression}s for all {@link HbMappedClass}es that are 
     * required for the current {@link TypeOfExchange}
     * 
     * @param hbClassData 
     *  A {@link Map} of all {@link HbMappedClass}es
     */
    @SuppressWarnings("unchecked")
    protected void createSubstantialTypes(Map<String, HbMappedClass> hbClassData) {
      LOGGER.debug("\n\n\n\t  ### CREATING SUBSTANTIAL_TYPES");
      long time = -System.currentTimeMillis();
      int cnt = 0;
      int cntProps = 0;
      for (HbMappedClass hbClass : hbClassData.values()) {
        if (needsToBeProcessed(hbClass) && !needsSpecialTreatment(hbClass)) {
          LOGGER.debug("Creating SubstantialTypeExpression for {0}", hbClass.getClassName());
          cnt += 1;
          SubstantialTypeExpression ste = getEMFMetamodel().createSubstantialType(transformName(hbClass.getClassName()));
          add(ste, hbClass);
          Class<?> bbClass = hbClass.hasReleaseClass() ? hbClass.getReleaseClass().getMappedClass() : hbClass.getMappedClass();
          addNamesAndAbbreviations(ste, bbClass);
          for (HbMappedProperty hbProp : hbClass.getAllProperties()) {
            if (needsToBeProcessed(hbProp)) {
              String name = transformName(hbProp.getName());
              if (DEFAULT_ATTRIBUTES.contains(name)) {
                PropertyExpression<?> property = ste.findPropertyByName(name);
                add(ste, property, hbProp.getGetMethod());
              }
              else {
                cntProps += 1;
                int lower = hbProp.isOptional() ? 0 : 1;
                int upper = hbProp.isMany() ? FeatureExpression.UNLIMITED : 1;
                PrimitiveTypeExpression type = getOrCreatePrimitiveType(hbProp.getType());
                PropertyExpression<?> property = getEMFMetamodel().createProperty(ste, name, lower, upper, type);
                addNames(property, bbClass);
                add(ste, property, hbProp.getGetMethod());
              }
            }
          }
          for (HbMappedProperty hbEnumProp : hbClass.getAllEnums()) {
            if (needsToBeProcessed(hbEnumProp)) {
              cntProps += 1;
              Class<? extends Enum<?>> type = (Class<? extends Enum<?>>) hbEnumProp.getType();
              EnumerationExpression enumExpr = resolve(type);
              int lower = hbEnumProp.isOptional() ? 0 : 1;
              int upper = hbEnumProp.isMany() ? FeatureExpression.UNLIMITED : 1;
              PropertyExpression<?> property = getEMFMetamodel().createProperty(ste, transformName(hbEnumProp.getName()), lower, upper, enumExpr);
              addNames(property, bbClass);
              add(ste, property, hbEnumProp.getGetMethod());
            }

          }
        }
      }
      LOGGER.debug("Created {0} SubstantialTypes (with {1} properties) in {2}ms\n\n\n", Integer.valueOf(cnt), Integer.valueOf(cntProps),
          BigInteger.valueOf(System.currentTimeMillis() + time));
    }

    private void addNames(PropertyExpression<?> property, Class<?> bbClass) {
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
      }
    }

    private PrimitiveTypeExpression getOrCreatePrimitiveType(Class<?> clazz) {

      Class<?> dataTypeClass = clazz;
      if (clazz.equals(Integer.class)) {
        dataTypeClass = BigInteger.class;
      }
      else if (boolean.class.equals(clazz)) {
        dataTypeClass = Boolean.class;
      }

      PrimitiveTypeExpression pte = resolvePT(dataTypeClass);
      if (pte != null) {
        return pte;
      }

      pte = (PrimitiveTypeExpression) getEMFMetamodel().findTypeByPersistentName(dataTypeClass.getName());
      if (pte != null) {
        return pte;
      }

      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR, "No primitive type defined for class " + dataTypeClass);
    }

    /**
     * Add naming information (names and abbreviations) for all available {@link Locale}s
     * 
     * @param ste 
     * @param bbClass the iteraplan model {@link Class}
     */
    private void addNamesAndAbbreviations(SubstantialTypeExpression ste, Class<?> bbClass) {
      TypeOfBuildingBlock tob = TypeOfBuildingBlock.typeOfBuildingBlockForClass(bbClass);
      if (tob == null) {
        return;
      }
      String bbKey = getBBKey(tob);
      for (String localeString : Constants.LOCALES) {
        Locale locale = new Locale(localeString);
        ste.setName(MessageAccess.getStringOrNull(tob.getValue(), locale), locale);
        ste.setAbbreviation(MessageAccess.getStringOrNull(bbKey + ".abbr", locale), locale);
        ste.setDescription(MessageAccess.getStringOrNull("glossary." + bbKey, locale), locale);
      }
    }

    private void addNamesAndAbbreviations(RelationshipExpression relationship) {
      for (String localeString : Constants.LOCALES) {
        Locale locale = new Locale(localeString);
        List<RelationshipEndExpression> relationshipEnds = relationship.getRelationshipEnds();
        if (relationshipEnds.size() == 2) {
          RelationshipEndExpression ree1 = relationshipEnds.get(0);
          RelationshipEndExpression ree2 = relationshipEnds.get(1);
          UniversalTypeExpression ute1 = ree1.getHolder();
          UniversalTypeExpression ute2 = ree2.getHolder();
          if (ute1.equals(ute2)) {
            //set names but no abbreviations
            String name1 = transformName(ree1.getName(locale));
            String name2 = transformName(ree2.getName(locale));
            if (name1 != null && !name1.equals(ree1.getName(locale))) {
              ree1.setName(name1, locale);
            }
            if (name2 != null && !name2.equals(ree2.getName(locale))) {
              ree2.setName(name2, locale);
            }
          }
          else {
            //set abbreviations but no names
            String abbr1 = ute2.getAbbreviation(locale).toLowerCase();
            String abbr2 = ute1.getAbbreviation(locale).toLowerCase();
            if (ute1.findRelationshipEndByName(abbr1) != null) {
              ute1.findRelationshipEndByName(abbr1).setAbbreviation(abbr1 + "A", locale);
              abbr2 += "B";
            }
            if (ute2.findRelationshipEndByName(abbr2) != null) {
              ute2.findRelationshipEndByName(abbr2).setAbbreviation(abbr2 + "A", locale);
              abbr1 += "B";
            }
            ree1.setAbbreviation(abbr1, locale);
            ree2.setAbbreviation(abbr2, locale);
          }

        }
        else {
          LOGGER.error("Did not find expected number of RelationshipEnds for Relationship " + relationship + " expected 2 but was "
              + relationshipEnds.size());
        }
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

    private static class Relationships {
      private final List<HbMappedProperty> hbRelationships      = Lists.newArrayList();
      private final List<HbMappedProperty> abstractAssociations = Lists.newArrayList();

      void addHbRelationship(HbMappedProperty hbMappedProperty) {
        this.hbRelationships.add(hbMappedProperty);
      }

      void addAbstractAssociation(HbMappedProperty hbMappedProperty) {
        this.abstractAssociations.add(hbMappedProperty);
      }

      private Iterator<HbMappedProperty[]> relationshipPairsIterator() {
        return new Iterator<HbMappedProperty[]>() {
          public boolean hasNext() {
            return !hbRelationships.isEmpty();
          }

          public HbMappedProperty[] next() {
            HbMappedProperty first = hbRelationships.get(0);
            if (first.getOpposite() != null) {
              hbRelationships.remove(first.getOpposite());
            }
            hbRelationships.remove(first);
            return new HbMappedProperty[] { first, first.getOpposite() };
          }

          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }

      Iterable<HbMappedProperty[]> relationshipPairs() {
        return new Iterable<HbMappedProperty[]>() {
          public Iterator<HbMappedProperty[]> iterator() {
            return relationshipPairsIterator();
          }
        };
      }
    }

    /**
     * Creating {@link RelationshipTypeExpression}s for all associations among the considered model {@link Class}es
     * 
     * @param hbClassData a {@link Map} of all {@link HbMappedClass}es
     */
    protected void createRelationshipTypes(Map<String, HbMappedClass> hbClassData) {
      long time = -System.currentTimeMillis();
      int cnt = 0;
      int mixinCnt = 0;
      LOGGER.debug("\n\n\n #### CREATING RELATIONSHIP_TYPES");

      Relationships relationships = determineRelationships(hbClassData);

      for (HbMappedProperty[] hbRelPair : relationships.relationshipPairs()) {
        HbMappedClass hbClass1 = hbRelPair[0].getContainingClass();
        if (hbClass1.isReleaseClass()) {
          hbClass1 = hbClass1.getReleaseBase();
        }
        SubstantialTypeExpression ste = getSubstantialTypeExpression(hbClass1);
        int lower = hbRelPair[0].isOptional() ? 0 : 1;
        int upper = hbRelPair[0].isMany() ? FeatureExpression.UNLIMITED : 1;

        if (hbRelPair[1] != null) {
          HbMappedClass hbClass2 = hbRelPair[1].getContainingClass();
          if (hbClass2.isReleaseClass()) {
            hbClass2 = hbClassData.get(hbClass2.getMappedClass().getCanonicalName().replace("Release", ""));
          }
          if ((MixinTypeHierarchic.PARENT.getPersistentName().equals(hbRelPair[0].getName()) && MixinTypeHierarchic.CHILDREN.getPersistentName()
              .equals(hbRelPair[1].getName()))
              || (MixinTypeHierarchic.PARENT.getPersistentName().equals(hbRelPair[1].getName()) && MixinTypeHierarchic.CHILDREN.getPersistentName()
                  .equals(hbRelPair[0].getName())) && hbClass1.equals(hbClass2)) {
            mixinCnt += 1;
            getEMFMetamodel().addMixin(ste, MixinTypeHierarchic.INSTANCE);
          }
          else {
            String relName = getRelationshipName(hbClass1, hbRelPair[0]);

            if (hbClass2.hasSubClasses()) {
              int lowerOpp = hbRelPair[1].isOptional() ? 0 : 1;
              int upperOpp = hbRelPair[1].isMany() ? FeatureExpression.UNLIMITED : 1;

              for (HbMappedClass hbSubclass : hbClass2.getSubClasses()) {
                if (needsToBeProcessed(hbSubclass) && !needsSpecialTreatment(hbSubclass)) {
                  SubstantialTypeExpression oppSubTypeExpression = getSubstantialTypeExpression(hbSubclass);
                  String relNameOpp = transformName(hbRelPair[1].getName() + "_" + hbSubclass.getClassName());
                  if (oppSubTypeExpression != null) {
                    cnt += 1;
                    RelationshipExpression re = getEMFMetamodel().createRelationship(relName, ste, transformName(hbRelPair[0].getName()), lower,
                        upper, oppSubTypeExpression, transformName(relNameOpp), lowerOpp, upperOpp);
                    addNamesAndAbbreviations(re);
                  }
                }
              }
            }
            else {
              int lowerOpp = hbRelPair[1].isOptional() ? 0 : 1;
              int upperOpp = hbRelPair[1].isMany() ? FeatureExpression.UNLIMITED : 1;
              String oppName = transformName(hbRelPair[1].getName());
              SubstantialTypeExpression oppTypeExpression = getSubstantialTypeExpression(hbClass2);
              RelationshipExpression re = getEMFMetamodel().createRelationship(relName, ste, transformName(hbRelPair[0].getName()), lower, upper,
                  oppTypeExpression, transformName(oppName), lowerOpp, upperOpp);
              cnt += 1;
              addNamesAndAbbreviations(re);
            }
          }

        }
      }
      LOGGER.debug("Created {0} RelationshipExpressions (and applied {1} mixins) in {2}ms", Integer.valueOf(cnt), Integer.valueOf(mixinCnt),
          BigInteger.valueOf(System.currentTimeMillis() + time));

      HbMappedClass businessMapping = hbClassData.get(BusinessMapping.class.getCanonicalName());
      if (needsToBeProcessed(businessMapping)) {
        createBusinessMapping(businessMapping);
      }

      time = -System.currentTimeMillis();
      cnt = 0;
      for (HbMappedClass hbClass : hbClassData.values()) {
        if (needsToBeProcessed(hbClass) && hbClass.getMappedClass() != null
            && AbstractAssociation.class.equals(hbClass.getMappedClass().getSuperclass())) {
          createRelationshipTypeForAbstractAssociationSubclass(hbClass);
          cnt += 1;
        }
      }
      LOGGER.debug("Created {0} RelationshipTypeExpressions for subclasses of AbstractAssociation in {1}", Integer.valueOf(cnt),
          BigInteger.valueOf(System.currentTimeMillis() + time));

    }

    /**
     * Determines which associations among the iteraplan model classes 
     * needs to be processed for the current {@link TypeOfExchange}
     * 
     * @param hbClassData a {@link Map} of all {@link HbMappedClass}es
     * 
     * @return all {@link Relationships} that need to be considered for the current {@link TypeOfExchange}
     */
    private Relationships determineRelationships(Map<String, HbMappedClass> hbClassData) {
      Relationships relationships = new Relationships();

      for (HbMappedClass hbClass : hbClassData.values()) {
        if (needsToBeProcessed(hbClass) && !needsSpecialTreatment(hbClass)) {
          for (HbMappedProperty hbRelation : hbClass.getAllRelations()) {
            if (needsToBeProcessed(hbRelation)) {
              HbMappedClass hbType = hbRelation.getHbType();
              if (hbType != null && hbType.isReleaseClass()) {
                hbType = hbClassData.get(hbType.getMappedClass().getCanonicalName().replace("Release", ""));
              }

              Class<?> type = hbRelation.getType();
              if (type != null && hbType != null && needsToBeProcessed(hbType) && !BusinessMapping.class.equals(type)) {
                if (AbstractAssociation.class.equals(type.getSuperclass())) {
                  relationships.addAbstractAssociation(hbRelation);
                }
                else {
                  relationships.addHbRelationship(hbRelation);
                }
              }
            }

          }
        }
      }
      return relationships;
    }

    /**
     * Creates a {@link RelationshipTypeExpression} for, all {@link PropertyExpression}s and {@link RelationshipTypeExpression}s for {@link BusinessMapping}
     * 
     * @param businessMapping
     * @param hbClassData
     */
    @SuppressWarnings("unchecked")
    private void createBusinessMapping(HbMappedClass businessMapping) {
      long time = -System.currentTimeMillis();
      List<HbMappedProperty> businessMappingRelationshipEnds = Lists.newArrayList();

      for (HbMappedProperty hbRelation : businessMapping.getAllRelations()) {
        if (needsToBeProcessed(hbRelation)) {
          HbMappedClass hbType = hbRelation.getHbType();
          if (hbType.isReleaseClass()) {
            hbType = hbType.getReleaseBase();
          }
          SubstantialTypeExpression ste = getSubstantialTypeExpression(hbType);
          if (ste == null && needsToBeProcessed(hbType)) {
            LOGGER.error("Could not find SubstantialTypeExpression for BusinessMapping's relation " + hbRelation);
          }
          else if (ste != null) {
            HbMappedProperty opposite = hbRelation.getOpposite();
            if (opposite == null && !needsToBeProcessed(hbType)) {
              LOGGER.error("Could not find SubstantialTypeExpression for BusinessMapping relation's opposite " + hbRelation);
            }
            else {
              businessMappingRelationshipEnds.add(hbRelation);
            }
          }
        }
      }
      if (businessMappingRelationshipEnds.isEmpty()) {
        LOGGER.error("BusinessMapping's relationship ends have not been processed");
      }
      else {
        RelationshipTypeExpression rte = getEMFMetamodel().createRelationshipType("BusinessMapping");
        for (String localeString : Constants.LOCALES) {
          Locale locale = new Locale(localeString);
          rte.setName(MessageAccess.getStringOrNull("businessMapping.singular", locale), locale);
          rte.setAbbreviation(MessageAccess.getStringOrNull("businessMapping.abbr", locale), locale);
        }
        add(rte, businessMapping);
        for (HbMappedProperty hbRelation : businessMappingRelationshipEnds) {
          HbMappedClass hbType = hbRelation.getHbType();
          if (hbType.isReleaseClass()) {
            hbType = hbType.getReleaseBase();
          }
          SubstantialTypeExpression ste = getSubstantialTypeExpression(hbType);
          HbMappedProperty opposite = hbRelation.getOpposite();
          int lower = InformationSystem.class.getSimpleName().equals(ste.getPersistentName()) ? 1 : 0;
          RelationshipExpression re = getEMFMetamodel().createRelationship("BM_" + ste.getAbbreviation(), rte, hbRelation.getName(), lower, 1, ste,
              opposite.getName(), 0, FeatureExpression.UNLIMITED);
          addNamesAndAbbreviations(re);
        }

        for (HbMappedProperty hbProp : businessMapping.getAllProperties()) {
          if (needsToBeProcessed(hbProp)) {
            int lower = hbProp.isOptional() ? 0 : 1;
            int upper = hbProp.isMany() ? FeatureExpression.UNLIMITED : 1;
            if (DEFAULT_ATTRIBUTES.contains(hbProp.getName())) {
              PropertyExpression<?> property = rte.findPropertyByName(hbProp.getName());
              if (property != null) {
                add(rte, property, hbProp.getGetMethod());
              }
            }
            else {
              PrimitiveTypeExpression type = getOrCreatePrimitiveType(hbProp.getType());
              PrimitivePropertyExpression elaticProperty = getEMFMetamodel().createProperty(rte, hbProp.getName(), lower, upper, type);
              add(rte, elaticProperty, hbProp.getGetMethod());
            }
          }
        }
        for (HbMappedProperty hbEnumProp : businessMapping.getAllEnums()) {
          if (needsToBeProcessed(hbEnumProp)) {
            int lower = hbEnumProp.isOptional() ? 0 : 1;
            int upper = hbEnumProp.isMany() ? FeatureExpression.UNLIMITED : 1;
            getEMFMetamodel().createProperty(rte, hbEnumProp.getName(), lower, upper, resolve((Class<? extends Enum<?>>) hbEnumProp.getType()));
          }
        }
      }

      LOGGER.debug("Handled BusinessMapping in {0}ms", BigInteger.valueOf(System.currentTimeMillis() + time));
    }

    /**
     * Create {@link RelationshipTypeExpression}s for all subclasses of {@link AbstractAssociation}
     * 
     * @param assocHbClass
     */
    @SuppressWarnings("unchecked")
    private void createRelationshipTypeForAbstractAssociationSubclass(HbMappedClass assocHbClass) {
      List<HbMappedProperty> relationshipEnds = Lists.newArrayList();
      long time = -System.currentTimeMillis();
      for (HbMappedProperty rel : assocHbClass.getAllRelations()) {
        if (needsToBeProcessed(rel)) {
          Class<?> type = rel.getType();
          HbMappedClass hbType = rel.getHbType();
          if (hbType.isReleaseClass()) {
            hbType = hbType.getReleaseBase();
          }
          if (needsToBeProcessed(hbType)) {
            SubstantialTypeExpression typeExpression = getSubstantialTypeExpression(hbType);
            if (typeExpression == null) {
              LOGGER.error("Did not find HbMappedClass for Type: '" + type + "'");
            }
            else {
              HbMappedProperty opposite = rel.getOpposite();
              if (opposite == null) {
                LOGGER.debug("Found property {0} without opposite", rel.toString());
              }
              else {
                relationshipEnds.add(rel);
              }
            }
          }
        }
      }
      if (relationshipEnds.isEmpty()) {
        LOGGER.error("Did not create any relationship ends for HbMappedClass " + assocHbClass);
      }
      else {
        String typeName = transformName(assocHbClass.getClassName());
        RelationshipTypeExpression rte = getEMFMetamodel().createRelationshipType(typeName);
        for (String localeString : Constants.LOCALES) {
          Locale locale = new Locale(localeString);
          rte.setName(typeName, locale);
          String abbr = typeName.replace("Association", "");
          abbr = abbr.replace(INFORMATION_FLOW, "IF");
          rte.setAbbreviation(abbr, locale);
        }

        for (HbMappedProperty hbRelation : relationshipEnds) {
          HbMappedClass hbType = hbRelation.getHbType();
          if (hbType.isReleaseClass()) {
            hbType = hbType.getReleaseBase();
          }
          SubstantialTypeExpression ste = getSubstantialTypeExpression(hbType);
          HbMappedProperty opposite = hbRelation.getOpposite();
          RelationshipExpression re = getEMFMetamodel().createRelationship("", rte, transformName(hbRelation.getName()),
              hbRelation.isOptional() ? 0 : 1, hbRelation.isMany() ? FeatureExpression.UNLIMITED : 1, ste, transformName(opposite.getName()),
              opposite.isOptional() ? 0 : 1, opposite.isMany() ? FeatureExpression.UNLIMITED : 1);
          addNamesAndAbbreviations(re);
        }

        add(rte, assocHbClass);
        for (HbMappedProperty hbProp : assocHbClass.getAllProperties()) {
          if (needsToBeProcessed(hbProp)) {
            int lower = hbProp.isOptional() ? 0 : 1;
            int upper = hbProp.isMany() ? FeatureExpression.UNLIMITED : 1;
            if (DEFAULT_ATTRIBUTES.contains(hbProp.getName())) {
              PropertyExpression<?> property = rte.findPropertyByName(hbProp.getName());
              if (property != null) {
                add(rte, property, hbProp.getGetMethod());
              }
            }
            else {
              PrimitiveTypeExpression type = getOrCreatePrimitiveType(hbProp.getType());
              PrimitivePropertyExpression elaticProperty = getEMFMetamodel().createProperty(rte, transformName(hbProp.getName()), lower, upper, type);
              add(rte, elaticProperty, hbProp.getGetMethod());
            }
          }
        }
        for (HbMappedProperty hbEnumProp : assocHbClass.getAllEnums()) {
          if (needsToBeProcessed(hbEnumProp)) {
            int lower = hbEnumProp.isOptional() ? 0 : 1;
            int upper = hbEnumProp.isMany() ? FeatureExpression.UNLIMITED : 1;
            getEMFMetamodel().createProperty(rte, hbEnumProp.getName(), lower, upper, resolve((Class<? extends Enum<?>>) hbEnumProp.getType()));
          }
        }
        applySpecialTransformations(rte);
      }
      LOGGER.debug("Created RelationshipTypeExpression for {0} in {1}ms", assocHbClass.getClassName(),
          BigInteger.valueOf(System.currentTimeMillis() + time));
    }

    /**
     * Applies special transformations for {@link RelationshipTypeExpression}s
     * 
     * @param rte
     *      The {@link RelationshipTypeExpression}
     */
    private void applySpecialTransformations(RelationshipTypeExpression rte) {
      if (INFORMATION_FLOW.equals(rte.getPersistentName())) {
        SubstantialTypeExpression isType = getSubstantialTypeExpression(InformationSystem.class.getSimpleName());
        RelationshipEndExpression businessObject = rte.findRelationshipEndByPersistentName("businessObject");
        RelationshipEndExpression informationSystemInterface = rte.findRelationshipEndByPersistentName("informationSystemInterface");
        RelationshipExpression flow2Bo = businessObject.getRelationship();
        RelationshipExpression flow2Isi = informationSystemInterface.getRelationship();

        getEMFMetamodel().createProperty(rte, INFORMATION_FLOW_ISI_ID, 0, 1, BuiltinPrimitiveType.INTEGER);
        String relName = flow2Bo.getPersistentName();
        String end0Name = businessObject.getPersistentName();
        String end1Name = flow2Bo.getOppositeEndFor(businessObject).getPersistentName();
        UniversalTypeExpression boType = businessObject.getType();

        getEMFMetamodel().deleteRelationship(flow2Bo);
        getEMFMetamodel().createRelationship(relName, rte, end0Name, 0, 1, boType, end1Name, 0, FeatureExpression.UNLIMITED);

        relName = flow2Isi.getPersistentName();
        end0Name = informationSystemInterface.getPersistentName();
        end1Name = flow2Isi.getOppositeEndFor(informationSystemInterface).getPersistentName();
        UniversalTypeExpression isiType = informationSystemInterface.getType();

        getEMFMetamodel().deleteRelationship(flow2Isi);
        getEMFMetamodel().createRelationship(relName, rte, end0Name, 0, 1, isiType, end1Name, 1, FeatureExpression.UNLIMITED);

        RelationshipExpression rel1 = getEMFMetamodel().createRelationship("IF_IS_1", isType, INFORMATION_FLOW_IS1_OPP, 0,
            FeatureExpression.UNLIMITED, rte, INFORMATION_FLOW_IS1, 0, 1);
        RelationshipExpression rel2 = getEMFMetamodel().createRelationship("IF_IS_2", isType, INFORMATION_FLOW_IS2_OPP, 0,
            FeatureExpression.UNLIMITED, rte, INFORMATION_FLOW_IS2, 0, 1);
        addNamesAndAbbreviations(rel1);
        addNamesAndAbbreviations(rel2);
      }

    }

    /**
     * Responsible for creating {@link PropertyExpression}s for all {@link AttributeType} instances
     *  
     * @param bbtDAO
     * @param hbClassData
     */
    private void createPropertiesForAttributeTypes(BuildingBlockTypeDAO bbtDAO, Map<String, HbMappedClass> hbClassData) {
      long time = -System.currentTimeMillis();
      int cnt = 0;
      LOGGER.debug("\n\n\n    ###### Creating Properties for AttributeTypes");
      List<BuildingBlockType> bbts = bbtDAO.loadElementList("id");
      for (BuildingBlockType bbt : bbts) {
        TypeOfBuildingBlock tobb = bbt.getTypeOfBuildingBlock();
        if (!UserContext.getCurrentPerms().getUserHasBbTypeFunctionalPermission(bbt.getTypeOfBuildingBlock().getValue())) {
          continue;
        }

        for (AttributeType at : bbt.getAttributeTypes()) {
          long tmp = -System.currentTimeMillis();
          if (!UserContext.getCurrentPerms().userHasAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ)) {
            continue;
          }
          LOGGER.debug("Checked perms in {0}ms", BigInteger.valueOf(System.currentTimeMillis() + tmp));

          Class<? extends BuildingBlock> bbClass = tobb.getAssociatedClass();
          HbMappedClass bbHbClass = hbClassData.get(bbClass.getCanonicalName());
          if (bbHbClass.isReleaseClass()) {
            bbHbClass = bbHbClass.getReleaseBase();
          }
          if (needsToBeProcessed(bbHbClass)) {
            addAttributeType(at, bbHbClass);
            cnt += 1;
          }
          LOGGER.debug("Created PropertyExpression for {0} {1} in {2}ms", at.getClass().getSimpleName(), at.getName(),
              BigInteger.valueOf(System.currentTimeMillis() + tmp));
        }
      }
      LOGGER.debug("Created {0} Properties for AttributeTypes in {1}ms", Integer.valueOf(cnt), BigInteger.valueOf(System.currentTimeMillis() + time));
    }

    /**
     * If the data is read from a data source, other than the MASTER data source, 
     * we need to ensure that the roles that are defined within the master data source 
     * do have read permissions for all types and features; otherwise it is likely that 
     * the users cannot access any data of the metamodel or model
     * 
     * @param sessionFactory
     *  The {@link SessionFactory} that is used to open a new session to allow the accessing of roles in the master data source
     */
    @SuppressWarnings({ "unchecked", "cast" })
    private void addReadPermissionsForMasterDsRoles(SessionFactory sessionFactory) {
      String key = UserContext.getActiveDatasource();
      Session session = null;
      Criteria cQuery = null;
      List<Role> roles = null;
      if (!Constants.MASTER_DATA_SOURCE.equals(key)) {
        try {
          UserContext.getCurrentUserContext().setDataSource(Constants.MASTER_DATA_SOURCE);
          session = sessionFactory.openSession();
          session.setFlushMode(FlushMode.MANUAL);
          session.setCacheMode(CacheMode.GET);

          Transaction t = session.beginTransaction();
          try {
            cQuery = session.createCriteria(Role.class);
            roles = (List<Role>) cQuery.list();
            addPermissions(roles);
            t.commit();
          } catch (HibernateException e) {
            t.rollback();
            LOGGER.error(e);
          } finally {
            SessionFactoryUtils.closeSession(session);
          }
        } finally {
          UserContext.getCurrentUserContext().setDataSource(key);
        }

      }
      else {
        session = sessionFactory.getCurrentSession();
        cQuery = session.createCriteria(Role.class);
        roles = (List<Role>) cQuery.list();
        addPermissions(roles);
      }
    }

    /**
     * Add read permissions for all roles if they have the corresponding {@link PermissionFunctional}
     * 
     * @param roles a {@link List} of all {@link Role}s that are defined in the Master DataSource
     */
    private void addPermissions(List<Role> roles) {
      for (UniversalTypeExpression ute : this.getMetamodel().getUniversalTypes()) {
        addTypePermissions(roles, ute);
        addPropertyPermissions(roles, ute);
      }
    }

    private void addTypePermissions(List<Role> roles, UniversalTypeExpression ute) {
      HbMappedClass permissionsHolder = null;
      if (ute instanceof SubstantialTypeExpression) {
        permissionsHolder = getSubstantialTypes().get(ute);
      }
      else {
        permissionsHolder = getRelationshipTypes().get(ute);
      }

      if (permissionsHolder.hasReleaseClass()) {
        permissionsHolder = permissionsHolder.getReleaseClass();
      }
      for (Role role : roles) {
        if (!handlePermissionSpecialCases(ute, role)) {
          for (PermissionFunctional permission : role.getPermissionsFunctionalAggregated()) {
            if (permissionsHolder.getMappedClass().equals(permission.getTypeOfFunctionalPermission().getClassForPermission())) {
              getEMFMetamodel().grantPermission(ute, role, UniversalTypePermissions.READ);
            }
          }
        }
      }
    }

    private void addPropertyPermissions(List<Role> roles, UniversalTypeExpression ute) {
      for (PropertyExpression<?> property : ute.getProperties()) {
        if (isDerivedFromAT(property)) {
          boolean roleWithPermissionForGroupExists = false;
          for (Role role : roles) {
            for (PermissionAttrTypeGroup permission : role.getPermissionsAttrTypeGroup()) {
              if (areSameAttrTypeGroups(permission.getAttrTypeGroup(), resolveAdditionalProperty(property).getAttributeTypeGroup())) {
                getEMFMetamodel().grantPermission(property, role, FeaturePermissions.READ);
                if (!role.isSupervisor()) {
                  roleWithPermissionForGroupExists = true;
                }
              }
            }
          }
          if (!roleWithPermissionForGroupExists) {
            for (Role role : roles) {
              getEMFMetamodel().grantPermission(property, role, FeaturePermissions.READ);
            }
          }
        }
        else {
          for (Role role : roles) {
            getEMFMetamodel().grantPermission(property, role, FeaturePermissions.READ);
          }
        }
      }
    }

    /** 
     * Instead of using the equals() method, we only use ID and name equality, because this won't cause 
     * trouble with Hibernate lazy loading on cache Property reprensentation
     * 
     * @return true if both ATGs are identical by means of name and ID
     */
    private boolean areSameAttrTypeGroups(AttributeTypeGroup left, AttributeTypeGroup right) {
      return left.getId().equals(right.getId()) && left.getName().equals(right.getName());
    }

    /**
     * Handling of permissions for Universal Types without according Functional Permission in iteraplan
     * @param ute
     *          The {@link UniversalTypeExpression} permissions are to set for
     * @param role
     *          The {@link Role} permissions are to set for
     * @return true if the given ute falls into one of the special cases, false otherwise
     */
    private boolean handlePermissionSpecialCases(UniversalTypeExpression ute, Role role) {
      ContainsTypeOfFunctionalPermissionFilter filter = null;
      if (ute.getPersistentName().equals(Tcr2IeAssociation.class.getSimpleName())) {
        filter = new ContainsTypeOfFunctionalPermissionFilter(TypeOfFunctionalPermission.TECHNICALCOMPONENTRELEASES,
            TypeOfFunctionalPermission.INFRASTRUCTUREELEMENT);
      }
      else if (ute.getPersistentName().equals(Isr2BoAssociation.class.getSimpleName())) {
        filter = new ContainsTypeOfFunctionalPermissionFilter(TypeOfFunctionalPermission.INFORMATIONSYSTEMRELEASE,
            TypeOfFunctionalPermission.BUSINESSOBJECT);
      }
      else if (ute.getPersistentName().equals(MetamodelExport.INFORMATION_FLOW)) {
        filter = new ContainsTypeOfFunctionalPermissionFilter(TypeOfFunctionalPermission.INFORMATIONSYSTEMINTERFACE);
      }

      if (filter != null) {
        if (Iterables.any(role.getPermissionsFunctionalAggregated(), filter)) {
          getEMFMetamodel().grantPermission(ute, role, UniversalTypePermissions.READ);
        }
        return true;
      }
      else {
        return false;
      }
    }

    /**
     * Create
     *  {@link EnumerationExpression} for {@link EnumAT}
     *  {@link EnumerationLiteralExpression} for all {@link EnumAV}s
     *  {@link PropertyExpression} for {@link AttributeType}
     *  
     * @param at
     * @param bbHbClass
     */
    private void addAttributeType(AttributeType at, HbMappedClass bbHbClass) {
      UniversalTypeExpression ute = resolve(bbHbClass);
      if (ute == null) {
        LOGGER.error("Did not find SubstantialType for bbt " + bbHbClass.getClassName());
      }
      else {
        int lower = at.isMandatory() ? 1 : 0;
        int upper = 1;
        String name = at.getName();
        Class<?> type = null;
        if (at instanceof TextAT) {
          type = String.class;
        }
        else if (at instanceof NumberAT) {
          type = BigDecimal.class;
        }
        else if (at instanceof DateAT) {
          type = Date.class;
        }
        else if (at instanceof ResponsibilityAT) {
          type = String.class;
          if (((ResponsibilityAT) at).isMultiassignmenttype()) {
            upper = FeatureExpression.UNLIMITED;
          }
        }
        if (type == null && (at instanceof EnumAT)) {
          EnumerationExpression enumm = resolve((EnumAT) at);
          if (enumm == null) {
            EnumAT enumAT = (EnumAT) at;
            enumm = getEMFMetamodel().createEnumeration(enumAT.getClass().getCanonicalName() + "." + enumAT.getName());
            enumm.setName(enumAT.getName());
            add(enumm, enumAT);
            for (EnumAV enumAV : enumAT.getSortedAttributeValues()) {
              Color literalDefaultColor = null;
              try {
                literalDefaultColor = Color.decode("#" + enumAV.getDefaultColorHex());
              } catch (NumberFormatException e) {
                LOGGER.info("Enum literal " + enumAV.getName() + " does not provide a default color.");
              }
              EnumerationLiteralExpression literal = getEMFMetamodel().createEnumerationLiteral(enumm, enumAV.getIdentityString(),
                  literalDefaultColor);
              literal.setName(enumAV.getIdentityString());
              literal.setDescription(enumAV.getDescription());
              add(literal, enumAV);
            }
          }
          if (((EnumAT) at).isMultiassignmenttype()) {
            upper = FeatureExpression.UNLIMITED;
          }
          PropertyExpression<?> property = getEMFMetamodel().createProperty(ute, at.getName(), lower, upper, enumm);
          add(property, at);
        }
        else if (type != null) {
          PrimitiveTypeExpression pte = getOrCreatePrimitiveType(type);
          PropertyExpression<?> property = getEMFMetamodel().createProperty(ute, name, lower, upper, pte);
          add(property, at);
        }
      }
    }

    /**
     * Decides whether a {@link HbMappedClass} needs to be exported or not, 
     * considering the current {@link TypeOfExchange}
     * 
     * @see #getExchangeType()
     * @param hbClass
     * @return true, if the {@link HbMappedClass} needs to be exported
     */
    private boolean needsToBeProcessed(HbMappedClass hbClass) {
      if (hbClass == null) {
        return false;
      }
      else if (getExchangeType().equals(TypeOfExchange.BACKUP)) {
        return true;
      }
      else if (getExchangeType().equals(TypeOfExchange.CONCEPTUAL)) {
        return (hbClass.getMappedClass() != null && MODEL.equals(hbClass.getMappedClass().getPackage())
            && !hbClass.getClassName().contains("History") && !hbClass.hasSubClasses() && !hbClass.isReleaseClass()
            && !Seal.class.equals(hbClass.getMappedClass()) && !BuildingBlockType.class.equals(hbClass.getMappedClass()));
      }
      else {
        return (hbClass.getMappedClass() != null && USER.equals(hbClass.getMappedClass().getPackage()));
      }
    }

    /**
     * Decides whether a {@link HbMappedProperty} needs to be exported or not,
     * considering the current {@link TypeOfExchange} 
     * 
     * @see #getExchangeType()
     * 
     * @param hbProp 
     * @return true, if the {@link HbMappedProperty} needs to be exported
     */
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
      if (TypeOfExchange.CONCEPTUAL.equals(getExchangeType())) {
        if (hbProp.getContainingClass().isReleaseClass() && JOINED_RELEASE_ATTRIBUTE.equals(hbProp.getName())) {
          return false;
        }
        return !IGNORED_ATTRIBUTES_CONCEPTUAL.contains(hbProp.getName());
      }
      // if required, handle other export types here as well
      return true;
    }

    /**
     * Decides whether a {@link HbMappedClass} needs special transformations or not
     * 
     * @param hbClass
     * @return true, if the {@link HbMappedClass} needs to be processed in a special way
     */
    private boolean needsSpecialTreatment(HbMappedClass hbClass) {
      if (hbClass == null) {
        return true;
      }
      else if (hbClass.getMappedClass() != null) {
        Class<?> clazz = hbClass.getMappedClass();
        if (BusinessMapping.class.equals(clazz)) {
          return !getExchangeType().equals(TypeOfExchange.USER_AND_ROLES);
        }
        else if (AbstractAssociation.class.equals(clazz.getSuperclass())) {
          return !getExchangeType().equals(TypeOfExchange.USER_AND_ROLES);
        }
        else {
          return false;
        }
      }
      else {
        return false;
      }
    }

    /**
     * Helper method to create a unique name for a {@link RelationshipTypeExpression}
     * 
     * @param hbClass the {@link HbMappedClass}, containing the {@link HbMappedProperty} representing the relationship
     * @param relation the {@link HbMappedProperty} representing the relationship
     * 
     * @return a concatenated name (class and relationship names, ordered by class names)
     */
    private String getRelationshipName(HbMappedClass hbClass, HbMappedProperty relation) {
      HbMappedClass c2 = relation.getHbType();
      if (c2 == null && !relation.getName().equals("runtimePeriod")) {
        LOGGER.error("Could not find HbMapped class for relationship " + relation + "'s type");
      }
      HbMappedProperty opposite = relation.getOpposite();
      String opName = null;
      if (opposite == null) {
        opName = "noOpposite";
      }
      else {
        opName = opposite.getName();
      }
      String res = null;
      String c2Name = null;
      if (c2 == null) {
        c2Name = "noTargetClass";
        if (relation.getName().equals("runtimePeriod")) {
          c2Name = "RuntimePeriod";
          opName = "backref" + hbClass.getClassName();
        }
      }
      else {
        c2Name = c2.getClassName();
      }
      if (hbClass.getClassName().compareTo(c2Name) < 0) {
        res = hbClass.getClassName() + "." + relation.getName() + "_" + c2Name + "." + opName;
      }
      else if (hbClass.getClassName().compareTo(c2Name) == 0) {
        if (relation.getName().compareTo(opName) < 0) {
          res = hbClass.getClassName() + "." + relation.getName() + "_" + hbClass.getClassName() + "." + opName;
        }
        else {
          res = hbClass.getClassName() + "." + opName + "_" + hbClass.getClassName() + "." + relation.getName();
        }

      }
      else {
        res = c2Name + "." + opName + "_" + hbClass.getClassName() + "." + relation.getName();
      }
      return transformName(res);
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
      res = res.replace(Transport.class.getSimpleName(), INFORMATION_FLOW);
      res = res.replace("transport", "informationFlow");
      return res;
    }
  }

}
