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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.Resource;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.SheetContext;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.WorkbookContext;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;


/**
 *  This class is responsible for the creation of the introduction sheet
 */
public class DataExportIntroSheetGenerator extends AbstractIntroSheetGenerator {

  private WorkbookContext wbContext;

  /**
   * Default constructor.
   * @param wbContext
   * @param logoImage 
   */
  public DataExportIntroSheetGenerator(WorkbookContext wbContext, Resource logoImage) {
    super(logoImage);
    this.wbContext = wbContext;
  }

  protected Workbook getWorkbook() {
    return wbContext.getWb();
  }

  protected void createSummary() {

    // sort the sheet in types. This extra sorting is better, as generating the entries
    // in generateSheet() would pollute the code too much
    List<SheetContext> types = Lists.newArrayList();
    List<SheetContext> relationships = Lists.newArrayList();
    List<SheetContext> enums = Lists.newArrayList();
    for (SheetContext sheetCtx : wbContext.getSheetContexts()) {
      NamedExpression expression = sheetCtx.getExpression();
      if (expression instanceof UniversalTypeExpression) {
        types.add(sheetCtx);
      }
      else if (expression instanceof RelationshipExpression) {
        relationships.add(sheetCtx);
      }
      else if (expression instanceof EnumerationExpression) {
        enums.add(sheetCtx);
      }
    }

    int rowAt = createSummarySection(types, SUMMARY_ROW, MessageAccess.getString("excel.export.header.types"));
    rowAt = createSummarySection(relationships, rowAt, MessageAccess.getString("excel.export.header.relationships"));
    rowAt = createSummarySection(enums, rowAt, MessageAccess.getString("excel.export.header.enums"));

    adjustSheetColumnWidths();
  }

  /**
   * Creates a section of the summary, containing links to one of the types of sheets 
   * 
   * @param sheetContexts list of sheets to link to. Only contains one type of {@link NamedExpression}
   * @param startAt the row to start at in the introduction sheet
   * @param sectionHeader the header of the section
   * 
   * @return the row number that was reached while inserting the links
   */
  private int createSummarySection(List<SheetContext> sheetContexts, int startAt, String sectionHeader) {
    int rowNr = startAt;
    Map<IteraExcelStyle, CellStyle> styles = wbContext.getStyles();
    Workbook workbook = wbContext.getWb();
    CreationHelper createHelper = workbook.getCreationHelper();

    // header
    Sheet introSheet = getIntroductionSheet();
    Cell headerCell = introSheet.createRow(rowNr++).createCell(SUMMARY_COL);
    headerCell.setCellValue(sectionHeader);
    headerCell.setCellStyle(styles.get(IteraExcelStyle.HEADER));
    headerCell.getRow().createCell(SUMMARY_COL + 1).setCellStyle(styles.get(IteraExcelStyle.HEADER));

    for (SheetContext sheetContext : sheetContexts) {
      String sheetName = sheetContext.getSheetName();
      String extraInfo = sheetContext.getExpression().getName();
      // name is empty, we assume it's a relationship, we need to get the name of the relationship ends
      if (StringUtils.isEmpty(extraInfo)) {
        NamedExpression expression = sheetContext.getExpression();
        if (expression instanceof RelationshipExpression) {
          extraInfo = createRelationshipExtrainfo((RelationshipExpression) expression);
        }
      }

      Row entryRow = introSheet.createRow(rowNr++);
      Cell hyperlinkCell = entryRow.createCell(SUMMARY_COL);
      hyperlinkCell.setCellValue(sheetName);
      entryRow.createCell(SUMMARY_COL + 1).setCellValue(extraInfo);

      Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
      link.setAddress("'" + sheetName + "'!A1");
      hyperlinkCell.setHyperlink(link);
      hyperlinkCell.setCellStyle(styles.get(IteraExcelStyle.HYPERLINK));
    }

    //spacing between sections
    introSheet.createRow(rowNr++);

    return rowNr;
  }

  private String createRelationshipExtrainfo(RelationshipExpression expression) {
    List<String> result = new ArrayList<String>();
    for (RelationshipEndExpression re : expression.getRelationshipEnds()) {
      result.add(re.getType().getName());
    }

    return Joiner.on(" - ").skipNulls().join(result);
  }

}
