Ext.ns("ExtGeoNet");

ExtGeoNet.jsimport = function(basedir, jsfiles) {
    var agent = navigator.userAgent;
    var docWrite = (agent.match("MSIE") || agent.match("Safari"));
    var allScriptTags;
    if(docWrite) {
        allScriptTags = new Array(jsfiles.length);
    }
    
    if(!basedir.match("/$")) {basedir = basedir + "/";}
        
    var host = (window.javascriptsLocation + basedir).replace(/\/\//g,"/");

    for (var i=0, len=jsfiles.length; i<len; i++) {
        if (docWrite) {
            allScriptTags[i] = "<script src='" + host + jsfiles[i] +
                               "'></script>"; 
        } else {
            var s = document.createElement("script");
            s.src = host + jsfiles[i];
            var h = document.getElementsByTagName("head").length ? 
                       document.getElementsByTagName("head")[0] : 
                       document.body;
            h.appendChild(s);
        }
    }
    if (docWrite) {
        document.write(allScriptTags.join(""));
    }  
};