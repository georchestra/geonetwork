Ext.ns("ExtGeoNet.loc.Languages");

ExtGeoNet.loc.Languages.convA3ToA2 = function(abbr3,toLower) {
    abbr3 = abbr3.trim();
    if(abbr3.toLowerCase() == "ger" ) {
        abbr3 = "deu";
    }
    var abbr2 = abbr3.substring(0,2);
    return toLower ? abbr2.toLowerCase() : abbr2.toUpperCase();
};

ExtGeoNet.loc.Languages.convA3toUrl = function(abbr3) {
    return ExtGeoNet.loc.Languages.convA3ToA2(abbr3,true);
};