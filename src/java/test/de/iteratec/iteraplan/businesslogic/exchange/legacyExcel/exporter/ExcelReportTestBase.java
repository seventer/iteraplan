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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Before;

import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


/**
 * {@link ExcelReportTestBase} serves as a parent class for test cases that are responsible for testing the creation of excel reports for
 * the different types of {@link de.iteratec.iteraplan.model.BuildingBlock}s.
 * For required DAO-instances mocks are used.<br/>
 * The main test issue is the composition of a whole report out of single sheets which is controlled
 * by user permissions. The sheets themselves are empty. {@link ExcelSheetTestBase} and its subclasses are testing the actual contents
 * of the sheets.
 * The main purpose of {@link ExcelReportTestBase} is to hold shared constants and common methods. The actual test methods for the
 * specific {@link de.iteratec.iteraplan.model.BuildingBlock}s are maintained in the corresponding subclasses of this class.
 */
public class ExcelReportTestBase {

  static final String                DEFAULT_SHEET_KEY   = "ExcelTemplateSheet";
  static final String                AD_SHEET_KEY        = "architecturalDomain.plural";
  static final String                ISR_SHEET_KEY       = "informationSystemRelease.plural";
  static final String                ISI_SHEET_KEY       = "interface.plural";
  static final String                ISD_SHEET_KEY       = "informationSystemDomain.plural";
  static final String                BD_SHEET_KEY        = "businessDomain.plural";
  static final String                BF_SHEET_KEY        = "global.business_functions";
  static final String                BM_SHEET_KEY        = "businessMapping.plural";
  static final String                BO_SHEET_KEY        = "businessObject.plural";
  static final String                BP_SHEET_KEY        = "businessProcess.plural";
  static final String                BU_SHEET_KEY        = "businessUnit.plural";
  static final String                IE_SHEET_KEY        = "deploymentInfrastructure";
  static final String                PRODUCT_SHEET_KEY   = "global.products";
  static final String                PROJECT_SHEET_KEY   = "project.plural";
  static final String                TCR_SHEET_KEY       = "technicalRealisation";

  static final TypeOfBuildingBlock[] BLOCKS              = new TypeOfBuildingBlock[] { TypeOfBuildingBlock.ARCHITECTURALDOMAIN,
      TypeOfBuildingBlock.BUSINESSDOMAIN, TypeOfBuildingBlock.BUSINESSFUNCTION, TypeOfBuildingBlock.BUSINESSOBJECT,
      TypeOfBuildingBlock.BUSINESSPROCESS, TypeOfBuildingBlock.BUSINESSUNIT, TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN,
      TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE,
      TypeOfBuildingBlock.BUSINESSMAPPING, TypeOfBuildingBlock.PRODUCT, TypeOfBuildingBlock.PROJECT, TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE };

  private BuildingBlockTypeDAO       bbtDAOMock;
  private AttributeTypeDAO           attrTypeDAOMock;

  static final String                TEST_SERVER_URL     = "testServerURL";

  /**
   * setup method that is called before a test method in {@link ExcelReportTestBase} or one of its subclasses
   * will be invoked.
   * 
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    attrTypeDAOMock = EasyMock.createMock(AttributeTypeDAO.class);
    bbtDAOMock = EasyMock.createMock(BuildingBlockTypeDAO.class);
  }

  protected void addExpectedCall(TypeOfBuildingBlock bbType, List<AttributeType> attrTypes) {
    EasyMock.expect(this.attrTypeDAOMock.getAttributeTypesForTypeOfBuildingBlock(bbType, true)).andReturn(attrTypes);
  }

  protected void addExpectedCall() {
    for (int i = 0; i < BLOCKS.length; i++) {
      TypeOfBuildingBlock bbt = BLOCKS[i];
      EasyMock.expect(this.bbtDAOMock.getBuildingBlockTypeByType(bbt)).andReturn(this.createBBT(bbt, Integer.valueOf(i)));
    }
  }

  @SuppressWarnings("boxing")
  protected void verifySheets(ExportWorkbook context, int expSheetNumber, String... expSheetKeys) {
    assertNotNull(context);
    assertSame("Expected " + expSheetNumber + " sheets, received " + context.getNumberOfSheets(), context.getNumberOfSheets(), expSheetNumber);
    for (int i = 1; i < expSheetKeys.length; i++) {
      String sheetName = MessageAccess.getStringOrNull(expSheetKeys[i], context.getLocale());

      // If the sheet name contains one of the characters below the excel workbook could not be
      // generated
      assertFalse(sheetName.contains("\\"));
      assertFalse(sheetName.contains("/"));
      assertFalse(sheetName.contains("*"));
      assertFalse(sheetName.contains("["));
      assertFalse(sheetName.contains("]"));
      assertFalse(sheetName.contains("?"));

      String expSheetName = context.getSheetName(i);
      assertEquals(sheetName, expSheetName);
    }
  }

  protected UserContext createUserContext(List<TypeOfFunctionalPermission> funcPerms) {
    User user = new User();
    user.setLoginName("system");
    user.setFirstName("");
    user.setLastName("");
    user.setDataSource("MASTER");
    user.setId(Integer.valueOf(0));
    user.setParentUserGroups(new HashSet<UserGroup>());
    Role role = new Role();
    role.setRoleName("Testrole");
    for (TypeOfFunctionalPermission tofp : funcPerms) {
      PermissionFunctional pf = new PermissionFunctional();
      pf.setTypeOfFunctionalPermission(tofp);
      role.addPermissionFunctionalTwoWay(pf);
    }
    Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    return new UserContext(user.getLoginName(), roles, new Locale("de"), user);
  }

  private BuildingBlockType createBBT(TypeOfBuildingBlock bbType, Integer id) {
    BuildingBlockType bbt = new BuildingBlockType(bbType);
    bbt.setId(id);
    return bbt;
  }

  protected BuildingBlockTypeDAO getBbtDAOMock() {
    return bbtDAOMock;
  }

  protected AttributeTypeDAO getAttrTypeDAOMock() {
    return attrTypeDAOMock;
  }
}
