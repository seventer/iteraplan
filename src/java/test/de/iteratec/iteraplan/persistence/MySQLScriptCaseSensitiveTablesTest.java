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
package de.iteratec.iteraplan.persistence;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.db.SqlScriptExecutor;


/**
 * <b>Attention:</b> Test needs the schema-export.sql file to be created prior to its execution.
 * <p>
 * Test to check MySQL migration scripts for incorrect lower case letters in table names.
 * The test reads the names of all tables iteraplan uses from the {@code schema-export.sql} created
 * by the {@code test.junitreport} ant target.
 * It then checks every occurrence of those table names in the scripts to check, if they are not within a comment
 * or part of another word/text.
 * </p>
 * <ul>
 * <li>The test checks all scripts in the {@code migration} folder which contain {@code mysql} in their name.
 *     Only the most current scripts are of actual interest, but it's easier just to test all of them, than to
 *     determine or manually maintain a version number to check and match it to filenames which might or might
 *     not adhere to naming conventions.</li>
 * <li>Scripts can be excluded from the check by adding their name (in lower case) to the
 *     {@link #IGNORED_SCRIPTS} list.</li>
 * <li>Tables can be excluded from the check by adding their name (in lower case) to the
 *     {@link #IGNORED_TABLES} list.</li>
 * </ul>
 */
public class MySQLScriptCaseSensitiveTablesTest {

  private static final Logger      LOGGER                        = Logger.getIteraplanLogger(MySQLScriptCaseSensitiveTablesTest.class);

  private static final String      SCHEMA_EPORT_SQL              = "/schema-export.sql";
  private static final String      MIGRATION_DIR                 = "migration";
  private static final Pattern     CREATE_TABLE_NAME_PATTERN     = Pattern.compile("create table ([0-9A-Z_]+)", Pattern.CASE_INSENSITIVE);
  private static final Pattern     MYSQL_SCRIPT_FILENAME_PATTERN = Pattern.compile("^.*mysql.*.sql$", Pattern.CASE_INSENSITIVE);
  private static final Pattern     LOWER_CASE_PATTERN            = Pattern.compile(".*[a-z].*");
  private static final Pattern     UPPER_CASE_PATTERN            = Pattern.compile(".*[A-Z].*");

  /** Some tables may need to be lower case, as opposed to the general rule */
  private static final Set<String> LOWER_CASE_TABLES             = ImmutableSet.of("hibernate_sequences");
  /** Table names ignored by this test, given in lower case */
  private static final Set<String> IGNORED_TABLES                = ImmutableSet.of();
  /** Names of scripts ignored by this test, given in lower case */
  private static final Set<String> IGNORED_SCRIPTS               = ImmutableSet.of("migration_iteraplan_21_to_22_mysql.sql",
                                                                     "schema-export-mysql.sql", "backup-13-mysql.sql");

  @Test
  public void testCaseSensitiveTables() {
    File migrationDir = new File(MIGRATION_DIR);
    assertTrue("Could not find migration script directory " + migrationDir.getAbsolutePath(), migrationDir.exists());

    Set<File> mysqlScripts = getMySQLMigrationScripts(migrationDir);
    Set<String> tableNames = readTableNames();
    Pattern tableNamesPattern = createTableNamesPattern(tableNames);

    boolean fine = true;
    for (File script : mysqlScripts) {
      for (TableNameEntry extracted : extractTableNamesFromScript(script, tableNamesPattern)) {
        for (String name : extracted.tableNames) {
          if (LOWER_CASE_TABLES.contains(name.toLowerCase())) {
            fine &= checkAllLowerCase(script, extracted, name);
          }
          else {
            fine &= checkAllUpperCase(script, extracted, name);
          }
        }
      }
    }
    assertTrue("Tablenames containing wrong capitalization were found in a MySQL-Script (see log)", fine);
  }

  private boolean checkAllUpperCase(File script, TableNameEntry extracted, String name) {
    Matcher m = LOWER_CASE_PATTERN.matcher(name);
    if (m.matches()) {
      LOGGER.error("Script \"{0}\", Line {1}: table name \"{2}\" contains lower case characters, but needs to be all upper case!", script,
          Integer.valueOf(extracted.lineNr), name);
      return false;
    }
    return true;
  }

  private boolean checkAllLowerCase(File script, TableNameEntry extracted, String name) {
    Matcher m = UPPER_CASE_PATTERN.matcher(name);
    if (m.matches()) {
      LOGGER.error("Script \"{0}\", Line {1}: table name \"{2}\" contains upper case characters, but needs to be all lower case!", script,
          Integer.valueOf(extracted.lineNr), name);
      return false;
    }
    return true;
  }

  private List<TableNameEntry> extractTableNamesFromScript(File script, Pattern tableNamePattern) {
    List<TableNameEntry> extracted = Lists.newArrayList();

    FileReader in = null;
    BufferedReader buf = null;
    try {
      in = new FileReader(script);
      buf = new BufferedReader(in);

      String line = buf.readLine();
      int lineNr = 1;
      while (line != null) {
        String lineWithoutComments = line.contains("--") ? line.substring(0, line.indexOf("--")) : line;
        Matcher m = tableNamePattern.matcher(lineWithoutComments);
        List<String> names = Lists.newArrayList();
        while (m.find()) {
          names.add(m.group(1));
        }

        if (!names.isEmpty()) {
          extracted.add(new TableNameEntry(lineNr, names));
        }

        line = buf.readLine();
        lineNr++;
      }
    } catch (FileNotFoundException e) {
      LOGGER.error(e);
      fail("Could not find file \"" + script + "\"");
    } catch (IOException e) {
      LOGGER.error(e);
      fail("Could not read file \"" + script + "\"");
    } finally {
      IOUtils.closeQuietly(in);
      IOUtils.closeQuietly(buf);
    }

    return extracted;
  }

  private Set<String> readTableNames() {
    List<String> sqlStatements = SqlScriptExecutor.readSqlScript(this.getClass().getResourceAsStream(SCHEMA_EPORT_SQL));

    Set<String> tableNames = Sets.newHashSet();
    for (String statement : sqlStatements) {
      Matcher m = CREATE_TABLE_NAME_PATTERN.matcher(statement);
      while (m.find()) {
        if (!IGNORED_TABLES.contains(m.group(1).toLowerCase())) {
          tableNames.add(m.group(1));
        }
      }
    }
    return tableNames;
  }

  private Set<File> getMySQLMigrationScripts(File rootDir) {
    Set<File> scriptFiles = Sets.newHashSet();
    File[] content = rootDir.listFiles();
    for (File file : content) {
      if (file.isDirectory()) {
        scriptFiles.addAll(getMySQLMigrationScripts(file));
      }
      else {
        Matcher m = MYSQL_SCRIPT_FILENAME_PATTERN.matcher(file.getName());
        if (m.matches() && !IGNORED_SCRIPTS.contains(file.getName().toLowerCase())) {
          scriptFiles.add(file);
        }
      }
    }

    return scriptFiles;
  }

  private Pattern createTableNamesPattern(Set<String> tableNames) {
    String patternString = "(?<=^|[` ,\\(\\)])(" + Joiner.on('|').join(tableNames) + ")(?=[` ,\\(\\)\\.;]|$)";
    return Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
  }

  private static class TableNameEntry {
    private final int          lineNr;
    private final List<String> tableNames;

    public TableNameEntry(int lineNr, List<String> tableNames) {
      this.lineNr = lineNr;
      this.tableNames = tableNames;
    }
  }
}
