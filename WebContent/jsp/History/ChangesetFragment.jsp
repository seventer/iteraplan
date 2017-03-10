<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<%-- The changeset object that shall be rendered --%>
<tiles:useAttribute name="bbChangeset" />
<div class="row-fluid inner-module">

	<div class="inner-module-heading">
		<c:choose>
			<c:when test="${bbChangeset.hasAuthor}">
				<c:set var="link">
					<itera:linkToElement name="bbChangeset" property="revisionAuthor"
						type="html" />
				</c:set>
				<a href="${link}"><c:out value="${bbChangeset.author}" /></a> - ${bbChangeset.timestampString}
		</c:when>
			<c:otherwise>
			${bbChangeset.timestampString}
		</c:otherwise>
		</c:choose>
	</div>
	<div class="row-fluid visibleRows">
		<div class="inner-module-body">
			<div class="row-fluid">
				<c:choose>

					<%-- First known version, no previous version to compare to --%>
					<c:when test="${bbChangeset.initialChangeset}">
						<fmt:message key="history.initialrevision" />
					</c:when>

					<%-- Need to list two very similar long conditions twice, as we rely on the other wise clause and hence can't use nested conditions --%>
					<c:when
						test="${not bbChangeset.hierarchicalType && not bbChangeset.basicPropertiesChanged && not bbChangeset.relationsChanged && not bbChangeset.attributesChanged}">
						<fmt:message key="history.noKnownChanges" />
					</c:when>
					<%-- Almost like above case, but also inspect hierarchy changes --%>
					<c:when
						test="${(
					bbChangeset.hierarchicalType 
					&& not bbChangeset.parentChanged
					&& not bbChangeset.childrenChanged) 
					&& not bbChangeset.basicPropertiesChanged 
					&& not bbChangeset.relationsChanged 
					&& not bbChangeset.attributesChanged}">
						<fmt:message key="history.noKnownChanges" />
					</c:when>
					<c:otherwise>
						<%-- something has changed, so let's render it! --%>
						<c:set var="showSuperElementChange"
							value="${bbChangeset.hierarchicalType && bbChangeset.parentChanged}" />
						<c:if
							test="${bbChangeset.basicPropertiesChanged or showSuperElementChange}">
							<%-- Module of simple From-To changes --%>
							<div class="row-fluid inner-module">
								<div class="row-fluid">
									<div class="inner-module-body-table">
										<div class="row-fluid">
											<table
												class="table table-striped table-condensed tableInModule">
												<thead>
													<tr>
														<th></th>
														<th><fmt:message key="history.from" /></th>
														<th><fmt:message key="history.to" /></th>
													</tr>
												</thead>
												<tbody>
													<c:if test="${bbChangeset.nameChanged}">
														<tr>
															<td><b><fmt:message key="global.name" /></b></td>
															<td><c:out value="${bbChangeset.nameFrom}" /></td>
															<td><c:out value="${bbChangeset.nameTo}" /></td>
														</tr>
													</c:if>

													<c:if test="${bbChangeset.descriptionChanged}">
														<tr>
															<td><b><fmt:message key="global.description" /></b></td>
															<td><c:out value="${bbChangeset.descriptionFrom}" /></td>
															<td><c:out value="${bbChangeset.descriptionTo}" /></td>
														</tr>
													</c:if>

													<c:if
														test="${(bbChangeset.typeOfBuildingBlock == 'INFORMATIONSYSTEMRELEASE' || bbChangeset.typeOfBuildingBlock == 'TECHNICALCOMPONENTRELEASE' || bbChangeset.typeOfBuildingBlock == 'PROJECT')
								       	    		 				&& bbChangeset.runtimeHasChanged }">
														<tr>
															<td><b><fmt:message key="global.productive_from" /></b></td>
															<td><c:out
																	value="${bbChangeset.runtimeStartRemovedAsString}" /></td>
															<td><c:out
																	value="${bbChangeset.runtimeStartAddedAsString}" /></td>
														</tr>
														<tr>
															<td><b><fmt:message key="global.productive_to" /></b></td>
															<td><c:out
																	value="${bbChangeset.runtimeEndRemovedAsString}" /></td>
															<td><c:out
																	value="${bbChangeset.runtimeEndAddedAsString}" /></td>
														</tr>
													</c:if>

													<c:if
														test="${(bbChangeset.typeOfBuildingBlock == 'INFORMATIONSYSTEMRELEASE' || bbChangeset.typeOfBuildingBlock == 'TECHNICALCOMPONENTRELEASE')
								       	    		 				&& (not empty bbChangeset.statusFrom or not empty bbChangeset.statusTo)}">
														<tr>
															<td><b><fmt:message key="global.type_of_status" /></b></td>
															<td><fmt:message key="${bbChangeset.statusFrom}" /></td>
															<td><fmt:message key="${bbChangeset.statusTo}" /></td>
														</tr>
													</c:if>

													<c:if test="${showSuperElementChange}">
														<tr>
															<td><b><fmt:message
																		key="${bbChangeset.parentElementLabelKey}" /></b></td>
															<td><c:out value="${bbChangeset.parentFromName}" /></td>
															<td><c:out value="${bbChangeset.parentToName}" /></td>
														</tr>
													</c:if>
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</div>
						</c:if>

						<c:if
							test="${bbChangeset.hierarchicalType && bbChangeset.childrenChanged}">
							<tiles:insertTemplate
								template="/jsp/History/ManyAssociationSetFragment.jsp">
								<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
								<tiles:putAttribute name="relationLabelKey"
									value="${bbChangeset.childElementsLabelKey}" />
								<tiles:putAttribute name="addedElementsPath"
									value="childrenAdded" />
								<tiles:putAttribute name="removedElementsPath"
									value="childrenRemoved" />
							</tiles:insertTemplate>
						</c:if>

						<c:if test="${bbChangeset.relationsChanged}">
							<c:choose>
								<c:when
									test="${bbChangeset.typeOfBuildingBlock == 'ARCHITECTURALDOMAIN'}">
									<tiles:insertTemplate
										template="/jsp/History/ArchitecturalDomainRelations.jsp">
										<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
									</tiles:insertTemplate>
								</c:when>
								<c:when
									test="${bbChangeset.typeOfBuildingBlock == 'BUSINESSDOMAIN'}">
									<tiles:insertTemplate
										template="/jsp/History/BusinessDomainRelations.jsp">
										<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
									</tiles:insertTemplate>
								</c:when>
								<c:when
									test="${bbChangeset.typeOfBuildingBlock == 'BUSINESSFUNCTION'}">
									<tiles:insertTemplate
										template="/jsp/History/BusinessFunctionRelations.jsp">
										<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
									</tiles:insertTemplate>
								</c:when>
								<c:when
									test="${bbChangeset.typeOfBuildingBlock == 'BUSINESSOBJECT'}">
									<tiles:insertTemplate
										template="/jsp/History/BusinessObjectRelations.jsp">
										<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
									</tiles:insertTemplate>
								</c:when>
								<c:when
									test="${bbChangeset.typeOfBuildingBlock == 'BUSINESSPROCESS'}">
									<tiles:insertTemplate
										template="/jsp/History/BusinessProcessRelations.jsp">
										<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
									</tiles:insertTemplate>
									<%-- add business mapping diffs! --%>
								</c:when>
								<c:when
									test="${bbChangeset.typeOfBuildingBlock == 'BUSINESSUNIT'}">
									<tiles:insertTemplate
										template="/jsp/History/BusinessUnitRelations.jsp">
										<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
										<%-- add business mapping diffs! --%>
									</tiles:insertTemplate>
								</c:when>
								<c:when
									test="${bbChangeset.typeOfBuildingBlock == 'INFORMATIONSYSTEMDOMAIN'}">
									<tiles:insertTemplate
										template="/jsp/History/InformationSystemDomainRelations.jsp">
										<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
									</tiles:insertTemplate>
								</c:when>
								<c:when
									test="${bbChangeset.typeOfBuildingBlock == 'INFORMATIONSYSTEMRELEASE'}">
									<tiles:insertTemplate
										template="/jsp/History/InformationSystemReleaseRelations.jsp">
										<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
										<%-- add business mapping diffs! --%>
									</tiles:insertTemplate>
								</c:when>
								<c:when
									test="${bbChangeset.typeOfBuildingBlock == 'INFORMATIONSYSTEMINTERFACE'}">
									<tiles:insertTemplate
										template="/jsp/History/InformationSystemInterfaceRelations.jsp">
										<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
									</tiles:insertTemplate>
								</c:when>
								<c:when
									test="${bbChangeset.typeOfBuildingBlock == 'INFRASTRUCTUREELEMENT'}">
									<tiles:insertTemplate
										template="/jsp/History/InfrastructureElementRelations.jsp">
										<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
									</tiles:insertTemplate>
								</c:when>
								<c:when test="${bbChangeset.typeOfBuildingBlock == 'PRODUCT'}">
									<tiles:insertTemplate
										template="/jsp/History/ProductRelations.jsp">
										<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
									</tiles:insertTemplate>
									<%-- add business mapping diffs! --%>
								</c:when>
								<c:when test="${bbChangeset.typeOfBuildingBlock == 'PROJECT'}">
									<tiles:insertTemplate
										template="/jsp/History/ProjectRelations.jsp">
										<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
									</tiles:insertTemplate>
								</c:when>
								<c:when
									test="${bbChangeset.typeOfBuildingBlock == 'TECHNICALCOMPONENTRELEASE'}">
									<tiles:insertTemplate
										template="/jsp/History/TechnicalComponentReleaseRelations.jsp">
										<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
									</tiles:insertTemplate>
								</c:when>
							</c:choose>
						</c:if>

						<%-- Attribute Groups  --%>
						<c:forEach items="${bbChangeset.attributeGroupChangesets}"
							var="attributeGroupChangeset">
							<div class="row-fluid inner-module">
								<div class="inner-module-heading">
									<%--Uncomment to support attribute grouping  ${attributeGroupChangeset.groupName} --%>
									<fmt:message key="global.attributes" />
								</div>
								<div class="row-fluid">
									<div class="inner-module-body-table">
										<div class="row-fluid">
											<table
												class="table table-striped table-condensed tableInModule">
												<thead>
													<tr>
														<th></th>
														<th><fmt:message key="history.from" /></th>
														<th><fmt:message key="history.to" /></th>
													</tr>
												</thead>
												<tbody>
													<c:forEach
														items="${attributeGroupChangeset.changedAttributes}"
														var="changedAttribute">
														<tr>
															<td><b><c:out value="${changedAttribute[0]}" /></b></td>
															<td><c:out value="${changedAttribute[1]}" /></td>
															<td><c:out value="${changedAttribute[2]}" /></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</div>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</div>

