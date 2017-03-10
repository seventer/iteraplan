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
package de.iteratec.iteraplan.presentation.memory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.support.PagedListHolder;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.model.dto.SavedQueryDTO;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.presentation.dialog.GuiSearchController.SearchStrategy;
import de.iteratec.iteraplan.presentation.dialog.GuiTableState;
import de.iteratec.iteraplan.presentation.dialog.TableActionParams;


public abstract class SearchDialogMemory extends DialogMemory {

  /** Serialization version. */
  private static final long serialVersionUID = 8140682515441415829L;

  /**
   * A class to describe how a search criterion text field shall be rendered.
   * <p>
   * This class is used by JSPs to render text field with name reference, label text and a hint
   * text.
   */
  public static class Criterion {
    private final String name;
    private final String labelKey;
    private final String hintTextKey;

    /**
     * Creates a search criterion descriptor.
     * 
     * @param name
     *          Name of the search criterion. <b>Must</b> correspond to a property of the specific
     *          dialog memory, so that form contents can be posted back to the object.
     * @param labelKey
     *          A message key which can be dereferenced to the text box label
     * @param hintTextKey
     *          A message key which can be dereferenced to a slightly longer hint text for the text
     *          field.
     */
    public Criterion(String name, String labelKey, String hintTextKey) {
      this.name = name;
      this.labelKey = labelKey;
      this.hintTextKey = hintTextKey;
    }

    public String getName() {
      return name;
    }

    public String getLabelKey() {
      return labelKey;
    }

    public String getHintTextKey() {
      return hintTextKey;
    }

  }

  public static class QueryFormProperties implements Serializable {

    /** Serialization version. */
    private static final long serialVersionUID = -5444428003553643467L;
    private String            chosenAttributeStringId;
    private Integer           chosenOperationId;
    private Boolean           freeTextCriteriaSelected;
    private String            existingCriteria;
    private String            freeTextCriteria;

    public QueryFormProperties(String chosenAttributeStringId, Integer chosenOperationId, Boolean freeTextCriteriaSelected, String existingCriteria,
        String freeTextCriteria) {
      super();
      this.chosenAttributeStringId = StringUtils.isEmpty(chosenAttributeStringId) ? null : chosenAttributeStringId;
      this.chosenOperationId = chosenOperationId;
      this.freeTextCriteriaSelected = freeTextCriteriaSelected;
      this.existingCriteria = StringUtils.isEmpty(existingCriteria) ? null : existingCriteria;
      this.freeTextCriteria = StringUtils.isEmpty(freeTextCriteria) ? null : freeTextCriteria;

      // list of existing criteria exist but free text criteria is selected
      if (this.freeTextCriteriaSelected != null) {
        if (this.freeTextCriteriaSelected.booleanValue()) {
          this.existingCriteria = null;
        }
        else {
          this.freeTextCriteria = null;
        }
      }

      // free text criteria but no existing criteria e.g. description attribute 
      if (StringUtils.isNotEmpty(this.freeTextCriteria) && (this.existingCriteria == null) && (this.freeTextCriteriaSelected == null)) {
        this.freeTextCriteriaSelected = Boolean.TRUE;
      }
    }

    public String getChosenAttributeStringId() {
      return chosenAttributeStringId;
    }

    public Integer getChosenOperationId() {
      return chosenOperationId;
    }

    public Boolean getFreeTextCriteriaSelected() {
      return freeTextCriteriaSelected;
    }

    public String getExistingCriteria() {
      return existingCriteria;
    }

    public String getFreeTextCriteria() {
      return freeTextCriteria;
    }
  }

  /**
   * Returns a list of search criterion descriptors. These are used to render the search fields.
   * 
   * @return a list of search criteria
   */
  public abstract List<Criterion> getCriteria();

  /**
   * Returns a list of column descriptors for the respective search pageStart' results.
   * 
   * @return a list of result column descriptor for all columns to be displayed.
   */
  public abstract List<ColumnDefinition> getInitialColumnDefinitions();

  /**
   * Get the CSS class for this entity icon. 
   * 
   * @return  The CSS class for this entity icon 
   */
  public abstract String getIconCss();

  /**
   * {@link #getPageSize()}
   */
  private int                     pageSize;

  /**
   * {@link #getPageStart()}
   */
  private int                     pageStart;

  private int                     currentPageNumber;

  private Boolean                 resetAttributeSearch;

  private Integer                 savedQueryId;

  private String                  savedQueryName;

  private String                  fullTextSearchValue;

  private SearchStrategy          activeSearchStrategy = SearchStrategy.SEARCH_ALL;

  private String                  nextPage;

  private String                  previousPage;

  private Boolean                 nextPageToShow;

  /** Will be set to true, if the savedQueryId is set to != null */
  private boolean                 filteredBySavedQuery = false;

  /** A list of all available saved queries of the underlying type */
  private List<SavedQueryDTO>     savedQueries         = new ArrayList<SavedQueryDTO>();

  private String                  queryId;

  private QueryFormProperties     queryFormProperties;

  private DynamicQueryFormData<?> queryForm;

  private TableActionParams       tableActionParameter;

  private HierarchicalEntity<?>   hierarchicalTopElement;

  private GuiTableState           tableState;

  /** Non-hierarchical, but paged result List */
  private PagedListHolder<?>      resultList;

  /** Flag to determine, if tree view is show. When false, show normal result list. */
  private boolean                 treeView             = false;

  private TreeViewHelper          treeViewHelper;

  public TreeViewHelper getTreeViewHelper() {
    return treeViewHelper;
  }

  public void setTreeViewHelper(TreeViewHelper treeViewHelper) {
    this.treeViewHelper = treeViewHelper;
  }

  public void initTreeViewHelper() {
    if (this.treeViewHelper == null) {
      this.treeViewHelper = new TreeViewHelper();
      // initially sort by position if tree is enabled the first time
      this.tableState.getSortDefinition().setProperty("position");
      this.tableState.getSortDefinition().setAscending(true);
    }
    this.treeViewHelper.buildTreeModel(getResultList(), getHierarchicalTopElement(), this);
  }

  /**
   * Returns the maximum pageStart size of the result list. Never return null, as nothing would be
   * displayed then. Return -1 (all) instead.
   */
  public int getPageSize() {
    return (pageSize == 0) ? -1 : pageSize;
  }

  /**
   * {@link #getPageSize()}
   */
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getCurrentPageNumber() {
    return currentPageNumber;
  }

  public void setCurrentPageNumber(int currentPageNumber) {
    this.currentPageNumber = currentPageNumber;
  }

  public String getNextPage() {
    return nextPage;
  }

  public void setNextPage(String nextPage) {
    this.nextPage = nextPage;
  }

  public String getPreviousPage() {
    return previousPage;
  }

  public void setPreviousPage(String previousPage) {
    this.previousPage = previousPage;
  }

  public Boolean isNextPageToShow() {
    return nextPageToShow;
  }

  public void setNextPageToShow(Boolean nextPageToShow) {
    this.nextPageToShow = nextPageToShow;
  }

  public PagedListHolder<?> getResultList() {
    return resultList;
  }

  public void setResultList(PagedListHolder<?> resultList) {
    this.resultList = resultList;
  }

  /**
   * Returns the current pageStart of the result list.
   */
  public int getPageStart() {
    return pageStart;
  }

  /**
   * {@link #getPageStart()}
   */
  public void setPageStart(int page) {
    this.pageStart = page;
  }

  /**
   * @return the current query id that is used for filtering
   */
  public Integer getSavedQueryId() {
    return savedQueryId;
  }

  /**
   * sets the query id that will be used for filtering
   * 
   * @param savedQueryId
   */
  public void setSavedQueryId(Integer savedQueryId) {
    this.savedQueryId = savedQueryId;
    if (savedQueryId != null) {
      setFilteredBySavedQuery(true);
    }
  }

  /**
   * sets the name of the saved query that is used for filtering
   * 
   * @param savedQueryName
   */
  public void setSavedQueryName(String savedQueryName) {
    this.savedQueryName = savedQueryName;
  }

  public String getSavedQueryName() {
    return savedQueryName;
  }

  /**
   * @return list of all available saved queries of the underlying type
   */
  public List<SavedQueryDTO> getSavedQueries() {
    return savedQueries;
  }

  public void setSavedQueries(List<SavedQueryDTO> savedQueries) {
    this.savedQueries = savedQueries;
  }

  public String getQueryId() {
    return queryId;
  }

  public void setQueryId(String queryId) {
    this.queryId = queryId;
  }

  /**
   * @return if the associated result list was filtered by a saved query
   */
  public boolean isFilteredBySavedQuery() {
    return filteredBySavedQuery;
  }

  /**
   * Will be set to true, if the savedQueryId is set to != null
   * 
   * @param filteredBySavedQuery
   */
  public void setFilteredBySavedQuery(boolean filteredBySavedQuery) {
    this.filteredBySavedQuery = filteredBySavedQuery;
  }

  public void setQueryForm(DynamicQueryFormData<?> reportForm) {
    this.queryForm = reportForm;
  }

  public DynamicQueryFormData<?> getQueryForm() {
    return queryForm;
  }

  public GuiTableState getTableState() {
    return tableState;
  }

  public void setTableState(GuiTableState tableState) {
    this.tableState = tableState;
  }

  public Boolean getResetAttributeSearch() {
    return resetAttributeSearch;
  }

  public void setResetAttributeSearch(Boolean resetAttributeSearch) {
    this.resetAttributeSearch = resetAttributeSearch;
  }

  public SearchStrategy getActiveSearchStrategy() {
    return activeSearchStrategy;
  }

  public void setActiveSearchStrategy(SearchStrategy activeSearchStrategy) {
    this.activeSearchStrategy = activeSearchStrategy;
  }

  public QueryFormProperties getQueryFormProperties() {
    return queryFormProperties;
  }

  public void setQueryFormProperties(QueryFormProperties queryFormProperties) {
    this.queryFormProperties = queryFormProperties;
  }

  public TableActionParams getTableActionParameter() {
    return tableActionParameter;
  }

  public void setTableActionParameter(TableActionParams tableActionParameter) {
    this.tableActionParameter = tableActionParameter;
  }

  public String getFullTextSearchValue() {
    return fullTextSearchValue;
  }

  public void setFullTextSearchValue(String fullTextSearchValue) {
    this.fullTextSearchValue = fullTextSearchValue;
  }

  public boolean isTreeView() {
    return treeView;
  }

  public void setTreeView(boolean treeView) {
    this.treeView = treeView;
  }

  public HierarchicalEntity<?> getHierarchicalTopElement() {
    return hierarchicalTopElement;
  }

  public void setHierarchicalTopElement(HierarchicalEntity<?> hierarchicalTopElement) {
    this.hierarchicalTopElement = hierarchicalTopElement;
  }

  public boolean isHierarchical() {
    return getHierarchicalTopElement() != null;
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = prime * result + pageStart;
    result = prime * result + pageSize;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SearchDialogMemory other = (SearchDialogMemory) obj;
    if (pageStart != other.pageStart) {
      return false;
    }
    if (pageSize != other.pageSize) {
      return false;
    }
    return true;
  }
}
