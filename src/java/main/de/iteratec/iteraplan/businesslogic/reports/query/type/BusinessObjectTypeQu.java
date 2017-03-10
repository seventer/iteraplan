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
import java.util.List;

import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.RemoveTopLevelElementStrategy;
import de.iteratec.iteraplan.businesslogic.service.BusinessObjectService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.BusinessObject;


/**
 * Extends the class {@link BusinessObjectType} with functionality for textual queries.
 */
public final class BusinessObjectTypeQu extends BusinessObjectType implements QueryType, HierarchicalType<BusinessObject> {

  /** Serialization version. */
  private static final long                 serialVersionUID                    = -235458017594629651L;

  private static final BusinessObjectTypeQu INSTANCE                            = new BusinessObjectTypeQu();

  public static final String                EXTENSION_INFORMATIONSYSTEMRELEASES = "boE1";
  public static final String                EXTENSION_BUSINESSFUNCTION          = "boE2";
  public static final String                EXTENSION_BUSINESSDOMAIN            = "boE3";
  public static final String                EXTENSION_PARENT                    = "boE4";
  public static final String                EXTENSION_CHILDREN                  = "boE5";
  public static final String                EXTENSION_GENERALISATION            = "boE6";
  public static final String                EXTENSION_SPECIALISATIONS           = "boE7";
  public static final String                EXTENSION_ATTRIBUTES_IS2BO          = "boE8";

  public static final String                EXTENSION_PARENT_KEY                = "reporting.extension.businessObject.parent";
  public static final String                EXTENSION_CHILDREN_KEY              = "reporting.extension.businessObject.children";
  public static final String                EXTENSION_GENERALISATION_KEY        = "reporting.extension.businessObject.generalisation";
  public static final String                EXTENSION_SPECIALISATIONS_KEY       = "reporting.extension.businessObject.specialisations";

  /**
   * Constructor.
   */
  private BusinessObjectTypeQu() {
    super(BusinessObject.class.getSimpleName(), "bo");
  }

  public static BusinessObjectTypeQu getInstance() {
    return INSTANCE;
  }

  @Override
  protected void initExtensions() {

    addExtensionToInformationSystemReleases();

    addExtension(new Extension(EXTENSION_BUSINESSFUNCTION, Constants.EXTENSION_BUSINESSFUNCTION, getAssociation(ASSOCIATION_BUSINESSFUNCTION)));

    addExtension(new Extension(EXTENSION_BUSINESSDOMAIN, Constants.EXTENSION_BD, getAssociation(ASSOCIATION_BUSINESSDOMAIN)));

    addExtension(new Extension(EXTENSION_PARENT, EXTENSION_PARENT_KEY, getAssociation(ASSOCIATION_PARENT)));

    addExtension(new Extension(EXTENSION_CHILDREN, EXTENSION_CHILDREN_KEY, getAssociation(ASSOCIATION_CHILDREN)));

    addExtension(new Extension(EXTENSION_GENERALISATION, EXTENSION_GENERALISATION_KEY, getAssociation(ASSOCIATION_GENERALISATION)));

    addExtension(new Extension(EXTENSION_SPECIALISATIONS, EXTENSION_SPECIALISATIONS_KEY, getAssociation(ASSOCIATION_SPECIALISATION)));

    addExtension(new Extension(EXTENSION_ATTRIBUTES_IS2BO, Constants.EXTENSION_ISR2BOASSOCIATION, getAssociation(ASSOCIATION_ATTRIBUTES_IS2BO)));
  }

  private void addExtensionToInformationSystemReleases() {
    // Queries for technical components need to go over the attributable association 
    TypeWithJoinProperty firstJoin = getAssociation(ASSOCIATION_INFORMATIONSYSTEMRELEASE_ASSOCIATIONS);

    TypeWithJoinProperty secondJoin = Isr2BoAssociationTypeQu.getInstance().getAssociation(
        Isr2BoAssociationTypeQu.ASSOCIATION_INFORMATIONSYSTEMRELEASE);

    List<TypeWithJoinProperty> typesWithJoinProperties = new ArrayList<TypeWithJoinProperty>();
    typesWithJoinProperties.add(firstJoin);
    typesWithJoinProperties.add(secondJoin);

    addExtension(new Extension(EXTENSION_INFORMATIONSYSTEMRELEASES, Constants.EXTENSION_ISR, typesWithJoinProperties));
  }

  @Override
  protected void initAssociations() {

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_INFORMATIONSYSTEMRELEASE_ASSOCIATIONS, InformationSystemReleaseTypeQu.getInstance(), true));

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_BUSINESSFUNCTION, BusinessFunctionQueryType.getInstance(), true));

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_BUSINESSDOMAIN, BusinessDomainQueryType.getInstance(), true));

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_PARENT, BusinessObjectTypeQu.getInstance(), false));

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_CHILDREN, BusinessObjectTypeQu.getInstance(), true));

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_GENERALISATION, BusinessObjectTypeQu.getInstance(), false));

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_SPECIALISATION, BusinessObjectTypeQu.getInstance(), true));

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_ATTRIBUTES_IS2BO, Isr2BoAssociationTypeQu.getInstance(), true));
  }

  @SuppressWarnings("PMD.NoSpringFactory")
  public List<BuildingBlockHierarchy<BusinessObject>> getHierarchies() {
    final BusinessObjectService businessObjectService = SpringServiceFactory.getBusinessObjectService();

    BuildingBlockHierarchy<BusinessObject> hierarchy = new BuildingBlockHierarchyImpl<BusinessObject>(businessObjectService);

    BuildingBlockHierarchy<BusinessObject> generalizationHierarchy = new GetHierarchiesBuildingBlockHierarchy(businessObjectService);

    List<BuildingBlockHierarchy<BusinessObject>> res = new ArrayList<BuildingBlockHierarchy<BusinessObject>>();
    res.add(hierarchy);
    res.add(generalizationHierarchy);
    return res;
  }

  @Override
  public boolean isOrderedHierarchy() {
    return true;
  }

  @Override
  public AbstractPostprocessingStrategy<BusinessObject> getOrderedHierarchyRemoveRootElementStrategy() {
    return new RemoveTopLevelElementStrategy<BusinessObject>();
  }

  public QueryType getQueryType() {
    return getInstance();
  }

  private static final class GetHierarchiesBuildingBlockHierarchy implements BuildingBlockHierarchy<BusinessObject> {
    private final BusinessObjectService businessObjectService;

    public GetHierarchiesBuildingBlockHierarchy(BusinessObjectService businessObjectService) {
      this.businessObjectService = businessObjectService;
    }

    public List<BusinessObject> getChildren(BusinessObject element) {
      return new ArrayList<BusinessObject>(element.getSpecialisations());
    }

    public BusinessObject getParent(BusinessObject element) {
      return element.getGeneralisation();
    }

    public List<BusinessObject> getToplevelElements() {
      return businessObjectService.getBusinessObjectsWithoutGeneralisation();
    }
  }

}