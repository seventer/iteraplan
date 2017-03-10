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
/**

The Excel Import works in four steps: two for importing the metamodel, and two for importing the data.

<p>
(1) Create the types, (2) create the features for all types,
(3) read data and create entities, (4) read data and create relationships.

<h2>First Pass</h2>

Read type names, and create the types in meta model.
So far, they just exist, but don't have any features yet.

<p>
Read:
<ul>
<li>SubstantialTypeExpression
<li>RelationshipTypeExpression
<li>EnumerationExpression
</ul>
but ignore RelationshipExpression.

Also: read all enum literals.

<p>
<i>Missing:</i>
<ul>
<li>Excel workbook validation. E.g. read version number or something similar.
<li>For enum types: type name and abbreviation are missing. (using sheet name as workaround.)
</ul>


<h2>Second Pass</h2>

Completely create the types: For each type, create all features (properties, relations).

<p>
Import the relationship sheets: add the relationships to the metamodel.


<h2>Third Pass</h2>

Read data: Import all entities and their properties, everything except relations.


<h2>Fourth Pass</h2>

Read data: Import relations.


<h2>Open Issues</h2>

Integration in GUI: add/enhance dialog.

<p>
ExcelImportServiceImpl, main method (l. 141ff):
<ul>
<li>Consistency checks
<li>Proper error handling, nice presentation to the user, and meaningful error messages. 
</ul>

 */
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.excelimport;

