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
package de.iteratec.iteraplan.model.xml.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.OptionConsiderStateAndDate;
import de.iteratec.iteraplan.businesslogic.reports.query.type.IPresentationExtension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.ConcurrentModificationException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.BBAttribute;


/**
 * XML dto for (un)marshalling
 * {@link de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData}
 * 
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 */
@XmlRootElement
@XmlType(name = "queryForm", propOrder = { "extension", "postProcessingStrategies", "postprocessingStrategy", "queryUserInput", "secondLevel" })
public class QueryFormXML implements QueryXMLElement<DynamicQueryFormData<?>> {

  private static final Logger         LOGGER                   = Logger.getIteraplanLogger(QueryFormXML.class);

  private QUserInputXML               queryUserInput           = null;
  private TypeXML                     typeXML                  = null;
  private PresentationExtensionXML    extension                = null;
  private PostProcessingStrategiesXML postProcessingStrategies = null;
  private final String                postprocessingStrategy   = null;
  private List<QueryFormXML>          secondLevel              = new ArrayList<QueryFormXML>();

  @XmlElement
  public QUserInputXML getQueryUserInput() {
    return queryUserInput;
  }

  @XmlAttribute(required = true)
  public TypeXML getTypeXML() {
    return typeXML;
  }

  @XmlElement
  public PresentationExtensionXML getExtension() {
    return extension;
  }

  @XmlElement
  public PostProcessingStrategiesXML getPostProcessingStrategies() {
    if (postProcessingStrategies != null) {
      return postProcessingStrategies;
    }
    else {
      return new PostProcessingStrategiesXML();
    }
  }

  public void setPostProcessingStrategies(PostProcessingStrategiesXML postProcessingStrategies) {
    this.postProcessingStrategies = postProcessingStrategies;
  }

  /**
   * Deprecated method necessary for the backwards compatibility of saved queries of iteraplan versions prior to 2.8.
   * @return
   *    The selected postprocessing strategy.
   */
  @XmlElement
  @Deprecated
  public String getPostprocessingStrategy() {
    return postprocessingStrategy;
  }

  public void setExtension(PresentationExtensionXML extension) {
    this.extension = extension;
  }

  public void setQueryUserInput(QUserInputXML queryUserInput) {
    this.queryUserInput = queryUserInput;
  }

  public void setTypeXML(TypeXML type) {
    this.typeXML = type;
  }

  @XmlTransient
  public Type<?> getType() {
    return this.getTypeXML().getQueryType();
  }

  /**
   * Very unfancy helper method to wrap from a QueryType to its XML representation
   * 
   * @param type
   *          The query type
   */
  public void setType(Type<?> type) {
    this.typeXML = TypeXML.getTypeXML(type);
  }

  private List<PostProcessingStrategyXML> getListOfStrategies(List<AbstractPostprocessingStrategy<? extends BuildingBlock>> selectedPPStrategies) {
    List<PostProcessingStrategyXML> listOfStrategies = new ArrayList<PostProcessingStrategyXML>();

    if (selectedPPStrategies != null && !selectedPPStrategies.isEmpty()) {
      for (AbstractPostprocessingStrategy<? extends BuildingBlock> tmp : selectedPPStrategies) {
        PostProcessingStrategyXML strategy = new PostProcessingStrategyXML();
        List<PostProcessingAdditionalOptionsXML> additionalOptions = new ArrayList<PostProcessingAdditionalOptionsXML>();

        strategy.setName(tmp.getNameKeyForPresentation());

        for (OptionConsiderStateAndDate option : tmp.getAdditionalOptions()) {
          if (option.isSelected()) {
            PostProcessingAdditionalOptionsXML strategyOption = new PostProcessingAdditionalOptionsXML();
            strategyOption.setAdditionalOption(option.getKey());
            additionalOptions.add(strategyOption);
          }
        }
        strategy.setAdditionalOptions(additionalOptions);
        listOfStrategies.add(strategy);
      }
    }

    return listOfStrategies;
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#initFrom(java.lang.Object)
   */
  public void initFrom(DynamicQueryFormData<?> queryElement, Locale locale,
                       List<AbstractPostprocessingStrategy<? extends BuildingBlock>> selectedPPStrategies) {
    this.setType(queryElement.getType());
    this.postProcessingStrategies = new PostProcessingStrategiesXML();
    List<PostProcessingStrategyXML> listOfStrategies = getListOfStrategies(selectedPPStrategies);

    this.postProcessingStrategies.setPostProcessingStrategy(listOfStrategies);
    this.queryUserInput = new QUserInputXML();
    queryUserInput.initFrom(queryElement.getQueryUserInput(), locale);
    // up to here attributes are referenced by their ID.
    // Transform userdefined attribute to their name here as the integer ID might change
    // --> use 'type_dbName_AttributeName' instead of 'type_dbName_Id' for identification
    // Note: this has to be changed back when importing an XML file
    for (QFirstLevelXML firstLevel : queryUserInput.getQueryFirstLevels()) {
      for (QPartXML partXML : firstLevel.getQuerySecondLevels()) {
        String attrId = partXML.getChosenAttributeStringId();
        if (StringUtils.isNotEmpty(attrId) && (!BBAttribute.BLANK_ATTRIBUTE_TYPE.equals(BBAttribute.getTypeByStringId(attrId)))) {
          BBAttribute attribute = queryElement.getBBAttributeByStringId(attrId);
          if (attribute == null) {
            throw new ConcurrentModificationException();
          }
          partXML.setChosenAttributeStringId(attribute.getStringId());
        }
      }
    }
    if (queryElement.getExtension() != null) {
      this.extension = new PresentationExtensionXML();
      extension.initFrom(queryElement.getExtension(), locale);
    }
    for (DynamicQueryFormData<?> formData : queryElement.getSecondLevelQueryForms()) {
      QueryFormXML businessMappingElement = new QueryFormXML();
      businessMappingElement.initFrom(formData, locale);
      this.addBusinessMappingElement(businessMappingElement);
    }
  }

  public void initFrom(DynamicQueryFormData<?> queryElement, Locale locale) {
    initFrom(queryElement, locale, new ArrayList<AbstractPostprocessingStrategy<? extends BuildingBlock>>());
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#update(java.lang.Object,
   * java.util.Locale)
   */
  public void update(DynamicQueryFormData<?> queryElement, Locale locale) {
    // set the query configuration (attributes, ANDs, ORs, ...
    if (this.queryUserInput != null) {
      this.queryUserInput.update(queryElement.getQueryUserInput(), locale);
    }
  }

  /**
   * Marks a form as a query extension. Creates a new {@link IPresentationExtension} that is added
   * to the queryElement, marking it as extension. Removes the extension from the list of available
   * extensions.
   * 
   * @param queryElement
   *          The form that is an extension
   * @param availableExtensions
   *          The available extensions, the current extension will be removed
   */
  public void updateExtensions(DynamicQueryFormData<?> queryElement, Map<String, IPresentationExtension> availableExtensions) {
    // if the queryForm represents an extension (i.e. a query part that was added by selecting
    // an extension element from the drop down box) more information has to be set
    if (this.extension != null && availableExtensions != null) {
      IPresentationExtension ext = availableExtensions.get(extension.getName());
      if (ext == null) {
        return;
      }
      LOGGER.debug("Adding extension: {0}", ext.getName());

      queryElement.setExtension(ext);
      availableExtensions.remove(extension.getName());
    }
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#validate(java.util.Locale)
   */
  public void validate(Locale locale) {
    // typeXML does not have not be validated as it is marked as a required attribute and therefore
    // validated against the XML schema

    if (this.queryUserInput != null) {
      queryUserInput.validate(locale);
    }
    if (this.extension != null) {
      extension.validate(locale);
    }
  }

  @XmlElement
  public List<QueryFormXML> getSecondLevel() {
    return secondLevel;
  }

  public void setSecondLevel(List<QueryFormXML> secondLevel) {
    this.secondLevel = secondLevel;
  }

  public void addBusinessMappingElement(QueryFormXML element) {
    this.secondLevel.add(element);
  }

}
