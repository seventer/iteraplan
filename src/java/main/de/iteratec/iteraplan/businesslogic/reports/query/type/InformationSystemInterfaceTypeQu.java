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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.InformationSystemInterface;


/**
 * Extends the class {@link InformationSystemDomainType} with functionality for textual queries.
 */
public final class InformationSystemInterfaceTypeQu extends InformationSystemInterfaceType implements QueryType {

  /** Serialization version. */
  private static final long                             serialVersionUID                     = -7778496724879489067L;

  private static final InformationSystemInterfaceTypeQu INSTANCE                             = new InformationSystemInterfaceTypeQu();

  public static final String                            DIRECTION = "interfaceDirection";
  
  public static final String                            EXTENSION_TECHNICALCOMPONENTRELEASE  = "connE1";
  public static final String                            EXTENSION_INFORMATIONSYSTEMRELEASE_A = "connE2";
  public static final String                            EXTENSION_INFORMATIONSYSTEMRELEASE_B = "connE3";
  public static final String                            EXTENSION_BUSINESSOBJECT             = "connE4";

  public static final String                            PRESENTATION_EXTENSION_ISR           = "connPE1";
  
  private InformationSystemInterfaceTypeQu() {
    super(ClassUtils.getShortClassName(InformationSystemInterface.class), "isi");
  }

  public static InformationSystemInterfaceTypeQu getInstance() {
    return INSTANCE;
  }

  @Override
  protected void initExtensions() {
    addExtension(new Extension(EXTENSION_TECHNICALCOMPONENTRELEASE, Constants.EXTENSION_TCR, getAssociation(ASSOCIATION_TECHNICALCOMPONENTRELEASES)));
    addExtension(new Extension(EXTENSION_INFORMATIONSYSTEMRELEASE_A, Constants.EXTENSION_ISR_ISI,
        getAssociation(ASSOCIATION_INFORMATIONSYSTEMRELEASE_A)));
    addExtension(new Extension(EXTENSION_INFORMATIONSYSTEMRELEASE_B, Constants.EXTENSION_ISR_ISI,
        getAssociation(ASSOCIATION_INFORMATIONSYSTEMRELEASE_B)));
    addExtension(new Extension(EXTENSION_INFORMATIONSYSTEMRELEASE_B, Constants.EXTENSION_ISR_ISI,
        getAssociation(ASSOCIATION_INFORMATIONSYSTEMRELEASE_B)));

    // Complex extension: Business Object via Transport.
    TypeWithJoinProperty firstStep = getAssociation(ASSOCIATION_TRANSPORTS);
    TypeWithJoinProperty secondStep = TransportQueryType.getInstance().getAssociation(TransportQueryType.ASSOCIATION_BUSINESSOBJECT);
    List<TypeWithJoinProperty> typesWithJoinProperties = new ArrayList<TypeWithJoinProperty>();
    typesWithJoinProperties.add(firstStep);
    typesWithJoinProperties.add(secondStep);
    addExtension(new Extension(EXTENSION_BUSINESSOBJECT, Constants.EXTENSION_ISI_BO, typesWithJoinProperties));
  }

  @Override
  protected void initAssociations() {
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_TECHNICALCOMPONENTRELEASES, TechnicalComponentReleaseTypeQu.getInstance(), true));

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_INFORMATIONSYSTEMRELEASE_A, InformationSystemReleaseTypeQu.getInstance(), false));

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_INFORMATIONSYSTEMRELEASE_B, InformationSystemReleaseTypeQu.getInstance(), false));

    addAssociation(new TypeWithJoinProperty(ASSOCIATION_TRANSPORTS, TransportQueryType.getInstance(), true));
  }

  @Override
  public Map<String, IPresentationExtension> getExtensionsForPresentation() {
    // combine from and to ipureleases:
    Map<String, IPresentationExtension> extensions = new HashMap<String, IPresentationExtension>(getExtensions());
    Extension ex1 = (Extension) extensions.get(EXTENSION_INFORMATIONSYSTEMRELEASE_A);
    Extension ex2 = (Extension) extensions.get(EXTENSION_INFORMATIONSYSTEMRELEASE_B);
    List<Extension> extensionList = new ArrayList<Extension>();
    extensionList.add(ex1);
    extensionList.add(ex2);
    CombinedExtension cex = new CombinedExtension(PRESENTATION_EXTENSION_ISR, Constants.EXTENSION_ISI_ISR, extensionList);
    extensions.put(PRESENTATION_EXTENSION_ISR, cex);
    extensions.remove(EXTENSION_INFORMATIONSYSTEMRELEASE_A);
    extensions.remove(EXTENSION_INFORMATIONSYSTEMRELEASE_B);
    return extensions;
  }

  public QueryType getQueryType() {
    return getInstance();
  }

  @Override
  protected void initProperties() {
    super.initProperties();
    addProperty(new Property(DIRECTION, Constants.ATTRIBUTE_TRANSPORT, getProperties().size()-1));
  }
}
