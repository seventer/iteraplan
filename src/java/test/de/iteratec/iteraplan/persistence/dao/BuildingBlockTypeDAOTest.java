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
package de.iteratec.iteraplan.persistence.dao;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * @author mma
 */
public class BuildingBlockTypeDAOTest extends BaseTransactionalTestSupport {

  @Autowired
  private BuildingBlockTypeDAO buildingBlockTypeDAO;

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAOImpl#getAvailableBuildingBlockTypesForAttributeType(java.lang.Integer)}
   */
  @Test
  public void testGetAvailableBuildingBlockTypesForAttributeType() {
    Integer id = Integer.valueOf(2);
    List<BuildingBlockType> actual = buildingBlockTypeDAO.getAvailableBuildingBlockTypesForAttributeType(id);
    List<BuildingBlockType> expected = arrayList();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAOImpl#getAvailableBuildingBlockTypesForAttributeType(java.lang.Integer)}
   */
  @Test
  public void testGetAvailableBuildingBlockTypesForAttributeTypeCaseEmpty() {
    Integer id = null;
    List<BuildingBlockType> actual = buildingBlockTypeDAO.getAvailableBuildingBlockTypesForAttributeType(id);
    List<BuildingBlockType> expected = arrayList();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAOImpl#getConnectedBuildingBlockTypesForAttributeType(java.lang.Integer)}
   */
  @Test
  public void testGetConnectedBuildingBlockTypesForAttributeType() {
    Integer id = Integer.valueOf(2);
    List<BuildingBlockType> actual = buildingBlockTypeDAO.getConnectedBuildingBlockTypesForAttributeType(id);
    List<BuildingBlockType> expected = arrayList();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAOImpl#getConnectedBuildingBlockTypesForAttributeType(java.lang.Integer)}
   */
  @Test
  public void testGetConnectedBuildingBlockTypesForAttributeTypeCaseEmpty() {
    Integer id = null;
    List<BuildingBlockType> actual = buildingBlockTypeDAO.getConnectedBuildingBlockTypesForAttributeType(id);
    List<BuildingBlockType> expected = arrayList();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAOImpl#getBuildingBlockTypeByType(de.iteratec.iteraplan.model.TypeOfBuildingBlock)}
   */
  @Test
  public void testGetBuildingBlockTypeByType() {
    BuildingBlockType actual = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSOBJECT);
    BuildingBlockType expected = new BuildingBlockType(TypeOfBuildingBlock.BUSINESSOBJECT);

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAOImpl#getBuildingBlockTypeByType(de.iteratec.iteraplan.model.TypeOfBuildingBlock)}
   */
  @Test
  public void testGetBuildingBlockTypeByTypeCaseException() {
    try {
      buildingBlockTypeDAO.getBuildingBlockTypeByType(null);
    } catch (IteraplanTechnicalException e) {
      // nothing to do.
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAOImpl#getBuildingBlockTypesEligibleForAttributes()}
   */
  @Test
  public void testGetBuildingBlockTypesEligibleForAttributes() {
    List<BuildingBlockType> actual = buildingBlockTypeDAO.getBuildingBlockTypesEligibleForAttributes();
    Collections.sort(actual);

    List<BuildingBlockType> expected = arrayList();
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.ISR2BOASSOCIATION));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.TCR2IEASSOCIATION));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSMAPPING));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.PROJECT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.PRODUCT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSPROCESS));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSUNIT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSOBJECT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSFUNCTION));
    Collections.sort(expected);

    assertEquals(expected, actual);
  }

}
