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
package de.iteratec.iteraplan.presentation.ajax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.AttributesComponentModel;
import de.iteratec.iteraplan.presentation.tags.TagUtils;


public final class DojoUtils {

  private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = ISODateTimeFormat.dateTimeNoMillis();
  private static final DateTimeFormatter ISO_DATE_FORMATTER      = ISODateTimeFormat.date();

  private DojoUtils() {
    // Nothing to comment
  }

  /**
   * holds a BusinessUnit and an InformationSystemRelease and allows comparison based on those fields.
   */
  private static class BusinessMappingProjection {
    private final BusinessUnit             businessUnit;
    private final InformationSystemRelease informationSystemRelease;

    public BusinessMappingProjection(BusinessUnit bu, InformationSystemRelease isr) {
      this.businessUnit = bu;
      this.informationSystemRelease = isr;
    }

    @Override
    public boolean equals(Object anObject) {
      if (anObject instanceof BusinessMappingProjection) {
        EqualsBuilder builder = new EqualsBuilder();

        builder.append(this.businessUnit, ((BusinessMappingProjection) anObject).businessUnit);
        builder.append(this.informationSystemRelease, ((BusinessMappingProjection) anObject).informationSystemRelease);

        return builder.isEquals();
      }
      return false;
    }

    @Override
    public int hashCode() {
      int buHash = (businessUnit != null) ? businessUnit.hashCode() : 0;
      int isrHash = (informationSystemRelease != null) ? informationSystemRelease.hashCode() : 0;

      int result = 31;
      int prime = 37;
      result = result * prime + buHash;
      result = result * prime + isrHash;
      return result;
    }
  }

  private static MappingJacksonHttpMessageConverter getConverter() {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule smodule = new SimpleModule("iteraplanModule", new Version(1, 0, 0, null));

    smodule.addSerializer(new JsonSerializers.AtgCmPartSerializer());

    smodule.addSerializer(new JsonSerializers.NumberAVSerializer());
    smodule.addSerializer(new JsonSerializers.DateAVSerializer());
    smodule.addSerializer(new JsonSerializers.TextAVSerializer());
    smodule.addSerializer(new JsonSerializers.EnumAVSerializer());
    smodule.addSerializer(new JsonSerializers.RespAVSerializer());

    smodule.addSerializer(new JsonSerializers.EnumSinglePartSerializer());
    smodule.addSerializer(new JsonSerializers.EnumMultiPartSerializer());
    smodule.addSerializer(new JsonSerializers.RespSinglePartSerializer());
    smodule.addSerializer(new JsonSerializers.RespMultiPartSerializer());
    smodule.addSerializer(new JsonSerializers.NumberPartSerializer());
    smodule.addSerializer(new JsonSerializers.DatePartSerializer());
    smodule.addSerializer(new JsonSerializers.TextPartSerializer());

    mapper.registerModule(smodule);

    MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
    converter.setObjectMapper(mapper);

    return converter;
  }

  /**
   * projects the given BusinessMappings to a connection between InformationSystems and BusinessUnits. Only those BusinessMappings that actually have a BusinessUnit will be taken into account.
   * @param data
   * @param req
   * @return a List containing a Map representing the projection of each BusinessMapping. This map contains the following Objects with the respecting keys: 
   * <ol>
   * <li>informationSystem: The Name of the connected InformationSystem; more exactly the IdentityString, thus with hierarchy and version</li>
   * <li>informationSystemId: The Id of the connected InformationSystem</li>
   * <li>informationSystemURI: The URI of the connected InformationSystem</li>
   * <li>businessUnit: The Name of the connected BusinessUnit</li>
   * <li>businessUnitId: The Id of the connected BusinessUnit</li>
   * <li>businessUnitURI: The URI of the connected BusinessUnit</li>
   * <li>businessMappingIds: The Ids of the BusinessMappings which are projected on this relationship</li> 
   * </ol>
   * 
   */
  public static List<Map<String, Object>> projectBusinessMappingsToBUISRelationship(Collection<BusinessMapping> data, HttpServletRequest req) {

    //stores all relationships between IS and BU and the IDs of the BusinessMappings that represent them 
    Map<BusinessMappingProjection, Set<Integer>> mappingIds = Maps.newHashMap();

    List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

    for (BusinessMapping bm : data) {
      InformationSystemRelease isr = bm.getInformationSystemRelease();
      BusinessUnit bu = bm.getBusinessUnit();

      if (isr == null || bu == null) {
        continue;
      }

      BusinessMappingProjection bmp = new BusinessMappingProjection(bu, isr);

      if (!mappingIds.containsKey(bmp)) {
        Set<Integer> set = Sets.newHashSet();
        mappingIds.put(bmp, set);
      }

      mappingIds.get(bmp).add(bm.getId());
    }

    for (Entry<BusinessMappingProjection, Set<Integer>> e : mappingIds.entrySet()) {
      Map<String, Object> item = Maps.newHashMap();

      InformationSystemRelease isr = e.getKey().informationSystemRelease;

      BusinessUnit bu = e.getKey().businessUnit;

      item.put("informationSystem", isr.getIdentityString());
      item.put("informationSystemId", isr.getId());
      item.put("informationSystemURI", URLBuilder.getEntityURL(isr, req, URLBuilder.EntityRepresentation.JSON));
      item.put("businessUnit", bu.getIdentityString());
      item.put("businessUnitId", bu.getId());
      item.put("businessUnitURI", URLBuilder.getEntityURL(bu, req, URLBuilder.EntityRepresentation.JSON));
      List<Integer> idsSorted = Lists.newArrayList(e.getValue());
      Collections.sort(idsSorted);
      item.put("businessMappingIds", idsSorted);

      items.add(item);

    }

    return items;
  }

  public static Map<String, Object> convertToMap(Collection<? extends BuildingBlock> data, boolean escapeHtml, HttpServletRequest req) {
    List<Map<String, String>> items = new ArrayList<Map<String, String>>();
    for (BuildingBlock bb : data) {
      Map<String, String> item = new HashMap<String, String>();
      item.put("id", String.valueOf(bb.getId().intValue()));
      String name = bb.getIdentityString();
      if (escapeHtml) {
        name = TagUtils.filter(name);
      }
      item.put("name", name);

      LocalDateTime modificationTime = new LocalDateTime(bb.getLastModificationTime());
      item.put("lastmodified", ISO_DATE_TIME_FORMATTER.print(modificationTime));

      String bbJsonUri = URLBuilder.getEntityURL(bb, req, URLBuilder.EntityRepresentation.JSON);
      item.put("elementUri", bbJsonUri);

      items.add(item);
    }

    Map<String, Object> result = new HashMap<String, Object>();
    result.put("identifier", "id");
    result.put("label", "name");
    result.put("items", items);
    return result;
  }

  public static void write(Object data, HttpServletResponse response) throws IOException {
    HttpMessageConverter<Object> converter = getConverter();
    if (!converter.canWrite(data.getClass(), MediaType.APPLICATION_JSON)) {
      throw new IllegalArgumentException("The object cannot be serialized to JSON");
    }

    converter.write(data, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
  }

  public static Map<String, Object> convertToMap(InformationSystemRelease entity, HttpServletRequest req) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("id", entity.getId());
    result.put("name", entity.getNameWithoutVersion());
    result.put("version", entity.getVersion());
    result.put("description", entity.getDescription());

    LocalDateTime modificationTime = new LocalDateTime(entity.getLastModificationTime());
    result.put("lastModified", ISO_DATE_TIME_FORMATTER.print(modificationTime));

    String status = MessageAccess.getString(entity.getTypeOfStatusAsString(), Locale.ENGLISH);
    result.put("status", status);

    if (entity.getRuntimePeriod() != null && entity.getRuntimePeriod().getStart() != null) {
      LocalDateTime startDate = new LocalDateTime(entity.getRuntimePeriod().getStart());
      result.put("startDate", ISO_DATE_FORMATTER.print(startDate));
    }
    if (entity.getRuntimePeriod() != null && entity.getRuntimePeriod().getEnd() != null) {
      LocalDateTime endDate = new LocalDateTime(entity.getRuntimePeriod().getEnd());
      result.put("endDate", ISO_DATE_FORMATTER.print(endDate));
    }

    if (entity.getParent() != null) {
      String parentUri = URLBuilder.getEntityURL(entity.getParent(), req, URLBuilder.EntityRepresentation.JSON);
      result.put("parentUri", parentUri);
    }

    AttributesComponentModel attributesModel = new AttributesComponentModel(ComponentMode.READ, "") {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean showATG(AttributeTypeGroup atg) {
        return true;
      }
    };
    attributesModel.initializeFrom(entity);

    result.put("attributes", attributesModel.getAtgParts());

    Set<String> buURIs = Sets.newTreeSet();

    for (BusinessMapping bm : entity.getBusinessMappings()) {
      if (bm.getBusinessUnit() != null) {
        buURIs.add(URLBuilder.getEntityURL(bm.getBusinessUnit(), req, URLBuilder.EntityRepresentation.JSON));
      }
    }

    if (!buURIs.isEmpty()) {
      result.put("connectedBusinessUnits", buURIs);
    }

    return result;
  }

  /**
   * 
   * @param entity
   * @param req
   * @return a Map containing the following attributes with their respective keys:
   * <ol>
   * <li>"id": the Id of the BusinessUnit</li>
   * <li>"name": the name of the BusinessUnit</li>
   * <li>"description": the description of</li>
   * <li>"lastModified": a String representation of the last modification date/time, formatted by the {@code ISO_DATE_TIME_FORMATTER} </li>
   * <li>"parentUri": the URI of the parent Building Block. Key only exists if there is a parent.</li>
   * <li>"attributes": all attributes of the BusinessUnit</li>
   * <li>"connectedInformationSystems": all InformationSystemReleases that are connected to the BusinessUnit via at least one BusinessMapping</li>
   * </ol>
   */
  public static Map<String, Object> convertToMap(BusinessUnit entity, HttpServletRequest req) {
    Map<String, Object> result = Maps.newHashMap();

    result.put("id", entity.getId());
    result.put("name", entity.getName());

    result.put("description", entity.getDescription());

    LocalDateTime modificationTime = new LocalDateTime(entity.getLastModificationTime());
    result.put("lastModified", ISO_DATE_TIME_FORMATTER.print(modificationTime));

    if (entity.getParent() != null) {
      String parentUri = URLBuilder.getEntityURL(entity.getParent(), req, URLBuilder.EntityRepresentation.JSON);
      result.put("parentUri", parentUri);
    }

    Set<String> isURIs = Sets.newTreeSet();
    for (BusinessMapping bm : entity.getBusinessMappings()) {
      if (bm.getInformationSystemRelease() != null) {
        isURIs.add(URLBuilder.getEntityURL(bm.getInformationSystemRelease(), req, URLBuilder.EntityRepresentation.JSON));
      }
    }

    AttributesComponentModel attributesModel = new AttributesComponentModel(ComponentMode.READ, "") {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean showATG(AttributeTypeGroup atg) {
        return true;
      }
    };
    attributesModel.initializeFrom(entity);

    result.put("attributes", attributesModel.getAtgParts());

    if (!isURIs.isEmpty()) {
      result.put("connectedInformationSystems", isURIs);
    }

    return result;
  }

  /**
   * Calls writeResponse() and sets the response status to ok
   * @param data
   *          Object which should be serialized and written to the response
   * @param request
   * @param response
   */
  public static void doOkResponse(Object data, HttpServletRequest request, HttpServletResponse response) throws IOException {
    write(data, response);
    response.setStatus(HttpServletResponse.SC_OK);
  }

  /**
   * Sets the response status to source not found
   * 
   * @param response
   */
  public static void doNotFoundResponse(HttpServletResponse response) throws IOException {
    response.sendError(HttpServletResponse.SC_NOT_FOUND);
  }

  /**
   * Sets the response status to bad request
   * 
   * @param response
   */
  public static void doBadRequestResponse(HttpServletResponse response) throws IOException {
    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
  }

  /**
   * Sets the response status to bad request
   * 
   * @param response
   */
  public static void doErrorResponse(HttpServletResponse response) throws IOException {
    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  }
}