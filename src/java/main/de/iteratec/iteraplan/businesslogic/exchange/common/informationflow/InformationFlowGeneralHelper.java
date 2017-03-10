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
package de.iteratec.iteraplan.businesslogic.exchange.common.informationflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.IInformationFlowOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;


/**
 * Helper class for generating visio and svg information flow diagrams. A set of useful static
 * methods for the creation of information flow diagrams are bundled here.
 * 
 * @author est
 */
public final class InformationFlowGeneralHelper {
  /**
   * Static class must not be instantiated.
   */
  private InformationFlowGeneralHelper() {
    // Empty constructor
  }

  public static List<String> getReferencedTcReleaseNames(InformationSystemInterface connection) {

    List<TechnicalComponentRelease> tcReleases = new ArrayList<TechnicalComponentRelease>();
    tcReleases.addAll(connection.getTechnicalComponentReleases());

    List<String> referencedTcrs = new ArrayList<String>();

    if (tcReleases.size() > 0) {
      for (TechnicalComponentRelease tcr : tcReleases) {
        if (StringUtils.isNotEmpty(tcr.getNonHierarchicalName())) {
          referencedTcrs.add(tcr.getNonHierarchicalName());
        }
      }
    }
    return referencedTcrs;
  }

  /**
   * retrieves the caption for the legend
   * 
   * @param lineCaptionSelected
   *          which type of element / attribute to be used for the caption
   * @param lineCaptionAttributeId
   *          the id of the attribute
   * @param informationFlowOptions
   *          the info flow options bean
   * @param locale
   *          the currently used locale
   * @return See method description.
   */
  public static String getDescriptionTypeName(AttributeTypeService attributeTypeService, int[] lineCaptionSelected, Integer lineCaptionAttributeId,
                                              IInformationFlowOptions informationFlowOptions, Locale locale) {

    List<String> descriptionTypeNames = Lists.newArrayList();

    for (int lineCaption : lineCaptionSelected) {
      switch (lineCaption) {

        case InformationFlowOptionsBean.LINE_DESCR_ATTRIBUTES:
          if (GraphicalExportBaseOptions.NOTHING_SELECTED != lineCaptionAttributeId.intValue()) {
            AttributeType attrType = attributeTypeService.loadObjectById(lineCaptionAttributeId);
            descriptionTypeNames.add(attrType.getName());
          }
          else {
            descriptionTypeNames.add("n.a.");
          }

          break;

        case InformationFlowOptionsBean.LINE_DESCR_TECHNICAL_COMPONENTS:
          descriptionTypeNames.add(MessageAccess.getStringOrNull(Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL, locale));
          break;

        case InformationFlowOptionsBean.LINE_DESCR_DESCRIPTION:
          descriptionTypeNames.add(MessageAccess.getStringOrNull(Constants.ATTRIBUTE_DESCRIPTION, locale));
          break;

        case InformationFlowOptionsBean.LINE_DESCR_NAME:
          descriptionTypeNames.add(MessageAccess.getStringOrNull(Constants.ATTRIBUTE_NAME, locale));
          break;

        default: // Business Objects are displayed at the end
      }
    }
    if (ArrayUtils.contains(lineCaptionSelected, InformationFlowOptionsBean.LINE_DESCR_BUSINESS_OBJECTS)) {
      descriptionTypeNames.add(MessageAccess.getStringOrNull(Constants.BB_BUSINESSOBJECT_PLURAL, locale));
    }
    return StringUtils.join(descriptionTypeNames, "; ");
  }

  /**
   * Retrieves the label for a selected attribute
   * 
   * @param connection
   *          the edge (information system interface)
   * @param lineCaptionAttributeId
   *          the id of the selected attribute
   * @return A textual description of the selected attribute to server as edge label (caption)
   */
  public static List<String> getLabelDescrForAttribute(AttributeTypeService attributeTypeService, InformationSystemInterface connection,
                                                       Integer lineCaptionAttributeId) {
    List<String> resultValues = new ArrayList<String>();

    // exclude case that nothing has been selected (value of -1)
    if (GraphicalExportBaseOptions.NOTHING_SELECTED != lineCaptionAttributeId.intValue()) {
      AttributeType attrType = attributeTypeService.loadObjectById(lineCaptionAttributeId);

      HashBucketMap<AttributeType, AttributeValue> allAssignmentsMap = connection.getAttributeTypeToAttributeValues();

      List<AttributeValue> allValues = allAssignmentsMap.get(attrType);

      if (allValues != null) {
        for (AttributeValue val : allValues) {
          resultValues.add(val.getLocalizedValueString(UserContext.getCurrentLocale()));
        }
      }

    }
    return resultValues;
  }

  public static String headerAttributeOrBuilding(Locale locale) {
    return MessageAccess.getStringOrNull("global.attribute", locale) + " / " + MessageAccess.getStringOrNull("global.bb", locale);
  }

  public static String replaceBlank(String fieldValueFromDimension) {
    return StringUtils.isBlank(fieldValueFromDimension) ? "n.a." : fieldValueFromDimension;

  }

}
