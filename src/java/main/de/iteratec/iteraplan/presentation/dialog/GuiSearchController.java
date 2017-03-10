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
package de.iteratec.iteraplan.presentation.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.beans.support.SortDefinition;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.exchange.nettoExport.NettoExporter;
import de.iteratec.iteraplan.businesslogic.exchange.nettoExport.OverviewPageTableStructure;
import de.iteratec.iteraplan.businesslogic.exchange.nettoExport.TableStructure;
import de.iteratec.iteraplan.businesslogic.reports.query.QueryTreeGenerator;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QPart;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QTimespanData;
import de.iteratec.iteraplan.businesslogic.reports.query.type.QueryTypeHelper;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.HierarchicalBuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.InitFormHelperService;
import de.iteratec.iteraplan.businesslogic.service.QueryService;
import de.iteratec.iteraplan.businesslogic.service.RefreshHelperService;
import de.iteratec.iteraplan.businesslogic.service.SavedQueryService;
import de.iteratec.iteraplan.businesslogic.service.SearchService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.dto.SavedQueryDTO;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.PresentationHelper;
import de.iteratec.iteraplan.presentation.SessionConstants;
import de.iteratec.iteraplan.presentation.dialog.TabularReporting.TabularReportingFrontendService;
import de.iteratec.iteraplan.presentation.memory.ColumnDefinition;
import de.iteratec.iteraplan.presentation.memory.SearchDialogMemory;
import de.iteratec.iteraplan.presentation.memory.SearchDialogMemory.QueryFormProperties;
import de.iteratec.iteraplan.presentation.memory.TreeViewHelper;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator.Content;
import de.iteratec.iteraplan.presentation.responsegenerators.TabularResponseGenerator.TabularResultFormat;


/**
 * This is the base controller for all overview pages.
 * @param <T>
 */
@SuppressWarnings("PMD.TooManyMethods")
public abstract class GuiSearchController<T extends SearchDialogMemory> extends GuiController {

  private static final Logger             LOGGER        = Logger.getIteraplanLogger(GuiSearchController.class);

  private boolean                         errorOccurred = false;

  @Autowired
  private InitFormHelperService           initFormHelperService;

  @Autowired
  private TabularReportingFrontendService tabularReportingFrontendService;

  @Autowired
  private SavedQueryService               savedQueryService;

  @Autowired
  private SearchService                   searchService;

  @Autowired
  private RefreshHelperService            refreshHelperService;

  @Autowired
  private AttributeTypeService            attributeTypeService;

  @Autowired
  private QueryService                    queryService;

  @Autowired
  private BuildingBlockServiceLocator     buildingBlockServiceLocator;

  /** 
   *  All search/lookup strategies, which can be handled by this controller.
   *  Can be validated in JSPs using dialog memory's activeSearchMethod property.
   */
  public enum SearchStrategy {
    SEARCH_BY_QUERY_FORM, SEARCH_BY_SAVED_QUERY, SEARCH_BY_INDEX, SEARCH_ALL
  }

  /**
   * Returns a fresh instance of the page-specific dialog memory.
   */
  protected abstract T getDialogMemory();

  /**
   * must return the name that is used in the views as the prefix i.e. a view definition might be
   * mapped to "architecturaldomain/init", thus the architecturalDomainController should return
   * "architecturaldomain"
   * 
   * @return the name of the base view
   */
  protected abstract String getBaseViewMapping();

  /**
   * Needed to correctly load saved queries for the specific Controller. I.e. only load
   * ArchitecturalDomain Saved queries for the ArchitecturalDomainController
   * 
   * @return the TypeOf BuildingBlock for BuildingBlock-Controllers, otherwise null is fine
   */
  protected abstract TypeOfBuildingBlock getTob();

  /**
   * Specific search for all types. 
   * @return
   *    The database search result containing all instances of this type
   */
  protected abstract PagedListHolder<?> searchAll(T dialogMemory, ModelMap model);

  /**
   *  Building block type controllers should override this method.
   */
  protected void addBuildingBlockTypeToModel(ModelMap model) {
    // override for building block types
  }

  @Override
  public ModelAndView handleIteraplanException(Throwable ex, HttpServletRequest req, HttpServletResponse resp) {
    // set flag for reset
    errorOccurred = true;

    // the newly created ModelAndView needs some of the attributes stored here, e.g. the building block type, if available
    ModelAndView mav = super.handleIteraplanException(ex, req, resp);
    addBuildingBlockTypeToModel(mav.getModelMap());

    return mav;
  }

  /**
   * initialize our dialog memory with the element that should be displayed at the top of the hierarchy view
   * @param searchDialogMemory
   */
  protected abstract void initHierarchicalTopElement(SearchDialogMemory searchDialogMemory);

  /**
   * Need to disable automatic data binding, because implementing a default
   * constructor for attribute DynamicQueryFormData#queryForm is not an
   * option.
   */
  @InitBinder
  protected void initBinder(WebDataBinder binder) {
    binder.setIgnoreInvalidFields(true);

  }

  /**
   * Request mapping method for requests coming outside this page to this page (new or returning).
   * 
   * {@inheritDoc}
   */
  @Override
  @RequestMapping
  protected void init(ModelMap model, HttpSession session, HttpServletRequest request) {
    super.init(model, session, request);

    T dialogMemory = getOrCreateDialogMemory();

    mainProcessing(dialogMemory, model);

    this.updateGuiContext(dialogMemory);
    model.addAttribute(SessionConstants.DIALOG_MEMORY_LABEL, dialogMemory);
  }

  @RequestMapping
  protected String resetSearch(@ModelAttribute(SessionConstants.DIALOG_MEMORY_LABEL) T dialogMemory) {
    initNewDialogMemoryFromGuiContext(dialogMemory);
    T oldDialogMemory = getOldDialogMemory();
    dialogMemory.setPageSize(oldDialogMemory.getPageSize());
    updateGuiContext(dialogMemory);
    return "redirect:/" + getBaseViewMapping() + "/init.do";
  }

  /**
   * Request mapping method for requests from inside this page.
   * 
   * @see #initBinder(WebDataBinder) for details regarding why we need to send seperate request params for queryForm
   */
  @RequestMapping
  protected String search(@ModelAttribute(SessionConstants.DIALOG_MEMORY_LABEL) T dialogMemory,
                          @RequestParam(value = "queryForm.queryUserInput.queryFirstLevels[0].querySecondLevels[0].chosenAttributeStringId", required = false) String chosenAttributeStringId,
                          @RequestParam(value = "queryForm.queryUserInput.queryFirstLevels[0].querySecondLevels[0].chosenOperationId", required = false) Integer chosenOperationId,
                          @RequestParam(value = "queryForm.queryUserInput.queryFirstLevels[0].querySecondLevels[0].freeTextCriteriaSelected", required = false) Boolean freeTextCriteriaSelected,
                          @RequestParam(value = "queryForm.queryUserInput.queryFirstLevels[0].querySecondLevels[0].existingCriteria", required = false) String existingCriteria,
                          @RequestParam(value = "queryForm.queryUserInput.queryFirstLevels[0].querySecondLevels[0].freeTextCriteria", required = false) String freeTextCriteria,
                          @RequestParam(value = "colMoveIndex", required = false) Integer colMoveIndex,
                          @RequestParam(value = "colRemoveIndex", required = false) Integer colRemoveIndex,
                          @RequestParam(value = "colMoveDirection", required = false) String colMoveDirection,
                          @RequestParam(value = "colToAdd", required = false) Integer colToAdd,
                          @RequestParam(value = "colSortIndex", required = false) Integer colSortIndex,
                          @RequestParam(value = "showTreeView", required = false) Boolean showTreeView,
                          @RequestParam(value = "sortByPosition", required = false) Boolean sortByPosition,
                          @RequestParam(value = "enableReorder", required = false) Boolean enableReorder,
                          @RequestParam(value = "loadAllFrom", required = false) Integer loadAllFrom,
                          @RequestParam(value = "format", required = false) String downloadFormat, ModelMap model, HttpSession session) {

    //bind the query form params to QueryFormProperties
    QueryFormProperties queryFormProperties = new QueryFormProperties(chosenAttributeStringId, chosenOperationId, freeTextCriteriaSelected,
        existingCriteria, freeTextCriteria);
    dialogMemory.setQueryFormProperties(queryFormProperties);

    // bind the table action(s) params to TableActionParameters
    TableActionParams tableActionParameters = new TableActionParams(colSortIndex, colToAdd, colMoveIndex, colMoveDirection, colRemoveIndex,
        showTreeView, sortByPosition, loadAllFrom);
    dialogMemory.setTableActionParameter(tableActionParameters);

    initNewDialogMemoryFromGuiContext(dialogMemory);

    /* Disable/Enable reordering  */
    if (enableReorder != null) {
      LOGGER.debug((enableReorder.booleanValue() ? "Enable" : "Disable") + " reordering.");
      dialogMemory.getTreeViewHelper().setReorderingEnabled(enableReorder.booleanValue());
    }

    updateGuiContext(dialogMemory);

    if (downloadFormat != null) {
      model.addAttribute("format", downloadFormat);
      return "redirect:/" + getBaseViewMapping() + "/download.do";
    }
    return "redirect:/" + getBaseViewMapping() + "/init.do";
  }

  @RequestMapping
  protected void download(@RequestParam(value = "format", required = true) String downloadFormat, ModelMap model, HttpSession session,
                          HttpServletRequest request, HttpServletResponse response) {
    model.addAttribute("componentMode", "READ");
    T dialogMemory = getOrCreateDialogMemory();
    mainProcessing(dialogMemory, model);

    List<?> sourceList = dialogMemory.getResultList().getSource();
    TableStructure tableStructure = new OverviewPageTableStructure(dialogMemory.getTableState());

    TypeOfBuildingBlock tob = getTob();
    if (tob == null) {
      LOGGER.error("Can't determine type of building block for the download of the current selection.");
      throw new IteraplanTechnicalException();
    }

    NettoExporter exporter = NettoExporter.newInstance(tob, downloadFormat);
    exporter.exportToResponse(sourceList, tableStructure, response);
  }

  /** init values in new DialogMemory from old instance in GuiContext */
  private void initNewDialogMemoryFromGuiContext(T newDialogMemory) {

    T oldDialogMemory = getOldDialogMemory();

    if (oldDialogMemory != null) {
      newDialogMemory.setTreeViewHelper(oldDialogMemory.getTreeViewHelper());
      newDialogMemory.setTreeView(oldDialogMemory.isTreeView());
    }
  }

  /**
   * @return the dialog memory currently in the gui context
   */
  @SuppressWarnings("unchecked")
  private T getOldDialogMemory() {
    GuiContext currentGuiContext = GuiContext.getCurrentGuiContext();
    String activeDialogName = currentGuiContext.getActiveDialogName();
    return (T) currentGuiContext.getDialogMemory(activeDialogName);
  }

  /**
   * Main search sequence: PreProcessing(s) -> Search -> PostProcessing(s) 
   */
  private void mainProcessing(T dialogMemory, ModelMap model) {
    initHierarchicalTopElement(dialogMemory);
    initTableState(dialogMemory);
    initColumnSortDefinition(dialogMemory);

    if (dialogMemory.getTableActionParameter() != null) {
      checkAndPerformTableActions(dialogMemory);
    }

    initBuildingBlockSpecificSettings(dialogMemory, model);

    if (dialogMemory.getQueryForm() != null) {
      loadQueryForm(dialogMemory, model);
    }

    PagedListHolder<?> results = performSearchAndGetResults(dialogMemory, model);

    applySortDefinition(results, dialogMemory.getTableState().getSortDefinition());
    performPaginationCalculation(dialogMemory, results);

    model.addAttribute(SessionConstants.MVC_SEARCH_RESULT_LIST, results);
    dialogMemory.setResultList(results);

    initTreeView(dialogMemory);
  }

  /**
   *  Perform Ajax action(s).
   * Usually no specific response is needed here (=> empty body).
   * */
  @RequestMapping
  public @ResponseBody
  String tree(@RequestParam(value = "collapseAll", required = false) Boolean collapseAll,
              @RequestParam(value = "expandAll", required = false) Boolean expandAll,
              @RequestParam(value = "collapseId", required = false) Integer collapseId,
              @RequestParam(value = "sortItem", required = false) Integer sortItem,
              @RequestParam(value = "afterItem", required = false) Integer afterItem,
              @RequestParam(value = "beforeItem", required = false) Integer beforeItem,
              @RequestParam(value = "expandId", required = false) Integer expandId, ModelMap model, HttpSession session) {

    T dialogMemory = getOrCreateDialogMemory();
    TreeViewHelper treeViewHelper = dialogMemory.getTreeViewHelper();

    if (treeViewHelper == null) {
      LOGGER.debug("No TreeViewHelper found! Return without processing tree action.");
      // TODO throw new IteraplanTechnicalException 
      return "";
    }

    /* Reorder BuildingBlock hierarchy */
    if (sortItem != null) {

      // activate reordering in the response page
      treeViewHelper.setReorderingEnabled(true);

      BuildingBlockService<BuildingBlock, Integer> service = buildingBlockServiceLocator.getService(getTob());
      HierarchicalBuildingBlockService<?, Integer> hbbService = (HierarchicalBuildingBlockService<?, Integer>) service;

      boolean success = false;
      if (beforeItem != null) {
        success = hbbService.saveReorderMove(sortItem, beforeItem, false);
      }
      else if (afterItem != null) {
        success = hbbService.saveReorderMove(sortItem, afterItem, true);
      }
      else {
        LOGGER.error("Missing reorder destination!");
        // TODO throw new IteraplanTechnicalException 
      }

      if (!success) {
        throw new IteraplanTechnicalException();
      }
    }

    /* Collapse all nodes. */
    if (collapseAll != null && collapseAll.booleanValue()) {
      LOGGER.debug("Collapse all nodes in tree view.");
      treeViewHelper.collapseAll();
    }

    /* Expand all nodes. */
    if (expandAll != null && expandAll.booleanValue()) {
      LOGGER.debug("Expand all nodes in tree view.");
      treeViewHelper.expandAll();
    }

    /* Collapse a specific node. */
    if (collapseId != null) {
      LOGGER.debug("Collapse node with ID:" + collapseId + " in tree view.");
      treeViewHelper.setCollapseNode(collapseId, true);
    }

    /* Expand a specific node. */
    if (expandId != null) {
      LOGGER.debug("Expand node with ID:" + collapseId + " in tree view.");
      treeViewHelper.setCollapseNode(expandId, false);
    }

    // return empty response body, when nothing else is needed
    // => leads to a HTTP response code 200  and thus:  $.ajax success
    // to trigger $.ajax fail, throw an exception (which returns a HTTP error code)
    return "";
  }

  private void initTreeView(T dialogMemory) {
    if (dialogMemory.isTreeView() && dialogMemory.isHierarchical()) {
      dialogMemory.initTreeViewHelper();

      TreeViewHelper treeViewHelper = dialogMemory.getTreeViewHelper();
      treeViewHelper.setReorderingEnabled(treeViewHelper.isReorderingPossible() && treeViewHelper.isReorderingEnabled());
    }
  }

  /**
   * Helper method to provide a dialog memory instance.
   * 
   * @return
   *    Dialog memory for this controller, either from GuiContext or created/initalized a new one. 
   */
  @SuppressWarnings("unchecked")
  private T getOrCreateDialogMemory() {
    GuiContext guiContext = GuiContext.getCurrentGuiContext();

    T dialogMemory;
    if (guiContext.hasDialogMemory(getDialogName()) && !errorOccurred) {
      // if present and not leading to an error, use context data instead of creating new dialog memory
      dialogMemory = (T) guiContext.getDialogMemory(getDialogName());
    }
    else {
      errorOccurred = false;

      // create new dialog memory
      dialogMemory = getDialogMemory();
      dialogMemory.setPageSize(UserContext.getCurrentUserContext().getDefaultResultCount());
      dialogMemory.setPreviousPage("");
      dialogMemory.setNextPage("");
      dialogMemory.setNextPageToShow(Boolean.FALSE);
      dialogMemory.setCurrentPageNumber(0);
    }
    return dialogMemory;
  }

  /**
   * Some building block type specific initialisations.
   */
  private void initBuildingBlockSpecificSettings(T dialogMemory, ModelMap model) {

    if (isBuildingBlockType()) {
      addBuildingBlockTypeToModel(model);
      initSavedQueries(dialogMemory);
      initQueryForm(dialogMemory);
    }
  }

  /**
   * Set initial columns for this dialog.
   */
  private void initTableState(T dialogMemory) {
    GuiContext guiContext = GuiContext.getCurrentGuiContext();
    GuiTableState tableState;

    TableActionParams params = dialogMemory.getTableActionParameter();
    if (params != null) {
      Boolean showTreeView = params.getShowTreeView();
      if (showTreeView != null) {
        dialogMemory.setTreeView(showTreeView.booleanValue());
      }

      Integer loadAllId = params.getLoadAllFrom();
      if (loadAllId != null) {
        TreeViewHelper treeViewHelper = dialogMemory.getTreeViewHelper();
        if (treeViewHelper != null) {
          treeViewHelper.loadAllFrom(loadAllId);
        }
      }
    }

    if (guiContext.getTableState(getDialogName()) != null) {
      tableState = guiContext.getTableState(getDialogName());
    }
    else {
      tableState = new GuiTableState();
      tableState.addColumnEntries(dialogMemory.getInitialColumnDefinitions());

      if (isBuildingBlockType()) {
        List<AttributeType> attributeTypes = attributeTypeService.getAttributeTypesForTypeOfBuildingBlock(getTob(), true);
        for (AttributeType at : attributeTypes) {
          tableState.addColumnEntry(new ColumnDefinition(at, false));
        }
      }

      tableState.setSortDefinition(new MutableSortDefinition(true));
      guiContext.storeTableState(getDialogName(), tableState);
    }

    dialogMemory.setTableState(tableState);
  }

  /**
   *    Get/create SortDefinition for active dialog and store it in DialogMemory for this request.
   *    When a sort action happened, toggles the appropriate column in the sort definition.  
   *    If no sorting is defined, it sorts the first column ascending.
   */
  @SuppressWarnings("boxing")
  private void initColumnSortDefinition(T dialogMemory) {
    TableActionParams params = dialogMemory.getTableActionParameter();

    // Get/create SortDefinition for this Dialog and store it in DialogMemory for this request
    MutableSortDefinition sortDefinition = dialogMemory.getTableState().getSortDefinition();

    // check, if sorting is toggled
    List<ColumnDefinition> visibleColumnDefinitions = dialogMemory.getTableState().getVisibleColumnDefinitions();
    if (params != null && params.getColSortIndex() != null) {
      int sortIndex = params.getColSortIndex();

      if (sortIndex < 0) {
        sortIndex = 0;
      }

      if (sortIndex > (visibleColumnDefinitions.size() - 1)) {
        sortIndex = visibleColumnDefinitions.size() - 1;
      }

      ColumnDefinition sortColumnDefinition = visibleColumnDefinitions.get(sortIndex);
      sortDefinition.setProperty(sortColumnDefinition.getBeanPropertyPath());
      params.setColSortIndex(null);
    }
    else if (params != null && params.getSortByPosition() != null && params.getSortByPosition().booleanValue() && dialogMemory.isTreeView()) {
      sortDefinition.setProperty("position");
      sortDefinition.setAscending(true);
      params.setSortByPosition(null);
    }
    else {

      if (sortDefinition.getProperty() != null && "".equals(sortDefinition.getProperty()) && visibleColumnDefinitions != null
          && visibleColumnDefinitions.size() > 0) {

        // set sort definition to first column's property
        sortDefinition.setProperty(visibleColumnDefinitions.get(0).getModelPath());
      }
    }
  }

  /**
   * Perform table actions like add/move/remove column(s)
   * @param dialogMemory
   */
  @SuppressWarnings("boxing")
  private void checkAndPerformTableActions(T dialogMemory) {
    TableActionParams params = dialogMemory.getTableActionParameter();
    GuiTableState tableState = dialogMemory.getTableState();

    if (params.getColRemoveIndex() != null) {
      tableState.removeColumn(params.getColRemoveIndex());
      params.setColRemoveIndex(null);
    }
    else if (params.getColMoveIndex() != null && params.getColMoveDirection() != null) {
      if ("left".equals(params.getColMoveDirection())) {
        tableState.moveColumnLeft(params.getColMoveIndex());
      }
      else if ("right".equals(params.getColMoveDirection())) {
        tableState.moveColumnRight(params.getColMoveIndex());
      }

      params.setColMoveIndex(null);
      params.setColMoveDirection(null);
    }
    else if (params.getColToAdd() != null) {
      tableState.addColumn(params.getColToAdd());
      params.setColToAdd(null);
    }
  }

  /**
   *  Apply a sort definition to a PagedListHolder and force resorting of its source list.
   *    
   * @param pagedListHolder
   *            PagedListHolder to which the sort definition should be applied  
   * @param sortDefinition
   *            A SortDefinition to be applied to this PagedListHolder
   */
  private void applySortDefinition(PagedListHolder<?> pagedListHolder, SortDefinition sortDefinition) {

    // apply Sort Definition to resultList
    if (pagedListHolder != null && sortDefinition != null) {
      pagedListHolder.setSort(sortDefinition);
      pagedListHolder.resort();
    }
  }

  /**
   * Performs the appropriate search method and returns the results as PagedListHolder.
   * @return
   *    Search results as PagedListHolder.
   */
  private PagedListHolder<?> performSearchAndGetResults(T dialogMemory, ModelMap model) {
    // execute search all (which also handles filtering) if we are not operating on a BBType (e.g. user)
    // TODO avoid this special handling
    if (getTob() == null) {
      return searchAll(dialogMemory, model);
    }

    // perform appropriate search
    switch (dialogMemory.getActiveSearchStrategy()) {
      case SEARCH_BY_QUERY_FORM:
        // Database Search by Query Form
        return searchBuildingBlocksByQueryForm(dialogMemory);

      case SEARCH_BY_INDEX:
        // Lucene index search
        return searchBuildingBlocksFromIndex(dialogMemory);

      case SEARCH_BY_SAVED_QUERY:
        // Saved Query Search
        return searchBuildingBlocksBySavedQuery(dialogMemory);

      case SEARCH_ALL:
        // Default database search: lookup all
        return searchAll(dialogMemory, model);

      default:
        // Empty result list   
        return new PagedListHolder<BuildingBlock>();
    }
  }

  /**
   * Search strategy for FullText Search on the lucene index.
   */
  private PagedListHolder<?> searchBuildingBlocksFromIndex(T dialogMemory) {

    // lucene query
    List<BuildingBlock> buildingBlocks = searchService.getSearchBuildingBlocks(getTob(), dialogMemory.getFullTextSearchValue());

    PagedListHolder<BuildingBlock> resultList = new PagedListHolder<BuildingBlock>(buildingBlocks);

    return resultList;
  }

  /**
   * Search strategy for Attribute Search on the database (using a query form).
   */
  @SuppressWarnings("unchecked")
  private PagedListHolder<?> searchBuildingBlocksByQueryForm(T dialogMemory) {

    QueryTreeGenerator qtg = new QueryTreeGenerator(UserContext.getCurrentLocale(), attributeTypeService);

    List<DynamicQueryFormData<?>> queryForms = new ArrayList<DynamicQueryFormData<?>>();
    queryForms.add(dialogMemory.getQueryForm());

    Node node = qtg.generateQueryTree(queryForms);

    PagedListHolder<BuildingBlock> resultList;

    try {
      resultList = new PagedListHolder<BuildingBlock>(queryService.evaluateQueryTree(node, null, Collections.EMPTY_LIST));
    } catch (ClassCastException e) {
      // thrown if the input value could not be converted by hibernate to perform a valid query
      throw new IteraplanBusinessException(IteraplanErrorMessages.QUERY_NO_VALUE_PROVIDED, e);
    }

    return resultList;
  }

  /**
   * Search strategy for Saved Query Search.
   */
  @SuppressWarnings("unchecked")
  private PagedListHolder<?> searchBuildingBlocksBySavedQuery(T dialogMemory) {

    Integer id = PresentationHelper.parseId(dialogMemory.getQueryId());
    dialogMemory.setSavedQueryId(id);
    ManageReportMemoryBean mBean = loadSavedQueryById(id, null, null);
    dialogMemory.setSavedQueryName(mBean.getXmlQueryName());

    PagedListHolder<?> resultList = new PagedListHolder<BuildingBlock>((List<BuildingBlock>) mBean.getResults());

    return resultList;
  }

  /**
   * Calculates/sets the page size and calls further calculation, if a navigation action has been performed. 
   */
  private void performPaginationCalculation(T dialogMemory, PagedListHolder<?> results) {

    int pageSize = (dialogMemory.getPageSize() > 0) ? dialogMemory.getPageSize() : results.getNrOfElements();
    results.setPageSize(pageSize);

    if (dialogMemory.isNextPageToShow() != null && dialogMemory.isNextPageToShow().booleanValue()) {

      // for navigation actions with changed page number (e.g. goto first, last, next, previous, column sort)
      choosePageToShow(dialogMemory, results);
    }
    else {
      results.setPage(dialogMemory.getCurrentPageNumber());
    }
  }

  /**
   * Calculates the new page to show in the PagedListHolder, after a navigation action (like goto next, first, previous, last, sorting...) has been performed.
   */
  private void choosePageToShow(T searchDialogMemory, PagedListHolder<?> results) {

    int pageNumber = searchDialogMemory.getCurrentPageNumber();

    if (!StringUtils.isEmpty(searchDialogMemory.getNextPage()) && "next".equals(searchDialogMemory.getNextPage())) {
      results.setPage(pageNumber);
      results.nextPage();
      searchDialogMemory.setNextPage("");
    }
    else if (!StringUtils.isEmpty(searchDialogMemory.getPreviousPage()) && "previous".equals(searchDialogMemory.getPreviousPage())) {
      results.setPage(pageNumber);
      results.previousPage();
      searchDialogMemory.setPreviousPage("");
    }
    else if (!StringUtils.isEmpty(searchDialogMemory.getNextPage()) && "last".equals(searchDialogMemory.getNextPage())) {
      results.setPage(results.getLastLinkedPage());
    }
    else if (!StringUtils.isEmpty(searchDialogMemory.getPreviousPage()) && "first".equals(searchDialogMemory.getPreviousPage())) {
      results.setPage(results.getFirstLinkedPage());
    }

    searchDialogMemory.setCurrentPageNumber(results.getPage());
    searchDialogMemory.setNextPageToShow(Boolean.FALSE);
  }

  /**
   * Loads all saved queries for that type of building block and stores them in the dialog memory.
   */
  private void initSavedQueries(T searchDialogMemory) {

    if (savedQueryService != null) {
      ReportType sqt = ReportType.fromValue(getTob().toString());

      List<SavedQuery> savedQueries = savedQueryService.getSavedQueriesWithoutContent(sqt);
      List<SavedQueryDTO> savedQueryDtos = convertSavedQueriesToDtos(savedQueries);
      searchDialogMemory.setSavedQueries(savedQueryDtos);
    }
  }

  private List<SavedQueryDTO> convertSavedQueriesToDtos(List<SavedQuery> savedQueries) {
    List<SavedQueryDTO> result = Lists.newArrayList();
    for (SavedQuery savedQuery : savedQueries) {
      result.add(new SavedQueryDTO(savedQuery));
    }

    return result;
  }

  protected void initQueryForm(T searchDialogMemory) {
    Type<?> typeObject = QueryTypeHelper.getTypeObject(getTob());
    DynamicQueryFormData<?> reportForm = initFormHelperService.getReportForm(typeObject);
    searchDialogMemory.setQueryForm(reportForm);
  }

  /**
   * Loads query form and its properties. Returns "true",
   * if a valid/complete query form for the attribute search exists.
   * Implement in subclasses for specific query form preprocessing, i.e. in -realease types like IS or TC.
   * 
   * @return 
   *    true, if a valid query form has been loaded
   */
  protected boolean loadQueryForm(T dialogMemory, ModelMap model) {

    GuiContext guiContext = GuiContext.getCurrentGuiContext();
    boolean hasDialogMemoryInContext = guiContext.hasDialogMemory(getDialogName());

    if (dialogMemory.getQueryForm() == null) {
      initQueryForm(dialogMemory);
    }

    QueryFormProperties queryFormProperties = dialogMemory.getQueryFormProperties();

    Boolean resetAttributeSearch = dialogMemory.getResetAttributeSearch();
    boolean hasQueryFormBeenLoaded = false;

    if ((queryFormProperties != null) && (resetAttributeSearch == null || !resetAttributeSearch.booleanValue())) {

      setUpNewQueryForm(dialogMemory, queryFormProperties);
    }

    if (hasDialogMemoryInContext) { // update gui context
      SearchDialogMemory contextMemory = (SearchDialogMemory) guiContext.getDialogMemory(getDialogName());
      contextMemory.setQueryForm(dialogMemory.getQueryForm());
      contextMemory.setQueryFormProperties(queryFormProperties);
    }

    return hasQueryFormBeenLoaded;
  }

  private void setUpNewQueryForm(SearchDialogMemory searchDialogMemory, QueryFormProperties queryFormProperties) {
    //map request params to actual queryForm object
    DynamicQueryFormData<?> queryForm = searchDialogMemory.getQueryForm();
    QPart qPart = queryForm.getQueryUserInput().getQueryFirstLevels().get(0).getQuerySecondLevels().get(0);

    qPart.setChosenAttributeStringId(queryFormProperties.getChosenAttributeStringId());
    qPart.setChosenOperationId(queryFormProperties.getChosenOperationId());
    qPart.setFreeTextCriteriaSelected(queryFormProperties.getFreeTextCriteriaSelected());
    qPart.setExistingCriteria(queryFormProperties.getExistingCriteria());
    qPart.setFreeTextCriteria(queryFormProperties.getFreeTextCriteria());

    // remove date constraints (if applicable), so that no elements are filtered out unintentionally
    QTimespanData timespan = queryForm.getQueryUserInput().getTimespanQueryData();
    if (timespan != null) {
      timespan.setStartDateAsString("");
      timespan.setEndDateAsString("");
    }

    // refresh select option values
    refreshHelperService.refreshForm(queryForm);

    //update memory with new queryForm
    searchDialogMemory.setQueryForm(queryForm);
  }

  private ManageReportMemoryBean loadSavedQueryById(Integer id, HttpServletRequest request, HttpServletResponse response) {
    // get an initialized mBean, the BBtype doesn't really matter here
    ManageReportMemoryBean mBean = initFormHelperService.getInitializedReportMemBean(getTob().toString());
    mBean.setSavedQueryId(id);
    mBean = tabularReportingFrontendService.loadSavedQuery(mBean);
    mBean.setContent(Content.INLINE);

    // resultFormat is HTML, so no direct response writing is done
    // TODO maybe even introduce new resultFormat, that makes the FS return a list instead of a
    // filled mbean?
    mBean.getTabularOptions().setResultFormat(TabularResultFormat.HTML.getResultFormat());

    tabularReportingFrontendService.requestReport(mBean, request, response);
    return mBean;
  }

  protected boolean isEmptyQueryProperties(QueryFormProperties queryFormProperties) {
    if (queryFormProperties != null
        && ("blank_null_-1".equals(queryFormProperties.getChosenAttributeStringId()) || queryFormProperties.getChosenAttributeStringId() == null)
        && queryFormProperties.getChosenOperationId() == null && queryFormProperties.getExistingCriteria() == null
        && queryFormProperties.getFreeTextCriteria() == null) {
      return true;
    }
    return false;
  }

  /**
   * Uses method getTob() to determine, if the specific controller is for a building block type.
   * @return
   *    Returns true, if specific getTob() method doesn't return a null value.
   */
  private boolean isBuildingBlockType() {
    return getTob() != null;
  }

  public void setAttributeTypeService(AttributeTypeService attributeTypeService) {
    this.attributeTypeService = attributeTypeService;
  }

  public AttributeTypeService getAttributeTypeService() {
    return attributeTypeService;
  }

  public void setInitFormHelperService(InitFormHelperService initFormHelperService) {
    this.initFormHelperService = initFormHelperService;
  }

  public InitFormHelperService getInitFormHelperService() {
    return initFormHelperService;
  }

  public void setRefreshHelperService(RefreshHelperService refreshHelperService) {
    this.refreshHelperService = refreshHelperService;
  }

  public RefreshHelperService getRefreshHelperService() {
    return refreshHelperService;
  }

  public void setQueryService(QueryService queryService) {
    this.queryService = queryService;
  }

  public QueryService getQueryService() {
    return queryService;
  }

  public void setBuildingBlockServiceLocator(BuildingBlockServiceLocator buildingBlockServiceLocator) {
    this.buildingBlockServiceLocator = buildingBlockServiceLocator;
  }
}
