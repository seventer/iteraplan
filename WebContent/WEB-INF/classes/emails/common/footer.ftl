User: ${user}

Time: ${time?datetime?string.medium}

-- 
<#if ilink??>
Element URL: <#if link??><#import "link.ftl" as l><@l.createLink url=link/></#if>
iteraplan <#if applicationLink??><#import "link.ftl" as l><@l.createLink url=applicationLink/></#if>
</#if>
EAM Tool iteraplan