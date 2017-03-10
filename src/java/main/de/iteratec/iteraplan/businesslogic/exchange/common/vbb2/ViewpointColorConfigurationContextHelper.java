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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb2;

import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQlQuery;
import de.iteratec.iteraplan.elasticmi.metamodel.common.impl.atomic.AtomicDataType;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RNominalEnumerationExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;


/**
 * Wraps a {@link ViewpointColorConfiguration} and takes care of the resolution of
 * elements from diverse contexts (elasticMi context, attributeTypeSerivce, spring gui factory).
 * Takes care to only create color configuration for supported data types.
 * 
 */
public class ViewpointColorConfigurationContextHelper {

  private final AttributeTypeService atService;

  public ViewpointColorConfigurationContextHelper(AttributeTypeService atService) {
    this.atService = atService;
  }

  public Map<String, String> createColorConfiguration(String typePersistentName, String propertyPersistentName) {
    IteraQlQuery query = ElasticMiContext.getCurrentContext().compile(typePersistentName + ";");
    if (query.isRight()) {
      return Maps.newHashMap();
    }
    RStructuredTypeExpression type = query.getLeft();
    RPropertyExpression property = type.findPropertyByPersistentName(propertyPersistentName);
    if (property == null) {
      return Maps.newHashMap();
    }

    if (property.getType() instanceof RNominalEnumerationExpression) {
      return new ViewpointColorConfiguration().createEnumColorConfig((RNominalEnumerationExpression) property.getType(),
          "typeOfStatus".equals(property.getPersistentName()), Lists.newArrayList(SpringGuiFactory.getInstance().getVbbClusterColors()));
    }
    else if (AtomicDataType.DECIMAL.type().equals(property.getType())) {
      AttributeType at = atService.getAttributeTypeByName(propertyPersistentName);
      if (!(at instanceof NumberAT)) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }
      return new ViewpointColorConfiguration().createDecimalColorConfig(property, (NumberAT) at);
    }
    return Maps.newHashMap();
  }

}
