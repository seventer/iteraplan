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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Hyperlink;

import com.google.common.base.Joiner;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.WorkbookContext;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;


/**
 * Generates the new Excel-Sheet for the specified {@link RelationshipExpression}.
 */
public class RelationshipSheetGenerator extends AbstractSheetGenerator {

  /**
   * @param wbContext the instance of the workbook context
   * @param relationshipExpression the relationship to generate the sheet for
   */
  public RelationshipSheetGenerator(WorkbookContext wbContext, RelationshipExpression relationshipExpression) {
    super(wbContext, relationshipExpression);
  }

  // ===== Header Area =====

  /** add the header cells for each feature, i.e. for each column */
  protected void createFeatureHeaders() {
    for (RelationshipEndExpression relExpr : getRelationshipEnds()) {
      addStandardColumnHeader(relExpr);
    }
  }

  // ===== Data Area =====

  /**{@inheritDoc}**/
  @Override
  protected void formatDataArea() {
    initDataCells();
    addDropdowns();
  }

  @Override
  public void addDropdowns() {
    addDropdownBoxToRelations();
  }

  private void addDropdownBoxToRelations() {
    for (RelationshipEndExpression relationshipEndExpression : getRelationshipEnds()) {
      addDropdownsToRelationshipColumn(relationshipEndExpression);
    }
  }

  // ===== Implement/Override methods =====

  protected String createCompleteSheetName() {
    List<String> abbrevations = getAbbreviations(getRelationshipEnds());
    return Joiner.on("-").skipNulls().join(abbrevations);
  }

  // ===== Helpers =====

  @Override
  protected RelationshipExpression getTypeExpression() {
    return (RelationshipExpression) typeExpression;
  }

  private List<String> getAbbreviations(List<RelationshipEndExpression> relationshipEnds) {
    List<String> result = new ArrayList<String>();
    for (RelationshipEndExpression re : relationshipEnds) {
      result.add(re.getType().getAbbreviation());
    }
    return result;
  }

  private List<RelationshipEndExpression> getRelationshipEnds() {
    return getTypeExpression().getRelationshipEnds();
  }

  /**{@inheritDoc}**/
  protected void addHeaderHyperlink(FeatureExpression<?> featureExpression, Cell headerCell) {
    Hyperlink link = wbContext.getWb().getCreationHelper().createHyperlink(Hyperlink.LINK_DOCUMENT);
    String sheetName = wbContext.getSheetContextByExpressionName(featureExpression.getType().getPersistentName()).getSheetName();
    link.setAddress("'" + sheetName + "'!A1");
    headerCell.setHyperlink(link);
  }
}
