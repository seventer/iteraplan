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
package de.iteratec.iteraplan.businesslogic.service;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QFirstLevel;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QPart;
import de.iteratec.iteraplan.common.util.NamedId;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.presentation.PresentationHelper;


/**
 * Implements {@link RefreshHelperService}
 * 
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 * @see RefreshHelperService
 */
public class RefreshHelperServiceImpl extends AbstractService implements RefreshHelperService {
  private QueryService queryService;

  public void setQueryService(QueryService queryService) {
    this.queryService = queryService;
  }

  /** {@inheritDoc} */
  public void refreshAllForms(List<DynamicQueryFormData<?>> queryForms) {
    for (DynamicQueryFormData<?> queryForm : queryForms) {
      refreshForm(queryForm);
    }
  }

  /** {@inheritDoc} */
  public void refreshForm(DynamicQueryFormData<?> queryForm) {
    Map<String, List<NamedId>> availableAttributeValues = Maps.newHashMap();
    for (QFirstLevel qfl : queryForm.getQueryUserInput().getQueryFirstLevels()) {
      for (QPart qp : qfl.getQuerySecondLevels()) {
        String attrId = qp.getChosenAttributeStringId();
        BBAttribute attribute = queryForm.getBBAttributeByStringId(attrId);
        if (attribute == null) {
          continue;
        }

        if (!availableAttributeValues.containsKey(attribute.getStringId())) {
          List<String> newAttrValList = queryService.getAttributeValuesForAttribute(queryForm.getType(), attribute);
          List<NamedId> newAttrValIdList = PresentationHelper.convertStringsToNamedIds(newAttrValList);
          availableAttributeValues.put(attribute.getStringId(), newAttrValIdList);
          if (newAttrValIdList.isEmpty() || (qp.getFreeTextCriteria() != null && !qp.getFreeTextCriteria().isEmpty())) {
            qp.setFreeTextCriteriaSelected(Boolean.TRUE);
          }
          else {
            qp.setFreeTextCriteriaSelected(Boolean.FALSE);
          }
        }
      }
    }

    queryForm.setAvailableAttributeValues(availableAttributeValues);

    if (queryForm.getSecondLevelQueryForms() != null) {
      refreshAllForms(queryForm.getSecondLevelQueryForms());
    }
  }

  /**{@inheritDoc}**/
  public void refreshTimeseriesQuery(QueryResult result) {
    Map<String, List<NamedId>> availableAttributeValues = Maps.newHashMap();
    QPart qp = result.getTimeseriesQuery().getPart();
    String attrId = qp.getChosenAttributeStringId();
    DynamicQueryFormData<?> queryForm = result.getQueryForms().get(0);
    BBAttribute attribute = queryForm.getBBAttributeByStringId(attrId);
    if (attribute != null) {
      List<String> newAttrValList = queryService.getAttributeValuesForAttribute(queryForm.getType(), attribute);
      List<NamedId> newAttrValIdList = PresentationHelper.convertStringsToNamedIds(newAttrValList);
      availableAttributeValues.put(attribute.getStringId(), newAttrValIdList);
      if (newAttrValIdList.isEmpty() || (qp.getFreeTextCriteria() != null && !qp.getFreeTextCriteria().isEmpty())) {
        qp.setFreeTextCriteriaSelected(Boolean.TRUE);
      }
      else {
        qp.setFreeTextCriteriaSelected(Boolean.FALSE);
      }
    }
    result.getTimeseriesQuery().setAvailableAttributeValues(availableAttributeValues);
  }

}