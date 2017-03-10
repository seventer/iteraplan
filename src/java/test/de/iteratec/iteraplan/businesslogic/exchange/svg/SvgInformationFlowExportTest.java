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
package de.iteratec.iteraplan.businesslogic.exchange.svg;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.common.informationflow.InformationFlowGraphConverter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.svg.model.Document;


public class SvgInformationFlowExportTest extends BaseTransactionalTestSupport {

  private static final TypeOfStatus CURRENT_STATUS   = InformationSystemRelease.TypeOfStatus.CURRENT;
  private static final String       STANDARD_DATE    = "01.03.2005";
  private static final String[]     LABELS           = { "IS1", "IS2", "IS3", "IS4", "IS5" };
  private static final String[]     VERSIONS         = { "0.9", "1.0", "1.1", "2.0" };
  private static final String       TEST_DESCRIPTION = "testDescription";

  @Autowired
  private AttributeTypeService      attributeTypeService;
  @Autowired
  private AttributeValueService     attributeValueService;
  @Autowired
  private TestDataHelper2           testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.svg.SvgInformationFlowExport#createDiagram()}.
   */
  @Test
  public final void testCreateDiagram() {
    final Set<InformationSystemRelease> isrSet = setUpTestData();

    SvgInformationFlowExport export = firstCase(isrSet);
    assertCreation(export);

    export = secondCase(isrSet);
    assertCreation(export);

    export = thirdCase(isrSet);
    assertCreation(export);

    export = fourthCase(isrSet);
    assertCreation(export);
  }

  private SvgInformationFlowExport firstCase(final Set<InformationSystemRelease> isrSet) {
    final InformationFlowOptionsBean informationFlowOptions = new InformationFlowOptionsBean();

    return createExport(isrSet, informationFlowOptions);
  }

  private SvgInformationFlowExport secondCase(final Set<InformationSystemRelease> isrSet) {
    final InformationFlowOptionsBean informationFlowOptions = new InformationFlowOptionsBean();
    informationFlowOptions.setSelectionType(new int[] { InformationFlowOptionsBean.LINE_DESCR_DESCRIPTION });
    informationFlowOptions.setShowIsBusinessObjects(false);

    return createExport(isrSet, informationFlowOptions);
  }

  private SvgInformationFlowExport thirdCase(final Set<InformationSystemRelease> isrSet) {
    final InformationFlowOptionsBean informationFlowOptions = new InformationFlowOptionsBean();
    informationFlowOptions.setSelectionType(new int[] { InformationFlowOptionsBean.LINE_DESCR_NAME });
    informationFlowOptions.setShowIsBaseComponents(false);

    return createExport(isrSet, informationFlowOptions);
  }

  private SvgInformationFlowExport fourthCase(final Set<InformationSystemRelease> isrSet) {
    final InformationFlowOptionsBean informationFlowOptions = new InformationFlowOptionsBean();
    informationFlowOptions.setSelectionType(new int[] { InformationFlowOptionsBean.LINE_DESCR_TECHNICAL_COMPONENTS });
    informationFlowOptions.setShowIsBaseComponents(false);
    informationFlowOptions.setShowIsBusinessObjects(false);

    return createExport(isrSet, informationFlowOptions);
  }

  private void assertCreation(final SvgInformationFlowExport export) {

    Document doc = null;
    try {
      doc = export.createDiagram();
    } catch (IteraplanTechnicalException e) {
      fail("There should have been a diagram created:\n" + e.getMessage());
    }
    assertNotNull("There should have been a diagram created", doc);
  }

  private SvgInformationFlowExport createExport(final Set<InformationSystemRelease> isrSet, final InformationFlowOptionsBean informationFlowOptions) {
    final InformationFlowGraphConverter informationFlowConverter = new InformationFlowGraphConverter(isrSet, null, null, attributeTypeService,
        informationFlowOptions);
    informationFlowConverter.convertToGraph();

    return new SvgInformationFlowExport(UserContext.getCurrentLocale(), informationFlowConverter.getConvertedGraph(), informationFlowOptions,
        attributeTypeService, attributeValueService);
  }

  private Set<InformationSystemRelease> setUpTestData() {
    final List<InformationSystemRelease> isrs = createIsrs();
    createRelations(isrs);
    createInterfaces(isrs);

    return new HashSet<InformationSystemRelease>(isrs);
  }

  private List<InformationSystemRelease> createIsrs() {
    final List<InformationSystemRelease> isrs = CollectionUtils.arrayList();

    final InformationSystem is1 = testDataHelper.createInformationSystem(LABELS[0]);

    final InformationSystemRelease isr1a = testDataHelper.createInformationSystemRelease(is1, VERSIONS[0], TEST_DESCRIPTION, null, STANDARD_DATE,
        CURRENT_STATUS);
    isrs.add(isr1a);
    final InformationSystemRelease isr1b = testDataHelper.createInformationSystemRelease(is1, VERSIONS[1], TEST_DESCRIPTION, null, STANDARD_DATE,
        CURRENT_STATUS);
    isrs.add(isr1b);

    final InformationSystem is2 = testDataHelper.createInformationSystem(LABELS[1]);

    final InformationSystemRelease isr2a = testDataHelper.createInformationSystemRelease(is2, VERSIONS[1], TEST_DESCRIPTION, null, STANDARD_DATE,
        CURRENT_STATUS);
    isrs.add(isr2a);
    final InformationSystemRelease isr2b = testDataHelper.createInformationSystemRelease(is2, VERSIONS[3], TEST_DESCRIPTION, null, STANDARD_DATE,
        CURRENT_STATUS);
    isrs.add(isr2b);

    final InformationSystem is3 = testDataHelper.createInformationSystem(LABELS[2]);

    final InformationSystemRelease isr3a = testDataHelper.createInformationSystemRelease(is3, VERSIONS[1], TEST_DESCRIPTION, null, STANDARD_DATE,
        CURRENT_STATUS);
    isrs.add(isr3a);
    final InformationSystemRelease isr3b = testDataHelper.createInformationSystemRelease(is3, VERSIONS[2], TEST_DESCRIPTION, null, STANDARD_DATE,
        CURRENT_STATUS);
    isrs.add(isr3b);

    final InformationSystem is4 = testDataHelper.createInformationSystem(LABELS[3]);

    final InformationSystemRelease isr4a = testDataHelper.createInformationSystemRelease(is4, VERSIONS[2], TEST_DESCRIPTION, null, STANDARD_DATE,
        CURRENT_STATUS);
    isrs.add(isr4a);
    final InformationSystemRelease isr4b = testDataHelper.createInformationSystemRelease(is4, VERSIONS[3], TEST_DESCRIPTION, null, STANDARD_DATE,
        CURRENT_STATUS);
    isrs.add(isr4b);

    final InformationSystem is5 = testDataHelper.createInformationSystem(LABELS[4]);

    final InformationSystemRelease isr5a = testDataHelper.createInformationSystemRelease(is5, VERSIONS[0], TEST_DESCRIPTION, null, STANDARD_DATE,
        CURRENT_STATUS);
    isrs.add(isr5a);
    final InformationSystemRelease isr5b = testDataHelper.createInformationSystemRelease(is5, VERSIONS[2], TEST_DESCRIPTION, null, STANDARD_DATE,
        CURRENT_STATUS);
    isrs.add(isr5b);
    return isrs;
  }

  private void createRelations(List<InformationSystemRelease> isrs) {
    isrs.get(0).addBaseComponent(isrs.get(2));
    isrs.get(0).addBaseComponent(isrs.get(3));

    isrs.get(5).addBaseComponent(isrs.get(9));

    testDataHelper.addBusinessObjectToInformationSystem(isrs.get(2), testDataHelper.createBusinessObject("BO1", TEST_DESCRIPTION));
    testDataHelper.addBusinessObjectToInformationSystem(isrs.get(0), testDataHelper.createBusinessObject("BO2", TEST_DESCRIPTION));
    testDataHelper.addBusinessObjectToInformationSystem(isrs.get(8), testDataHelper.createBusinessObject("BO3", TEST_DESCRIPTION));
    testDataHelper.addBusinessObjectToInformationSystem(isrs.get(4), testDataHelper.createBusinessObject("BO4", TEST_DESCRIPTION));

    isrs.get(1).addParent(isrs.get(4));

    isrs.get(4).addParent(isrs.get(6));

    isrs.get(5).addChild(isrs.get(7));
    isrs.get(5).addChild(isrs.get(0));
    isrs.get(5).addChild(isrs.get(8));

    isrs.get(8).addChild(isrs.get(9));
  }

  private void createInterfaces(List<InformationSystemRelease> isrs) {
    testDataHelper.createInformationSystemInterface(isrs.get(0), isrs.get(1), null, TEST_DESCRIPTION);
    testDataHelper.createInformationSystemInterface(isrs.get(0), isrs.get(2), null, TEST_DESCRIPTION);
    testDataHelper.createInformationSystemInterface(isrs.get(2), isrs.get(8), null, TEST_DESCRIPTION);
    testDataHelper.createInformationSystemInterface(isrs.get(3), isrs.get(7), null, TEST_DESCRIPTION);
    testDataHelper.createInformationSystemInterface(isrs.get(4), isrs.get(7), null, TEST_DESCRIPTION);
    testDataHelper.createInformationSystemInterface(isrs.get(5), isrs.get(6), null, TEST_DESCRIPTION);
    testDataHelper.createInformationSystemInterface(isrs.get(5), isrs.get(7), null, TEST_DESCRIPTION);
    testDataHelper.createInformationSystemInterface(isrs.get(5), isrs.get(9), null, TEST_DESCRIPTION);
    testDataHelper.createInformationSystemInterface(isrs.get(1), isrs.get(3), null, TEST_DESCRIPTION);
    testDataHelper.createInformationSystemInterface(isrs.get(1), isrs.get(8), null, TEST_DESCRIPTION);
    testDataHelper.createInformationSystemInterface(isrs.get(1), isrs.get(6), null, TEST_DESCRIPTION);
  }
}
