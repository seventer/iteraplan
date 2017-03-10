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
import java.util.List;

import org.apache.commons.lang.ClassUtils;

import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.RemoveTopLevelElementStrategy;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.InfrastructureElement;


/**
 * Extends the class {@link InfrastructureElementType} with functionality for textual queries.
 */
public final class InfrastructureElementTypeQu extends InfrastructureElementType implements QueryType, HierarchicalType<InfrastructureElement> {

  /** Serialization version. */
  private static final long                        serialVersionUID                     = 4183138808685475566L;

  private static final InfrastructureElementTypeQu INSTANCE                             = new InfrastructureElementTypeQu();

  public static final String                       EXTENSION_INFORMATIONSYSTEMRELEASES  = "ciE1";
  public static final String                       EXTENSION_PARENT                     = "ciE2";
  public static final String                       EXTENSION_CHILDREN                   = "ciE3";
  public static final String                       EXTENSION_TECHNICALCOMPONENTRELEASES = "ciE4";
  public static final String                       EXTENSION_BASECOMPONENTS             = "ciE5_baseComponents";
  public static final String                       EXTENSION_PARENTCOMPONENTS           = "ciE6_parentComponents";

  private InfrastructureElementTypeQu() {
    super(ClassUtils.getShortClassName(InfrastructureElement.class), "ci");
  }

  public static InfrastructureElementTypeQu getInstance() {
    return INSTANCE;
  }

  @Override
  protected void initExtensions() {
    addExtension(new Extension(EXTENSION_INFORMATIONSYSTEMRELEASES, Constants.EXTENSION_ISR, getAssociation(ASSOCIATION_INFORMATIONSYSTEMRELEASES)));
    addExtensionToTechnicalComponentReleases();
    addExtension(new Extension(EXTENSION_PARENT, Constants.EXTENSION_IE_PARENT_KEY, getAssociation(ASSOCIATION_PARENT)));
    addExtension(new Extension(EXTENSION_CHILDREN, Constants.EXTENSION_IE_CHILDREN_KEY, getAssociation(ASSOCIATION_CHILDREN)));
    addExtension(new Extension(EXTENSION_BASECOMPONENTS, Constants.EXTENSION_IE_BASE_COMPONENTS, getAssociation(ASSOCIATION_BASECOMPONENTS)));
    addExtension(new Extension(EXTENSION_PARENTCOMPONENTS, Constants.EXTENSION_IE_PARENT_COMPONENTS, getAssociation(ASSOCIATION_PARENTCOMPONENTS)));
  }

  private void addExtensionToTechnicalComponentReleases() {
    // Queries for technical components need to go over the attributable association 
    TypeWithJoinProperty firstJoin = getAssociation(ASSOCIATION_TECHNICALCOMPONENTRELEASE_ASSOCIATIONS);

    TypeWithJoinProperty secondJoin = Tcr2IeAssociationTypeQu.getInstance().getAssociation(
        Tcr2IeAssociationTypeQu.ASSOCIATION_TECHNICALCOMPONENTRELEASE);

    List<TypeWithJoinProperty> typesWithJoinProperties = new ArrayList<TypeWithJoinProperty>();
    typesWithJoinProperties.add(firstJoin);
    typesWithJoinProperties.add(secondJoin);

    addExtension(new Extension(EXTENSION_TECHNICALCOMPONENTRELEASES, Constants.EXTENSION_TCR, typesWithJoinProperties));
  }

  @Override
  protected void initAssociations() {
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_INFORMATIONSYSTEMRELEASES, InformationSystemReleaseTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_TECHNICALCOMPONENTRELEASE_ASSOCIATIONS, TechnicalComponentReleaseTypeQu.getInstance(), true));
    addAssociation((new TypeWithJoinProperty(ASSOCIATION_PARENT, InfrastructureElementTypeQu.getInstance(), false)));
    addAssociation((new TypeWithJoinProperty(ASSOCIATION_CHILDREN, InfrastructureElementTypeQu.getInstance(), true)));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_BASECOMPONENTS, InfrastructureElementTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_PARENTCOMPONENTS, InfrastructureElementTypeQu.getInstance(), true));
  }

  @SuppressWarnings("PMD.NoSpringFactory")
  public List<BuildingBlockHierarchy<InfrastructureElement>> getHierarchies() {
    BuildingBlockHierarchy<InfrastructureElement> hierarchy = new BuildingBlockHierarchyImpl<InfrastructureElement>(
        SpringServiceFactory.getInfrastructureElementService());
    return Collections.singletonList(hierarchy);
  }

  @Override
  public boolean isOrderedHierarchy() {
    return true;
  }

  @Override
  public AbstractPostprocessingStrategy<InfrastructureElement> getOrderedHierarchyRemoveRootElementStrategy() {
    return new RemoveTopLevelElementStrategy<InfrastructureElement>();
  }

  public QueryType getQueryType() {
    return getInstance();
  }

}