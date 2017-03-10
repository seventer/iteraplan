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
package de.iteratec.iteraplan.persistence.search;

//$Id: FullTextSessionImpl.java 15621 2008-11-26 13:17:23Z hardy.ferentschik $
import java.io.Serializable;

import org.hibernate.Hibernate;
import org.hibernate.classic.Session;
import org.hibernate.event.EventSource;
import org.hibernate.search.backend.TransactionContext;
import org.hibernate.search.backend.Work;
import org.hibernate.search.backend.WorkType;
import org.hibernate.search.backend.impl.EventSourceTransactionContext;
import org.hibernate.search.engine.SearchFactoryImplementor;
import org.hibernate.search.impl.FullTextSessionImpl;
import org.hibernate.search.util.ContextHelper;

import de.iteratec.iteraplan.model.fulltextsearch.BuildingBlockBridge;


/**
 * <p>
 * This extends the org.hibernate.search.impl.FullTextSessionImpl and has a patched index()
 * Method. Instead of passing the id to the worker and letting the worker convert it through the
 * BuildingBlockBridge, we convert the id to the documentId already here within the main thread.
 * </p>
 * Lucene full text search aware session.
 * 
 * @author Emmanuel Bernard
 * @author John Griffin
 * @author Hardy Ferentschik
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
// suppress findbugs warnings, as this is not our own code
@edu.umd.cs.findbugs.annotations.SuppressWarnings({ "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", "NP_LOAD_OF_KNOWN_NULL_VALUE" })
public class IteraFullTextSessionImpl extends FullTextSessionImpl {

  private static final long                  serialVersionUID = -2771441365655429431L;

  private final Session                      session;
  private transient SearchFactoryImplementor searchFactory;
  private final TransactionContext           transactionContext;
  private final BuildingBlockBridge          bbb;

  public IteraFullTextSessionImpl(org.hibernate.Session session) {
    super(session);
    this.session = (Session) session;
    this.transactionContext = new EventSourceTransactionContext((EventSource) session);
    this.bbb = new BuildingBlockBridge();
  }

  /**
   * (Re-)index an entity.
   * The entity must be associated with the session and non indexable entities are ignored.
   *
   * @param entity The entity to index - must not be <code>null</code>.
   *
   * @throws IllegalArgumentException if entity is null or not an @Indexed entity
   */
  public <T> void index(T entity) {
    if (entity == null) {
      throw new IllegalArgumentException("Entity to index should not be null");
    }

    Class<?> clazz = Hibernate.getClass(entity);
    //TODO cache that at the FTSession level
    SearchFactoryImplementor searchFactoryImplementor = getSearchFactoryImplementor();
    //not strictly necessary but a small optimization
    if (searchFactoryImplementor.getDocumentBuilderIndexedEntity(clazz) == null) {
      String msg = "Entity to index is not an @Indexed entity: " + entity.getClass().getName();
      throw new IllegalArgumentException(msg);
    }
    Serializable id = session.getIdentifier(entity);
    id = bbb.objectToString(id);
    Work<T> work = new Work<T>(entity, id, WorkType.INDEX);
    searchFactoryImplementor.getWorker().performWork(work, transactionContext);

    //TODO
    //need to add elements in a queue kept at the Session level
    //the queue will be processed by a Lucene(Auto)FlushEventListener
    //note that we could keep this queue somewhere in the event listener in the mean time but that requires
    //a synchronized hashmap holding this queue on a per session basis plus some session house keeping (yuk)
    //another solution would be to subclass SessionImpl instead of having this LuceneSession delegation model
    //this is an open discussion
  }

  @SuppressWarnings("deprecation")
  private SearchFactoryImplementor getSearchFactoryImplementor() {
    if (searchFactory == null) {
      searchFactory = ContextHelper.getSearchFactory(session);
    }
    return searchFactory;
  }
}
