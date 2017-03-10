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
package de.iteratec.iteraplan.businesslogic.exchange.svg;

import java.util.List;

import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.piebar.AbstractPieBarDiagramCreator;
import de.iteratec.iteraplan.businesslogic.exchange.common.piebar.BarDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.common.piebar.BarDiagramCreator;
import de.iteratec.iteraplan.businesslogic.exchange.common.piebar.PieBar;
import de.iteratec.iteraplan.businesslogic.exchange.common.piebar.PieDiagramCreator;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Composite.CompositeDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.svg.model.Document;
import de.iteratec.svg.model.Shape;
import de.iteratec.svg.model.SvgExportException;
import de.iteratec.svg.styling.SvgCssStyling;


public class SvgCompositeDiagramExport extends SvgExport {

  private static final String                         COMPOSITE_TEMPLATE_FILE = "/SVGCompositeTemplate.svg";

  private final List<AbstractPieBarDiagramCreator<?>> creators;
  private final List<PieBarDiagramOptionsBean>        optionsList;
  private final CompositeDiagramOptionsBean           compositeOptions;

  private final List<Shape>                           diagramContainerShapes  = CollectionUtils.arrayList();

  private final Coordinates                           documentDimension       = new Coordinates(0, 0);
  private final List<String>                          cssClassNames           = CollectionUtils.arrayList();

  public SvgCompositeDiagramExport(CompositeDiagramOptionsBean compositeOptions, List<AbstractPieBarDiagramCreator<?>> creators,
      List<PieBarDiagramOptionsBean> optionsList, AttributeTypeService attributeTypeService, AttributeValueService attributeValueService) {
    super(attributeTypeService, attributeValueService);
    this.creators = creators;
    this.optionsList = optionsList;
    this.compositeOptions = compositeOptions;

    loadSvgDocumentFromTemplate(COMPOSITE_TEMPLATE_FILE, "Composite");
  }

  @Override
  public Document createDiagram() {
    try {
      initCssClassNameList();

      createSubDiagrams();

      addSubDiagramsToDocument();

      setDocumentDimensions();

      if (!compositeOptions.isNakedExport()) {
        createGeneratedInformation(getSvgDocument().getPageWidth(), getSvgDocument().getPageHeight());
        createLogos(0, 0, getSvgDocument().getPageWidth(), getSvgDocument().getPageHeight());
      }
      
      setCustomSize(compositeOptions.getWidth(), compositeOptions.getHeight());

      getSvgDocument().finalizeDocument();
    } catch (SvgExportException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }

    return getSvgDocument();
  }

  private void initCssClassNameList() {
    for (SvgCssStyling css : getSvgDocument().getCSSStyleClasses()) {
      cssClassNames.add(css.getCssClassName());
    }
  }

  private void createSubDiagrams() throws SvgExportException {
    List<SvgCssStyling> documentCssStyles = getSvgDocument().getCSSStyleClasses();
    int shapeIdCount = 0;
    for (PieBarDiagramOptionsBean options : optionsList) {
      int index = optionsList.indexOf(options);
      options.setNakedExport(true);

      switch (options.getDiagramType()) {
        case PIE:
          PieBar pieBar = ((PieDiagramCreator) creators.get(index)).createDiagram();
          SvgPieDiagramExport pieExport = new SvgPieDiagramExport(pieBar, options, getAttributeTypeService(), getAttributeValueService());

          shapeIdCount = createSubDiagram(documentCssStyles, shapeIdCount, pieExport);
          break;
        case BAR:
          BarDiagram diagram = ((BarDiagramCreator) creators.get(index)).createDiagram();
          SvgBarDiagramExport barExport = new SvgBarDiagramExport(diagram, options, getAttributeTypeService(), getAttributeValueService());

          shapeIdCount = createSubDiagram(documentCssStyles, shapeIdCount, barExport);
          break;
        default:
      }

      options.switchDimensionOptionsToPresentationMode();
    }
  }

  private int createSubDiagram(List<SvgCssStyling> documentCssStyles, int shapeIdCount, AbstractSvgPieBarExport export) throws SvgExportException {
    export.setEmbeddedDiagram(true);
    export.setStartShapeIdCount(shapeIdCount);

    Document doc = export.createDiagram();

    Shape containerShape = export.getDiagramContainer();

    if (containerShape != null) {
      diagramContainerShapes.add(containerShape);
      documentDimension.setX(Math.max(documentDimension.getX(), containerShape.getWidth()));
      documentDimension.incY(containerShape.getHeight());
    }

    for (SvgCssStyling css : doc.getCSSStyleClasses()) {
      if (!cssClassNames.contains(css.getCssClassName())) {
        documentCssStyles.add(css);
        cssClassNames.add(css.getCssClassName());
      }
    }
    return doc.getShapeIdCount();
  }

  private void addSubDiagramsToDocument() {
    double yPos = 0;
    for (Shape diagramShape : diagramContainerShapes) {
      diagramShape.setPosition(diagramShape.getPinX(), yPos);
      getSvgDocument().addShapeToDocument(Document.DEFAULT_LAYER, diagramShape);
      yPos += diagramShape.getHeight();
    }
  }

  private void setDocumentDimensions() {
    getSvgDocument().setPageSize(documentDimension.getX(), documentDimension.getY());
  }

}