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
package de.iteratec.iteraplan.presentation.ajax;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.exchange.common.VbbVersion;
import de.iteratec.iteraplan.businesslogic.service.ElasticeamService;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;


/**
 *
 */
public class MetamodelExplorerServiceImpl implements MetamodelExplorerService {

  private ElasticeamService elasticeamService;

  public void setElasticeamService(ElasticeamService elasticeamService) {
    this.elasticeamService = elasticeamService;
  }

  /**{@inheritDoc}**/
  public List<PropertyExpressionDTO> getProperties(String uTypePersistentName) {
    if (VbbVersion.isVbb30()) {
      UniversalTypeExpression uType = this.elasticeamService.getMetamodel().findUniversalTypeByPersistentName(uTypePersistentName);
      if (uType == null) {
        return Collections.emptyList();
      }
      List<PropertyExpressionDTO> result = Lists.newLinkedList();
      for (PropertyExpression<?> property : uType.getProperties()) {
        PropertyExpressionDTO dto = new PropertyExpressionDTO();
        dto.setPersistentName(property.getPersistentName());
        dto.setLocalName(property.getName());
        dto.setLower(property.getLowerBound());
        dto.setUpper(property.getUpperBound());
        dto.setType(property.getType().getPersistentName());
        result.add(dto);
      }
      return result;
    }
    else {
      RMetamodel rMetamodel = ElasticMiContext.getCurrentContext().getContextMetamodel();
      RStructuredTypeExpression rType = rMetamodel.findStructuredTypeByPersistentName(uTypePersistentName);
      if (rType == null) {
        return Collections.emptyList();
      }
      List<PropertyExpressionDTO> result = Lists.newArrayList();
      for (RPropertyExpression property : rType.getAllProperties()) {
        PropertyExpressionDTO dto = new PropertyExpressionDTO();
        dto.setPersistentName(property.getPersistentName());
        dto.setLocalName(property.getName());
        dto.setLower(property.getLowerBound());
        dto.setUpper(property.getUpperBound());
        dto.setType(property.getType().getPersistentName());
        result.add(dto);
      }
      return result;
    }
  }

}
