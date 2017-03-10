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
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.AbstractAttributeTypeComponentModelPartBase;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.AttributeTypeGroupComponentModelPart;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.DateAttributeTypeComponentModelPart;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.EnumAttributeTypeMultiComponentModelPart;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.EnumAttributeTypeSingleComponentModelPart;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.NumberAttributeTypeComponentModelPart;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.ResponsibilityAttributeTypeMultiComponentModelPart;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.ResponsibilityAttributeTypeSingleComponentModelPart;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.TextAttributeTypeComponentModelPart;

public class JsonSerializers {

  public static class NumberAVSerializer extends SerializerBase<NumberAV> {

    public NumberAVSerializer() {
      super(NumberAV.class);
    }

    /**{@inheritDoc}**/
    @Override
    public void serialize(NumberAV value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
    JsonProcessingException {

      jgen.writeNumber(value.getValue());
    }

  }

  public static class EnumAVSerializer extends SerializerBase<EnumAV> {

    public EnumAVSerializer() {
      super(EnumAV.class);
    }

    /**{@inheritDoc}**/
    @Override
    public void serialize(EnumAV value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
    JsonProcessingException {

      jgen.writeString(value.getName());
    }
  }

  public static class RespAVSerializer extends SerializerBase<ResponsibilityAV> {

    public RespAVSerializer() {
      super(ResponsibilityAV.class);
    }

    /**{@inheritDoc}**/
    @Override
    public void serialize(ResponsibilityAV value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {

      jgen.writeString(value.getName());
    }
  }

  public static class TextAVSerializer extends SerializerBase<TextAV> {

    protected TextAVSerializer() {
      super(TextAV.class);
    }

    /**{@inheritDoc}**/
    @Override
    public void serialize(TextAV value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {

      jgen.writeString(value.getValue());
    }
  }

  public static class DateAVSerializer extends SerializerBase<DateAV> {

    public DateAVSerializer() {
      super(DateAV.class);
    }

    /**{@inheritDoc}**/
    @Override
    public void serialize(DateAV value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {

      jgen.writeString(value.getValueString());
    }
  }

  public static class AtgCmPartSerializer extends SerializerBase<AttributeTypeGroupComponentModelPart> {

    public AtgCmPartSerializer() {
      super(AttributeTypeGroupComponentModelPart.class);
    }

    /**{@inheritDoc}**/
    @Override
    public void serialize(AttributeTypeGroupComponentModelPart value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
    JsonProcessingException {

      jgen.writeStartObject();
      jgen.writeObjectField("name", value.getAtg().getName());

      jgen.writeObjectField("attributes", value.getAtParts());
      jgen.writeEndObject();
    }
  }

  protected abstract static class AtCmPartSerializerBase<T extends AbstractAttributeTypeComponentModelPartBase<?>> extends SerializerBase<T> {

    public AtCmPartSerializerBase(Class<T> t) {
      super(t);
    }

    /**{@inheritDoc}**/
    @Override
    public void serialize(T value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
      jgen.useDefaultPrettyPrinter();
      jgen.writeStartObject();

      jgen.writeFieldName("attributeName");
      jgen.writeString(value.getAttributeType().getName());

      jgen.writeObjectField("attributeType", value.getAttributeType().getTypeOfAttribute());

      writeSpecifics(value, jgen, provider);
      jgen.writeEndObject();
    }

    protected abstract void writeSpecifics(T value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonProcessingException ;
  }

  public static class EnumSinglePartSerializer extends AtCmPartSerializerBase<EnumAttributeTypeSingleComponentModelPart> {

    public EnumSinglePartSerializer() {
      super(EnumAttributeTypeSingleComponentModelPart.class);
    }

    /**{@inheritDoc}**/
    @Override
    protected void writeSpecifics(EnumAttributeTypeSingleComponentModelPart value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonProcessingException {
      jgen.writeObjectField("value", value.getAttributeValue());
    }
  }

  public static class EnumMultiPartSerializer extends AtCmPartSerializerBase<EnumAttributeTypeMultiComponentModelPart> {

    public EnumMultiPartSerializer() {
      super(EnumAttributeTypeMultiComponentModelPart.class);
    }

    /**{@inheritDoc}**/
    @Override
    protected void writeSpecifics(EnumAttributeTypeMultiComponentModelPart value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonProcessingException {
      jgen.writeObjectField("values", value.getConnectedAVs());
    }
  }

  public static class RespSinglePartSerializer extends AtCmPartSerializerBase<ResponsibilityAttributeTypeSingleComponentModelPart> {

    public RespSinglePartSerializer() {
      super(ResponsibilityAttributeTypeSingleComponentModelPart.class);
    }

    /**{@inheritDoc}**/
    @Override
    protected void writeSpecifics(ResponsibilityAttributeTypeSingleComponentModelPart value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonProcessingException {
      jgen.writeObjectField("value", value.getAttributeValue());
    }
  }

  public static class RespMultiPartSerializer extends AtCmPartSerializerBase<ResponsibilityAttributeTypeMultiComponentModelPart> {

    public RespMultiPartSerializer() {
      super(ResponsibilityAttributeTypeMultiComponentModelPart.class);
    }

    /**{@inheritDoc}**/
    @Override
    protected void writeSpecifics(ResponsibilityAttributeTypeMultiComponentModelPart value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonProcessingException {
      jgen.writeObjectField("values", value.getConnectedAVs());
    }
  }

  public static class NumberPartSerializer extends AtCmPartSerializerBase<NumberAttributeTypeComponentModelPart> {

    public NumberPartSerializer() {
      super(NumberAttributeTypeComponentModelPart.class);
    }

    /**{@inheritDoc}**/
    @Override
    protected void writeSpecifics(NumberAttributeTypeComponentModelPart value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonProcessingException {
      BigDecimal attributeValue = value.getAttributeValueAsNumber();
      if (attributeValue != null) {
        jgen.writeNumberField("value", attributeValue);
      } else {
        jgen.writeNullField("value");
      }

      String unit = value.getAttributeType().getUnit();
      if (StringUtils.isNotBlank(unit)) {
        jgen.writeStringField("unit", unit);
      }
    }
  }

  public static class DatePartSerializer extends AtCmPartSerializerBase<DateAttributeTypeComponentModelPart> {

    public DatePartSerializer() {
      super(DateAttributeTypeComponentModelPart.class);
    }

    /**{@inheritDoc}**/
    @Override
    protected void writeSpecifics(DateAttributeTypeComponentModelPart value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonProcessingException {
      jgen.writeObjectField("value", value.getAttributeValueAsDate());
    }
  }

  public static class TextPartSerializer extends AtCmPartSerializerBase<TextAttributeTypeComponentModelPart> {

    public TextPartSerializer() {
      super(TextAttributeTypeComponentModelPart.class);
    }

    /**{@inheritDoc}**/
    @Override
    protected void writeSpecifics(TextAttributeTypeComponentModelPart value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonProcessingException {
      jgen.writeObjectField("value", value.getAttributeValueAsString());
    }
  }


}
