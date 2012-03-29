Ext.ns("ExtGeoNet.widget.keyword");

ExtGeoNet.widget.keyword.record = Ext.data.Record.create([
       {name: 'value'},
       {name: 'thesaurus'},
       {name: 'uri'}
     ]);
ExtGeoNet.widget.keyword.newStore = function() {
    var store = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({
             url: 'xml.search.keywords',
             method: 'GET'
         }),
         autoLoad: false,
         paramNames: {
             limit: 'maxResults'
         },
         baseParams: {
             pNewSearch: true,
             pTypeSearch: 0,
             pKeyword: '*',
             pMode: 'searchBox'
         },
         reader: new Ext.data.XmlReader({
             record: 'keyword',
             id: 'uri'
         }, ExtGeoNet.widget.keyword.record),
         lastQuery:null,
         listeners: {
             beforeload: function (store, opts) {
                 store.baseParams.pKeyword = store.baseParams.query;
                 if(store.baseParams.pKeyword === undefined || store.baseParams.pKeyword === null || 
                     Ext.util.Format.trim(store.baseParams.pKeyword) == '') {
                         store.baseParams.pKeyword = "*";
                     }
                 if(store.lastQuery == store.baseParams.query) {
                     store.baseParams.pNewSearch = false;
                 }
             }
         }
    });

    
    return store;
};
ExtGeoNet.widget.Keywords = Ext.extend(Ext.ux.form.SuperBoxSelect, {
    
    initComponent: function() {
        Ext.apply(this, {
            store: ExtGeoNet.widget.keyword.newStore(),
            selectOnFocus: true,
            valueField:'value',
            displayField:'value',
            typeAhead:true,
            mode: 'remote',
            loadingText:'Searching...',
            autoHeight: true,
            minChars:1,
            triggerAction: 'all',
            queryDelay:0,
            allowAddNewData:true
        });
        ExtGeoNet.widget.Keywords.superclass.initComponent.call(this);
        
        
    },
    afterRender: function(cmp) {
        ExtGeoNet.widget.Keywords.superclass.afterRender.call(this, cmp);
        var items = [];
        var toRecord = function(n) {
            var val = Functional.curry(ExtGeoNet.Xml.findValue, n);
            var word = {
                value: val('keyword/CharacterString'),
                thesaurus: val('thesaurusName//title/CharacterString'),
                uri: "",
                theme: val('keyword/type/MD_KeywordTypeCode')
            };
            if(Ext.util.Format.trim(word.value) != '') {
                items.push(word);
            }
        };
        Ext.each(this.nodes, toRecord);
        this.addItems(items);
    },
    onRender: function(ct,position) {
        ExtGeoNet.widget.Keywords.superclass.onRender.call(this, ct, position);
        this.buttonFind = this.buttonWrap.createChild({
            tag:'div',
            cls: 'md-keywords-label'
        });
        
        this.buttonFind.on('click', this.showKeywordSelectionPanel);
    },
   /**
    * Property: keywordSelectionWindow
    * The window in which we can select keywords
    */
   keywordSelectionWindow:null,

   /**
    * Display keyword selection panel
    * 
    * @param ref
    */
   showKeywordSelectionPanel: function (ref) {
       if (!this.keywordSelectionWindow) {
           var keywordSelectionPanel = new app.KeywordSelectionPanel({
               listeners: {
                   keywordselected: function(panel, keywords) {
                       var xml;
                       Ext.each(keywords, function(item, index) {
                           // Format XML
                           keywords[index] = item.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>","")
                               .replace(/\"/g,"&quot;").replace(/\r\n/g,"");
                           if (first) {
                               xml = keywords[index];
                               first = false;
                           } else {
                               xml += "&amp;&amp;&amp;" + keywords[index];
                           }
                       });

                   }
               }
           });

           this.keywordSelectionWindow = new Ext.Window({
               title: translate('keywordSelectionWindowTitle'),
               width: 620,
               height: 300,
               layout: 'fit',
               items: keywordSelectionPanel,
               closeAction: 'hide',
               constrain: true
               //iconCls: 'searchIcon'
           });
       }

       this.keywordSelectionWindow.items.get(0).setRef(ref);
       this.keywordSelectionWindow.show();
   }
});

Ext.reg('md-keywords', ExtGeoNet.widget.Keywords);
     
