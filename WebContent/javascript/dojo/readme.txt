This folder contains the minified versions of the dojo modules. 
The iteraplan.profile.js contains the descriptions, what should be 
compiled in a minified JavaScript files. The full description for 
how to create such files can be found under 
http://dojotoolkit.org/reference-guide/build/index.html  

common.js contains common Dojo modules. Which modules will packed, see
in iteraplan.profile.js

Run the build process with a command similar to this one:

$ build.bat profileFile=<path to iteraplan project>/iteraplan\WebContent\javascript\dojo\iteraplan.profile.js 
		action=clean,release  version=1.6.1 optimize=shrinksafe layerOptimize=shrinksafe buildLayers=dashboard.js,common.js