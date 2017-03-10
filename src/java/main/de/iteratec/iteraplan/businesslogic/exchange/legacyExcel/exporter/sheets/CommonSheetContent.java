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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelAdditionalQueryData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelHelper;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;


/**
 * This class provides functionality for creation of headers and respective content which are common
 * to all subclasses. It handles their order as well as the id-column and the attached attributes.
 * Subclasses themselves are responsible for all specific contents.<br/>
 * <br/>
 * In case a subclass requires special treatment of its columns, the appropriate methods can be
 * overridden to change the content of the common and special columns. The order of those columns in
 * reference to the id- and the attribute-columns will stay unchanged.<br/>
 * <br/>
 * In opposition to {@link ExcelSheet} it has knowledge about the semantics of the written contents.
 */
public abstract class CommonSheetContent<B extends BuildingBlock> extends ExcelSheet<B> {
  private Map<Object, String> currentHyperlinks;

  private String              serverURL;

  /**
   * Initializes a new excel sheet.
   * 
   * @param context
   *          the context to be used for setting of the contents
   * @param activatedAttributeTypes
   *          the attribute types to be shown in the sheet
   * @param queryData
   *          additional query data; is ignored if <code>null</code>
   * @param bbt
   *          the {@link BuildingBlockType} that requires the current excel sheet
   * @param serverURL
   *          the server url to be used for creation of links within the current sheet
   */
  @SuppressWarnings("hiding")
  protected void init(Set<B> contents, ExportWorkbook context, List<AttributeType> activatedAttributeTypes, ExcelAdditionalQueryData queryData,
                      BuildingBlockType bbt, String serverURL) {
    super.init(contents, context, activatedAttributeTypes, queryData, bbt);
    this.serverURL = serverURL;
  }

  /**
   * This method returns all headers in the right order. They consist of the id-header, all common
   * headers, all specific headers and headers for attributes. For specific headers
   * {@link #addSpecificHeaders(List)} is called. If other common headers are required,
   * {@link #addCommonHeaders(List)} can be overridden.<br/>
   * <br/>
   * During the collection of all headers the special widths for this headers are also collected.
   * 
   * @return list with all headers in the right order
   */
  @Override
  protected final List<ExcelSheet.Header> getHeaders() {
    List<ExcelSheet.Header> headers = Lists.newLinkedList();
    headers.add(getIdHeader());

    this.addCommonHeaders(headers);

    this.addSpecificHeaders(headers);

    this.addAttributeSeparationKey(headers);
    this.addActivatedAttributeTypesAsStrings(headers);

    return headers;
  }

  /**
   * Adds all common headers to the given <code>headers</code>. <code>headerWidths</code> is
   * supposed to be a container for all special widths required for the appropriate columns. It is
   * expected that <code>headerWidths</code> contains the header names as keys and the corresponding
   * widths as values.
   * 
   * @param headers
   *          list of header for collection of common headers
   */
  protected void addCommonHeaders(List<Header> headers) {
    String sheetKey = getString(this.getSheetKey());
    headers.add(new Header(sheetKey, ExportWorkbook.getColumnWidthForSheetKey()));

    StringBuilder sheetKeyExt = new StringBuilder();
    sheetKeyExt.append(getString(this.getSheetKey()));
    sheetKeyExt.append(' ');
    sheetKeyExt.append(getString(Constants.ATTRIBUTE_HIERARCHICAL));
    headers.add(new Header(sheetKeyExt.toString(), ExportWorkbook.getColumnWidthForSheetKey()));

    headers.add(getHeader(Constants.ATTRIBUTE_DESCRIPTION, ExportWorkbook.getColumnWidthWide()));

    headers.add(getHeader(Constants.ATTRIBUTE_LAST_USER));
    headers.add(getHeader(Constants.ATTRIBUTE_LAST_MODIFICATION_DATE));
    headers.add(getHeader(Constants.SUBSCRIBED_USERS));
  }

  /**
   * This method returns all contents of the given <code>bb</code> in the right order. For all
   * common contents {@link #addCommonLineContents(BuildingBlock, List)} is called. For specific
   * headers {@link #addSpecificHeaders(List)} is called. Finally all attributes are added.<br/>
   * <br/>
   * During the collection of all contents hyperlinks are gathered to be fetched afterwards
   * {@link #getHyperlinksForContent()}.
   * 
   * @param bb
   *          the {@link BuildingBlock} for contents
   * @return list with all headers in the right order
   */
  @Override
  protected List<Object> getContentLine(B bb) {
    List<Object> contents = new ArrayList<Object>();
    this.currentHyperlinks = new HashMap<Object, String>();

    Integer id = this.getIdForBB(bb);
    contents.add(id);
    this.currentHyperlinks.put(id, this.getUrlForEntity(this.getBBForIdHyperlink(bb)));

    this.addCommonLineContents(bb, contents);

    this.addSpecificLineContents(bb, contents);
    contents.add("");
    contents.add(bb.getAttributeTypeToAttributeValues());

    return contents;

  }

  /**
   * Adds contents which are common to all types of {@link BuildingBlock} excluding the id-column
   * which is done in {@link #getContentLine(BuildingBlock)}.<br/>
   * <br/>
   * This method can be overriden, if other contents are required.
   * 
   * @param bb
   *          the {@link BuildingBlock} with context to extract
   * @param contents
   *          the list of all already collected contents
   */
  protected void addCommonLineContents(B bb, List<Object> contents) {
    contents.add(bb.getNonHierarchicalName());
    contents.add(bb.getHierarchicalName());

    // Unnecessary formatString call? We already do this later in the ExcelWorkbook
    String description = this.getDescription(bb);
    contents.add(description);

    contents.add(bb.getLastModificationUser());
    contents.add(bb.getLastModificationTime());
    contents.add(ExcelHelper.concatMultipleUsers(bb.getSubscribedUsers(), IN_LINE_SEPARATOR));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Map<Object, String> getHyperlinksForContent() {
    if (this.currentHyperlinks == null) {
      return new HashMap<Object, String>();
    }
    return this.currentHyperlinks;
  }

  /**
   * Adds names of all activated {@link AttributeType}s to the given <code>headers</code> as strings
   * using {@link AttributeType#getNameWithGroupForExport()}.
   */
  protected void addActivatedAttributeTypesAsStrings(List<Header> headers) {
    for (AttributeType attributeType : this.getActivatedAttributeTypes()) {
      Header header = new Header(attributeType.getNameWithGroupForExport(), attributeType.getDescription());
      headers.add(header);
    }
  }

  /**
   * Adds attribute separation character, at the moment #
   */
  protected void addAttributeSeparationKey(List<Header> headers) {
    headers.add(new Header("#"));
  }

  /**
   * Adds headers which are specific to a certain type of {@link BuildingBlock}.
   * 
   * @param headers
   *          the list of all already collected headers
   */
  protected abstract void addSpecificHeaders(List<Header> headers);

  /**
   * Adds contents which are specific to a certain type of {@link BuildingBlock}.
   * 
   * @param bb
   *          the {@link BuildingBlock} with context to extract
   * @param lineContents
   *          the list of all already collected contents
   */
  protected abstract void addSpecificLineContents(B bb, List<Object> lineContents);

  /**
   * Returns the description of the given <code>bb</code>. Due to the fact that not all subclasses
   * of {@link BuildingBlock} implement a direct method for retrieving of a description, this work
   * around had to be created.
   * 
   * @param bb
   *          the current {@link BuildingBlock}
   * @return the description for <code>bb</code>
   */
  protected String getDescription(B bb) {
    return bb == null ? "" : bb.getDescription();
  }

  /**
   * Returns the id of the given <code>bb</code>. In some cases, such as with
   * {@link de.iteratec.iteraplan.model.BusinessMapping BusinessMapping}, it is required
   * to use not the id of the {@link BuildingBlock} directly, but of the connected entities.
   * For such {@link BuildingBlock}s this method can be overridden.
   * 
   * @param bb
   *          the current {@link BuildingBlock}
   * @return the id
   */
  protected Integer getIdForBB(B bb) {
    return bb.getId();
  }

  /**
   * Returns the header for the id-column. In some cases, such as with
   * {@link de.iteratec.iteraplan.model.BusinessMapping BusinessMapping}, it
   * is required to point out, that the ids in the corresponding column does not belong to the
   * {@link BuildingBlock} itself but to connection entities. For such cases this method can be
   * overridden.
   * 
   * @return the header for the id-column.
   */
  protected Header getIdHeader() {
    return new Header(getString(Constants.ATTRIBUTE_ID), ExportWorkbook.getColumnWidthForId());
  }

  /**
   * Returns the {@link BuildingBlock} to be used for the hyperlink in the id-column. In some cases,
   * such as with {@link de.iteratec.iteraplan.model.BusinessMapping BusinessMapping}, it is required
   * to use not the {@link BuildingBlock} itself but connected entities for the id-column.
   * In such cases this method can be overridden.
   *  
   * @param bb
   *          the current {@link BuildingBlock}
   * @return the {@link BuildingBlock} to be used for the id-column.
   */
  protected BuildingBlock getBBForIdHyperlink(B bb) {
    return bb;
  }

  /**
   * Gets URL for the given <code>entity</code>.
   * 
   * @param entity
   *          the entity in question
   * @return a url string showing to the <code>entity</code>'s edit page or <code>null</code> if
   *         sever url is missing
   */
  private String getUrlForEntity(IdentityEntity entity) {

    if (this.serverURL == null) {
      return null;
    }
    return URLBuilder.getEntityURL(entity, this.serverURL);
  }
}
