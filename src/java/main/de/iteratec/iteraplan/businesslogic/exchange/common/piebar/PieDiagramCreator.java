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
package de.iteratec.iteraplan.businesslogic.exchange.common.piebar;

import java.util.List;

import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.DimensionAdapter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;


public class PieDiagramCreator extends AbstractPieBarDiagramCreator<PieBar> {

  private static final Logger LOGGER = Logger.getIteraplanLogger(PieDiagramCreator.class);

  public PieDiagramCreator(PieBarDiagramOptionsBean options, List<BuildingBlock> selectedBbs, AttributeTypeService attributeTypeService,
      AttributeValueService attributeValueService) {
    super(options, selectedBbs, attributeTypeService, attributeValueService);
  }

  @Override
  public PieBar createDiagram() {
    LOGGER.info("creating pie diagram");

    if (!PieBarDiagramOptionsBean.DiagramType.PIE.equals(getOptions().getDiagramType())) {
      LOGGER.error("Wrong Diagram Type: {0}", getOptions().getDiagramType().name());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }

    return createPie();
  }

  /**
   * Create and fill the {@link PieBar}-Object
   * @return {@link PieBar}-Object
   */
  private PieBar createPie() {
    switch (getOptions().getValuesSource()) {
      case ASSOCIATION:
        return createAssociationPie();
      case ATTRIBUTE:
        return createAttributePie();
      default:
        LOGGER.error("Invalid ValuesSource: {0}", getOptions().getValuesSource().name());
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
  }

  private PieBar createAssociationPie() {
    final PieBar pie = new PieBar(getOptions().getDiagramValuesType(), getValues(getOptions().getColorOptionsBean()));
    String label = MessageAccess.getStringOrNull(getOptions().getDiagramValuesType().getValue(), getLocale());
    pie.setLabel(MessageAccess.getStringOrNull(getOptions().getSelectedAssociation(), getLocale()) + ":\n" + label);

    for (BuildingBlock bb : getSelectedEntities()) {
      pie.add(createValuesListFromIdentityEntities(getAssociatedEntities(getOptions().getSelectedAssociation(), bb)));
    }

    return pie;
  }

  private PieBar createAttributePie() {
    Integer dimensionAttributeId = getOptions().getColorOptionsBean().getDimensionAttributeId();
    DimensionAdapter<?> adapter = getValuesAdapter(dimensionAttributeId, getOptions().getColorOptionsBean().isUseColorRange());

    final PieBar pie = new PieBar(getOptions().getDiagramValuesType(), getValues(getOptions().getColorOptionsBean(), adapter));

    for (BuildingBlock bb : getSelectedEntities()) {
      pie.add(createValuesListFromBuildingBlock(bb, dimensionAttributeId, adapter));
    }

    return pie;
  }

}