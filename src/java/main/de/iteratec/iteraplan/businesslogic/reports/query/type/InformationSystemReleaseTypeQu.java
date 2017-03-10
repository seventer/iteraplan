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
package de.iteratec.iteraplan.businesslogic.reports.query.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;

import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.HideSubInformationSystemReleasesStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.HideSubInformationSystemReleasesWithConnectionMergingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.IncludeConnectedInformationSystemReleasesStrategy;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 * Extends the class {@link InformationSystemReleaseType} with functionality for textual queries.
 */
public final class InformationSystemReleaseTypeQu extends InformationSystemReleaseType implements ITypeWithDates, ITypeWithStatus, QueryType,
    HierarchicalType<InformationSystemRelease> {

  /** Serialization version. */
  private static final long                           serialVersionUID                                      = 4451574411324097678L;

  private static final InformationSystemReleaseTypeQu INSTANCE                                              = new InformationSystemReleaseTypeQu();

  public static final String                          EXTENSION_INFORMATIONSYSTEMRELEASES                   = "isrE00";
  public static final String                          EXTENSION_ISR_PREDECESSORS                            = "isrE00_predecessors";
  public static final String                          EXTENSION_ISR_CHILDREN                                = "isrE00_children";
  public static final String                          EXTENSION_ISR_BASECOMPONENTS                          = "isrE00_baseComponents";
  public static final String                          EXTENSION_PROJECTS                                    = "isrE01";
  public static final String                          EXTENSION_INFORMATIONSYSTEMDOMAIN                     = "isrE02";
  public static final String                          EXTENSION_INTERFACES_A                                = "isrE03";
  public static final String                          EXTENSION_INTERFACES_B                                = "isrE04";
  public static final String                          EXTENSION_TECHNICALCOMPONENTRELEASES                  = "isrE05";
  public static final String                          EXTENSION_INFRASTRUCTUREELEMENTS                      = "isrE06";
  public static final String                          EXTENSION_BUSINESSOBJECTS                             = "isrE07";
  public static final String                          EXTENSION_BUSINESSFUNCTIONS                           = "isrE08";
  public static final String                          EXTENSION_ARCHITECTURALDOMAINS_VIA_TECHNICALCOMPONENT = "isrE09";
  public static final String                          EXTENSION_FROMISRELEASEOVERFROMCONNECTION             = "isrE10";
  public static final String                          EXTENSION_TOISRELEASEOVERTOCONNECTION                 = "isrE11";
  public static final String                          EXTENSION_SUCCESSORS                                  = "isrE12";
  public static final String                          EXTENSION_PREDECESSORS                                = "isrE13";
  public static final String                          EXTENSION_CHILDREN                                    = "isrE14";
  public static final String                          EXTENSION_PARENT                                      = "isrE15";
  public static final String                          EXTENSION_BUSINESSMAPPINGS                            = "isrE16";
  public static final String                          EXTENSION_BM_BUSINESSUNIT                             = "isrE16_businessUnit";
  public static final String                          EXTENSION_BM_PRODUCT                                  = "isrE16_product";
  public static final String                          EXTENSION_BM_BUSINESSPROCESS                          = "isrE16_businessProcess";
  public static final String                          EXTENSION_BASECOMPONENTS                              = "isrE19";
  public static final String                          EXTENSION_PARENTCOMPONENTS                            = "isrE20";

  public static final String                          EXTENSION_BUSINESSOBJECT_VIA_INTERFACES_A             = "isrE17";
  public static final String                          EXTENSION_BUSINESSOBJECS_VIA_INTERFACES_B             = "isrE18";

  public static final String                          PRESENTATION_EXTENSION_INTERFACES                     = "isrPE1";
  public static final String                          PRESENTATION_EXTENSION_IPURELEASEOVERCONNECTION       = "isrPE2";

  public static final String                          PRESENTATION_EXTENSION_BUSINESSOBJECTOVERCONNECTION   = "isrPE3";

  private InformationSystemReleaseTypeQu() {
    super(ClassUtils.getShortClassName(InformationSystemRelease.class), "isr");
    addSpecialPropertyHQLString(PROPERTY_STARTDATE, new String[] { "is", "null" });
    addSpecialPropertyHQLString(PROPERTY_ENDDATE, new String[] { "is", "null" });
  }

  public static InformationSystemReleaseTypeQu getInstance() {
    assert INSTANCE != null;
    return INSTANCE;
  }

  @Override
  protected void initProperties() {
    addProperty(new Property(PROPERTY_NAME, Constants.ATTRIBUTE_NAME));
    addProperty(new Property(PROPERTY_VERSION, Constants.ATTRIBUTE_VERSION));
    addProperty(new Property(PROPERTY_DESCRIPTION, Constants.ATTRIBUTE_DESCRIPTION));
  }

  @Override
  protected void initExtensions() {

    addExtension(new Extension(EXTENSION_PROJECTS, Constants.EXTENSION_PROJ, getAssociation(ASSOCIATION_PROJECTS)));

    addExtension(new Extension(EXTENSION_INFORMATIONSYSTEMDOMAIN, Constants.EXTENSION_ISD, getAssociation(ASSOCIATION_INFORMATIONSYSTEMDOMAINS)));

    addExtension(new Extension(EXTENSION_INTERFACES_A, Constants.EXTENSION_ISR_ISI, getAssociation(ASSOCIATION_INTERFACES_A)));

    addExtension(new Extension(EXTENSION_INTERFACES_B, Constants.EXTENSION_ISR_ISI, getAssociation(ASSOCIATION_INTERFACES_B)));

    addExtension(new Extension(EXTENSION_TECHNICALCOMPONENTRELEASES, Constants.EXTENSION_TCR, getAssociation(ASSOCIATION_TECHNICALCOMPONENTRELEASES)));

    addExtension(new Extension(EXTENSION_INFRASTRUCTUREELEMENTS, Constants.EXTENSION_IE, getAssociation(ASSOCIATION_INFRASTRUCTUREELEMENTS)));

    addExtension(new Extension(EXTENSION_BUSINESSFUNCTIONS, Constants.EXTENSION_BUSINESSFUNCTION, getAssociation(ASSOCIATION_BUSINESSFUNCTIONS)));

    addExtension(getExtensionToBusinessObject());

    addExtensionToBusinessMappings();

    // Complex extension: Architectural Domains via Technical Components.
    List<TypeWithJoinProperty> typesWithJoinProperties = new ArrayList<TypeWithJoinProperty>();
    typesWithJoinProperties.add(getAssociation(ASSOCIATION_TECHNICALCOMPONENTRELEASES));
    typesWithJoinProperties.add(TechnicalComponentReleaseTypeQu.getInstance().getAssociation(
        TechnicalComponentReleaseTypeQu.ASSOCIATION_ARCHITECTURALDOMAINS));
    addExtension(new Extension(EXTENSION_ARCHITECTURALDOMAINS_VIA_TECHNICALCOMPONENT, Constants.EXTENSION_ISR_AD_VIA_TCR, typesWithJoinProperties));

    // Configures the query extension
    // "Eigenschaften der über Schnittstellen verbundenen Informationssysteme"
    // complex extension information system release over connection (2 paths)
    // path 1: query the following path 1st information system
    // release->connectionsReleaseA->releaseB.
    // returns the 2nd information system release
    typesWithJoinProperties = new ArrayList<TypeWithJoinProperty>();
    typesWithJoinProperties.add(getAssociation(ASSOCIATION_INTERFACES_A));
    typesWithJoinProperties.add(InformationSystemInterfaceTypeQu.getInstance().getAssociation(
        InformationSystemInterfaceTypeQu.ASSOCIATION_INFORMATIONSYSTEMRELEASE_B));
    addExtension(new Extension(EXTENSION_FROMISRELEASEOVERFROMCONNECTION, Constants.EXTENSION_ISR_ISR_VIA_ISI, typesWithJoinProperties));
    // path 2: query the following path 1st information system
    // release->connectionsReleaseB->releaseA.
    // returns the 2nd information system release
    typesWithJoinProperties = new ArrayList<TypeWithJoinProperty>();
    typesWithJoinProperties.add(getAssociation(ASSOCIATION_INTERFACES_B));
    typesWithJoinProperties.add(InformationSystemInterfaceTypeQu.getInstance().getAssociation(
        InformationSystemInterfaceTypeQu.ASSOCIATION_INFORMATIONSYSTEMRELEASE_A));
    addExtension(new Extension(EXTENSION_TOISRELEASEOVERTOCONNECTION, Constants.EXTENSION_ISR_ISR_VIA_ISI, typesWithJoinProperties));
    // END query over connection

    addExtension(new Extension(EXTENSION_SUCCESSORS, Constants.EXTENSION_ISR_SUCC, getAssociation(ASSOCIATION_SUCCESSORS)));

    addExtension(new Extension(EXTENSION_PREDECESSORS, Constants.EXTENSION_ISR_PRED, getAssociation(ASSOCIATION_PREDECESSORS)));

    addExtension(new Extension(EXTENSION_CHILDREN, Constants.EXTENSION_ISR_CHILDREN, getAssociation(ASSOCIATION_CHILDREN)));

    addExtension(new Extension(EXTENSION_PARENT, Constants.EXTENSION_ISR_PARENT, getAssociation(ASSOCIATION_PARENT)));

    // Complex extension: Business Object via Connection and Transport.
    // path 1
    typesWithJoinProperties = new ArrayList<TypeWithJoinProperty>();
    typesWithJoinProperties.add(getAssociation(ASSOCIATION_INTERFACES_A));
    typesWithJoinProperties.add(InformationSystemInterfaceTypeQu.getInstance()
        .getAssociation(InformationSystemInterfaceTypeQu.ASSOCIATION_TRANSPORTS));
    typesWithJoinProperties.add(TransportQueryType.getInstance().getAssociation(TransportQueryType.ASSOCIATION_BUSINESSOBJECT));
    addExtension(new Extension(EXTENSION_BUSINESSOBJECT_VIA_INTERFACES_A, Constants.EXTENSION_ISR_BO_VIA_ISI, typesWithJoinProperties));
    // path 2
    typesWithJoinProperties = new ArrayList<TypeWithJoinProperty>();
    typesWithJoinProperties.add(getAssociation(ASSOCIATION_INTERFACES_B));
    typesWithJoinProperties.add(InformationSystemInterfaceTypeQu.getInstance()
        .getAssociation(InformationSystemInterfaceTypeQu.ASSOCIATION_TRANSPORTS));
    typesWithJoinProperties.add(TransportQueryType.getInstance().getAssociation(TransportQueryType.ASSOCIATION_BUSINESSOBJECT));
    addExtension(new Extension(EXTENSION_BUSINESSOBJECS_VIA_INTERFACES_B, Constants.EXTENSION_ISR_BO_VIA_ISI, typesWithJoinProperties));

    addExtension(new Extension(EXTENSION_BASECOMPONENTS, Constants.EXTENSION_ISR_BASE_COMP, getAssociation(ASSOCIATION_BASECOMPONENTS)));

    addExtension(new Extension(EXTENSION_PARENTCOMPONENTS, Constants.EXTENSION_ISR_PARENT_COMP, getAssociation(ASSOCIATION_PARENTCOMPONENTS)));
  }

  private Extension getExtensionToBusinessObject() {
    // Queries for business objects need to go over the attributable association 
    TypeWithJoinProperty firstJoin = getAssociation(ASSOCIATION_BUSINESSOBJECT_ASSOCIATIONS);

    TypeWithJoinProperty secondJoin = Isr2BoAssociationTypeQu.getInstance().getAssociation(Isr2BoAssociationTypeQu.ASSOCIATION_BUSINESSOBJECT);

    List<TypeWithJoinProperty> typesWithJoinProperties = new ArrayList<TypeWithJoinProperty>();
    typesWithJoinProperties.add(firstJoin);
    typesWithJoinProperties.add(secondJoin);

    return new Extension(EXTENSION_BUSINESSOBJECTS, Constants.EXTENSION_BO, typesWithJoinProperties);
  }

  @Override
  protected void initAssociations() {
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_PROJECTS, ProjectQueryType.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_INFORMATIONSYSTEMDOMAINS, InformationSystemDomainTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_INTERFACES_B, InformationSystemInterfaceTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_INTERFACES_A, InformationSystemInterfaceTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_TECHNICALCOMPONENTRELEASES, TechnicalComponentReleaseTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_INFRASTRUCTUREELEMENTS, InfrastructureElementTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_BUSINESSOBJECT_ASSOCIATIONS, BusinessObjectTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_SUCCESSORS, InformationSystemReleaseTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_PREDECESSORS, InformationSystemReleaseTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_CHILDREN, InformationSystemReleaseTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_PARENT, InformationSystemReleaseTypeQu.getInstance(), false));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_BUSINESSMAPPINGS, BusinessMappingTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_BUSINESSFUNCTIONS, BusinessFunctionQueryType.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_BASECOMPONENTS, InformationSystemReleaseTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_PARENTCOMPONENTS, InformationSystemReleaseTypeQu.getInstance(), true));
  }

  @Override
  protected void initRelations() {

    super.initRelations();

    addSelfRelations();

    // Relation to projects.
    addRelation(new Extension(EXTENSION_PROJECTS, Constants.BB_PROJECT_PLURAL, getAssociation(ASSOCIATION_PROJECTS)));

    // Relation to infrastructure elements.
    addRelation(new Extension(EXTENSION_INFRASTRUCTUREELEMENTS, Constants.BB_INFRASTRUCTUREELEMENT_PLURAL,
        getAssociation(ASSOCIATION_INFRASTRUCTUREELEMENTS)));

    // Relation to information system domains.
    addRelation(new Extension(EXTENSION_INFORMATIONSYSTEMDOMAIN, Constants.BB_INFORMATIONSYSTEMDOMAIN_PLURAL,
        getAssociation(ASSOCIATION_INFORMATIONSYSTEMDOMAINS)));

    // Relation to technical components.
    addRelation(new Extension(EXTENSION_TECHNICALCOMPONENTRELEASES, Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL,
        getAssociation(ASSOCIATION_TECHNICALCOMPONENTRELEASES)));
    // Relation to business functions
    addRelation(new Extension(EXTENSION_BUSINESSFUNCTIONS, Constants.BB_BUSINESSFUNCTION_PLURAL, getAssociation(ASSOCIATION_BUSINESSFUNCTIONS)));

    // Relation to business objects.
    Extension extensionToBusinessObject = getExtensionToBusinessObject();
    extensionToBusinessObject.setNameKeyForPresentation(Constants.BB_BUSINESSOBJECT_PLURAL);
    addRelation(extensionToBusinessObject);

    // Relation to business units (via business mapping).
    List<TypeWithJoinProperty> list = new ArrayList<TypeWithJoinProperty>();
    list.add(getAssociation(ASSOCIATION_BUSINESSMAPPINGS));
    list.add(BusinessMappingTypeQu.getInstance().getAssociation(BusinessMappingTypeQu.ASSOCIATION_BUSINESSUNIT));

    addRelation(new Extension(EXTENSION_BM_BUSINESSUNIT, Constants.BB_BUSINESSUNIT_PLURAL, list));

    // Relation to products (via business mapping).
    list = new ArrayList<TypeWithJoinProperty>();
    list.add(getAssociation(ASSOCIATION_BUSINESSMAPPINGS));
    list.add(BusinessMappingTypeQu.getInstance().getAssociation(BusinessMappingTypeQu.ASSOCIATION_PRODUCT));

    addRelation(new Extension(EXTENSION_BM_PRODUCT, Constants.BB_PRODUCT_PLURAL, list));

    // Relation to business processes (via business mapping).
    list = new ArrayList<TypeWithJoinProperty>();
    list.add(getAssociation(ASSOCIATION_BUSINESSMAPPINGS));
    list.add(BusinessMappingTypeQu.getInstance().getAssociation(BusinessMappingTypeQu.ASSOCIATION_BUSINESSPROCESS));

    addRelation(new Extension(EXTENSION_BM_BUSINESSPROCESS, Constants.BB_BUSINESSPROCESS_PLURAL, list));
  }

  private void addSelfRelations() {
    String prefix = "graphicalReport." + Constants.BB_INFORMATIONSYSTEMRELEASE_BASE;
    String permKey = "informationSystemRelease";
    // Predecessors
    addRelation(new Extension(EXTENSION_ISR_PREDECESSORS, prefix + "predecessors", permKey, getAssociation(ASSOCIATION_PREDECESSORS)));
    // Children
    addRelation(new Extension(EXTENSION_ISR_CHILDREN, prefix + "children", permKey, getAssociation(ASSOCIATION_CHILDREN)));
    // base Components
    addRelation(new Extension(EXTENSION_ISR_BASECOMPONENTS, prefix + "baseComponents", permKey, getAssociation(ASSOCIATION_BASECOMPONENTS)));
  }

  @Override
  public Map<String, IPresentationExtension> getExtensionsForPresentation() {
    Map<String, IPresentationExtension> extensions = new HashMap<String, IPresentationExtension>(getExtensions());
    // combine from and to connections
    Extension ex1 = (Extension) extensions.get(EXTENSION_INTERFACES_B);
    Extension ex2 = (Extension) extensions.get(EXTENSION_INTERFACES_A);
    List<Extension> extensionList = new ArrayList<Extension>();
    extensionList.add(ex1);
    extensionList.add(ex2);
    CombinedExtension cex = new CombinedExtension(PRESENTATION_EXTENSION_INTERFACES, Constants.EXTENSION_ISR_ISI, extensionList);
    extensions.put(PRESENTATION_EXTENSION_INTERFACES, cex);
    extensions.remove(EXTENSION_INTERFACES_B);
    extensions.remove(EXTENSION_INTERFACES_A);

    // combine information system release over connection extensions (only 2 out of 4 are relevant)
    ex1 = (Extension) extensions.get(EXTENSION_FROMISRELEASEOVERFROMCONNECTION);
    ex2 = (Extension) extensions.get(EXTENSION_TOISRELEASEOVERTOCONNECTION);
    extensionList = new ArrayList<Extension>();
    extensionList.add(ex1);
    extensionList.add(ex2);
    cex = new CombinedExtension(PRESENTATION_EXTENSION_IPURELEASEOVERCONNECTION, Constants.EXTENSION_ISR_ISR_VIA_ISI, extensionList);
    extensions.put(PRESENTATION_EXTENSION_IPURELEASEOVERCONNECTION, cex);
    extensions.remove(EXTENSION_FROMISRELEASEOVERFROMCONNECTION);
    extensions.remove(EXTENSION_TOISRELEASEOVERTOCONNECTION);

    // see #61:
    // combine business objects over from and to connection extensions:
    ex1 = (Extension) extensions.get(EXTENSION_BUSINESSOBJECT_VIA_INTERFACES_A);
    ex2 = (Extension) extensions.get(EXTENSION_BUSINESSOBJECS_VIA_INTERFACES_B);
    extensionList = new ArrayList<Extension>();
    extensionList.add(ex1);
    extensionList.add(ex2);
    cex = new CombinedExtension(PRESENTATION_EXTENSION_BUSINESSOBJECTOVERCONNECTION, Constants.EXTENSION_ISR_BO_VIA_ISI, extensionList);
    extensions.put(PRESENTATION_EXTENSION_BUSINESSOBJECTOVERCONNECTION, cex);
    extensions.remove(EXTENSION_BUSINESSOBJECT_VIA_INTERFACES_A);
    extensions.remove(EXTENSION_BUSINESSOBJECS_VIA_INTERFACES_B);

    return extensions;
  }

  public String getStartDateProperty() {
    return PROPERTY_STARTDATE;
  }

  public String getEndDateProperty() {
    return PROPERTY_ENDDATE;
  }

  public String getTypeOfStatusProperty() {
    return PROPERTY_TYPEOFSTATUS;
  }

  @SuppressWarnings("boxing")
  @Override
  protected void initPostprocessingStrategies() {
    addPostProcessingStrategy(new IncludeConnectedInformationSystemReleasesStrategy(0));
    addPostProcessingStrategy(new HideSubInformationSystemReleasesStrategy(1));
    addPostProcessingStrategy(new HideSubInformationSystemReleasesWithConnectionMergingStrategy(2));
  }

  @SuppressWarnings("PMD.NoSpringFactory")
  public List<BuildingBlockHierarchy<InformationSystemRelease>> getHierarchies() {
    final InformationSystemReleaseService informationSystemReleaseService = SpringServiceFactory.getInformationSystemReleaseService();
    BuildingBlockHierarchy<InformationSystemRelease> hierarchy = new GetHierarchiesBuildingBlockHierarchy(informationSystemReleaseService);
    return Collections.singletonList(hierarchy);
  }


  private void addExtensionToBusinessMappings() {

    // Add second step extensions which represents those extensions which are connected via a
    // business mapping. Avoid check for permission here as the extensions are initialised for all
    // users.
    List<Extension> secondStepExtensions = new ArrayList<Extension>();

    secondStepExtensions.add(BusinessMappingTypeQu.getInstance().getExtensionWithoutPermissionCheck(BusinessMappingTypeQu.EXTENSION_BUSINESSPROCESS));
    secondStepExtensions.add(BusinessMappingTypeQu.getInstance().getExtensionWithoutPermissionCheck(BusinessMappingTypeQu.EXTENSION_BUSINESSUNIT));
    secondStepExtensions.add(BusinessMappingTypeQu.getInstance().getExtensionWithoutPermissionCheck(BusinessMappingTypeQu.EXTENSION_PRODUCT));
    addExtension(new Extension(EXTENSION_BUSINESSMAPPINGS, Constants.EXTENSION_BM, getAssociation(ASSOCIATION_BUSINESSMAPPINGS), secondStepExtensions));
  }

  public QueryType getQueryType() {
    return getInstance();
  }

  private static final class GetHierarchiesBuildingBlockHierarchy implements BuildingBlockHierarchy<InformationSystemRelease> {
    private final InformationSystemReleaseService informationSystemReleaseService;

    public GetHierarchiesBuildingBlockHierarchy(InformationSystemReleaseService informationSystemReleaseService) {
      this.informationSystemReleaseService = informationSystemReleaseService;
    }

    public List<InformationSystemRelease> getChildren(InformationSystemRelease element) {
      return element.getChildrenAsList();
    }

    public InformationSystemRelease getParent(InformationSystemRelease element) {
      return element.getParent();
    }

    public List<InformationSystemRelease> getToplevelElements() {
      return informationSystemReleaseService.getOutermostInformationSystemReleases();
    }
  }

}