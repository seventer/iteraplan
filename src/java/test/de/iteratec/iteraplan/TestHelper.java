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
package de.iteratec.iteraplan;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.writer.IteraplanModelDiffWriter;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.writer.IteraplanModelDiffWriter.IteraplanDiffWriterResult;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.BaseDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffBuilder;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffBuilder.DiffMode;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffBuilderResult;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffPart;
import de.iteratec.iteraplan.elasticeam.model.compare.LeftSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.MatchResult;
import de.iteratec.iteraplan.elasticeam.model.compare.Matcher;
import de.iteratec.iteraplan.elasticeam.model.compare.RightSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.TwoSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.impl.DiffBuilderImpl;
import de.iteratec.iteraplan.elasticeam.model.compare.impl.MatcherImpl;
import de.iteratec.iteraplan.model.RuntimePeriod;


/**
 * This Class offers helper methods maybe needed in several test cases 
 *
 */
public final class TestHelper {

  private static final Logger LOGGER   = Logger.getIteraplanLogger(TestHelper.class);

  private static TestHelper   instance = new TestHelper();

  /**  
   * Default-Konstruktor, der nicht auﬂerhalb dieser Klasse
   * aufgerufen werden kann
   */
  private TestHelper() {
    //Default-Konstruktor
  }

  public static TestHelper getInstance() {
    return instance;
  }

  /**
   * Returns path to workspace test folder
   * Needed e.g. in case of finding TestQuery xml files in the test source folder
   * @return see description
   */
  public String getTestPath() {
    File f = new File("");
    //get path to workspace project
    String p = f.getAbsolutePath();
    //add relative path of test query file to absolute path
    return p + File.separator + "src" + File.separator + "java" + File.separator + "test";
  }

  /**
   * Provide possibility to change some of already set properties of the Iteraplan
   * Properties file
   * @param key key of property
   * @param value value of property
   */
  public void setIteraplanProperty(String key, String value) {

    IteraplanProperties props = IteraplanProperties.getProperties();
    try {
      //alter property of iteraplan properties
      Field property = props.getClass().getDeclaredField("bundle");
      property.setAccessible(true);
      Properties newProp = (Properties) property.get(props);
      newProp.setProperty(key, value);

    } catch (Exception e) {
      LOGGER.error(e);
    }
  }

  public RuntimePeriod getStandardRuntimePeriod(String pattern, String startStr, String endStr) throws ParseException {
    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(pattern);
    Date start = format.parse(startStr);
    Date end = format.parse(endStr);
    return new RuntimePeriod(start, end);
  }

  public static List<String> getBaseDiffInfoStrings(List<BaseDiff> diffs) {
    Map<UniversalTypeExpression, Set<BaseDiff>> added = Maps.newHashMap();
    Map<UniversalTypeExpression, Set<BaseDiff>> changed = Maps.newHashMap();
    Map<UniversalTypeExpression, Set<BaseDiff>> removed = Maps.newHashMap();

    for (BaseDiff diff : diffs) {
      if (diff instanceof RightSidedDiff) {
        putDiff(added, diff);
      }
      if (diff instanceof TwoSidedDiff) {
        putDiff(changed, diff);
      }
      if (diff instanceof LeftSidedDiff) {
        putDiff(removed, diff);
      }
    }

    List<String> infoMessages = Lists.newArrayList();
    for (Map.Entry<UniversalTypeExpression, Set<BaseDiff>> entry : added.entrySet()) {
      infoMessages.add(entry.getKey().getPersistentName() + "s added: " + entry.getValue().size());
    }
    for (Map.Entry<UniversalTypeExpression, Set<BaseDiff>> entry : changed.entrySet()) {
      infoMessages.add(entry.getKey().getPersistentName() + "s changed: " + entry.getValue().size());
    }
    for (Map.Entry<UniversalTypeExpression, Set<BaseDiff>> entry : removed.entrySet()) {
      infoMessages.add(entry.getKey().getPersistentName() + "s removed: " + entry.getValue().size());
    }
    if (infoMessages.isEmpty()) {
      infoMessages.add("No changes.");
    }
    return infoMessages;
  }

  private static void putDiff(Map<UniversalTypeExpression, Set<BaseDiff>> map, BaseDiff diff) {
    if (!map.containsKey(diff.getType())) {
      map.put(diff.getType(), new HashSet<BaseDiff>());
    }
    map.get(diff.getType()).add(diff);
  }

  /**
   * @param leftModel
   * @param rightModel
   * @return DiffBuildeResult
   */
  public static DiffBuilderResult getDiffBuilderResults(Metamodel metamodel, Model leftModel, Model rightModel) {
    Matcher modelMatcher = new MatcherImpl(metamodel, MatcherImpl.IDCOMPARATOR);
    MatchResult matchResult = modelMatcher.match(leftModel, rightModel);

    DiffBuilder diffBuilder = new DiffBuilderImpl(matchResult);
    diffBuilder.setMode(DiffMode.ADDITIVE);
    return diffBuilder.computeDifferences();
  }

  /**
   * @param diffResult
   */
  public static void printDiffBuilderResultDifferences(Metamodel metamodel, DiffBuilderResult diffResult) {
    for (UniversalTypeExpression type : metamodel.getUniversalTypes()) {
      System.out.println("======== " + type.getPersistentName() + " =======");
      for (BaseDiff bDiff : diffResult.getDiffsByType(type)) {

        if (LeftSidedDiff.class.isInstance(bDiff)) {
          System.out.println("LSD: " + ((LeftSidedDiff) bDiff).getExpression());
        }
        else if (RightSidedDiff.class.isInstance(bDiff)) {
          System.out.println("RSD: " + ((RightSidedDiff) bDiff).getExpression());
        }
        else {
          TwoSidedDiff tsd = (TwoSidedDiff) bDiff;
          System.out.println("TSD. Left side: " + tsd.getLeftExpression() + " ; Right side: " + tsd.getRightExpression());
          for (DiffPart part : tsd.getDiffParts()) {
            System.out.println("-- " + part.getFeature().getPersistentName() + " left: " + part.getLeftValue() + " ; right: " + part.getRightValue());
          }
        }
      }
    }
  }

  /**
   * @param diffResult
   * @param referenceMapping
   * @param instanceMapping
   * @param avService
   * @param bbServiceLocator
   */
  public static void writeChangesToDatabase(DiffBuilderResult diffResult, IteraplanMapping referenceMapping,
                                            BiMap<Object, UniversalModelExpression> instanceMapping, AttributeValueService avService,
                                            BuildingBlockServiceLocator bbServiceLocator) {
    IteraplanModelDiffWriter writer = new IteraplanModelDiffWriter(diffResult, referenceMapping, instanceMapping, avService, bbServiceLocator);

    IteraplanDiffWriterResult writerResult = writer.writeDifferences();

    List<String> infoMessages = getBaseDiffInfoStrings(writerResult.getAppliedDiffs());
    System.out.println("Following changes were applied:");
    System.out.println(infoMessages);
    System.out.println(writerResult.getWarnings());
    System.out.println(writerResult.getErrors());

  }

}
