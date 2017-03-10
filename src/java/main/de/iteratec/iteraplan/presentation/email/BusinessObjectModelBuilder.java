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
package de.iteratec.iteraplan.presentation.email;

import java.util.List;

import de.iteratec.iteraplan.diffs.model.BusinessObjectChangeset;
import de.iteratec.iteraplan.diffs.model.HistoryBBChangeset;
import de.iteratec.iteraplan.model.BuildingBlock;


public class BusinessObjectModelBuilder extends BuildingBlockModelBuilder {

  private BusinessObjectChangeset changeSet;

  public BusinessObjectModelBuilder(BuildingBlock bb, HistoryBBChangeset changeSet, String applicationBaseUri) {
    super(bb, applicationBaseUri, changeSet);
    if (changeSet != null && ! (changeSet instanceof BusinessObjectChangeset)) {
      throw new IllegalArgumentException("expecting an BusinessObjectChangeSet");
    }

    this.changeSet = (BusinessObjectChangeset)changeSet;
  }

  @Override
  public EmailModel createModel() {
    EmailModel model = super.createModel();

    if (changeSet == null) {
      return model;
    }

    List<EmailModel.Change> changes = model.getChanges();

    if ((changeSet.getParentTo() != null) || (changeSet.getParentFrom() != null)) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName("Parent");
      if ((changeSet.getParentTo() != null) && (changeSet.getParentFrom() == null)) {
        change.setType(EmailModel.ADDED);
        change.setValue(SubscriptionsUtil.extractName(changeSet.getParentTo()));
        change.setLink(this.createLink(changeSet.getParentTo()));
      }
      else if ((changeSet.getParentTo() == null) && (changeSet.getParentFrom() != null)) {
        change.setType(EmailModel.REMOVED);
        change.setValue(SubscriptionsUtil.extractName(changeSet.getParentFrom()));
        change.setLink(this.createLink(changeSet.getParentFrom()));
      }
      else {
        change.setType(EmailModel.CHANGED);
        change.setFrom(SubscriptionsUtil.extractName(changeSet.getParentFrom()));
        change.setTo(SubscriptionsUtil.extractName(changeSet.getParentTo()));
        change.setFromLink(this.createLink(changeSet.getParentFrom()));
        change.setToLink(this.createLink(changeSet.getParentTo()));
      }
      changes.add(change);
    }
    this.addChange(changes, "Child", EmailModel.ADDED, changeSet.getChildrenAdded());
    this.addChange(changes, "Child", EmailModel.REMOVED, changeSet.getChildrenRemoved());
    this.addChange(changes, "Business domain", EmailModel.ADDED, changeSet.getBusinessDomainsAdded());
    this.addChange(changes, "Business domain", EmailModel.REMOVED, changeSet.getBusinessDomainsRemoved());
    this.addChange(changes, "Business function", EmailModel.ADDED, changeSet.getBusinessFunctionsAdded());
    this.addChange(changes, "Business function", EmailModel.REMOVED, changeSet.getBusinessFunctionsRemoved());

    if ((changeSet.getGeneralisationTo() != null) || (changeSet.getGeneralisationFrom() != null)) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName("Generalisation");
      if ((changeSet.getGeneralisationTo() != null) && (changeSet.getGeneralisationFrom() == null)) {
        change.setType(EmailModel.ADDED);
        change.setValue(SubscriptionsUtil.extractName(changeSet.getGeneralisationTo()));
        change.setLink(this.createLink(changeSet.getGeneralisationTo()));
      }
      else if ((changeSet.getGeneralisationTo() == null) && (changeSet.getGeneralisationFrom() != null)) {
        change.setType(EmailModel.REMOVED);
        change.setValue(SubscriptionsUtil.extractName(changeSet.getGeneralisationFrom()));
        change.setLink(this.createLink(changeSet.getGeneralisationFrom()));
      }
      else {
        change.setType(EmailModel.CHANGED);
        change.setFrom(SubscriptionsUtil.extractName(changeSet.getGeneralisationFrom()));
        change.setTo(SubscriptionsUtil.extractName(changeSet.getGeneralisationTo()));
        change.setLink(this.createLink(changeSet.getGeneralisationTo()));
      }
      changes.add(change);
    }
    this.addChange(changes, "Specialisation", EmailModel.ADDED, changeSet.getSpecialisationsAdded());
    this.addChange(changes, "Specialisation", EmailModel.REMOVED, changeSet.getSpecialisationsRemoved());
    this.addChange(changes, "Information system release", EmailModel.ADDED, changeSet.getInformationSystemReleasesAdded());
    this.addChange(changes, "Information system release", EmailModel.REMOVED, changeSet.getInformationSystemReleasesRemoved());
    this.addChange(changes, "Interface", EmailModel.ADDED, changeSet.getInformationSystemInterfacesAdded());
    this.addChange(changes, "Interface", EmailModel.REMOVED, changeSet.getInformationSystemInterfacesRemoved());

    return model;
  }
}
