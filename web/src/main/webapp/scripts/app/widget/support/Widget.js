Ext.ns('ExtGeoNet.widget.support');

ExtGeoNet.widget.support.Widget = {
    config : function(widget) {
        widget.name = widget.name === undefined ? Ext.util.Format.trim(widget.nodes.nodeName) : widget.name;
        widget.value = widget.value === undefined ? Ext.util.Format.trim(widget.nodes.textContent) : widget.value;
    },

    multilingualUpdate : function(obj,changeEvent) {
        var node = obj.nodes;
        var NEED_TRANSLATION_STYLE = 'x-form-empty-field';

        var values = ExtGeoNet.config.Mediator.CharString.read(node,true);
        Ext.apply(obj,values);

        var languageCombo = ExtGeoNet.Main.languageCombo;
        var currentLang = languageCombo.getLanguage();

        obj.setValue(obj[currentLang]);

        var showLang = function() {
            if (obj[languageCombo.getLanguage()] === undefined) {
              obj.addClass(NEED_TRANSLATION_STYLE);
            } else {
              obj.removeClass(NEED_TRANSLATION_STYLE);
              obj.setValue(obj[languageCombo.getLanguage()]);
            }
        };
        obj.mon(languageCombo,'select', showLang);

        obj.on('focus', function() {
            obj.selectText();
            obj.removeClass(NEED_TRANSLATION_STYLE);
            if(Ext.util.Format.trim(obj.getValue()) != '' &&
                        obj[languageCombo.getLanguage()] !== obj.getValue()) {
                ExtGeoNet.config.Mediator.CharString.update(node,obj,true);
                obj[languageCombo.getLanguage()] = obj.getValue();
            }
        });

        obj.on(changeEvent, function(text, newValue, oldValue){
            // TODO correct update
            if(Ext.util.Format.trim(newValue) == '') {
                obj[languageCombo.getLanguage()] = undefined;
            } else {
                obj[languageCombo.getLanguage()] = newValue;
            }
            ExtGeoNet.config.Mediator.CharString.update(node,obj,true);
        });
    },
    charStringUpdate: function(obj,changeEvent) {
        var node = obj.nodes;

        var values = ExtGeoNet.config.Mediator.CharString.read(node,true);
        Ext.apply(obj,values);

        var langCode = ExtGeoNet.loc.Languages.convA3ToA2(ExtGeoNet.config.Mediator.language.main());
        obj.setValue(obj[langCode]);

        obj.on(changeEvent, function(text, newValue, oldValue){
            obj[langCode] = newValue;
            ExtGeoNet.config.Mediator.CharString.update(node,newValue,false);
        });
    },
    simpleUpdate: function(obj,changeEvent) {
        var node = obj.nodes;

        obj.setValue(node.textContent);

        obj.on(changeEvent, function(text, newValue, oldValue){
            node.textContent = newValue;
        });
    }
    
};