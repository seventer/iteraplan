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
package de.iteratec.iteraplan.common;

import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.collections.BBTLocalizedNameFunction;
import de.iteratec.iteraplan.common.collections.EntityToIdFunction;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.model.AbstractAssociation;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.interfaces.IdEntity;


/**
 * Contains static methods that are used all over iteraplan.
 */
public final class GeneralHelper {

  private static final String  NULL_STRING                = "null";

  public static final Date     MIN_DATE                   = new GregorianCalendar(1000, 0, 1).getTime();
  public static final Date     MAX_DATE                   = new GregorianCalendar(9999, 11, 31).getTime();

  //following expression should resolve to "\\s*(.+?)\\s*(#\\s*(.*?)\\s*)?"
  // meaning: search for two relevant groups in the name string, 1st and 3rd pair of parentheses
  // name may start and end with whitespace, which is not taken into the group
  // building block name can be any character (.+?), up to hashmark
  // after the hashmark, some whitespace may appear, and everything after that (except trailing
  // whitespace)
  // is taken into the second group (surrounded by parentheses)
  // starting and including the hashmark, everything is optional for a match
  private static final String  NAME_AND_VERSION_REGEX_STR = "\\s*(.+?)\\s*(" + Constants.VERSIONSEP.trim() + "\\s*(.*?)\\s*)?";
  private static final Pattern NAME_AND_VERSION_REGEX     = Pattern.compile(NAME_AND_VERSION_REGEX_STR);

  private GeneralHelper() {
    // No public constructor.
  }

  /**
   * Returns an integer array containing the IDs of the given list of {@link IdEntity}s. 
   * 
   * @param list 
   *    A list of {@link IdEntity}s.
   *    
   * @return 
   *    An integer array containing the IDs of the given list.
   */
  public static Integer[] createIdArrayFromIdEntities(Collection<? extends IdEntity> list) {
    Integer[] ids = new Integer[list.size()];
    int index = 0;
    for (IdEntity entity : list) {
      ids[index++] = entity.getId();
    }

    return ids;
  }

  /**
   * Returns a set of integers containing the IDs of the given collection of {@link IdEntity}s.
   * 
   * @param col 
   *    A set of {@link IdEntity}s.
   * 
   * @return 
   *    A set of integers containing the IDs of the given collection. If the collection is 
   *    {@code null} or empty, an empty set is returned.
   */
  public static Set<Integer> createIdSetFromIdEntities(Collection<? extends IdEntity> col) {
    if (col == null || col.isEmpty()) {
      return Sets.newHashSet();
    }

    EntityToIdFunction<IdEntity, Integer> entityToIdFunction = new EntityToIdFunction<IdEntity, Integer>();
    Iterable<Integer> ids = Iterables.transform(col, entityToIdFunction);

    return Sets.newHashSet(ids);
  }

  /**
   * Returns a string made up from the given list of string separated by the given separator string.
   * If either the list or the separator is {@code null}, an empty string is returned.
   * 
   * @param list 
   *    A list of strings.
   * @param separator 
   *    The separation string.
   *    
   * @return 
   *    The elements of the list concatenated and separated by the separator string or an empty
   *    string if either the list or the separator is {@code null}.
   */
  public static String makeConcatenatedStringWithSeparator(List<String> list, String separator) {
    if (list == null || separator == null) {
      return StringUtils.EMPTY;
    }

    return Joiner.on(separator).useForNull(NULL_STRING).join(list);
  }

  /**
   * Returns a String made from the concatenation of value strings of the given Collection of Attribute Values,
   * separated by the standard {@link Constants#BUILDINGBLOCKSEP}.
   * @param avs
   *          Collection of Attribute Values
   * @return Concatenated value strings of the Attribute Values
   */
  public static String makeConcatenatedNameStringForAvCollection(Collection<? extends AttributeValue> avs) {
    if (avs == null || avs.isEmpty()) {
      return " ";
    }

    Function<AttributeValue, String> toNameFuntion = new Function<AttributeValue, String>() {
      public String apply(AttributeValue av) {
        if (av == null) {
          return "null";
        }
        return av.getValueString();
      }
    };
    return Joiner.on(Constants.BUILDINGBLOCKSEP).join(Iterables.transform(avs, toNameFuntion));
  }

  /**
   * Returns a String made from the concatenation of names of the given Collection of Building Blocks,
   * separated by the standard {@link Constants#BUILDINGBLOCKSEP}.
   * @param bbs
   *          Collection of Building Blocks
   * @return Concatenated names of the Building Blocks
   */
  public static String makeConcatenatedNameStringForBbCollection(Collection<? extends BuildingBlock> bbs) {
    if (bbs == null || bbs.isEmpty()) {
      return " ";
    }

    Function<BuildingBlock, String> toNameFuntion = new Function<BuildingBlock, String>() {
      public String apply(BuildingBlock bb) {
        return bb.getNonHierarchicalName();
      }
    };
    return Joiner.on(Constants.BUILDINGBLOCKSEP).join(Iterables.transform(bbs, toNameFuntion));
  }

  /**
   * Returns a String made from the concatenation of names of the given Collection of Building Block Types,
   * separated by the standard {@link Constants#BUILDINGBLOCKSEP}.
   * @param bbts
   *          Collection of Building Block Types
   * @return Concatenated names of the Building Block Types
   */
  public static String makeConcatenatedNameStringForBbtCollection(Collection<BuildingBlockType> bbts) {
    if (bbts == null || bbts.isEmpty()) {
      return " ";
    }

    return Joiner.on(Constants.BUILDINGBLOCKSEP).join(Iterables.transform(bbts, new BBTLocalizedNameFunction()));
  }

  /**
   * Returns a String made from the concatenation of names of the given Collection of Associations/BuildingBlocks,
   * separated by the standard {@link Constants#BUILDINGBLOCKSEP}.
   * 
   * @param aas
   *        Collection of Associations
   * @param isLeftEndSearchedBB
   *        Decision to use the right or left end of association for the concatenation of names.<br/>
   *        true == the left end is used <br/>
   *        false == the right end is used<br/>
   * @param isReturnAttributes
   *        Decision to use Attributes in the concatenation of names.
   * @param showAttributeName
   *        Decides whether the attribute name is returned, if attributes are to be used.
   * @return Concatenated names of building blocks of the association.
   */
  public static String makeConcatenatedNameStringForAssociationCollection(Collection<? extends AbstractAssociation<? extends BuildingBlock, ? extends BuildingBlock>> aas,
                                                                          final boolean isLeftEndSearchedBB, final boolean isReturnAttributes,
                                                                          final boolean showAttributeName) {
    if (aas == null || aas.isEmpty()) {
      return " ";
    }

    Function<AbstractAssociation<? extends BuildingBlock, ? extends BuildingBlock>, String> toNameFunction = new Function<AbstractAssociation<? extends BuildingBlock, ? extends BuildingBlock>, String>() {
      public String apply(AbstractAssociation<? extends BuildingBlock, ? extends BuildingBlock> aa) {

        String attributes = "";
        if (isReturnAttributes) {
          attributes = getAttributValuesForAssociation(aa, showAttributeName);
        }

        BuildingBlock buildingBlock;
        if (isLeftEndSearchedBB) {
          buildingBlock = aa.getLeftEnd();
        }
        else {
          buildingBlock = aa.getRightEnd();
        }

        return buildingBlock.getNonHierarchicalName() + attributes;
      }
    };

    return Joiner.on(Constants.BUILDINGBLOCKSEP).join(Iterables.transform(aas, toNameFunction));
  }

  /**
   * Returns a String made from the concatenation of the attribute names and values of the given Collection of associations,
   * separated by the standard {@link Constants#BUILDINGBLOCKSEP}.
   * 
   * @param assoc
   *    A Isr2BoAssociation Object
   *    
   * @param isAttributeName
   *    Decides whether the attribute name is returned.
   *    
   * @return Concatenated attributes names and values of the association.
   */
  public static String getAttributValuesForAssociation(AbstractAssociation<? extends BuildingBlock, ? extends BuildingBlock> assoc,
                                                       boolean isAttributeName) {
    StringBuilder attributes = new StringBuilder();

    HashBucketMap<AttributeType, AttributeValue> attributeMap = assoc.getAttributeTypeToAttributeValues();
    if (attributeMap.size() != 0) {
      Set<AttributeType> atSet = attributeMap.keySet();
      attributes.append(" (");

      for (AttributeType at : atSet) {
        if (isAttributeName) { // append the name of the attribute
          attributes.append(at.getName());
          attributes.append(": ");
        }
        List<AttributeValue> avList = attributeMap.get(at);

        for (AttributeValue attValue : avList) {
          attributes.append(attValue.getValue().toString()); // append attribute value
          attributes.append(Constants.BUILDINGBLOCKSEP); // append separator  
        }
      }

      attributes.delete(attributes.length() - 2, attributes.length());
      attributes.append(")");
    }
    return attributes.toString();
  }

  /**
   * Returns a concatenated string of the given name and version which are separated by the 
   * '#' separator string. If the given version is null or empty nothing is concatenated. 
   * 
   * @param name The name.
   * @param version  The version.
   * 
   * @return the release name
   */
  public static String makeReleaseName(String name, String version) {
    if (StringUtils.isNotEmpty(version)) {
      return Joiner.on(Constants.VERSIONSEP).useForNull(NULL_STRING).join(name, version);
    }
    else {
      return name;
    }
  }

  /**
   * Splits a TCR / ISR name into its name and version components. '#' is used as the separator
   * character, where it must be immediately preceded by at least one space.
   *
   * @param releaseName
   * @return a two-element String array. Element 0 always contains the name. Element 1 contains the
   *         version string, but may also be null
   * @throws IllegalArgumentException
   *           if the passed name does not match the expected format (which is very permissive
   *           actually)
   */
  public static String[] getPartsOfReleaseName(String releaseName) {
    Preconditions.checkNotNull(releaseName, "Name must never be null");
    if (StringUtils.isEmpty(releaseName)) {
      return new String[] { releaseName, null };
    }

    Matcher m = NAME_AND_VERSION_REGEX.matcher(releaseName);

    if (!m.matches()) {
      throw new IllegalArgumentException("The passed name does not conform to the expected syntax: " + releaseName);
    }

    return new String[] { m.group(1), m.group(3) };
  }

  /**
   * @param serverUrl 
   * @param savedQueryType 
   * @param id 
   * @return fast export URL
   */
  public static String createFastExportUrl(String serverUrl, String savedQueryType, Integer id) {
    return String.format("%s/fastexport/generateSavedQuery.do?id=%d&savedQueryType=%s&outputMode=attachment", serverUrl, id, savedQueryType);
  }

  /**
   * 
   * Filter BusinessObjects from a InformationSystemRelease Object by a given Map of Business Objects and a return the Association between Informations System and Business Object.
   * 
   * @param rel
   *        Information System Release
   * @param businessObjects
   *        Map of Business Objects
   * @param isLeftEndSearchedBB
   *        Decision to use the right or left end of association between IS and BO.<br/>
   *        true == the left end is used <br/>
   *        false == the right end is used<br/>
   * @return Association Object between BO and IS
   */
  public static Set<Isr2BoAssociation> filterAbstractAssociationsByBusinessObjects(InformationSystemRelease rel,
                                                                                   Map<String, BusinessObject> businessObjects,
                                                                                   boolean isLeftEndSearchedBB) {
    if (businessObjects == null || businessObjects.isEmpty()) {
      return rel.getBusinessObjectAssociations();
    }

    Set<Isr2BoAssociation> isr2BoAssociationSet = Sets.newHashSet();
    for (Iterator<Isr2BoAssociation> iter = rel.getBusinessObjectAssociations().iterator(); iter.hasNext();) {
      Isr2BoAssociation ass = iter.next();
      BuildingBlock buildingBlock;
      if (isLeftEndSearchedBB) {
        buildingBlock = ass.getLeftEnd();
      }
      else {
        buildingBlock = ass.getRightEnd();
      }

      if (businessObjects.get(buildingBlock.getNonHierarchicalName()) != null) {
        isr2BoAssociationSet.add(ass);
      }
    }
    return isr2BoAssociationSet;
  }

}