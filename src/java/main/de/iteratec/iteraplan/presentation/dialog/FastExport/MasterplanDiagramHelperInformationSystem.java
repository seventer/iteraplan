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
package de.iteratec.iteraplan.presentation.dialog.FastExport;

import java.util.ArrayList;
import java.util.List;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanRowTypeOptions;
import de.iteratec.iteraplan.businesslogic.service.FastExportService.DiagramVariant;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.StringEnumReflectionHelper;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.DateInterval;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;


public class MasterplanDiagramHelperInformationSystem extends MasterplanDiagramHelper {

  @Override
  public List<BuildingBlock> determineResults(BuildingBlock start, DiagramVariant variant, MasterplanOptionsBean bean) {

    List<BuildingBlock> initialList = new ArrayList<BuildingBlock>();
    InformationSystemRelease rel = (InformationSystemRelease) start;

    if (DiagramVariant.MASTERPLAN_HIERARCHY.equals(variant)) {
      initialList.addAll(rel.getPredecessors());
      initialList.add(rel);
      initialList.addAll(rel.getSuccessors());
      if (bean != null) {
        bean.setSelectedBbType(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL);

        MasterplanRowTypeOptions level0Options = new MasterplanRowTypeOptions("", Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, 0,
            new ArrayList<DateInterval>(), new ArrayList<BBAttribute>(), new ArrayList<ColumnEntry>());
        level0Options.getColorOptions().setAvailableColors(SpringGuiFactory.getInstance().getMasterplanColors());
        level0Options.getColorOptions().setDimensionAttributeId(Integer.valueOf(GraphicalExportBaseOptions.STATUS_SELECTED));
        level0Options.getColorOptions().setColorRangeAvailable(false);
        level0Options.getColorOptions().refreshDimensionOptions(
            StringEnumReflectionHelper.getLanguageSpecificEnumValues(TypeOfStatus.class, UserContext.getCurrentLocale()));

        bean.setLevel0Options(level0Options);
      }
    }
    else if (DiagramVariant.MASTERPLAN_PROJECTS.equals(variant)) {
      initialList.addAll(rel.getProjects());
      if (bean != null) {

        bean.setSelectedBbType(Constants.BB_PROJECT_PLURAL);
      }

      if (initialList.isEmpty()) {

        throw new IteraplanBusinessException(IteraplanErrorMessages.MASTERPLAN_NO_ELEMENTS);
      }
      if (bean != null) {

        MasterplanRowTypeOptions level0Options = new MasterplanRowTypeOptions("", Constants.BB_PROJECT_PLURAL, 0, new ArrayList<DateInterval>(),
            new ArrayList<BBAttribute>(), new ArrayList<ColumnEntry>());
        level0Options.getColorOptions().setAvailableColors(SpringGuiFactory.getInstance().getMasterplanColors());
        level0Options.getColorOptions().setDimensionAttributeId(Integer.valueOf(GraphicalExportBaseOptions.NOTHING_SELECTED));
        level0Options.getColorOptions().resetValueToColorMap();

        bean.setLevel0Options(level0Options);
      }
    }
    else if (DiagramVariant.MASTERPLAN_TECHNICAL_COMPONENTS.equals(variant)) {
      initialList.addAll(rel.getTechnicalComponentReleases());
      if (bean != null) {

        bean.setSelectedBbType(Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL);
      }

      if (initialList.isEmpty()) {

        throw new IteraplanBusinessException(IteraplanErrorMessages.MASTERPLAN_NO_ELEMENTS);
      }
      if (bean != null) {
        MasterplanRowTypeOptions level0Options = new MasterplanRowTypeOptions("", Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL, 0,
            new ArrayList<DateInterval>(), new ArrayList<BBAttribute>(), new ArrayList<ColumnEntry>());
        level0Options.getColorOptions().setAvailableColors(SpringGuiFactory.getInstance().getMasterplanColors());
        level0Options.getColorOptions().setDimensionAttributeId(Integer.valueOf(GraphicalExportBaseOptions.STATUS_SELECTED));
        level0Options.getColorOptions().setColorRangeAvailable(false);
        level0Options.getColorOptions().refreshDimensionOptions(
            StringEnumReflectionHelper.getLanguageSpecificEnumValues(de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus.class,
                UserContext.getCurrentLocale()));

        bean.setLevel0Options(level0Options);
      }
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }

    return initialList;
  }
}
