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

import java.util.Map;

import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public final class QueryTypeHelper {

  private static final Map<TypeOfBuildingBlock, QueryType> TOBB_TO_QUERY_TYPE_MAP = CollectionUtils.hashMap();

  /* initialize tobToQueryTypeMap */
  static {
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, ArchitecturalDomainTypeQu.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.BUSINESSDOMAIN, BusinessDomainQueryType.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.BUSINESSFUNCTION, BusinessFunctionQueryType.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.BUSINESSMAPPING, BusinessMappingTypeQu.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.BUSINESSOBJECT, BusinessObjectTypeQu.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.BUSINESSPROCESS, BusinessProcessTypeQ.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.BUSINESSUNIT, BusinessUnitQueryType.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.BUSINESSMAPPING, BusinessMappingTypeQu.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.INFORMATIONSYSTEM, InformationSystemReleaseTypeQu.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, InformationSystemDomainTypeQu.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, InformationSystemInterfaceTypeQu.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, InformationSystemReleaseTypeQu.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, InfrastructureElementTypeQu.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.PRODUCT, ProductQueryType.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.PROJECT, ProjectQueryType.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.TECHNICALCOMPONENT, TechnicalComponentReleaseTypeQu.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, TechnicalComponentReleaseTypeQu.getInstance());
    TOBB_TO_QUERY_TYPE_MAP.put(TypeOfBuildingBlock.TRANSPORT, TransportQueryType.getInstance());
  }

  private QueryTypeHelper() {
    // just a static helper class
  }

  /**
   * Returns the query type object that corresponds to the passed TypeOfBuildingBlock 
   * @param tob a building block type enumeration value
   * @return the corresponding QueryType object
   */
  public static QueryType getQueryType(TypeOfBuildingBlock tob) {
    return TOBB_TO_QUERY_TYPE_MAP.get(tob);
  }

  /**
   * As {@link #getQueryType(TypeOfBuildingBlock)}, returns the query type object that corresponds to the passed TypeOfBuildingBlock.
   * Additionally, it performs an upcast to {@link Type}, to save the caller this casting. 
   * @param tob a building block type enumeration value
   * @return the corresponding QueryType object, being of type {@link Type}
   */
  public static Type<?> getTypeObject(TypeOfBuildingBlock tob) {
    return (Type<?>) TOBB_TO_QUERY_TYPE_MAP.get(tob);
  }
}
