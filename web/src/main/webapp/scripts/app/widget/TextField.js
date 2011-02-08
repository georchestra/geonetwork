Ext.ns('ExtGeoNet.widget');

ExtGeoNet.widget.TextField = Ext.extend(Ext.form.TextField, {
    /**
     * Initialize the component.
     * @private
     */
    initComponent : function() {
        ExtGeoNet.widget.support.Widget.config(this);
        ExtGeoNet.widget.TextField.superclass.initComponent.call(this);
        this.updateFn = this.updateFn || ExtGeoNet.widget.support.Widget.simpleUpdate;
        this.updateFn(this,'change');
    },
    onRender : function(ct, position){
        ExtGeoNet.widget.TextField.superclass.onRender.call(this, ct, position);
        
        if(this.buttons) {
            var self = this;
            var createButton = function(def) {
                var originalHandler = def.handler;
                var handler = function(evt) {
                    var close = originalHandler(self,evt);
                    if(close) {
                        mouseOver = false;
                        self.tip.hide();
                    }
                };
                def.handler = handler;
                return def;
            };
            var mouseOver = false;
            this.tip = new Ext.ToolTip({
                target: self.getId(),
                anchor: 'right',
                defaults: {
                    xtype: 'button',
                    anchor: '100%'
                    },
                layout: 'anchor',
                items: Functional.map(createButton, self.buttons),
                listeners: {
                    afterrender: function(evt) {
                        this.getEl().on('mouseenter', function(evt) {
                            mouseOver = true;
                        });
                        this.getEl().on('mouseleave', function(evt) {
                            mouseOver = false;
                            self.tip.hide();
                        });
                    },
                    beforehide: function(evt) {
                        if(mouseOver) {
                            return false;
                        }
                        return true;
                    }
                }
            });
        }
    }
});

Ext.reg('md-textfield', ExtGeoNet.widget.TextField);
