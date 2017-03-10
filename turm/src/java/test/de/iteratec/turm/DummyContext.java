/*
 * iTURM is a User and Roles Management web application developed by iteratec, GmbH
 * Copyright (C) 2008 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
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
 * You can contact iteratec GmbH headquarters at Inselkammerstraße 4
 * 82008 München - Unterhaching, Germany, or at email address info@iteratec.de.
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
package de.iteratec.turm;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * This is a dummy implementation of the <code>Context</code> interface used for
 * unit tests only. This implementation provides neither less nor more functionality
 * then used by B2V COM and the test itself.
 * <dl>
 * <dt style="margin-top:0.5cm; margin-left:1cm;"><b>Attention:</b></dt>
 * <dd>This implementation may fail in future if more functionality is required.</dd>
 * </dl>
 * 
 * @author Jens Bachmann (jens.bachmann@iteratec.de)
 * @since 30.11.2006
 * @version $Id: DummyContext.java 4280 2006-12-13 14:24:40Z uid=jba,dc=iteratec,dc=de $
 */
public class DummyContext implements Context {
    private Map<String, Object> objects = new HashMap<String, Object>();

    public void close() throws NamingException {
    }
    public synchronized void bind(String name, Object obj) throws NamingException {
        this.objects.put(name, obj);
    }
    public synchronized Object lookup(String name) throws NamingException {
        return this.objects.get(name);
    }



    public String getNameInNamespace() throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public void destroySubcontext(String name) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public void unbind(String name) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public Hashtable<?,?> getEnvironment() throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public void destroySubcontext(Name name) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public void unbind(Name name) throws NamingException {
    }
    public Object lookupLink(String name) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public Object removeFromEnvironment(String propName) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public void rebind(String name, Object obj) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public Object lookup(Name name) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public Object lookupLink(Name name) throws NamingException {
      throw new IllegalStateException("Not implemented");
    }
    public void bind(Name name, Object obj) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public void rebind(Name name, Object obj) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public void rename(String oldName, String newName) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public Context createSubcontext(String name) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public Context createSubcontext(Name name) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public void rename(Name oldName, Name newName) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public NameParser getNameParser(String name) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public NameParser getNameParser(Name name) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public Object addToEnvironment(String propName, Object propVal)
        throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public String composeName(String name, String prefix) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
    public Name composeName(Name name, Name prefix) throws NamingException {
        throw new IllegalStateException("Not implemented");
    }
}
