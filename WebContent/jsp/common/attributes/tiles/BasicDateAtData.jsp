<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<tiles:useAttribute name="atPartPath"/>
<tiles:useAttribute name="extended_html_id" />

<itera:define id="atPart" name="memBean" property="${atPartPath}"/>

<%-- Date Attribute Type Min Value--%>

