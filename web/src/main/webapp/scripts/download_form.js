
Ext.onReady(function() {
    //Ext.QuickTips.init();
    
    // Ext.Window
    var win;

    // see http://www.w3schools.com/js/js_cookies.asp
    var getCookie = function(name) {
        var i, x, y, ARRcookies = document.cookie.split(";");
        for (i=0;i<ARRcookies.length;i++) {
            x = ARRcookies[i].substr(0, ARRcookies[i].indexOf("="));
            y = ARRcookies[i].substr(ARRcookies[i].indexOf("=") + 1);
            x = x.replace(/^\s+|\s+$/g, "");
            if (x == name) {
                return unescape(y);
            }
        }
    };
    
    var createForm = function(options) {
        var ls = localStorage;
        
        var dr = Ext.data.Record.create([
            {name: 'id'},
            {name: 'name'}
        ]);
        
        return new Ext.FormPanel({
            labelWidth: 100,
            region: 'center',
            standardSubmit: false,
            //monitorValid: true,
            frame: true,
            bodyStyle:'padding:5px 5px 0',
            defaults: {
                width: 550
            },
            defaultType: 'textfield',
            labelSeparator: ' : ',
            items: [{
                    fieldLabel: 'Prénom',
                    labelStyle: 'font-weight:bold;',
                    name: 'first_name',
                    value: Geonetwork.user.first_name || (ls && ls.getItem('first_name')) || '', 
                    allowBlank: false
                },{
                    fieldLabel: 'Nom',
                    labelStyle: 'font-weight:bold;',
                    name: 'last_name',
                    value:  Geonetwork.user.last_name || (ls && ls.getItem('last_name')) || '',
                    allowBlank: false
                },{
                    fieldLabel: 'Organisme',
                    labelStyle: 'font-weight:bold;',
                    value: Geonetwork.user.company  ||  (ls && ls.getItem('company')) || '',
                    name: 'company',
                    allowBlank: false
                }, {
                    fieldLabel: 'Email',
                    labelStyle: 'font-weight:bold;',
                    name: 'email',
                    vtype: 'email',
                    value: Geonetwork.user.email ||  (ls && ls.getItem('email')) || '',
                    allowBlank: false
                }, {
                    fieldLabel: 'Téléphone',
                    value: Geonetwork.user.tel || (ls && ls.getItem('tel')) || '',
                    name: 'tel'
                },
                // data use
                {
                    xtype: 'combo',
                    fieldLabel: 'Application',
                    name: 'datause',
                    labelStyle: 'font-weight:bold;',
                    allowBlank: false,
                    forceSelection: true,
                    triggerAction: "all",
                    mode: "remote",
                    typeAhead: false,
                    editable: false,
                    displayField: 'name',
                    valueField: 'id',
                    hiddenName: 'datause', 
                    store: new Ext.data.Store({
                        // requires dlform webapp to be deployed:
                        url: '/downloadform/data_usage', 
                        //autoLoad: true,
                        reader: new Ext.data.JsonReader({
                            root: 'rows',
                            id: 'id'
                        }, dr)
                    })
                },
                // comment
                {
                    xtype:'htmleditor',
                    fieldLabel:'Commentaires',
                    name: 'comment',
                    height: 150
                },
                // checkbox
                {
                    xtype:'checkboxgroup',
                    allowBlank: false,
                    columns: 1,
                    labelSeparator: ' ',
                    items: [{
                        boxLabel: ["<span style='font-weight:bold;'>J'accepte sans réserve les <a href='",
                            Geonetwork.dlform.pdf_url,
                            "' target='_blank'>conditions d'utilisation</a> des données.</span>"].join(''),
                        name: 'ok'
                    }]
                }, 
                // hidden fields:
                {
                    xtype: 'hidden',
                    name: 'id',
                    value: options.id
                }, {
                    xtype: 'hidden',
                    name: 'fname',
                    value: options.fname
                },
                {
                    xtype: 'hidden',
                    name: 'sessionid',
                    value: getCookie('JSESSIONID')
                }
            ],
            buttons: [{
                text: 'OK',
                //formBind: true,
                handler: function() {
                    var fp = this.ownerCt;
                    var form = fp.getForm();
                    if (form.isValid()) {
                        
                        // save form fields in local storage if user is not connected.
                        if (ls) {
                            var fields = ['first_name', 'last_name', 'company', 'email', 'tel'];
                            if (!Geonetwork.user || !Geonetwork.user.username) {
                                var v = form.getValues();
                                for (var i=0,l=fields.length;i<l;i++) {
                                    ls.setItem(fields[i], v[fields[i]]);
                                }
                            } else {
                                // clear values
                                for (var i=0,l=fields.length;i<l;i++) {
                                    ls.removeItem(fields[i]);
                                }
                            }
                        }
                        
                        var df = Function.prototype.defer;
                        // before form submission, restore Ext's Function.prototype.defer:
                        Function.prototype.defer = function(millis, obj, args, appendArgs){
                            var fn = this.createDelegate(obj, args, appendArgs);
                            if(millis){
                                return setTimeout(fn, millis);
                            }
                            fn();
                            return 0;
                        };
                        
                        form.submit({
                            // requires dlform webapp to be deployed:
                            url: '/downloadform/geonetwork',
                            success: options.callback,
                            failure: function(form, action) {
                                switch (action.failureType) {
                                    case Ext.form.Action.CLIENT_INVALID:
                                        // should not happen, since we have formBind
                                        alert('Formulaire invalide');
                                        break;
                                    case Ext.form.Action.CONNECT_FAILURE:
                                        alert("Impossible de sauver le formulaire. "+
                                            "Merci de contacter l'administrateur de la plateforme");
                                        break;
                                    case Ext.form.Action.SERVER_INVALID:
                                        alert("Erreur : "+ action.result.msg);
                                }
                            }
                        });
                        
                        // after form submission, restore prototype's Function.prototype.defer:
                        Function.prototype.defer = df;
                    }
                }
            }]
        });
    };
    
    // register to event "click" on all A tags with dlform CSS class:
    Ext.select('a.dlform').on('click', function(e, t) {
        // do not follow the original link:
        e.preventDefault();
        
        var p = t.href.split('?');
        if (p.length !== 2) {
            return;
        }
        var o = Ext.urlDecode(p[1]);
        
        // open popup window with download form:
        win = new Ext.Window({
            title: "Prenez quelques instants pour nous indiquer l'utilisation des données",
            constrainHeader: true,
            layout: 'fit',
            border: false,
            width: 700,
            height: 450,
            closeAction: 'close',
            modal: true,
            items: [{
                layout: 'card',
                border: false,
                activeItem: 0,
                items: [{
                    frame: true,
                    layout: 'border',
                    defaults: {
                        border: false,
                        frame: false
                    },
                    items: [{
                        region: "north",
                        bodyStyle: "padding:5px;",
                        html: ["<div style='font-size:12px;'>Les champs en",
                            "<span style='font-weight:bold;'>gras</span>",
                            "sont obligatoires.</div>"].join(' ')
                    }, createForm({
                        id: o.id,
                        fname: o.fname,
                        callback: function() {
                            win.items.get(0).layout.setActiveItem(1);
                        }
                    })]
                }, {
                    bodyStyle: "padding: 10px",
                    html: '<font size="6">​Merci.<br /><br /><a href="'+t.href+
                        '" target="_blank">Télécharger le fichier</a></font>'
                }]
            }]
        });
        win.show();
    });

});
