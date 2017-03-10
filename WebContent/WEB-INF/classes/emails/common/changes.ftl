<#import "link.ftl" as l>
<#import "format.ftl" as f>
<#if changes??>
<#if (changes?size == 0)>
No changes.
</#if>
<#if (changes?size > 0)>
<#list changes as c>
<#if c.type == "added">
${c.name} ${c.type}: "<@f.formatValue value=c.value/>" <@l.createLink url=(c.link!"")/>
</#if>
<#if c.type == "removed">
${c.name} ${c.type}: "<@f.formatValue value=c.value/>" <@l.createLink url=(c.link!"")/>
</#if>
<#if c.type == "changed">
<#if c.from?? && c.to??>
${c.name} ${c.type}: from "<@f.formatValue value=c.from/>" <@l.createLink url=(c.fromLink!"")/> to "<@f.formatValue value=c.to/>" <@l.createLink url=(c.toLink!"")/>
<#elseif c.value??>
${c.name} ${c.type}: "<@f.formatValue value=c.value/>"
<#else>
${c.name} ${c.type}
</#if>
</#if>
</#list>
</#if>
</#if>