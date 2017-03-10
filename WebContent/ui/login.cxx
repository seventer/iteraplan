/* This CSS file has an .cxx ending, because it must be available with JAWRs dynamic URL prefixes. */
/* Since the Servlet mapping is made on *.css, there was no other way but to change the extension */

#outerbox {
	margin:      15% auto;
	width:     300px;
	border-width:  1px; 
	border-style:  solid; 
	border-color:  rgb(0, 0, 100);
	padding:     6px; 
}

#innerbox {
	background-color: rgb(245, 245, 245); 
	border-width:  1px; 
	border-style:  solid; 
	border-color:  rgb(204, 204, 204); 
	padding:     1px;
}

#buttons {
	text-align:    center;
	line-height:   120%;
	margin:      2em auto;
}

#failurebox {
	border-width:  1px; 
	border-style:  solid; 
	border-color:  rgb(0, 0, 100);
	width:     100%; 
	padding:     2px; 
	margin:      5 auto;
}   

.table{
	width: 100%;
}

#loginButtonCursor{
	cursor: pointer;
}   

body {
	font-family:   Verdana, Arial, Helvetica, sans-serif; 
	line-height:   120%;
	font-size:   7pt;
	}

h1 {
	text-align:    center !important;
	margin:      1.5em auto !important;
	font-size:   11pt !important;
	font-weight:   normal !important;
	margin-top: 9 !important;
	margin-bottom: 9 !important;
}

.label {
	text-align:    right;
}

.input {
	text-align:    left;
	width:     140px;
}

#hintbox {
	text-align:    center;
}

#hintbox h3 {
	margin-bottom: 0em;
}

p.errorMsg {
	color:     red;
	font-weight:   bold;
	text-align:    center;
}

#logo{
	text-align:   center;
	padding-top:    15px;
	padding-bottom: 15px;
}

#iteraplan_header_image {
	width: 200px;
	height: 49px;
	background: url('../images/iteraplanLogo200x49.gif') top left no-repeat;
}

/* Some bootstrap css for button */
.btn {
	display: inline-block;
	padding: 4px 10px 4px;
	font-size: 13px;
	line-height: 18px;
	color: #333333;
	text-align: center;
	text-shadow: 0 1px 1px rgba(255, 255, 255, 0.75);
	background-color: #fafafa;
	background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#ffffff), color-stop(25%, #ffffff), to(#e6e6e6));
	background-image: -webkit-linear-gradient(#ffffff, #ffffff 25%, #e6e6e6);
	background-image: -moz-linear-gradient(top, #ffffff, #ffffff 25%, #e6e6e6);
	background-image: -ms-linear-gradient(#ffffff, #ffffff 25%, #e6e6e6);
	background-image: -o-linear-gradient(#ffffff, #ffffff 25%, #e6e6e6);
	background-image: linear-gradient(#ffffff, #ffffff 25%, #e6e6e6);
	background-repeat: no-repeat;
	filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ffffff', endColorstr='#e6e6e6', GradientType=0);
	border: 1px solid #ccc;
	border-bottom-color: #bbb;
	-webkit-border-radius: 4px;
	-moz-border-radius: 4px;
	border-radius: 4px;
	-webkit-box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05);
	-moz-box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05);
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05);
	cursor: pointer;
	*margin-left: .3em;
}

.btn:first-child {
	*margin-left: 0;
}

.btn:hover {
	color: #333333;
	text-decoration: none;
	background-color: #e6e6e6;
	background-position: 0 -15px;
	-webkit-transition: background-position 0.1s linear;
	-moz-transition: background-position 0.1s linear;
	-ms-transition: background-position 0.1s linear;
	-o-transition: background-position 0.1s linear;
	transition: background-position 0.1s linear;
}

.btn:focus {
	outline: thin dotted;
	outline: 5px auto -webkit-focus-ring-color;
	outline-offset: -2px;
}
.btn-primary,
.btn-primary:hover {
	text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.25);
	color: #ffffff;
}

.btn-primary.active {
	color: rgba(255, 255, 255, 0.75);
}

.btn-primary {
	background-color: #006dcc;
	background-image: -moz-linear-gradient(top, #0088cc, #0044cc);
	background-image: -ms-linear-gradient(top, #0088cc, #0044cc);
	background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#0088cc), to(#0044cc));
	background-image: -webkit-linear-gradient(top, #0088cc, #0044cc);
	background-image: -o-linear-gradient(top, #0088cc, #0044cc);
	background-image: linear-gradient(top, #0088cc, #0044cc);
	background-repeat: repeat-x;
	filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#0088cc', endColorstr='#0044cc', GradientType=0);
	border-color: #0044cc #0044cc #002a80;
	border-color: rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25);
	filter: progid:DXImageTransform.Microsoft.gradient(enabled = false);
}

.btn-primary:hover,
.btn-primary:active,
.btn-primary[disabled] {
	background-color: #0044cc;
}

.btn-primary:active {
	background-color: #003399 \9;
}