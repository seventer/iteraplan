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
package de.iteratec.iteraplan.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.iteratec.iteraplan.presentation.dialog.ArchitectualDomain.ArchitecturalDomainDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.AttributeType.AttributeTypeDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.AttributeTypeGroup.AttributeTypeGroupDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.BusinessDomain.BusinessDomainDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.BusinessFunction.BusinessFunctionDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.BusinessObject.BusinessObjectDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.BusinessProcess.BusinessProcessDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.BusinessUnit.BusinessUnitDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.Configuration.ConfigurationDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.ConsistencyCheck.ConsistencyCheckDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.InformationSystem.InformationSystemDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.InformationSystemDomain.InformationSystemDomainDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.InformationSystemInterface.InformationSystemInterfaceDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.InfrastructureElement.InfrastructureElementDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.ObjectRelatedPermission.ObjectRelatedPermissionDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.Product.ProductDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.Project.ProjectDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.Role.RoleDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.Search.SearchDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.SuccessorReport.ReleaseSuccessorDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.SupportingQuery.PermissionQueriesDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.TechnicalComponent.TechnicalComponentDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.User.UserDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.UserGroup.UserGroupDialogMemory;
import de.iteratec.iteraplan.presentation.dialog.XmiDeserialization.XmiDeserializationDialogMemory;
import de.iteratec.iteraplan.presentation.memory.DialogMemory;


@RunWith(Parameterized.class)
public class GuiContextTest {

  private static final String TMP_FILE = "guiContextTest12345.tmp";

  private final DialogMemory  dialogMemory;

  public GuiContextTest(DialogMemory dialogMemory) {
    this.dialogMemory = dialogMemory;
  }

  @Parameters
  public static Collection<?> getDialogMemoryImplementations() {
    return Arrays.asList(new Object[][] { { new AttributeTypeGroupDialogMemory() }, { new ConfigurationDialogMemory() },
        { new ConsistencyCheckDialogMemory() }, { new ReleaseSuccessorDialogMemory() }, { new PermissionQueriesDialogMemory() },
        { new ArchitecturalDomainDialogMemory() }, { new AttributeTypeDialogMemory() }, { new BusinessDomainDialogMemory() },
        { new BusinessFunctionDialogMemory() }, { new BusinessObjectDialogMemory() }, { new BusinessProcessDialogMemory() },
        { new BusinessUnitDialogMemory() }, { new InformationSystemDialogMemory() }, { new InformationSystemDomainDialogMemory() },
        { new InformationSystemInterfaceDialogMemory() }, { new InfrastructureElementDialogMemory() }, { new ObjectRelatedPermissionDialogMemory() },
        { new ProductDialogMemory() }, { new ProjectDialogMemory() }, { new RoleDialogMemory() }, { new TechnicalComponentDialogMemory() },
        { new UserDialogMemory() }, { new UserGroupDialogMemory() }, { new SearchDialogMemory() }, { new XmiDeserializationDialogMemory() } });
  }

  @Test
  public void testSerializing() throws IOException, ClassNotFoundException {
    ObjectInputStream is = null;
    ObjectOutputStream oos = null;
    try {
      GuiContext guiContext = createGuiContext();
      oos = new ObjectOutputStream(new FileOutputStream(TMP_FILE));
      oos.writeObject(guiContext);
    } finally {
      if (oos != null) {
        oos.close();
      }
    }
    try {
      is = new ObjectInputStream(new FileInputStream(TMP_FILE));
      postCheck((GuiContext) is.readObject());
    } finally {
      if (is != null) {
        is.close();
      }
    }
  }

  private void postCheck(GuiContext guiContext) {
    DialogMemory d = guiContext.getDialogMemory("dummy");
    assertEquals(d.getClass(), dialogMemory.getClass());

    // If the class overrides readObject, check that both transient and persistent variables exist
    if (d instanceof ConfigurationDialogMemory) {
      assertNotNull(((ConfigurationDialogMemory) d).getSelectedDataSource());
      assertNotNull(((ConfigurationDialogMemory) d).getRoutingDatasourceModel());
    }
    else if (d instanceof ReleaseSuccessorDialogMemory) {
      assertNotNull(((ReleaseSuccessorDialogMemory) d).getSelectedIsrId());
      assertNotNull(((ReleaseSuccessorDialogMemory) d).getIsrSuccessorDTO());
    }
    else if (d instanceof SearchDialogMemory) {
      assertNotNull(((SearchDialogMemory) d).getSearchField());
      assertNotNull(((SearchDialogMemory) d).getSearchDTO());
    }
  }

  private GuiContext createGuiContext() {
    GuiContext guiContext = new GuiContext();
    guiContext.addFlowEntry("AttributeType", "key", "entity", Integer.valueOf(1));
    guiContext.setDialogMemory("dummy", dialogMemory);

    // Set dialog memory specific values
    if (dialogMemory instanceof ConfigurationDialogMemory) {
      ((ConfigurationDialogMemory) dialogMemory).setSelectedDataSource("dataSource");
    }
    else if (dialogMemory instanceof ReleaseSuccessorDialogMemory) {
      ((ReleaseSuccessorDialogMemory) dialogMemory).setSelectedIsrId(Integer.valueOf(-42));
    }
    else if (dialogMemory instanceof SearchDialogMemory) {
      ((SearchDialogMemory) dialogMemory).setSearchField("field");
    }
    return guiContext;

  }

  @AfterClass
  public static void cleanUp() {
    File tmpFile = new File(TMP_FILE);
    if (tmpFile.exists()) {
      tmpFile.delete();
    }
  }
}
