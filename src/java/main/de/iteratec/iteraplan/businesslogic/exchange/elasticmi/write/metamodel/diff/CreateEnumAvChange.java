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

import java.util.List;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.diff.message.AddLiteralMessage;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.elasticmi.messages.Message;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;


/**
 *Implementation class of the command pattern {@link AtsApplicableChange} create new {@link EnumAV}s in an {@link EnumAT}
 */
public class CreateEnumAvChange implements AtsApplicableChange {

  private String enumValue;
  private String atName;

  public CreateEnumAvChange(String enumValue, String atName) {
    super();
    this.enumValue = enumValue;
    this.atName = atName;
  }

  /**{@inheritDoc}**/
  @Override
  public void writeTo(AttributeTypeService attributeTypeService) {
    EnumAT updatedAT = (EnumAT) attributeTypeService.getAttributeTypeByName(atName);
    EnumAV av = new EnumAV();

    List<String> colors = SpringGuiFactory.getInstance().getAttributeColors();
    if (colors.size() > 0) {
      av.setDefaultColorHex(colors.iterator().next());
    }

    av.setName(enumValue);
    av.setAttributeTypeTwoWay(updatedAT);
    attributeTypeService.saveOrUpdate(updatedAT);
  }

  /**{@inheritDoc}**/
  @Override
  public int hashCode() {
    //31 is prime
    return 31 + (31 + getClass().hashCode() + ((this.atName == null) ? 0 : this.atName.hashCode()))
        * ((this.enumValue == null) ? 1 : this.enumValue.hashCode());
  }

  /**{@inheritDoc}**/
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof CreateEnumAvChange)) {
      return false;
    }
    CreateEnumAvChange other = (CreateEnumAvChange) obj;
    boolean namesAreSame = this.atName == null ? other.atName == null : this.atName.equals(other.atName);
    boolean enumValsAreSame = this.enumValue == null ? other.enumValue == null : this.enumValue.equals(other.enumValue);
    return namesAreSame && enumValsAreSame;
  }

  /**{@inheritDoc}**/
  @Override
  public Message getMessage() {
    return new AddLiteralMessage(enumValue, atName);
  }

}
