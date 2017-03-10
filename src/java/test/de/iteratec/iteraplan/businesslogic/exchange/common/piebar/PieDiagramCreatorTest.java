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
package de.iteratec.iteraplan.businesslogic.exchange.common.piebar;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.ValuesSource;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class PieDiagramCreatorTest extends PieBarDiagramCreatorTestBase {

  private PieDiagramCreator     pieDiagramCreator;

  @Autowired
  private AttributeTypeService  attributeTypeService;
  @Autowired
  private AttributeValueService attributeValueService;

  @Before
  public void setUp() {
    super.setUp();
    getPieBarOptions().setDiagramType(PieBarDiagramOptionsBean.DiagramType.PIE);
  }

  @Test
  public final void testCreateDiagramCaseAttributeValues() {
    beginTransaction();

    getPieBarOptions().setDiagramKeyType(PieBarDiagramOptionsBean.DiagramKeyType.ATTRIBUTE_TYPES);
    getPieBarOptions().getColorOptionsBean().setDimensionAttributeId(getNumberAT().getId());
    getPieBarOptions().setDiagramValuesType(PieBarDiagramOptionsBean.ValuesType.VALUES);

    refresh();

    pieDiagramCreator = new PieDiagramCreator(getPieBarOptions(), getBoList(), attributeTypeService, attributeValueService);
    PieBar pie = pieDiagramCreator.createDiagram();

    int[] expectedSegmentSizes = { 1, 0, 2, 0, 2, 0 };
    assertBarSegments(pie, expectedSegmentSizes);
  }

  @Test
  public final void testCreateDiagramCaseAttributeMaintained() {
    beginTransaction();

    getPieBarOptions().setDiagramKeyType(PieBarDiagramOptionsBean.DiagramKeyType.ATTRIBUTE_TYPES);
    getPieBarOptions().getColorOptionsBean().setDimensionAttributeId(getEnumAT().getId());
    getPieBarOptions().setDiagramValuesType(PieBarDiagramOptionsBean.ValuesType.MAINTAINED);

    refresh();

    pieDiagramCreator = new PieDiagramCreator(getPieBarOptions(), getBoList(), attributeTypeService, attributeValueService);
    PieBar pie = pieDiagramCreator.createDiagram();

    int[] expectedSegmentSizes = { 4, 1 };
    assertBarSegments(pie, expectedSegmentSizes);
  }

  @Test
  public final void testCreateDiagramCaseAttributeCount() {
    beginTransaction();

    getPieBarOptions().setDiagramKeyType(PieBarDiagramOptionsBean.DiagramKeyType.ATTRIBUTE_TYPES);
    getPieBarOptions().getColorOptionsBean().setDimensionAttributeId(getEnumAT().getId());
    getPieBarOptions().setDiagramValuesType(PieBarDiagramOptionsBean.ValuesType.COUNT);

    refresh();

    pieDiagramCreator = new PieDiagramCreator(getPieBarOptions(), getBoList(), attributeTypeService, attributeValueService);
    PieBar pie = pieDiagramCreator.createDiagram();

    int[] expectedSegmentSizes = { 1, 2, 0, 1, 0, 1 };
    assertBarSegments(pie, expectedSegmentSizes);
  }

  @Test
  public final void testCreateDiagramCaseAssociationMaintained() {
    beginTransaction();

    getPieBarOptions().setDiagramKeyType(PieBarDiagramOptionsBean.DiagramKeyType.ASSOCIATION_NAMES);
    getPieBarOptions().setSelectedAssociation(TypeOfBuildingBlock.BUSINESSFUNCTION.getValue());
    getPieBarOptions().setValuesSource(ValuesSource.ASSOCIATION);
    getPieBarOptions().setDiagramValuesType(PieBarDiagramOptionsBean.ValuesType.MAINTAINED);

    refresh();

    pieDiagramCreator = new PieDiagramCreator(getPieBarOptions(), getBoList(), attributeTypeService, attributeValueService);
    PieBar pie = pieDiagramCreator.createDiagram();

    int[] expectedSegmentSizes = { 4, 1 };
    assertBarSegments(pie, expectedSegmentSizes);
  }

  @Test
  public final void testCreateDiagramCaseAssociationCount() {
    beginTransaction();

    getPieBarOptions().setDiagramKeyType(PieBarDiagramOptionsBean.DiagramKeyType.ASSOCIATION_NAMES);
    getPieBarOptions().setSelectedAssociation(TypeOfBuildingBlock.BUSINESSFUNCTION.getValue());
    getPieBarOptions().setValuesSource(ValuesSource.ASSOCIATION);
    getPieBarOptions().setDiagramValuesType(PieBarDiagramOptionsBean.ValuesType.COUNT);

    refresh();

    pieDiagramCreator = new PieDiagramCreator(getPieBarOptions(), getBoList(), attributeTypeService, attributeValueService);
    PieBar pie = pieDiagramCreator.createDiagram();

    int[] expectedSegmentSizes = { 2, 1, 1, 1 };
    assertBarSegments(pie, expectedSegmentSizes);
  }

  private void assertBarSegments(PieBar pie, int[] expectedSegmentSizesBar1) {
    int count = 0;
    for (Map.Entry<String, Integer> entry : pie.getValuesToSizeMap().entrySet()) {
      assertEquals(expectedSegmentSizesBar1[count], entry.getValue().intValue());
      count++;
    }
  }

}
