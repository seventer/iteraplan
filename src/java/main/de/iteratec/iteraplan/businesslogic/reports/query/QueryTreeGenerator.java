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
package de.iteratec.iteraplan.businesslogic.reports.query;

import static de.iteratec.iteraplan.model.AbstractHierarchicalEntity.TOP_LEVEL_NAME;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.reports.query.node.AbstractLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.AssociatedLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.AttributeLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Comparator;
import de.iteratec.iteraplan.businesslogic.reports.query.node.DateAttributeLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.EnumAttributeLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.ExtensionNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.node.NotAssociatedLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.NumberAttributeLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Operation;
import de.iteratec.iteraplan.businesslogic.reports.query.node.OperationNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.PropertyLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.ResponsibilityAttributeLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.SealLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.SetPropertyLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.TextAttributeLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.IQStatusData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QFirstLevel;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QPart;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QSealStatus;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QTimespanData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QUserInput;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ArchitecturalDomainTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessDomainQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessFunctionQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessObjectTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessProcessTypeQ;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessUnitQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.CombinedExtension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ITypeWithDates;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ITypeWithStatus;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemDomainTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemInterfaceTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ProductQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ProjectQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.SealState;
import de.iteratec.iteraplan.model.attribute.BBAttribute;


/**
 * This class takes input from the GUI and converts it into a query tree that may be processed by
 * the business logic. On the GUI the query itself is represented as a list of
 * {@link DynamicQueryFormData}. This class converts the structure used by the GUI into a
 * {@link Node} graph, which expresses the very same query. The business logic can then convert this
 * node graph into actual queries.
 */
public class QueryTreeGenerator {

  private static final Logger        LOGGER = Logger.getIteraplanLogger(QueryTreeGenerator.class);

  private final Locale               locale;
  private final AttributeTypeService attributeTypeService;

  public QueryTreeGenerator(Locale locale, AttributeTypeService attributeTypeService) {
    this.locale = locale;
    this.attributeTypeService = attributeTypeService;
  }

  /**
   * Converts a list of {@link DynamicQueryFormData} to a {@link Node} graph.
   *
   * @param queryForms
   *          A list of DynamicQueryFormData objects which contains the user input. The first query
   *          form dictates the result type, the other query forms in the list represent query
   *          extensions.
   * @return The root node of the generated query tree. This can be passed to the business logic
   *         which generates respective HQL statements.
   */
  public Node generateQueryTree(List<DynamicQueryFormData<?>> queryForms) {
    LOGGER.debug("Generating query tree.");

    OperationNode root = new OperationNode(Operation.AND);

    // The type of the first query form determines the result type.
    Type<?> resultType = queryForms.get(0).getType();

    // Iterate over the queries the user has entered. There is always at least
    // the main query. Each added extension represents a new query.
    for (DynamicQueryFormData<?> form : queryForms) {
      if (form.getExtension() instanceof CombinedExtension) {
        processCombinedExtensionForm(root, resultType, form);
      }
      else {
        processForm(root, form, resultType, (Extension) form.getExtension());
      }
    }

    printTree(root);
    checkReadPermission(root);
    LOGGER.debug("Finished generating query tree.");

    return root;
  }

  /**
   * Converts {@link CombinedExtension}s into an appropriate query sub-graph.
   *
   * @param root
   *          The node to which the generated tree will be attached.
   * @param resType
   *          The overall result type of the query.
   * @param form
   *          The form that contains all the information for the combined extension.
   * @throws IteraplanException
   */
  private void processCombinedExtensionForm(OperationNode root, Type<?> resType, DynamicQueryFormData<?> form) {
    CombinedExtension combinedExtension = (CombinedExtension) form.getExtension();
    if (form.getQueryUserInput().getNoAssignements().booleanValue()) {
      OperationNode operationNode = combinedExtension.isLastPartMultiEnded() ? new OperationNode(Operation.AND) : new OperationNode(Operation.OR);
      root.addChild(operationNode);

      for (Extension extension : combinedExtension.getExtensionList()) {
        operationNode.addChild(new NotAssociatedLeafNode(resType, extension));
      }
    }
    else {
      OperationNode multipathOr = new OperationNode(Operation.OR);
      root.addChild(multipathOr);
      for (Extension extension : combinedExtension.getExtensionList()) {
        processForm(multipathOr, form, resType, extension);
      }
      // handle "->" and "<-" directions in extensions as specified in ITERAPLAN-446
      if (isLeftOrRightDirectionInterfacesExtension(combinedExtension, multipathOr)) {
        root.getChildren().remove(multipathOr);

        processForm(root, form, resType, combinedExtension.getExtensionList().get(1));
      }
    }
  }

  private boolean isLeftOrRightDirectionInterfacesExtension(CombinedExtension combinedExtension, OperationNode multipathOr) {
    boolean isInterfacesExtension = InformationSystemReleaseTypeQu.PRESENTATION_EXTENSION_INTERFACES.equals(combinedExtension.getName());
    boolean isExtensionNode = multipathOr.getChildren().get(0) instanceof ExtensionNode;

    if (!(isInterfacesExtension && isExtensionNode)) {
      return false;
    }

    ExtensionNode extensionNode = (ExtensionNode) multipathOr.getChildren().get(0);
    if (!(extensionNode.getChild() instanceof PropertyLeafNode)) {
      return false;
    }

    PropertyLeafNode propertyLeafNode = (PropertyLeafNode) extensionNode.getChild();
    boolean isDirectionProperty = propertyLeafNode.getPropertyName().equals(InformationSystemInterfaceTypeQu.DIRECTION);
    boolean isRightDirection = "->".equals(propertyLeafNode.getPattern());
    boolean isLeftDirection = "<-".equals(propertyLeafNode.getPattern());

    return isDirectionProperty && (isRightDirection || isLeftDirection);
  }

  /**
   * Traverses the query tree starting at the given node and checks if the currently logged in user
   * has read permissions for all user defined attributes contained in the tree.
   *
   * @param root
   *          The root node of a query tree.
   * @throws IteraplanException
   *           If the user does not have read permissions for all contained attributes.
   */
  private void checkReadPermission(OperationNode root) {
    Set<Integer> set = new HashSet<Integer>();
    for (Node node : root.getChildren()) {
      collectAttributeIDs(node, set);
    }
    attributeTypeService.assureReadPermission(set);
  }

  /**
   * Collects the IDs of all attributes contained in the query tree starting at the given node.
   *
   * @param node
   *          The root node of the query tree.
   * @param set
   *          The set of attribute IDs collected so far. Acts as an in-out parameter.
   */
  private void collectAttributeIDs(Node node, Set<Integer> set) {
    if (node instanceof AttributeLeafNode) {
      AttributeLeafNode alf = (AttributeLeafNode) node;
      set.add(Integer.valueOf(alf.getAttributeId()));
    }

    if (node instanceof OperationNode) {
      OperationNode on = (OperationNode) node;
      for (Node childNode : on.getChildren()) {
        collectAttributeIDs(childNode, set);
      }
    }

    if (node instanceof ExtensionNode) {
      ExtensionNode en = (ExtensionNode) node;
      collectAttributeIDs(en.getChild(), set);
    }
  }

  /**
   * Generates a query tree for the given {@link DynamicQueryFormData}. According to the user input
   * stored in the given form instance, the given operation node is extended with additional
   * operation nodes and leaf nodes.
   *
   * @param root
   *          The node onto which the generated query tree is attached.
   * @param form
   *          The form to process.
   * @param resultType
   *          The overall result type.
   * @param extension
   *          If the form is an extension form, the related Extension is passed. Set to null
   *          otherwise.
   */
  private void processForm(OperationNode root, DynamicQueryFormData<?> form, Type<?> resultType, Extension extension) {
    LOGGER.debug("Processing form.");

    OperationNode tempRoot = root;
    Type<?> tempResultType = resultType;
    Extension tempExtension = extension;

    ExtensionNode extensionNode = null;
    QUserInput input = form.getQueryUserInput();
    if (input.getNoAssignements().booleanValue()) {
      String key = tempExtension.getNameKeyForPresentation();

      // Special case when querying for hierarchical elements without a parent element. In fact
      // they do have a parent element, the virtual element, but direct children of this element are
      // treated as top-level elements.
      if (key.equals(ArchitecturalDomainTypeQu.EXTENSION_PARENT_KEY) || key.equals(BusinessFunctionQueryType.EXTENSION_PARENT_KEY)
          || key.equals(BusinessObjectTypeQu.EXTENSION_PARENT_KEY) || key.equals(BusinessProcessTypeQ.EXTENSION_PARENT_KEY)
          || key.equals(Constants.EXTENSION_IE_PARENT_KEY) || key.equals(BusinessDomainQueryType.EXTENSION_PARENT_KEY)
          || key.equals(InformationSystemDomainTypeQu.EXTENSION_PARENT_KEY) || key.equals(ProjectQueryType.EXTENSION_PARENT_KEY)
          || key.equals(BusinessUnitQueryType.EXTENSION_PARENT_KEY) || key.equals(ProductQueryType.EXTENSION_PARENT_KEY)) { // TODO die Konstanten in
        // Constants auslagern

        PropertyLeafNode pln = new PropertyLeafNode(tempResultType, tempExtension, "name", Comparator.LIKE, TOP_LEVEL_NAME,
            BBAttribute.FIXED_ATTRIBUTE_TYPE);
        tempRoot.addChild(pln);
      }
      else {
        tempRoot.addChild(new NotAssociatedLeafNode(tempResultType, tempExtension));
      }

      // Second level forms are not possible for "not-associated" queries, thus return.
      return;
    }

    if (!input.isUserInputAvailable()) {
      LOGGER.debug("This form contains no user input.");

      tempRoot.addChild(new AssociatedLeafNode(tempResultType, tempExtension));
      if (form.getSecondLevelQueryForms().size() > 0) {
        extensionNode = new ExtensionNode(tempResultType, tempExtension);
        tempRoot.addChild(extensionNode);
        OperationNode tmpRoot = new OperationNode(Operation.AND);
        extensionNode.setChild(tmpRoot);
        processSecondLevelForms(tmpRoot, form, tempExtension.getRequestedType());
      }
      return;
    }

    if (tempExtension != null) {
      extensionNode = new ExtensionNode(tempResultType, tempExtension);
      tempRoot.addChild(extensionNode);
      OperationNode newRoot = new OperationNode(Operation.AND);
      extensionNode.setChild(newRoot);
      tempRoot = newRoot;
      tempResultType = tempExtension.getRequestedType();
    }

    if (input.getStatusQueryData() != null) {
      processStatusQueryData(tempRoot, form, null, tempResultType);
    }

    if (input.getSealQueryData() != null) {
      processSealQueryData(tempRoot, form, null, tempResultType);
    }

    if (input.getTimespanQueryData() != null) {
      processTimespanQueryData(tempRoot, form, null, tempResultType);
    }

    for (QFirstLevel firstLevel : input.getQueryFirstLevels()) {
      buildPartOfQuery(tempRoot, form, tempResultType, null, firstLevel);
    }

    // If this form represents an extension and the root node for that extension
    // (which by default is an 'and' node) has only one child, throw away the 'and' node.
    if ((extensionNode != null) && (tempRoot.getChildren().size() == 1)) {
      extensionNode.setChild(tempRoot.getChildren().get(0));
    }

    processSecondLevelForms(tempRoot, form, tempResultType);
    LOGGER.debug("Finished processing form.");
  }

  private void buildPartOfQuery(OperationNode root, DynamicQueryFormData<?> form, Type<?> resultType, Extension extension, QFirstLevel firstLevel) {
    OperationNode or = new OperationNode(Operation.OR);
    for (QPart qpart : firstLevel.getQuerySecondLevels()) {
      String stringID = qpart.getChosenAttributeStringId();
      BBAttribute attribute = form.getBBAttributeByStringId(stringID);

      if (attribute != null) {
        Comparator comparator;
        int attributeTypeId = attribute.getId().intValue();

        if (BBAttribute.FIXED_ATTRIBUTE_ENUM.equals(attribute.getType())) {
          or.addChild(processFixedEnumAttribute(resultType, extension, qpart));
        }
        if (BBAttribute.FIXED_ATTRIBUTE_TYPE.equals(attribute.getType())) {
          or.addChild(processFixedAttribute(resultType, extension, qpart));
        }
        else if (BBAttribute.FIXED_ATTRIBUTE_DATETYPE.equals(attribute.getType())) {
          or.addChild(processDateAttribute(resultType, extension, qpart));
        }
        else if (BBAttribute.FIXED_ATTRIBUTE_SET.equals(attribute.getType())) {
          or.addChild(processSetFixedAttribute(resultType, extension, qpart));
        }
        else if (BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE.equals(attribute.getType())) {
          comparator = QueryTreeGenerator.getComparatorForId(qpart.getChosenOperationId());

          //due to the fact that if it's searched for arbitrary value in any BBE then no 
          //search criteria is selected and the search pattern is null, so NullPointerException is thrown
          if (qpart.getPattern() == null) {
            or.addChild(EnumAttributeLeafNode.createNode(resultType, extension, attributeTypeId, comparator, ""));
          }
          else {
            or.addChild(EnumAttributeLeafNode.createNode(resultType, extension, attributeTypeId, comparator, qpart.getPattern()));
          }
        }
        else if (BBAttribute.USERDEF_NUMBER_ATTRIBUTE_TYPE.equals(attribute.getType())) {
          comparator = QueryTreeGenerator.getComparatorForId(qpart.getChosenOperationId());
          or.addChild(NumberAttributeLeafNode.createNode(resultType, extension, attributeTypeId, comparator, qpart.getPattern(), locale));
        }
        else if (BBAttribute.USERDEF_TEXT_ATTRIBUTE_TYPE.equals(attribute.getType())) {
          comparator = QueryTreeGenerator.getComparatorForId(qpart.getChosenOperationId());
          if (qpart.getPattern() == null) {
            or.addChild(TextAttributeLeafNode.createNode(resultType, extension, attributeTypeId, comparator, ""));
          }
          else {
            or.addChild(TextAttributeLeafNode.createNode(resultType, extension, attributeTypeId, comparator, qpart.getPattern()));
          }
        }
        else if (BBAttribute.USERDEF_DATE_ATTRIBUTE_TYPE.equals(attribute.getType())) {
          comparator = QueryTreeGenerator.getComparatorForId(qpart.getChosenOperationId());
          or.addChild(DateAttributeLeafNode.createNode(resultType, extension, attributeTypeId, comparator, qpart.getPattern(), locale));
        }
        else if (BBAttribute.USERDEF_RESPONSIBILITY_ATTRIBUTE_TYPE.equals(attribute.getType())) {
          comparator = QueryTreeGenerator.getComparatorForId(qpart.getChosenOperationId());
          if (qpart.getPattern() == null) {
            or.addChild(ResponsibilityAttributeLeafNode.createNode(resultType, extension, attributeTypeId, comparator, ""));
          }
          else {
            or.addChild(ResponsibilityAttributeLeafNode.createNode(resultType, extension, attributeTypeId, comparator, qpart.getPattern()));
          }
        }
      }
    }

    if (!or.getChildren().isEmpty()) {
      // If there is just one child, throw away the 'or' node.
      if (or.getChildren().size() == 1) {
        root.addChild(or.getChildren().get(0));
      }
      else {
        root.addChild(or);
      }
    }
  }

  /**
   * This method adds new condition and operation nodes to the given root node for all second level
   * query forms found in the given first level query form.
   *
   * @param root
   *          An operation node to which other nodes are added with respect to the given form.
   * @param form
   *          The DynamicQueryFormData instance describing a piece of user input.
   * @param resType
   *          The expected resultType for root (@see
   *          de.iteratec.iteraplan.businesslogic.reports.query.node.AbstractLeafNode).
   * @throws IteraplanException
   */
  private void processSecondLevelForms(OperationNode root, DynamicQueryFormData<?> form, Type<?> resType) throws IteraplanException {
    LOGGER.debug("Starting to process second level forms.");

    for (DynamicQueryFormData<?> secondLevelForm : form.getSecondLevelQueryForms()) {
      if (secondLevelForm.getExtension() instanceof Extension) {
        Extension secondLevelExtension = (Extension) secondLevelForm.getExtension();
        processForm(root, secondLevelForm, resType, secondLevelExtension);
      }
      else {
        String errorMessage = "Found ComplexExtension in second level DynamicQueryFormData instance. "
            + "Complex Extensions are only allowed in first level DynamicQueryFormData instances.";
        LOGGER.error(errorMessage);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }
    }

    LOGGER.debug("Finished processing second level forms.");
  }

  /**
   * Creates a new {@link SetPropertyLeafNode} for the given query condition.
   *
   * @param resType
   *          The overall result type
   * @param extension
   *          The extension this query condition is part of
   * @param qpart
   *          The query condition
   * @return The newly created PropertyLeafNode
   * @throws IteraplanException
   */
  private Node processSetFixedAttribute(Type<?> resType, Extension extension, QPart qpart) throws IteraplanException {
    Comparator c = QueryTreeGenerator.getComparatorForId(qpart.getChosenOperationId());
    String chosenProperty = BBAttribute.getDbNameByStringId(qpart.getChosenAttributeStringId());

    return new SetPropertyLeafNode(resType, extension, chosenProperty, c, qpart.getPattern());
  }

  /**
   * Creates a new {@link PropertyLeafNode} for the given query condition.
   *
   * @param resType
   *          The overall result type
   * @param extension
   *          The extension this query condition is part of
   * @param qpart
   *          The query condition
   * @return The newly created PropertyLeafNode
   * @throws IteraplanException
   */
  private Node processFixedAttribute(Type<?> resType, Extension extension, QPart qpart) throws IteraplanException {
    Comparator c = QueryTreeGenerator.getComparatorForId(qpart.getChosenOperationId());
    String chosenProperty = BBAttribute.getDbNameByStringId(qpart.getChosenAttributeStringId());

    return new PropertyLeafNode(resType, extension, chosenProperty, c, qpart.getPattern(), BBAttribute.FIXED_ATTRIBUTE_TYPE);
  }

  private Node processFixedEnumAttribute(Type<?> resType, Extension extension, QPart qpart) throws IteraplanException {
    Comparator c = QueryTreeGenerator.getComparatorForId(qpart.getChosenOperationId());
    String chosenProperty = BBAttribute.getDbNameByStringId(qpart.getChosenAttributeStringId());

    return new PropertyLeafNode(resType, extension, chosenProperty, c, qpart.getPattern(), BBAttribute.FIXED_ATTRIBUTE_ENUM);
  }

  /**
   * Creates a new {@link PropertyLeafNode} for the given query condition.
   *
   * @param resType The overall result type
   * @param extension The extension this query condition is part of
   * @param qpart The query condition
   * @return The newly created PropertyLeafNode
   * @throws IteraplanException
   */
  private Node processDateAttribute(Type<?> resType, Extension extension, QPart qpart) throws IteraplanException {
    Node node = null;
    String chosenProperty = BBAttribute.getDbNameByStringId(qpart.getChosenAttributeStringId());
    Comparator c = QueryTreeGenerator.getComparatorForId(qpart.getChosenOperationId());
    if (c.equals(Comparator.EQ)) {
      // date has to be parsed
      OperationNode andNode = new OperationNode(Operation.AND);
      Date date = DateUtils.parseAsDate(qpart.getPattern(), locale);
      Date nextDateStart = DateUtils.shiftDate(date, 1, locale);
      DateUtils.formatAsString(date, locale);

      andNode.addChild(new PropertyLeafNode(resType, extension, chosenProperty, Comparator.GT, qpart.getPattern(),
          BBAttribute.FIXED_ATTRIBUTE_DATETYPE));
      andNode.addChild(new PropertyLeafNode(resType, extension, chosenProperty, Comparator.LT, DateUtils.formatAsString(nextDateStart, locale),
          BBAttribute.FIXED_ATTRIBUTE_DATETYPE));
      node = andNode;
    }
    else {
      node = new PropertyLeafNode(resType, extension, chosenProperty, c, qpart.getPattern(), BBAttribute.FIXED_ATTRIBUTE_DATETYPE);
    }

    return node;
  }

  /**
   * Converts query data, which is held in an {@link IQStatusData} object into a appropriate
   * query tree.
   *
   * @param root the node onto which the resulting query tree is attached.
   * @param form the form that contains the {@link QSealStatus}.
   * @param extension the extension the TimespanQueryData is part of.
   * @param resType the overall result type
   * @throws IteraplanException
   */
  private void processStatusQueryData(OperationNode root, DynamicQueryFormData<?> form, Extension extension, Type<?> resType) {
    LOGGER.debug("Entering: processStatusQueryData()");

    Type<?> type = form.getType();
    QUserInput input = form.getQueryUserInput();
    IQStatusData statusQueryData = input.getStatusQueryData();

    OperationNode or = new OperationNode(Operation.OR);
    ITypeWithStatus typeWithStatus = (ITypeWithStatus) type;
    String property = typeWithStatus.getTypeOfStatusProperty();
    Map<?, Boolean> map = statusQueryData.getStatusMap();
    for (Map.Entry<?, Boolean> entry : map.entrySet()) {
      if (entry.getValue().equals(Boolean.TRUE)) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("- Add child to OR-Node: " + entry.getKey() + " with value " + entry.getValue());
        }

        or.addChild(new PropertyLeafNode(resType, extension, property, Comparator.LIKE, entry.getKey(), BBAttribute.FIXED_ATTRIBUTE_TYPE));
      }
    }

    // if just one status is set, dispose of 'or' node
    if (or.getChildren().size() == 1) {
      LOGGER.debug("- Dispose of OR-Node: Only one status set");
      root.addChild(or.getChildren().get(0));
    }
    else {
      LOGGER.debug("- Add child to Root-Node: OR-Node");
      root.addChild(or);
    }

    LOGGER.debug("Leaving: processStatusQueryData()");
  }

  /**
   * Converts query data, which is held in an {@link QSealStatus} object into a appropriate
   * query tree.
   *
   * @param root the node onto which the resulting query tree is attached.
   * @param form the form that contains the {@link QSealStatus}.
   * @param extension the extension the TimespanQueryData is part of.
   * @param resType the overall result type
   * @throws IteraplanException
   */
  private void processSealQueryData(OperationNode root, DynamicQueryFormData<?> form, Extension extension, Type<?> resType) {
    QUserInput input = form.getQueryUserInput();
    QSealStatus statusQueryData = input.getSealQueryData();

    OperationNode or = new OperationNode(Operation.OR);
    Map<SealState, Boolean> map = statusQueryData.getStatusMap();
    for (Map.Entry<SealState, Boolean> entry : map.entrySet()) {
      if (entry.getValue().equals(Boolean.TRUE)) {
        or.addChild(new SealLeafNode(resType, extension, "state", entry.getKey()));
      }
    }

    // if just one status is set, dispose of 'or' node
    if (or.getChildren().size() == 1) {
      root.addChild(or.getChildren().get(0));
    }
    else if (!or.getChildren().isEmpty()) {
      root.addChild(or);
    }
  }

  /**
   * Converts query data, which is held in an {@link TimespanQueryData} object into a appropriate
   * query tree.
   *
   * @param root The node onto which the resulting query tree is attached.
   * @param form The form that contains the TimespanQueryData.
   * @param extension The extension the TimespanQueryData is part of.
   * @param resType The overall result type
   * @throws IteraplanException
   */
  private void processTimespanQueryData(OperationNode root, DynamicQueryFormData<?> form, Extension extension, Type<?> resType)
      throws IteraplanException {

    Type<?> type = form.getType();
    QUserInput input = form.getQueryUserInput();
    QTimespanData timespanQueryData = input.getTimespanQueryData();

    if (timespanQueryData.isStartDateSet()) {
      // non-strict start date matching: get all elements with endDate >= getStartDateAsString()
      ITypeWithDates typeWithDates = (ITypeWithDates) type;
      // String property = typeWithDates.getEndDateProperty();
      String property = typeWithDates.getStartDateProperty();
      Comparator comparator = Comparator.LEQ;
      Date date = timespanQueryData.getStartDate();

      LOGGER.debug("- Add child 'startDate' to Root-Node");

      root.addChild(new PropertyLeafNode(resType, extension, property, comparator, date, BBAttribute.FIXED_ATTRIBUTE_DATETYPE));
    }

    if (timespanQueryData.isEndDateSet()) {
      // non-strict end date matching: get all elements with startDate <= getEndDateAsString()
      ITypeWithDates typeWithDates = (ITypeWithDates) type;
      // String property = typeWithDates.getStartDateProperty();
      String property = typeWithDates.getEndDateProperty();
      Comparator comparator = Comparator.GEQ;
      Date date = timespanQueryData.getEndDate();

      LOGGER.debug("- Add child 'endDate' to Root-Node");

      root.addChild(new PropertyLeafNode(resType, extension, property, comparator, date, BBAttribute.FIXED_ATTRIBUTE_DATETYPE));
    }
  }

  /**
   * Maps operation ids from the GUI to {@link Comparator} objects used within the query tree.
   *
   * @param operationId
   *          The id of the operation.
   * @return The corresponding Comparator object.
   * @throws IteraplanException
   * @see AttributeLeafNode
   * @see PropertyLeafNode
   */
  public static Comparator getComparatorForId(Integer operationId) throws IteraplanException {
    if (Constants.OPERATION_CONTAINS_ID.equals(operationId) || Constants.OPERATION_EQUALS_ID.equals(operationId)
        || Constants.OPERATION_STARTSWITH_ID.equals(operationId) || Constants.OPERATION_ENDSWITH_ID.equals(operationId)) {
      return Comparator.LIKE;
    }
    if (Constants.OPERATION_CONTAINSNOT_ID.equals(operationId) || Constants.OPERATION_EQUALSNOT_ID.equals(operationId)) {
      return Comparator.NOT_LIKE;
    }
    if (Constants.OPERATION_GT_ID.equals(operationId) || Constants.OPERATION_AFTER_ID.equals(operationId)) {
      return Comparator.GT;
    }
    if (Constants.OPERATION_GEQ_ID.equals(operationId)) {
      return Comparator.GEQ;
    }
    if (Constants.OPERATION_EQ_ID.equals(operationId) || Constants.OPERATION_ON_ID.equals(operationId)) {
      return Comparator.EQ;
    }
    if (Constants.OPERATION_LEQ_ID.equals(operationId)) {
      return Comparator.LEQ;
    }
    if (Constants.OPERATION_LT_ID.equals(operationId) || Constants.OPERATION_BEFORE_ID.equals(operationId)) {
      return Comparator.LT;
    }
    if (Constants.OPERATION_NOENTRIES_ID.equals(operationId)) {
      return Comparator.NO_ASSIGNMENT;
    }
    if (Constants.OPERATION_ANYENTRIES_ID.equals(operationId)) {
      return Comparator.ANY_ASSIGNMENT;
    }

    throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
  }

  /**
   * Prints the generated query tree for debugging purposes.
   *
   * @param rootNode
   *          The root node of the query tree.
   */
  private void printTree(Node rootNode) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("-------------------------------------------");
      LOGGER.debug("-----------Generated Query Tree------------");
      LOGGER.debug("-------------------------------------------");
      printNodes(rootNode, "");
      LOGGER.debug("-------------------------------------------");
    }
  }

  /**
   * Prints the generated query tree for debugging purposes.
   *
   * @param rootNode
   *          The root node of the query tree to print.
   * @param indentation
   *          The current indentation.
   */
  private void printNodes(Node rootNode, String indentation) {
    String indent = indentation;
    if (rootNode instanceof OperationNode) {
      OperationNode node = (OperationNode) rootNode;
      LOGGER.debug(indent + node.getOperation().toString());
      indent += "   ";
      for (Node childNode : node.getChildren()) {
        printNodes(childNode, indent);
      }
    }

    if (rootNode instanceof ExtensionNode) {
      ExtensionNode en = (ExtensionNode) rootNode;
      LOGGER.debug(indent + en);
      indent += "   ";
      printNodes(en.getChild(), indent);
    }

    if (rootNode instanceof AbstractLeafNode) {
      AbstractLeafNode abstractLeafNode = (AbstractLeafNode) rootNode;
      Type<?> type = abstractLeafNode.getExtension() == null ? abstractLeafNode.getResultType() : abstractLeafNode.getExtension().getRequestedType();
      String typeName = type.getTypeNameDB();

      if (rootNode instanceof PropertyLeafNode) {
        PropertyLeafNode pln = (PropertyLeafNode) rootNode;
        StringBuilder builder = new StringBuilder();
        builder.append(indent).append(typeName).append(" ");
        builder.append(pln.getPropertyName()).append(" ");
        builder.append(pln.getComparator()).append(" ");
        builder.append(pln.getPattern());
        LOGGER.debug(builder.toString());
      }
      else if (rootNode instanceof AttributeLeafNode) {
        AttributeLeafNode aln = (AttributeLeafNode) rootNode;
        if (aln.getComparator() != null) {
          StringBuilder builder = new StringBuilder();
          builder.append(indent).append(typeName).append(" attributeTypeID ");
          builder.append(aln.getAttributeId()).append(" ");
          builder.append(aln.getComparator()).append(" ");
          builder.append(aln.getPattern());
          LOGGER.debug(builder.toString());
        }
        else {
          StringBuilder builder = new StringBuilder();
          builder.append(indent).append(typeName).append(" attributeTypeID ");
          builder.append(aln.getAttributeId()).append(" (no values");
          LOGGER.debug(builder.toString());
        }
      }
      else {
        LOGGER.debug(indent + rootNode);
      }
    }
  }
}