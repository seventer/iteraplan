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
package de.iteratec.iteraplan.presentation.dialog;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

import java.util.List;

import org.easymock.EasyMock;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QFirstLevel;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QPart;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QUserInput;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.InitFormHelperService;
import de.iteratec.iteraplan.businesslogic.service.RefreshHelperService;


public class GuiSearchControllerTestHelper {

  private InitFormHelperService initFormHelperService = EasyMock.createNiceMock(InitFormHelperService.class);

  private RefreshHelperService  refreshHelperService  = EasyMock.createNiceMock(RefreshHelperService.class);

  private AttributeTypeService  attributeTypeService  = EasyMock.createNiceMock(AttributeTypeService.class);

  @SuppressWarnings("unchecked")
  public void setUp() {

    DynamicQueryFormData<?> queryFormData = createNiceMock(DynamicQueryFormData.class);
    EasyMock.<DynamicQueryFormData<?>> expect(initFormHelperService.getReportForm((Type<?>) anyObject())).andReturn(queryFormData);

    QUserInput userInput = createNiceMock(QUserInput.class);
    expect(queryFormData.getQueryUserInput()).andReturn(userInput);

    List<QFirstLevel> firstLevelList = createNiceMock(List.class);
    expect(userInput.getQueryFirstLevels()).andReturn(firstLevelList);

    QFirstLevel firstLevel = createNiceMock(QFirstLevel.class);
    expect(firstLevelList.get(0)).andReturn(firstLevel);

    List<QPart> qpartList = createNiceMock(List.class);
    expect(firstLevel.getQuerySecondLevels()).andReturn(qpartList);

    QPart qpart = createNiceMock(QPart.class);
    expect(qpartList.get(0)).andReturn(qpart);

    qpart.setChosenAttributeStringId("name");
    expectLastCall();
    qpart.setChosenOperationId(Integer.valueOf(1));
    expectLastCall();
    qpart.setFreeTextCriteriaSelected(Boolean.TRUE);
    expectLastCall();
    qpart.setFreeTextCriteria((String) anyObject());
    expectLastCall();
    qpart.setExistingCriteria((String) anyObject());
    expectLastCall();
    qpart.setDateATSelected(Boolean.FALSE);
    expectLastCall();

    expect(queryFormData.getQueryUserInput()).andReturn(userInput);

    QPart qpartSecond = createNiceMock(QPart.class);
    expect(qpartList.get(0)).andReturn(qpartSecond);

    qpartSecond.setChosenAttributeStringId("costs");
    expectLastCall();
    qpart.setChosenOperationId(Integer.valueOf(9));
    expectLastCall();
    qpart.setFreeTextCriteriaSelected(Boolean.TRUE);
    expectLastCall();
    qpart.setFreeTextCriteria((String) anyObject());
    expectLastCall();
    qpart.setExistingCriteria((String) anyObject());
    expectLastCall();
    qpart.setDateATSelected(Boolean.FALSE);
    expectLastCall();

    qpartList.add(0, qpart);
    qpartList.add(1, qpartSecond);

    firstLevel.setQuerySecondLevels(qpartList);
    expectLastCall();

    firstLevelList.add(0, firstLevel);
    expectLastCall();

    refreshHelperService.refreshForm((DynamicQueryFormData<?>) anyObject());
    expectLastCall();

    replay(initFormHelperService, queryFormData, userInput, firstLevelList, firstLevel, qpartList, qpart, refreshHelperService);
  }

  protected InitFormHelperService getInitFormHelperService() {
    return initFormHelperService;
  }

  protected RefreshHelperService getRefreshHelperService() {
    return refreshHelperService;
  }

  protected AttributeTypeService getAttributeTypeService() {
    return attributeTypeService;
  }

}
