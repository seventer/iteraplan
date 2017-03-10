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
package de.iteratec.iteraplan.businesslogic.exchange.nettoExport;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * CSV Exporter for Netto List
 */
public final class NettoCSVTransformer implements NettoTransformer {

  private static final Logger LOGGER = Logger.getIteraplanLogger(NettoCSVTransformer.class);

  private TableStructure      tableStructure;

  private NettoCSVTransformer() {
    // use static factory
  }

  public static NettoTransformer newInstance(TableStructure table) {
    NettoCSVTransformer csvTransformer = new NettoCSVTransformer();
    csvTransformer.tableStructure = table;
    return csvTransformer;
  }

  /**{@inheritDoc}**/
  @Override
  public void transform(List<?> sourceList, OutputStream out, TypeOfBuildingBlock typeOfBuildingBlock) {

    CSVWriter writer;

    char[] charArray = IteraplanProperties.getProperties().getProperty(IteraplanProperties.CSV_SEPARATOR).toCharArray();
    char seperator;

    //check the lengh of the char array because only one char as separator can be used
    if (charArray.length == 1) {
      seperator = charArray[0];
    }
    else {
      LOGGER.warn("Number of separator chars unequals one, use the default one (';')");
      seperator = ';';
    }

    try {

      /*
       * Important: This prints the BOM (Byte Order Marker) to the stream 
       * This is used for displaying the csv files correctly in excel
       */
      PrintStream pStream = new PrintStream(out, false, "UTF-8");
      pStream.print('\ufeff');
      pStream.flush();

      /*
       * open the output stream as UTF-8 (Unicode) stream
       * the library (opencsv) wraps all text strings in quotation marks
       * " and ; will be masked with quotation marks -> """ ";"
       * A new line in a string is displayed as CR LF
       * A new record (a new line at the end of a dataset) is represented by LF
       * 
       */
      writer = new CSVWriter(new OutputStreamWriter(out, "UTF-8"), seperator);

      ColumnStructure[] columns = tableStructure.getColumns();

      //set the column header
      List<String> headerList = new ArrayList<String>();
      for (ColumnStructure columnStructure : columns) {
        headerList.add(columnStructure.getColumnHeader());
      }

      //write headerList to writer
      writer.writeNext(headerList.toArray(new String[headerList.size()]));

      //iterate through the objects
      for (Object obj : sourceList) {
        if (obj instanceof BuildingBlock) {
          BuildingBlock bb = (BuildingBlock) obj;

          // skip virutal root element
          if (bb instanceof AbstractHierarchicalEntity<?>) {
            AbstractHierarchicalEntity<?> hierarchicalEntity = (AbstractHierarchicalEntity<?>) bb;
            if (hierarchicalEntity.isTopLevelElement()) {
              continue;
            }
          }

          List<String> columnList = new ArrayList<String>();

          for (ColumnStructure columnStructure : columns) {
            //prepare the columns here...
            Object resolvedValue = columnStructure.resolveValue(bb);
            columnList.add(resolvedValue.toString());

          }

          //add column to writer
          writer.writeNext(columnList.toArray(new String[columnList.size()]));

        }
      }

      //flush to stream
      out.flush();
      pStream.close();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
