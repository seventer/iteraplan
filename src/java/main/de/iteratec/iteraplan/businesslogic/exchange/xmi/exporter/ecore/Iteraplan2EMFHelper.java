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
package de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.ecore;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.springframework.core.io.ClassPathResource;

import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * Class to provide static access to an {@link EClass} which can be used to create an 
 * instance which serves as a root element for the iteraplan XMI export.
 */
public final class Iteraplan2EMFHelper {

  /** The name of the predefined ecore model. */
  public static final String LOCATION_OF_ECORE           = "IteraplanModelForTabularReporting.ecore";
  public static final String CLASS_NAME                  = "IteraplanXMIExport";
  public static final String CLASS_NAME_TABULARREPORTING = "IteraplanXMLExportForTabularReporting";
  public static final String EREFERENCE_NAME             = "content";

  /** empty private constructor */
  private Iteraplan2EMFHelper() {
    // hide constructor
  }

  /**
   * Generate the {@link EClass}
   * @return the {@link EClass}
   */
  public static EClass getEContainerClass() {
    EReference containmentReference = EcoreFactory.eINSTANCE.createEReference();
    containmentReference.setName(EREFERENCE_NAME);
    containmentReference.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
    containmentReference.setUnique(true);
    containmentReference.setEType(EcoreFactory.eINSTANCE.createEObject().eClass());
    containmentReference.setContainment(true);

    EClass containerEClass = EcoreFactory.eINSTANCE.createEClass();
    containerEClass.setName(CLASS_NAME);

    containerEClass.getEStructuralFeatures().add(containmentReference);

    return containerEClass;
  }

  /**
   * Generate the {@link EClass} for tabular reporting
   * @return the EClass
   */
  public static EClass getEContainerClassForTabularReporting() {
    EClass eContainerClass = getEContainerClass();
    eContainerClass.setName(CLASS_NAME_TABULARREPORTING);

    return eContainerClass;
  }

  public static EPackage getBasicIteraplanEPackage() {
    //The returned EPackage must not be cached, as the "...ForTabReporting"-Service alter the package.
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
    ResourceSet rs = new ResourceSetImpl();

    try {
      org.springframework.core.io.Resource ecoreFile = new ClassPathResource(LOCATION_OF_ECORE);
      URI uri = URI.createFileURI(ecoreFile.getURI().getPath());
      Resource ecoreResource = rs.createResource(uri);
      ecoreResource.load(null);

      EPackage ePackage = (EPackage) ecoreResource.getContents().get(0);
      ePackage.getEClassifiers().add(Iteraplan2EMFHelper.getEContainerClassForTabularReporting());
      ePackage.setNsURI(ePackage.getNsURI().replaceAll("src/java/main/de/iteratec/iteraplan/xmi", "XMI"));
      ePackage.setNsURI(ePackage.getNsURI().replaceAll(".ecore", "_extended.ecore"));

      return ePackage;
    } catch (IOException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.FILE_NOT_FOUND_EXCEPTION, e);
    }
  }

  public static void setName(ENamedElement eNamedElement, String name) {
    eNamedElement.setName(name);
    StringBuffer escapedName = new StringBuffer();
    boolean isFirst = true;
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
        escapedName.append(c);
      }
      else if (!isFirst && (c == '-' || c == '.' || ('0' <= c && c <= '9'))) {
        escapedName.append(c);
      }
      else {
        escapedName.append('_');
        escapedName.append((int) c);
      }
      isFirst = false;
    }

    EcoreUtil.setAnnotation(eNamedElement, "http:///org/eclipse/emf/ecore/util/ExtendedMetaData", "name", escapedName.toString());

  }
}
