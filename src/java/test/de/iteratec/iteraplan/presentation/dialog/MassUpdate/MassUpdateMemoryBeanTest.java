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
package de.iteratec.iteraplan.presentation.dialog.MassUpdate;

import static org.easymock.EasyMock.eq;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.MockTestDataFactory;
import de.iteratec.iteraplan.MockTestHelper;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QUserInput;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeMu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Property;
import de.iteratec.iteraplan.businesslogic.reports.query.type.SimpleAssociation;
import de.iteratec.iteraplan.businesslogic.service.MassUpdateService;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.presentation.dialog.MemBeanSerializationTestHelper;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.InformationSystemReleaseCmMu;


public class MassUpdateMemoryBeanTest {

  private MassUpdateService massUpdateServiceMock;

  @Before
  public void setUp() {
    massUpdateServiceMock = MockTestHelper.createMock(MassUpdateService.class);
  }

  @Test
  public void testInitFromMemoryBean() {
    MassUpdateMemoryBean memoryBean = new MassUpdateMemoryBean(InformationSystemReleaseTypeMu.getInstance());

    List<BBAttribute> availableAttributes = new ArrayList<BBAttribute>();
    DynamicQueryFormData<InformationSystemRelease> formData = new DynamicQueryFormData<InformationSystemRelease>(availableAttributes,
        InformationSystemReleaseTypeQu.getInstance(), Locale.GERMAN);
    formData.setMassUpdateType(InformationSystemReleaseTypeMu.getInstance());

    List<BBAttribute> availableTimeseriesAttributes = Lists.newArrayList(Collections2.filter(availableAttributes, new Predicate<BBAttribute>() {
      public boolean apply(BBAttribute input) {
        return input.equals(BBAttribute.UNDEFINED_ID_VALUE) || input.isTimeseries();
      }
    }));

    ManageReportMemoryBean manageReportMemoryBean = new ManageReportMemoryBean(formData, new TimeseriesQuery(availableTimeseriesAttributes));

    QUserInput userinput = manageReportMemoryBean.getQueryResult().getQueryForms().get(0).getQueryUserInput();
    Property prop1 = InformationSystemReleaseTypeMu.getInstance().getProperties().get(0);
    Property prop2 = InformationSystemReleaseTypeMu.getInstance().getProperties().get(1);
    userinput.getMassUpdateData().setPropertySelected(prop1.getNameAsID(), Boolean.TRUE);
    userinput.getMassUpdateData().setPropertySelected(prop2.getNameAsID(), Boolean.TRUE);

    for (SimpleAssociation muAssoc : InformationSystemReleaseTypeMu.getInstance().getMassUpdateAssociations()) {
      if (muAssoc.getPosition() == 1 || muAssoc.getPosition() == 2) {
        userinput.getMassUpdateData().setAssociationSelected(muAssoc.getName(), Boolean.TRUE);
      }
    }

    MockTestDataFactory mtdf = MockTestDataFactory.getInstance();
    InformationSystemRelease isr1 = mtdf.getIsrTestData();
    InformationSystemRelease isr2 = mtdf.getIsrTestData();
    InformationSystem is = mtdf.getInformationSystem();
    isr1.setId(Integer.valueOf(1));
    isr1.setInformationSystem(is);
    isr2.setId(Integer.valueOf(2));
    isr2.setInformationSystem(is);

    List<InformationSystemRelease> results = new ArrayList<InformationSystemRelease>();
    results.add(isr1);
    results.add(isr2);
    manageReportMemoryBean.setResults(results);
    manageReportMemoryBean.getQueryResult().setSelectedResultIds(new Integer[] { Integer.valueOf(1), Integer.valueOf(2) });

    List<String> associatons = userinput.getMassUpdateData().getSelectedAssociationsList();
    List<String> properties = userinput.getMassUpdateData().getSelectedPropertiesList();

    MockTestHelper.expect(
        massUpdateServiceMock.initComponentModel(MockTestHelper.isA(InformationSystemReleaseCmMu.class), eq(isr1), eq(properties), eq(associatons)))
        .andReturn(isr1);
    MockTestHelper.expect(
        massUpdateServiceMock.initComponentModel(MockTestHelper.isA(InformationSystemReleaseCmMu.class), eq(isr2), eq(properties), eq(associatons)))
        .andReturn(isr2);

    MockTestHelper.replay(massUpdateServiceMock);

    memoryBean.initFromMemoryBean(manageReportMemoryBean, massUpdateServiceMock);

    assertEquals(2, memoryBean.getMassUpdatePropertyConfig().size());
    assertEquals(2, memoryBean.getMassUpdateAssociationConfig().size());

    MockTestHelper.verify(massUpdateServiceMock);
  }

  @Test
  public void testSerialization() {
    MassUpdateMemoryBean originalMemBean = new MassUpdateMemoryBean(InformationSystemReleaseTypeMu.getInstance());
    MemBeanSerializationTestHelper.testSerializeDeserialize(originalMemBean);
  }

}
