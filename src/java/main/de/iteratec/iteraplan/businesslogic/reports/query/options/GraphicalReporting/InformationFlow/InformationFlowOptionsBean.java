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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportBeanBase;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.ExportOption;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;


public class InformationFlowOptionsBean extends GraphicalExportBaseOptions implements Serializable, IInformationFlowOptions {

  /** Name shown on the UI for "no layout template selected" */
  public static final String                 DUMMY_TEMPLATE_NAME             = "< >";

  /** Serialization version. */
  private static final long                  serialVersionUID                = -5092924316959944523L;
  public static final int                    LINE_DESCR_ATTRIBUTES           = 0;
  public static final int                    LINE_DESCR_BUSINESS_OBJECTS     = 1;
  public static final int                    LINE_DESCR_TECHNICAL_COMPONENTS = 2;
  public static final int                    LINE_DESCR_DESCRIPTION          = 3;
  public static final int                    LINE_DESCR_NAME                 = 4;

  public static final String                 INTERFACE_QUERY                 = "interfaceQuery";
  public static final String                 BUSINESSOBJECT_QUERY            = "businessObejctQuery";

  /** The {@link de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult QueryResult}s this Diagram supports */
  private static final ImmutableList<String> DIAGRAM_QUERIES                 = ImmutableList.of(ManageReportBeanBase.MAIN_QUERY, INTERFACE_QUERY,
                                                                                 BUSINESSOBJECT_QUERY);

  /**
   * determines whether building blocks or attributes should be taken. Business Objects are the
   * default value here.
   */
  private int[]                              selectionType                   = { LINE_DESCR_BUSINESS_OBJECTS };

  private String                             selectedNodeLayout              = Constants.REPORTS_EXPORT_INFORMATIONFLOW_LAYOUT_STANDARD;
  private List<ExportOption>                 availableNodeLayouts            = getInformationFlowLayoutAlgorithms();

  private List<BBAttribute>                  isiAttributes                   = new ArrayList<BBAttribute>();
  private List<Integer>                      lineCaptionAttributeIds         = new ArrayList<Integer>();
  private List<String>                       lineCaptionAttributeNames       = new ArrayList<String>();

  /** A list containing all relevant interfaces (selected by filter and both isr ends where selected). */
  private List<InformationSystemInterface>   relevantInterfaces              = null;

  /** A list containing all relevant Business Objects */
  private List<BusinessObject>               relevantBusinessObjects         = null;

  /**
   * The Id of the attribute for line caption.
   */
  private Integer                            lineCaptionSelectedAttributeId  = Integer.valueOf(-1);

  private boolean                            showIsBusinessObjects           = true;
  private boolean                            showIsBaseComponents            = true;

  /** Name of the (optional) layout template to use for this diagram */
  private String                             selectedTemplateName;
  /** Holds the available template names for the currently selected result format */
  private List<ExportOption>                 availableLayoutTemplates        = Lists.newArrayList();
  private File                               selectedTemplateFile;

  public InformationFlowOptionsBean() {
    super();
    getColorOptionsBean().setAvailableColors(SpringGuiFactory.getInstance().getInformationFlowColors());
    setAvailableBbTypes(Collections.singletonList(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL));
  }

  public boolean isShowIsBaseComponents() {
    return showIsBaseComponents;
  }

  public void setShowIsBaseComponents(boolean showIsBaseComponents) {
    this.showIsBaseComponents = showIsBaseComponents;
  }

  public boolean isShowIsBusinessObjects() {
    return showIsBusinessObjects;
  }

  public void setShowIsBusinessObjects(boolean showIsBusinessObjects) {
    this.showIsBusinessObjects = showIsBusinessObjects;
  }

  public void setSelectedTemplateName(String selectedTemplateName) {
    this.selectedTemplateName = selectedTemplateName;
  }

  public String getSelectedTemplateName() {
    return selectedTemplateName;
  }

  /**
   * Sets the available layout templates and makes sure that the option not to use
   * a template is present as well.
   * @param availableLayoutTemplates
   */
  public void setAvailableLayoutTemplates(List<ExportOption> availableLayoutTemplates) {
    ExportOption emptyOption = new ExportOption(DUMMY_TEMPLATE_NAME);
    if (!availableLayoutTemplates.contains(emptyOption)) {
      this.availableLayoutTemplates = Lists.newArrayList(emptyOption);
    }
    else {
      this.availableLayoutTemplates = Lists.newArrayList();
    }
    this.availableLayoutTemplates.addAll(availableLayoutTemplates);
  }

  public List<ExportOption> getAvailableLayoutTemplates() {
    return availableLayoutTemplates;
  }

  public String getSelectedNodeLayout() {
    return selectedNodeLayout;
  }

  public void setSelectedNodeLayout(String selectedNodeLayout) {
    this.selectedNodeLayout = selectedNodeLayout;
  }

  public List<ExportOption> getAvailableNodeLayouts() {
    return availableNodeLayouts;
  }

  public void setAvailableNodeLayouts(List<ExportOption> availableNodeLayouts) {
    this.availableNodeLayouts = availableNodeLayouts;
  }

  public void setSelectedTemplateFile(File selectedTemplateFile) {
    this.selectedTemplateFile = selectedTemplateFile;
  }

  public File getSelectedTemplateFile() {
    return selectedTemplateFile;
  }

  public void setIsiAttributes(List<BBAttribute> attributes) {
    this.isiAttributes = attributes;

    // refresh lists once again
    this.lineCaptionAttributeIds = new ArrayList<Integer>();
    this.lineCaptionAttributeNames = new ArrayList<String>();

    for (BBAttribute attribute : attributes) {
      this.lineCaptionAttributeIds.add(attribute.getId());
      this.lineCaptionAttributeNames.add(attribute.getName());
    }
  }

  public List<BBAttribute> getIsiAttributes() {
    return isiAttributes;
  }

  public List<BBAttribute> getSingleValueIsiAttributes() {
    final List<BBAttribute> singleValueAttributes = CollectionUtils.arrayList();
    for (BBAttribute attr : isiAttributes) {
      if (!attr.isMultiValue()) {
        singleValueAttributes.add(attr);
      }
    }
    return singleValueAttributes;
  }

  public int[] getSelectionType() {
    return selectionType.clone();
  }

  public void setSelectionType(int[] selectionType) {
    this.selectionType = selectionType.clone();
  }

  public boolean isAttributeLineCaption() {
    return ArrayUtils.contains(selectionType, LINE_DESCR_ATTRIBUTES);
  }

  /**
   * Retrieves a list of the available layouting algorithms for the information flow diagram.
   * 
   * @return The list of export options.
   */
  public static List<ExportOption> getInformationFlowLayoutAlgorithms() {

    List<ExportOption> supportedLayoutAlgorithms = new ArrayList<ExportOption>();

    supportedLayoutAlgorithms.add(new ExportOption(Constants.REPORTS_EXPORT_INFORMATIONFLOW_LAYOUT_STANDARD));
    supportedLayoutAlgorithms.add(new ExportOption(Constants.REPORTS_EXPORT_INFORMATIONFLOW_LAYOUT_KK));
    supportedLayoutAlgorithms.add(new ExportOption(Constants.REPORTS_EXPORT_INFORMATIONFLOW_LAYOUT_CIRCLE));

    return supportedLayoutAlgorithms;
  }

  public Integer getLineCaptionSelectedAttributeId() {
    return lineCaptionSelectedAttributeId;
  }

  public void setLineCaptionSelectedAttributeId(Integer lineCaptionSelectedAttributeId) {
    this.lineCaptionSelectedAttributeId = lineCaptionSelectedAttributeId;
  }

  public List<String> getLineCaptionAttributeNames() {
    return lineCaptionAttributeNames;
  }

  public void setLineCaptionAttributeNames(List<String> captionAttributeNames) {
    this.lineCaptionAttributeNames = captionAttributeNames;
  }

  public List<Integer> getLineCaptionAttributeIds() {
    return lineCaptionAttributeIds;
  }

  public void setLineCaptionAttributeIds(List<Integer> captionAttributeIds) {
    this.lineCaptionAttributeIds = captionAttributeIds;
  }

  @Override
  public List<String> getQueryResultNames() {
    return DIAGRAM_QUERIES;
  }

  public List<InformationSystemInterface> getRelevantInterfaces() {
    return relevantInterfaces;
  }

  public void setRelevantInterfaces(List<InformationSystemInterface> relevantInterfaces) {
    this.relevantInterfaces = relevantInterfaces;
  }

  public List<BusinessObject> getRelevantBusinessObjects() {
    return relevantBusinessObjects;
  }

  public void setRelevantBusinessObjects(List<BusinessObject> relevantBusinessObjects) {
    this.relevantBusinessObjects = relevantBusinessObjects;
  }

}