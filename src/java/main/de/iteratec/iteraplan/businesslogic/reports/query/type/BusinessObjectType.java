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
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * The meta model describing the type {@link BusinessObject}.
 */
public abstract class BusinessObjectType extends Type<BusinessObject> {

  private static final long  serialVersionUID                                  = 5102790346599663979L;
  public static final String PROPERTY_NAME                                     = "name";
  public static final String PROPERTY_DESCRIPTION                              = "description";

  public static final String ASSOCIATION_PARENT                                = "parent";
  public static final String ASSOCIATION_CHILDREN                              = "children";

  public static final String ASSOCIATION_GENERALISATION                        = "generalisation";
  public static final String ASSOCIATION_SPECIALISATION                        = "specialisations";

  public static final String ASSOCIATION_BUSINESSFUNCTION                      = "businessFunctions";
  public static final String ASSOCIATION_BUSINESSDOMAIN                        = "businessDomains";
  public static final String ASSOCIATION_INFORMATIONSYSTEMRELEASE_ASSOCIATIONS = "informationSystemReleaseAssociations";

  public static final String ASSOCIATION_ATTRIBUTES_IS2BO                      = "informationSystemReleaseAssociations";

  BusinessObjectType(String typeNameDB, String typeNameDBShort) {
    super(typeNameDB, typeNameDBShort, Constants.BB_BUSINESSOBJECT, Constants.BB_BUSINESSOBJECT_PLURAL);
  }

  BusinessObjectType() {
    super(Constants.BB_BUSINESSOBJECT, Constants.BB_BUSINESSOBJECT_PLURAL);
  }

  /** {@inheritDoc} */
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.BUSINESSOBJECT;
  }

  /** {@inheritDoc} */
  protected void initProperties() {
    addProperty(new Property(PROPERTY_NAME, Constants.ATTRIBUTE_NAME, 0));
    addProperty(new Property(PROPERTY_DESCRIPTION, Constants.ATTRIBUTE_DESCRIPTION, 1));
  }

  /** {@inheritDoc} */
  void initPostprocessingStrategies() {
    // nothing  to do here
  }

}
