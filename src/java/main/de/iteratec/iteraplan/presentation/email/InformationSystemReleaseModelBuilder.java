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
import java.util.Locale;

import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.diffs.model.HistoryBBChangeset;
import de.iteratec.iteraplan.diffs.model.InformationSystemReleaseChangeset;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.presentation.email.EmailModel.Change;


public class InformationSystemReleaseModelBuilder extends BuildingBlockModelBuilder {

  private InformationSystemReleaseChangeset changeSet;

  public InformationSystemReleaseModelBuilder(BuildingBlock bb, HistoryBBChangeset changeSet, String applicationBaseUri) {
    super(bb, applicationBaseUri, changeSet);
    if (changeSet != null && ! (changeSet instanceof InformationSystemReleaseChangeset)) {
      throw new IllegalArgumentException("expecting an InformationSystemReleaseChangeSet");
    }
    this.changeSet = (InformationSystemReleaseChangeset) changeSet;
  }

  @Override
  public EmailModel createModel() {
    EmailModel model = super.createModel();

    if (changeSet == null) {
      return model;
    }

    List<EmailModel.Change> changes = model.getChanges();
    addChanges(changes);

    applyChangesFromBusinessMappings(changes, changeSet.getBusinessMappingsAdded(), EmailModel.ADDED);

    applyChangesFromBusinessMappings(changes, changeSet.getBusinessMappingsRemoved(), EmailModel.REMOVED);

    return model;
  }

  /**
   * @param changes
   */
  private void addChanges(List<Change> changes) {
    this.addParent(changes);
    this.addStatus(changes);
    this.addRuntimeFrom(changes);
    this.addRuntimeTo(changes);

    this.addChange(changes, "Child", EmailModel.ADDED, changeSet.getChildrenAdded());
    this.addChange(changes, "Child", EmailModel.REMOVED, changeSet.getChildrenRemoved());
    this.addChange(changes, "Successor", EmailModel.ADDED, changeSet.getSuccessorsAdded());
    this.addChange(changes, "Successor", EmailModel.REMOVED, changeSet.getSuccessorsRemoved());
    this.addChange(changes, "Predecessor", EmailModel.ADDED, changeSet.getPredecessorsAdded());
    this.addChange(changes, "Predecessor", EmailModel.REMOVED, changeSet.getPredecessorsRemoved());
    this.addChange(changes, "Information system domain", EmailModel.ADDED, changeSet.getInformationSystemDomainsAdded());
    this.addChange(changes, "Information system domain", EmailModel.REMOVED, changeSet.getInformationSystemDomainsRemoved());
    this.addChange(changes, "Uses Information System", EmailModel.ADDED, changeSet.getBaseComponentsAdded());
    this.addChange(changes, "Uses Information System", EmailModel.REMOVED, changeSet.getBaseComponentsRemoved());
    this.addChange(changes, "Used by Information System", EmailModel.ADDED, changeSet.getParentComponentsAdded());
    this.addChange(changes, "Used by Information System", EmailModel.REMOVED, changeSet.getParentComponentsRemoved());
    this.addChange(changes, "Technical component release", EmailModel.ADDED, changeSet.getTechnicalComponentReleasesAdded());
    this.addChange(changes, "Technical component release", EmailModel.REMOVED, changeSet.getTechnicalComponentReleasesRemoved());
    this.addChange(changes, "Infrastructure element", EmailModel.ADDED, changeSet.getInfrastructureElementsAdded());
    this.addChange(changes, "Infrastructure element", EmailModel.REMOVED, changeSet.getInfrastructureElementsRemoved());
    this.addChange(changes, "Project", EmailModel.ADDED, changeSet.getProjectsAdded());
    this.addChange(changes, "Project", EmailModel.REMOVED, changeSet.getProjectsRemoved());
    this.addChange(changes, "Business object", EmailModel.ADDED, changeSet.getBusinessObjectsAdded());
    this.addChange(changes, "Business object", EmailModel.REMOVED, changeSet.getBusinessObjectsRemoved());
    this.addChange(changes, "Business function", EmailModel.ADDED, changeSet.getBusinessFunctionsAdded());
    this.addChange(changes, "Business function", EmailModel.REMOVED, changeSet.getBusinessFunctionsRemoved());
    this.addChange(changes, "Interfaces", EmailModel.ADDED, changeSet.getInterfaceAdded());
    this.addChange(changes, "Interfaces", EmailModel.REMOVED, changeSet.getInterfaceRemoved());
  }

  private void addStatus(List<EmailModel.Change> changes) {
    if ((changeSet.getStatusTo() != null) || (changeSet.getStatusFrom() != null)) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName("Status");
      if ((changeSet.getStatusTo() != null) && (changeSet.getStatusFrom() == null)) {
        change.setType(EmailModel.ADDED);
        change.setValue(MessageAccess.getString(changeSet.getStatusTo(), Locale.ENGLISH));
      }
      else if ((changeSet.getStatusTo() == null) && (changeSet.getStatusFrom() != null)) {
        change.setType(EmailModel.REMOVED);
        change.setValue(MessageAccess.getString(changeSet.getStatusFrom(), Locale.ENGLISH));
      }
      else {
        change.setType(EmailModel.CHANGED);
        change.setFrom(MessageAccess.getString(changeSet.getStatusFrom(), Locale.ENGLISH));
        change.setTo(MessageAccess.getString(changeSet.getStatusTo(), Locale.ENGLISH));
      }
      changes.add(change);
    }
  }

  private void addRuntimeFrom(List<EmailModel.Change> changes) {
    if ((changeSet.getRuntimeStartAdded() != null) || (changeSet.getRuntimeStartRemoved() != null)) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName("Runtime from");
      if ((changeSet.getRuntimeStartAdded() != null) && (changeSet.getRuntimeStartRemoved() == null)) {
        change.setType(EmailModel.ADDED);
        change.setValue(changeSet.getRuntimeStartAdded());
      }
      else if ((changeSet.getRuntimeStartAdded() == null) && (changeSet.getRuntimeStartRemoved() != null)) {
        change.setType(EmailModel.REMOVED);
        change.setValue(changeSet.getRuntimeStartRemoved());
      }
      else {
        change.setType(EmailModel.CHANGED);
        change.setFrom(changeSet.getRuntimeStartRemoved());
        change.setTo(changeSet.getRuntimeStartAdded());
      }
      changes.add(change);
    }
  }

  private void addRuntimeTo(List<EmailModel.Change> changes) {
    if ((changeSet.getRuntimeEndAdded() != null) || (changeSet.getRuntimeEndRemoved() != null)) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName("Runtime to");
      if ((changeSet.getRuntimeEndAdded() != null) && (changeSet.getRuntimeEndRemoved() == null)) {
        change.setType(EmailModel.ADDED);
        change.setValue(changeSet.getRuntimeEndAdded());
      }
      else if ((changeSet.getRuntimeEndAdded() == null) && (changeSet.getRuntimeEndRemoved() != null)) {
        change.setType(EmailModel.REMOVED);
        change.setValue(changeSet.getRuntimeEndRemoved());
      }
      else {
        change.setType(EmailModel.CHANGED);
        change.setFrom(changeSet.getRuntimeEndRemoved());
        change.setTo(changeSet.getRuntimeEndAdded());
      }
      changes.add(change);
    }
  }

  private void addParent(List<EmailModel.Change> changes) {
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
  }
}
