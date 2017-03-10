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
package de.iteratec.iteraplan.db;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.setup.LocalApplicationContextUtil;


/**
 * A utility class to have Hibernate generate an SQL script for generating the schema in a given database system.
 * This class has a {@code #main(String[])} method and accepts one optional parameter: a file name
 * to write the generated SQL script to.
 * 
 * The database type and connection specifics are completely configured via Spring mechanisms,
 * effectively through {@code iteraplan-db.properties}.
 */
public class SchemaGenerationScriptCreator {

  private static final Logger LOGGER              = Logger.getIteraplanLogger(SchemaGenerationScriptCreator.class);
  private String              outputFile          = "schema-export.sql";
  private static final String STATEMENT_DELIMITER = ";";

  public static void main(String[] args) {
    SchemaGenerationScriptCreator creator = new SchemaGenerationScriptCreator();
    String outputFilename = args.length > 0 ? args[0] : null;
    creator.generateDatabaseSchemaScript(outputFilename);
  }

  public void generateDatabaseSchemaScript(String sqlOutputFilename) {
    ConfigurableApplicationContext context = LocalApplicationContextUtil.getApplicationContext();

    // potential starting point for further development - make printing to console configurable
    boolean printToConsole = true;

    LOGGER.info("Running hbm2ddl schema export");

    if (StringUtils.isNotBlank(sqlOutputFilename)) {
      outputFile = sqlOutputFilename;
    }

    Writer outputFileWriter = null;

    try {
      if (outputFile != null) {
        LOGGER.info("writing generated schema to file: {0}", outputFile);
        System.out.println("writing generated schema to file: " + outputFile);
        outputFileWriter = new FileWriter(outputFile);
      }

      // retrieve Hibernate configuration object from Spring context
      AnnotationSessionFactoryBean sessionFactoryBean = context.getBean(AnnotationSessionFactoryBean.class);
      Configuration hibernateCfg = sessionFactoryBean.getConfiguration();
      Dialect dialect = Dialect.getDialect(hibernateCfg.getProperties());

      // generate drop SQL statements and write to file
      String[] dropSQL = hibernateCfg.generateDropSchemaScript(dialect);
      for (int i = 0; i < dropSQL.length; i++) {
        writeStatement(printToConsole, outputFileWriter, dropSQL[i]);
      }

      // generate create table SQL statements and write to file
      String[] createSQL = hibernateCfg.generateSchemaCreationScript(dialect);
      for (int j = 0; j < createSQL.length; j++) {
        writeStatement(printToConsole, outputFileWriter, createSQL[j]);
      }

      LOGGER.info("schema export complete");

    } catch (Exception e) {
      LOGGER.error("schema export unsuccessful", e);

    } finally {
      context.close();

      try {
        if (outputFileWriter != null) {
          outputFileWriter.close();
        }
      } catch (IOException ioe) {
        LOGGER.error("Error closing output file: " + outputFile, ioe);
      }
    }
  }

  /**
   * Writes the passed {@code sqlStatement} to the output writer {@code fileOutput} and adds statement
   * separators to make the resulting file parseable.
   * 
   * @param printToConsole true to write the statement to the console in addition
   * @param fileOutput
   * @param sqlStatement
   */
  private void writeStatement(boolean printToConsole, Writer fileOutput, final String sqlStatement) throws IOException {
    String formatted = sqlStatement;
    if (STATEMENT_DELIMITER != null) {
      formatted = formatted + STATEMENT_DELIMITER + "\n";
    }
    if (printToConsole) {
      System.out.print(formatted);
    }
    LOGGER.debug(formatted);
    if (outputFile != null) {
      fileOutput.write(formatted);
    }

  }

}
