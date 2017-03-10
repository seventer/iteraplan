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

import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.NamedId;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.common.util.StringUtil;


/**
 * An interface, where appropriately directed, defines a dependency between two
 * {@link InformationSystemRelease}s. It is separable into two areas, information and control flow.
 * In the context of architecture management the term "interface" is used in the sense of
 * "information flow" between information systems.
 */
@Entity
@Audited
@Indexed(index = "index.InformationSystemInterface")
public class InformationSystemInterface extends BuildingBlock {

  private static final long              serialVersionUID           = -854556813876919213L;

  @Field(store = Store.YES)
  private String                         name                       = "";

  @Field(store = Store.YES)
  private String                         direction                  = "";

  private String                         directionBA                = "";

  private Map<Object, Object>            isiKey                     = new HashMap<Object, Object>();
  private Map<Object, Object>            isiKeyBA                   = new HashMap<Object, Object>();

  private Direction                      interfaceDirection         = Direction.NO_DIRECTION;

  @Field(store = Store.YES)
  private String                         description;

  @IndexedEmbedded
  private InformationSystemRelease       informationSystemReleaseA;
  @IndexedEmbedded
  private InformationSystemRelease       informationSystemReleaseB;

  private Set<Transport>                 transports                 = hashSet();
  private Set<TechnicalComponentRelease> technicalComponentReleases = hashSet();

  /**
   * Defines a reference {@link InformationSystemRelease} to extract the transport information for
   * that release. This field is not persisted.
   */
  private InformationSystemRelease       referenceRelease;

  public InformationSystemInterface() {
    // No-arg constructor.
  }

  @Override
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  public InformationSystemRelease getInformationSystemReleaseA() {
    return this.informationSystemReleaseA;
  }

  public InformationSystemRelease getInformationSystemReleaseB() {
    return this.informationSystemReleaseB;
  }

  public Set<TechnicalComponentRelease> getTechnicalComponentReleases() {
    return technicalComponentReleases;
  }

  public Set<Transport> getTransports() {
    return this.transports;
  }

  public InformationSystemRelease getReferenceRelease() {
    return referenceRelease;
  }

  public String getName() {
    if (name != null) {
      return name;
    }
    else {
      name = "";
      return name;
    }
  }

  public void setName(String stringToSet) {
    if (stringToSet == null) {
      name = "";
    }
    else {
      this.name = StringUtils.trim(StringUtil.removeIllegalXMLChars(stringToSet));
    }
  }

  public void setDescription(String description) {
    this.description = StringUtil.removeIllegalXMLChars(description);
  }

  public void setInformationSystemReleaseA(InformationSystemRelease releaseA) {
    this.informationSystemReleaseA = releaseA;
  }

  public void setInformationSystemReleaseB(InformationSystemRelease releaseB) {
    this.informationSystemReleaseB = releaseB;
  }

  public void setTransports(Set<Transport> transports) {
    this.transports = transports;
  }

  public void setTechnicalComponentReleases(Set<TechnicalComponentRelease> set) {
    this.technicalComponentReleases = set;
  }

  public void setReferenceRelease(InformationSystemRelease isr) {
    this.referenceRelease = isr;
  }

  /**
   * Connects two {@link InformationSystemRelease}s via this interface. Updates both sides of the
   * association.
   */
  public void connect(InformationSystemRelease a, InformationSystemRelease b) {
    Preconditions.checkNotNull(a);
    Preconditions.checkNotNull(b);

    setInformationSystemReleaseA(a);
    a.getInterfacesReleaseA().add(this);

    setInformationSystemReleaseB(b);
    b.getInterfacesReleaseB().add(this);
  }

  /**
   * Adds a {@link TechnicalComponentRelease}. Updates both sides of the association.
   */
  public void addTechnicalComponentRelease(TechnicalComponentRelease rel) {
    Preconditions.checkNotNull(rel);
    technicalComponentReleases.add(rel);
    rel.getInformationSystemInterfaces().add(this);
  }

  /**
   * Adds each element of the given set of {@link TechnicalComponentRelease}s. Updates both sides of
   * the association. Neither the set nor an element in the set must be {@code null}.
   */
  public void addTechnicalComponentReleases(Collection<TechnicalComponentRelease> set) {
    Preconditions.checkContentsNotNull(set);
    for (TechnicalComponentRelease rel : set) {
      technicalComponentReleases.add(rel);
      rel.getInformationSystemInterfaces().add(this);
    }
  }

  /**
   * Makes an {@link InformationSystemRelease} the release A of the connection. Updates both sides
   * of the association.
   */
  public void addReleaseA(InformationSystemRelease rel) {
    Preconditions.checkNotNull(rel);
    setInformationSystemReleaseA(rel);
    rel.getInterfacesReleaseA().add(this);
  }

  /**
   * Makes an {@link InformationSystemRelease} the release B of the connection. Updates both sides
   * of the association.
   */
  public void addReleaseB(InformationSystemRelease rel) {
    Preconditions.checkNotNull(rel);
    setInformationSystemReleaseB(rel);
    rel.getInterfacesReleaseB().add(this);
  }

  /**
   * Adds a {@link Transport}. Updates both sides of the association.
   */
  public void addTransport(Transport transport) {
    transports.add(transport);
    transport.setInformationSystemInterface(this);
  }

  /**
   * Adds the specified collection of {@link Transport}s. Updates both sides of the association.
   */
  public void addTransports(Collection<Transport> transportsToAdd) {
    Preconditions.checkContentsNotNull(transportsToAdd);
    for (Transport transport : transportsToAdd) {
      transports.add(transport);
      transport.setInformationSystemInterface(this);
    }
  }

  /**
   * Remove all {@link Transport}s. Updates both sides of the association.
   */
  public void removeTransports() {
    for (Transport transport : transports) {
      transport.setInformationSystemInterface(null);
    }
    transports.clear();
  }

  /**
   * Remove all {@link TechnicalComponentRelease}s. Updates both sides of the association.
   */
  public void removeTechnicalComponentReleases() {
    for (TechnicalComponentRelease rel : technicalComponentReleases) {
      rel.getInformationSystemInterfaces().remove(this);
    }
    technicalComponentReleases.clear();
  }

  /**
   * @return Returns a list of all associated {@link TechnicalComponentRelease}s of this connection
   *         that is sorted by the contained items' release name.
   */
  public List<TechnicalComponentRelease> getTechnicalComponentReleasesSorted() {

    // Throws LazyInitException
    List<TechnicalComponentRelease> list = new ArrayList<TechnicalComponentRelease>(technicalComponentReleases);

    Collections.sort(list);
    return list;
  }

  /**
   * Used by the interfaces table
   * @return direction transport of the interface
   */
  public String getDirection() {
    addDirection();
    return direction;
  }

  /**
   * Used by the interfaces excel importer
   * @param direction
   */
  public void setDirection(String direction) {
    this.direction = direction;

    if (direction == null) {
      this.interfaceDirection = Direction.NO_DIRECTION;
    }
    else if (direction.equals(TransportInfo.BOTH_DIRECTIONS.getTextRepresentation())) {
      this.interfaceDirection = Direction.BOTH_DIRECTIONS;
    }
    else if (direction.equals(TransportInfo.FIRST_TO_SECOND.getTextRepresentation())) {
      this.interfaceDirection = Direction.FIRST_TO_SECOND;
    }
    else if (direction.equals(TransportInfo.SECOND_TO_FIRST.getTextRepresentation())) {
      this.interfaceDirection = Direction.SECOND_TO_FIRST;
    }
    else if (direction.equals(TransportInfo.NO_DIRECTION.getTextRepresentation())) {
      this.interfaceDirection = Direction.NO_DIRECTION;
    }
  }

  public void addDirection() {
    direction = interfaceDirection.getValue();
  }

  /**
   * Used by the interfaces table under information systems
   * @return directionBA flow direction from IS A to IS B
   */
  public String getDirectionBA() {
    getDirection();
    if (direction.equals(TransportInfo.FIRST_TO_SECOND.getTextRepresentation())) {
      directionBA = TransportInfo.SECOND_TO_FIRST.getTextRepresentation();
    }
    else if (direction.equals(TransportInfo.SECOND_TO_FIRST.getTextRepresentation())) {
      directionBA = TransportInfo.FIRST_TO_SECOND.getTextRepresentation();
    }
    else {
      directionBA = direction;
    }

    return directionBA;
  }

  public void setDirectionBA(String directionBA) {
    this.directionBA = directionBA;
  }

  public Map<Object, Object> getIsiKey() {

    String directionKey = "isiDirectionKey";

    getDirection();
    if (direction.equals(TransportInfo.FIRST_TO_SECOND.getTextRepresentation())) {
      isiKey.put(directionKey, Constants.FIRST_TO_SECOND);
    }
    else if (direction.equals(TransportInfo.SECOND_TO_FIRST.getTextRepresentation())) {
      isiKey.put(directionKey, Constants.SECOND_TO_FIRST);
    }
    else if (direction.equals(TransportInfo.BOTH_DIRECTIONS.getTextRepresentation())) {
      isiKey.put(directionKey, Constants.BOTH_DIRECTIONS);
    }
    else {
      isiKey.put(directionKey, Constants.NO_DIRECTION);
    }
    return isiKey;
  }

  public void setIsiKey(Map<Object, Object> isiKey) {
    this.isiKey = isiKey;
  }

  public Map<Object, Object> getIsiKeyBA() {

    String directionKey = "isiDirectionKeyBA";

    getDirectionBA();
    if (directionBA.equals(TransportInfo.FIRST_TO_SECOND.getTextRepresentation())) {
      isiKeyBA.put(directionKey, Constants.FIRST_TO_SECOND);
    }
    else if (directionBA.equals(TransportInfo.SECOND_TO_FIRST.getTextRepresentation())) {
      isiKeyBA.put(directionKey, Constants.SECOND_TO_FIRST);
    }
    else if (directionBA.equals(TransportInfo.BOTH_DIRECTIONS.getTextRepresentation())) {
      isiKeyBA.put(directionKey, Constants.BOTH_DIRECTIONS);
    }
    else {
      isiKeyBA.put(directionKey, Constants.NO_DIRECTION);
    }
    return isiKeyBA;
  }

  public void setIsiKeyBA(Map<Object, Object> isiKeyBA) {
    this.isiKeyBA = isiKeyBA;
  }

  /**
   * Returns a list of {@link NamedId}s that represent the business objects transported by this
   * connection in the proper direction.
   * 
   * @return See method description.
   */
  public List<NamedId> getTransportInformation() {

    if (referenceRelease == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    List<Transport> transportsCopy = new ArrayList<Transport>(transports);
    Collections.sort(transportsCopy);

    if (referenceRelease.getId().equals(this.getInformationSystemReleaseA().getId())) {
      return getNamedIdsForTransports(transportsCopy, true);
    }
    else if (referenceRelease.getId().equals(this.getInformationSystemReleaseB().getId())) {
      return getNamedIdsForTransports(transportsCopy, false);
    }
    else {
      return new ArrayList<NamedId>();
    }
  }

  /**
   * @return Returns a String containing the name and version of the
   *         {@link InformationSystemRelease}s at both ends of the interface. The ends are separated
   *         by a interface direction indicator.
   */
  public String getInterfaceInformation() {
    return informationSystemReleaseA.getReleaseName() + " " + getDirection() + " " + informationSystemReleaseB.getReleaseName();
  }

  /**
   * @return Returns the {@link InformationSystemRelease} connected to the reference release.
   */
  public InformationSystemRelease getOtherRelease() {
    if (informationSystemReleaseA.getId().equals(referenceRelease.getId())) {
      return informationSystemReleaseB;
    }
    return informationSystemReleaseA;
  }

  /**
   * Returns a list of strings describing the Transports of the Interface containing the name and
   * the direction of flow for every Business Object. This method is needed for the presentation
   * tier.
   * 
   * @return A List of {@link String}, describing all transports.
   */
  public List<String> getTransportInfos() {
    List<String> transportList = new ArrayList<String>();

    // sort Transports:
    List<Transport> sortedTransports = new ArrayList<Transport>(this.getTransports());
    Collections.sort(sortedTransports);

    for (Transport transport : sortedTransports) {
      StringBuffer transportBuffer = new StringBuffer();
      transportBuffer.append(transport.getTransportInfo().getTextRepresentation());
      transportBuffer.append(Constants.TRANSPORTINFOSEP);
      transportBuffer.append(transport.getBusinessObject().getName());
      transportBuffer.append(Constants.TRANSPORTINFOSEP);
      transportBuffer.append(transport.getTransportInfo().getTextRepresentation());
      transportList.add(transportBuffer.toString());
    }

    return transportList;
  }

  public String getIdentityString() {
    String hierarchNameA = null;
    String hierarchNameB = null;
    if (null != informationSystemReleaseA) {
      hierarchNameA = informationSystemReleaseA.getHierarchicalName();
    }
    if (null != informationSystemReleaseB) {
      hierarchNameB = informationSystemReleaseB.getHierarchicalName();
    }
    return hierarchNameA + " " + getDirection() + " " + hierarchNameB;
  }

  @Override
  public void validate() {
    super.validate();

    if ((description != null) && (description.length() > Constants.TEXT_LONG)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.TEXT_TOO_LONG);
    }

    for (TechnicalComponentRelease release : technicalComponentReleases) {
      if (!release.getTechnicalComponent().isAvailableForInterfaces()) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.TECHNICALCOMPONENT_NOT_AVAILABLE_FOR_CONNECTION);
      }
    }
  }

  /**
   * Helper method for {@link #getTransportInformation()}. Returns a list of NamedIds whose id is
   * the Transport id, name is the Tansport name, description is the Transport description and misc
   * map contains the Transport image key under the map key 'transportkey'.
   * 
   * @param transportInstances
   *          List of {@link Transport}s.
   * @param firstToSecond
   *          flag
   * @return List of {@link NamedId}s.
   */
  private List<NamedId> getNamedIdsForTransports(List<Transport> transportInstances, boolean firstToSecond) {
    List<NamedId> namedIds = new ArrayList<NamedId>();
    if (transportInstances == null) {
      return namedIds;
    }
    for (Transport t : transportInstances) {
      NamedId ni = new NamedId();
      ni.setId(t.getBusinessObject().getId());
      ni.setName(t.getBusinessObject().getHierarchicalName());
      ni.setDescription(t.getBusinessObject().getDescription());

      Map<Object, Object> misc = new HashMap<Object, Object>();
      String key = t.calculateTransportKey(firstToSecond);
      misc.put("transportkey", key);

      ni.setMisc(misc);
      namedIds.add(ni);
    }
    return namedIds;
  }

  public Direction getInterfaceDirection() {
    return interfaceDirection;
  }

  public void setInterfaceDirection(Direction interfaceDirection) {
    this.interfaceDirection = interfaceDirection;
  }
}