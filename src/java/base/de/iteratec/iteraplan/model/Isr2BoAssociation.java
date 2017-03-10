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
import org.hibernate.search.annotations.Indexed;

import de.iteratec.iteraplan.common.util.Preconditions;


/**
 * An association class to connect a {@link InformationSystemRelease} with an {@link BusinessObject}.
 * The association entity is a building block itself, so that it can carry attributes, have access permissions
 * defined etc.
 * <p>
 * The implementation is rather straightforward, since the actual association and infrastructure code
 * is in {@link AbstractAssociation}. In order to avoid trouble with Hibernate, we need this generics-free
 * class where all types are known up-front.
 * <p>
 * In addition, this class provides easy-to-understand (non-generic) method names for getters and setters. This
 * should help avoid inconsistent use.
 */
@Entity
@Audited
@Indexed(index = "index.Isr2BoAssociation")
public class Isr2BoAssociation extends AbstractAssociation<InformationSystemRelease, BusinessObject> {

  private static final long serialVersionUID = -8749431290135232380L;

  public Isr2BoAssociation() {
    super();
    // no-arg constructor
  }

  /**
   * Creates a new association object and assigns its two ends. However, the connected objects are not updated,
   * i.e. it is the caller's responsibility to add the association object to the respective sets of
   * InfrastructureElement and TechnicalComponentRelease.
   * @param isr The Technical Component Release that this association should link.
   * @param bo The Infrastructure Element that this association should link.
   */
  public Isr2BoAssociation(InformationSystemRelease isr, BusinessObject bo) {
    this();
    this.setLeftEnd(isr);
    this.setRightEnd(bo);
  }

  /**
   * Returns the {@link InformationSystemRelease} from this association.
   */
  public InformationSystemRelease getInformationSystemRelease() {
    return getLeftEnd();
  }

  /**
   * Sets the {@link InformationSystemRelease} object on this association. The InformationSystemRelease object is not changed.
   */
  public void setInformationSystemRelease(InformationSystemRelease isr) {
    this.setLeftEnd(isr);
  }

  /**
   * Sets the {@link InformationSystemRelease} object on this association and adds
   * this association to the association set at the InformationSystemRelease.
   */
  public void setInformationSystemReleaseTwoWay(InformationSystemRelease isr) {
    this.setInformationSystemRelease(isr);
    isr.getBusinessObjectAssociations().add(this);
  }

  /**
   * Returns the {@link BusinessObject} from this association.
   */
  public BusinessObject getBusinessObject() {
    return getRightEnd();
  }

  /**
   * Sets the {@link BusinessObject} object on this association. The Business object is not changed.
   */
  public void setBusinessObject(BusinessObject bo) {
    this.setRightEnd(bo);
  }

  /**
   * Sets the {@link BusinessObject} object on this association and adds
   * this association to the association set at the BusinessObject.
   */
  public void setBusinessObjectTwoWay(BusinessObject bo) {
    this.setBusinessObject(bo);
    bo.getInformationSystemReleaseAssociations().add(this);
  }

  /**
   * Returns the {@link TypeOfBuildingBlock} descriptor for this object. Is always {@link TypeOfBuildingBlock#ISR2BOASSOCIATION} for this class.
   */
  @Override
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.ISR2BOASSOCIATION;
  }

  @Override
  public void connect() {
    Preconditions.checkNotNull(getInformationSystemRelease());
    Preconditions.checkNotNull(getBusinessObject());
    getInformationSystemRelease().getBusinessObjectAssociations().add(this);
    getBusinessObject().getInformationSystemReleaseAssociations().add(this);
  }

  @Override
  public void disconnect() {
    Preconditions.checkNotNull(getInformationSystemRelease());
    Preconditions.checkNotNull(getBusinessObject());
    if (getInformationSystemRelease() != null) {
      disconnectFromElementInCollection(getInformationSystemRelease(), getInformationSystemRelease().getBusinessObjectAssociations());
    }
    if (getBusinessObject() != null) {
      disconnectFromElementInCollection(getBusinessObject(), getBusinessObject().getInformationSystemReleaseAssociations());
    }
  }

}