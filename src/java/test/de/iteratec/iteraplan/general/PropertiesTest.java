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
package de.iteratec.iteraplan.general;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.common.Logger;


public class PropertiesTest {

  private static final Logger       LOGGER                          = Logger.getIteraplanLogger(PropertiesTest.class);

  private static final String       EQUALs                          = "=";

  private static final String       GERMAN_PROPERTY_FILE            = "/de/iteratec/iteraplan/presentation/resources/ApplicationResources_de.properties";
  private static final String       ENGLISH_PROPERTY_FILE           = "/de/iteratec/iteraplan/presentation/resources/ApplicationResources.properties";
  private static final String       SPANISH_PROPERTY_FILE           = "/de/iteratec/iteraplan/presentation/resources/ApplicationResources_es.properties";
  private static final String       FRENCH_PROPERTY_FILE            = "/de/iteratec/iteraplan/presentation/resources/ApplicationResources_fr.properties";
  private static final String       BULGARIAN_PROPERTY_FILE         = "/de/iteratec/iteraplan/presentation/resources/ApplicationResources_bg.properties";
  private static final String       HUNGARIAN_PROPERTY_FILE         = "/de/iteratec/iteraplan/presentation/resources/ApplicationResources_hu.properties";
  private static final String       SWEDISH_PROPERTY_FILE           = "/de/iteratec/iteraplan/presentation/resources/ApplicationResources_sv.properties";
  private static final String       GERMAN                          = "german";
  private static final String       ENGLISH                         = "english";
  private static final String       SPANISH                         = "spanish";
  private static final String       FRENCH                          = "french";
  private static final String       BULGARIAN                       = "bulgarian";
  private static final String       HUNGARIAN                       = "hungarian";
  private static final String       SWEDISH                         = "swedish";
  private static final int          POSITION_OF_PROPERTY_IN_JSP_TAG = 3;

  private static final String[]     ACCEPTABLE_MISSES               = { "build.id", "build.version", "audit.logging.enabled",
      "lastmodification.logging.enabled", "searchresults.option.1", "searchresults.option.2", "searchresults.option.3", "searchresults.default.count" };
  private static final List<String> ACCEPTABLE_MISSES_LIST          = Arrays.asList(ACCEPTABLE_MISSES);

  private static List<LanguageFile> languageFiles                   = new ArrayList<LanguageFile>();

  @Before
  public void initLanguageSets() throws IOException {
    languageFiles.add(new LanguageFile(GERMAN, GERMAN_PROPERTY_FILE));
    languageFiles.add(new LanguageFile(ENGLISH, ENGLISH_PROPERTY_FILE));
    languageFiles.add(new LanguageFile(SPANISH, SPANISH_PROPERTY_FILE));
    languageFiles.add(new LanguageFile(FRENCH, FRENCH_PROPERTY_FILE));
    languageFiles.add(new LanguageFile(BULGARIAN, BULGARIAN_PROPERTY_FILE));
    languageFiles.add(new LanguageFile(HUNGARIAN, HUNGARIAN_PROPERTY_FILE));
    languageFiles.add(new LanguageFile(SWEDISH, SWEDISH_PROPERTY_FILE));
  }

  static class LanguageFile {
    private String              language;
    private Map<Object, Object> properties;
    private String              path;

    public LanguageFile(String language, String path) throws IOException {
      this.language = language;
      this.path = path;
      this.properties = getProperties(path);
    }

    public LanguageFile(LanguageFile l) {
      this.language = l.language;
      this.properties = new Hashtable<Object, Object>(l.properties);
    }

    Set<Object> keySet() {
      return properties.keySet();
    }

    Set<Entry<Object, Object>> entrySet() {
      return properties.entrySet();
    }

    public String getPath() {
      return path;
    }
  }

  private static Properties getProperties(String resource) throws IOException {
    Properties properties = new Properties();
    InputStream resourceAsStream = null;
    InputStreamReader reader = null;
    try {
      resourceAsStream = PropertiesTest.class.getResourceAsStream(resource);
      if (resourceAsStream == null) {
        resourceAsStream = new FileInputStream(new File(resource));
      }
      reader = new InputStreamReader(resourceAsStream, "UTF-8");
      properties.load(reader);
    } finally {
      IOUtils.closeQuietly(resourceAsStream);
      IOUtils.closeQuietly(reader);
    }
    return properties;
  }

  /**
   * Looks at all JSPs and extracts the bundle keys defined in fmt:message or bean(-el):message
   * tags. Then checks if these are defined in the language bundle files. Finally all bundle keys
   * are printed, which are contained in the german or the english bundle files, but could not be
   * found in the JSPs (and are thus possibly obsolete).
   * 
   * @throws Exception
   */
  @Test
  public void testJspKeys() throws Exception {

    Set<String> collectedBundleKeys = new HashSet<String>();
    Pattern p = Pattern.compile("(bean(-el)|fmt)?:message[^>]+key=\"([^\"]+)\"");
    File rootDirectory = new File("WebContent/jsp/");

    boolean success = parseJspForBundleKeys(collectedBundleKeys, p, rootDirectory);

    for (LanguageFile l : languageFiles) {
      LOGGER.info("===============Bundle keys in {0} ApplicationResources.properties which were not found in JSPs: ====================", l.language);
      logDifferences(l.keySet(), collectedBundleKeys);
    }

    assertTrue(
        "At least one language file is missing a message key referenced by a JSP. Check the console output for details (You may need to increase the log level to INFO first!)",
        success);
  }

  /**
   * @see {@link #testJspKeys()}
   * @param collectedBundleKeys
   *          Bundle keys collected so far. Acts as in-out parameter.
   * @param p
   *          The pattern to find.
   * @param fileToProcess
   *          The file to be looked at.
   * @return true if a bundle key was missing.
   * @throws FileNotFoundException
   */
  private boolean parseJspForBundleKeys(Set<String> collectedBundleKeys, Pattern p, File fileToProcess) throws FileNotFoundException {
    boolean wasSuccessful = true;

    if (fileToProcess.isDirectory()) {
      for (File f : fileToProcess.listFiles()) {
        boolean containedSuccess = parseJspForBundleKeys(collectedBundleKeys, p, f);
        if (!containedSuccess) {
          wasSuccessful = false;
        }
      }
    }
    else if (fileToProcess.getName().toLowerCase().endsWith("jsp")) {
      Scanner sc = new Scanner(fileToProcess);
      StringBuffer jspAsStringBuffer = new StringBuffer();
      while (sc.hasNextLine()) {
        jspAsStringBuffer.append(sc.nextLine());
      }
      String jspAsString = jspAsStringBuffer.toString();
      MatchResult result = null;
      sc = new Scanner(jspAsString);
      while (true) {
        if (sc.findInLine(p) == null) {
          break;
        }
        result = sc.match();
        String bundleKey = result.group(POSITION_OF_PROPERTY_IN_JSP_TAG); // refers to regexp which is passed into this method
        collectedBundleKeys.add(bundleKey);

        // omit registered keys and keys which contain variables
        if (!ACCEPTABLE_MISSES_LIST.contains(bundleKey) && !(bundleKey.contains("$"))) {
          for (LanguageFile l : languageFiles) {
            if (!l.keySet().contains(bundleKey)) {
              wasSuccessful = false;
              LOGGER.info("Bundle key {0} defined in JSP {1} was not found in {2} ApplicationResources.properties!", bundleKey, fileToProcess,
                  l.language);
            }
          }
        }
      }
    }

    return wasSuccessful;
  }

  @Test
  public void testApplicationResources() {
    boolean failureOccured = false;
    for (LanguageFile l : languageFiles) {
      for (LanguageFile k : languageFiles) {
        if (!l.keySet().equals(k.keySet())) {
          LOGGER.warn("===============Missing in {0} ApplicationResources.properties (present in {1}): ===============", k.language, l.language);
          logDifferencesExtended(l.keySet(), k.keySet(), l, k);
          failureOccured = true;
        }
      }
    }
    assertFalse("At least two property files have different property sets.", failureOccured);
  }

  private void logDifferences(Set<?> first, Set<?> second) {
    Set<Object> firstCopy = new HashSet<Object>(first);
    firstCopy.removeAll(second);
    LOGGER.info(firstCopy.toString());
  }

  private void logDifferencesExtended(Set<?> first, Set<?> second, LanguageFile l, LanguageFile k) {
    Set<Object> firstCopy = new HashSet<Object>(first);
    firstCopy.removeAll(second);
    for (Object key : firstCopy) {
      LOGGER.info("Key: {0}", key);
      if (l != null) {
        LOGGER.warn("{0}: {1}", l.language, l.properties.get(key));
      }
      if (k != null) {
        LOGGER.warn("{0}: {1}", k.language, k.properties.get(key));
      }
    }
  }

  @Test
  public void testDuplicateValues() {
    for (LanguageFile l : languageFiles) {
      logDuplicateValues(l.language, getDuplicateValues(l));
    }
  }

  private Map<String, List<String>> getDuplicateValues(LanguageFile l) {
    Map<String, List<String>> res = new HashMap<String, List<String>>();

    // Map all keys by their values
    for (Entry<Object, Object> entry : l.entrySet()) {
      String key = (String) entry.getKey();
      String value = (String) entry.getValue();
      if (!res.containsKey(value)) {
        List<String> list = new ArrayList<String>();
        list.add(key);
        res.put(value, list);
      }
      else {
        res.get(value).add(key);
      }
    }

    // Delete all values with only one key
    Iterator<Entry<String, List<String>>> it = res.entrySet().iterator();
    while (it.hasNext()) {
      Entry<String, List<String>> entry = it.next();
      if (entry.getValue().size() <= 1) {
        it.remove();
      }
    }

    return res;
  }

  private void logDuplicateValues(String language, Map<String, List<String>> duplicatedValues) {
    if (!duplicatedValues.isEmpty()) {
      LOGGER.info("===============Duplicate values {0} ApplicationResources.properties: ====================", language);

      for (Entry<String, List<String>> entry : duplicatedValues.entrySet()) {
        LOGGER.info("Value: {0}", entry.getKey());
        for (String key : entry.getValue()) {
          LOGGER.info("\t Key: {0}", key);
        }
      }
    }
  }

  /**
   * Tests if double quotes are present in the applicationresources
   */
  @Test
  public void testDoubleQuotes() {
    for (LanguageFile l : languageFiles) {
      for (Entry<Object, Object> entry : l.entrySet()) {
        String value = (String) entry.getValue();
        if (value.contains("\"")) {
          fail("Property " + entry.getKey() + " contains a doublequote");
        }
      }
    }
  }

  /**
   * tests all .properties files in de/iteratec/iteraplan/presentation/resources for duplicate keys.
   * 
   */
  @Test
  public void testDuplicateKeys() {

    HashSet<String> errorMessages = new HashSet<String>();

    for (LanguageFile lf : languageFiles) {
      errorMessages.addAll(checkFileForDuplicateKeys(lf.getPath()));
      if (!errorMessages.isEmpty()) {
        fail(StringUtils.join(errorMessages, "\n"));
      }
    }
  }

  public Set<String> checkFileForDuplicateKeys(String path) {
    HashSet<String> errorMessages = new HashSet<String>();

    InputStream resourceAsStream = PropertiesTest.class.getResourceAsStream(path);
    if (resourceAsStream == null) {
      try {
        resourceAsStream = new FileInputStream(new File(path));
      } catch (FileNotFoundException fnfe) {
        errorMessages.add("File not found: " + path);
      }
    }

    BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
    HashSet<String> keys = new HashSet<String>();
    try {
      String strLine = br.readLine();
      while (strLine != null && strLine.length() > 0) {
        if (strLine.contains(EQUALs)) {
          String[] keyValuePair = strLine.split(EQUALs);
          String key = keyValuePair[0];
          if (keys.contains(key)) {
            errorMessages.add("Duplicate key " + key + " detected in " + path);
          }
          else {
            keys.add(key);
          }
        }
        else if (!strLine.isEmpty() && strLine.charAt(0) != '#') {
          System.out.println("WARNING: found a probably malformatted line in " + path);
        }
        strLine = br.readLine();
      }
      br.close();
    } catch (IOException ex) {
      fail("IO Exception in file " + path);
    }
    return errorMessages;
  }
}