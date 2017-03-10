dependencies = {
		stripConsole: "normal",
		layers: [
		{
			name: "dashboard.js",
			dependencies: [
				"dojox.gfx.vml",
				"dojox.gfx.svg",
				"dojox.charting.Chart2D",
				"dojox.charting.widget.Chart2D",
				"dojox.charting.themes.PlotKit.purple",
				"dojox.charting.themes.MiamiNice",
				"dojox.charting.widget.Legend",
				"dojox.charting.plot2d.Pie"
			]
		}
	],

	prefixes: [
		[ "dojox", "../dojox" ]
	]
};
