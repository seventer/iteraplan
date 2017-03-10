// XWiki tags example
mySettings = {
	previewParserPath:	'', // path to your Wiki parser
	onShiftEnter:		{keepDefault:false, replaceWith:'\n\n'},
	markupSet: [
		{name:'Heading 1', key:'1', openWith:'= ', closeWith:' =', placeHolder:'Your title here...' },
		{name:'Heading 2', key:'2', openWith:'== ', closeWith:' ==', placeHolder:'Your title here...' },
		{name:'Heading 3', key:'3', openWith:'=== ', closeWith:' ===', placeHolder:'Your title here...' },
		{name:'Heading 4', key:'4', openWith:'==== ', closeWith:' ====', placeHolder:'Your title here...' },
		{name:'Heading 5', key:'5', openWith:'===== ', closeWith:' =====', placeHolder:'Your title here...' },
		{separator:'---------------' },		
		{name:'Bold', key:'B', openWith:"**", closeWith:"**"}, 
		{name:'Italic', key:'I', openWith:"//", closeWith:"//"}, 
		{name:'Stroke through', key:'S', openWith:'--', closeWith:'--'}, 
		{separator:'---------------' },
		{name:'Bulleted list', openWith:'(!(* |!|*)!)'}, 
		{name:'Numeric list', openWith:'(!(1. |!|1.)!)'}, 
		{separator:'---------------' },
		{name:'Picture', key:"P", replaceWith:'[[image:[![Url:!:http://]!]]]'}, 
		{name:'Link', key:"L", replaceWith:'[[[![Link]!]]]'},
		{name:'Url', replaceWith:'[[[![name]!]>>[![Url:!:http://]!]]]' },
		{separator:'---------------' },
		{name:'Quotes', openWith:'(!(> |!|>)!)', placeHolder:''},
//		{name:'Code', openWith:'(!({{code lang="[![Language:!:php]!]"}}|!|<pre>)!)', closeWith:'(!({{/code}}|!|</pre>)!)'} 
//		{separator:'---------------' },
//		{name:'Preview', call:'preview', className:'preview'}
	]
}