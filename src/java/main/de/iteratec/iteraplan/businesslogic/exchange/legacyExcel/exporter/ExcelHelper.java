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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Release;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.sorting.ExtendedHierarchicalEntityComparator;
import de.iteratec.iteraplan.model.sorting.IdentityStringComparator;
import de.iteratec.iteraplan.model.user.User;


/**
 * This class provided methods which can be used while creating a new excel sheet.
 */
public final class ExcelHelper {

  private ExcelHelper() {
    // no instance needed. Only providing static methods
  }

  /**
   * Concatenates hierarchical names of the given <code>elements</code> by using
   * <code>separator</code>. The <code>elements</code> are sorted before concatenation using
   * {@link #sortEntities(Collection, Comparator)}. <br/>
   * 
   * @param elements
   * @param separator
   * @return the result string or an empty string if <code>elements</code> is <code>null</code> or
   *         empty
   */
  public static String concatMultipleHierarchicalNames(Set<? extends BuildingBlock> elements, String separator) {
    if ((elements == null) || elements.isEmpty()) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    List<? extends BuildingBlock> sortedElements = sortEntities(elements, new ExtendedHierarchicalEntityComparator());
    for (Iterator<? extends BuildingBlock> it = sortedElements.iterator(); it.hasNext();) {
      BuildingBlock bb = it.next();
      if (isVirtualElement(bb)) {
        continue;
      }
      String hierarchicalName = bb.getHierarchicalName();
      if (hierarchicalName != null) {
        sb.append(hierarchicalName);
        if (it.hasNext()) {
          sb.append(separator);
        }
      }
    }
    return sb.toString();
  }

  /**
   * Concatenates names of the given <code>elements</code> by using <code>separator</code>. The
   * <code>elements</code> are sorted before concatenation using
   * {@link #sortEntities(Collection, Comparator)}.
   * 
   * @param elements
   * @param separator
   * @return the result string or an empty string if <code>elements</code> is <code>null</code> or
   *         empty
   */
  @SuppressWarnings("rawtypes")
  public static String concatMultipleNames(Set<? extends AbstractHierarchicalEntity> elements, String separator) {
    if ((elements == null) || elements.isEmpty()) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    List<? extends AbstractHierarchicalEntity> sortedElements = sortEntities(elements, new ExtendedHierarchicalEntityComparator());
    for (Iterator<? extends AbstractHierarchicalEntity> it = sortedElements.iterator(); it.hasNext();) {
      AbstractHierarchicalEntity entity = it.next();
      if (isVirtualElement(entity)) {
        continue;
      }
      if (entity.getName() != null) {
        sb.append(entity.getName());
        if (it.hasNext()) {
          sb.append(separator);
        }
      }
    }
    return sb.toString();
  }

  public static String concatMultipleUsers(Set<? extends User> elements, String separator) {
    if ((elements == null) || elements.isEmpty()) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    List<? extends User> sortedElements = sortEntities(elements, new IdentityStringComparator());
    for (Iterator<? extends User> it = sortedElements.iterator(); it.hasNext();) {
      User entity = it.next();
      if (entity.getIdentityString() != null) {
        sb.append(entity.getIdentityString());
        if (it.hasNext()) {
          sb.append(separator);
        }
      }
    }
    return sb.toString();
  }

  /**
   * Concatenates release names of the given <code>elements</code> by using <code>separator</code>.
   * The <code>elements</code> are sorted before concatenation using
   * {@link #sortEntities(Collection, Comparator)}.
   * 
   * @param elements
   * @param separator
   * @return the result string or an empty string if <code>elements</code> is <code>null</code> or
   *         empty
   */
  public static String concatMultipleReleaseNames(Set<? extends Release> elements, String separator) {
    if ((elements == null) || elements.isEmpty()) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    List<? extends Release> sortedElements = sortEntities(elements, new ExtendedHierarchicalEntityComparator());
    for (Iterator<? extends Release> it = sortedElements.iterator(); it.hasNext();) {
      Release release = it.next();
      if (release.getReleaseName() != null) {
        sb.append(release.getReleaseName());
        if (it.hasNext()) {
          sb.append(separator);
        }
      }
    }
    return sb.toString();
  }

  /**
   * Concatenates all {@link BusinessProcess}es, {@link BusinessUnit}s and {@link Product}s
   * connected via the given {@link BusinessMapping}s. All triples are sorted before concatenation
   * in one final string.<br/>
   * The required String are used to separate the single values in the following way: e.g.
   * (<i>business process</i> <i>product</i> <i>business unit</i>) ; (<i>business process</i>
   * <i>product</i> <i>business unit</i>)...
   * <ul>
   * <li><code>unitOpener</code> is '('</li>
   * <li><code>unitSeparator</code> is ' '</li>
   * <li><code>unitCloser</code> is ')'</li>
   * <li><code>inlineSeparator</code> is ' ; '</li>
   * </ul>
   * 
   * @param bmList
   *          all {@link BusinessMapping}s
   * @return all associated information or an empty String if <code>bmList</code> is
   *         <code>null</code> or empty
   */
  public static String concatBPandProdandBU(Set<BusinessMapping> bmList, String unitOpener, String unitSeparator, String unitCloser,
                                            String inlineSeparator) {
    return concatBMContents(bmList, true, true, true, false, unitOpener, unitSeparator, unitCloser, inlineSeparator);
  }

  /**
   * Concatenates all {@link BusinessProcess}es, {@link Product}s and
   * {@link InformationSystemRelease}s connected via the given {@link BusinessMapping}s. All triples
   * are sorted before concatenation in one final string.<br/>
   * The required String are used to separate the single values in the following way: e.g.
   * (<i>business process</i> <i>product</i> <i>information system release</i>) ; (<i>business
   * process</i> <i>product</i> <i>information system release</i>)...
   * <ul>
   * <li><code>unitOpener</code> is '('</li>
   * <li><code>unitSeparator</code> is ' '</li>
   * <li><code>unitCloser</code> is ')'</li>
   * <li><code>inlineSeparator</code> is ' ; '</li>
   * </ul>
   * 
   * @param bmList
   *          all {@link BusinessMapping}s
   * @return all associated information or an empty String if <code>bmList</code> is
   *         <code>null</code> or empty
   */
  public static String concatBPandProdandISR(Set<BusinessMapping> bmList, String unitOpener, String unitSeparator, String unitCloser,
                                             String inlineSeparator) {
    return concatBMContents(bmList, true, true, false, true, unitOpener, unitSeparator, unitCloser, inlineSeparator);
  }

  /**
   * Concatenates all {@link BusinessProcess}es, {@link BusinessUnit}s and
   * {@link InformationSystemRelease}s connected via the given {@link BusinessMapping}s. All triples
   * are sorted before concatenation in one final string.<br/>
   * The required String are used to separate the single values in the following way: e.g.
   * (<i>business process</i> <i>business unit</i> <i>information system release</i>) ; (<i>business
   * process</i> <i>business unit</i> <i>information system release</i>)...
   * <ul>
   * <li><code>unitOpener</code> is '('</li>
   * <li><code>unitSeparator</code> is ' '</li>
   * <li><code>unitCloser</code> is ')'</li>
   * <li><code>inlineSeparator</code> is ' ; '</li>
   * </ul>
   * 
   * @param bmList
   *          all {@link BusinessMapping}s
   * @return all associated information or an empty String if <code>bmList</code> is
   *         <code>null</code> or empty
   */
  public static String concatBPandBUandISR(Set<BusinessMapping> bmList, String unitOpener, String unitSeparator, String unitCloser,
                                           String inlineSeparator) {
    return concatBMContents(bmList, true, false, true, true, unitOpener, unitSeparator, unitCloser, inlineSeparator);
  }

  /**
   * Concatenates all {@link Product}s, {@link BusinessUnit}s and {@link InformationSystemRelease}s
   * connected via the given {@link BusinessMapping}s. All triples are sorted before concatenation
   * in one final string.<br/>
   * The required String are used to separate the single values in the following way: e.g. (
   * <i>product</i> <i>business unit</i> <i>information system release</i>) ; (<i>product</i>
   * <i>business unit</i> <i>information system release</i>)...
   * <ul>
   * <li><code>unitOpener</code> is '('</li>
   * <li><code>unitSeparator</code> is ' '</li>
   * <li><code>unitCloser</code> is ')'</li>
   * <li><code>inlineSeparator</code> is ' ; '</li>
   * </ul>
   * 
   * @param bmList
   *          all {@link BusinessMapping}s
   * @return all associated information or an empty String if <code>bmList</code> is
   *         <code>null</code> or empty
   */
  public static String concatProdandBuandISR(Set<BusinessMapping> bmList, String unitOpener, String unitSeparator, String unitCloser,
                                             String inlineSeparator) {
    return concatBMContents(bmList, false, true, true, true, unitOpener, unitSeparator, unitCloser, inlineSeparator);
  }

  /**
   * Concatenates names of all {@link InformationSystemRelease}s connected by the given interfaces
   * in <code>elements</code>. The {@link InformationSystemRelease}s are connected by the given
   * <code>interfaceSeparator</code>. <code>separator</code> is used between different interfaces.<br/>
   * No output is made if an interface has no corresponding {@link InformationSystemRelease}s.<br/>
   * The <code>elements</code> are sorted before concatenation using
   * {@link #sortEntities(Collection, Comparator)}.
   * 
   * @param elements
   * @param interfaceSeparator
   * @param separator
   * @return the result string or an empty string if <code>elements</code> is <code>null</code> or
   *         empty
   */
  public static String concatISINamesBothWays(Set<InformationSystemInterface> elements, String interfaceSeparator, String separator) {
    if ((elements == null) || elements.isEmpty()) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    List<InformationSystemInterface> sortedElements = sortEntities(elements, new ExtendedHierarchicalEntityComparator());
    for (Iterator<InformationSystemInterface> it = sortedElements.iterator(); it.hasNext();) {
      InformationSystemInterface isi = it.next();
      if (isVirtualElement(isi)) {
        continue;
      }
      if ((isi.getInformationSystemReleaseA() != null) && (isi.getInformationSystemReleaseB() != null)) {
        sb.append(isi.getInformationSystemReleaseA().getHierarchicalName());
        sb.append(interfaceSeparator);
        sb.append(isi.getInformationSystemReleaseB().getHierarchicalName());
        if (it.hasNext()) {
          sb.append(separator);
        }
      }
    }
    return sb.toString();
  }

  /**
   * Retrieves all {@link BusinessProcess}es connected via the given <code>bmList</code>.
   * 
   * @param bmList
   *          the list of {@link BusinessMapping}s
   * @return the set with all connected {@link BusinessProcess}es or an empty set, if no processes
   *         exist or <code>bmList</code> is <code>null</code> or empty.
   */
  public static Set<BusinessProcess> retrieveBusinessProcesses(Set<BusinessMapping> bmList) {
    Set<BusinessProcess> allBPs = new HashSet<BusinessProcess>();
    if ((bmList == null) || bmList.isEmpty()) {
      return allBPs;
    }
    for (BusinessMapping bm : bmList) {
      BusinessProcess bp = bm.getBusinessProcess();
      if ((bp != null) && !isVirtualElement(bp)) {
        allBPs.add(bp);
      }
    }
    return allBPs;
  }

  /**
   * Retrieves all {@link BusinessUnit}s connected via the given <code>bmList</code>.
   * 
   * @param bmList
   *          the list of {@link BusinessMapping}s
   * @return the set with all connected {@link BusinessUnit}s or an empty set, if no units exist or
   *         <code>bmList</code> is <code>null</code> or empty.
   */
  public static Set<BusinessUnit> retrieveBusinessUnits(Set<BusinessMapping> bmList) {
    Set<BusinessUnit> allBUs = new HashSet<BusinessUnit>();
    if ((bmList == null) || bmList.isEmpty()) {
      return allBUs;
    }
    for (BusinessMapping bm : bmList) {
      BusinessUnit bu = bm.getBusinessUnit();
      if ((bu != null) && !isVirtualElement(bu)) {
        allBUs.add(bu);
      }
    }
    return allBUs;
  }

  /**
   * Retrieves all {@link InformationSystemRelease}s connected via the given <code>bmList</code>.
   * 
   * @param bmList
   *          the list of {@link BusinessMapping}s
   * @return the set with all connected {@link InformationSystemRelease}s or an empty set, if no
   *         ISRs exist or <code>bmList</code> is <code>null</code> or empty.
   */
  public static Set<InformationSystemRelease> retrieveInformationSystemReleases(Set<BusinessMapping> bmList) {
    Set<InformationSystemRelease> allISRs = new HashSet<InformationSystemRelease>();
    if ((bmList == null) || bmList.isEmpty()) {
      return allISRs;
    }
    for (BusinessMapping bm : bmList) {
      InformationSystemRelease isr = bm.getInformationSystemRelease();
      if ((isr != null) && !isVirtualElement(isr)) {
        allISRs.add(isr);
      }
    }
    return allISRs;
  }

  /**
   * Retrieves all {@link InformationSystemInterface}s connected to elements in the given
   * <code>tList</code>.
   * 
   * @param tList
   *          the list of {@link Transport}s
   * @return the set with all connected {@link InformationSystemInterface}s or an empty set, if no
   *         ISIs exist or <code>tList</code> is <code>null</code> or empty.
   */
  public static Set<InformationSystemInterface> retrieveInformationSystemInterfaces(Set<Transport> tList) {
    Set<InformationSystemInterface> allISIs = new HashSet<InformationSystemInterface>();
    if ((tList == null) || tList.isEmpty()) {
      return allISIs;
    }
    for (Transport transport : tList) {
      InformationSystemInterface isi = transport.getInformationSystemInterface();
      if ((isi != null) && !isVirtualElement(isi)) {
        allISIs.add(isi);
      }
    }
    return allISIs;
  }

  /**
   * Retrieves all {@link BusinessObject}s connected to elements in the given <code>tList</code>.
   * 
   * @param tList
   *          the list of {@link Transport}s
   * @return the set with all connected {@link BusinessObject}s or an empty set, if no BOs exist or
   *         <code>tList</code> is <code>null</code> or empty.
   */
  public static Set<BusinessObject> retrieveBusinessObjects(Set<Transport> tList) {
    Set<BusinessObject> allBOs = new HashSet<BusinessObject>();
    if ((tList == null) || tList.isEmpty()) {
      return allBOs;
    }
    for (Transport transport : tList) {
      BusinessObject bo = transport.getBusinessObject();
      if ((bo != null) && !isVirtualElement(bo)) {
        allBOs.add(bo);
      }
    }
    return allBOs;
  }

  /**
   * Retrieves all {@link Product}s connected via the given <code>bmList</code>.
   * 
   * @param bmList
   *          the list of {@link BusinessMapping}s
   * @return the set with all connected {@link Product}s or an empty set, if no products exist or
   *         <code>bmList</code> is <code>null</code> or empty.
   */
  public static Set<Product> retrieveProducts(Set<BusinessMapping> bmList) {
    Set<Product> allProducts = new HashSet<Product>();
    if ((bmList == null) || bmList.isEmpty()) {
      return allProducts;
    }
    for (BusinessMapping bm : bmList) {
      Product prod = bm.getProduct();
      if ((prod != null) && !isVirtualElement(prod)) {
        allProducts.add(prod);
      }
    }
    return allProducts;
  }

  /**
   * Extracts multiple contents connected with the given <code>inputBMs</code>. The contents will be
   * stored in the parameter-{@link Set}s. No action is done if <code>inputBMs</code> is
   * <code>null</code>. If any of the other parameters are not required to be filled they can be
   * provided as <code>null</code>.<br/>
   * <b>NOTE</b>: use this method instead of single <code>retrieve</code>-methods if you require all
   * contents at one time (due to efficiency issues).
   * 
   * @param inputBMs
   *          the list with mappings to be iterated
   * @param allBPs
   *          all found {@link BusinessProcess}s will be stored here, if not <code>null</code>.
   * @param allBUs
   *          all found {@link BusinessUnit}s will be stored here, if not <code>null</code>.
   * @param allISRs
   *          all found {@link InformationSystemRelease}s will be stored here, if not
   *          <code>null</code>.
   * @param allProducts
   *          all found {@link Product}s will be stored here, if not <code>null</code>.
   */
  public static void extractContentsFromBMs(Set<BusinessMapping> inputBMs, Set<BusinessProcess> allBPs, Set<BusinessUnit> allBUs,
                                            Set<InformationSystemRelease> allISRs, Set<Product> allProducts) {
    if (inputBMs == null) {
      return;
    }
    for (BusinessMapping bm : inputBMs) {
      extractBPs(allBPs, bm);
      extractBUs(allBUs, bm);
      extractISRs(allISRs, bm);
      extractProducts(allProducts, bm);
    }

  }

  private static void extractProducts(Set<Product> allProducts, BusinessMapping bm) {
    if ((allProducts != null) && (bm.getProduct() != null)) {
      allProducts.add(bm.getProduct());
    }
  }

  private static void extractISRs(Set<InformationSystemRelease> allISRs, BusinessMapping bm) {
    if ((allISRs != null) && (bm.getInformationSystemRelease() != null) && !isVirtualElement(bm.getInformationSystemRelease())) {
      allISRs.add(bm.getInformationSystemRelease());
    }
  }

  private static void extractBUs(Set<BusinessUnit> allBUs, BusinessMapping bm) {
    if ((allBUs != null) && (bm.getBusinessUnit() != null) && !isVirtualElement(bm.getBusinessUnit())) {
      allBUs.add(bm.getBusinessUnit());
    }
  }

  private static void extractBPs(Set<BusinessProcess> allBPs, BusinessMapping bm) {
    if ((allBPs != null) && (bm.getBusinessProcess() != null) && !isVirtualElement(bm.getBusinessProcess())) {
      allBPs.add(bm.getBusinessProcess());
    }
  }

  /**
   * Sorts elements provided in <code>entities</code>. For comparison
   * {@link ExtendedHierarchicalEntityComparator} is used.
   * 
   * @param entities
   *          elements to be sorted
   * @return sorted list.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static <T> List<T> sortEntities(Collection<T> entities, Comparator comparator) {
    List<T> entityList = new ArrayList<T>(entities);
    Collections.sort((List) entityList, comparator);
    return entityList;
  }

  /**
   * Tests if the given <code>bb</code> is a virtual element by comparing its hierarchical name and
   * {@link AbstractHierarchicalEntity#TOP_LEVEL_NAME}.
   * 
   * @param bb
   *          the entity to be tested
   * @return <code>true</code> if <code>bb</code> is virtual else <code>false</code> is returned
   */
  public static boolean isVirtualElement(BuildingBlock bb) {
    return bb.getHierarchicalName().equals(AbstractHierarchicalEntity.TOP_LEVEL_NAME);
  }

  private static String concatBMContents(Set<BusinessMapping> bmList, boolean bpRequired, boolean prodRequired, boolean buRequired,
                                         boolean isrRequired, String unitOpener, String unitSeparator, String unitCloser, String inlineSeparator) {
    if ((bmList == null) || bmList.isEmpty()) {
      return "";
    }
    List<String> allEntities = new ArrayList<String>();
    for (BusinessMapping bm : bmList) {
      // a.t.m.
      if (!isVirtualElement(bm)) {
        StringBuilder sb = new StringBuilder();
        sb.append(unitOpener);
        sb.append(getBpString(bpRequired, unitSeparator, bm));
        sb.append(getProdString(prodRequired, unitSeparator, bm));
        sb.append(getBuString(buRequired, isrRequired, unitSeparator, bm));
        sb.append(getIsrString(isrRequired, bm));
        sb.append(unitCloser);
        allEntities.add(sb.toString());
      }
    }

    // for better visualization the retrieved list is sorted before the final concatenation
    Collections.sort(allEntities);
    return GeneralHelper.makeConcatenatedStringWithSeparator(allEntities, inlineSeparator);
  }

  private static String getIsrString(boolean isrRequired, BusinessMapping bm) {
    StringBuilder sb = new StringBuilder();
    if (isrRequired && (bm.getInformationSystemRelease() != null)) {
      sb.append(bm.getInformationSystemRelease());
    }
    return sb.toString();
  }

  private static String getBuString(boolean buRequired, boolean isrRequired, String unitSeparator, BusinessMapping bm) {
    StringBuilder sb = new StringBuilder();
    if (buRequired && (bm.getBusinessUnit() != null)) {
      sb.append(bm.getBusinessUnit().getHierarchicalName());
      if (isrRequired && (bm.getInformationSystemRelease() != null)) {
        // otherwise a unit closer occurs directly after a unit separator without an
        // information system release in between
        sb.append(unitSeparator);
      }
    }
    return sb.toString();
  }

  private static String getProdString(boolean prodRequired, String unitSeparator, BusinessMapping bm) {
    StringBuilder sb = new StringBuilder();
    if (prodRequired && (bm.getProduct() != null)) {
      sb.append(bm.getProduct().getHierarchicalName());
      sb.append(unitSeparator);
    }
    return sb.toString();
  }

  private static String getBpString(boolean bpRequired, String unitSeparator, BusinessMapping bm) {
    StringBuilder sb = new StringBuilder();
    if (bpRequired && (bm.getBusinessProcess() != null)) {
      sb.append(bm.getBusinessProcess().getHierarchicalName());
      sb.append(unitSeparator);
    }
    return sb.toString();
  }
}
