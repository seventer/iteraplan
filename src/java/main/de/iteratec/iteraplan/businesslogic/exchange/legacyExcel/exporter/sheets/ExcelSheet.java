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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelAdditionalQueryData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelHelper;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.sorting.AttributeTypeWithATGComparator;
import de.iteratec.iteraplan.model.sorting.IdentityStringComparator;


/**
 * This class provides all general methods required for creation of excel sheets for different types
 * of {@link BuildingBlock}s. It is basically responsible for the general format of the created
 * sheet,i.e it initializes a new sheet and fills it with contents provided by a subclass, including
 * the preface, header and the actual content lines. <br/>
 * A subclass is therefore responsible for the contents. Because this upper class does not have any
 * knowledge about the semantics of the lines, the subclass should ensure that the order of headers
 * match the order of the actual contents. Otherwise a mismatch will occur in the result sheet.<br/>
 * This class should be extended by all classes representing excel sheets.
 */
public abstract class ExcelSheet<B extends BuildingBlock> {

  public static final class Header {
    Header(String label, String description) {
      this(label);
      this.description = description;
    }

    Header(String label) {
      this.label = label;
    }

    Header(String label, int width) {
      this(label);
      this.width = Integer.valueOf(width);
    }

    private String  label;
    private String  description;
    private Integer width;

    public String getLabel() {
      return this.label;
    }

    public Integer getWidth() {
      return this.width;
    }

    public String getDescription() {
      return this.description;
    }
  }

  protected static final Logger    LOGGER              = Logger.getIteraplanLogger(ExcelSheet.class);
  /**
   * Separator used for separating values in preface
   */
  protected static final String    PREFACE_SEPARATOR   = ", ";

  /**
   * Separator used for separating names of different releases or similar entities when they are
   * written in one cell.
   */
  public static final String       IN_LINE_SEPARATOR   = ";\n";

  /**
   * Separator for dates
   */
  protected static final String    DATE_SEPARATOR      = " - ";

  /**
   * Constants used for grouping of certain elements while concatenating them to one value
   */
  public static final String       UNIT_OPENER         = "(";
  public static final String       UNIT_CLOSER         = ")";
  public static final String       UNIT_SEPARATOR      = " / ";
  /**
   * Separator used for separating names of Information System Releases connected by one Information
   * System Interface
   */
  protected static final String    INTERFACE_SEPARATOR = " <=> ";

  private List<B>                  contents;

  private int                      sheetId;

  private ExportWorkbook           context;

  private List<AttributeType>      activatedAttributeTypes;

  private BuildingBlockType        bbt;

  private ExcelAdditionalQueryData queryData;

  /**
   * This method provided a general schema for filling of an excel sheet with contents. The preface
   * is added by {@link ExportWorkbook#addPreface(int, String, ExcelAdditionalQueryData)}, the
   * headers by calling {@link ExportWorkbook#addHeaders(int, List)} and for each single
   * {@link BuildingBlock} provided for this sheet
   * {@link ExportWorkbook#addContentLine(int, List, Map, List)} is called.<br/>
   * <br/>
   * The actual knowledge about the composition of headers and corresponding lines is responsibility
   * of implementing classes.
   */
  public void createSheet() {
    LOGGER.debug("Creating sheet for " + this.bbt.getTypeOfBuildingBlock().toString() + "-report");
    // this.addPreface(this.queryData);
    this.getContext().addPreface(this.sheetId, getTitleKey(), this.queryData);
    LOGGER.debug("Added preface");

    if ((bbt != null) && !bbt.getSubscribedUsers().isEmpty()) {
      this.getContext().addSubscribersForBBT(this.sheetId, Constants.SUBSCRIBED_USERS,
          ExcelHelper.concatMultipleUsers(bbt.getSubscribedUsers(), IN_LINE_SEPARATOR));
    }

    this.getContext().addHeaders(this.sheetId, getHeaders());
    LOGGER.debug("Added headers lines");

    int counter = 0;
    for (B bb : this.contents) {
      if (ExcelHelper.isVirtualElement(bb)) {
        continue;
      }
      List<Object> contentLine = this.getContentLine(bb);
      Map<Object, String> linksForContents = this.getHyperlinksForContent();

      this.getContext().addContentLine(sheetId, contentLine, linksForContents, getActivatedAttributeTypes());
      counter++;
    }
    LOGGER.debug("Added " + counter + " lines");
  }

  /**
   * Initializes a new excel sheet.
   * 
   * @param context
   *          the context to be used for setting the contents
   * @param activatedAttributeTypes
   *          the attribute types to be shown in the sheet
   * @param queryData
   *          additional query data; is ignored if <code>null</code>
   * @param bbt
   *          the type of {@link BuildingBlock} that requires the current excel sheet
   */
  @SuppressWarnings("hiding")
  protected void init(Set<B> contents, ExportWorkbook context, List<AttributeType> activatedAttributeTypes, ExcelAdditionalQueryData queryData,
                      BuildingBlockType bbt) {
    this.contents = ExcelHelper.sortEntities(contents, new IdentityStringComparator());
    Collections.sort(activatedAttributeTypes, new AttributeTypeWithATGComparator());
    this.activatedAttributeTypes = activatedAttributeTypes;
    this.context = context;
    this.bbt = bbt;
    this.queryData = queryData;

    // Initialize Sheet
    this.sheetId = context.createSheet(getString(this.getSheetKey()));
  }

  /**
   * @param messageKey A message key string. I will be looked up through {@link MessageAccess}.
   * @return the localized string corresponding to the passed message key
   */
  protected final String getString(final String messageKey) {
    return MessageAccess.getStringOrNull(messageKey, this.context.getLocale());
  }

  protected final Header getHeader(final String messageKey) {
    return new Header(getString(messageKey));
  }

  protected final Header getHeader(final String messageKey, int width) {
    return new Header(getString(messageKey), width);
  }

  /**
   * Provides access to the current {@link ExportWorkbook} to subclasses.
   * 
   * @return the current Excel Workbook
   */
  protected ExportWorkbook getContext() {
    return this.context;
  }

  /**
   * Provides access to the activated attribute types to subclasses.
   * 
   * @return the activated types
   */
  protected List<AttributeType> getActivatedAttributeTypes() {
    return this.activatedAttributeTypes;
  }

  /**
   * @return the sheet key of the specific excel sheet type
   */
  protected abstract String getSheetKey();

  /**
   * @return the title key of the specific excel sheet type
   */
  protected abstract String getTitleKey();

  /**
   * This method should return all headers in the right order.<br/>
   * <br/>
   * During the collection of all headers the special widths for this headers should also be
   * collected.
   * 
   * @return a list with all headers; should <b>not</b> be <code>null</code>.
   */
  protected abstract List<Header> getHeaders();

  /**
   * An implementation of this method should return the contents of the given <code>bb</code> as a
   * {@link List}. They will be added in the order provided in the returned list. This order should
   * match the order returned by {@link #getHeaders()}.
   * 
   * @param bb
   *          the {@link BuildingBlock} for the current line
   * @return a list with all information concerning the given <code>bb</code>; should <b>not</b> be
   *         <code>null</code>.
   */
  protected abstract List<Object> getContentLine(B bb);

  /**
   * The implementation of this method should provide a map with all hyperlinks required for linking
   * of contents collected in {@link #getContentLine(BuildingBlock)}. For each new line a new map
   * will be returned. <br/>
   * <br/>
   * It is expected that the result map contains the content elements as keys and the corresponding
   * cell styles as values.
   * 
   * @return a map with specific cell styles; should <b>not</b> be <code>null</code>.
   */
  protected abstract Map<Object, String> getHyperlinksForContent();

}
