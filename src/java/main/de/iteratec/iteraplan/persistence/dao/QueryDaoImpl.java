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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.reports.query.node.AbstractLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.ExtensionNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Operation;
import de.iteratec.iteraplan.businesslogic.reports.query.node.OperationNode;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;


/**
 * Implementation of the DAO interface {@link QueryDAO}.
 */
public class QueryDaoImpl extends HibernateDaoSupport implements QueryDAO {

  private static final Logger LOGGER = Logger.getIteraplanLogger(QueryDaoImpl.class);

  /** {@inheritDoc} */
  public Set<BuildingBlock> evaluateQueryTree(Node rootNode) {
    LOGGER.debug("Starting evaluation of query tree.");

    if (rootNode instanceof AbstractLeafNode) {
      LOGGER.debug("Reached a leaf node.");
      return processLeafNode((AbstractLeafNode)rootNode);
    }

    if (rootNode instanceof OperationNode) {
      LOGGER.debug("Reached an operation node.");

      OperationNode node = (OperationNode) rootNode;
      Operation operationType = node.getOperation();

      // Operation node is of type AND.
      if (operationType == Operation.AND) {
        LOGGER.debug("Operation node type: AND");

        Set<BuildingBlock> setAND = Sets.newHashSet();
        List<Node> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
          Node child = children.get(i);
          Set<? extends BuildingBlock> childResults = evaluateQueryTree(child);
          if (i == 0) {
            setAND.addAll(childResults);
          }
          else {
            setAND.retainAll(childResults);
          }
        }

        return setAND;
      }

      // Operation node is of type OR.
      if (operationType == Operation.OR) {
        LOGGER.debug("Operation node type: OR");

        Set<BuildingBlock> setOR = Sets.newHashSet();
        List<Node> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
          Node child = children.get(i);
          Set<? extends BuildingBlock> childResults = evaluateQueryTree(child);
          setOR.addAll(childResults);
        }
        return setOR;
      }
    }

    if (rootNode instanceof ExtensionNode) {
      return processExtensionNode((ExtensionNode) rootNode);
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  private Set<BuildingBlock> processExtensionNode(ExtensionNode extensionNode) {
    LOGGER.debug("Reached an extension node.");

    Node child = extensionNode.getChild();
    Set<? extends BuildingBlock> subResults = evaluateQueryTree(child);
    if ((subResults != null) && (subResults.size() > 0)) {
      extensionNode.setLeafNodeBuildingBlocks(subResults);
      return (Set<BuildingBlock>) runCriteria(extensionNode.getCriteria());
    }

    return Sets.newHashSet();
  }

  private Set<BuildingBlock> processLeafNode(AbstractLeafNode node) {
    return getResultSetForAbstractLeafNodeUsingCriteria(node);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<String> getAttributeValuesForFixedAttribute(final Type<?> type, final String attrName) {
    HibernateCallback<List<String>> callback = new HibernateCallback<List<String>>() {
      public List<String> doInHibernate(Session session) {
        List<String> res = new ArrayList<String>();
        StringBuffer hql = new StringBuffer(50);
        hql.append("select distinct obj.");
        hql.append(attrName);
        hql.append(" from ");
        hql.append(type.getTypeNameDB());
        hql.append(" obj order by obj.");
        hql.append(attrName);

        List<Object> list = session.createQuery(hql.toString()).list();
        postProcessAttributeValues(list, type, attrName);
        convertToStrings(list, res);
        return res;
      }
    };
    
    return getHibernateTemplate().execute(callback);
  }

  /** {@inheritDoc} */
  public List<String> getSetAttribute(final Type<?> type, final String attrName) {
    HibernateCallback<List<String>> callback = new SetAttributeCallback(attrName, type);
    return getHibernateTemplate().execute(callback);

  }

  private void convertToStrings(List<Object> list, List<String> result) {
    if (list != null) {
      for (Object obj : list) {
        result.add(obj.toString());
      }
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public Set<BuildingBlock> getResultSetForAbstractLeafNodeUsingCriteria(final AbstractLeafNode node) {
    Set<BuildingBlock> resultSet = (Set<BuildingBlock>) runCriteria(node.getCriteria());
    LOGGER.debug("Size of query result set (standard): {1}", Integer.valueOf(resultSet.size()));

    if (node.isAdditionalQueryRequired()) {
      Set<BuildingBlock> additionalResultSet = (Set<BuildingBlock>) runCriteria(node.getCriteriaForInheritance());
      LOGGER.debug("Size of query result set (additionale): {1}", Integer.valueOf(additionalResultSet.size()));

      resultSet.addAll(additionalResultSet);
    }

    removeMismatchElementsFromResultForNode(resultSet, node);
    LOGGER.debug("Size of query result set (after mismatch removal): {1}", Integer.valueOf(resultSet.size()));

    return resultSet;
  }

  private Set<?> runCriteria(final DetachedCriteria criteria) {
    HibernateCallback<Set<?>> callback = new RunCriteriaCallback(criteria);
    return getHibernateTemplate().execute(callback);
  }

  private void postProcessAttributeValues(List<Object> res, Type<?> type, String attrName) {
    boolean removeTopLevelElement = false;
    if ("name".equals(attrName) && type.isOrderedHierarchy()) {
      removeTopLevelElement = true;
    }

    for (Iterator<Object> it = res.iterator(); it.hasNext();) {
      Object obj = it.next();

      if (obj == null) {
        it.remove();
        continue;
      }

      String string = obj.toString();
      if ((string == null) || string.equals("")) {
        it.remove();
      }

      if (removeTopLevelElement && type.getOrderedHierarchyRootElementName().equals(string)) {
        it.remove();
      }
    }
  }

  private void removeMismatchElementsFromResultForNode(Set<?> result, AbstractLeafNode node) {
    for (Iterator<?> iter = result.iterator(); iter.hasNext();) {
      Object element = iter.next();

      if (node.isToBeRemoved(element)) {
        iter.remove();
      }
    }
  }

  private static final class SetAttributeCallback implements HibernateCallback<List<String>> {
    private final String  attrName;
    private final Type<?> type;

    public SetAttributeCallback(String attrName, Type<?> type) {
      this.attrName = attrName;
      this.type = type;
    }

    @SuppressWarnings("unchecked")
    public List<String> doInHibernate(Session session) {
      StringBuffer hql = new StringBuffer(50);
      hql.append("select distinct obj.");
      hql.append(attrName);
      hql.append(" from ");
      hql.append(type.getTypeNameDB());
      hql.append(" obj");
      
      List<Object> queryResult = session.createQuery(hql.toString()).list();
      List<String> res = null;
      if (queryResult != null) {
        res = new ArrayList<String>();
        for (Object obj : queryResult) {
          res.add(obj.toString());
        }
      }
      
      return res;
    }
  }

  private static final class RunCriteriaCallback implements HibernateCallback<Set<?>> {
    private final DetachedCriteria criteria;

    public RunCriteriaCallback(DetachedCriteria criteria) {
      this.criteria = criteria;
    }

    @SuppressWarnings("unchecked")
    public Set<?> doInHibernate(Session session) {
      try {
        return new HashSet<Object>(criteria.getExecutableCriteria(session).list());
      } catch (HibernateException hex) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, hex);
      }
    }
  }

}
