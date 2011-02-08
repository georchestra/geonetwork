Ext.ns('ExtGeoNet.widget.iso19139');

ExtGeoNet.widget.iso19139.Contact = Ext.extend(Ext.form.FieldSet, {
    initComponent: function() {
        var node = this.nodes;
        var Xml = ExtGeoNet.Xml;
        
        if(this.labelInBorder) {
            this.title = this.fieldLabel;
            this.fieldLabel = undefined;
            this.border = true;
        } else {
            this.border = false;
            this.defaults = this.defaults || {};
            padding = "padding: 0px;";
            this.style = (this.style || "") + padding + "margin: 0px;";
        }
        
        
        var fieldDefaults = {
            labelInBorder: false,
            anchor: "100%"
        };
        var createItems = ExtGeoNet.widget.Factory.createItems;
        var leftColumn = createItems (node, [
            {
                name : 'name',
                config : {
                    xpath : 'gmd:individualName',
                    multi : true
                }
            },
            {
                name : 'organisation',
                config : {
                    xpath : 'gmd:organisationName',
                    multi : true
                }
            },
            {
                name : 'position',
                config : {
                    xpath : 'gmd:positionName',
                    multi : true
                }
            }
        ]);
        leftColumn.push({
            xtype: 'fieldset',
            defaults: fieldDefaults,
            title: 'Address',
            collapsible: true,
            items: createItems(node,[
                {
                    name: 'street',
                    config: {
                        xpath: "gmd:deliveryPoint",
                        multi: true
                    }
                },
                {city: "gmd:city"},
                {country : "gmd:administrativeArea"},
                {postalCode: "gmd:postalCode"}
            ])
        });
        var rightColumn = createItems (node, [
            {email : 'gmd:electronicMailAddress'},
            {website : 'gmd:URL'},
            {
                name : 'voice',
                config : {
                    xpath : 'gmd:voice',
                    multi : true
                }
            },
            {
                name : 'fax',
                config : {
                    xpath : 'gmd:facsimile',
                    multi : true
                }
            }
        ]);
        Ext.apply(this, {
            autoHeight: true,
            layout: 'column',
            border: this.border,
            defaults: {
                xtype: 'fieldset',
                labelWidth: 75,
                columnWidth: 0.50,
                border: false,
                defaults: fieldDefaults
            },
            items: [{
                items : leftColumn
                },{
                    style: 'margin-left: 5px;',
                    items : rightColumn
                }
            ]
        });
        
        ExtGeoNet.widget.iso19139.Contact.superclass.initComponent.call(this);
    }
});

Ext.reg('md-iso19139-contact', ExtGeoNet.widget.iso19139.Contact);
