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
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public final class TransportQueryType extends TransportType implements QueryType {

  /** Serialization version. */
  private static final long               serialVersionUID         = 6788625564851635255L;

  private static final TransportQueryType INSTANCE                 = new TransportQueryType();

  public static final String              EXTENSION_BUSINESSOBJECT = "trE1";

  /**
   * @param typeName
   * @param typeNameShort
   */
  private TransportQueryType() {
    super(Transport.class.getSimpleName(), "tr", Constants.BB_BUSINESSOBJECT, Constants.BB_BUSINESSOBJECT_PLURAL);
  }

  public static TransportQueryType getInstance() {
    return INSTANCE;
  }

  /* (non-Javadoc)
   * @see de.iteratec.iteraplan.businesslogic.reports.query.type.Type#getTypeOfBuildingBlock()
   */
  @Override
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.TRANSPORT;
  }

  @Override
  protected void initExtensions() {
    addExtension(new Extension(EXTENSION_BUSINESSOBJECT, Constants.EXTENSION_BO, getAssociation(ASSOCIATION_BUSINESSOBJECT)));
  }

  @Override
  protected void initAssociations() {
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_BUSINESSOBJECT, BusinessObjectTypeQu.getInstance(), false));
  }

  @Override
  public boolean isAssociationType() {
    return true;
  }

  @Override
  public boolean isAnnotatedWithAttributesOrProperties() {
    return true;
  }

  public QueryType getQueryType() {
    return getInstance();
  }
}
