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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.diff;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.diff.message.CreateAttributeTypeMessage;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.util.VirtualAttributeType;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticmi.messages.Message;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.MultiassignementType;


/**
 *Implementation class of the command pattern {@link AtsApplicableChange} to create a new {@link AttributeType}
 */
public class CreateAttributeTypeChange implements AtsApplicableChange {

  private final VirtualAttributeType virtualAttributeType;
  private final AttributeTypeGroup   defaultATG;

  public CreateAttributeTypeChange(VirtualAttributeType attributeType, AttributeTypeGroup defaultATG) {
    super();
    this.virtualAttributeType = attributeType;
    this.defaultATG = defaultATG;
  }

  /**{@inheritDoc}**/
  @Override
  public void writeTo(AttributeTypeService attributeTypeService) {
    //skip multi-value string attributes (responsibility)
    if (!MultiassignementType.class.isAssignableFrom(virtualAttributeType.getAtType()) && virtualAttributeType.isMultivalue()) {
      return;
    }
    AttributeType createdAT;
    try {
      //every Implementation of an AttributeType has a Default Constructer
      //      we can use this per reflection to create a instance, rather than creating a huge if-else statement
      createdAT = virtualAttributeType.getAtType().newInstance();
      createdAT.setAttributeTypeGroupTwoWay(defaultATG);

    } catch (InstantiationException e) {
      //Dead programs don't tell lies
      throw new IteraplanTechnicalException(e);
    } catch (IllegalAccessException e) {
      //Dead programs don't tell lies
      throw new IteraplanTechnicalException(e);
    }
    createdAT.setName(virtualAttributeType.getAtName());
    createdAT.setMandatory(virtualAttributeType.isMandatory());

    if (createdAT instanceof MultiassignementType) {
      ((MultiassignementType) createdAT).setMultiassignmenttype(virtualAttributeType.isMultivalue());
    }
    attributeTypeService.saveOrUpdate(createdAT);
  }

  /**{@inheritDoc}**/
  @Override
  public int hashCode() {
    return 31 * ((virtualAttributeType == null) ? getClass().hashCode() : getClass().hashCode() + virtualAttributeType.hashCode());
  }

  /**{@inheritDoc}**/
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof CreateAttributeTypeChange)) {
      return false;
    }
    CreateAttributeTypeChange other = (CreateAttributeTypeChange) obj;
    return this.virtualAttributeType == null ? other.virtualAttributeType == null : this.virtualAttributeType.equals(other.virtualAttributeType);
  }

  /**{@inheritDoc}**/
  @Override
  public Message getMessage() {
    return new CreateAttributeTypeMessage(this.virtualAttributeType.getAtName(), this.virtualAttributeType.isMandatory(),
        this.virtualAttributeType.isMultivalue(), this.virtualAttributeType.getAtType());
  }
}
