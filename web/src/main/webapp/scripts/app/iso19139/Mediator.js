Ext.ns('ExtGeoNet.config.Mediator');

ExtGeoNet.config.Mediator.language = {
    defaultLang: "eng",
    add : function(metadata,language) {return null;}, // TODO
    remove : function(metadata,language) {return null;}, // TODO
    list : function(metadata, excludeMain) {
        if(metadata === undefined) {
          metadata = ExtGeoNet.Main.metadata;
        }
        var root = ExtGeoNet.Xml.localName(ExtGeoNet.Xml.firstEl(metadata));
        var locales = ExtGeoNet.Xml.findValues(metadata,"/"+root+"/ locale / PT_Locale / languageCode / LanguageCode / @codeListValue");
        locales = Functional.map("x.toLowerCase()", locales);
        if(excludeMain) {
            return locales;
        } else {
            return [ExtGeoNet.config.Mediator.language.main(metadata)].concat(locales);
        }
    },
    main : function(metadata) {
        if(metadata === undefined) {
            metadata = ExtGeoNet.Main.metadata;
        }
        var root = ExtGeoNet.Xml.localName(ExtGeoNet.Xml.firstEl(metadata));

        var mainLanguage = ExtGeoNet.Xml.findValue(metadata,"/ "+root+" / language / CharacterString");

        if(mainLanguage === undefined) {return ExtGeoNet.config.Mediator.language.defaultLang;}
        return Ext.util.Format.trim(mainLanguage).toLowerCase();
    }
};

    ExtGeoNet.config.Mediator.missingAtt = 'gco:nilReason="missing"';

    ExtGeoNet.config.Mediator.CharString = (function() {
    var namespaces = 'xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco"';
    var nilReason = 'gco:nilReason="missing"';
    var mainStr = '<gco:CharacterString {nilReason} '+namespaces+'>{value}</gco:CharacterString>';

    var mainTpl = new Ext.Template(mainStr);
    var newNodeTpl = new Ext.Template("<{name} "+namespaces+">\n"+mainStr+"\n</{name}>");
    var pTextTpl = new Ext.Template('<gmd:PT_FreeText>{value}</gmd:PT_FreeText>');
    var textGroupTpl = new Ext.Template('<gmd:textGroup><gmd:LocalisedCharacterString locale="#{abbr2}" {nilReason}>{value}</gmd:LocalisedCharacterString></gmd:textGroup>');

    return {

        /**
        * Updates the node with the new value
        * @param {Element} node the node to update
        * @param {Object} object with the languages as properties and values as property values
        */
        update: function (node,newValues,multilingual) {
            var medLang = ExtGeoNet.config.Mediator.language;
            var _nilReason = nilReason;
            var langXml = function(tpl, a3Code, trimEmpty) {
                var val = newValues;
                var abbr2;
                if(multilingual) {
                    abbr2 = ExtGeoNet.loc.Languages.convA3ToA2(a3Code);
                    val = newValues[abbr2];
                } 
                var nilReason = val === undefined || val.trim() == '' ? _nilReason : '';
    
                if(trimEmpty && nilReason != '') {
                    return "";
                } else {
                    return tpl.apply({
                        nilReason: nilReason,
                        value: val,
                        abbr2: abbr2
                    });
                }
            };

            var main = langXml(mainTpl, medLang.main());

            var textGroups = [];
            
            if(multilingual) {
                Ext.each(medLang.list(node.ownerDocument, true), function(lang) {
                    var xml = langXml(textGroupTpl, lang, true);
                    if(xml!='') {textGroups.push(xml);}
                });
            }
            ExtGeoNet.Xml.removeChildren(node);        
            var newNodeString = '<root '+namespaces+'>'+main+textGroups.join("\n")+'</root>';
            ExtGeoNet.Xml.appendChildren(node, newNodeString);
        },
        /**
        * Reads the value from a node in an object with the languages as properties and values as property values
        * @param {Element} node the node to read
        * @param {boolean} if true the strings will be trimmed
        * @return {Object} object with the languages as properties and values as property values
        */
        read: function (node, trim) {
          var xml = ExtGeoNet.Xml;
          var main = ExtGeoNet.loc.Languages.convA3ToA2(ExtGeoNet.config.Mediator.language.main(node.ownerDocument));
          var result = {};
          result[main] = xml.findValue(node,"/CharacterString");
          result[main] = trim ? Ext.util.Format.trim(result[main]) : result[main];

          Ext.each(ExtGeoNet.Xml.find(node,"textGroup/LocalisedCharacterString"),function(n) {
              var abbr2 = Ext.util.Format.trim(xml.findValue(n,"@locale")).toUpperCase();
              abbr2 = abbr2.match(/^#?(.*)/)[1];
              var value = xml.valueOf(n);
              value = trim ? Ext.util.Format.trim(value) : value;
  
              if(abbr2 == main) {
                  if(result[main] === undefined) {
                      result[main] = value;
                  }
              }
  
              if(Ext.util.Format.trim(value) != '') {
                  result[abbr2] = value;
              }
          });
          return result;
        },

        /**
        * Updates the node with the new value
        * @param {Element} node the node to update
        * @param {Object} object with the languages as properties and values as property values
        */
        create: function (parent,nodeName, sibling) {
            var newNodeString = newNodeTpl.apply({
                nilReason: nilReason,
                value: '',
                name: nodeName
            });

            return ExtGeoNet.Xml.insertSiblingFromText(parent, newNodeString, sibling);
        }
    };
})();

ExtGeoNet.config.Mediator.URL = null; // TODO