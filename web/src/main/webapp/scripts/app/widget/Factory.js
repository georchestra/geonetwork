Ext.ns('ExtGeoNet.widget');

ExtGeoNet.widget.Factory = { 
  mapping : function (mapping) {
    
    if(mapping !== undefined) {
        Ext.applyIf(mapping, ExtGeoNet.widget.DefaultMapping);
    } else if (ExtGeoNet.config !== undefined && ExtGeoNet.config.Mapping !== undefined) {
        mapping = ExtGeoNet.config.Mapping;
        Ext.applyIf(mapping, ExtGeoNet.widget.DefaultMapping);
    } else {
      mapping = ExtGeoNet.widget.DefaultMapping;
    }
    
    return mapping;
  },

  create : function (fieldLabel,mappingKey,config,metadata) {
    var xpath = config;
    if(config.xpath !== undefined) {
        xpath = config.xpath;
    }
    var nodes = ExtGeoNet.Xml.find(metadata, xpath);
          
    if(nodes.length > 0) {
        if(!config.multi) {
            var f = Functional.curry(ExtGeoNet.widget.Factory.toWidget, fieldLabel, mappingKey);
            return Functional.map(f, nodes);
        } else {
            return [ExtGeoNet.widget.Factory.toWidget(fieldLabel, mappingKey, nodes, config.multi)];
        }
    }
    return [];
  },

  toWidget : function (fieldLabel,mappingKey, nodes, multi) {
    var node;
    // TODO handle case where nodes === undefined or is an empty array
    if(nodes instanceof Array) {
        node = nodes[0];
    } else {
        node = nodes;
    }
    var mapping = ExtGeoNet.widget.Factory.mapping();

    var type;
    if(mappingKey !== undefined) {
        type = mapping[mappingKey.toUpperCase()];
    } else {
        type = mapping[node.nodeName.toUpperCase()];    
    }
    if(type === undefined) {
      type = mapping[node.nodeName.toUpperCase()];
    }
    if(type === undefined) {
      type = mapping[ExtGeoNet.Xml.localName(node).toUpperCase()];
    }
    if(type === undefined) {
      type = mapping[fieldLabel.toUpperCase()];
    }
    if(type === undefined) {
      type = mapping.def;
    }
    
    return type(nodes, {
      fieldLabel : translate(fieldLabel)
    });
  },
  createItems: function(metadata, dfn) {
		var fieldToItem = function(field) {
          var fieldName = field.name;
          var mappingKey = field.mapping;
          var config = field.config;
          if (config === undefined) {
            config = field.xpath;
          }


          if(fieldName !== undefined || mappingKey !== undefined || config !== undefined) {
            return ExtGeoNet.widget.Factory.create(fieldName,mappingKey, config,metadata);
          } else if (field.xtype !== undefined) {
            return field;
          } else {
            for(var fn in field) {
              var xpath = field[fn];
              return ExtGeoNet.widget.Factory.create(fn,undefined, xpath,metadata);
            }
          }

		};
        var items;
		if(dfn.fields !== undefined) {
		    if(dfn.fields instanceof Array) {
			    items = Functional.map(fieldToItem, dfn.fields);
			} else {
			    items = [fieldToItem(dfn.fields)];
			}
		} else if(dfn instanceof Array) {
		    items = Functional.map(fieldToItem, dfn);
		} else {
		    alert("no items found for tab in definition"+Ext.util.JSON.encode(dfn));
		    return undefined;
		}

		return items;
	}
}
