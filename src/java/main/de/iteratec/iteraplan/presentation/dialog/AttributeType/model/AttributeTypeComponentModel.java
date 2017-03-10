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
package de.iteratec.iteraplan.presentation.dialog.AttributeType.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TimeseriesType;
import de.iteratec.iteraplan.model.attribute.TypeOfAttribute;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;
import de.iteratec.iteraplan.presentation.dialog.common.model.BigDecimalComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.BooleanComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ElementNameComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.EnumComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyToOneComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.NullSafeModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.StringComponentModel;


/**
 * GUI model for managing {@link AttributeType}s.
 */
public class AttributeTypeComponentModel extends AbstractComponentModelBase<AttributeType> {

  /** Serialization version. */
  private static final long                                                  serialVersionUID      = 7887375124470850634L;
  private static final String                                                NAME_LABEL            = "global.name";
  private static final String                                                DESCRIPTION_LABEL     = "global.description";
  private static final String                                                ATTRIBUTE_TYPE_LABEL  = "manageAttributes.type";
  private static final String                                                ATTRIBUTE_GROUP_LABEL = "global.attributegroup";
  private static final String                                                MANDATORY_LABEL       = "manageAttributes.mandatoryattribute";

  private String                                                             lastModificationUser  = "";
  private Date                                                               lastModificationTime  = null;
  private TypeOfAttribute                                                    typeOfAttribute;

  private ElementNameComponentModel<AttributeType>                           nameModel;
  private StringComponentModel<AttributeType>                                descriptionModel;
  private EnumComponentModel<AttributeType, TypeOfAttribute>                 typeOfAttributeModel;
  private ManyToOneComponentModel<AttributeType, AttributeTypeGroup>         attributeTypeGroupModel;
  private BooleanComponentModel<AttributeType>                               mandatoryModel;
  private ManyAssociationSetComponentModel<AttributeType, BuildingBlockType> buildingBlockTypeModel;

  private Integer                                                            modelObjectId;

  private final Collection<ComponentModel<AttributeType>>                    baseModels            = new ArrayList<ComponentModel<AttributeType>>();
  private ComponentModel<AttributeType>                                      attributeTypeSpecializationComponentModel;
  private AttributeType                                                      attributeType;

  public AttributeTypeComponentModel(ComponentMode componentMode) {
    super(componentMode);
    baseModels.add(getNameModel());
    baseModels.add(getDescriptionModel());
    baseModels.add(getTypeOfAttributeModel());
    baseModels.add(getAttributeTypeGroupModel());
    baseModels.add(getMandatoryModel());
    baseModels.add(getBuildingBlockTypeModel());
  }

  public void initializeFrom(AttributeType at) {
    Preconditions.checkNotNull(at);

    modelObjectId = at.getId();
    lastModificationUser = at.getLastModificationUser();
    lastModificationTime = at.getLastModificationTime();

    for (ComponentModel<AttributeType> model : baseModels) {
      model.initializeFrom(at);
    }

    if (typeOfAttribute == null) {
      initTypeOfAttribute(at);
    }
    attributeTypeSpecializationComponentModel.initializeFrom(at);
  }

  protected void initTypeOfAttribute(AttributeType at) {
    typeOfAttribute = at.getTypeOfAttribute();
    if (typeOfAttribute == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    switch (typeOfAttribute) {
      case DATE:
        attributeTypeSpecializationComponentModel = new NullSafeModel<AttributeType>();
        break;
      case ENUM:
        attributeTypeSpecializationComponentModel = new EnumAttributeTypeComponentModel(getComponentMode());
        break;
      case NUMBER:
        attributeTypeSpecializationComponentModel = new NumberAttributeTypeComponentModel(getComponentMode());
        break;
      case RESPONSIBILITY:
        attributeTypeSpecializationComponentModel = new ResponsibilityAttributeTypeComponentModel(getComponentMode());
        break;
      case TEXT:
        attributeTypeSpecializationComponentModel = new TextAttributeTypeComponentModel(getComponentMode());
        break;
      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  public final void update() {
    if (getComponentMode() != ComponentMode.READ) {
      Preconditions.checkNotNull(attributeTypeSpecializationComponentModel);
      for (ComponentModel<AttributeType> model : baseModels) {
        model.update();
      }
      attributeTypeSpecializationComponentModel.update();
    }
  }

  public void configure(AttributeType at) {
    Preconditions.checkNotNull(attributeTypeSpecializationComponentModel);
    for (ComponentModel<AttributeType> model : baseModels) {
      model.configure(at);
    }
    attributeTypeSpecializationComponentModel.configure(at);
  }

  public final void validate(Errors errors) {
    Preconditions.checkNotNull(attributeTypeSpecializationComponentModel);

    errors.pushNestedPath("nameModel");
    nameModel.validate(errors);
    errors.popNestedPath();

    errors.pushNestedPath("descriptionModel");
    descriptionModel.validateDescription(errors);
    errors.popNestedPath();

    attributeTypeSpecializationComponentModel.validate(errors);
  }

  protected Collection<ComponentModel<AttributeType>> getBaseModels() {
    return baseModels;
  }

  public final ElementNameComponentModel<AttributeType> getNameModel() {
    if (nameModel == null) {
      nameModel = new NameElementNameComponentModel(getComponentMode(), "name", NAME_LABEL);
    }
    return nameModel;
  }

  public final StringComponentModel<AttributeType> getDescriptionModel() {
    if (descriptionModel == null) {
      descriptionModel = new DescriptionStringComponentModel(getComponentMode(), "description", DESCRIPTION_LABEL);
    }
    return descriptionModel;
  }

  public final ManyToOneComponentModel<AttributeType, AttributeTypeGroup> getAttributeTypeGroupModel() {
    if (attributeTypeGroupModel == null) {
      attributeTypeGroupModel = new AttributeTypeGroupManyToOneComponentModel(getComponentMode(), "attributeTypeGroup", ATTRIBUTE_GROUP_LABEL, false);
    }
    return attributeTypeGroupModel;
  }

  public final EnumComponentModel<AttributeType, TypeOfAttribute> getTypeOfAttributeModel() {
    if (typeOfAttributeModel == null) {
      typeOfAttributeModel = new TypeOfAttributeEnumComponentModel(ComponentMode.READ, "typeOfAttribute", ATTRIBUTE_TYPE_LABEL);
    }
    return typeOfAttributeModel;
  }

  public final BooleanComponentModel<AttributeType> getMandatoryModel() {
    if (mandatoryModel == null) {
      mandatoryModel = new MandatoryBooleanComponentModel(getComponentMode(), "mandatory", MANDATORY_LABEL);
    }
    return mandatoryModel;
  }

  public final ManyAssociationSetComponentModel<AttributeType, BuildingBlockType> getBuildingBlockTypeModel() {
    if (buildingBlockTypeModel == null) {
      buildingBlockTypeModel = new BuildingBlockTypeCM(getComponentMode(), "buildingBlockType", "manageAttributes.assignedentities",
          new String[] { "global.name" }, new String[] { "typeOfBuildingBlock.value" }, "typeOfBuildingBlock.value", new BuildingBlockType(
              TypeOfBuildingBlock.DUMMY, true), new Boolean[] { Boolean.TRUE }, Boolean.TRUE, null);
    }
    return buildingBlockTypeModel;
  }

  public String getLastModificationUser() {
    return lastModificationUser;
  }

  public User getLastModificationUserByLoginName() {
    return SpringServiceFactory.getUserService().getUserByLoginIfExists(getLastModificationUser());
  }

  public Date getLastModificationTime() {
    return lastModificationTime;
  }

  public Integer getModelObjectId() {
    return modelObjectId;
  }

  public TypeOfAttribute getManagedTypeOfAttribute() {
    return typeOfAttribute;
  }

  public BooleanComponentModel<EnumAT> getMultivalueModel() {
    Preconditions.checkArgument(attributeTypeSpecializationComponentModel instanceof EnumAttributeTypeComponentModel);
    return ((EnumAttributeTypeComponentModel) attributeTypeSpecializationComponentModel).getMultivalueModel();
  }

  public BooleanComponentModel<TimeseriesType> getTimeseriesModel() {
    Preconditions.checkArgument(attributeTypeSpecializationComponentModel instanceof TimeseriesTypeComponentModel);
    return ((TimeseriesTypeComponentModel) attributeTypeSpecializationComponentModel).getTimeseriesModel();
  }

  public EnumAttributeValuesComponentModel getEnumAttributeValuesModel() {
    Preconditions.checkArgument(attributeTypeSpecializationComponentModel instanceof EnumAttributeTypeComponentModel);
    return ((EnumAttributeTypeComponentModel) attributeTypeSpecializationComponentModel).getEnumAttributeValuesModel();
  }

  public BigDecimalComponentModel<NumberAT> getLowerBoundModel() {
    Preconditions.checkArgument(attributeTypeSpecializationComponentModel instanceof NumberAttributeTypeComponentModel);
    return ((NumberAttributeTypeComponentModel) attributeTypeSpecializationComponentModel).getLowerBoundModel();
  }

  public BigDecimalComponentModel<NumberAT> getUpperBoundModel() {
    Preconditions.checkArgument(attributeTypeSpecializationComponentModel instanceof NumberAttributeTypeComponentModel);
    return ((NumberAttributeTypeComponentModel) attributeTypeSpecializationComponentModel).getUpperBoundModel();
  }

  public StringComponentModel<NumberAT> getUnitModel() {
    Preconditions.checkArgument(attributeTypeSpecializationComponentModel instanceof NumberAttributeTypeComponentModel);
    return ((NumberAttributeTypeComponentModel) attributeTypeSpecializationComponentModel).getUnitModel();
  }

  public BooleanComponentModel<NumberAT> getRangeUniformyDistributed() {
    Preconditions.checkArgument(attributeTypeSpecializationComponentModel instanceof NumberAttributeTypeComponentModel);
    return ((NumberAttributeTypeComponentModel) attributeTypeSpecializationComponentModel).getRangeUniformyDistributedModel();
  }

  public RangeValuesComponentModel getRangesModel() {
    Preconditions.checkArgument(attributeTypeSpecializationComponentModel instanceof NumberAttributeTypeComponentModel);
    return ((NumberAttributeTypeComponentModel) attributeTypeSpecializationComponentModel).getRangesModel();
  }

  public BooleanComponentModel<TextAT> getMultiLineModel() {
    Preconditions.checkArgument(attributeTypeSpecializationComponentModel instanceof TextAttributeTypeComponentModel);
    return ((TextAttributeTypeComponentModel) attributeTypeSpecializationComponentModel).getMultiLineModel();
  }

  public BooleanComponentModel<ResponsibilityAT> getMultiAssignmentTypeModel() {
    Preconditions.checkArgument(attributeTypeSpecializationComponentModel instanceof ResponsibilityAttributeTypeComponentModel);
    return ((ResponsibilityAttributeTypeComponentModel) attributeTypeSpecializationComponentModel).getMultiAssignmentTypeModel();
  }

  public ResponsibilityAttributeValuesComponentModel getResponsibilityAttributeValuesModel() {
    Preconditions.checkArgument(attributeTypeSpecializationComponentModel instanceof ResponsibilityAttributeTypeComponentModel);
    return ((ResponsibilityAttributeTypeComponentModel) attributeTypeSpecializationComponentModel).getResponsibilityAttributeValuesModel();
  }

  protected void setTypeOfAttribute(TypeOfAttribute typeOfAttribute) {
    this.typeOfAttribute = typeOfAttribute;
  }

  protected TypeOfAttribute getTypeOfAttribute() {
    return typeOfAttribute;
  }

  protected void setAttributeTypeSpecializationComponentModel(ComponentModel<AttributeType> attributeTypeSpecializationComponentModel) {
    this.attributeTypeSpecializationComponentModel = attributeTypeSpecializationComponentModel;
  }

  protected ComponentModel<AttributeType> getAttributeTypeSpecializationComponentModel() {
    return attributeTypeSpecializationComponentModel;
  }

  private static final class NameElementNameComponentModel extends ElementNameComponentModel<AttributeType> {
    /** Serialization version. */
    private static final long serialVersionUID = -2168507012040915810L;

    public NameElementNameComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public void setStringForElement(AttributeType target, String stringToSet) {
      target.setName(stringToSet);
    }

    @Override
    public String getStringFromElement(AttributeType source) {
      return source.getName();
    }

    /**{@inheritDoc}**/
    @Override
    public void validate(Errors errors) {
      super.validate(errors);

      for (String forbiddenName : AttributeType.BLACKLIST_FOR_AT_NAME) {
        if (forbiddenName.equalsIgnoreCase(getCurrent())) {
          errors.rejectValue("name", "errors.reservedKeyword", new String[] { forbiddenName, MessageAccess.getString(getLabelKey()) },
              "reserved keyword");
        }
      }
    }
  }

  private static final class DescriptionStringComponentModel extends StringComponentModel<AttributeType> {
    /** Serialization version. */
    private static final long serialVersionUID = -6824399151167258795L;

    public DescriptionStringComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public void setStringForElement(AttributeType target, String stringToSet) {
      target.setDescription(stringToSet);
    }

    @Override
    public String getStringFromElement(AttributeType source) {
      return source.getDescription();
    }
  }

  private static final class AttributeTypeGroupManyToOneComponentModel extends ManyToOneComponentModel<AttributeType, AttributeTypeGroup> {
    /** Serialization version. */
    private static final long serialVersionUID = 288476299596291181L;

    public AttributeTypeGroupManyToOneComponentModel(ComponentMode componentMode, String htmlId, String labelKey, boolean nullable) {
      super(componentMode, htmlId, labelKey, nullable);
    }

    @Override
    protected List<AttributeTypeGroup> getAvailableElements(Integer id) {
      return SpringServiceFactory.getAttributeTypeGroupService().getAllAttributeTypeGroups();
    }

    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      throw new IllegalArgumentException();
    }

    @Override
    protected AttributeTypeGroup getConnectedElement(AttributeType source) {
      return source.getAttributeTypeGroup();
    }

    @Override
    protected void setConnectedElement(AttributeType target, AttributeTypeGroup atg) {
      AttributeTypeGroup reloadedAtg = SpringServiceFactory.getAttributeTypeGroupService().loadObjectById(atg.getId());

      target.setAttributeTypeGroupTwoWay(reloadedAtg);
    }
  }

  private static final class TypeOfAttributeEnumComponentModel extends EnumComponentModel<AttributeType, TypeOfAttribute> {
    /** Serialization version. */
    private static final long serialVersionUID = 1622854773233149308L;

    public TypeOfAttributeEnumComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    protected TypeOfAttribute getEnumByString(String value) {
      return TypeOfAttribute.getTypeOfAttributeFromString(value);
    }

    @Override
    protected TypeOfAttribute getEnumFromElement(AttributeType source) {
      return source.getTypeOfAttribute();
    }

    @Override
    protected void setEnumForElement(AttributeType target, TypeOfAttribute currentEnum) {
      // empty implementation: the type of attribute enum is read-only and should not be
      // changed.
    }

    @Override
    protected List<TypeOfAttribute> getValues() {
      return Arrays.asList(TypeOfAttribute.values());
    }
  }

  private static final class MandatoryBooleanComponentModel extends BooleanComponentModel<AttributeType> {
    /** Serialization version. */
    private static final long serialVersionUID = -7291274952674481333L;

    public MandatoryBooleanComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public Boolean getBooleanFromElement(AttributeType source) {
      return Boolean.valueOf(source.isMandatory());
    }

    @Override
    public void setBooleanForElement(AttributeType target, Boolean booleanToSet) {
      target.setMandatory(booleanToSet.booleanValue());
    }
  }

  private static final class BuildingBlockTypeCM extends ManyAssociationSetComponentModel<AttributeType, BuildingBlockType> {
    /** Serialization version. */
    private static final long serialVersionUID = 1454395275232106710L;

    public BuildingBlockTypeCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, BuildingBlockType dummyForPresentation, Boolean[] lookupLablesMode,
        Boolean lookupAvailableLablesMode, String[] availableElementsPresentationGroupKeys) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation,
          lookupLablesMode, lookupAvailableLablesMode, availableElementsPresentationGroupKeys);
    }

    @Override
    protected List<BuildingBlockType> getAvailableElements(Integer id, List<BuildingBlockType> connected) {
      List<BuildingBlockType> list = SpringServiceFactory.getBuildingBlockTypeService().getBuildingBlockTypesEligibleForAttributes();

      // consider only those building block type that have not already been connected
      list.removeAll(connected);

      return list;
    }

    @Override
    protected Set<BuildingBlockType> getConnectedElements(AttributeType source) {
      return source.getBuildingBlockTypes();
    }

    @Override
    protected void setConnectedElements(AttributeType target, Set<BuildingBlockType> referenceObjects) {
      List<BuildingBlockType> reloadBbts = SpringServiceFactory.getBuildingBlockTypeService().reload(referenceObjects);

      target.removeAllBuildingBlockTypesTwoWay();
      target.addBuildingBlockTypesTwoWay(Sets.newHashSet(reloadBbts));
    }
  }

  public AttributeType getAttributeType() {
    return attributeType;
  }

  public void setAttributeType(AttributeType attributeType) {
    this.attributeType = attributeType;
  }

}
