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
package de.iteratec.iteraplan.businesslogic.reports.interchange;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public enum InterchangeDestination {

  GRAPHIC_INFORMATION_FLOW("graphicalExport.informationFlowDiagram", CollectionUtils.hashSet(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE), Sets
      .newHashSet(InterchangeOrigin.ITERAQL)), //
  GRAPHIC_IS_LANDSCAPE_CONTENT(InterchangeBean.IS_LANDSCAPE_CONTENT, Sets.newHashSet(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE), Sets
      .newHashSet(InterchangeOrigin.ITERAQL)), //
  GRAPHIC_IS_LANDSCAPE_X_AXIS(InterchangeBean.IS_LANDSCAPE_X_AXIS, Sets.newHashSet(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE,
      TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, TypeOfBuildingBlock.BUSINESSOBJECT, TypeOfBuildingBlock.BUSINESSPROCESS,
      TypeOfBuildingBlock.BUSINESSUNIT, TypeOfBuildingBlock.BUSINESSFUNCTION, TypeOfBuildingBlock.PRODUCT,
      TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, TypeOfBuildingBlock.PROJECT), Sets.newHashSet(InterchangeOrigin.ITERAQL)), //
  GRAPHIC_IS_LANDSCAPE_Y_AXIS(InterchangeBean.IS_LANDSCAPE_Y_AXIS, Sets.newHashSet(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE,
      TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, TypeOfBuildingBlock.BUSINESSOBJECT, TypeOfBuildingBlock.BUSINESSPROCESS,
      TypeOfBuildingBlock.BUSINESSUNIT, TypeOfBuildingBlock.BUSINESSFUNCTION, TypeOfBuildingBlock.PRODUCT,
      TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, TypeOfBuildingBlock.PROJECT), Sets.newHashSet(InterchangeOrigin.ITERAQL)), //
  GRAPHIC_TC_LANDSCAPE_CONTENT(InterchangeBean.TC_LANDSCAPE_CONTENT, Sets.newHashSet(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE), Sets
      .newHashSet(InterchangeOrigin.ITERAQL)), //
  GRAPHIC_TC_LANDSCAPE_X_AXIS(InterchangeBean.TC_LANDSCAPE_X_AXIS, Sets.newHashSet(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE,
      TypeOfBuildingBlock.ARCHITECTURALDOMAIN, TypeOfBuildingBlock.INFRASTRUCTUREELEMENT), Sets.newHashSet(InterchangeOrigin.ITERAQL)), //
  GRAPHIC_TC_LANDSCAPE_Y_AXIS(InterchangeBean.TC_LANDSCAPE_Y_AXIS, Sets.newHashSet(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE,
      TypeOfBuildingBlock.ARCHITECTURALDOMAIN, TypeOfBuildingBlock.INFRASTRUCTUREELEMENT), Sets.newHashSet(InterchangeOrigin.ITERAQL)), //
  GRAPHIC_CLUSTER("graphicalExport.clusterDiagram", Sets.newHashSet(TypeOfBuildingBlock.DISPLAY), Sets.newHashSet(InterchangeOrigin.ITERAQL)), //
  GRAPHIC_PORTFOLIO("graphicalExport.portfolioDiagram", Sets.newHashSet(TypeOfBuildingBlock.DISPLAY), Sets.newHashSet(InterchangeOrigin.ITERAQL)), //
  GRAPHIC_MASTERPLAN("graphicalExport.masterplanDiagram", Sets.newHashSet(TypeOfBuildingBlock.DISPLAY), Sets.newHashSet(InterchangeOrigin.ITERAQL)), //
  PIE_BAR("graphicalExport.pieBarDiagram", Sets.newHashSet(TypeOfBuildingBlock.DISPLAY), Sets.newHashSet(InterchangeOrigin.ITERAQL)), //
  MASSUPDATE("global.mass_updates", Sets.newHashSet(TypeOfBuildingBlock.DISPLAY), Sets.newHashSet(InterchangeOrigin.ITERAQL)), //
  TABULAR_REPORTS("global.report.text", Sets.newHashSet(TypeOfBuildingBlock.DISPLAY), Sets.newHashSet(InterchangeOrigin.DASHBOARD));

  private final String                                     interchangeResult;
  private final Set<TypeOfBuildingBlock>                   supportedTypesOfBuildingBlock;
  private final Set<InterchangeOrigin>                     supportedOrigins;
  private static final Map<String, InterchangeDestination> LOOKUP_MAP = new HashMap<String, InterchangeDestination>();

  static {
    for (InterchangeDestination s : EnumSet.allOf(InterchangeDestination.class)) {
      LOOKUP_MAP.put(s.getValue(), s);
    }
  }

  private InterchangeDestination(String interchangeResult, Set<TypeOfBuildingBlock> supportedTypesOfBuildingBlock,
      Set<InterchangeOrigin> supportedOrigins) {
    this.interchangeResult = interchangeResult;
    this.supportedTypesOfBuildingBlock = supportedTypesOfBuildingBlock;
    this.supportedOrigins = supportedOrigins;
  }

  public String getValue() {
    return interchangeResult;
  }

  public Set<TypeOfBuildingBlock> getSupportedTypesOfBuildingBlock() {
    return supportedTypesOfBuildingBlock;
  }

  public Set<InterchangeOrigin> getSupportedOrigins() {
    return supportedOrigins;
  }

  public static InterchangeDestination getInterchangeResultByString(String str) {
    return LOOKUP_MAP.get(str);
  }

  public static List<InterchangeDestination> getSupportedDestinations(TypeOfBuildingBlock typeOfBuildingBlock, InterchangeOrigin forInterchangeOrigin) {
    List<InterchangeDestination> result = new ArrayList<InterchangeDestination>();

    for (InterchangeDestination res : InterchangeDestination.values()) {
      if (res.getSupportedTypesOfBuildingBlock().contains(typeOfBuildingBlock) && res.getSupportedOrigins().contains(forInterchangeOrigin)) {
        result.add(res);
      }
    }
    return result;
  }

  public static List<String> getSupportedDestinationStrings(TypeOfBuildingBlock typeOfBuildingBlock, InterchangeOrigin forInterchangeOrigin) {
    List<String> result = new ArrayList<String>();
    for (InterchangeDestination dest : getSupportedDestinations(typeOfBuildingBlock, forInterchangeOrigin)) {
      result.add(dest.getValue());
    }
    return result;
  }

}
