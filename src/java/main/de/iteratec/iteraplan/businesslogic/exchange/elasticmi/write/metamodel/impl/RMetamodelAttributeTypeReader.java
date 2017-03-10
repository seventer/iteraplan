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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.impl;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.LocalizedIteraplanMessage;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.AttributeTypeReader;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.util.BuildingBlockTypeNameMatcher;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.util.VirtualAttributeType;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.util.VirtualAttributeTypeCreator;
import de.iteratec.iteraplan.elasticmi.messages.Message.Severity;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.metamodel.read.REnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RNominalEnumerationExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.util.NamedUtil;


public class RMetamodelAttributeTypeReader extends AttributeTypeReader {

  private final RMetamodel      rMetamodel;
  private final MessageListener messageListener;

  public RMetamodelAttributeTypeReader(RMetamodel rMetamodel, MessageListener messageListener) {
    this.rMetamodel = rMetamodel;
    this.messageListener = messageListener;
  }

  /**{@inheritDoc}**/
  @Override
  public List<VirtualAttributeType> readVirtualAttributes() {
    Map<String, VirtualAttributeType> vats = Maps.newHashMap();
    for (RStructuredTypeExpression type : rMetamodel.getStructuredTypes()) {
      if (isValidStructuredType(type.getPersistentName())) {
        readVirtualAttributes(type, vats);
      }
      else if (!NamedUtil.areSame("InformationFlow", type.getPersistentName())) {
        this.messageListener.onMessage(new LocalizedIteraplanMessage(Severity.WARNING, MESSAGE_KEY_UNSUPPOERTED_STRUCTURED_TYPE, type
            .getPersistentName()));
      }
    }
    return Lists.newArrayList(vats.values());
  }

  private void readVirtualAttributes(RStructuredTypeExpression fromType, Map<String, VirtualAttributeType> vats) {
    for (RPropertyExpression property : fromType.getAllProperties()) {
      VirtualAttributeType vat = null;
      if (vats.get(property.getPersistentName()) != null) {
        vat = vats.get(property.getPersistentName());
      }
      else {
        vat = VirtualAttributeTypeCreator.createAttributeType(property);
        if (vat == null) {
          continue;
        }
        vats.put(property.getPersistentName(), vat);
        if (rMetamodel.getEnumerationTypes().contains(property.getType())) {
          RNominalEnumerationExpression enumeration = (RNominalEnumerationExpression) property.getType();
          for (REnumerationLiteralExpression literal : enumeration.getLiterals()) {
            vat.addEnumAV(literal.getPersistentName());
          }
        }
      }
      vat.addAssociatedToBB(BuildingBlockTypeNameMatcher.getTypeOfBuildingBlockForPersistentName(fromType.getPersistentName()));
    }
  }
}
