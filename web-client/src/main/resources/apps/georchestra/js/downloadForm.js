Ext.namespace('GeoNetwork');

GeoNetwork.dlForm = (function() {
	
	var win;
	
	var GN_PDF_URL = GeoNetwork.Settings.DownloadFormPDF || '';
	
	var createForm = function(options,user,url) {
		
	    var ls = localStorage;
	    
	    if(user === undefined) {
	    	user = {};
	    }
	    var storeOptions = {
            autoLoad: true,
            reader: new Ext.data.JsonReader({
                root: 'rows',
                fields: ['id', 'name'],
                idProperty: 'id'
            })
        };
	    
	    if (false) {
            // we are debugging the app with "mvn jetty:run"
            // we do not want to deploy dlform webapp to get this list
            // i18n: we let that strings hardcoded, as they are for debugging
            // purposes
            storeOptions.data = {
                "rows": [
                    {"id": 1, "name": "Administratif et budgétaire"},
                    {"id": 2, "name": "Aménagement du Territoire et Gestion de l'Espace"},
                    {"id": 3, "name": "Communication"},
                    {"id": 4, "name": "Environnement"},
                    {"id": 5, "name": "Fond de Plan"},
                    {"id": 6, "name": "Foncier et Urbanisme"},
                    {"id": 7, "name": "Formation"},
                    {"id": 8, "name": "Gestion du Domaine Public"},
                    {"id": 9, "name": "Mise en valeur du Territoire (Tourisme)"},
                    {"id": 10, "name": "Risques Naturels et Technologiques"}
                ]
            };
        } else {
            // use the dedicated dlform webservice.
            storeOptions.url = '/downloadform/data_usage';
        }

	    var formPanelItems = [
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
	            value: dl_getCookie('JSESSIONID')
	        },
	        // regular fields:
	        {
	            fieldLabel: 'Prénom',
	            labelStyle: 'font-weight:bold;',
	            name: 'first_name',
	            value: user.surname || (ls && ls.getItem('first_name')) || '', 
	            allowBlank: false
	        },{
	            fieldLabel: 'Nom',
	            labelStyle: 'font-weight:bold;',
	            name: 'last_name',
	            value:  user.name || (ls && ls.getItem('last_name')) || '',
	            allowBlank: false
	        },{
	            fieldLabel: 'Organisme',
	            labelStyle: 'font-weight:bold;',
	            value: user.organisation ||  (ls && ls.getItem('company')) || '',
	            name: 'company',
	            allowBlank: false
	        }, {
	            fieldLabel: 'Email',
	            labelStyle: 'font-weight:bold;',
	            name: 'email',
	            vtype: 'email',
	            value: user.email ||  (ls && ls.getItem('email')) || '',
	            allowBlank: false
	        }, {
	            fieldLabel: 'Téléphone',
	            value: user.phone || (ls && ls.getItem('tel')) || '',
	            name: 'tel'
	        },
	        // data use (refer to ext-ux/MultiSelect.js and not the one used by GN)
	        {
	            xtype: 'multiselect2',
	            fieldLabel: "Applications",
	            labelStyle: 'font-weight:bold;',
	            name: 'datause',
	            height: 120,
	            allowBlank: false,
	            displayField: 'name',
	            valueField: 'id',
	            minSelections: 1,
	            store: new Ext.data.Store(storeOptions)
	        },
	        // comment
	        {
	            xtype:'htmleditor',
	            fieldLabel:'Commentaires',
	            name: 'comment',
	            height: 150
	        }
	    ];

	    if (GN_PDF_URL) {
	    	formPanelItems.push({
                xtype:'checkboxgroup',
                allowBlank: false,
                blankText: "dlform.blanktext",
                columns: 1,
                items: [{
                    boxLabel: ["<span style='font-weight:bold;'>J'accepte sans réserve les <a href='",
                               GN_PDF_URL,
                               "' target='_blank'>conditions d'utilisation</a> des données.</span>"].join(''),
                    name: 'ok'
                }]
            });
	    }
    
	    return new Ext.FormPanel({
	        labelWidth: 100,
	        region: 'center',
	        standardSubmit: false,
	        monitorValid: true,
	        frame: true,
	        bodyStyle:'padding:5px 5px 0',
	        defaults: {
	            width: 550
	        },
	        defaultType: 'textfield',
	        labelSeparator: ' : ',
	        items: formPanelItems,
	        buttons: [{
	            text: 'OK',
	            formBind: true,
	            handler: function() {
	            	var fp = this.ownerCt.ownerCt,
                    form = fp.getForm();
                if (form.isValid()) {
                    var v = form.getValues();

                    // save form fields in local storage if not connected.
                    if (ls) {
                        var fields = ['first_name', 'last_name', 'company', 'email', 'tel'];
                        if (!user || !user.username) {
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

                    var submitOptions = {
                        // requires dlform webapp to be deployed:
                        url: '/downloadform/geonetwork',
                        success: function() {
                            options.callback.call();
                        }
                    };
                    
                    //TODO : failure method
                    
                    // We do not want to block the app when running with jetty
                    // in this particular case, the success callback always gets executed
                    // Note: this is no security breach, since the extractorapp "initiate" controler
                    // always checks that the form has been validated before the job is done.
//                    submitOptions.failure = (GEOR.data.jettyrun) ?
//                        submitOptions.success : function(form, action) {
//                        switch (action.failureType) {
//                            case Ext.form.Action.CLIENT_INVALID:
//                                // should not happen, since we have formBind
//                                GEOR.util.errorDialog({
//                                    msg: tr("Invalid form")
//                                });
//                                break;
//                            case Ext.form.Action.CONNECT_FAILURE:
//                                GEOR.util.errorDialog({
//                                    msg: tr("dlform.save.error")
//                                });
//                                break;
//                            case Ext.form.Action.SERVER_INVALID:
//                                GEOR.util.errorDialog({
//                                    msg: action.result.msg
//                                });
//                        }
//                    };
                    form.submit(submitOptions);
	                }
	            }
	        }]
	    });
	};
		
	/* public */
	return {
		
		show: function(options,user,url) {
			
			win = new Ext.Window({
		        title: "Prenez quelques instants pour nous indiquer l'utilisation des données",
		        constrainHeader: true,
		        layout: 'fit',
		        border: false,
		        width: 700,
		        height: 600,
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
		                    id: options.id,
		                    fname: options.fname,
		                    callback: function() {
		                        win.items.get(0).layout.setActiveItem(1);
		                    }
		                },user,url)]
		            }, {
		                bodyStyle: "padding: 10px",
		                html: '<font size="6">Merci.<br /><br /><a href="'+url+
		                    '" target="_blank">Télécharger le fichier</a></font>'
		            }]
		        }]
		    });
		    win.show();
		}
    };
})();
	
// see http://www.w3schools.com/js/js_cookies.asp
var dl_getCookie = function(name) {
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
