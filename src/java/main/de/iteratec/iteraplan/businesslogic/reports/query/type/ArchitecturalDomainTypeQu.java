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

import java.util.Collections;
import java.util.List;

import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.RemoveTopLevelElementStrategy;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.ArchitecturalDomain;


/**
 * Extends the class {@link ArchitecturalDomainType} with functionality for textual queries.
 */
public final class ArchitecturalDomainTypeQu extends ArchitecturalDomainType implements QueryType, HierarchicalType<ArchitecturalDomain> {

  /** Serialization version. */
  private static final long                      serialVersionUID                    = -9101131239770088815L;

  private static final ArchitecturalDomainTypeQu INSTANCE                            = new ArchitecturalDomainTypeQu();

  public static final String                     EXTENSION_TECHNICALCOMPONENTRELEASE = "adomE1";
  public static final String                     EXTENSION_PARENT                    = "adomE2";
  public static final String                     EXTENSION_CHILDREN                  = "adomE3";

  public static final String                     EXTENSION_PARENT_KEY                = "reporting.extension.architecturalDomain.parent";
  public static final String                     EXTENSION_CHILDREN_KEY              = "reporting.extension.architecturalDomain.children";

  private ArchitecturalDomainTypeQu() {
    super(ArchitecturalDomain.class.getSimpleName(), "ad");
  }

  public static ArchitecturalDomainTypeQu getInstance() {
    return INSTANCE;
  }

  @Override
  protected void initExtensions() {

    addExtension(new Extension(EXTENSION_TECHNICALCOMPONENTRELEASE, Constants.EXTENSION_TCR, getAssociation(ASSOCIATION_TECHNICALCOMPONENTRELEASES)));

    addExtension(new Extension(EXTENSION_PARENT, EXTENSION_PARENT_KEY, getAssociation(ASSOCIATION_PARENT)));

    addExtension(new Extension(EXTENSION_CHILDREN, EXTENSION_CHILDREN_KEY, getAssociation(ASSOCIATION_CHILDREN)));
  }

  @Override
  protected void initAssociations() {

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_TECHNICALCOMPONENTRELEASES, TechnicalComponentReleaseTypeQu.getInstance(), true));

    addAssociation((new TypeWithJoinProperty(ASSOCIATION_PARENT, ArchitecturalDomainTypeQu.getInstance(), false)));

    addAssociation((new TypeWithJoinProperty(ASSOCIATION_CHILDREN, ArchitecturalDomainTypeQu.getInstance(), true)));
  }

  @SuppressWarnings("PMD.NoSpringFactory")
  public List<BuildingBlockHierarchy<ArchitecturalDomain>> getHierarchies() {
    BuildingBlockHierarchy<ArchitecturalDomain> hierarchy = new BuildingBlockHierarchyImpl<ArchitecturalDomain>(
        SpringServiceFactory.getArchitecturalDomainService());
    return Collections.singletonList(hierarchy);
  }

  @Override
  public boolean isOrderedHierarchy() {
    return true;
  }

  @Override
  public AbstractPostprocessingStrategy<ArchitecturalDomain> getOrderedHierarchyRemoveRootElementStrategy() {
    return new RemoveTopLevelElementStrategy<ArchitecturalDomain>();
  }

  public QueryType getQueryType() {
    return getInstance();
  }
}