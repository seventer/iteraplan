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
package de.iteratec.iteraplan.businesslogic.exchange.common.informationflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TransportInfo;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.layoutgraph.LayoutEdge;
import de.iteratec.layoutgraph.LayoutEdge.Direction;
import de.iteratec.layoutgraph.LayoutGraph;
import de.iteratec.layoutgraph.LayoutNode;


/**
 * This class transcodes from the iteraplan business model to a simple graph structure. This is
 * currently fairly simple, but will get complicated once different types of building blocks and
 * relations are added.
 */
public class InformationFlowGraphConverter {

  private final Set<InformationSystemRelease>   entities;

  private final Set<InformationSystemInterface> isInterfaces;

  private final Map<String, BusinessObject>     businessObjects;

  private final LayoutGraph                     graph;

  private Map<Integer, LayoutNode>              layoutNodeIdMap;

  private boolean                               conversionCompleted;

  private List<Integer>                         doneEdgeIds;

  private final int[]                           lineCaptionSelected;
  private final Integer                         lineCaptionAttributeId;

  private final boolean                         showIsBusinessObjects;
  private final boolean                         showIsBaseComponents;
  private static final boolean                  IS_LEFT_END_SEARCHED_BB             = false;

  private final AttributeTypeService            attributeTypeService;

  public static final String                    FLOW_INFORMATION_OBJECTS            = "flow.informationObjects";

  public static final String                    APPLICATION_COLOR                   = "application.color";

  public static final String                    APPLICATION_NAME                    = "application.name";

  public static final String                    APPLICATION_VERSION                 = "application.version";

  public static final String                    APPLICATION_INFORMATION_OBJECTS     = "application.informationObjects";

  public static final String                    APPLICATION_HAS_INFORMATION_OBJECTS = "application.informationObjects.YES";

  public static final String                    APPLICATION_BASE_COMPONENTS         = "application.baseComponents";

  public static final String                    APPLICATION_HAS_BASE_COMPONENTS     = "application.baseComponents.YES";

  public InformationFlowGraphConverter(Set<InformationSystemRelease> entities, Set<InformationSystemInterface> isInterfaces,
      Map<String, BusinessObject> bos, AttributeTypeService attributeTypeService, InformationFlowOptionsBean informationFlowOptions) {
    this.entities = entities;
    this.isInterfaces = isInterfaces;
    this.businessObjects = bos;
    this.attributeTypeService = attributeTypeService;
    this.graph = new LayoutGraph();
    this.conversionCompleted = false;
    this.lineCaptionSelected = informationFlowOptions.getSelectionType();
    this.lineCaptionAttributeId = informationFlowOptions.getLineCaptionSelectedAttributeId();
    this.showIsBusinessObjects = informationFlowOptions.isShowIsBusinessObjects();
    this.showIsBaseComponents = informationFlowOptions.isShowIsBaseComponents();
  }

  public void convertToGraph() {

    Map<Integer, InformationSystemRelease> idToIsrMap = new HashMap<Integer, InformationSystemRelease>();
    layoutNodeIdMap = new HashMap<Integer, LayoutNode>();

    Set<InformationSystemRelease> topReleasesInList = retrieveTopRelieses();

    /*
     * We create a hierarchical graph that represents the BB structure. Note: We create the
     * structure only with the selected entities, which makes it a bit more complicated. Therefore
     * we first estimate all selected entities that are top level (i.e. they either don't have a
     * parent or the parent is not selected). After that we search through the selection and find
     * the nearest selected parent for every selected non-top-level entity. We then attach the two.
     */

    // Retrieve the top level nodes
    for (InformationSystemRelease rel : topReleasesInList) {
      LayoutNode node = new LayoutNode();
      node.setNodeElement(rel);
      node.setRepresentedId(rel.getId());
      graph.addNode(node);
      idToIsrMap.put(rel.getId(), rel);
      layoutNodeIdMap.put(rel.getId(), node);
      addNodeProperties(node, rel);
    }

    // Retrieve all selected non-top-level entities
    Set<InformationSystemRelease> nonTopLevelEntities = new HashSet<InformationSystemRelease>();
    for (InformationSystemRelease rel : entities) {
      if (idToIsrMap.get(rel.getId()) == null) {
        nonTopLevelEntities.add(rel);
      }
    }

    // Create a node for every selected non-top-level entity
    for (InformationSystemRelease rel : nonTopLevelEntities) {
      LayoutNode node = new LayoutNode();
      node.setNodeElement(rel);
      node.setRepresentedId(rel.getId());
      addNodeProperties(node, rel);
      idToIsrMap.put(node.getRepresentedId(), rel);
      layoutNodeIdMap.put(node.getRepresentedId(), node);
    }

    // Find the "nearest"(aka. lowest) parent for every non-top-level entity from the selection and
    // attach them.
    for (InformationSystemRelease rel : nonTopLevelEntities) {
      InformationSystemRelease lowestSelectedParent = getLowestLevelParentInSelection(rel);
      LayoutNode parentNode = layoutNodeIdMap.get(lowestSelectedParent.getId());
      LayoutNode childNode = layoutNodeIdMap.get(rel.getId());

      parentNode.addChild(childNode);
      childNode.setParent(parentNode);
    }

    // Load the edges:
    doneEdgeIds = new LinkedList<Integer>();
    for (LayoutNode node : graph.getNodes()) {
      fetchConntections(node, idToIsrMap);
    }
    this.conversionCompleted = true;
  }

  public LayoutGraph getConvertedGraph() {
    if (!conversionCompleted) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
    return graph;
  }

  private Set<InformationSystemRelease> retrieveTopRelieses() {
    Set<InformationSystemRelease> resultList = new HashSet<InformationSystemRelease>();

    for (InformationSystemRelease rel : entities) {
      Set<InformationSystemRelease> allParents = retrieveAllParents(rel);
      Set<InformationSystemRelease> selectedParents = retrieveSelectedParents(allParents);
      InformationSystemRelease topLevelSelectedParent = getTopLevelSelectedParent(selectedParents);

      if (topLevelSelectedParent == null) {
        resultList.add(rel);
      }
      else {
        resultList.add(topLevelSelectedParent);
      }
    }
    return resultList;
  }

  private Set<InformationSystemRelease> retrieveAllParents(InformationSystemRelease rel) {
    Set<InformationSystemRelease> allParents = new HashSet<InformationSystemRelease>();
    InformationSystemRelease temp = rel;
    while (temp.getParent() != null) {
      allParents.add(temp.getParent());
      temp = temp.getParent();
    }
    return allParents;
  }

  private Set<InformationSystemRelease> retrieveSelectedParents(Set<InformationSystemRelease> allParents) {
    Set<InformationSystemRelease> filteredParents = new HashSet<InformationSystemRelease>();
    for (InformationSystemRelease parent : allParents) {
      if (entities.contains(parent)) {
        filteredParents.add(parent);
      }
    }
    return filteredParents;
  }

  private InformationSystemRelease getTopLevelSelectedParent(Set<InformationSystemRelease> selectedParents) {
    InformationSystemRelease topLevelSelectedParent = null;
    boolean loaded = false;

    for (InformationSystemRelease parent : selectedParents) {
      if (!loaded) {
        topLevelSelectedParent = parent;
        loaded = true;
      }
      else {
        if (parent.getLevel() < topLevelSelectedParent.getLevel()) {
          topLevelSelectedParent = parent;
        }
      }
    }
    return topLevelSelectedParent;
  }

  private InformationSystemRelease getLowestLevelParentInSelection(InformationSystemRelease rel) {
    Set<InformationSystemRelease> allParents = retrieveAllParents(rel);
    Set<InformationSystemRelease> selectedParents = retrieveSelectedParents(allParents);
    InformationSystemRelease lowestParent = null;
    boolean loaded = false;

    for (InformationSystemRelease parent : selectedParents) {
      if (!loaded) {
        lowestParent = parent;
        loaded = true;
      }
      else {
        if (parent.getLevel() > lowestParent.getLevel()) {
          lowestParent = parent;
        }
      }
    }
    return lowestParent;
  }

  private void addNodeProperties(LayoutNode node, InformationSystemRelease rel) {

    node.addCustomProperty(InformationFlowGraphConverter.APPLICATION_NAME, rel.getInformationSystem().getName());
    node.addCustomProperty(InformationFlowGraphConverter.APPLICATION_VERSION, rel.getVersion());

    Set<Isr2BoAssociation> isr2BoAssociationSet = GeneralHelper.filterAbstractAssociationsByBusinessObjects(rel, businessObjects,
        IS_LEFT_END_SEARCHED_BB);

    if (showIsBusinessObjects && (isr2BoAssociationSet != null) && (isr2BoAssociationSet.size() != 0)) {
      node.addCustomProperty(InformationFlowGraphConverter.APPLICATION_HAS_INFORMATION_OBJECTS,
          InformationFlowGraphConverter.APPLICATION_HAS_INFORMATION_OBJECTS);
      node.addCustomProperty(InformationFlowGraphConverter.APPLICATION_INFORMATION_OBJECTS,
          GeneralHelper.makeConcatenatedNameStringForAssociationCollection(isr2BoAssociationSet, IS_LEFT_END_SEARCHED_BB, true, false));
    }
    else {
      node.addCustomProperty(InformationFlowGraphConverter.APPLICATION_HAS_INFORMATION_OBJECTS, "no");
    }

    if (showIsBaseComponents && (rel.getBaseComponents() != null) && (rel.getBaseComponents().size() != 0)) {
      node.addCustomProperty(InformationFlowGraphConverter.APPLICATION_HAS_BASE_COMPONENTS,
          InformationFlowGraphConverter.APPLICATION_HAS_BASE_COMPONENTS);
      node.addCustomProperty(InformationFlowGraphConverter.APPLICATION_BASE_COMPONENTS,
          GeneralHelper.makeConcatenatedNameStringForBbCollection(rel.getBaseComponents()));
    }
    else {
      node.addCustomProperty(InformationFlowGraphConverter.APPLICATION_HAS_BASE_COMPONENTS, "no");
    }
  }

  private void fetchConntections(LayoutNode node, Map<Integer, InformationSystemRelease> idToIsrMap) {
    InformationSystemRelease rootRel = idToIsrMap.get(node.getRepresentedId());
    Set<InformationSystemInterface> interfaces = rootRel.getAllConnections();

    // filter connections
    if (isInterfaces != null) {
      interfaces = Sets.intersection(interfaces, isInterfaces);
    }

    // Fetch interfaces for the current node
    for (InformationSystemInterface iface : interfaces) {
      if (iface.getInformationSystemReleaseA().getId().equals(rootRel.getId())) {
        addEdgesForConnection(iface, layoutNodeIdMap.get(rootRel.getId()), layoutNodeIdMap.get(iface.getInformationSystemReleaseB().getId()));
      }
      else if (iface.getInformationSystemReleaseB().getId().equals(rootRel.getId())) {
        addEdgesForConnection(iface, layoutNodeIdMap.get(iface.getInformationSystemReleaseA().getId()), layoutNodeIdMap.get(rootRel.getId()));
      }
      else {
        // TODO check if exception is needed and appropriate
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }
    }
    // recursively fetch interfaces for child nodes
    for (LayoutNode child : node.getChildren()) {
      fetchConntections(child, idToIsrMap);
    }

  }

  private void addEdgesForConnection(InformationSystemInterface connection, LayoutNode startNode, LayoutNode endNode) {
    // Validate start and end nodes
    if ((startNode == null) || (endNode == null) || doneEdgeIds.contains(connection.getId())) {
      return;
    }

    List<String> edgeLabels = new ArrayList<String>();
    List<Direction> directions = new ArrayList<Direction>();
    Direction directionInterface = setInterfaceEdgeDirection(connection);

    if (ArrayUtils.contains(lineCaptionSelected, InformationFlowOptionsBean.LINE_DESCR_BUSINESS_OBJECTS)) {
      setBusinessObjectEdgeLabelsAndDirections(connection, edgeLabels, directions);
    }
    else {
      edgeLabels.add("");
      directions.add(directionInterface);
    }

    List<String> commonEdgeLabels = Lists.newArrayList();
    for (int lineCaption : this.lineCaptionSelected) {
      String prefix = "";
      switch (lineCaption) {
        case InformationFlowOptionsBean.LINE_DESCR_TECHNICAL_COMPONENTS:
          String edgeLabelForTcr = getEdgeLabelForTcr(connection);
          if (!edgeLabelForTcr.isEmpty()) {
            commonEdgeLabels.add(edgeLabelForTcr);
          }
          break;

        case InformationFlowOptionsBean.LINE_DESCR_ATTRIBUTES:
          commonEdgeLabels.add(getEdgeLabelForAttributes(connection));
          break;

        case InformationFlowOptionsBean.LINE_DESCR_DESCRIPTION:
          prefix = MessageAccess.getStringOrNull("graphicalExport.informationflow.description.abbreviation") + ": ";
          commonEdgeLabels.add(prefix + connection.getDescription());
          break;

        case InformationFlowOptionsBean.LINE_DESCR_NAME:
          prefix = MessageAccess.getStringOrNull("graphicalExport.informationflow.name.abbreviation") + ": ";
          if (!connection.getName().isEmpty()) {
            commonEdgeLabels.add(prefix + connection.getName());
          }
          break;

        default: // Business Objects: do nothing, case already handled above
      }
    }

    // add edges
    for (int i = 0; i < edgeLabels.size(); i++) {
      String edgeLabel = buildEdgeLabel(commonEdgeLabels, edgeLabels.get(i));
      addEdge(startNode, endNode, connection, edgeLabel, directions.get(i));
    }

    // Mark the connection as done
    doneEdgeIds.add(connection.getId());
  }

  private String buildEdgeLabel(List<String> edgeLabelPrefixes, String edgeLabelSuffix) {
    List<String> allEdgeLabels = Lists.newArrayList(edgeLabelPrefixes);
    allEdgeLabels.add(edgeLabelSuffix);

    allEdgeLabels.removeAll(Collections.singleton(""));

    return StringUtils.join(allEdgeLabels, "; ");
  }

  /**
   * @param connection
   * @return the own direction of the interface.
   * The direction is displayed between the two information systems, together with the chosen line description.
   */
  private Direction setInterfaceEdgeDirection(InformationSystemInterface connection) {
    Direction directionInterface;
    if (de.iteratec.iteraplan.model.Direction.BOTH_DIRECTIONS.equals(connection.getInterfaceDirection())) {
      directionInterface = Direction.BIDIRECTIONAL;
    }
    else if (de.iteratec.iteraplan.model.Direction.FIRST_TO_SECOND.equals(connection.getInterfaceDirection())) {
      directionInterface = Direction.START_TO_END;
    }
    else if (de.iteratec.iteraplan.model.Direction.SECOND_TO_FIRST.equals(connection.getInterfaceDirection())) {
      directionInterface = Direction.END_TO_START;
    }
    else {
      directionInterface = Direction.NO_DIRECTION;
    }
    return directionInterface;
  }

  private void setBusinessObjectEdgeLabelsAndDirections(InformationSystemInterface connection, List<String> edgeLabels, List<Direction> directions) {
    // Load all transported objects
    Map<Direction, List<String>> transportedObjects = getTransportsForConnection(connection);

    for (Map.Entry<Direction, List<String>> mapEntry : transportedObjects.entrySet()) {
      List<String> boNames = mapEntry.getValue();

      if (boNames.size() > 0) {
        String prefix = MessageAccess.getStringOrNull("graphicalExport.informationflow.businessObject.abbreviation") + ": ";
        edgeLabels.add(prefix + GeneralHelper.makeConcatenatedStringWithSeparator(boNames, Constants.BUILDINGBLOCKSEP));
        directions.add(mapEntry.getKey());
      }
    }

    // If no edges have been added, create a default edge
    if (edgeLabels.size() == 0) {
      edgeLabels.add("");
      directions.add(Direction.NO_DIRECTION);
    }
  }

  private String getEdgeLabelForAttributes(InformationSystemInterface connection) {
    List<String> resultValues = InformationFlowGeneralHelper.getLabelDescrForAttribute(attributeTypeService, connection, this.lineCaptionAttributeId);
    String prefix = MessageAccess.getStringOrNull("graphicalExport.informationflow.attribute.abbreviation") + ": ";
    return prefix + GeneralHelper.makeConcatenatedStringWithSeparator(resultValues, Constants.BUILDINGBLOCKSEP);
  }

  private String getEdgeLabelForTcr(InformationSystemInterface connection) {
    List<String> referencedTcrs = InformationFlowGeneralHelper.getReferencedTcReleaseNames(connection);
    String prefix = MessageAccess.getStringOrNull("graphicalExport.informationflow.technicalComponent.abbreviation") + ": ";
    String tcrString = GeneralHelper.makeConcatenatedStringWithSeparator(referencedTcrs, Constants.BUILDINGBLOCKSEP);
    if (tcrString.isEmpty()) {
      return "";
    }
    return prefix + tcrString;
  }

  /**
   * Adds an edge with the specified parameters to the layout graph and sets all relationships
   * between the edge and its nodes.
   * 
   * @param startNode
   *          The start node of the edge (this does not imply direction)
   * @param endNode
   *          The end node of the edge (this does not imply direction)
   * @param content
   *          The {@link IdentityEntity} the node should represent
   * @param label
   *          The string label of the node
   * @param direction
   *          The {@link Direction} of the node
   * @return The created edge, with all relationships to its nodes set.
   */
  private LayoutEdge addEdge(LayoutNode startNode, LayoutNode endNode, IdentityEntity content, String label, Direction direction) {
    LayoutEdge edge = new LayoutEdge();

    graph.addEdge(edge);

    edge.setStartNode(startNode);
    edge.setEndNode(endNode);

    startNode.addEdge(edge);
    endNode.addEdge(edge);

    edge.setEdgeDirection(direction);
    edge.setEdgeLabel(label);
    edge.setEdgeElement(content);

    return edge;
  }

  /**
   * Retrieves all business objects transported for each one of the four possible transport
   * directions.
   * 
   * @param connection
   *          The {@link InformationSystemInterface} whose transports are of interest.
   * @return A mapping from {@link Direction} to a list with the names of the transported business
   *         objects.
   */
  private Map<Direction, List<String>> getTransportsForConnection(InformationSystemInterface connection) {
    Map<Direction, List<String>> transports = getInitializedTransportsMap();

    for (Transport transport : connection.getTransports()) {

      TransportInfo transportInfo = transport.getTransportInfo();
      String transportBusinessObject = transport.getBusinessObject().getName();

      switch (transportInfo) {
        case NO_DIRECTION:
          transports.get(Direction.NO_DIRECTION).add(transportBusinessObject);
          break;
        case FIRST_TO_SECOND:
          transports.get(Direction.START_TO_END).add(transportBusinessObject);
          break;
        case SECOND_TO_FIRST:
          transports.get(Direction.END_TO_START).add(transportBusinessObject);
          break;
        case BOTH_DIRECTIONS:
          transports.get(Direction.BIDIRECTIONAL).add(transportBusinessObject);
          break;
        default:
          break;
      }
    }

    return transports;
  }

  private static Map<Direction, List<String>> getInitializedTransportsMap() {
    Map<Direction, List<String>> transports = new HashMap<Direction, List<String>>();
    transports.put(Direction.NO_DIRECTION, new ArrayList<String>());
    transports.put(Direction.START_TO_END, new ArrayList<String>());
    transports.put(Direction.END_TO_START, new ArrayList<String>());
    transports.put(Direction.BIDIRECTIONAL, new ArrayList<String>());
    return transports;
  }

}