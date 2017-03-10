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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.util;

import java.util.Map;

import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * Class to find a BuildingBlockType by its name, without relying on the database
 */
public final class BuildingBlockTypeNameMatcher {

  private BuildingBlockTypeNameMatcher() {
    //Nothing to do here
  }

  private static final Map<String, TypeOfBuildingBlock> STRING_TO_BBT_MAP = CollectionUtils.hashMap();

  static {
    STRING_TO_BBT_MAP.put("ArchitecturalDomain", TypeOfBuildingBlock.ARCHITECTURALDOMAIN);
    STRING_TO_BBT_MAP.put("BusinessDomain", TypeOfBuildingBlock.BUSINESSDOMAIN);
    STRING_TO_BBT_MAP.put("BusinessFunction", TypeOfBuildingBlock.BUSINESSFUNCTION);
    STRING_TO_BBT_MAP.put("BusinessObject", TypeOfBuildingBlock.BUSINESSOBJECT);
    STRING_TO_BBT_MAP.put("BusinessProcess", TypeOfBuildingBlock.BUSINESSPROCESS);
    STRING_TO_BBT_MAP.put("BusinessUnit", TypeOfBuildingBlock.BUSINESSUNIT);
    STRING_TO_BBT_MAP.put("InformationSystem", TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    STRING_TO_BBT_MAP.put("InformationSystemRelease", TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    STRING_TO_BBT_MAP.put("InformationSystemDomain", TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN);
    STRING_TO_BBT_MAP.put("InformationSystemInterface", TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE);
    STRING_TO_BBT_MAP.put("InfrastructureElement", TypeOfBuildingBlock.INFRASTRUCTUREELEMENT);
    STRING_TO_BBT_MAP.put("Product", TypeOfBuildingBlock.PRODUCT);
    STRING_TO_BBT_MAP.put("Project", TypeOfBuildingBlock.PROJECT);
    STRING_TO_BBT_MAP.put("TechnicalComponent", TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE);
    STRING_TO_BBT_MAP.put("TechnicalComponentRelease", TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE);
    STRING_TO_BBT_MAP.put("Isr2BoAssociation", TypeOfBuildingBlock.ISR2BOASSOCIATION);
    STRING_TO_BBT_MAP.put("BusinessMapping", TypeOfBuildingBlock.BUSINESSMAPPING);
    STRING_TO_BBT_MAP.put("Tcr2IeAssociation", TypeOfBuildingBlock.TCR2IEASSOCIATION);
  }

  /**
   * Returns the {@link TypeOfBuildingBlock} by its persistent name, currently the most simple solution to get the ToBB by name 
   * @param persistentName name
   * @return {@link TypeOfBuildingBlock}
   */
  public static TypeOfBuildingBlock getTypeOfBuildingBlockForPersistentName(String persistentName) {
    return STRING_TO_BBT_MAP.get(persistentName);
  }

}
