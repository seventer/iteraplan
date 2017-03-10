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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.IteraExcelStyle;


/**
 * The class containing the workbook related information like the workbook instance and 
 * the map of available styles.
 */
public class WorkbookContext {

  private final Workbook                        wb;
  private final Map<IteraExcelStyle, CellStyle> styles;

  /** list of sheets. */
  private final List<SheetContext>              sheetContexts          = Lists.newArrayList();

  /** map from sheet name to sheet. Same instances as in {@link #sheetContexts}. */
  private final Map<String, SheetContext>       nameSheetMap           = new HashMap<String, SheetContext>();

  /** map from expression name to sheet. Same instances as in {@link #sheetContexts}. */
  private final Map<String, SheetContext>       expressionNameSheetMap = new HashMap<String, SheetContext>();

  public static final int                       DEFAULT_EMPTY_ROW_COUNT = 10;

  /**
   * Default constructor.
   * 
   * @param wb the workbook
   * @param styles the map of styles available
   */
  public WorkbookContext(Workbook wb, Map<IteraExcelStyle, CellStyle> styles) {
    this.wb = Preconditions.checkNotNull(wb);
    this.styles = Preconditions.checkNotNull(styles);
  }

  /**
   * Returns the current {@link Workbook} instance.
   * 
   * @return wb the workbook instance
   */
  public Workbook getWb() {
    return wb;
  }

  /**
   * Returns the map of available styles.
   * 
   * @return styles the map of available styles.
   */
  public Map<IteraExcelStyle, CellStyle> getStyles() {
    return styles;
  }

  public void addSheetContext(SheetContext sheetContext) {
    sheetContexts.add(sheetContext);
    nameSheetMap.put(sheetContext.getSheetName(), sheetContext);
    expressionNameSheetMap.put(sheetContext.getExpression().getPersistentName(), sheetContext);
  }

  /**
   * @return sheetContexts the sheetContexts
   */
  public List<SheetContext> getSheetContexts() {
    return sheetContexts;
  }

  /**
   * @param sheetName
   * @return the SheetContext for the given sheet name.
   */
  public SheetContext getSheetContext(String sheetName) {
    return nameSheetMap.get(sheetName);
  }

  /**
   * @param expressionName
   * @return the SheetContext for the given expression name.
   */
  public SheetContext getSheetContextByExpressionName(String expressionName) {
    return expressionNameSheetMap.get(expressionName);
  }
}
