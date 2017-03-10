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

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * The meta model describing the type {@link InformationSystemRelease}.
 */
public abstract class InformationSystemReleaseType extends Type<InformationSystemRelease> {

  private static final long  serialVersionUID                        = -7196043243182030141L;
  public static final String PROPERTY_NAME                           = "informationSystem.name";
  public static final String PROPERTY_DESCRIPTION                    = "description";
  public static final String PROPERTY_VERSION                        = "version";
  public static final String PROPERTY_STARTDATE                      = "runtimePeriod.start";
  public static final String PROPERTY_ENDDATE                        = "runtimePeriod.end";
  public static final String PROPERTY_TYPEOFSTATUS                   = "typeOfStatus";

  public static final String ASSOCIATION_PARENT                      = "parent";
  public static final String ASSOCIATION_CHILDREN                    = "children";

  public static final String ASSOCIATION_PREDECESSORS                = "predecessors";
  public static final String ASSOCIATION_SUCCESSORS                  = "successors";
  public static final String ASSOCIATION_BASECOMPONENTS              = "baseComponents";
  public static final String ASSOCIATION_PARENTCOMPONENTS            = "parentComponents";

  public static final String ASSOCIATION_BUSINESSFUNCTIONS           = "businessFunctions";
  public static final String ASSOCIATION_BUSINESSOBJECT_ASSOCIATIONS = "businessObjectAssociations";
  public static final String ASSOCIATION_BUSINESSMAPPINGS            = "businessMappings";
  public static final String ASSOCIATION_INFORMATIONSYSTEMDOMAINS    = "informationSystemDomains";
  public static final String ASSOCIATION_INFRASTRUCTUREELEMENTS      = "infrastructureElements";
  public static final String ASSOCIATION_INTERFACES_B                = "interfacesReleaseB";
  public static final String ASSOCIATION_INTERFACES_A                = "interfacesReleaseA";
  public static final String ASSOCIATION_PROJECTS                    = "projects";
  public static final String ASSOCIATION_TECHNICALCOMPONENTRELEASES  = "technicalComponentReleases";

  InformationSystemReleaseType(String typeNameDB, String typeNameDBShort) {
    super(typeNameDB, typeNameDBShort, Constants.BB_INFORMATIONSYSTEMRELEASE, Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL);
  }

  InformationSystemReleaseType() {
    super(Constants.BB_INFORMATIONSYSTEMRELEASE, Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL);
  }

  @Override
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isHasStatus() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isHasTimespan() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isHasSeal() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public String getTimespanPresentationKey() {
    return Constants.TIMESPAN_PRODUCTIVE;
  }

}