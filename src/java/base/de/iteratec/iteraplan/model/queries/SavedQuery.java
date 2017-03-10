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
package de.iteratec.iteraplan.model.queries;

import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class SavedQuery extends SavedQueryEntity {

  private static final long              serialVersionUID = 8941312440864077186L;

  private static Map<ReportType, String> diagrams         = Maps.newHashMap();

  private ReportType                     type;
  private String                         schemaFile;
  private BuildingBlockType              resultBbType;

  public ReportType getType() {
    return type;
  }

  public BuildingBlockType getResultBbType() {
    return resultBbType;
  }

  public void setResultBbType(BuildingBlockType bbType) {
    this.resultBbType = bbType;
  }

  public String getSchemaFile() {
    return schemaFile;
  }

  public void setType(ReportType type) {
    this.type = type;
  }

  public void setSchemaFile(String schemaFile) {
    this.schemaFile = schemaFile;
  }

  @Override
  void validate() {
    if (!getSchemaMapping().containsKey(type)) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.WRONG_FILE_TYPE_EXCEPTION);
    }
  }

  /**
   * A map from diagram resource keys to the locations of XML schemas responsible for the respective
   * diagram
   * 
   * @return The mapping from key to schema name
   */
  public static final Map<ReportType, String> getSchemaMapping() {
    synchronized (diagrams) {
      if (diagrams.isEmpty()) {
        diagrams.put(ReportType.INFORMATIONFLOW, Constants.SCHEMA_QUERY);
        diagrams.put(ReportType.PORTFOLIO, Constants.SCHEMA_QUERY);
        diagrams.put(ReportType.MASTERPLAN, Constants.SCHEMA_QUERY);
        diagrams.put(ReportType.CLUSTER, Constants.SCHEMA_QUERY);
        diagrams.put(ReportType.LANDSCAPE, Constants.SCHEMA_GRAPHICAL_LANDSCAPE);
        diagrams.put(ReportType.COMPOSITE, Constants.SCHEMA_COMPOSITE_DIAGRAM);
        diagrams.put(ReportType.PIE, Constants.SCHEMA_QUERY);
        diagrams.put(ReportType.BAR, Constants.SCHEMA_QUERY);
        diagrams.put(ReportType.VBBCLUSTER, Constants.SCHEMA_QUERY);
        diagrams.put(ReportType.TIMELINE, Constants.SCHEMA_QUERY);
        diagrams.put(ReportType.LINE, Constants.SCHEMA_QUERY);
        diagrams.put(ReportType.MATRIX, Constants.SCHEMA_QUERY);
        diagrams.put(ReportType.TABVIEW, Constants.SCHEMA_QUERY);

        for (TypeOfBuildingBlock block : TypeOfBuildingBlock.values()) {
          ReportType sqt = ReportType.fromValue(block.toString());
          diagrams.put(sqt, Constants.SCHEMA_QUERY);
        }
      }
      return diagrams;
    }
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    builder.append("id", getId());
    builder.append("name", getName());
    builder.append("bbt", getResultBbType());

    return builder.toString();
  }
}
