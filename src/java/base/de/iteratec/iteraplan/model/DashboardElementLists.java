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

import java.util.List;


/**
 * Contains lists of all BBT. Is used to hold element lists, to prevent from loading more then once.
 */
public class DashboardElementLists {
  private List<BusinessDomain>             bdList;
  private List<BusinessProcess>            bpList;
  private List<BusinessFunction>           bfList;
  private List<Product>                    prodList;
  private List<BusinessUnit>               buList;
  private List<BusinessObject>             boList;
  private List<InformationSystemDomain>    isdList;
  private List<InformationSystemRelease>   isrList;
  private List<InformationSystemInterface> isiList;
  private List<ArchitecturalDomain>        adList;
  private List<TechnicalComponentRelease>  tcrList;
  private List<InfrastructureElement>      ieList;
  private List<Project>                    projList;

  public List<BusinessDomain> getBdList() {
    return bdList;
  }

  public void setBdList(List<BusinessDomain> bdList) {
    this.bdList = bdList;
  }

  public List<BusinessProcess> getBpList() {
    return bpList;
  }

  public void setBpList(List<BusinessProcess> bpList) {
    this.bpList = bpList;
  }

  public List<BusinessFunction> getBfList() {
    return bfList;
  }

  public void setBfList(List<BusinessFunction> bfList) {
    this.bfList = bfList;
  }

  public List<Product> getProdList() {
    return prodList;
  }

  public void setProdList(List<Product> prodList) {
    this.prodList = prodList;
  }

  public List<BusinessUnit> getBuList() {
    return buList;
  }

  public void setBuList(List<BusinessUnit> buList) {
    this.buList = buList;
  }

  public List<BusinessObject> getBoList() {
    return boList;
  }

  public void setBoList(List<BusinessObject> boList) {
    this.boList = boList;
  }

  public List<InformationSystemDomain> getIsdList() {
    return isdList;
  }

  public void setIsdList(List<InformationSystemDomain> isdList) {
    this.isdList = isdList;
  }

  public List<InformationSystemRelease> getIsrList() {
    return isrList;
  }

  public void setIsrList(List<InformationSystemRelease> isrList) {
    this.isrList = isrList;
  }

  public List<InformationSystemInterface> getIsiList() {
    return isiList;
  }

  public void setIsiList(List<InformationSystemInterface> isiList) {
    this.isiList = isiList;
  }

  public List<ArchitecturalDomain> getAdList() {
    return adList;
  }

  public void setAdList(List<ArchitecturalDomain> adList) {
    this.adList = adList;
  }

  public List<TechnicalComponentRelease> getTcrList() {
    return tcrList;
  }

  public void setTcrList(List<TechnicalComponentRelease> tcrList) {
    this.tcrList = tcrList;
  }

  public List<InfrastructureElement> getIeList() {
    return ieList;
  }

  public void setIeList(List<InfrastructureElement> ieList) {
    this.ieList = ieList;
  }

  public List<Project> getProjList() {
    return projList;
  }

  public void setProjList(List<Project> projList) {
    this.projList = projList;
  }

}
