/*
 * Copyright 2011 Christian M. Schweda & iteratec
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iteratec.iteraplan.businesslogic.service;

import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.exchange.common.VbbVersion;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.BaseVBB;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfiguration;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfigurationParser;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointFactory;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalOptionsGetter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.vbb.VbbOptionsBean;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.elasticeam.derived.DerivedMetamodelFactory;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticmi.exception.ElasticMiException;
import de.iteratec.visualizationmodel.ASymbol;
import de.iteratec.visualizationmodel.BaseMapSymbol;
import de.iteratec.visualizationmodel.renderer.IRenderer;
import de.iteratec.visualizationmodel.renderer.RendererFactory;
import de.iteratec.visualizationmodel.renderer.RendererNotFoundException;


/**
 *
 */
public class VbbGraphicsServiceImpl implements VbbGraphicsService {

  private static final Logger LOGGER = Logger.getIteraplanLogger(VbbGraphicsService.class);

  private static enum TitleKey {
    CLUSTER("graphicalExport.vbbClusterDiagram"), TIMELINE("graphicalExport.timelineDiagram"), RECURSIVECLUSTER("graphicalExport.vbbClusterDiagram"), BINARYMATRIX(
        "graphicalExport.matrixDiagram");

    private String titleKey;

    private TitleKey(String titleKey) {
      this.titleKey = titleKey;
    }

    private String getTitleKey() {
      return this.titleKey;
    }
  }

  private ElasticeamService elasticeamService;

  public void setElasticeamService(ElasticeamService elasticeamService) {
    this.elasticeamService = elasticeamService;
  }

  /** {@inheritDoc}. */
  public byte[] createVbbDiagram(ManageReportMemoryBean memBean, String viewpointName) {
    if (VbbVersion.isVbb30()) {
      VbbOptionsBean optionsBean = GraphicalOptionsGetter.getVbbOptions(memBean);

      // Enrich viewpoint configuration with Title and URL
      Map<String, String> vpConfigMap = optionsBean.getViewpointConfigMap();
      // TODO Make this more generic. The viewpoint config map shouldn't be extended here.
      String titleKey = TitleKey.valueOf(viewpointName.toUpperCase()).getTitleKey();
      vpConfigMap.put(BaseMapSymbol.TITLE, MessageAccess.getStringOrNull(titleKey, UserContext.getCurrentLocale()));
      vpConfigMap.put(BaseMapSymbol.BASE_URL, optionsBean.getServerUrl());

      if (optionsBean.getWidth() != null) {
        vpConfigMap.put(BaseMapSymbol.WIDTH, optionsBean.getWidth().toString());
      }
      if (optionsBean.getHeight() != null) {
        vpConfigMap.put(BaseMapSymbol.HEIGHT, optionsBean.getHeight().toString());
      }
      vpConfigMap.put(BaseMapSymbol.NAKEDEXPORT, Boolean.toString(optionsBean.isNakedExport()));

      return generate(viewpointName, vpConfigMap);
    }
    else {
      throw new ElasticMiException(ElasticMiException.UNSUPPORTED_OPERATION, "Work in progress");
    }
  }

  @SuppressWarnings("boxing")
  private byte[] generate(String viewpointName, Map<String, String> vpConfigMap) {
    Metamodel enrichedMetamodel = this.elasticeamService.getMetamodel();
    enrichedMetamodel = DerivedMetamodelFactory.deriveMetamodel(enrichedMetamodel);

    ViewpointConfiguration vpConfig = new ViewpointConfiguration(viewpointName);
    BaseVBB vbb = ViewpointFactory.getViewpoint(vpConfig);

    // Enrich the viewpointConfiguration Object with the values from the configuration map.
    ViewpointConfigurationParser vpConfigParser = new ViewpointConfigurationParser(vbb, enrichedMetamodel);
    vpConfigParser.parse(vpConfigMap, vpConfig);

    if (vpConfigParser.isComplete()) {
      vbb.applyFilters(vpConfigMap, vpConfig, enrichedMetamodel);
      vbb.setVisualVariables(vpConfig.getVisualVariables());

      // Render the result
      ASymbol visObject = vbb.transform(this.elasticeamService.getModel(), vpConfig);

      if (visObject instanceof BaseMapSymbol) {
        BaseMapSymbol baseMapSymbol = (BaseMapSymbol) visObject;
        baseMapSymbol.setCustomWidth(vpConfigMap.get(BaseMapSymbol.WIDTH) == null ? null : new Double(vpConfigMap.get(BaseMapSymbol.WIDTH)));
        baseMapSymbol.setCustomHeight(vpConfigMap.get(BaseMapSymbol.HEIGHT) == null ? null : new Double(vpConfigMap.get(BaseMapSymbol.HEIGHT)));
        baseMapSymbol.setNaked(Boolean.valueOf(vpConfigMap.get(BaseMapSymbol.NAKEDEXPORT)));
      }

      IRenderer renderer;

      try {
        renderer = RendererFactory.getRenderer("svg");
        return renderer.render(visObject);
      } catch (RendererNotFoundException e) {
        LOGGER.error(e);
        return new byte[] {};
      }
    }
    else {
      // Display an error message.
      List<String> errors = vpConfigParser.getErrors();
      LOGGER.error("Invalid viewpoint configuration provided. Parser found {0} error(s).", errors.size());
      throw new IteraplanBusinessException(IteraplanErrorMessages.GRAPHIC_INCOMPLETE_USER_INPUT);
    }
  }
}
