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

import de.iteratec.iteraplan.businesslogic.reports.query.type.SimpleAssociation.AssociationCardinality;
import de.iteratec.iteraplan.common.Constants;


/**
 * Extends the class {@link InformationSystemReleaseType} with functionality for mass updates.
 */
public class InformationSystemReleaseTypeMu extends InformationSystemReleaseType implements MassUpdateType {

  /** Serialization version. */
  private static final long                           serialVersionUID  = -209246280253844507L;

  public static final String                          PROPERTY_TIMESPAN = "timespan";

  private static final InformationSystemReleaseTypeMu INSTANCE          = new InformationSystemReleaseTypeMu();

  public static MassUpdateType getInstance() {
    return INSTANCE;
  }

  @Override
  protected void initProperties() {
    addProperty(new Property(PROPERTY_NAME, Constants.MU_NAME_VERSION, 0));
    addProperty(new Property(PROPERTY_DESCRIPTION, Constants.MU_DESCRIPTION, 1));
    addProperty(new Property(PROPERTY_TYPEOFSTATUS, Constants.MU_TYPE_OF_STATUS, 2));
    addProperty(new Property(PROPERTY_TIMESPAN, Constants.MU_TIMESPAN, 3));
  }

  @Override
  protected void initAssociations() {

    addMassUpdateAssociation(new SimpleAssociation(ASSOCIATION_INFORMATIONSYSTEMDOMAINS, "informationSystemRelease.to.informationSystemDomains",
        AssociationCardinality.TO_MANY_SET, 1));

    addMassUpdateAssociation(new SimpleAssociation(ASSOCIATION_TECHNICALCOMPONENTRELEASES, "informationSystemRelease.to.technicalComponentReleases",
        AssociationCardinality.TO_MANY_SET, 2));

    addMassUpdateAssociation(new SimpleAssociation(ASSOCIATION_INFRASTRUCTUREELEMENTS, "informationSystemRelease.to.infrastructureElements",
        AssociationCardinality.TO_MANY_SET, 3));

    addMassUpdateAssociation(new SimpleAssociation(ASSOCIATION_PROJECTS, "informationSystemRelease.to.projects", AssociationCardinality.TO_MANY_SET,
        4));

    addMassUpdateAssociation(new SimpleAssociation(ASSOCIATION_BUSINESSOBJECT_ASSOCIATIONS, "informationSystemRelease.to.businessObjects",
        AssociationCardinality.TO_MANY_SET, 5));

    addMassUpdateAssociation(new SimpleAssociation(ASSOCIATION_BUSINESSFUNCTIONS, "informationSystemRelease.to.businessFunctions",
        AssociationCardinality.TO_MANY_SET, 6));

    addMassUpdateAssociation(new SimpleAssociation(ASSOCIATION_BASECOMPONENTS, "informationSystemRelease.baseComponents",
        AssociationCardinality.TO_MANY_SET, 7));

    addMassUpdateAssociation(new SimpleAssociation(ASSOCIATION_PARENTCOMPONENTS, "informationSystemRelease.parentComponents",
        AssociationCardinality.TO_MANY_SET, 8));

    addMassUpdateAssociation(new SimpleAssociation(ASSOCIATION_PREDECESSORS, "informationSystemRelease.predecessors",
        AssociationCardinality.TO_MANY_SET, 9));

    addMassUpdateAssociation(new SimpleAssociation(ASSOCIATION_SUCCESSORS, "informationSystemRelease.successors", AssociationCardinality.TO_MANY_SET,
        10));

    addMassUpdateAssociation(new SimpleAssociation(ASSOCIATION_PARENT, "informationSystemRelease.parent", AssociationCardinality.TO_ONE, 11));

  }

  @Override
  void initPostprocessingStrategies() {
    // Nothing  to do.
  }

}