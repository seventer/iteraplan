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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ObjectRetrievalFailureException;

import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.collections.EntityToIdFunction;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.Timeseries;
import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesEntry;
import de.iteratec.iteraplan.persistence.dao.TimeseriesDAO;


public class TimeseriesServiceImpl extends AbstractService implements TimeseriesService {
  private static final Logger         LOGGER            = Logger.getIteraplanLogger(TimeseriesServiceImpl.class);

  private boolean                     timeseriesEnabled = IteraplanProperties.getProperties().propertyIsSetToTrue(
                                                            IteraplanProperties.TIMESERIES_ENABLED);

  private TimeseriesDAO               timeseriesDAO;
  private AttributeValueService       avService;
  private BuildingBlockServiceLocator bbServiceLocator;

  /**
   * Only for tests
   */
  public void setTimeseriesEnabled(boolean timeseriesEnabled) {
    this.timeseriesEnabled = timeseriesEnabled;
  }

  public void setTimeseriesDAO(TimeseriesDAO timeseriesDAO) {
    this.timeseriesDAO = timeseriesDAO;
  }

  public void setAvService(AttributeValueService avService) {
    this.avService = avService;
  }

  public void setBbServiceLocator(BuildingBlockServiceLocator bbServiceLocator) {
    this.bbServiceLocator = bbServiceLocator;
  }

  public Timeseries loadObjectById(Integer id) {
    if (!timeseriesEnabled) {
      return null;
    }

    if (id == null) {
      LOGGER.error("Tried to load an object with id 'null'");
    }

    try {
      return timeseriesDAO.loadObjectById(id);
    } catch (ObjectRetrievalFailureException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ENTITY_NOT_FOUND_READ, e);
    }
  }

  public Timeseries loadObjectByIdIfExists(Integer id) {
    if (!timeseriesEnabled) {
      return null;
    }

    if (id == null) {
      LOGGER.error("Tried to load an object with id 'null'");
    }

    return timeseriesDAO.loadObjectByIdIfExists(id);
  }

  public Timeseries loadTimeseriesByBuildingBlockAndAttributeType(BuildingBlock bb, AttributeType at) {
    if (!timeseriesEnabled) {
      return null;
    }

    List<Timeseries> loadedTimeseries = timeseriesDAO.loadByBuildingBlockAndAttributeType(bb.getId(), at.getId());
    if (loadedTimeseries.isEmpty()) {
      return null;
    }
    else if (loadedTimeseries.size() != 1) {
      LOGGER.error("Several timeseries for same BuildingBlock \"{0}\" ({1}) and AttributeType \"{2}\" found.", bb.getHierarchicalName(), bb
          .getClass().getSimpleName(), at.getName());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }
    return loadedTimeseries.get(0);
  }

  public void deleteTimeseries(Timeseries toDelete) {
    timeseriesDAO.delete(toDelete);
  }

  public Timeseries saveOrUpdateWithBbUpdate(Timeseries timeseries) {
    if (!timeseriesEnabled) {
      return null;
    }
    BuildingBlock bb = updateBuildingBlockAttribute(timeseries, timeseries.getBuildingBlock());

    bbServiceLocator.getService(bb.getTypeOfBuildingBlock()).saveOrUpdate(bb);

    return saveOrUpdateWithoutBbUpdate(timeseries);
  }

  public BuildingBlock updateBuildingBlockAttribute(Timeseries timeseries, BuildingBlock buildingBlock) {
    if (!timeseriesEnabled) {
      return null;
    }

    TimeseriesEntry latestEntry = timeseries.getLatestEntry();

    AttributeType at = timeseries.getAttribute();
    if (latestEntry == null || latestEntry.getValue() == null) {
      avService.setValue(buildingBlock, null, at);
    }
    else if (at instanceof EnumAT) {
      writeLatestEnumEntry(latestEntry, buildingBlock, at);
    }
    else if (at instanceof NumberAT) {
      writeLatestNumberEntry(latestEntry, buildingBlock, (NumberAT) at);
    }
    else {
      LOGGER.error("Unhandled timeseries attribute type \"{0}\".", at);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }
    return buildingBlock;
  }

  /**{@inheritDoc}**/
  public Timeseries saveOrUpdateWithoutBbUpdate(Timeseries timeseries) {
    if (!timeseriesEnabled) {
      return null;
    }

    timeseries.validate();
    return timeseriesDAO.saveOrUpdate(timeseries);
  }

  public Integer deleteTimeseriesByAttributeType(final AttributeType at) {
    if (!timeseriesEnabled) {
      return null;
    }

    return timeseriesDAO.deleteTimeseriesByAttributeTypeId(at.getId());
  }

  public Integer deleteTimeseriesByBuildingBlocks(final Collection<? extends BuildingBlock> bbs) {
    if (!timeseriesEnabled) {
      return null;
    }

    Collection<Integer> ids = Sets.newHashSet(Collections2.transform(bbs, new EntityToIdFunction<BuildingBlock, Integer>()));
    return timeseriesDAO.deleteTimeseriesByBuildingBlockIds(ids);
  }

  private void writeLatestEnumEntry(TimeseriesEntry latestEntry, BuildingBlock bb, AttributeType at) {
    List<? extends AttributeValue> allAVs = avService.getAllAVs(at.getId());
    for (AttributeValue av : allAVs) {
      if (latestEntry.getValue().equals(av.getValueString())) {
        avService.setReferenceValues(bb, Collections.singleton(av), at.getId());
        return;
      }
    }
    LOGGER.error("No enumeration attribute value with name \"{0}\" found.", latestEntry.getValue());
    throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
  }

  private void writeLatestNumberEntry(TimeseriesEntry latestEntry, BuildingBlock bb, NumberAT at) {
    BigDecimal numberValue = BigDecimalConverter.parse(latestEntry.getValue(), UserContext.getCurrentLocale());
    NumberAV numberAV = new NumberAV(at, numberValue);
    avService.saveOrUpdate(numberAV);
    avService.setValue(bb, numberAV, at);
  }

  /**{@inheritDoc}**/
  public Map<BuildingBlock, Timeseries> loadTimeseriesForBuildingBlocks(Collection<? extends BuildingBlock> bbs, AttributeType at) {
    Map<BuildingBlock, Timeseries> result = Maps.newHashMap();

    Collection<Integer> ids = Sets.newHashSet();

    for (BuildingBlock bb : bbs) {
      ids.add(bb.getId());
    }

    List<Timeseries> resultList = timeseriesDAO.loadForBuildingBlocks(ids, at.getId());

    for (Timeseries ts : resultList) {
      result.put(ts.getBuildingBlock(), ts);
    }

    return result;
  }
}
