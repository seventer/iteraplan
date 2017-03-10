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
package de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.ecore.EntityBeanIntrospectionServiceBean;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * @author mba
 */
public class XmiExportServiceImpl implements XmiExportService {
  private static final Logger                      LOGGER         = Logger.getIteraplanLogger(XmiExportServiceImpl.class);
  private static final String                      ECORE_FILENAME = "iteraplanModel.ecore";
  private static final String                      XMI_PATH       = "./";
  private static final String                      XMI_FILENAME   = "iteraplanData.xmi";

  private final EObjectCreator                     eObjectCreator;
  private final EntityBeanIntrospectionServiceBean introspectionBean;

  public XmiExportServiceImpl(EObjectCreator eObjectCreator, EntityBeanIntrospectionServiceBean introspectionBean) {
    this.eObjectCreator = eObjectCreator;
    this.introspectionBean = introspectionBean;

    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
  }

  /**
   * {@inheritDoc}
   */
  public void serializeModel(OutputStream os) throws IOException {
    Resource resource = createXmiResource();
    EObject rootObject = eObjectCreator.getXmiExportObject();
    resource.getContents().add(rootObject);
    Map<String, Object> options = createSaveOptions();

    resource.save(os, options);
  }

  /**
   * {@inheritDoc}
   */
  public void serializeMetamodel(OutputStream stream) {
    ResourceSet resourceSet = createResourceSet();
    Resource ecoreResource = resourceSet.createResource(URI.createURI(ECORE_FILENAME));
    ecoreResource.getContents().addAll(introspectionBean.getEPackages());
    Map<String, Object> options = createSaveOptions();

    try {
      ecoreResource.save(stream, options);
    } catch (IOException e) {
      throw new IteraplanTechnicalException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public byte[] serializeBundle() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ZipOutputStream zipStream = new ZipOutputStream(out);
    Resource resource = createXmiResource();
    resource.getContents().add(eObjectCreator.getXmiExportObject());
    Map<String, Object> options = createSaveOptions();

    try {
      zipStream.putNextEntry(new ZipEntry(XMI_FILENAME));
      resource.save(zipStream, options);
      saveEcoreModels(zipStream);
      zipStream.closeEntry();
      zipStream.finish();
      return out.toByteArray();
    } catch (IOException e) {
      throw new IteraplanTechnicalException(e);
    }
  }

  /**
   * Saves the ecore models in the specified {@link ZipOutputStream}.
   * 
   * @param zos the zip output stream to save the model in
   */
  private void saveEcoreModels(ZipOutputStream zos) {
    for (Resource buildResource : buildEcoreResources()) {
      try {
        EPackage ePackage = (EPackage) buildResource.getContents().get(0);
        zos.putNextEntry(new ZipEntry(ePackage.getName() + ".ecore"));
        buildResource.save(zos, null);
        zos.closeEntry();
      } catch (IOException e) {
        LOGGER.error("Could not save file (" + buildResource.getURI().path() + ")", e);
      }
    }
  }

  private List<Resource> buildEcoreResources() {
    ResourceSet resourceSetLocal = createResourceSet();

    List<Resource> resources = Lists.newLinkedList();
    for (EPackage ePackage : introspectionBean.getEPackages()) {
      String fileName = ePackage.getName() + ".ecore";
      String pathName = "./" + fileName;
      URI fileURI = URI.createFileURI(pathName);
      Resource createdResource = resourceSetLocal.createResource(fileURI);
      createdResource.getContents().add(ePackage);

      resources.add(createdResource);
    }

    return resources;
  }

  /**
   * Creates the resource to serialize the xmi data.
   * 
   * @return the newly created resource for the xmi data
   */
  private Resource createXmiResource() {
    ResourceSet resourceSet = createResourceSet();

    URI uri = URI.createURI(XMI_PATH + XMI_FILENAME);
    LOGGER.debug("using default path for the output file: {0}", uri.path());

    return resourceSet.createResource(uri);
  }

  /**
   * Creates a new {@link ResourceSet} and registers the namespace uris in its
   * package registry.
   * 
   * @return a newly created {@link ResourceSet}
   */
  private ResourceSet createResourceSet() {
    ResourceSet resourceSet = new ResourceSetImpl();

    for (EPackage ePackage : introspectionBean.getEPackages()) {
      resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
    }

    return resourceSet;
  }

  /**
   * Creates the save options for saving ecore and xmi models.
   * 
   * @return the map containing save options
   */
  private Map<String, Object> createSaveOptions() {
    Map<String, Object> options = Maps.newHashMap();
    options.put(XMLResource.OPTION_ENCODING, "UTF-8");

    return options;
  }
}
