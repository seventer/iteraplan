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
package de.iteratec.iteraplan.model;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import com.google.common.base.Joiner;

import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.common.util.StringUtil;


/**
 * This class represents the link to the business architecture management in iteraplan. It models a
 * tuple of building blocks that together represent a particular business architecture, that is,
 * relations between an information system release, a business process, a business function, a
 * business unit or a product.
 */
@Entity
@Audited
@Indexed(index = "index.BusinessMapping")
public class BusinessMapping extends BuildingBlock {

  private static final long        serialVersionUID = 7832991563400904622L;
  private InformationSystemRelease informationSystemRelease;
  private BusinessProcess          businessProcess;
  private BusinessUnit             businessUnit;
  private Product                  product;

  @Field(store = Store.YES)
  private String                   name;

  @Field(store = Store.YES)
  private String                   description;

  public BusinessMapping() {
    // No-arg constructor.
  }

  public void setName(String name) {
    this.name = StringUtils.trim(StringUtil.removeIllegalXMLChars(name));
  }

  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = StringUtil.removeIllegalXMLChars(description);
  }

  @Override
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.BUSINESSMAPPING;
  }

  public BuildingBlock getAssociatedElement(TypeOfBuildingBlock tob) {
    switch (tob) {
      case INFORMATIONSYSTEMRELEASE:
        return informationSystemRelease;
      case BUSINESSPROCESS:
        return businessProcess;
      case BUSINESSUNIT:
        return businessUnit;
      case PRODUCT:
        return product;
      default:
        return null;
    }
  }

  public InformationSystemRelease getInformationSystemRelease() {
    return informationSystemRelease;
  }

  public BusinessProcess getBusinessProcess() {
    return businessProcess;
  }

  public BusinessUnit getBusinessUnit() {
    return businessUnit;
  }

  public Product getProduct() {
    return product;
  }

  public void setInformationSystemRelease(InformationSystemRelease isr) {
    this.informationSystemRelease = isr;
  }

  public void setBusinessProcess(BusinessProcess bp) {
    this.businessProcess = bp;
  }

  public void setBusinessUnit(BusinessUnit businessUnit) {
    this.businessUnit = businessUnit;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  /**
   * Adds an {@link InformationSystemRelease}. Updates both sides of the association.
   */
  public void addInformationSystemRelease(InformationSystemRelease rel) {
    Preconditions.checkNotNull(rel);
    rel.getBusinessMappings().add(this);
    setInformationSystemRelease(rel);
  }

  /**
   * Adds a {@link BusinessProcess}. Updates both sides of the association.
   */
  public void addBusinessProcess(BusinessProcess bp) {
    Preconditions.checkNotNull(bp);
    bp.getBusinessMappings().add(this);
    setBusinessProcess(bp);
  }

  /**
   * Adds a {@code BusinessUnit}. Updates both sides of the association.
   */
  public void addBusinessUnit(BusinessUnit bu) {
    Preconditions.checkNotNull(bu);
    bu.getBusinessMappings().add(this);
    setBusinessUnit(bu);
  }

  /**
   * Adds a {@link Product}. Updates both sides of the association.
   */
  public void addProduct(Product p) {
    Preconditions.checkNotNull(p);
    p.getBusinessMappings().add(this);
    setProduct(p);
  }

  /**
   * Removes the {@link InformationSystemRelease}. Updates both sides of the association.
   */
  public void removeInformationSystemRelease() {
    if (informationSystemRelease != null) {
      informationSystemRelease.getBusinessMappings().remove(this);
      setInformationSystemRelease(null);
    }
  }

  /**
   * Removes the {@link BusinessProcess}. Updates both sides of the association.
   */
  public void removeBusinessProcess() {
    if (businessProcess != null) {
      businessProcess.getBusinessMappings().remove(this);
      setBusinessProcess(null);
    }
  }

  /**
   * Removes the {@link BusinessUnit}. Updates both sides of the association.
   */
  public void removeBusinessUnit() {
    if (businessUnit != null) {
      businessUnit.getBusinessMappings().remove(this);
      setBusinessUnit(null);
    }
  }

  /**
   * Removes the {@link Product}. Updates both sides of the association.
   */
  public void removeProduct() {
    if (product != null) {
      product.getBusinessMappings().remove(this);
      setProduct(null);
    }
  }

  public String getIdentityString() {
    String nullString = "null";

    String bpName = businessProcess == null ? nullString : businessProcess.getHierarchicalName();
    String buName = businessUnit == null ? nullString : businessUnit.getHierarchicalName();
    String prName = product == null ? nullString : product.getHierarchicalName();

    String isName = nullString;
    if (informationSystemRelease != null) {
      isName = informationSystemRelease.getHierarchicalName();
    }

    List<String> list = Arrays.asList(new String[] { isName, bpName, buName, prName });

    return GeneralHelper.makeConcatenatedStringWithSeparator(list, " / ");
  }

  /**
   * Returns a canonical name for this instance, suitable for use as part of a HTML ID.
   * 
   * @return The identity name as a String, free of non-word characters.
   */
  public String getNameForHtmlId() {
    String bpName = "";
    String buName = "";
    String prName = "";
    String isName = "";

    if (informationSystemRelease != null) {
      isName = processNameString(informationSystemRelease.getNonHierarchicalName());
    }

    if (businessProcess != null) {
      bpName = processNameString(businessProcess.getNonHierarchicalName());
    }

    if (businessUnit != null) {
      buName = processNameString(businessUnit.getNonHierarchicalName());
    }
    if (product != null) {
      prName = processNameString(product.getNonHierarchicalName());
    }

    return Joiner.on(':').join(isName, bpName, buName, prName);
  }

  private String processNameString(String processName) {
    final String defaultName = StringUtils.defaultIfEmpty(processName, "null");

    return defaultName.replaceAll("\\W", "");
  }

  @Override
  public void validate() {
    super.validate();
      if (isAnyReferenceNull()) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.CANNOT_ADD_INVALID_BUSINESS_MAPPINGS);
      }
      else if (hasTopLevelElementsOnly()) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.BUSINESS_MAPPING_AMBIGUOUS);
    }
  }

  public boolean equalsIds(Integer bpID, Integer ouID, Integer prId) {
    boolean isAnyParameterNull = bpID == null || ouID == null || prId == null;
    boolean isAnyReferenceNull = businessProcess == null || businessUnit == null || product == null;

    if (isAnyParameterNull || isAnyReferenceNull) {
      return false;
    }

    if (bpID.equals(businessProcess.getId()) && ouID.equals(businessUnit.getId()) && prId.equals(product.getId())) {
      return true;
    }

    return false;
  }

  public boolean hasTopLevelElementsOnly() {
    return businessProcess.isTopLevelElement() && product.isTopLevelElement() && businessUnit.isTopLevelElement();
  }

  private boolean isAnyReferenceNull() {
    return businessProcess == null || businessUnit == null || product == null || informationSystemRelease == null;
  }
}