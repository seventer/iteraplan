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
package de.iteratec.iteraplan.model.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import junit.framework.TestCase;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry.COLUMN_TYPE;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportBeanBase;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ViewConfiguration;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QFirstLevel;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QPart;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.NamedId;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.xml.query.BooleanKeyValueXML;
import de.iteratec.iteraplan.model.xml.query.QPartXML;
import de.iteratec.iteraplan.model.xml.query.QStatusXML.TypeOfStatusXML;
import de.iteratec.iteraplan.model.xml.query.QueryFormXML;
import de.iteratec.iteraplan.model.xml.query.TypeXML;


public class ReportXMLTest extends TestCase {

  public void testInitFrom() {

    ReportXML reportXML = new ReportXML();
    ManageReportMemoryBean memBean = new ManageReportMemoryBean();

    List<BBAttribute> attributes = new ArrayList<BBAttribute>();
    BBAttribute attr1 = new BBAttribute(Integer.valueOf(1), BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE, "myEnum", "someEnumName");
    BBAttribute attr2 = new BBAttribute(Integer.valueOf(2), BBAttribute.USERDEF_NUMBER_ATTRIBUTE_TYPE, "myNumber", "someNumberName");
    attributes.add(attr1);
    attributes.add(attr2);
    NamedId enumVal1 = new NamedId();
    enumVal1.setId(Integer.valueOf(10));
    enumVal1.setName("enumVal1");
    NamedId enumVal2 = new NamedId();
    enumVal2.setId(Integer.valueOf(11));
    enumVal2.setName("enumVal2");
    List<NamedId> lst = new ArrayList<NamedId>();
    lst.add(enumVal1);
    lst.add(enumVal2);

    DynamicQueryFormData<InformationSystemRelease> form = new DynamicQueryFormData<InformationSystemRelease>(attributes,
        InformationSystemReleaseTypeQu.getInstance(), Locale.GERMAN);
    List<QFirstLevel> firstLevels = new ArrayList<QFirstLevel>();
    QFirstLevel firstLevel = new QFirstLevel();
    List<QPart> qSecondLevels = new ArrayList<QPart>();
    QPart qSecondLevel = new QPart();
    qSecondLevel.setChosenAttributeStringId(attr1.getStringId());
    qSecondLevel.setChosenOperationId(Constants.OPERATION_GT_ID);
    qSecondLevels.add(qSecondLevel);
    firstLevel.setQuerySecondLevels(qSecondLevels);
    assertEquals("userdefEnum_someEnumName_1", qSecondLevel.getChosenAttributeStringId());
    firstLevels.add(firstLevel);
    form.getQueryUserInput().setQueryFirstLevels(firstLevels);
    List<DynamicQueryFormData<?>> queryForms = new ArrayList<DynamicQueryFormData<?>>();
    queryForms.add(form);
    memBean.setQueryForms(queryForms, InformationSystemReleaseTypeQu.getInstance().getExtensionsForPresentation());

    // add view configuration
    ViewConfiguration config = new ViewConfiguration(Locale.GERMANY);
    List<ColumnEntry> visibleColumns = new ArrayList<ColumnEntry>();
    visibleColumns.add(new ColumnEntry("someField", COLUMN_TYPE.ATTRIBUTE, "head"));
    config.setVisibleColumns(visibleColumns);
    memBean.setViewConfiguration(config);
    List<BBAttribute> tsAttributes = Collections.emptyList();
    UserContext.setCurrentUserContext(new UserContext("blubb", new HashSet<Role>(), Locale.ENGLISH, new User()));
    memBean.getQueryResult().setTimeseriesQuery(new TimeseriesQuery(tsAttributes));

    reportXML.initFrom(memBean, Locale.GERMAN);

    assertEquals(1, reportXML.getQueryResults().size());
    assertEquals(ManageReportBeanBase.MAIN_QUERY, reportXML.getQueryResults().get(0).getQueryName());
    assertEquals(1, reportXML.getQueryResults().get(0).getQueryForms().size());
    QueryFormXML formXML = reportXML.getQueryResults().get(0).getQueryForms().get(0);
    assertEquals(TypeXML.INFORMATIONSYSTEMRELEASE, formXML.getTypeXML());
    assertEquals(InformationSystemReleaseTypeQu.getInstance(), formXML.getType());
    assertNotNull(formXML.getQueryUserInput().getStatusQueryData());
    assertEquals(TypeOfStatusXML.INFORMATIONSYSTEMRELEASE, formXML.getQueryUserInput().getStatusQueryData().getTypeOfStatus());
    assertEquals(4, formXML.getQueryUserInput().getStatusQueryData().getValues().size());
    Set<String> statusSet = new HashSet<String>();

    for (BooleanKeyValueXML val : formXML.getQueryUserInput().getStatusQueryData().getValues()) {
      statusSet.add(val.getKey());
    }
    for (TypeOfStatus status : TypeOfStatus.values()) {
      assertTrue(statusSet.contains(status.getValue()));
    }
    assertEquals(1, formXML.getQueryUserInput().getQueryFirstLevels().size());
    assertEquals(1, formXML.getQueryUserInput().getQueryFirstLevels().get(0).getQuerySecondLevels().size());
    QPartXML part = formXML.getQueryUserInput().getQueryFirstLevels().get(0).getQuerySecondLevels().get(0);
    assertEquals("userdefEnum_someEnumName_1", part.getChosenAttributeStringId());
    assertNotNull(reportXML.getVisibleColumns());
    assertEquals(1, reportXML.getVisibleColumns().size());
  }
}
