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
package de.iteratec.iteraplan.businesslogic.reports.query.postprocessing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;

import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Transport;


/**
 * This strategy can be used to create an abstracted view of a set of Informationsystems. This
 * strategy converts a set of Informationssystems so that only top level Informationssystems remain.
 * In addition to that, Interfaces that connect lower level Information systems and their Business
 * Objects are aggregated and merged with the according top level Information system. The outline of
 * the algorithm this strategy implements is as follows:
 * <ul>
 * <li>For each Information system find the top level Information system.
 * <li>For each top level Information system:
 * <ul>
 * <li>aggregate all Interfaces and merge them if necessary
 * <li>aggregate all Business Objects
 * </ul>
 * </ul>
 * The Information systems after processing are shallow copies of the original Informationsystems.
 * Only (aggregated) Business Objects and Connections are attached. Therefore this strategy should
 * always be the last strategy that is applied to the result set.
 */
public class HideSubInformationSystemReleasesWithConnectionMergingStrategy extends AbstractPostprocessingStrategy<InformationSystemRelease> {

  /** Serialization version. */
  private static final long serialVersionUID = 5189550144362954246L;
  static final Logger       LOGGER           = Logger.getIteraplanLogger(HideSubInformationSystemReleasesWithConnectionMergingStrategy.class);

  /**
   * @param orderNumber
   *          The order is used to define the order in which all AbstractPostprocessingStrategies
   *          are executed.
   */
  public HideSubInformationSystemReleasesWithConnectionMergingStrategy(Integer orderNumber) {
    super(Constants.POSTPROCESSINGSTRATEGY_HIDE_CHILDREN_MERGE, orderNumber, new String[] { Constants.REPORTS_EXPORT_GRAPHICAL_INFORMATIONFLOW });
  }

  @Override
  public Set<InformationSystemRelease> process(Set<InformationSystemRelease> isReleases, Node queryNode) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Calling post processing strategy " + ClassUtils.getShortClassName(this.getClass()) + " with " + isReleases.size()
          + " ipureleases");
    }

    Set<InformationSystemRelease> topLevelISReleases = new HashSet<InformationSystemRelease>();
    for (InformationSystemRelease isr : isReleases) {
      topLevelISReleases.add(isr.getPrimeFather());
    }
    Set<InformationSystemRelease> results = propagateConnections(topLevelISReleases);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("returning " + results.size() + " information system releases");
    }

    return results;
  }

  /**
   * Takes a collection of top level IS releases and for each IS release:
   * <ul>
   * <li>Pulls up and merges all Business Objects of lower level IS releases.
   * <li>Pulls up and merges all Connections of lower level IS release.
   * </ul>
   * 
   * @param isReleases
   *          The top level IS releases to process.
   * @return The IS releases after processing. They now have aggregated Business Objects and
   *         Connections. All other relations are empty!
   */
  private static Set<InformationSystemRelease> propagateConnections(Set<InformationSystemRelease> isReleases) {
    Map<Integer, Map<Integer, InformationSystemInterface>> globalConnectionFromToMap = CollectionUtils.hashMap();

    Map<Integer, BusinessObject> globalBusinessObjectMap = CollectionUtils.hashMap();

    /* Map that maps the OD of a top-level Information System to it's (aggregated) Business Objects. */
    Map<Integer, Set<BusinessObject>> globalTopLevelIpuBusinessObjectMap = CollectionUtils.hashMap();

    for (InformationSystemRelease isr : isReleases) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("------------------------------------------------------------");
        LOGGER.debug("processing IS release: " + isr.getNonHierarchicalName());
      }
      processInterfacesForInformationSystemRelease(isr, globalConnectionFromToMap, globalBusinessObjectMap, globalTopLevelIpuBusinessObjectMap);
    }
    // set merged Business Objects into top level IS release
    Map<Integer, InformationSystemRelease> isrMap = CollectionUtils.hashMap();
    for (InformationSystemRelease isr : isReleases) {
      isr = simpleCopyInformationSystemRelease(isr, globalBusinessObjectMap);
      isr.setBusinessObjects(globalTopLevelIpuBusinessObjectMap.get(isr.getId()));
      isrMap.put(isr.getId(), isr);
    }
    // set merged Connections into top level IS release
    for (Map<Integer, InformationSystemInterface> map : globalConnectionFromToMap.values()) {
      for (InformationSystemInterface c : map.values()) {

        InformationSystemRelease fromIsr = isrMap.get(c.getInformationSystemReleaseA().getId());
        if (fromIsr != null) {
          fromIsr.getInterfacesReleaseB().add(c);
        }
        InformationSystemRelease toIsr = isrMap.get(c.getInformationSystemReleaseB().getId());
        if (toIsr != null) {
          toIsr.getInterfacesReleaseA().add(c);
        }
      }
    }
    return new HashSet<InformationSystemRelease>(isrMap.values());
  }

  /**
   * Merges the Business Objects and the Connection data for an IS release with its top level
   * IS release.
   * <ul>
   * <li>Business Object of isr are merged with its top level IS release by filling the
   * globalTopLevelIsrBusinessObjectMap.
   * <li>Interfaces with transports are merged with its top level IS release by filling
   * globalConnectionFromToMap.
   * <li>The above two steps are repeated recursively for all children IS releases.
   * </ul>
   * 
   * @param isr
   *          The current IS release.
   * @param globalInterfaceFromToMap
   *          <FromIsReleaseId, <ToIsReleaseId, InformationSystemInterface>> map. Acts as in-out parameter.
   * @param globalBusinessObjectMap
   *          for all processed Business Objects. Acts as in-out parameter.
   * @param globalTopLevelIsrBusinessObjectMap
   *          Acts as in-out parameter.
   */
  private static void processInterfacesForInformationSystemRelease(InformationSystemRelease isr,
                                                                   Map<Integer, Map<Integer, InformationSystemInterface>> globalInterfaceFromToMap,
                                                                   Map<Integer, BusinessObject> globalBusinessObjectMap,
                                                                   Map<Integer, Set<BusinessObject>> globalTopLevelIsrBusinessObjectMap) {

    mergeBusinessObjects(isr, globalBusinessObjectMap, globalTopLevelIsrBusinessObjectMap);

    // process interfaces
    Set<InformationSystemInterface> toInterfaces = isr.getInterfacesReleaseB();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("get to interfaces from information system release {0}: {1}", isr.getNonHierarchicalName(), toInterfaces);
    }
    if (toInterfaces != null) {
      mergeInterfacesToTopLevel(globalInterfaceFromToMap, globalBusinessObjectMap, toInterfaces);
    }

    Set<InformationSystemInterface> fromInterfaces = isr.getInterfacesReleaseA();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("get from interfaces from information system release {0}: {1}", isr.getNonHierarchicalName(), fromInterfaces);
    }
    if (fromInterfaces != null) {
      mergeInterfacesToTopLevel(globalInterfaceFromToMap, globalBusinessObjectMap, fromInterfaces);
    }

    // process children
    Set<InformationSystemRelease> consistsOfReleases = isr.getChildren();
    if (consistsOfReleases != null) {
      for (InformationSystemRelease subIsr : consistsOfReleases) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("processing sub information system release: {0}", subIsr.getNonHierarchicalName());
        }
        processInterfacesForInformationSystemRelease(subIsr, globalInterfaceFromToMap, globalBusinessObjectMap, globalTopLevelIsrBusinessObjectMap);
      }
    }
  }

  private static void mergeInterfacesToTopLevel(Map<Integer, Map<Integer, InformationSystemInterface>> globalInterfaceFromToMap,
                                                Map<Integer, BusinessObject> globalBusinessObjectMap, Set<InformationSystemInterface> interfaces) {
    for (InformationSystemInterface originalInterface : interfaces) {
      InformationSystemInterface iface = copyInterface(originalInterface, globalBusinessObjectMap);
      iface.setInformationSystemReleaseA(getTopLevelInformationSystemRelease(originalInterface.getInformationSystemReleaseA()));
      iface.setInformationSystemReleaseB(getTopLevelInformationSystemRelease(originalInterface.getInformationSystemReleaseB()));
      // ignore interfaces between releases with the same top level parent
      if (iface.getInformationSystemReleaseA().getId().equals(iface.getInformationSystemReleaseB().getId())) {
        continue;
      }
      storeInFromToMap(iface, globalInterfaceFromToMap);
    }
  }

  /**
   * Gets the set of BusinessObjects of {@code isr}'s top level IS release or create one if necessary.
   * Then merges the Business Objects of {@code isr} with its top level IS release
   * @param isr
   * @param globalBusinessObjectMap
   * @param globalTopLevelIsrBusinessObjectMap
   */
  private static void mergeBusinessObjects(InformationSystemRelease isr, Map<Integer, BusinessObject> globalBusinessObjectMap,
                                           Map<Integer, Set<BusinessObject>> globalTopLevelIsrBusinessObjectMap) {

    Set<BusinessObject> bos = globalTopLevelIsrBusinessObjectMap.get(isr.getPrimeFather().getId());
    if (bos == null) {
      bos = new HashSet<BusinessObject>();
      globalTopLevelIsrBusinessObjectMap.put(isr.getPrimeFather().getId(), bos);
    }

    for (BusinessObject bussinessObj : isr.getBusinessObjects()) {
      BusinessObject boCopy = simpleCopyBusinessObject(bussinessObj, globalBusinessObjectMap);
      bos.add(boCopy);
    }
  }

  /**
   * Store an interface in the globalConnectionFromToMap. If a similar interface was already
   * stored, the two interfaces are merged together.
   * 
   * @param iface
   *          The interface to store.
   * @param globalInterfaceFromToMap
   *          The map. Acts as an in-out parameter.
   */
  private static void storeInFromToMap(InformationSystemInterface iface,
                                       Map<Integer, Map<Integer, InformationSystemInterface>> globalInterfaceFromToMap) {
    InformationSystemInterface existingInterface = getFromTo(iface, globalInterfaceFromToMap);
    // if not found, see if it was stored the other way around.
    if (existingInterface == null) {
      existingInterface = getToFrom(iface, globalInterfaceFromToMap);
    }
    // new interface, so store
    if (existingInterface == null) {
      Map<Integer, InformationSystemInterface> toMap = globalInterfaceFromToMap.get(iface.getInformationSystemReleaseA().getId());
      if (toMap == null) {
        toMap = new HashMap<Integer, InformationSystemInterface>();
      }
      toMap.put(iface.getInformationSystemReleaseB().getId(), iface);
      globalInterfaceFromToMap.put(iface.getInformationSystemReleaseA().getId(), toMap);
    }
    /*
     * If the interface already exists, merge it. Note that the merging does not take into account
     * the associated TCRs! Connections with different TCRs will be merged together, even if they
     * already connect top level IS releases!
     */
    else {
      mergeInterfaces(existingInterface, iface);
    }
  }

  /**
   * @param iface
   *          The connection to check for.
   * @param globalInterfaceFromToMap
   *          <FromIsReleaseId, <ToIsReleaseId, InformationSystemInterface>> map that is checked.
   * @return The interface contained in the globalInterfaceFromToMap or null, if its not stored in
   *         the map.
   */
  private static InformationSystemInterface getFromTo(InformationSystemInterface iface,
                                                      Map<Integer, Map<Integer, InformationSystemInterface>> globalInterfaceFromToMap) {
    Map<Integer, InformationSystemInterface> toMap = globalInterfaceFromToMap.get(iface.getInformationSystemReleaseA().getId());
    if (toMap == null) {
      toMap = new HashMap<Integer, InformationSystemInterface>();
    }
    return toMap.get(iface.getInformationSystemReleaseB().getId());
  }

  /**
   * @param iface
   *          The interface to check for.
   * @param globalInterfaceFromToMap
   *          <FromIsReleaseId, <ToIsReleaseId, InformationSystemInterface>> map that is checked.
   * @return The interface contained in the globalInterfaceFromToMap or null, if its not stored in
   *         the map.
   */
  private static InformationSystemInterface getToFrom(InformationSystemInterface iface,
                                                      Map<Integer, Map<Integer, InformationSystemInterface>> globalInterfaceFromToMap) {
    Map<Integer, InformationSystemInterface> toMap = globalInterfaceFromToMap.get(iface.getInformationSystemReleaseB().getId());
    if (toMap == null) {
      toMap = new HashMap<Integer, InformationSystemInterface>();
    }
    return toMap.get(iface.getInformationSystemReleaseA().getId());
  }

  /**
   * Merges two interfaces which connect the same IS releases.
   * 
   * @param original
   *          The original interface. This is an in-out parameter.
   * @param duplicate
   *          The duplicate interface.
   */
  private static void mergeInterfaces(InformationSystemInterface original, InformationSystemInterface duplicate) {

    Integer origFromIsrId = original.getInformationSystemReleaseA().getId();
    Integer origToIsrId = original.getInformationSystemReleaseB().getId();

    Integer duplFromIsrId = duplicate.getInformationSystemReleaseA().getId();
    Integer duplToIsrId = duplicate.getInformationSystemReleaseB().getId();

    if (origFromIsrId.equals(duplFromIsrId) && origToIsrId.equals(duplToIsrId)) {
      original.setTransports(mergeTransports(original.getTransports(), duplicate.getTransports(), true));
    }
    else if (origFromIsrId.equals(duplToIsrId) && origToIsrId.equals(duplFromIsrId)) {
      original.setTransports(mergeTransports(original.getTransports(), duplicate.getTransports(), false));
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  /**
   * Merges to sets of transports.
   * 
   * @param transports1
   *          Transport set 1.
   * @param transports2
   *          Transport set 2.
   * @param symetricMerge
   *          The transports are symmetric.
   * @return The merged transports.
   */
  private static Set<Transport> mergeTransports(Set<Transport> transports1, Set<Transport> transports2, boolean symetricMerge) {
    LOGGER.debug("merging transports {0} and {1}", transports1, transports2);

    if ((transports1 == null) || transports1.isEmpty()) {
      return transports2;
    }
    if ((transports2 == null) || transports2.isEmpty()) {
      return transports1;
    }

    Map<Integer, Transport> newTransports = createNewTransportsMapFromTransportSet(transports1);

    for (Transport transport : transports2) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("(bo2: {0})", transport.getBusinessObject().getId());
      }

      Transport existingTransport = newTransports.get(transport.getBusinessObject().getId());
      if (existingTransport != null) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("transports to be merged carried same bo {0}", transport.getBusinessObject().getName());
        }

        setNewDirectionsForExistingTransport(transport, existingTransport, symetricMerge);
      }
      else {
        LOGGER.debug("transports to be merged did not carry same bo");

        setNewDirectionsForNewTransport(symetricMerge, transport);
        newTransports.put(transport.getBusinessObject().getId(), transport);
      }
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("done merging transports: {0}", newTransports.values());
    }
    return new HashSet<Transport>(newTransports.values());
  }

  private static void setNewDirectionsForNewTransport(boolean symetricMerge, Transport transport) {
    if (!symetricMerge) {
      LOGGER.debug("... C ... (not symetric)");
      if (Direction.FIRST_TO_SECOND.equals(transport.getDirection())) {
        transport.setDirection(Direction.SECOND_TO_FIRST);
      }
      else if (Direction.SECOND_TO_FIRST.equals(transport.getDirection())) {
        transport.setDirection(Direction.FIRST_TO_SECOND);
      }
    }
    else {
      LOGGER.debug("... D ... (symetric)");
    }
  }

  /**
   * Creates a Map which maps a BusinessObject-ID to the transport, which contains the BusinessObject.
   * Fills the map with data from the given Set of transports
   * @param transports
   *          the given transports-Set
   * @return a new Map<Integer, Transport>
   */
  private static Map<Integer, Transport> createNewTransportsMapFromTransportSet(Set<Transport> transports) {
    Map<Integer, Transport> newTransports = CollectionUtils.hashMap();

    for (Transport transport : transports) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("(bo1: {0})", transport.getBusinessObject().getId());
      }

      newTransports.put(transport.getBusinessObject().getId(), transport);
    }
    return newTransports;
  }

  private static void setNewDirectionsForExistingTransport(Transport transport, Transport existingTransport, boolean symetricMerge) {
    Direction newDirection = existingTransport.getDirection();
    if (symetricMerge) {
      LOGGER.debug("... A ... (symetric)");

      if (Direction.NO_DIRECTION.equals(newDirection)) {
        newDirection = transport.getDirection();
      }
      else if (Direction.FIRST_TO_SECOND.equals(newDirection) && transport.getDirection().isSecondToFirst()) {
        newDirection = Direction.BOTH_DIRECTIONS;
      }
      else if (Direction.SECOND_TO_FIRST.equals(newDirection) && transport.getDirection().isFirstToSecond()) {
        newDirection = Direction.BOTH_DIRECTIONS;
      }
    }
    else {
      LOGGER.debug("... B ... (not symetric)");

      if (Direction.NO_DIRECTION.equals(newDirection)) {
        if (transport.getDirection().isFirstToSecond() == transport.getDirection().isSecondToFirst()) {
          newDirection = transport.getDirection();
        }
        else if (transport.getDirection().isFirstToSecond()) {
          newDirection = Direction.SECOND_TO_FIRST;
        }
        else if (transport.getDirection().isSecondToFirst()) {
          newDirection = Direction.FIRST_TO_SECOND;
        }
      }
      else if (Direction.FIRST_TO_SECOND.equals(newDirection) && transport.getDirection().isFirstToSecond()) {
        newDirection = Direction.BOTH_DIRECTIONS;
      }
      else if (Direction.SECOND_TO_FIRST.equals(newDirection) && transport.getDirection().isSecondToFirst()) {
        newDirection = Direction.BOTH_DIRECTIONS;
      }
    }
    existingTransport.setDirection(newDirection);
  }

  private static InformationSystemRelease getTopLevelInformationSystemRelease(InformationSystemRelease isrParam) {
    InformationSystemRelease isr = isrParam;
    while (isr.getParent() != null) {
      isr = isr.getParent();
    }
    return isr;
  }

  /**
   * @param orgInterface
   *          The interface to copy.
   * @param globalBusinessObjectMap
   *          map containing shallow Business Object copies. Acts as an in-out parameter.
   * @return A shallow copy of an interface.
   */
  private static InformationSystemInterface copyInterface(InformationSystemInterface orgInterface,
                                                          Map<Integer, BusinessObject> globalBusinessObjectMap) {
    InformationSystemInterface isi = BuildingBlockFactory.createInformationSystemInterface();
    isi.setInformationSystemReleaseA(orgInterface.getInformationSystemReleaseA());
    isi.setInformationSystemReleaseB(orgInterface.getInformationSystemReleaseB());
    isi.setTransports(copyTransports(orgInterface.getTransports(), globalBusinessObjectMap));
    isi.setId(orgInterface.getId());
    return isi;
  }

  /**
   * @param transports
   *          The transports to copy.
   * @param globalBusinessObjectMap
   *          map containing shallow Business Object copies. Acts as an in-out parameter.
   * @return A set of shallow Transport copies.
   */
  private static Set<Transport> copyTransports(Set<Transport> transports, Map<Integer, BusinessObject> globalBusinessObjectMap) {
    Set<Transport> newTransports = new HashSet<Transport>();
    for (Transport transport : transports) {
      Transport t = BuildingBlockFactory.createTransport();
      t.setId(transport.getId());
      t.setBusinessObject(simpleCopyBusinessObject(transport.getBusinessObject(), globalBusinessObjectMap));
      t.setDirection(transport.getDirection());
      newTransports.add(t);
    }
    return newTransports;
  }

  /**
   * @param toCopy
   *          The Business Object to copy.
   * @param globalBusinessObjectMap
   *          map containing shallow Business Object copies. Acts as an in-out parameter.
   * @return A shallow Business Object copy.
   */
  private static BusinessObject simpleCopyBusinessObject(BusinessObject toCopy, Map<Integer, BusinessObject> globalBusinessObjectMap) {

    BusinessObject bo = globalBusinessObjectMap.get(toCopy.getId());
    if (bo != null) {
      return bo;
    }
    bo = new BusinessObject();
    bo.setBuildingBlockType(toCopy.getBuildingBlockType());
    bo.setId(toCopy.getId());
    bo.setName(toCopy.getName());
    globalBusinessObjectMap.put(bo.getId(), bo);
    return bo;
  }

  /**
   * @param isr
   *          The IS release to copy.
   * @param globalBusinessObjectMap
   *          map containing shallow Business Object copies. Acts as an in-out parameter.
   * @return A shallow IS release copy. Business Objects are set, all other relations are empty.
   */
  private static InformationSystemRelease simpleCopyInformationSystemRelease(InformationSystemRelease isr,
                                                                             Map<Integer, BusinessObject> globalBusinessObjectMap) {
    InformationSystemRelease isrCopy = new InformationSystemRelease();
    isrCopy.setBuildingBlockType(isr.getBuildingBlockType());
    isrCopy.setInformationSystem(simpleCopyInformationSystem(isr.getInformationSystem()));
    isrCopy.setId(isr.getId());
    isrCopy.setVersion(isr.getVersion());
    isrCopy.setDescription(isr.getDescription());
    isrCopy.setRuntimePeriod(isr.getRuntimePeriod());
    isrCopy.setTypeOfStatus(isr.getTypeOfStatus());
    isrCopy.setAttributeValueAssignments(isr.getAttributeValueAssignments());
    isrCopy.setBusinessObjects(new HashSet<BusinessObject>());
    for (BusinessObject bo : isr.getBusinessObjects()) {
      isrCopy.getBusinessObjects().add(simpleCopyBusinessObject(bo, globalBusinessObjectMap));
    }
    return isrCopy;
  }

  /**
   * @param is
   *          The IS to copy.
   * @return A shallow IS copy.
   */
  private static InformationSystem simpleCopyInformationSystem(InformationSystem is) {
    InformationSystem isCopy = new InformationSystem();
    isCopy.setBuildingBlockType(is.getBuildingBlockType());
    isCopy.setName(is.getName());
    return isCopy;
  }
}
