<#macro formatValue value><#if value?is_date>${value?date?string.long}<#else>${value}</#if></#macro>