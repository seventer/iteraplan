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
package de.iteratec.iteraplan.model;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.util.Preconditions;


/**
 * This class represents the table "transport" in the database.
 */
@Entity
@Audited
@edu.umd.cs.findbugs.annotations.SuppressWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
public class Transport extends AbstractAssociation<InformationSystemInterface, BusinessObject> {

  private static final long serialVersionUID = -8259663818811630232L;

  //used to set the transport direction when a transport is added to an interface
  private Direction         direction        = Direction.NO_DIRECTION;

  //used to directly edit the transport direction in edit mode of a building block by drop down selection
  private String            transportDirection;

  public Transport() {
    super();
  }

  @Override
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.TRANSPORT;
  }

  public BusinessObject getBusinessObject() {
    return this.getRightEnd();
  }

  public void setBusinessObject(BusinessObject bo) {
    this.setRightEnd(bo);
  }

  public InformationSystemInterface getInformationSystemInterface() {
    if (getLeftEnd() != null) {
      this.getLeftEnd().addDirection();
    }

    return this.getLeftEnd();
  }

  public void setInformationSystemInterface(InformationSystemInterface isi) {
    this.setLeftEnd(isi);
  }

  public void addBusinessObject(BusinessObject bo) {
    Preconditions.checkNotNull(bo);
    bo.addTransport(this);
  }

  /**
   * Since no fields of a Transport object can be changed, only the id is sufficient for the
   * identity String.
   */
  public String getIdentityString() {
    if (this.getId() == null) {
      return "";
    }
    return this.getId().toString();
  }

  @Override
  public int compareTo(BuildingBlock o) {
    Transport other = (Transport) o;
    return this.getBusinessObject().getName().compareToIgnoreCase(other.getBusinessObject().getName());
  }

  /**
   * chooses the fitting transportInfo enum for the Boolean-encoded direction
   * 
   * @return TransportInfo enum
   */
  public TransportInfo getTransportInfo() {
    if (Direction.BOTH_DIRECTIONS.equals(direction)) {
      return TransportInfo.BOTH_DIRECTIONS;
    }
    else if (Direction.FIRST_TO_SECOND.equals(direction)) {
      return TransportInfo.FIRST_TO_SECOND;
    }
    else if (Direction.SECOND_TO_FIRST.equals(direction)) {
      return TransportInfo.SECOND_TO_FIRST;
    }
    else {
      return TransportInfo.NO_DIRECTION;
    }
  }

  public String getTransportKey() {
    assert getInformationSystemInterface() != null;
    boolean fromTo = true;
    if ((getInformationSystemInterface().getId() != null) && (getInformationSystemInterface().getReferenceRelease() != null)) {
      fromTo = getInformationSystemInterface().getReferenceRelease().getId()
          .equals(getInformationSystemInterface().getInformationSystemReleaseA().getId());
    }
    return calculateTransportKey(fromTo);
  }

  public String calculateTransportKey(boolean fromTo) {
    if (Direction.BOTH_DIRECTIONS.equals(direction)) {
      return Constants.BOTH_DIRECTIONS;
    }
    else if (Direction.FIRST_TO_SECOND.equals(direction)) {
      if (fromTo) {
        return Constants.FIRST_TO_SECOND;
      }
      else {
        return Constants.SECOND_TO_FIRST;
      }
    }
    else if (Direction.SECOND_TO_FIRST.equals(direction)) {
      if (fromTo) {
        return Constants.SECOND_TO_FIRST;
      }
      else {
        return Constants.FIRST_TO_SECOND;
      }
    }
    else {
      return Constants.NO_DIRECTION;
    }
  }

  /**
   * @deprecated please use the {@link #getDirection()} method
   */
  @Deprecated
  public String getTransportDirection() {
    return transportDirection;
  }

  /**
   * @deprecated please use the {@link #setDirection(Direction)} method
   */
  @Deprecated
  public void setTransportDirection(String transportDirection) {
    this.transportDirection = transportDirection;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  /**
   * Transports currently have no description
   */
  @Override
  public String getDescription() {
    return "";
  }

  @Override
  public void connect() {
    Preconditions.checkNotNull(getInformationSystemInterface());
    Preconditions.checkNotNull(getBusinessObject());
    getInformationSystemInterface().getTransports().add(this);
    getBusinessObject().getTransports().add(this);
  }

  @Override
  public void disconnect() {
    //Note: needs to be done this way, since the hashCode of the transports
    //in the persistent sets is different than the actual hashCode
    //of the transport objects, i.e. Set.remove does not work.
    if (getInformationSystemInterface() != null) {
      disconnectFromElementInCollection(getInformationSystemInterface(), getInformationSystemInterface().getTransports());
    }
    if (getBusinessObject() != null) {
      disconnectFromElementInCollection(getBusinessObject(), getBusinessObject().getTransports());
    }
  }

}