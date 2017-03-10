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

import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.RemoveTopLevelElementStrategy;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.BusinessUnit;


/**
 * Extends the class {@code BusinessUnitType} with functionality concerning textual queries.
 */
public final class BusinessUnitQueryType extends BusinessUnitType implements QueryType, HierarchicalType<BusinessUnit> {

  /** Serialization version. */
  private static final long                  serialVersionUID             = 1551549189580207174L;

  private static final BusinessUnitQueryType INSTANCE                     = new BusinessUnitQueryType();

  private static final String                EXTENSION_PARENT             = "ouE1";
  private static final String                EXTENSION_CHILDREN           = "ouE2";
  private static final String                EXTENSION_BUSINESSDOMAIN     = "ouE3";
  public static final String                 EXTENSION_INFORMATIONSYSTEMS = "ouE4";

  public static final String                 EXTENSION_PARENT_KEY         = "reporting.extension.businessUnit.parent";
  private static final String                EXTENSION_CHILDREN_KEY       = "reporting.extension.businessUnit.children";

  private BusinessUnitQueryType() {
    super(BusinessUnit.class.getSimpleName(), "ou");
  }

  public static BusinessUnitQueryType getInstance() {
    return INSTANCE;
  }

  @Override
  protected void initExtensions() {

    // Queries for information systems are performed indirectly through the association to business mappings
    TypeWithJoinProperty firstJoin = getAssociation(ASSOCIATION_BUSINESSMAPPINGS);
    TypeWithJoinProperty secondJoin = BusinessMappingTypeQu.getInstance().getAssociation(BusinessMappingTypeQu.ASSOCIATION_INFORMATIONSYSTEM);

    List<TypeWithJoinProperty> typesWithJoinProperties = new ArrayList<TypeWithJoinProperty>();
    typesWithJoinProperties.add(firstJoin);
    typesWithJoinProperties.add(secondJoin);

    addExtension(new Extension(EXTENSION_PARENT, EXTENSION_PARENT_KEY, getAssociation(ASSOCIATION_PARENT)));

    addExtension(new Extension(EXTENSION_CHILDREN, EXTENSION_CHILDREN_KEY, getAssociation(ASSOCIATION_CHILDREN)));

    addExtension(new Extension(EXTENSION_BUSINESSDOMAIN, Constants.EXTENSION_BD, getAssociation(ASSOCIATION_BUSINESSDOMAINS)));

    addExtension(new Extension(EXTENSION_INFORMATIONSYSTEMS, Constants.EXTENSION_ISR, typesWithJoinProperties));

  }

  @Override
  protected void initAssociations() {

    addAssociation((new TypeWithJoinProperty(ASSOCIATION_CHILDREN, BusinessUnitQueryType.getInstance(), true)));

    addAssociation((new TypeWithJoinProperty(ASSOCIATION_PARENT, BusinessUnitQueryType.getInstance(), false)));

    addAssociation((new TypeWithJoinProperty(ASSOCIATION_BUSINESSDOMAINS, BusinessDomainQueryType.getInstance(), true)));

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_BUSINESSMAPPINGS, BusinessMappingTypeQu.getInstance(), true));

  }

  @SuppressWarnings("PMD.NoSpringFactory")
  public List<BuildingBlockHierarchy<BusinessUnit>> getHierarchies() {
    BuildingBlockHierarchy<BusinessUnit> hierarchy = new BuildingBlockHierarchyImpl<BusinessUnit>(SpringServiceFactory.getBusinessUnitService());
    return Collections.singletonList(hierarchy);
  }

  @Override
  public boolean isOrderedHierarchy() {
    return true;
  }

  @Override
  public AbstractPostprocessingStrategy<BusinessUnit> getOrderedHierarchyRemoveRootElementStrategy() {
    return new RemoveTopLevelElementStrategy<BusinessUnit>();
  }

  public QueryType getQueryType() {
    return getInstance();
  }

}