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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.EnumAT;


/**
 *The {@link VirtualAttributeType} is a simplified version of an {@link AttributeType} that only contains the changes which are actually imported
 */
public class VirtualAttributeType {

  private Class<? extends AttributeType> atType;
  private String                         atName;
  private boolean                        mandatory;
  private boolean                        multivalue;
  private Set<TypeOfBuildingBlock>       associatedToBB;
  private Set<String>                    enumAVs;

  public VirtualAttributeType(Class<? extends AttributeType> atType, String atName, boolean mandatory, boolean multivalue) {
    this.atType = atType;
    this.atName = atName;
    this.mandatory = mandatory;
    this.multivalue = multivalue;
    this.associatedToBB = new HashSet<TypeOfBuildingBlock>();
    this.enumAVs = new HashSet<String>();
  }

  public Class<? extends AttributeType> getAtType() {
    return atType;
  }

  public String getAtName() {
    return atName;
  }

  public boolean isMandatory() {
    return mandatory;
  }

  public boolean isMultivalue() {
    return multivalue;
  }

  public Set<TypeOfBuildingBlock> getAssociatedToBB() {
    return Collections.unmodifiableSet(associatedToBB);
  }

  public Set<String> getEnumAV() {
    return Collections.unmodifiableSet(enumAVs);
  }

  public void addEnumAV(String enumAV) {
    if (EnumAT.class.isAssignableFrom(this.getAtType())) {
      this.enumAVs.add(enumAV);
    }
    else {
      throw new UnsupportedOperationException("AttributeType is not a EnumAT");
    }
  }

  public void addAssociatedToBB(TypeOfBuildingBlock typeOfBuildingBlock) {
    this.associatedToBB.add(typeOfBuildingBlock);
  }

  /**{@inheritDoc}**/
  @Override
  public int hashCode() {
    //31 is prime
    return 31 * ((atName == null) ? getClass().hashCode() : getClass().hashCode() + atName.hashCode());
  }

  /**{@inheritDoc}**/
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof VirtualAttributeType)) {
      return false;
    }
    VirtualAttributeType other = (VirtualAttributeType) obj;
    return this.atName == null ? other.atName == null : this.atName.equals(other.atName);
  }

  /**
   * @param aType
   */
  public void setAtType(Class<? extends AttributeType> aType) {
    this.atType = aType;
  }

}
