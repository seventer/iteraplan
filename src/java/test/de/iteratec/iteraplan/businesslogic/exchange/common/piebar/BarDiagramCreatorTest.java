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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.DiagramKeyType;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.ValuesSource;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.ValuesType;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.SingleBarOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BusinessFunctionService;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class BarDiagramCreatorTest extends PieBarDiagramCreatorTestBase {

  private BarDiagramCreator       barDiagramCreator;

  @Autowired
  private AttributeTypeService    attributeTypeService;
  @Autowired
  private AttributeValueService   attributeValueService;
  @Autowired
  private BusinessFunctionService businessFunctionService;

  @Override
  @Before
  public void setUp() {
    super.setUp();

    getPieBarOptions().setDiagramType(PieBarDiagramOptionsBean.DiagramType.BAR);
    beginTransaction();
    initPieBarOptions();
    commit();
  }

  @Test
  public final void testCreateDiagramCaseAttributeTypesValues() {
    beginTransaction();

    getPieBarOptions().setDiagramKeyType(DiagramKeyType.ATTRIBUTE_TYPES);
    getPieBarOptions().getColorOptionsBean().setDimensionAttributeId(Integer.valueOf(-1));
    getPieBarOptions().setDiagramValuesType(ValuesType.VALUES);
    getPieBarOptions().refreshSingleBarValueTypes();
    for (SingleBarOptionsBean sbob : getPieBarOptions().getBarsMap().values()) {
      sbob.setSelected(true);
      getInitFormHelperService().refreshGraphicalExportColorOptionsForPieBar(sbob.getType(), getPieBarOptions(), sbob.getColorOptions(),
          TypeOfBuildingBlock.BUSINESSOBJECT);
    }

    barDiagramCreator = new BarDiagramCreator(getPieBarOptions(), getBoList(), attributeTypeService, attributeValueService, businessFunctionService);
    BarDiagram diagram = barDiagramCreator.createDiagram();

    assertTrue(diagram.isColorsSet());

    int[] expectedBarsSizes = { 5, 10, 5 };
    assertBarsCountAndSize(expectedBarsSizes, diagram.getBars());

    int[] expectedSegmentSizesDescriptionBar = { 5, 0 };
    assertBarSegments(diagram, expectedSegmentSizesDescriptionBar, 0);

    int[] expectedSegmentSizesEnumATBar = { 2, 2, 1, 2, 2, 1 };
    assertBarSegments(diagram, expectedSegmentSizesEnumATBar, 1);

    int[] expectedSegmentSizesNumberATBar = { 1, 0, 2, 0, 2, 0 };
    assertBarSegments(diagram, expectedSegmentSizesNumberATBar, 2);
  }

  @Test
  public final void testCreateDiagramCaseAttributeTypesMaintained() {
    beginTransaction();

    getPieBarOptions().setDiagramKeyType(DiagramKeyType.ATTRIBUTE_TYPES);
    getPieBarOptions().getColorOptionsBean().setDimensionAttributeId(Integer.valueOf(-1));
    getPieBarOptions().setDiagramValuesType(ValuesType.MAINTAINED);
    getPieBarOptions().refreshSingleBarValueTypes();
    for (SingleBarOptionsBean sbob : getPieBarOptions().getBarsMap().values()) {
      sbob.setSelected(true);
      getInitFormHelperService().refreshGraphicalExportColorOptionsForPieBar(sbob.getType(), getPieBarOptions(), sbob.getColorOptions(),
          TypeOfBuildingBlock.BUSINESSOBJECT);
    }

    barDiagramCreator = new BarDiagramCreator(getPieBarOptions(), getBoList(), attributeTypeService, attributeValueService, businessFunctionService);
    BarDiagram diagram = barDiagramCreator.createDiagram();

    assertTrue(diagram.isColorsSet());

    int[] expectedBarsSizes = { 5, 5, 5 };
    assertBarsCountAndSize(expectedBarsSizes, diagram.getBars());

    int[] expectedSegmentSizesDescriptionBar = { 5, 0 };
    assertBarSegments(diagram, expectedSegmentSizesDescriptionBar, 0);

    int[] expectedSegmentSizesEnumATBar = { 4, 1 };
    assertBarSegments(diagram, expectedSegmentSizesEnumATBar, 1);

    int[] expectedSegmentSizesNumberATBar = { 5, 0 };
    assertBarSegments(diagram, expectedSegmentSizesNumberATBar, 2);
  }

  @Test
  public final void testCreateDiagramCaseAttributeValuesWithAttributeColourSettings() {
    beginTransaction();

    getPieBarOptions().setDiagramKeyType(DiagramKeyType.ATTRIBUTE_VALUES);
    getPieBarOptions().setSelectedKeyAttributeTypeId(getNumberAT().getId().intValue());
    getPieBarOptions().getColorOptionsBean().setDimensionAttributeId(getEnumAT().getId());
    getPieBarOptions().setDiagramValuesType(ValuesType.MAINTAINED);

    refresh();

    barDiagramCreator = new BarDiagramCreator(getPieBarOptions(), getBoList(), attributeTypeService, attributeValueService, businessFunctionService);
    BarDiagram diagram = barDiagramCreator.createDiagram();

    assertTrue(diagram.isColorsSet());

    int[] expectedBarsSizes = { 1, 0, 2, 0, 2 };
    assertBarsCountAndSize(expectedBarsSizes, diagram.getBars());

    int[] expectedSegmentSizesBar1 = { 1, 0 };
    assertBarSegments(diagram, expectedSegmentSizesBar1, 0);

    int[] expectedSegmentSizesBar3 = { 1, 1 };
    assertBarSegments(diagram, expectedSegmentSizesBar3, 2);

    int[] expectedSegmentSizesBar5 = { 2, 0 };
    assertBarSegments(diagram, expectedSegmentSizesBar5, 4);
  }

  @Test
  public final void testCreateDiagramCaseAttributeValuesWithoutColourSettings() {
    beginTransaction();

    getPieBarOptions().setDiagramKeyType(DiagramKeyType.ATTRIBUTE_VALUES);
    getPieBarOptions().setSelectedKeyAttributeTypeId(getNumberAT().getId().intValue());
    getPieBarOptions().getColorOptionsBean().setDimensionAttributeId(Integer.valueOf(-1));
    getPieBarOptions().setValuesSource(ValuesSource.ATTRIBUTE);

    refresh();

    barDiagramCreator = new BarDiagramCreator(getPieBarOptions(), getBoList(), attributeTypeService, attributeValueService, businessFunctionService);
    BarDiagram diagram = barDiagramCreator.createDiagram();

    assertFalse(diagram.isColorsSet());

    int[] expectedBarsSizes = { 1, 0, 2, 0, 2 };
    assertBarsCountAndSize(expectedBarsSizes, diagram.getBars());

    int[] expectedSegmentSizesBar1 = { 0, 1 };
    assertBarSegments(diagram, expectedSegmentSizesBar1, 0);

    int[] expectedSegmentSizesBar3 = { 0, 2 };
    assertBarSegments(diagram, expectedSegmentSizesBar3, 2);

    int[] expectedSegmentSizesBar5 = { 0, 2 };
    assertBarSegments(diagram, expectedSegmentSizesBar5, 4);
  }

  @Test
  public final void testCreateDiagramCaseAttributeCountWithAssociationColourSettings() {
    beginTransaction();

    getPieBarOptions().setDiagramKeyType(DiagramKeyType.ATTRIBUTE_COUNT);
    getPieBarOptions().setSelectedKeyAttributeTypeId(getEnumAT().getId().intValue());
    getPieBarOptions().getColorOptionsBean().setDimensionAttributeId(Integer.valueOf(-1));
    getPieBarOptions().setSelectedAssociation(TypeOfBuildingBlock.BUSINESSFUNCTION.getValue());
    getPieBarOptions().setValuesSource(ValuesSource.ASSOCIATION);
    getPieBarOptions().setDiagramValuesType(ValuesType.MAINTAINED);

    refresh();

    barDiagramCreator = new BarDiagramCreator(getPieBarOptions(), getBoList(), attributeTypeService, attributeValueService, businessFunctionService);
    BarDiagram diagram = barDiagramCreator.createDiagram();

    assertTrue(diagram.isColorsSet());

    int[] expectedBarsSizes = { 1, 1, 2, 0, 1, 0 };
    assertBarsCountAndSize(expectedBarsSizes, diagram.getBars());

    int[] expectedSegmentSizesBar1 = { 1, 0 };
    assertBarSegments(diagram, expectedSegmentSizesBar1, 0);

    int[] expectedSegmentSizesBar2 = { 1, 0 };
    assertBarSegments(diagram, expectedSegmentSizesBar2, 1);

    int[] expectedSegmentSizesBar3 = { 2, 0 };
    assertBarSegments(diagram, expectedSegmentSizesBar3, 2);

    int[] expectedSegmentSizesBar5 = { 0, 1 };
    assertBarSegments(diagram, expectedSegmentSizesBar5, 4);
  }

  @Test
  public final void testCreateDiagramCaseAssociationNamesWithAttributeColourSettings() {
    beginTransaction();

    getPieBarOptions().setDiagramKeyType(DiagramKeyType.ASSOCIATION_NAMES);
    getPieBarOptions().setSelectedKeyAttributeTypeId(-1);
    getPieBarOptions().getColorOptionsBean().setDimensionAttributeId(getEnumAT().getId());
    getPieBarOptions().setSelectedKeyAssociation(TypeOfBuildingBlock.BUSINESSFUNCTION.getValue());
    getPieBarOptions().setDiagramValuesType(ValuesType.COUNT);

    refresh();

    barDiagramCreator = new BarDiagramCreator(getPieBarOptions(), getBoList(), attributeTypeService, attributeValueService, businessFunctionService);
    BarDiagram diagram = barDiagramCreator.createDiagram();

    assertTrue(diagram.isColorsSet());

    int[] expectedBarsSizes = { 1, 2, 1, 1, 2 };
    assertBarsCountAndSize(expectedBarsSizes, diagram.getBars());

    int[] expectedSegmentSizesBar1 = { 0, 1, 0, 0, 0, 0 };
    assertBarSegments(diagram, expectedSegmentSizesBar1, 0);

    int[] expectedSegmentSizesBar2 = { 1, 1, 0, 0, 0, 0 };
    assertBarSegments(diagram, expectedSegmentSizesBar2, 1);

    int[] expectedSegmentSizesBar3 = { 0, 0, 0, 0, 0, 1 };
    assertBarSegments(diagram, expectedSegmentSizesBar3, 2);

    int[] expectedSegmentSizesBar4 = { 1, 0, 0, 0, 0, 0 };
    assertBarSegments(diagram, expectedSegmentSizesBar4, 3);

    int[] expectedSegmentSizesBar5 = { 1, 0, 0, 0, 0, 1 };
    assertBarSegments(diagram, expectedSegmentSizesBar5, 4);
  }

  @Test
  public final void testCreateDiagramCaseAssociationCountWithAttributeColourSettings() {
    beginTransaction();

    getPieBarOptions().setDiagramKeyType(DiagramKeyType.ASSOCIATION_COUNT);
    getPieBarOptions().setSelectedKeyAttributeTypeId(-1);
    getPieBarOptions().getColorOptionsBean().setDimensionAttributeId(getNumberAT().getId());
    getPieBarOptions().setSelectedKeyAssociation(TypeOfBuildingBlock.BUSINESSFUNCTION.getValue());
    getPieBarOptions().setDiagramValuesType(ValuesType.VALUES);

    refresh();

    barDiagramCreator = new BarDiagramCreator(getPieBarOptions(), getBoList(), attributeTypeService, attributeValueService, businessFunctionService);
    BarDiagram diagram = barDiagramCreator.createDiagram();

    assertTrue(diagram.isColorsSet());

    int[] expectedBarsSizes = { 1, 2, 1, 1 };
    assertBarsCountAndSize(expectedBarsSizes, diagram.getBars());

    int[] expectedSegmentSizesBar1 = { 0, 0, 1, 0, 0, 0 };
    assertBarSegments(diagram, expectedSegmentSizesBar1, 0);

    int[] expectedSegmentSizesBar2 = { 1, 0, 0, 0, 1, 0 };
    assertBarSegments(diagram, expectedSegmentSizesBar2, 1);

    int[] expectedSegmentSizesBar3 = { 0, 0, 1, 0, 0, 0 };
    assertBarSegments(diagram, expectedSegmentSizesBar3, 2);

    int[] expectedSegmentSizesBar4 = { 0, 0, 0, 0, 1, 0 };
    assertBarSegments(diagram, expectedSegmentSizesBar4, 3);
  }

  private void assertBarsCountAndSize(int[] expectedBarsSizes, List<PieBar> bars) {
    assertEquals("number of bars wrong.", expectedBarsSizes.length, bars.size());
    int count = 0;
    for (PieBar bar : bars) {
      assertEquals("length of bar " + bar.getLabel() + " wrong.", expectedBarsSizes[count], bar.getTotalSize());
      count++;
    }
  }

  private void assertBarSegments(BarDiagram diagram, int[] expectedSegmentSizesBar1, int barIndex) {
    PieBar bar = diagram.getBars().get(barIndex);

    assertEquals("number of bar segments wrong.", expectedSegmentSizesBar1.length, bar.getValuesToSizeMap().values().size());

    int count = 0;
    for (Map.Entry<String, Integer> entry : bar.getValuesToSizeMap().entrySet()) {
      assertEquals("length of bar segment " + entry.getKey() + " wrong.", expectedSegmentSizesBar1[count], entry.getValue().intValue());
      count++;
    }
  }

}
