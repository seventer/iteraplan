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
package de.iteratec.iteraplan.businesslogic.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery.TooManyClauses;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.Version;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.reader.ReaderProvider;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.UserContext.Permissions;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.dto.SearchDTO;
import de.iteratec.iteraplan.model.dto.SearchRowDTO;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.persistence.dao.GeneralBuildingBlockDAO;
import de.iteratec.iteraplan.persistence.dao.SearchDAO;
import de.iteratec.iteraplan.presentation.dialog.Search.SearchDialogMemory;


/**
 * Provides methods for search.
 */
public class SearchServiceImpl implements SearchService {

  private static final Logger                       LOGGER                       = Logger.getIteraplanLogger(SearchServiceImpl.class);

  // number of elements that should be displayed before / after the found word
  private static final int                          MAX_NUM_FRAGMENTS_REQUIRED   = 5;

  // ellipsis followed by separator element (e.g. "..." [block element]). configured via css
  private static final String                       ELLIPSIS_WITH_SEPARATOR      = "... <span class=\"foundinseparator\"></span>";

  private SearchDAO                                 searchDAO;
  private GeneralBuildingBlockDAO                   buildingBlockDAO;

  private static final TypeOfFunctionalPermission[] APPLICABLE_PERMISSIONS_LISTS = { TypeOfFunctionalPermission.ARCHITECTURALDOMAIN,
    TypeOfFunctionalPermission.BUSINESSDOMAIN, TypeOfFunctionalPermission.BUSINESSFUNCTION, TypeOfFunctionalPermission.BUSINESSOBJECT,
    TypeOfFunctionalPermission.BUSINESSPROCESS, TypeOfFunctionalPermission.BUSINESSUNIT, TypeOfFunctionalPermission.INFORMATIONSYSTEMDOMAIN,
    TypeOfFunctionalPermission.INFORMATIONSYSTEMINTERFACE, TypeOfFunctionalPermission.INFORMATIONSYSTEMRELEASE,
    TypeOfFunctionalPermission.INFRASTRUCTUREELEMENT, TypeOfFunctionalPermission.PRODUCT, TypeOfFunctionalPermission.PROJECT,
    TypeOfFunctionalPermission.TECHNICALCOMPONENTRELEASES                     };

  private static final String                       NAME_MSG_KEY                 = "global.name";
  private static final String                       VERSION_MSG_KEY              = "global.version";
  private static final String                       DESCRIPTION_MSG_KEY          = "global.description";

  /**
   * Maps indexes of the search result array to applicable message keys
   * used in {@link #setFoundIn(SearchRowDTO, Object[], Highlighter)}
   */
  private static final Map<Integer, String>         LINE_INDEX_TO_MSG_KEY        = new ImmutableMap.Builder<Integer, String>()
      .put(Integer.valueOf(3), NAME_MSG_KEY)
      .put(Integer.valueOf(4), NAME_MSG_KEY)
      .put(Integer.valueOf(5), NAME_MSG_KEY)
      .put(Integer.valueOf(10), NAME_MSG_KEY)
      .put(Integer.valueOf(12), NAME_MSG_KEY)
      .put(Integer.valueOf(6), VERSION_MSG_KEY)
      .put(Integer.valueOf(11), VERSION_MSG_KEY)
      .put(Integer.valueOf(13), VERSION_MSG_KEY)
      .put(Integer.valueOf(7), DESCRIPTION_MSG_KEY)
      .put(Integer.valueOf(8), "search.resultsPeriod_from")
      .put(Integer.valueOf(9), "search.resultsPeriod_to").build();

  /** {@inheritDoc} */
  public List<BuildingBlock> getSearchBuildingBlocks(TypeOfBuildingBlock buildingBlockType, String searchField) {
    List<BuildingBlock> result = Collections.emptyList();

    // setup search criteria
    SearchDialogMemory searchDialogMemory = new SearchDialogMemory();
    searchDialogMemory.setSearchField(searchField);
    searchDialogMemory.setBuildingBlockTypeFilter(buildingBlockType.getValue());

    // global search
    SearchDTO searchDTO = this.getSearchDTO(searchDialogMemory);

    if (searchDTO != null) {
      Map<String, Collection<SearchRowDTO>> searchMap = searchDTO.getSearchMap();
      if (searchMap != null && searchMap.get(buildingBlockType.getValue()) != null) {
        Collection<SearchRowDTO> rowDTOs = searchMap.get(buildingBlockType.getValue());
        Collection<Integer> ids = new HashSet<Integer>();
        for (SearchRowDTO searchRowDTO : rowDTOs) {
          ids.add(searchRowDTO.getId());
        }

        result = buildingBlockDAO.loadBuildingBlocks(ids, buildingBlockType.getAssociatedClass());
      }
    }

    return result;
  }

  /** {@inheritDoc} */
  public SearchDTO getSearchDTO(SearchDialogMemory searchDialogMemory) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.SEARCH);

    String searchField = searchDialogMemory.getSearchField();

    SearchDTO dto = new SearchDTO();

    if (StringUtils.isEmpty(searchField)) {
      return dto;
    }

    // Use an ArrayListMultimap, to ensure the order of the results is preserved.
    Multimap<String, SearchRowDTO> searchMap = ArrayListMultimap.create();

    // Modify the queryString
    String modQueryString = modifyQueryString(searchField);

    // execute the search and put the results into the searchMap
    executeSearchQuery(searchMap, modQueryString, searchDialogMemory.getBuildingBlockTypeFilter());

    dto.setSearchMultiMap(searchMap);

    // if no results were found, and the string is not surrounded by '*' but only one word
    if (searchMap.size() == 0 && isQueryStringModifiable(modQueryString)) {
      Multimap<String, SearchRowDTO> altSearchMap = ArrayListMultimap.create();
      String altQueryString = modifyQueryString(modQueryString);
      executeSearchQuery(altSearchMap, altQueryString, searchDialogMemory.getBuildingBlockTypeFilter());
      dto.setNumberOfAlternativeResults(altSearchMap.size());
      dto.setAlternativeQueryString(altQueryString);
    }

    dto.setSearchMultiMap(searchMap);
    return dto;

  }

  private String modifyQueryString(String queryString) {
    StringBuilder modQueryString = new StringBuilder(queryString);
    // if whitespace included, don't modify
    if (!queryString.contains(" ")) {

      // if the String contains quotes, remove them if they are at the beginning or end
      if (queryString.contains("\"")) {
        if (queryString.endsWith("\"")) {
          modQueryString.deleteCharAt(queryString.length() - 1);
        }
        if (queryString.charAt(0) == '"') {
          modQueryString.deleteCharAt(0);
        }
        // no further modification, return the string
        return modQueryString.toString();
      }

      // if the String contains no "*" surround it with "*"
      if (!queryString.contains("*")) {
        modQueryString.insert(0, '*');
        modQueryString.append('*');
      }
    }
    return modQueryString.toString();
  }

  /**
   * Checks if the queryString is in a form that could be surrounded by '*'
   * 
   * @param queryString
   * @return true, if the queryString contains no whitespaces and doesn't start or end with '*'
   */
  private boolean isQueryStringModifiable(String queryString) {
    return (!queryString.contains(" ") && (queryString.charAt(0) != '*' || !queryString.endsWith("*")));
  }

  /**
   * Execute the hibernate search
   * 
   * @param searchMap
   *          Multimap of SearhRowDTOs contains the results
   * @param queryString
   *          the query string which the user entered
   */
  private void executeSearchQuery(Multimap<String, SearchRowDTO> searchMap, String queryString, String buildingBlockTypeFilter) {
    // reader provider is required to close the reader after search has finished
    ReaderProvider readerProvider = searchDAO.getReaderProvider();
    IndexReader reader = searchDAO.openReader(readerProvider, getClassArray());

    // if the reader is null (i.e. the user has no functional permissions to search any one of the
    // building blocks) return without executing a search
    if (reader == null) {
      return;
    }

    try {
      // index fields that will be searched
      String[] productFields = { "attributeValueAssignments.attributeValue.valueString", "name", "version", "description", "informationSystem.name",
          "technicalComponent.name", "runtimePeriod.start", "runtimePeriod.end", "informationSystemReleaseA.informationSystem.name",
          "informationSystemReleaseA.version", "informationSystemReleaseB.informationSystem.name", "informationSystemReleaseB.version" };

      QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_31, productFields, new StandardAnalyzer(Version.LUCENE_31));

      // allow wildcard * at the beginning of a query string
      parser.setAllowLeadingWildcard(true);
      // automatically put quotes around the search term for phrase search, because that's what most people expect
      parser.setAutoGeneratePhraseQueries(true);
      // workaround for known issue with highlighter and wildcard queries
      parser.setMultiTermRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_QUERY_REWRITE);

      Query luceneQuery = null;
      try {
        // parse the query string
        luceneQuery = parser.parse(queryString);

        // rewrite luceneQuery for highlighting
        luceneQuery = luceneQuery.rewrite(reader);

      } catch (TooManyClauses tmcEx) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.LUCENE_QUERY_TOO_COMPLEX, tmcEx);
      } catch (ParseException e) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.LUCENE_QUERY_PARSE_FAILED, e);
      } catch (IOException e) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.LUCENE_QUERY_REWRITE_FAILED, e);
      }

      // the found content is being highlighted
      SimpleHTMLFormatter sHtmlF = new SimpleHTMLFormatter("<span class=\"highlighted\">", "</span>");
      Highlighter highlighter = new Highlighter(sHtmlF, new QueryScorer(luceneQuery));
      highlighter.setTextFragmenter(new SimpleFragmenter(40));

      // sorted by Building block types
      SortField sortField = new SortField("buildingBlockType.name", SortField.STRING);
      Sort sort = new Sort(sortField);

      String[] projections = { "id", "buildingBlockType.name", FullTextQuery.DOCUMENT, "name", "informationSystem.name", "technicalComponent.name",
          "version", "description", "runtimePeriod.start", "runtimePeriod.end", "informationSystemReleaseA.informationSystem.name",
          "informationSystemReleaseA.version", "informationSystemReleaseB.informationSystem.name", "informationSystemReleaseB.version" };

      List<Object[]> results = searchDAO.search(luceneQuery, sort, getClassArray(), projections);

      addResultsToSearchMap(searchMap, results, buildingBlockTypeFilter, highlighter);
    } finally {
      readerProvider.closeReader(reader);
    }

  }

  /**
   * Adds the results to the searchMap.
   * 
   * @param searchMap
   * @param results
   */
  private void addResultsToSearchMap(Multimap<String, SearchRowDTO> searchMap, List<Object[]> results, String buildingBlockTypeFilter,
                                     Highlighter highlighter) {

    for (Object[] line : results) {
      // create a new SearchRowDTO
      SearchRowDTO container = new SearchRowDTO();

      // retrieve the attributeStringForSearchIndexing from the index document
      // must be identical with the method name in @see{BuildingBlock}
      Document doc = (Document) line[2];

      // skip this item and don't add it to the result if the indexed datasource is not equal
      // to the current datasource. If the indexed datasource is null, the item will be added.
      if (doc.getField("activeDataSource") != null && !UserContext.getActiveDatasource().equals(doc.getField("activeDataSource").stringValue())) {
        continue;
      }
      // save the id in the SearchRowDTO
      if (line[0] == null) {
        LOGGER.warn("Found Entity without id in the searchindex. Skipping this entity. Index might be corrupt.");
        continue;
      }
      else {
        container.setId((Integer) line[0]);
      }
      // Save the BuildingblockType in the SearchRowDTO
      if (!saveBBTypeToDTO(buildingBlockTypeFilter, (String) line[1], container)) {
        continue;
      }

      line[2] = getAttributeStringFieldValue(doc);

      saveNameToDTO((String) line[3], (String) line[4], (String) line[5], container);

      // if filterVirtualElements is set to true and the current element is a virtual element, don't
      // add it to the search results
      if (IteraplanProperties.getBooleanProperty(Constants.HIBERNATE_SEARCH_FILTER_VIRTUAL_ELEMENTS)
          && AbstractHierarchicalEntity.TOP_LEVEL_NAME.equals(container.getName())) {
        continue;
      }

      saveVersionToDTO(container, (String) line[6]);

      // save description in the SearchRowDTO
      container.setDescription((String) line[7]);

      saveNameOfISIsToDTO(line, container);

      setFoundIn(container, line, highlighter);

      // add SearchRowDTO to map
      searchMap.put(container.getBuildingBlockType(), container);
    }

  }

  /**
   * Saves "Name" of the InformationSystemInterface (name of connected ISRs) (if result is an ISI)
   * @param line
   *          search result
   * @param container
   *          DTO to save the name in
   */
  private void saveNameOfISIsToDTO(Object[] line, SearchRowDTO container) {
    // check if line represents an InformationSystemInterface (line[10] = IS A, line[12] = IS B)
    if (!StringUtils.isEmpty((String) line[10]) && !StringUtils.isEmpty((String) line[12])) {
      StringBuffer buf = new StringBuffer();
      // IS A
      buf.append(line[10]);
      // Release of IS A
      if (!StringUtils.isEmpty((String) line[11])) {
        buf.append("# " + line[11]);
      }
      // IS B
      buf.append(" <-> " + line[12]);
      // Release of IS B
      if (!StringUtils.isEmpty((String) line[13])) {
        buf.append("# " + line[13]);
      }
      container.setName(buf.toString());
    }
  }

  /**
   * Saves version in the SearchRowDTO
   * @param container
   *          DTO to save the version in
   * @param version
   *          String representing the version
   */
  private void saveVersionToDTO(SearchRowDTO container, String version) {
    if (version == null) {
      container.setVersion(null);
    }
    else if (!StringUtils.isEmpty(version)) {
      container.setVersion("# " + version);
    }
  }

  /**
   * Saves name in SearchRowDTO (either Name, InformationSystem.Name or TechnicalComponent.Name will be used)
   * @param name
   *          Name
   * @param isName
   *          InformationSystem.Name
   * @param tcName
   *          TechnicalComponent.Name
   * @param container
   *          DTO to save the name in
   */
  private void saveNameToDTO(String name, String isName, String tcName, SearchRowDTO container) {
    if (!StringUtils.isEmpty(name)) {
      // Name
      container.setName(name);
    }
    else if (!StringUtils.isEmpty(isName)) {
      // Name of the information system
      container.setName(isName);
    }
    else if (!StringUtils.isEmpty(tcName)) {
      // Name of the TechnicalComponent
      container.setName(tcName);
    }
  }

  private String getAttributeStringFieldValue(Document doc) {
    Field attributeStringField = doc.getField("attributeStringForSearchIndexing");
    if (attributeStringField != null) {
      return attributeStringField.stringValue();
    }
    else {
      return "";
    }
  }

  /**
   * Saves the BuildingblockType in the SearchRowDTO
   * @param buildingBlockTypeFilter
   *          Filter-BBType to allow only this BuildingBlockType to be saved. If empty, all BBTypes are allowed.
   * @param bbtString
   *          String representing the BuildingBlockType
   * @param container
   *          DTO to save the BuildingBlockType in
   * @return true if successful, false otherwise
   */
  private boolean saveBBTypeToDTO(String buildingBlockTypeFilter, String bbtString, SearchRowDTO container) {
    if (StringUtils.isEmpty(bbtString)) {
      LOGGER.warn("Found Entity without buildingBlockType in the searchindex. Skipping this entity. Index might be corrupt.");
    }
    else if (!StringUtils.isEmpty(buildingBlockTypeFilter) && !bbtString.equals(buildingBlockTypeFilter)) {
      LOGGER.info(String.format("Skipping this buildingBlockType due to filtering only '%s' building blocks", buildingBlockTypeFilter));
    }
    else {
      container.setBuildingBlockType(bbtString);
      return true;
    }
    return false;
  }

  /**
   * Adds the information where the search string was found to the SearchRowDTO.
   * 
   * @param container
   * @param line
   * @param highlighter
   *          A match highlighter that places markers at the matched words in the object
   */
  private void setFoundIn(SearchRowDTO container, Object[] line, Highlighter highlighter) {
    // retrieve the locale of the user
    Locale locale = UserContext.getCurrentLocale();
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);

    List<String> foundIn = new ArrayList<String>();
    for (int m = 2; m < line.length; m++) {

      if (line[m] != null) {
        String text = line[m].toString();

        if (!StringUtils.isEmpty(text)) {
          if (m != 2) {
            String foundInHighlighted = getFoundInHighlightedString(highlighter, analyzer, text);
            if (foundInHighlighted != null && foundInHighlighted.compareTo("") != 0) {
              foundIn.add(MessageAccess.getStringOrNull(LINE_INDEX_TO_MSG_KEY.get(Integer.valueOf(m)), locale) + ": " + foundInHighlighted);
            }
          } // If found in attributeString
          else {
            String[] splittedText = text.split(Pattern.quote("^^^"));
            for (int n = 0; n < splittedText.length; n++) {
              String foundInHighlighted = getFoundInHighlightedString(highlighter, analyzer, splittedText[n]);
              if (foundInHighlighted != null && foundInHighlighted.compareTo("") != 0) {
                foundIn.add(foundInHighlighted);
              }
            }
          }
        }
        container.setFoundIn(foundIn);
      }
    }
  }

  private String getFoundInHighlightedString(Highlighter highlighter, Analyzer analyzer, String text) {
    String foundInHighlighted = null;
    // create a token stream from the text
    TokenStream tokenStream = analyzer.tokenStream("", new StringReader(text));
    try {
      // mark the found queryString in the token stream
      foundInHighlighted = highlighter.getBestFragments(tokenStream, text, MAX_NUM_FRAGMENTS_REQUIRED, ELLIPSIS_WITH_SEPARATOR);
    } catch (IOException e) {
      throw new IteraplanTechnicalException(e);
    } catch (InvalidTokenOffsetsException e) {
      throw new IteraplanTechnicalException(e);
    }
    return foundInHighlighted;
  }

  /**
   * Retrieves the folders to search in, depending on the users rights
   */
  private Class<?>[] getClassArray() {
    Permissions userPermChecker = UserContext.getCurrentPerms();
    // list with entity classes to search in
    Set<Class<?>> classlist = new HashSet<Class<?>>();

    // iterate over all relevant functional permissions and collect the respective
    // building block class, if the user has the acces permission
    for (TypeOfFunctionalPermission funcPerm : APPLICABLE_PERMISSIONS_LISTS) {
      if (userPermChecker.userHasFunctionalPermission(funcPerm)) {
        classlist.add(TypeOfFunctionalPermission.PERMISSION_TO_CLASS_MAP.get(funcPerm));
      }
    }

    // contents of the class list is saved in an array, as the method createFullTextQuery only
    // accepts class arrays.
    return classlist.toArray(new Class<?>[classlist.size()]);
  }

  /**
   * {@inheritDoc}
   */
  public void createIndex(PurgeMode purge) {
    LOGGER.info("Creating New Index");

    HashSet<Class<?>> classList = new HashSet<Class<?>>();

    // iterate over all relevant functional permissions and collect the corresponding
    // building block class
    for (TypeOfFunctionalPermission funcPerm : APPLICABLE_PERMISSIONS_LISTS) {
      classList.add(TypeOfFunctionalPermission.PERMISSION_TO_CLASS_MAP.get(funcPerm));
    }
    // add classes which are not covered by functional permissions
    classList.add(InformationSystem.class);
    classList.add(TechnicalComponent.class);
    classList.add(Tcr2IeAssociation.class);
    classList.add(Isr2BoAssociation.class);

    if (PurgeMode.PURGE.equals(purge)) {
      searchDAO.purgeIndexes(classList);
    }

    searchDAO.createIndexes(classList);
    LOGGER.info("Finished Indexing");
  }

  public void setSearchDAO(SearchDAO searchDAO) {
    this.searchDAO = searchDAO;
  }

  public SearchDAO getSearchDAO() {
    return searchDAO;
  }

  public void setBuildingBlockDAO(GeneralBuildingBlockDAO buildingBlockDAO) {
    this.buildingBlockDAO = buildingBlockDAO;
  }
}
