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
package de.iteratec.iteraplan.model.xml.query;

import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlTransient;

import de.iteratec.iteraplan.businesslogic.reports.query.type.ArchitecturalDomainTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessDomainQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessFunctionQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessMappingTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessObjectTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessProcessTypeQ;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessUnitQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemDomainTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemInterfaceTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InfrastructureElementTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ProductQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ProjectQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.TechnicalComponentReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;


@XmlEnum(String.class)
public enum TypeXML {

  TECHNICALCOMPONENTRELEASE, INFORMATIONSYSTEMINTERFACE, INFORMATIONSYSTEMRELEASE, INFRASTRUCTUREELEMENT, PROJECT, BUSINESSUNIT, PRODUCT, BUSINESSPROCESS, BUSINESSOBJECT, ARCHITECTURALDOMAIN, INFORMATIONSYSTEMDOMAIN, BUSINESSFUNCTION, BUSINESSDOMAIN, BUSINESSMAPPING;

  private static final Map<TypeXML, Type<?>>                  XML_TYPE_TO_TYPE = CollectionUtils.hashMap();
  static {
    XML_TYPE_TO_TYPE.put(INFORMATIONSYSTEMRELEASE, InformationSystemReleaseTypeQu.getInstance());
    XML_TYPE_TO_TYPE.put(TECHNICALCOMPONENTRELEASE, TechnicalComponentReleaseTypeQu.getInstance());
    XML_TYPE_TO_TYPE.put(INFORMATIONSYSTEMINTERFACE, InformationSystemInterfaceTypeQu.getInstance());
    XML_TYPE_TO_TYPE.put(INFRASTRUCTUREELEMENT, InfrastructureElementTypeQu.getInstance());
    XML_TYPE_TO_TYPE.put(PROJECT, ProjectQueryType.getInstance());
    XML_TYPE_TO_TYPE.put(BUSINESSUNIT, BusinessUnitQueryType.getInstance());
    XML_TYPE_TO_TYPE.put(PRODUCT, ProductQueryType.getInstance());
    XML_TYPE_TO_TYPE.put(BUSINESSPROCESS, BusinessProcessTypeQ.getInstance());
    XML_TYPE_TO_TYPE.put(BUSINESSOBJECT, BusinessObjectTypeQu.getInstance());
    XML_TYPE_TO_TYPE.put(ARCHITECTURALDOMAIN, ArchitecturalDomainTypeQu.getInstance());
    XML_TYPE_TO_TYPE.put(INFORMATIONSYSTEMDOMAIN, InformationSystemDomainTypeQu.getInstance());
    XML_TYPE_TO_TYPE.put(BUSINESSFUNCTION, BusinessFunctionQueryType.getInstance());
    XML_TYPE_TO_TYPE.put(BUSINESSDOMAIN, BusinessDomainQueryType.getInstance());
    XML_TYPE_TO_TYPE.put(BUSINESSMAPPING, BusinessMappingTypeQu.getInstance());
  }

  private static final Map<Class<? extends Type<?>>, TypeXML> TYPE_TO_XML_TYPE = CollectionUtils.hashMap();
  static {
    TYPE_TO_XML_TYPE.put(InformationSystemReleaseTypeQu.class, INFORMATIONSYSTEMRELEASE);
    TYPE_TO_XML_TYPE.put(TechnicalComponentReleaseTypeQu.class, TECHNICALCOMPONENTRELEASE);
    TYPE_TO_XML_TYPE.put(InformationSystemInterfaceTypeQu.class, INFORMATIONSYSTEMINTERFACE);
    TYPE_TO_XML_TYPE.put(InfrastructureElementTypeQu.class, INFRASTRUCTUREELEMENT);
    TYPE_TO_XML_TYPE.put(ProjectQueryType.class, PROJECT);
    TYPE_TO_XML_TYPE.put(BusinessUnitQueryType.class, BUSINESSUNIT);
    TYPE_TO_XML_TYPE.put(ProductQueryType.class, PRODUCT);
    TYPE_TO_XML_TYPE.put(BusinessProcessTypeQ.class, BUSINESSPROCESS);
    TYPE_TO_XML_TYPE.put(BusinessObjectTypeQu.class, BUSINESSOBJECT);
    TYPE_TO_XML_TYPE.put(ArchitecturalDomainTypeQu.class, ARCHITECTURALDOMAIN);
    TYPE_TO_XML_TYPE.put(InformationSystemDomainTypeQu.class, INFORMATIONSYSTEMDOMAIN);
    TYPE_TO_XML_TYPE.put(BusinessFunctionQueryType.class, BUSINESSFUNCTION);
    TYPE_TO_XML_TYPE.put(BusinessDomainQueryType.class, BUSINESSDOMAIN);
    TYPE_TO_XML_TYPE.put(BusinessMappingTypeQu.class, BUSINESSMAPPING);
  }

  @XmlTransient
  public Type<?> getQueryType() {
    if (XML_TYPE_TO_TYPE.containsKey(this)) {
      return XML_TYPE_TO_TYPE.get(this);
    }
    throw new IteraplanTechnicalException();
  }

  @XmlTransient
  public static TypeXML getTypeXML(Type<?> type) {
    if (TYPE_TO_XML_TYPE.containsKey(type.getClass())) {
      return TYPE_TO_XML_TYPE.get(type.getClass());
    }
    else {
      Logger logger = Logger.getIteraplanLogger(TypeXML.class);
      logger.error("Type " + type + " is not supported!");
      throw new IteraplanTechnicalException();
    }
  }
}
