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

import static org.junit.Assert.assertSame;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Test;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.InformationSystemInterfaceExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TransportInfo;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.sorting.IdentityStringComparator;
import de.iteratec.iteraplan.model.user.User;

/**
 * This class is responsible for testing the content of the excel 
 * export for {@link de.iteratec.iteraplan.model.InformationSystemInterface}s.
 * 
 * @see InformationSystemInterfaceReportTest
 */
public class InformationSystemInterfaceSheetTest extends ExcelSheetTestBase {

  /**
   * Tests the content of the excel sheet export for {@link InformationSystemInterface}s in Excel 2003
   */
  @Test
  public void testISISheet2003() {
    ExportWorkbook context = new ExportWorkbook(Locale.GERMAN, TemplateType.EXCEL_2003);

    createAndVerifySheet(context);
  }

  /**
  * Tests the content of the excel sheet export for {@link InformationSystemInterface}s in Excel 2007
  */
  @Test
  public void testISISheet2007() {
    ExportWorkbook context = new ExportWorkbook(Locale.GERMAN, TemplateType.EXCEL_2007);

    createAndVerifySheet(context);
  }

  /**
   * @param context The Excel-Workbook to be tested
   */
  private void createAndVerifySheet(ExportWorkbook context) {
    ExporterDataUtil exporterDataUtil = ExporterDataUtil.getInstance();

    Set<User> subscribedUsers = exporterDataUtil.createUsers(0, SYSTEM_USER);

    Set<InformationSystemInterface> isiSet = createTestInterfaces(exporterDataUtil, subscribedUsers, "testISI", 10);

    BuildingBlockType bbt = new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE);
    bbt.setId(Integer.valueOf(0));

    InformationSystemInterfaceExcelSheet sheet = new InformationSystemInterfaceExcelSheet(isiSet, context, new ArrayList<AttributeType>(), null, bbt,
        TEST_URL);
    sheet.createSheet();

    // is required because of the order of the entities to be compared with the received contents.
    // All entities are sorted before adding them to the result sheet.
    List<InformationSystemInterface> sortedISIs = ExcelHelper.sortEntities(isiSet, new IdentityStringComparator());

    assertSame(Integer.valueOf(context.getNumberOfSheets()), Integer.valueOf(2));
    Sheet resultSheet = context.getSheetAt(1);

    this.verifyRowContent(this.retrieveNextRow(resultSheet), MessageAccess.getStringOrNull(ExcelReportTestBase.ISI_SHEET_KEY, context.getLocale()));

    List<String> expContent = new ArrayList<String>();
    expContent.add(MessageAccess.getStringOrNull("global.id", context.getLocale()));

    expContent.add(MessageAccess.getStringOrNull("reporting.excel.header.interface.releaseA", context.getLocale()));
    expContent.add(MessageAccess.getStringOrNull("reporting.excel.header.interface.releaseB", context.getLocale()));
    expContent.add(MessageAccess.getStringOrNull("global.name", context.getLocale()));
    expContent.add(MessageAccess.getStringOrNull(ISI_TRANSPORT, context.getLocale()));
    expContent.add(MessageAccess.getStringOrNull("global.description", context.getLocale()));
    expContent.add(MessageAccess.getStringOrNull("global.lastModificationUser", context.getLocale()));
    expContent.add(MessageAccess.getStringOrNull("global.lastModificationTime", context.getLocale()));
    expContent.add(MessageAccess.getStringOrNull("global.subscribed.users", context.getLocale()));
    expContent.add(MessageAccess.getStringOrNull("reporting.excel.header.interface.businessObjects", context.getLocale()));
    expContent.add(MessageAccess.getStringOrNull(Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL, context.getLocale()));

    this.verifyRowContent(this.retrieveNextRow(resultSheet), expContent.toArray());

    for (InformationSystemInterface entity : sortedISIs) {
      expContent = createIsiExpRowContent(entity);
      this.verifyRowContent(this.retrieveNextRow(resultSheet), expContent.toArray());
    }
  }

  private List<String> createIsiExpRowContent(InformationSystemInterface entity) {
    List<String> expContent = new ArrayList<String>();
    expContent.add(String.format("HYPERLINK(\"testURL/show/interface/%d\",%d)", entity.getId(), entity.getId()));
    expContent.add(entity.getInformationSystemReleaseA().getName());
    expContent.add(entity.getInformationSystemReleaseB().getName());
    expContent.add(entity.getName());
    String direction = "'" + TransportInfo.NO_DIRECTION.getTextRepresentation();
    expContent.add(direction);
    expContent.add(entity.getDescription());
    expContent.add(entity.getLastModificationUser());
    SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    formatter.applyPattern(DATE_FORMAT_PATTERN);
    expContent.add(formatter.format(entity.getLastModificationTime()));
    expContent.add(entity.getSubscribedUsers().iterator().next().getIdentityString());
    expContent.add("'- testISI - BusinessObject");
    expContent.add(entity.getTechnicalComponentReleases().iterator().next().getName());
    return expContent;
  }

  private Set<InformationSystemInterface> createTestInterfaces(ExporterDataUtil exporterDataUtil, Set<User> subscribedUsers, String name,
                                                               int entitiesNumber) {
    Set<InformationSystemInterface> entities = new HashSet<InformationSystemInterface>();
    for (int i = 0; i < entitiesNumber; i++) {
      InformationSystemInterface entity = new InformationSystemInterface();
      entity.setId(Integer.valueOf(i));
      Set<InformationSystemRelease> isrs = createISRs(exporterDataUtil, name, i, 2, 3);
      Iterator<InformationSystemRelease> iterator = isrs.iterator();
      entity.setInformationSystemReleaseA(iterator.next());
      entity.setInformationSystemReleaseB(iterator.next());
      entity.setDescription(TEST_DESCRIPTION);
      entity.setLastModificationUser(TEST_USER);
      entity.setLastModificationTime(this.getLastModDate());
      entity.setSubscribedUsers(subscribedUsers);

      // add bo to isi
      BusinessObject bo = new BusinessObject();
      bo.setId(Integer.valueOf(3));
      bo.setName(name + " - BusinessObject");
      Transport transport = new Transport();
      transport.setId(Integer.valueOf(9));
      transport.setDirection(Direction.NO_DIRECTION);
      transport.setBusinessObject(bo);
      entity.addTransport(transport);

      entity.setTechnicalComponentReleases(exporterDataUtil.createTCRs(i + 1, name + " - TechnicalComponent", 4));

      entities.add(entity);
    }
    return entities;
  }
}
