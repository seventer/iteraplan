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
package de.iteratec.iteraplan.businesslogic.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.common.util.StringUtil;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;

/**
 * A {@link CsvExportService} implementation to export the {@link InformationSystemRelease}s in CSV format.
 * 
 */
public class CsvExportServiceImpl implements CsvExportService {

  private static final Logger    LOGGER = Logger.getIteraplanLogger(CsvExportServiceImpl.class);

  private final AttributeTypeDAO attributeTypeDAO;

  /**
   * Creates an instance of this class with the required services.
   * 
   * @param attributeTypeDAO the attribute type dao for getting the attributes
   */
  public CsvExportServiceImpl(AttributeTypeDAO attributeTypeDAO) {
    this.attributeTypeDAO = attributeTypeDAO;
  }

  /** {@inheritDoc} */
  public String createCsvExport(List<InformationSystemRelease> isReleases) {
    List<AttributeType> attributes = getAttributeTypesToLog();
    IteraplanProperties properties = IteraplanProperties.getProperties();
    boolean exportDescription = properties.propertyIsSetToTrue(IteraplanProperties.PROP_CSV_EXPORT_DESCRIPTION);
    boolean exportTimespan = properties.propertyIsSetToTrue(IteraplanProperties.PROP_CSV_EXPORT_TIMESPAN);
    boolean exportStatus = properties.propertyIsSetToTrue(IteraplanProperties.PROP_CSV_EXPORT_STATUS);
    String sep = properties.getProperty(IteraplanProperties.CSV_SEPARATOR);
    String sepRep = properties.getProperty(IteraplanProperties.CSV_SEPARATOR_REPLACEMENT);
    String sepSub = properties.getProperty(IteraplanProperties.CSV_SEPARATOR_SUB);
    StringBuffer csv = new StringBuffer();
    addHeader(csv, exportDescription, exportTimespan, exportStatus, attributes, sep);
    
    for (InformationSystemRelease ipur : isReleases) {
      addIpureleaseToCsv(ipur, csv, exportDescription, exportTimespan, exportStatus, attributes, sep, sepRep, sepSub);
    }
    
    return csv.toString();
  }

  private void addHeader(StringBuffer csv, boolean exportDescription, boolean exportTimespan, boolean exportStatus, List<AttributeType> attributes,
                         String separator) {
    Locale locale = UserContext.getCurrentLocale();
    csv.append(MessageAccess.getStringOrNull("csv.fullName", locale));
    csv.append(separator);
    csv.append(MessageAccess.getStringOrNull("csv.ipuId", locale));
    csv.append(separator);
    csv.append(MessageAccess.getStringOrNull("csv.ipuName", locale));
    csv.append(separator);
    csv.append(MessageAccess.getStringOrNull("csv.relId", locale));
    csv.append(separator);
    csv.append(MessageAccess.getStringOrNull("csv.relName", locale));
    csv.append(separator);
    csv.append(MessageAccess.getStringOrNull("csv.elementOfIpuId", locale));
    csv.append(separator);
    csv.append(MessageAccess.getStringOrNull("csv.elementOfIpuName", locale));
    csv.append(separator);
    csv.append(MessageAccess.getStringOrNull("csv.elementOfRelId", locale));
    csv.append(separator);
    csv.append(MessageAccess.getStringOrNull("csv.elementOfRelName", locale));
    csv.append(separator);
    if (exportDescription) {
      csv.append(MessageAccess.getStringOrNull("csv.description", locale));
      csv.append(separator);
    }
    if (exportTimespan) {
      csv.append(MessageAccess.getStringOrNull("csv.from", locale));
      csv.append(separator);
      csv.append(MessageAccess.getStringOrNull("csv.to", locale));
      csv.append(separator);
    }
    if (exportStatus) {
      csv.append(MessageAccess.getStringOrNull("csv.status", locale));
      csv.append(separator);
    }
    for (AttributeType attrType : attributes) {
      csv.append(attrType.getName());
      csv.append(separator);
    }
    csv.append('\n');
  }

  private void addIpureleaseToCsv(InformationSystemRelease ipur, StringBuffer csv, boolean exportDescription, boolean exportTimespan,
                                  boolean exportStatus, List<AttributeType> attributes, String separator, String separatorRep, String separatorSub) {
    csv.append(makeCsvConform(ipur.getHierarchicalName(), separator, separatorRep));
    csv.append(separator);
    csv.append(ipur.getInformationSystem().getId().toString());
    csv.append(separator);
    csv.append(makeCsvConform(ipur.getInformationSystem().getName(), separator, separatorRep));
    csv.append(separator);
    csv.append(ipur.getId().toString());
    csv.append(separator);
    csv.append(makeCsvConform(ipur.getVersion(), separator, separatorRep));
    csv.append(separator);

    if (ipur.getParent() != null) {
      csv.append(getParentCsvString(ipur, separator, separatorRep));
    }
    else {
      csv.append(separator);
      csv.append(separator);
      csv.append(separator);
      csv.append(separator);
    }
    if (exportDescription) {
      csv.append(makeCsvConform(ipur.getDescription(), separator, separatorRep));
      csv.append(separator);
    }
    if (exportTimespan) {
      csv.append(getTimespanCsvString(ipur, separator));
    }
    if (exportStatus) {
      csv.append(MessageAccess.getStringOrNull(ipur.getTypeOfStatusAsString(), UserContext.getCurrentLocale()));
      csv.append(separator);
    }

    HashBucketMap<AttributeType, AttributeValue> attrMap = ipur.getAttributeTypeToAttributeValues();
    Locale locale = UserContext.getCurrentLocale();
    for (AttributeType attrType : attributes) {
      csv.append(getAttributeValuesCsvString(separator, separatorRep, separatorSub, locale, attrMap.get(attrType)));
      csv.append(separator);
    }
    csv.append('\n');
  }

  private String getAttributeValuesCsvString(String separator, String separatorRep, String separatorSub, Locale locale, List<AttributeValue> attrVals) {
    StringBuffer csv = new StringBuffer();

    if (attrVals != null) {
      for (Iterator<AttributeValue> it = attrVals.iterator(); it.hasNext();) {
        AttributeValue attrval = it.next();
        String attrvalString = attrval.getLocalizedValueString(locale);
        csv.append(makeCsvConform(attrvalString, separator, separatorRep));
        if (it.hasNext()) {
          csv.append(separatorSub);
        }
      }
    }

    return csv.toString();
  }

  private String getTimespanCsvString(InformationSystemRelease ipur, String separator) {
    StringBuffer csv = new StringBuffer();
    String from = DateUtils.formatAsString(ipur.runtimeStartsAt(), UserContext.getCurrentLocale());
    if (from != null) {
      csv.append(from);
    }
    csv.append(separator);
    String to = DateUtils.formatAsString(ipur.runtimeEndsAt(), UserContext.getCurrentLocale());
    if (to != null) {
      csv.append(to);
    }
    csv.append(separator);

    return csv.toString();
  }

  private String getParentCsvString(InformationSystemRelease ipur, String separator, String separatorRep) {
    StringBuffer csv = new StringBuffer();
    csv.append(ipur.getParent().getInformationSystem().getId().toString());
    csv.append(separator);
    csv.append(makeCsvConform(ipur.getParent().getInformationSystem().getName(), separator, separatorRep));
    csv.append(separator);
    csv.append(ipur.getParent().getId().toString());
    csv.append(separator);
    csv.append(makeCsvConform(ipur.getParent().getVersion(), separator, separatorRep));
    csv.append(separator);

    return csv.toString();
  }

  private String makeCsvConform(String string, String separator, String separatorRep) {
    String myString = StringUtil.removeNewLines(string);
    myString = myString.replace(separator.charAt(0), separatorRep.charAt(0));
    return myString;
  }

  private List<AttributeType> getAttributeTypesToLog() {
    IteraplanProperties properties = IteraplanProperties.getProperties();
    Collection<Object> keys = properties.getAllPropertyKeys();
    // extract property keys that define the attributes to log.
    List<String> attrKeys = new ArrayList<String>();
    for (Object obj : keys) {
      String key = (String) obj;
      if (key.startsWith(IteraplanProperties.PREFIX_CSV_ATTR)) {
        attrKeys.add(key);
      }
    }
    Collections.sort(attrKeys);
    // load all attribute types which are currently assigned to the
    Set<AttributeType> activatedAttrlist = new HashSet<AttributeType>(attributeTypeDAO.getAttributeTypesForTypeOfBuildingBlock(
        TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, true));
    // load the attribute types with the names.
    List<AttributeType> attrs = new ArrayList<AttributeType>();
    for (String attrKey : attrKeys) {
      String name = properties.getProperty(attrKey);
      AttributeType attr = attributeTypeDAO.getAttributeTypeByName(name);
      if (attr == null) {
        LOGGER.warn("User defined attribute '" + name
            + "', which is defined for CSV export could not be found in the database. Please update the iteraplan.properties.");
      }
      else if (!activatedAttrlist.contains(attr)) {
        LOGGER.warn("User defined attribute '" + name
            + "', which is defined for CSV export is either not assigned to information systems or the currently logged in user '"
            + UserContext.getCurrentUserContext().getLoginName()
            + "' has no read permission for that attribute. Please update the iteraplan.properties.");
      }
      else {
        attrs.add(attr);
      }
    }
    // load the attribute types with the names.
    return attrs;
  }

}
