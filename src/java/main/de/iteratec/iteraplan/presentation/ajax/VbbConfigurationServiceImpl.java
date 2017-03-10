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
package de.iteratec.iteraplan.presentation.ajax;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import de.iteratec.iteraplan.businesslogic.exchange.common.VbbVersion;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.BaseVBB;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfiguration;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfigurationParser;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointFactory;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.MixedColorCodingDecorator;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.recommendation.Recommender;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.recommendation.ViewpointConfigurationRecommendation;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.recommendation.ViewpointConfigurationRecommendation.PrioritizedNamedElement;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.ElasticeamService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.elasticeam.derived.DerivedMetamodelFactory;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationPropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;
import de.iteratec.iteraplan.elasticeam.metamodel.derived.DerivedNamedExpression;
import de.iteratec.iteraplan.elasticmi.exception.ElasticMiException;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;


public class VbbConfigurationServiceImpl implements VbbConfigurationService {

  private ElasticeamService    elasticeamService;
  private AttributeTypeService attributeTypeService;

  public void setElasticeamService(ElasticeamService elasticeamService) {
    this.elasticeamService = elasticeamService;
  }

  public void setAttributeTypeService(AttributeTypeService attributeTypeService) {
    this.attributeTypeService = attributeTypeService;
  }

  /**{@inheritDoc}**/
  public Map<String, Collection<Tag>> recommend(Map<String, String> parameters, String viewpointName) {
    if (VbbVersion.isVbb30()) {
      Metamodel metamodel = elasticeamService.getMetamodel();
      metamodel = DerivedMetamodelFactory.deriveMetamodel(metamodel);

      ViewpointConfiguration vpConfig = new ViewpointConfiguration(viewpointName);
      BaseVBB vbb = ViewpointFactory.getViewpoint(vpConfig);

      // Enrich the viewpointConfiguration Object with the values from the configuration map.
      ViewpointConfigurationParser vpConfigParser = new ViewpointConfigurationParser(vbb, metamodel);
      vpConfigParser.parse(parameters, vpConfig);

      Recommender recommender = new Recommender(vpConfigParser.getAbstractViewmodel(), metamodel);
      ViewpointConfigurationRecommendation rec = recommender.recommend(vpConfig);

      return generateJsonRecommendation(rec);
    }
    else {
      throw new ElasticMiException(ElasticMiException.UNSUPPORTED_OPERATION, "Work in progress");
    }
  }

  public Map<String, String> recommendColorMapping(String holder, String attributeName) {
    if (VbbVersion.isVbb30()) {
      Metamodel metamodel = elasticeamService.getMetamodel();
      metamodel = DerivedMetamodelFactory.deriveMetamodel(metamodel);

      Map<String, String> colorMappingConfig = Maps.newHashMap();

      UniversalTypeExpression type = metamodel.findUniversalTypeByPersistentName(holder);
      if (type == null) {
        return colorMappingConfig;
      }
      PropertyExpression<?> property = type.findPropertyByPersistentName(attributeName);
      if (property == null) {
        return colorMappingConfig;
      }

      if (BuiltinPrimitiveType.DECIMAL.equals(property.getType())) {
        colorMappingConfig.put(MixedColorCodingDecorator.VV_DECORATION_MODE, MixedColorCodingDecorator.DECORATION_MODE_CONTINUOUS);
        AttributeType at = attributeTypeService.getAttributeTypeByName(property.getPersistentName());
        if (!(at instanceof NumberAT)) {
          throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
        }
        NumberAT numberAT = (NumberAT) at;
        colorMappingConfig.put(MixedColorCodingDecorator.ContinuousColorCodingDecorator.VV_MIN_VALUE, numberAT.getMinValue() == null ? "" : numberAT
            .getMinValue().toString());
        colorMappingConfig.put(MixedColorCodingDecorator.ContinuousColorCodingDecorator.VV_MAX_VALUE, numberAT.getMaxValue() == null ? "" : numberAT
            .getMaxValue().toString());
      }
      else if (property instanceof EnumerationPropertyExpression) {
        colorMappingConfig.put(MixedColorCodingDecorator.VV_DECORATION_MODE, MixedColorCodingDecorator.DECORATION_MODE_DISCRETE);
        Gson gson = new Gson();
        List<String> availableColors = Lists.newArrayList(SpringGuiFactory.getInstance().getVbbClusterColors());
        availableColors.add(Constants.DEFAULT_GRAPHICAL_EXOPORT_COLOR);
        colorMappingConfig.put("availableColors", gson.toJson(availableColors));

        Map<String, String> literal2color = Maps.newHashMap();
        List<String> orderedLiterals = Lists.newArrayList();
        EnumerationExpression enumeration = (EnumerationExpression) property.getType();
        for (EnumerationLiteralExpression literal : enumeration.getLiterals()) {
          //escaping of names, use localized names?
          literal2color.put(literal.getPersistentName(), Integer.toHexString(literal.getDefaultColor().getRGB()).substring(2));
          orderedLiterals.add(literal.getPersistentName());
        }
        if (!"typeOfStatus".equals(property.getPersistentName())) {
          literal2color.put(MessageAccess.getString(DimensionOptionsBean.DEFAULT_VALUE), Constants.DEFAULT_GRAPHICAL_EXOPORT_COLOR);
          orderedLiterals.add(MessageAccess.getString(DimensionOptionsBean.DEFAULT_VALUE));
        }
        String serializedLiteralMappings = gson.toJson(literal2color);
        colorMappingConfig.put("selectedColors", serializedLiteralMappings);
        colorMappingConfig.put("orderedLiterals", gson.toJson(orderedLiterals));
      }
      return colorMappingConfig;
    }
    else {
      throw new ElasticMiException(ElasticMiException.UNSUPPORTED_OPERATION, "Work in progress");
    }
  }

  private Map<String, Collection<Tag>> generateJsonRecommendation(ViewpointConfigurationRecommendation rec) {
    Map<String, Collection<Tag>> resultMap = CollectionUtils.hashMap();

    for (Entry<NamedExpression, Set<PrioritizedNamedElement>> recEntry : rec.entrySet()) {
      Map<String, Tag> tmpMap = Maps.newHashMap();
      List<PrioritizedNamedElement> children = Lists.newLinkedList();
      for (PrioritizedNamedElement recommendation : recEntry.getValue()) {
        if (recommendation.getNamedElement() instanceof DerivedNamedExpression
            && ((DerivedNamedExpression) recommendation.getNamedElement()).getParent() != null) {
          children.add(recommendation);
        }
        else {
          tmpMap.put(recommendation.getNamedElement().getPersistentName(), new Tag(recommendation.getNamedElement().getPersistentName(),
              recommendation.getNamedElement().getName(), recommendation.getPriority(), true));
        }
      }
      for (PrioritizedNamedElement child : children) {
        Tag parent = tmpMap.get(((DerivedNamedExpression) child.getNamedElement()).getParent().getPersistentName());
        if (parent == null) {
          tmpMap.put(child.getNamedElement().getPersistentName(), new Tag(child.getNamedElement().getPersistentName(), child.getNamedElement()
              .getName(), child.getPriority(), false));
        }
        else {
          parent.addChild(new Tag(child.getNamedElement().getPersistentName(), child.getNamedElement().getName(), child.getPriority(), false));
        }
      }

      List<Tag> tmp = Lists.newLinkedList(tmpMap.values());
      Collections.sort(tmp);
      resultMap.put(getName(recEntry.getKey()), tmp);
    }

    return resultMap;
  }

  private static String getName(NamedExpression avmElement) {
    if (avmElement instanceof FeatureExpression) {
      return ((FeatureExpression<?>) avmElement).getHolder().getPersistentName() + "." + avmElement.getPersistentName();
    }
    else {
      return avmElement.getPersistentName();
    }
  }
}
