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
package de.iteratec.iteraplan.presentation.dialog.Dashboard;

import java.util.Map;

import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.presentation.memory.DialogMemory;


public class DashboardDialogMemory extends DialogMemory {

  /** Serialization version. */
  private static final long                                                 serialVersionUID = -1619260582109776185L;
  private Map<String, Integer>                                              bbMap;
  private Map<String, Integer>                                              isrStatusMap;
  private Map<String, Integer>                                              tcrStatusMap;
  private Map<String, Integer>                                              isrSealStateMap;
  private Map<TechnicalComponentRelease, Integer>                           topUsedTcrMap;
  private Map<ArchitecturalDomain, Map<TechnicalComponentRelease, Integer>> topUsedTcrByAdMap;
  private Map<InformationSystemRelease, Integer>                            topUsedIsrMap;

  public Map<String, Integer> getTcrStatusMap() {
    return tcrStatusMap;
  }

  public void setTcrStatusMap(Map<String, Integer> tcrStatusMap) {
    this.tcrStatusMap = tcrStatusMap;
  }

  public Map<String, Integer> getIsrStatusMap() {
    return isrStatusMap;
  }

  public void setIsrStatusMap(Map<String, Integer> isrStatusMap) {
    this.isrStatusMap = isrStatusMap;
  }

  public Map<String, Integer> getBbMap() {
    return bbMap;
  }

  public void setBbMap(Map<String, Integer> bbMap) {
    this.bbMap = bbMap;
  }

  public Map<TechnicalComponentRelease, Integer> getTopUsedTcrMap() {
    return topUsedTcrMap;
  }

  public void setTopUsedTcrMap(Map<TechnicalComponentRelease, Integer> topUsedTcr) {
    this.topUsedTcrMap = topUsedTcr;
  }

  public Map<ArchitecturalDomain, Map<TechnicalComponentRelease, Integer>> getTopUsedTcrByAdMap() {
    return topUsedTcrByAdMap;
  }

  public void setTopUsedTcrByAdMap(Map<ArchitecturalDomain, Map<TechnicalComponentRelease, Integer>> topUsedTcrByAD) {
    this.topUsedTcrByAdMap = topUsedTcrByAD;
  }

  public Map<InformationSystemRelease, Integer> getTopUsedIsrMap() {
    return topUsedIsrMap;
  }

  public void setTopUsedIsrMap(Map<InformationSystemRelease, Integer> topUsedIsrMap) {
    this.topUsedIsrMap = topUsedIsrMap;
  }

  public void setIsrSealStateMap(Map<String, Integer> sealStateMap) {
    this.isrSealStateMap = sealStateMap;
  }

  public Map<String, Integer> getIsrSealStateMap() {
    return isrSealStateMap;
  }

}
