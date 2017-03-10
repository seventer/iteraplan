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
package de.iteratec.iteraplan.presentation.rest.resource;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.StringRepresentation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;


/**
 *
 */
public class SingleOEResourceTest extends ADataResourceTest {
  private static final String url = "/data/BP";

  @Test
  public void testDelete() throws JsonIOException, JsonSyntaxException, IOException {
    Request request = new Request(Method.DELETE, url + "/" + createBP());
    Response response = new Response(request);
    router.handle(request, response);

    Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
  }

  @Test
  public void testDeleteNegative() {
    Request request = new Request(Method.DELETE, url + "/17");
    Response response = new Response(request);
    router.handle(request, response);

    //TODO what is the response status
    Assert.assertEquals(Status.CLIENT_ERROR_BAD_REQUEST, response.getStatus());
  }

  @Test
  public void testPut() throws JsonIOException, JsonSyntaxException, IOException {
    Request request = new Request(Method.PUT, url + "/" + createBP().toString());
    Reference ref = new Reference("http://localhost:8080/iteraplan/");
    request.setRootRef(ref);

    JsonObject post = new JsonObject();
    post.add("query", new JsonPrimitive("BP"));

    JsonArray result = new JsonArray();
    JsonObject object = new JsonObject();
    JsonArray name = new JsonArray();
    name.add(new JsonPrimitive("newName"));
    object.add("name", name);
    result.add(object);
    post.add("result", result);

    request.setEntity(new StringRepresentation(new Gson().toJson(post)));

    Response response = new Response(request);
    router.handle(request, response);

    Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
  }

  private BigInteger createBP() throws JsonIOException, JsonSyntaxException, IOException {
    Request request = new Request(Method.POST, url);
    Reference ref = new Reference("http://localhost:8080/iteraplan/");
    request.setRootRef(ref);

    JsonObject post = new JsonObject();
    post.add("query", new JsonPrimitive("BP"));

    JsonArray result = new JsonArray();
    JsonObject object = new JsonObject();
    JsonArray id = new JsonArray();
    id.add(new JsonPrimitive("3"));
    JsonArray name = new JsonArray();
    name.add(new JsonPrimitive("name"));
    object.add("id", id);
    object.add("name", name);
    result.add(object);
    post.add("result", result);

    request.setEntity(new StringRepresentation(new Gson().toJson(post)));

    Response response = new Response(request);
    router.handle(request, response);

    Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());

    JsonElement json = new JsonParser().parse(response.getEntity().getReader());
    String[] splitted = json.getAsJsonObject().get("elementURI").getAsString().split("/");
    return BigInteger.valueOf(Long.parseLong(splitted[splitted.length - 1]));
  }
}
