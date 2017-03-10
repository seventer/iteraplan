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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.common.BuildingBlockRelationMapping;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.AttributeAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.AttributeRangeAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.DimensionAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.NumberAttributeAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.SealStateAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.StatusAdapter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.iteraplan.model.interfaces.StatusEntity;


public abstract class AbstractPieBarDiagramCreator<T> {

  private final PieBarDiagramOptionsBean options;
  private final List<BuildingBlock>      selectedEntities;
  private final Locale                   locale;

  private final AttributeTypeService     attributeTypeService;
  private final AttributeValueService    attributeValueService;

  public AbstractPieBarDiagramCreator(PieBarDiagramOptionsBean options, List<BuildingBlock> selectedEntitites,
      AttributeTypeService attributeTypeService, AttributeValueService attributeValueService) {
    this.options = options;
    this.selectedEntities = selectedEntitites;
    this.locale = UserContext.getCurrentLocale();
    this.attributeTypeService = attributeTypeService;
    this.attributeValueService = attributeValueService;
  }

  public abstract T createDiagram();

  /**
   * Adapter to process attribute values before adding them to {@link PieBar}s.
   * Returns null if no special processing is needed.
   * @return an applicable adapter
   */
  protected DimensionAdapter<?> getValuesAdapter(Integer attributeId) {
    return getValuesAdapter(attributeId, false);
  }

  /**
   * Adapter to process attribute values before adding them to {@link PieBar}s.
   * Returns null if no special processing is needed.
   * @return an applicable adapter
   */
  protected DimensionAdapter<?> getValuesAdapter(Integer attributeId, boolean useValueRange) {

    if (Integer.valueOf(GraphicalExportBaseOptions.SEAL_SELECTED).equals(attributeId)) {
      return new SealStateAdapter(locale);
    }
    else if (Integer.valueOf(GraphicalExportBaseOptions.STATUS_SELECTED).equals(attributeId)) {
      return new StatusAdapter(locale, TypeOfBuildingBlock.getTypeOfBuildingBlockByString(options.getSelectedBbType()));
    }
    else if (attributeId.intValue() < 0) {
      return null;
    }

    AttributeAdapter adapter = null;
    AttributeType at = attributeTypeService.loadObjectById(attributeId);
    if (useValueRange) {
      List<String> values = attributeValueService.getAllAVStrings(attributeId);
      adapter = new NumberAttributeAdapter(locale);
      adapter.init(at, values);
    }
    else if (at instanceof NumberAT) {
      List<String> values = attributeValueService.getAllAVStrings(attributeId);
      adapter = new AttributeRangeAdapter(locale);
      adapter.init(at, values);
    }
    else if ((at instanceof EnumAT) || (at instanceof ResponsibilityAT)) {
      List<String> values = attributeValueService.getAllAVStrings(attributeId);
      adapter = new AttributeAdapter(locale);
      adapter.init(at, values);
    }

    return adapter;
  }

  /**
   * Creates a list of the identityString-values from the given list of {@link IdentityEntity}s
   * @param identityEntities
   *          List of {@link IdentityEntity}s
   * @return List of String values
   */
  protected List<String> createValuesListFromIdentityEntities(Collection<? extends IdentityEntity> identityEntities) {
    List<String> result = CollectionUtils.arrayList();
    for (IdentityEntity entity : identityEntities) {
      result.add(entity.getIdentityString());
    }
    return result;
  }

  /**
   * Creates a list of String-values from the attribute values of {@code bb} according to the given
   * {@code adapter} or, if {@code adapter} is null, to {@code attrId}.
   * @param bb
   *          {@link BuildingBlock}
   * @param attrId
   *          id of the attribute type in question
   * @param adapter
   *          Adapter to get the processed attribute values from {@code bb}, null possible, if no special processing is necessary
   * @return List of String values
   */
  protected List<String> createValuesListFromBuildingBlock(BuildingBlock bb, Integer attrId, DimensionAdapter<?> adapter) {
    if (adapter == null) {
      return getAttributeValues(bb, attrId);
    }
    else {
      return adapter.getMultipleResultsForValue(bb);
    }
  }

  /**
   * Returns the attribute values of the given {@code attributeTypeId}
   * which are assigned to the given building block.
   * If the selectedAttributeTypeId is null or -1 an empty list is returned. 
   * @param bb
   *          the given building block
   * @param attributeTypeId
   *          id 
   * @return List of assigned attribute value strings of the selected attribute type from {@link PieBarDiagramOptionsBean}
   */
  protected List<String> getAttributeValues(BuildingBlock bb, Integer attributeTypeId) {
    if (attributeTypeId == null || attributeTypeId.equals(Integer.valueOf(-1)) || bb == null) {
      return new ArrayList<String>();
    }
    else if (attributeTypeId.equals(Integer.valueOf(0))) {
      return ImmutableList.of(((StatusEntity) bb).getTypeOfStatusAsString());
    }
    else if (attributeTypeId.equals(Integer.valueOf(-4))) {
      String description = bb.getDescription();
      if (description == null || description.isEmpty()) {
        return new ArrayList<String>();
      }
      else {
        return ImmutableList.of(description);
      }
    }
    else {
      List<AttributeValue> connectedAvs = bb.getConnectedAttributeValues(attributeTypeService.loadObjectById(attributeTypeId));
      List<String> avStrings = CollectionUtils.arrayList();
      for (AttributeValue av : connectedAvs) {
        avStrings.add(av.getValueString());
      }
      return avStrings;
    }
  }

  protected String getAttributeNameFromId(Integer dimensionAttributeId) {
    String name = BBAttribute.getAttributeNameById(dimensionAttributeId);
    if (dimensionAttributeId.intValue() > 0) {
      return name;
    }
    else {
      return MessageAccess.getStringOrNull(name, getLocale());
    }
  }

  /**
   * Returns the values from an {@link ColorDimensionOptionsBean} or an {@link DimensionAdapter}.
   * First this method decides which values should be chosen. The one of these methods will be
   * called: {@link #getValues(ColorDimensionOptionsBean)}, {@link #getValues(DimensionAdapter)}
   * @param adapter {@link DimensionAdapter}, which contains the values.
   * @return returns a new list of {@link String} values.
   */
  protected List<String> getValues(ColorDimensionOptionsBean colorOptions, DimensionAdapter<?> adapter) {
    if (colorOptions.isUseColorRange()) {
      return getValues(adapter);
    }
    else {
      return getValues(colorOptions);
    }
  }

  /**
   * Returns the values from an {@link DimensionAdapter}. Also the default values will
   * be added to the list.
   * @param adapter {@link DimensionAdapter}, which contains the values.
   * @return returns a new list of {@link String} values.
   */
  protected List<String> getValues(DimensionAdapter<?> adapter) {
    List<String> list = Lists.newArrayList(adapter.getValues());
    list.add(DimensionOptionsBean.DEFAULT_VALUE);
    return list;
  }

  /**
   * Returns the values from an {@link ColorDimensionOptionsBean}. Also the default values will
   * be added to the list, if the dimension attribute's id is not the value 0.
   * @param colorOptions {@link ColorDimensionOptionsBean}, which contains the values.
   * @return returns a new list of {@link String} values.
   */
  protected List<String> getValues(ColorDimensionOptionsBean colorOptions) {
    List<String> list = Lists.newArrayList(colorOptions.getAttributeValues());
    if (!colorOptions.getDimensionAttributeId().equals(Integer.valueOf(0))) {
      list.add(DimensionOptionsBean.DEFAULT_VALUE);
    }
    return list;
  }

  protected List<BuildingBlock> getSelectedEntities() {
    return selectedEntities;
  }

  protected PieBarDiagramOptionsBean getOptions() {
    return options;
  }

  protected Locale getLocale() {
    return locale;
  }

  protected AttributeTypeService getAttributeTypeService() {
    return attributeTypeService;
  }

  protected AttributeValueService getAttributeValueService() {
    return attributeValueService;
  }

  protected String getLabelForSize(int size) {
    return String.valueOf(size);
  }

  protected Set<? extends IdentityEntity> getAssociatedEntities(String association, BuildingBlock bb) {
    Set<? extends IdentityEntity> associatedEntities = new BuildingBlockRelationMapping(bb).getMapping().get(association);
    if (associatedEntities == null) {
      return new HashSet<IdentityEntity>();
    }
    return associatedEntities;
  }

}
