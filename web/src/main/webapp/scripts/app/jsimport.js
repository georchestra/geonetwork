Ext.ns("ExtGeoNet");

ExtGeoNet.jsimport = function(basedir, jsfiles) {

    var allScriptTags = new Array(jsfiles.length);

    if(!basedir.match("/$")) {basedir = basedir + "/";}
        
    var host = (window.javascriptsLocation + basedir).replace(/\/\//g,"/");

    for (var i=0, len=jsfiles.length; i<len; i++) {
        allScriptTags[i] = "<script src='" + host + jsfiles[i] +
                            "'></script>"; 
        
    }
    document.write(allScriptTags.join(""));
};