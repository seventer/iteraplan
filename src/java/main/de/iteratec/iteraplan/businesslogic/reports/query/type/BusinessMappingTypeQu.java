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
package de.iteratec.iteraplan.businesslogic.reports.query.type;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.BusinessMapping;


/**
 * Extends the class {@link BusinessMappingType} with functionality concerning textual queries.
 */
public final class BusinessMappingTypeQu extends BusinessMappingType implements QueryType {

  /** Serialization version. */
  private static final long                  serialVersionUID            = 4098843966301903181L;

  private static final BusinessMappingTypeQu INSTANCE                    = new BusinessMappingTypeQu();

  public static final String                 EXTENSION_INFORMATIONSYSTEM = "bpcE1";
  public static final String                 EXTENSION_BUSINESSPROCESS   = "bpcE2";
  public static final String                 EXTENSION_BUSINESSUNIT      = "bpcE3";
  public static final String                 EXTENSION_PRODUCT           = "bpcE4";

  private BusinessMappingTypeQu() {
    super(BusinessMapping.class.getSimpleName(), "bpc");
  }

  public static BusinessMappingTypeQu getInstance() {
    return INSTANCE;
  }

  protected void initExtensions() {

    addExtension(new Extension(EXTENSION_INFORMATIONSYSTEM, Constants.EXTENSION_ISR, getAssociation(ASSOCIATION_INFORMATIONSYSTEM)));

    addExtension(new Extension(EXTENSION_BUSINESSPROCESS, Constants.EXTENSION_BP, getAssociation(ASSOCIATION_BUSINESSPROCESS)));

    addExtension(new Extension(EXTENSION_BUSINESSUNIT, Constants.EXTENSION_BU, getAssociation(ASSOCIATION_BUSINESSUNIT)));

    addExtension(new Extension(EXTENSION_PRODUCT, Constants.EXTENSION_PRODUCT, getAssociation(ASSOCIATION_PRODUCT)));
  }

  protected void initAssociations() {

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_INFORMATIONSYSTEM, InformationSystemReleaseTypeQu.getInstance(), false));

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_BUSINESSPROCESS, BusinessProcessTypeQ.getInstance(), false));

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_BUSINESSUNIT, BusinessUnitQueryType.getInstance(), false));

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_PRODUCT, ProductQueryType.getInstance(), false));
  }

  public boolean isAssociationType() {
    return true;
  }

  public boolean isAnnotatedWithAttributesOrProperties() {
    return true;
  }

  public QueryType getQueryType() {
    return getInstance();
  }

}