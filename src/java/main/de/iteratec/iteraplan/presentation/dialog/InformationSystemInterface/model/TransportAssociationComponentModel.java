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
package de.iteratec.iteraplan.presentation.dialog.InformationSystemInterface.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.util.NamedId;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TransportInfo;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;


/**
 * This class is used for managing the non-standard transport association of interfaces. It extends
 * the ManyAssociationSetComponentModel and adds functionality to handle the direction of the
 * transport. Most fields of the ManyAssociationSetComponentModel are not initialized because this
 * association is fairly non-standard.
 */
public class TransportAssociationComponentModel extends ManyAssociationSetComponentModel<InformationSystemInterface, Transport> {

  /** Serialization version. */
  private static final long    serialVersionUID         = -933690077544280795L;

  private String               transportDirectionToAdd  = null;

  private Map<Integer, String> transportDirectionsToAdd = new HashMap<Integer, String>();

  public TransportAssociationComponentModel(ComponentMode componentMode) {
    super(componentMode, "transports", null, null, null, null, null);
  }

  @Override
  protected List<Transport> getAvailableElements(Integer id, List<Transport> connected) {
    return SpringServiceFactory.getTransportService().getAvailableTransports();
  }

  @Override
  protected Set<Transport> getConnectedElements(InformationSystemInterface source) {
    return source.getTransports();
  }

  @Override
  protected void setConnectedElements(InformationSystemInterface target, Set<Transport> toConnect) {
    final Set<Transport> existingTransports = Sets.newHashSet();
    final Set<Transport> newTransports = Sets.newHashSet();
    for (Transport transport : toConnect) {
      Transport existingTransport = lookUpConnectedTransportByBusinessObject(target.getTransports(), transport.getBusinessObject());
      if (existingTransport != null) {
        existingTransport.setDirection(transport.getDirection());
        existingTransports.add(existingTransport);
      } else {
        newTransports.add(transport);
      }
    }
    
    //delete 
    ImmutableSet<Transport> deletedTransports = Sets.difference(target.getTransports(), existingTransports).immutableCopy();
    target.getTransports().removeAll(deletedTransports);
    
    //add new
    for (Transport transport : newTransports) {
      Transport newTransport = BuildingBlockFactory.createTransport();
      newTransport.setDirection(transport.getDirection());
      
      BusinessObject businessObject = SpringServiceFactory.getBusinessObjectService().loadObjectById(transport.getBusinessObject().getId());
      newTransport.addBusinessObject(businessObject);
      
      target.addTransport(newTransport);
    }
  }

  @Override
  public List<Transport> getAvailableElementsPresentation() {
    List<Transport> availableElementsPresentation = new ArrayList<Transport>();
    Transport dummy = new Transport();
    dummy.setBusinessObject(new BusinessObject());
    availableElementsPresentation.add(dummy);
    availableElementsPresentation.addAll(getAvailableElements());
    
    return availableElementsPresentation;
  }

  @Override
  public void initializeFrom(InformationSystemInterface source) {
    super.initializeFrom(source);

    for (Transport t : getConnectedElements()) {
      t.setTransportDirection(t.getTransportKey());
    }
  }

  @Override
  public void update() {
    if (getElementIdToAdd() != null && getElementIdToAdd().intValue() > 0) {
      processElementIdToAdd();
      setElementIdToAdd(null);
      setTransportDirectionToAdd(null);
    }
    // Change direction of already connected business objects. This part is relevant when a transport is
    // edited in edit mode of an interface in the GUI. transportDirection is set by drop down selection.
      for (Transport t : getConnectedElements()) {
        configureTransport(t, t.getTransportDirection());
      }

    if ((getElementIdToRemove() != null) && (getElementIdToRemove().intValue() > 0)) {
      for (Iterator<Transport> it = getConnectedElements().iterator(); it.hasNext();) {
        Transport connected = it.next();
        Integer connectedId = connected.getId();
        if (getElementIdToRemove().equals(connectedId)) {
          it.remove();
          break;
        }
      }
      setElementIdToRemove(null);
    }

    if ((getElementIdsToAdd() != null) && (getElementIdsToAdd().length > 0)) {
      processElementIdsToAdd();
      setElementIdToAdd(null);
      setElementIdsToAdd(new Integer[0]);
      setTransportDirectionToAdd(null);
    }
  }

  private static Transport lookUpConnectedTransportByBusinessObject(Collection<Transport> transports, BusinessObject bo) {
    for (Transport transport : transports) {
      if (transport.getBusinessObject().getId().equals(bo.getId())) {
        return transport;
      }
    }
    
    return null;
  }

  /**
   * Configure or add a transport identified by its id to the TransportAssociationComponentModel
   */
  @Override
  protected void processElementIdToAdd() {
    for (Transport transport : getAvailableElements()) {
      Integer id = transport.getBusinessObject().getId();

      if (getElementIdToAdd().equals(id)) {
        // Check if there is already a transport of the same id connected to the component model.
        Transport connectedTransport = lookUpConnectedTransportByBusinessObject(getConnectedElements(), transport.getBusinessObject());

        if (connectedTransport != null) {
          configureTransport(connectedTransport, getTransportDirectionToAdd());
        }
        else {
          transport.setInformationSystemInterface(getManagedElement());
          configureTransport(transport, getTransportDirectionToAdd());
          getConnectedElements().add(transport);
        }
        break;
      }
    }
    
    Collections.sort(getConnectedElements());
  }

  /**
   * Configure or add multiple transports identified by their id to the TransportAssociationComponentModel
   */
  @Override
  protected void processElementIdsToAdd() {
    for (Integer elementIdToAdd : getElementIdsToAdd()) {
      setElementIdToAdd(elementIdToAdd);
      setTransportDirectionToAdd(getTransportDirectionsToAdd().get(elementIdToAdd));
      processElementIdToAdd();
    }
  }

  /**
   * Configures the transport direction of the transport belonging to a business object.
   * 
   * @param transport
   * @param direction
   */
  private void configureTransport(Transport transport, String direction) {
      if (direction.equals(Constants.BOTH_DIRECTIONS)) {
        transport.setDirection(Direction.BOTH_DIRECTIONS);
      }
      else if (direction.equals(Constants.FIRST_TO_SECOND)) {
        transport.setDirection(Direction.FIRST_TO_SECOND);
      }
      else if (direction.equals(Constants.SECOND_TO_FIRST)) {
        transport.setDirection(Direction.SECOND_TO_FIRST);
      }
      else {
        transport.setDirection(Direction.NO_DIRECTION);
      }
      transport.setTransportDirection(transport.getTransportKey());
  }

  public List<NamedId> getAvailableTransportDirections() {
    List<NamedId> atd = new ArrayList<NamedId>();
    atd.add(new NamedId(null, TransportInfo.NO_DIRECTION.getTextRepresentation(), TransportInfo.NO_DIRECTION.getGraphicRepresentation()));
    atd.add(new NamedId(null, TransportInfo.FIRST_TO_SECOND.getTextRepresentation(), TransportInfo.FIRST_TO_SECOND.getGraphicRepresentation()));
    atd.add(new NamedId(null, TransportInfo.SECOND_TO_FIRST.getTextRepresentation(), TransportInfo.SECOND_TO_FIRST.getGraphicRepresentation()));
    atd.add(new NamedId(null, TransportInfo.BOTH_DIRECTIONS.getTextRepresentation(), TransportInfo.BOTH_DIRECTIONS.getGraphicRepresentation()));
    
    return atd;
  }

  public String getTransportDirectionToAdd() {
    return transportDirectionToAdd;
  }

  public void setTransportDirectionToAdd(String transportDirectionToAdd) {
    this.transportDirectionToAdd = transportDirectionToAdd;
  }

  public Map<Integer, String> getTransportDirectionsToAdd() {
    return transportDirectionsToAdd;
  }

  public void setTransportDirectionsToAdd(Map<Integer, String> transportDirectionsToAdd) {
    this.transportDirectionsToAdd = transportDirectionsToAdd;
  }
}
