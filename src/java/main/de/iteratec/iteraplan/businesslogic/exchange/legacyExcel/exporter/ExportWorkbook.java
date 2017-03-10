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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.ExcelWorkbook;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.ExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.TextAT;


/**
 * Represents an excel workbook and contains all surrounding information of it (like styles, locale,
 * the workbook itself)
 */
/**
 * @author vsh
 *
 */
public class ExportWorkbook extends ExcelWorkbook implements ExportableWorkbook {

  private static final Logger   LOGGER                     = Logger.getIteraplanLogger(ExportWorkbook.class);

  private CellStyle             titleStyle;
  private CellStyle             headerStyle;
  private CellStyle             headerTableStyle;
  private CellStyle             dateStyle;
  private CellStyle             wrappedStyle;
  private CellStyle             defaultStyle;
  private CellStyle             hyperlinkStyle;
  private Map<Sheet, Integer>   sheetCurrentPosition       = new HashMap<Sheet, Integer>();
  private List<Sheet>           sheetOrder                 = new ArrayList<Sheet>();

  // different column widths
  private static final int      MAXIMUM_CELL_LENGTH        = 22000;

  // the corresponding constants for the column width
  protected static final int    COLUMN_WIDTH_NARROW        = initNarrowWidth();
  protected static final int    COLUMN_WIDTH_MIDDLE        = initMiddleWidth();
  protected static final int    COLUMN_WIDTH_WIDE          = initWideWidth();

  // the following constants can be used as corresponding values in
  // subclasses or can be 'overwritten' by returning the special value in the
  // corresponding abstract methods.
  protected static final int    DEFAULT_COLUMN_WIDTH       = COLUMN_WIDTH_NARROW;
  protected static final int    COLUMN_WIDTH_FOR_SHEET_KEY = COLUMN_WIDTH_WIDE;
  // for some reasons the already defined column widths (e.g. COLUMN_WIDTH_NARROW) does
  // not work for the id-column, i.e. the id-column is almost not visible,
  // therefore it gets its own width.
  protected static final int    COLUMN_WIDTH_FOR_ID        = initIdColumnWidth();

  protected static final int    SHEET_NUMERATOR            = 3;
  protected static final int    SHEET_DENOMINATOR          = 4;

  private Pattern               urlPattern;
  /**
   * Separator used for separating values in preface
   */
  protected static final String PREFACE_SEPARATOR          = ", ";

  /**
   * Separator used for separating names of different releases or similar entities when they are
   * written in one cell.
   */
  protected static final String IN_LINE_SEPARATOR          = ";\n";

  /**
   * Separator for dates
   */
  protected static final String DATE_SEPARATOR             = " - ";

  /**
   * Constants used for grouping of certain elements while concatenating them to one value
   */
  protected static final String UNIT_OPENER                = "(";
  protected static final String UNIT_CLOSER                = ")";
  protected static final String UNIT_SEPARATOR             = " / ";
  /**
   * Separator used for separating names of Information System Releases connected by one Information
   * System Interface
   */
  protected static final String INTERFACE_SEPARATOR        = " <=> ";

  /**
   * Creates the instance of the export workbook with the specified {@code locale} and
   * using the default template for the specified {@code templateType}.
   * 
   * @param locale the locale for generating excel
   * @param templateType the TemplateType
   */
  public ExportWorkbook(Locale locale, TemplateType templateType) {
    this(locale, templateType, "");
  }
    /**
     * Creates the instance of the export workbook with the specified {@code locale} and
     * using the specified {@code templateType} and {@code templateFileName}.
     * 
     * @param locale the locale for generating excel
     * @param templateType the TemplateType
     * @param templateFileName the name of the excel template file that should be used
     */
    public ExportWorkbook(Locale locale, TemplateType templateType, String templateFileName) {
    super();
    this.setLocale(locale);

    String regex = "\\[(http://\\S*|https://\\S*|ftp://\\S*|file:///\\S*)\\s?([^]]*)?\\]";
    this.urlPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE); 

    loadWorkbookFromTemplateFileName(templateType, templateFileName);
    initStyles();
  }

  private static int initNarrowWidth() {
    try {
      return IteraplanProperties.getIntProperty(IteraplanProperties.EXCEL_EXPORT_COLUMN_WIDTH_NARROW);
    } catch (IteraplanTechnicalException e) {
      LOGGER.info("No property for narrow width is defined - taking the fallback-value", e);
    }

    return 22;
  }

  private static int initMiddleWidth() {
    try {
      return IteraplanProperties.getIntProperty(IteraplanProperties.EXCEL_EXPORT_COLUMN_WIDTH_MIDDLE);
    } catch (IteraplanTechnicalException e) {
      LOGGER.info("No property for middle width is defined - taking the fallback-value", e);
    }

    return 3500;
  }

  private static int initWideWidth() {
    try {
      return IteraplanProperties.getIntProperty(IteraplanProperties.EXCEL_EXPORT_COLUMN_WIDTH_WIDE);
    } catch (IteraplanTechnicalException e) {
      LOGGER.info("No property for wide width is defined - taking the fallback-value", e);
    }

    return 12000;
  }

  private static int initIdColumnWidth() {
    try {
      return IteraplanProperties.getIntProperty(IteraplanProperties.EXCEL_EXPORT_ID_COLUMN_WIDTH);
    } catch (IteraplanTechnicalException e) {
      LOGGER.info("No property for id column width is defined - taking the fallback-value", e);
    }

    return 1500;
  }

  /**
   * initializes all styles used in the workbook
   */
  private void initStyles() {

    titleStyle = getWb().createCellStyle();
    headerStyle = getWb().createCellStyle();
    headerTableStyle = getWb().createCellStyle();
    dateStyle = getWb().createCellStyle();
    wrappedStyle = getWb().createCellStyle();
    defaultStyle = getWb().createCellStyle();
    hyperlinkStyle = getWb().createCellStyle();
    
    DataFormat df = getWb().createDataFormat();

    Font font = getWb().createFont();
    font.setFontHeightInPoints((short) 12);
    font.setFontName(ExcelWorkbook.EXCEL_DEFAULT_FONT);
    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
    titleStyle.setFont(font);

    font = getWb().createFont();
    font.setFontHeightInPoints((short) 11);
    font.setFontName(ExcelWorkbook.EXCEL_DEFAULT_FONT);
    headerStyle.setFont(font);

    font = getWb().createFont();
    font.setFontHeightInPoints((short) 11);
    font.setFontName(ExcelWorkbook.EXCEL_DEFAULT_FONT);
    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
    headerTableStyle.setFont(font);

    dateStyle.setDataFormat(df.getFormat("m/d/yy"));
    setAlignementTopLeft(dateStyle);

    wrappedStyle.setWrapText(true);
    setAlignementTopLeft(wrappedStyle);

    setAlignementTopLeft(defaultStyle);

    Font hlinkFont = getWb().createFont();
    hlinkFont.setUnderline(Font.U_SINGLE);
    hlinkFont.setColor(IndexedColors.BLUE.getIndex());
    setAlignementTopLeft(hyperlinkStyle);
    hyperlinkStyle.setFont(hlinkFont);
  }

  private void setAlignementTopLeft(CellStyle style) {
    style.setAlignment(CellStyle.ALIGN_LEFT);
    style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
  }

  /**
   * @return Returns the dateStyle.
   */
  public CellStyle getDateStyle() {
    return dateStyle;
  }

  /**
   * @param dateStyle
   *          The dateStyle to set.
   */
  public void setDateStyle(CellStyle dateStyle) {
    this.dateStyle = dateStyle;
  }

  /**
   * @return Returns the headerStyle.
   */
  public CellStyle getHeaderStyle() {
    return headerStyle;
  }

  /**
   * @param headerStyle
   *          The headerStyle to set.
   */
  public void setHeaderStyle(CellStyle headerStyle) {
    this.headerStyle = headerStyle;
  }

  /**
   * @return Returns the headerTableStyle.
   */
  public CellStyle getHeaderTableStyle() {
    return headerTableStyle;
  }

  /**
   * @param headerTableStyle
   *          The headerTableStyle to set.
   */
  public void setHeaderTableStyle(CellStyle headerTableStyle) {
    this.headerTableStyle = headerTableStyle;
  }

  /**
   * @return Returns the titleStyle.
   */
  public CellStyle getTitleStyle() {
    return titleStyle;
  }

  /**
   * @param titleStyle
   *          The titleStyle to set.
   */
  public void setTitleStyle(CellStyle titleStyle) {
    this.titleStyle = titleStyle;
  }

  /**
   * @return Returns the wrappedStyle.
   */
  public CellStyle getWrappedStyle() {
    return wrappedStyle;
  }

  /**
   * @param wrappedStyle
   *          The wrappedStyle to set.
   */
  public void setWrappedStyle(CellStyle wrappedStyle) {
    this.wrappedStyle = wrappedStyle;
  }

  /**
   * @return Returns the hyperlinkStyle.
   */
  public CellStyle getHyperlinkStyle() {
    return hyperlinkStyle;
  }

  /**
   * @param hyperlinkStyle
   *          The hyperlink style to set.
   */
  public void setHyperlinkStyle(CellStyle hyperlinkStyle) {
    this.hyperlinkStyle = hyperlinkStyle;
  }

  public CellStyle getDefaultStyle() {
    return defaultStyle;
  }

  public void writeTo(OutputStream stream) throws IOException {
    getWb().write(stream);
  }

  /**
   * Makes a new sheet with <code>sheetName</code> and returns it. Only its current row insert
   * marker is tracked in this Class
   * 
   * @param sheetName
   * @return The internal sheet ID of the newly created sheet
   */
  public int createSheet(String sheetName) {
    // If these characters are not replaced, then the Excel workbook cannot be generated and it
    // throws an IllegalArgumentException
    String sanitizedSheetName = StringUtils.replaceEach(sheetName, new String[] { "\\", "/", "*", "[", "]", "?" }, new String[] { "_", "_", "_", "(",
        ")", "_" });

    checkIfSheetExists(sanitizedSheetName);

    Sheet sheet = this.getWb().createSheet(sanitizedSheetName);
    sheet.setZoom(SHEET_NUMERATOR, SHEET_DENOMINATOR);
    sheet.setDefaultColumnWidth(DEFAULT_COLUMN_WIDTH);

    sheetOrder.add(sheet);

    return sheetOrder.size() - 1; // return the sheet position in the list
  }

  /**
   * Checks if the sheet already exists with the specified {@code sheetName}.
   * 
   * @param sheetName the sheet name
   * @throws IteraplanBusinessException if the sheet already exists
   */
  private void checkIfSheetExists(String sheetName) {
    Sheet existingSheet = this.getWb().getSheet(sheetName);
    if (existingSheet != null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.INVALID_EXCEL_TEMPLATE, "Sheet '" + sheetName + "' already exist.");
    }
  }

  protected Sheet getSheetById(int sheetId) {
    return sheetOrder.get(sheetId);
  }

  public void adjustDefaultSettingsSheet() {
    if (sheetOrder.isEmpty()) {
      LOGGER.error("Can't find the default settings worksheet in the currently loaded workbook. Maybe the template workbook is broken?");
      return;
    }
    
    // move the sheet from first to last position in our internal list
    Sheet defaultSheet = sheetOrder.remove(0);
    sheetOrder.add(defaultSheet);
    // and eventually move the sheet in the POI workbook
    getWb().setSheetOrder(DEFAULT_SHEET_KEY, sheetOrder.size());
    getWb().setSelectedTab(0);

    // store the current locale on the configuration settings sheet
    // re-get the sheet, as the Sheet reference seems to become invalid when the sheet is moved around
    defaultSheet = getWb().getSheet(DEFAULT_SHEET_KEY);
    for (Row row : defaultSheet) {
      Cell firstCell = row.getCell(0);
      if (firstCell == null) {
        continue;
      }
      String firstCellContent;
      try {
        firstCellContent = firstCell.getStringCellValue();
      } catch (IllegalStateException poie) {
        LOGGER.error("Can't read this type of cell, skipping.", poie);
        continue;
      }

      if (!"Locale".equals(firstCellContent.trim())) {
        // not yet in the right row
        continue;
      }

      Cell valueCell = row.getCell(1);
      if (valueCell == null) {
        valueCell = row.createCell(1);
      }

      // write the language code to that cell
      valueCell.setCellValue(UserContext.getCurrentLocale().getLanguage());
      break;
    }

  }

  /**
   * Adds the subscribers information
   * 
   * @param sheetId the sheet id
   * @param title the properties string
   * @param subscribers the list of subscribers
   */
  public void addSubscribersForBBT(int sheetId, String title, String subscribers) {
    Sheet sheet = getSheetById(sheetId);
    Row row = sheet.createRow(this.getCurrentRowOfSheet(sheet, 2));
    // Create a cell with the name of the Building Block Type
    Cell cell = row.createCell(1);
    setCellValue(cell, MessageAccess.getStringOrNull(title, getLocale()), getTitleStyle());

    this.setCellValue(row.createCell(2), this.formatString(subscribers), getWrappedStyle());
  }

  /**
   * Adds some preliminary information to the sheet (e.g. the query that resulted the information of
   * this sheet). <br/>
   * The value returned by {@link de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.ExcelSheet#getTitleKey()} is used as headline. If
   * <code>null</code> the standard key will be taken. <br/>
   * If <code>queryData</code> is not <code>null</code> the according information will be placed on
   * the sheet. Otherwise the table header will be placed earlier.
   * 
   * @param queryData
   *          contains the asked query.
   */
  public void addPreface(int sheetId, String title, ExcelAdditionalQueryData queryData) {

    Sheet sheet = getSheetById(sheetId);
    // Create a row and put some cells in it. Rows are 0 based.
    Row row = sheet.createRow(this.getCurrentRowOfSheet(sheet));
    // Create a cell with the name of the Building Block Type
    Cell cell = row.createCell(1);
    setCellValue(cell, MessageAccess.getStringOrNull(title, getLocale()), getTitleStyle());

    if (queryData != null) {

      // Report Time: label
      row = sheet.createRow(this.getCurrentRowOfSheet(sheet, 2));
      cell = row.createCell(1);
      setCellValue(cell, MessageAccess.getStringOrNull("excelReport.report.time", getLocale()), getHeaderStyle());

      // Report Time: content
      cell = row.createCell(2);
      setCellValue(cell, queryData.getStartDate() + DATE_SEPARATOR + queryData.getEndDate(), getHeaderStyle());

      // Report Status
      if (!(queryData.getStatusList() == null)) {
        row = sheet.createRow(this.getCurrentRowOfSheet(sheet, 1));
        // Report Status: label
        cell = row.createCell(1);
        setCellValue(cell, MessageAccess.getStringOrNull("excelReport.report.statuses", getLocale()), getHeaderStyle());

        // Report Status: content (comma-separated enumeration of statuses into one cell)
        cell = row.createCell(2);
        StringBuffer sb = new StringBuffer();
        for (String status : queryData.getStatusList()) {
          if (sb.length() > 0) {
            sb.append(PREFACE_SEPARATOR);
          }
          sb.append(MessageAccess.getStringOrNull(status, getLocale()));
        }
        setCellValue(cell, sb.toString(), getHeaderStyle());
      }
    }
  }

  /**
   * Adds headers stored in <code>headers</code> to the current sheet. If required, a special width
   * for the corresponding column can be set by providing a value in <code>headersWidth</code> using
   * header as key.<br/>
   * <b>IMPORTANT</b>: Headers are added in the order provided in <code>headers</code>.
   * 
   * @param headers
   *          headers to be added
   */
  public void addHeaders(int sheetId, List<ExcelSheet.Header> headers) {
    Sheet sheet = getSheetById(sheetId);
    Drawing drawing = sheet.createDrawingPatriarch();
    CreationHelper factory = sheet.getWorkbook().getCreationHelper();
    Row row = sheet.createRow(this.getCurrentRowOfSheet(sheet, 3));
    int columnIndex = 0;
    for (ExcelSheet.Header header : headers) {
      int currColumnIndex = columnIndex;
      Cell cell = row.createCell(columnIndex);
      if (header.getDescription() != null) {
        ClientAnchor commentAnchor = factory.createClientAnchor();
        //Sizing the comment 1x3 cells
        commentAnchor.setCol1(cell.getColumnIndex());
        commentAnchor.setCol2(cell.getColumnIndex() + 1);
        commentAnchor.setRow1(row.getRowNum());
        commentAnchor.setRow2(row.getRowNum() + 3);

        Comment comment = drawing.createCellComment(commentAnchor);
        RichTextString str = factory.createRichTextString(header.getDescription());
        comment.setString(str);
        comment.setAuthor("");
        cell.setCellComment(comment);
      }

      setCellValue(cell, header.getLabel(), getHeaderTableStyle());
      Integer width = header.getWidth();
      if (width != null) {
        sheet.setColumnWidth(currColumnIndex, width.intValue());
      }
      columnIndex++;
    }

    LOGGER.debug("Added headers.");
  }

  /**
   * Adds a new line with contents provided in <code>contents</code>. This method handles values of
   * types {@link String} , {@link Date}, {@link Integer} and {@link HashBucketMap}(for attributes)
   * separately. For all attributes in {@link HashBucketMap} only one style can be supported.<br/>
   * If required {@link Hyperlink}s can also be provided for the given <code>contents</code>. In
   * this case a link should be in <code>linksForContents</code> with the actual content as key. In
   * the current version only links for {@link String}s and {@link Integer}s can be added.<br/>
   * <b>IMPORTANT</b>: The values are added in the order given in <code>contents</code>.
   * 
   * @param contents
   *          the map containing all contents for the current line including attributes in form of a
   *          {@link HashBucketMap}. If required, a special style can be provided as value for the
   *          corresponding key.
   * @param linksForContents
   *          the map with links for contents
   */
  public void addContentLine(int sheetId, List<?> contents, Map<?, String> linksForContents, List<AttributeType> activatedAttributeTypes) {
    Sheet sheet = getSheetById(sheetId);
    int columnIndex = 0;
    Row row = sheet.createRow(getCurrentRowOfSheet(sheet, 1));

    for (Object content : contents) {
      CellStyle style = getWrappedStyle();
      Hyperlink link = null;
      if (linksForContents != null) {
        link = createHyperlink(linksForContents.get(content));
      }

      // required to keep the right order of columns
      if (content == null) {
        this.setCellValue(row.createCell(columnIndex++), "", null);
      }
      else if (content instanceof String) {
        setStringContent(columnIndex++, row, content, style, link);
      }
      else if (content instanceof Date) {
        this.setCellValue(row.createCell(columnIndex++), (Date) content, getDateStyle());
      }
      else if (content instanceof Integer) {
        setIntegerContent(columnIndex++, row, content, style, link);
      }
      else if (content instanceof HashBucketMap<?, ?>) {
        setMapContent(activatedAttributeTypes, columnIndex, row, content, style);
      }
      else {
        LOGGER.warn("Received content of unsupported type: {0} for adding to an excel sheet - ignoring", content);
      }

    }
  }

  @SuppressWarnings("unchecked")
  private void setMapContent(List<AttributeType> activatedAttributeTypes, int columnIndex, Row row, Object content, CellStyle style) {
    try {
      HashBucketMap<AttributeType, AttributeValue> map = (HashBucketMap<AttributeType, AttributeValue>) content;
      this.addAttributeValues(activatedAttributeTypes, map, columnIndex, row, style);
    } catch (ClassCastException e) {
      LOGGER.warn("Expected a map with attributes, received unexpected values - ignoring.", e);
    }
  }

  private void setIntegerContent(int columnIndex, Row row, Object content, CellStyle style, Hyperlink link) {
    if (link != null) {
      this.setHyperlink(row.createCell(columnIndex), (Integer) content, link, getHyperlinkStyle());
    }
    else {
      this.setCellValue(row.createCell(columnIndex), (Integer) content, style);
    }
  }

  private void setStringContent(int columnIndex, Row row, Object content, CellStyle style, Hyperlink link) {
    if (link != null) {
      this.setHyperlink(row.createCell(columnIndex), this.formatString((String) content), link, getHyperlinkStyle());
    }
    else {
      this.setCellValue(row.createCell(columnIndex), this.formatString((String) content), style);
    }
  }

  /**
   * Inserts the values of the given attribute types in the given row, starting from the given
   * column index. For multi-lined text attributes the wrapped cell style is used if
   * <code>style</code> is <code>null</code>.
   * 
   * @param attributeTypes
   *          types of attributes
   * @param attributeTypeToAttributeValues
   *          map with values for the types
   * @param columnIndex
   *          the current column index
   * @param row
   *          the current row
   * @param style
   *          this <code>style</code> will be applied to all given attributes
   */
  private void addAttributeValues(List<? extends AttributeType> attributeTypes,
                                  HashBucketMap<AttributeType, AttributeValue> attributeTypeToAttributeValues, int columnIndex, Row row,
                                  CellStyle style) {
    int myColumnIndex = columnIndex;
    for (AttributeType at : attributeTypes) {
      Cell cell = row.createCell(myColumnIndex++);
      List<AttributeValue> values = attributeTypeToAttributeValues.getBucketNotNull(at);

      if (values.size() > 0) {
        // visualize number attributes
        if (at instanceof NumberAT) {
          setNumberValue(style, cell, values);
          continue;
        }
        // visualize date attributes
        if (at instanceof DateAT) {
          setDateValue(cell, values);
          continue;
        }
        // visualize all other types of attributes as strings
        String valueString = this.formatString(getValueString(values));
        this.setCellValue(cell, valueString, getCellStyle(style, at));
      }
    }
  }

  private String getValueString(List<AttributeValue> values) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (AttributeValue value : values) {
      if (!first) {
        sb.append(IN_LINE_SEPARATOR);
      }
      else {
        first = false;
      }
      sb.append(value.getLocalizedValueString(UserContext.getCurrentLocale()));
    }
    return sb.toString();
  }

  private CellStyle getCellStyle(CellStyle style, AttributeType at) {
    CellStyle cellStyle = null;
    if (at instanceof TextAT) {
      TextAT textAt = (TextAT) at;
      if (textAt.isMultiline() && (style == null)) {
        cellStyle = this.getWrappedStyle();
      }
    }
    if (cellStyle == null) {
      cellStyle = style;
    }
    return cellStyle;
  }

  private void setDateValue(Cell cell, List<AttributeValue> values) {
    // assumes that only one number is available
    AttributeValue value = values.iterator().next();
    if (value instanceof DateAV) {
      this.setCellValue(cell, ((DateAV) value).getValue(), null);
    }
    else {
      LOGGER.warn("Unexpectedly received a not number value for a number attribute type " + "for excel export. Omitting the value");
    }
  }

  private void setNumberValue(CellStyle style, Cell cell, List<AttributeValue> values) {
    // assumes that only one number is available
    AttributeValue value = values.iterator().next();
    if (value instanceof NumberAV) {
      this.setCellValue(cell, ((NumberAV) value).getValue().doubleValue(), style);
    }
    else {
      LOGGER.warn("Unexpectedly received a not number value for a number attribute type " + "for excel export. Omitting the value");
    }
  }

  /**
   * @return the current row counter for a sheet
   */
  protected int getCurrentRowOfSheet(Sheet sheet) {
    return getCurrentRowOfSheet(sheet, 0);
  }

  /**
   * Formats the given <code>description</code>. Removes all carriage return characters and replaces
   * wiki links. Finally, the string is trimmed to {@link #MAXIMUM_CELL_LENGTH} characters, if it
   * was longer.
   * 
   * @param description
   *          Input description string
   * @return the formated string or an empty string, if no match could be found.
   */
  protected String formatString(String description) {
    if (description == null) {
      return ("");
    }

    String myDescription = description.replaceAll("\r", "");
    Matcher matcher = this.urlPattern.matcher(myDescription);
    String markupFreeString = matcher.replaceAll("$1");
    if (markupFreeString.length() <= MAXIMUM_CELL_LENGTH) {
      return markupFreeString;
    }

    String trimmedString = markupFreeString.substring(0, MAXIMUM_CELL_LENGTH);
    return trimmedString + " ... [truncated]";
  }

  /**
   * Returns the current row counter for a sheet, incrementing it by <code>preIncrement</code>
   * beforehand.
   * 
   * @param sheet The work sheet, for which the current row number is requested.
   * @param preIncrement The number that shall be added to the current row number, before the (new) row number is returned
   * @return the incremented counter
   */
  protected int getCurrentRowOfSheet(Sheet sheet, int preIncrement) {

    Integer intPreIncrement = Integer.valueOf(preIncrement);
    if (!sheetCurrentPosition.containsKey(sheet)) {
      sheetCurrentPosition.put(sheet, intPreIncrement);
      return preIncrement;
    }
    else {
      int curValue = sheetCurrentPosition.get(sheet).intValue();
      curValue += preIncrement;
      sheetCurrentPosition.put(sheet, Integer.valueOf(curValue));
      return curValue;
    }
  }

  /**
   * Sets given value and style of the given cell.
   * 
   * @param cell
   *          the cell in question
   * @param value
   *          the value to be set
   * @param style
   *          if <code>null</code> the default style is taken
   */
  private void setCellValue(Cell cell, String value, CellStyle style) {
    if (cell == null) {
      return;
    }
    CreationHelper createHelper = getWb().getCreationHelper();
    if (value != null) {
      cell.setCellValue(createHelper.createRichTextString(value));
    }
    CellStyle styleToSet = (style != null ? style : getDefaultStyle());
    cell.setCellStyle(styleToSet);
  }

  /**
   * Sets given value and style of the given cell.
   * 
   * @param cell
   *          the cell in question
   * @param value
   *          the value to be set
   * @param style
   *          if <code>null</code> the default style is taken
   */
  private void setCellValue(Cell cell, double value, CellStyle style) {
    if (cell == null) {
      return;
    }
    cell.setCellValue(value);
    CellStyle styleToSet = (style != null ? style : getDefaultStyle());
    cell.setCellStyle(styleToSet);
  }

  /**
   * Sets given value and style of the given cell and adds the given <code>link</code> as hyperlink
   * to the <code>cell</code>.
   * 
   * @param cell
   *          the cell in question
   * @param value
   *          the value to be set
   * @param link
   *          the link to be added
   * @param style
   *          if <code>null</code> the default style for hyperlinks is used
   */
  private void setHyperlink(Cell cell, String value, Hyperlink link, CellStyle style) {
    if (cell == null) {
      return;
    }
    CreationHelper createHelper = getWb().getCreationHelper();
    if (value != null) {
      cell.setCellValue(createHelper.createRichTextString(value));
    }
    if (link != null) {
      cell.setHyperlink(link);
    }

    CellStyle styleToSet = (style != null ? style : getHyperlinkStyle());
    cell.setCellStyle(styleToSet);
  }

  /**
   * Sets given value and style of the given cell.
   * 
   * @param cell
   *          the cell in question
   * @param value
   *          the value to be set
   * @param style
   *          if <code>null</code> the default style for formatting dates is used
   */
  private void setCellValue(Cell cell, Date value, CellStyle style) {
    if (cell == null) {
      return;
    }
    if (value != null) {
      cell.setCellValue(value);
    }

    CellStyle styleToSet = (style != null ? style : getDateStyle());
    cell.setCellStyle(styleToSet);
  }

  /**
   * Sets given value and style of the given cell.
   * 
   * @param cell
   *          the cell in question
   * @param value
   *          the value to be set
   * @param style
   *          if <code>null</code> the default style is taken
   */
  private void setCellValue(Cell cell, Integer value, CellStyle style) {
    if (value != null) {
      this.setCellValue(cell, value.doubleValue(), style);
    }
  }

  /**
   * Sets given value and style of the given cell and adds the given <code>link</code> as hyperlink
   * to the <code>cell</code>.
   * 
   * @param cell
   *          the cell in question
   * @param value
   *          the value to be set
   * @param link
   *          the link to be added
   * @param style
   *          if <code>null</code> the default style for hyperlinks is taken
   */
  private void setHyperlink(Cell cell, Integer value, Hyperlink link, CellStyle style) {
    if (cell == null) {
      return;
    }
    if ((value != null) && (link != null)) {
      cell.setCellFormula("HYPERLINK(\"" + link.getAddress() + "\"," + value + ")");
    }

    CellStyle styleToSet = (style != null ? style : getHyperlinkStyle());
    cell.setCellStyle(styleToSet);
  }

  /**
   * Creates a hyperlink from a <code>url</code>.
   * 
   * @param url
   * @return a hyperlink to the <code>url</code>or <code>null</code> if url is null
   */
  private Hyperlink createHyperlink(String url) {
    if (url == null) {
      return null;
    }
    CreationHelper createHelper = this.getWb().getCreationHelper();
    Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_URL);
    link.setAddress(url);

    return link;
  }

  // Functionality that depends on these static functions should be moved into this class?
  //

  public static int getColumnWidthForId() {
    return COLUMN_WIDTH_FOR_ID;
  }

  public static int getColumnWidthWide() {
    return COLUMN_WIDTH_WIDE;
  }

  public static int getColumnWidthForSheetKey() {
    return COLUMN_WIDTH_FOR_SHEET_KEY;
  }

  public int getNumberOfSheets() {
    return getWb().getNumberOfSheets();
  }

  /**
   * For testing purposes only. All manipulation of sheets should be done internally in this class.
   * 
   * @param index
   */
  public Sheet getSheetAt(int index) {
    return getWb().getSheetAt(index);
  }

  /**
   * For testing purposes only. All manipulation of sheets should be done internally in this class.
   * 
   * @param index
   */
  public String getSheetName(int index) {
    return getWb().getSheetName(index);
  }
}
