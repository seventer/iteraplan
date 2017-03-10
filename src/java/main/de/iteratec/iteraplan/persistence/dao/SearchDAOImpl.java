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
package de.iteratec.iteraplan.persistence.dao;

import java.util.List;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.impl.FullTextSessionImpl;
import org.hibernate.search.reader.ReaderProvider;
import org.hibernate.search.store.DirectoryProvider;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.persistence.search.IteraFullTextSessionImpl;


/**
 * Implementation of the DAO interface {@link SearchDAO}.
 */
public class SearchDAOImpl extends GenericBaseDAO<de.iteratec.iteraplan.model.Search, Integer> implements SearchDAO {

  private static final Logger LOGGER = Logger.getIteraplanLogger(SearchDAOImpl.class);

  /** {@inheritDoc} */
  @Override
  protected String getNameAttribute() {
    return null;
  }

  /** {@inheritDoc} */
  public void createIndexes(Set<Class<?>> classList) {
    Session session = this.getSession();
    FullTextSession fullTextSession = getFullTextSession();

    session.setFlushMode(FlushMode.MANUAL); // Disable flush operations
    session.setCacheMode(CacheMode.IGNORE); // Disable second-level cache operations

    int batchSize = 100;

    // data is read from the database
    for (Class<?> bbClass : classList) {

      ScrollableResults results = session.createCriteria(bbClass).setFetchSize(batchSize).scroll(ScrollMode.SCROLL_INSENSITIVE);

      LOGGER.info("Indexing " + bbClass.getSimpleName());
      int index = 0;
      while (results.next()) {
        index++;
        // entities are indexed
        fullTextSession.index(results.get(0));
        if (index % batchSize == 0) {
          fullTextSession.flushToIndexes();
          fullTextSession.clear();
        }
      }
      results.close();
      LOGGER.info("Index for " + bbClass.getSimpleName() + " was created!");

    }
  }

  /** {@inheritDoc} */
  public void purgeIndexes(Set<Class<?>> classList) {
    FullTextSession fullTextSession = getFullTextSession();

    for (Class<?> bbClass : classList) {
      // Index is purged
      LOGGER.info(bbClass.getSimpleName() + " is deleted from index");
      fullTextSession.purgeAll(bbClass);

      // optimize the index
      LOGGER.info(bbClass + " optimize index!");
      fullTextSession.getSearchFactory().optimize(bbClass);
      LOGGER.info(bbClass + " was deleted from index");
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<Object[]> search(Query luceneQuery, Sort sortOrder, Class<?>[] classArray, String[] projections) {

    FullTextSession fullTextSession = getFullTextSession();

    // Results are fetched for the query
    FullTextQuery ftquery = fullTextSession.createFullTextQuery(luceneQuery, classArray);
    ftquery.setSort(sortOrder);

    // Projection-Query returns a list of Object[]
    return ftquery.setProjection(projections).list();
  }

  /** {@inheritDoc} */
  public IndexReader openReader(ReaderProvider readerProvider, Class<?>[] classes) {
    // create the directory provider
    DirectoryProvider<?>[] providers = new DirectoryProvider<?>[classes.length];
    for (int i = 0; i < classes.length; i++) {
      providers[i] = getSearchFactory().getDirectoryProviders(classes[i])[0];
    }

    // fetch and open the reader
    return readerProvider.openReader(providers);
  }

  /** {@inheritDoc} */
  public ReaderProvider getReaderProvider() {
    return getSearchFactory().getReaderProvider();
  }

  private SearchFactory getSearchFactory() {
    return getFullTextSession().getSearchFactory();
  }

  private FullTextSession getFullTextSession() {
    Session session = this.getSession();
    if (session instanceof FullTextSessionImpl) {
      return (FullTextSession) session;
    }
    else {
      return new IteraFullTextSessionImpl(session);
    }
  }

}