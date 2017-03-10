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
import java.util.List;

import org.apache.commons.lang.ClassUtils;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * Extends the class {@link TechnicalComponentReleaseType} with functionality for textual queries.
 */
public final class TechnicalComponentReleaseTypeQu extends TechnicalComponentReleaseType implements ITypeWithStatus, ITypeWithDates, QueryType {

  /** Serialization version. */
  private static final long                            serialVersionUID                     = -201086651419765018L;

  private static final TechnicalComponentReleaseTypeQu INSTANCE                             = new TechnicalComponentReleaseTypeQu();

  public static final String                           EXTENSION_TECHNICALCOMPONENTRELEASES = "tcrE0";
  public static final String                           EXTENSION_TCR_PREDECESSORS           = "tcrE0_predecessors";
  public static final String                           EXTENSION_TCR_BASECOMPONENTS         = "tcrE0_baseComponents";
  public static final String                           EXTENSION_INTERFACES                 = "tcrE1";
  public static final String                           EXTENSION_INFORMATIONSYSTEMRELEASES  = "tcrE2";
  public static final String                           EXTENSION_ARCHITECTURALDOMAINS       = "tcrE3";
  public static final String                           EXTENSION_SUCCESSORS                 = "tcrE4";
  public static final String                           EXTENSION_PREDECESSORS               = "tcrE5";
  public static final String                           EXTENSION_BASECOMPONENTS             = "tcrE6";
  public static final String                           EXTENSION_PARENTCOMPONENTS           = "tcrE7";
  public static final String                           EXTENSION_INFRASTRUCTUREELEMENTS     = "tcrE8";

  private TechnicalComponentReleaseTypeQu() {
    super(ClassUtils.getShortClassName(TechnicalComponentRelease.class), "tcr");
    addSpecialPropertyHQLString(PROPERTY_STARTDATE, new String[] { "is", "null" });
    addSpecialPropertyHQLString(PROPERTY_ENDDATE, new String[] { "is", "null" });
  }

  public static TechnicalComponentReleaseTypeQu getInstance() {
    return INSTANCE;
  }

  protected void initExtensions() {
    addExtension(new Extension(EXTENSION_INTERFACES, Constants.EXTENSION_TCR_ISI, getAssociation(ASSOCIATION_INTERFACES)));

    addExtension(getExtensionToInformationSystemReleases());

    addExtension(getExtensionToInfrastructureElements());

    addExtension(getExtensionToArchitecturalDomains());

    addExtension(new Extension(EXTENSION_SUCCESSORS, Constants.EXTENSION_TCR_SUCC, getAssociation(ASSOCIATION_SUCCESSORS)));

    addExtension(new Extension(EXTENSION_PREDECESSORS, Constants.EXTENSION_TCR_PRED, getAssociation(ASSOCIATION_PREDECESSORS)));

    addExtension(new Extension(EXTENSION_BASECOMPONENTS, Constants.EXTENSION_TCR_BASE, getAssociation(ASSOCIATION_BASECOMPONENTS)));

    addExtension(new Extension(EXTENSION_PARENTCOMPONENTS, Constants.EXTENSION_TCR_PARENT, getAssociation(ASSOCIATION_PARENTCOMPONENTS)));
  }

  private Extension getExtensionToArchitecturalDomains() {
    return new Extension(EXTENSION_ARCHITECTURALDOMAINS, Constants.EXTENSION_AD, getAssociation(ASSOCIATION_ARCHITECTURALDOMAINS));
  }

  private Extension getExtensionToInformationSystemReleases() {
    return new Extension(EXTENSION_INFORMATIONSYSTEMRELEASES, Constants.EXTENSION_ISR, getAssociation(ASSOCIATION_INFORMATIONSYSTEMRELEASES));
  }

  private Extension getExtensionToInfrastructureElements() {
    // Queries for infrastructure elements need to go over the attributable association 
    TypeWithJoinProperty firstJoin = getAssociation(ASSOCIATION_INFRASTRUCTUREELEMENT_ASSOCIATIONS);

    TypeWithJoinProperty secondJoin = Tcr2IeAssociationTypeQu.getInstance().getAssociation(Tcr2IeAssociationTypeQu.ASSOCIATION_INFRASTRUCTUREELEMENT);

    List<TypeWithJoinProperty> typesWithJoinProperties = new ArrayList<TypeWithJoinProperty>();
    typesWithJoinProperties.add(firstJoin);
    typesWithJoinProperties.add(secondJoin);

    return new Extension(EXTENSION_INFRASTRUCTUREELEMENTS, Constants.EXTENSION_IE, typesWithJoinProperties);
  }

  protected void initRelations() {
    super.initRelations();

    addSelfRelations();

    // relation to information system releases
    Extension isrRelation = getExtensionToInformationSystemReleases();
    isrRelation.setNameKeyForPresentation(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL);
    addRelation(isrRelation);

    // relation to infrastructure elements
    Extension ieRelation = getExtensionToInfrastructureElements();
    ieRelation.setNameKeyForPresentation(Constants.BB_INFRASTRUCTUREELEMENT_PLURAL);
    addRelation(ieRelation);

    // relation to architectural domains
    Extension adRelation = getExtensionToArchitecturalDomains();
    adRelation.setNameKeyForPresentation(Constants.BB_ARCHITECTURALDOMAIN_PLURAL);
    addRelation(adRelation);

  }

  private void addSelfRelations() {
    String prefix = "graphicalReport." + Constants.BB_TECHNICALCOMPONENTRELEASE_BASE;
    String permKey = "technicalComponentRelease";
    // predecessors
    addRelation(new Extension(EXTENSION_TCR_PREDECESSORS, prefix + "predecessors", permKey, getAssociation(ASSOCIATION_PREDECESSORS)));
    // base components
    addRelation(new Extension(EXTENSION_TCR_BASECOMPONENTS, prefix + "baseComponents", permKey, getAssociation(ASSOCIATION_BASECOMPONENTS)));
  }

  public String getTypeOfStatusProperty() {
    return PROPERTY_TYPEOFSTATUS;
  }

  public String getStartDateProperty() {
    return PROPERTY_STARTDATE;
  }

  public String getEndDateProperty() {
    return PROPERTY_ENDDATE;
  }

  protected void initAssociations() {
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_INTERFACES, InformationSystemInterfaceTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_INFORMATIONSYSTEMRELEASES, InformationSystemReleaseTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_INFRASTRUCTUREELEMENT_ASSOCIATIONS, Tcr2IeAssociationTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_ARCHITECTURALDOMAINS, ArchitecturalDomainTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_SUCCESSORS, TechnicalComponentReleaseTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_PREDECESSORS, TechnicalComponentReleaseTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_BASECOMPONENTS, TechnicalComponentReleaseTypeQu.getInstance(), true));
    addAssociation(new TypeWithJoinProperty(ASSOCIATION_PARENTCOMPONENTS, TechnicalComponentReleaseTypeQu.getInstance(), true));
  }

  public QueryType getQueryType() {
    return getInstance();
  }

}
