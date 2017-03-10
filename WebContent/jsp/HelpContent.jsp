<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<h1><fmt:message key="help.header" /></h1>
<br/>
<fmt:message key="help.intro" />
<div id="helpDocumentsModul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="help.documents" />
	</div>
	<div class="row-fluid">
		<div class="module-body">
			<div class="row-fluid">
				<ul>
					<li>
						<a href="../manuals/UserGuide.pdf" target="_blank">
							<fmt:message key="help.documents.usermanual" />
						</a>
						<fmt:message key="help.documents.usermanual.seealso.start" /> 
					    <a href="http://wiki.iteraplan.de" target=_blank>
					    	<fmt:message key="help.documents.usermanual.seealso.onlineversion" /> 
						</a>    
					    <fmt:message key="help.documents.usermanual.seealso.end" /> 
					</li>
					<li>
						<a href="../manuals/RELEASE.txt" target="_blank">
							<fmt:message key="help.documents.release" />
						</a>
					</li>
					<li>
						<a href="../manuals/LICENSE.txt" target="_blank">
							<fmt:message key="help.documents.license" />
						</a>
					</li>
				</ul>
			</div>
		</div>
	</div>
</div>

<div id="helpGlossaryModul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="help.glossary" />
	</div>
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">
				<table class="table table-striped table-condensed tableInModule">
					<tbody>
			  			<tr>
			  				<td><fmt:message key="businessDomain.singular"/></td>
			  				<td><fmt:message key="glossary.businessDomain"/></td>
			  			</tr>
			  			<tr>
			  				<td><fmt:message key="businessProcess.singular"/></td>
			  				<td><fmt:message key="glossary.businessProcess"/></td>
			  			</tr>
			  			<tr>
			  				<td><fmt:message key="global.business_function"/></td>
			  				<td><fmt:message key="glossary.business_function.description"/></td>
			  			</tr>
			  			<tr>
			  				<td><fmt:message key="global.product"/></td>
			  				<td><fmt:message key="glossary.product.description"/></td>
			  			</tr>
			  			<tr>
			                <td><fmt:message key="businessUnit.singular"/></td>
			                <td><fmt:message key="glossary.businessUnit"/></td>
			            </tr>
			            <tr>
			  				<td><fmt:message key="businessObject.singular"/></td>
			  				<td><fmt:message key="glossary.businessObject"/></td>
			  			</tr>
			  			<tr>
			  				<td><fmt:message key="informationSystemDomain.singular"/></td>
			  				<td><fmt:message key="glossary.informationSystemDomain"/></td>
			  			</tr>
			  			<tr>
			  				<td><fmt:message key="informationSystemRelease.singular"/></td>
			  				<td><fmt:message key="glossary.informationSystemRelease"/></td>
			  			</tr>
			  			<tr>
			  				<td><fmt:message key="interface.singular"/></td>
			  				<td><fmt:message key="glossary.interface"/></td>
			  			</tr>
			  			<tr>
			  				<td><fmt:message key="architecturalDomain.singular"/></td>
			  				<td><fmt:message key="glossary.arch_domain.description"/></td>
			  			</tr>
			  			<tr>
			  				<td><fmt:message key="technicalComponentRelease.singular"/></td>
			  				<td><fmt:message key="glossary.technicalComponentRelease"/></td>
			  			</tr>	
			  			<tr>
			  				<td><fmt:message key="deploymentInfrastructure"/></td>
			  				<td><fmt:message key="glossary.infrastructureElement"/></td>
			  			</tr>	
			  		  	<tr>
			  				<td><fmt:message key="project.singular"/></td>
			  				<td><fmt:message key="glossary.project"/></td>
			  			</tr>
		  			</tbody>				
		  		</table>
			</div>
		</div>
	</div>
</div>