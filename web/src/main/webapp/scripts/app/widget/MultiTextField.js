Ext.ns('ExtGeoNet.widget');

ExtGeoNet.widget.MultiTextField = Ext.extend(Ext.form.FieldSet, {
    fieldTemplate: function(x) {
        var self = this;
        
        var cls = "";
        if(x === self.nodes[0]) {
            cls += "md-multitextfield-item-first";
        } 
        if(x === self.nodes[self.nodes.length - 1 ]) {
            cls += "md-multitextfield-item-last";                
        } 
        if(cls.length === 0) {
            cls = "md-multitextfield-item-other";
        }
        
        var listeners;

        return {
            xtype: 'md-textfield',
            anchor: '100%',
            nodes: x,
            cls: cls,
            hideLabel: true,
            updateFn: self.updateFn,
            itemCls: "md-multitextfield-item",
            buttons : [{
                iconCls: 'md-add',
                handler : function(selected) {
                    var newElem = ExtGeoNet.config.Mediator.CharString.create(self.parentNode,self.nodeName,x);
                    self.multiTextAdd(self.fieldTemplate(newElem), selected);
                }
            },{
                iconCls: 'md-dup',
                handler : function(selected) {
                    var newElem = selected.nodes.cloneNode(true);
                    var parent = selected.nodes.parentNode;
                    var insertedNode = ExtGeoNet.Xml.insertSibling(parent, newElem, selected.nodes);
                    self.multiTextAdd(self.fieldTemplate(insertedNode), selected);
                }
            },{
                iconCls: 'md-delete',
                handler : function(selected) {
                    self.multiTextRemove(selected);
                    selected.nodes.parentNode.removeChild(selected.nodes);
                    return true;
                }
            }]               
        };
    },
    /**
     * Initialize the component.
     * @private
     */
    initComponent : function() {
        var self = this;
        var fieldTemplate = this.fieldTemplate;
        
        // TODO this needs to be made robust in the face of a missing element
        this.parentNode = this.nodes[0].parentNode;
        this.nodeName = this.nodes[0].nodeName;
        this.border = this.initialConfig.labelInBorder || false;
        
        if(this.initialConfig.labelInBorder) {
            this.title = this.initialConfig.fieldLabel;
            this.fieldLabel = undefined;
        } else {
            this.defaults = this.defaults || {};
            padding = "padding: 0px;";
            this.style = (this.style || "") + padding + "margin: 0px;";
        }
        
        this.items = Functional.map(fieldTemplate, this.nodes, this);
        ExtGeoNet.widget.MultiTextField.superclass.initComponent.call(this);
    },
    multiTextAdd: function(comp, sibling) {
        var index = sibling === null ? 0 : this.nodes.indexOf(sibling.nodes) + 1;
        this.nodes.splice(index,0,comp.nodes);
        this.insert(index,comp);
        this.refreshLayout();
    },
    multiTextRemove: function(comp) {
        var node = comp.nodes;
        var index = this.nodes.indexOf(node);
        this.nodes.splice(index,1);
        this.remove(comp,true);
        if(this.nodes.length === 0) {
            var newElem = ExtGeoNet.config.Mediator.CharString.create(this.parentNode, this.nodeName, node);
            this.multiTextAdd(this.fieldTemplate(newElem));
        } else {
            this.refreshLayout();
        }
    },
    refreshLayout: function() {
        var items = this.items;
        
        items.each(function(i) {
            i.removeClass("md-multitextfield-item-first");
            i.removeClass("md-multitextfield-item-last");
            i.removeClass("md-multitextfield-item-other");

            if(items.indexOf(i) === 0) {
                i.addClass("md-multitextfield-item-first");
            } else if (items.indexOf(i) == (items.length-1)) {
                i.addClass("md-multitextfield-item-last");                
            } else {
                i.addClass("md-multitextfield-item-other");                
            }
        });
        
        this.doLayout();
    }
});

Ext.reg('md-multitextfield', ExtGeoNet.widget.MultiTextField);

