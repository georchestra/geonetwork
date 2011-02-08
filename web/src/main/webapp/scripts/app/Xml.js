Ext.ns("ExtGeoNet");

ExtGeoNet.Xml = {
    newParser: function() {
        return new OpenLayers.Format.XML();
    },
    parser: new OpenLayers.Format.XML(),
    firstEl: function(node) {
        if(Ext.isIE) {
            var children = ExtGeoNet.Xml.children(node);
            if(children.length > 0) {
                return children[0];
            }
        } else {
            return node.firstChild;
        }
    },
    localName: function(node) {
        if(Ext.isIE) {
            return node.baseName;
        } else {
            return node.localName;
        }
    },
    matches : function(elements,nodeType,ns,name) {
        var match = function(one,two) {
            if(one === '*' || two == '*') {
                return true;
            }
            return one == two;
        };

        ns = ns === undefined || ns === null ? '*' : ns;
        name = name === undefined || name === null ? '*' : name;

        var results = [];
        Ext.each(elements, function(n) {
            if(n.nodeType === nodeType && match(ns,n.namespaceURI) && match(name, ExtGeoNet.Xml.localName(n))) {
                results.push(n);
            }
        });

        return results;
    },
    children : function(node,ns,name) {
        return ExtGeoNet.Xml.matches (node.childNodes,ExtGeoNet.Xml.ELEMENT_NODE,ns,name);
    },
    attributes : function(node,ns,name) {
        return ExtGeoNet.Xml.matches (node.attributes,ExtGeoNet.Xml.ATTRIBUTE_NODE,ns,name);
    },
    findValue : function(node,path) {
        var matches = ExtGeoNet.Xml.find(node,path);
        if(matches.length > 0) {
            return ExtGeoNet.Xml.valueOf(matches[0]);
        } else {
            return undefined;
        }
    },
    valueOf : function(node) {
        if(node.nodeType === ExtGeoNet.Xml.ELEMENT_NODE) {
            return ExtGeoNet.Xml.parser.getChildValue(node);  
        } else if(node.nodeType === ExtGeoNet.Xml.ATTRIBUTE_NODE) {
            return node.nodeValue;
        } else {
            return undefined;
        }
    },
    findValues : function(node,path) {
      var matches = ExtGeoNet.Xml.find(node,path);
      return Functional.map("ExtGeoNet.Xml.valueOf(x)",matches);
    },
    findFirst : function (node,path) {
      // TODO make efficient so not all elements are looked up
      return Ext.safeFirst(ExtGeoNet.Xml.find(node,path));
    },
    find : function (node,path) {
    var splitName = function(nodeName) {
      var parts = nodeName.split(":");
      if(parts.length == 1) {
        return {localName: parts[0]};
      } else {
        return {
          prefix: ExtGeoNet.Xml.parser.lookupNamespaceURI(node,parts[0]),
          localName: parts[1]
        };
      }
    };
    var next = function(path) {
        var mode;
        var result;
        if(path.match("^@.*") == path) {
            result = splitName(path.substring(1));
            result.mode = "@";

            return result;
        } else if(path.match("^/@.*") == path) {
            result = splitName(path.substring(2));
            result.mode = "@";

            return result;
        } else if(path.match("^//.*") == path) {
            path = path.substring(2); 
        } else if(path.match("^/.*") == path) {
            mode = "/";
            path = path.substring(1); 
        }

        var child = path.indexOf("/");
        if(child == -1) {child = path.length;}

        var finalResult = {
            mode: mode,
            rest: child == path.length ? undefined : path.substring(child, path.length)
        };

        Ext.apply(finalResult, splitName(path.substring(0,child)));
        return finalResult;
    };

        path = path.replace(/\s*/g,"");

        if(path === "/"){
            return [node];
        }

        if(path.match("^\\*/*.*")) {
            path = path.match("^\\*/*(.*)")[1];
        } else if (path.match("^//(.*)")) {
            path = path.match("^//(.*)")[1];
        }
        var token = next(path);
        var nodes = [node];
        if(node instanceof Array) {
            nodes = node;
        }
        do {
            var result = [];
            var ns = token.prefix === null ? undefined : token.prefix;

            Ext.each(nodes, function (n) {
                var more;
                if (token.mode == "@") {
                    more = ExtGeoNet.Xml.attributes(n,ns,token.localName);
                } else if (token.mode == "/") {
                    more = ExtGeoNet.Xml.children(n,ns,token.localName);
                } else if(ns === undefined) {
                    if(Ext.isIE) {
                        more = ExtGeoNet.Xml.findDescendantEl(n,token.localName);
                    } else {
                        more = Ext.DomQuery.jsSelect(token.localName,n);
                    }
                } else {
                    more = ExtGeoNet.Xml.parser.getElementsByTagNameNS(n,ns,token.localName);
                }

                Ext.each(more,function(r) {result.push(r);});
            });

            nodes = result;

            if(token.rest !== undefined) {
                token = next(token.rest);
            } else {
                token = undefined;
            }
        } while(token !== undefined);

        return nodes;
    },
    toNodeMatcher: function (seed) {
        var func;
        
        if(Ext.isNumber(seed)) {
            func = function(n,depth) {return depth === seed;};
        } else if(Ext.isString(seed)){
            func = function(n,depth) {return (ExtGeoNet.Xml.localName(n) === seed) || (n.nodeName === seed);};
        } else {
            func = seed;
        }
        return func;
    },
    findDescendantEl: function(node, matcher) {
        var toVisit = [node.childNodes];
        var depth = 0;
        var func = ExtGeoNet.Xml.toNodeMatcher(matcher);
        
        var results = [];
            
        while(toVisit[depth] !== undefined) {
            var nodes = toVisit[depth]; 
            for(var i=0; i<nodes.length; i++) {
                var currentNode = nodes[i];
                if(func(currentNode, depth + 1)) {
                    results.push(currentNode);
                }
                if(toVisit[depth+1] === undefined) {
                    toVisit[depth+1] = [];
                }
                var children = currentNode.childNodes;
                for(var j=0; j<children.length; j++) {
                    toVisit[depth+1].push(children[j]);
                }
                                    
            }
            depth += 1;
        }
        
        return results;
    },
    /**
     * node - node to start from
     * matcher - if a string it is treated as the node name of the parent to find
     *           if a function it is a function that will return true when the match is made
     *           if a number it will be the nth parent node
     */
    findParent : function(node,matcher) {
        var currentNode = node.parentNode;
        var depth = 1;
        var func = ExtGeoNet.Xml.toNodeMatcher(matcher);
            
        while(currentNode !== undefined && currentNode !== null && currentNode !== node.ownerDocument) {
            if(func(currentNode, depth)) {
                return currentNode;
            }
            depth += 1;
            currentNode = currentNode.parentNode;
        }
        
        return undefined;
    },
    removeText : function(node) {
      Ext.each(node.childNodes, function (n) {
          if(n.nodeType == ExtGeoNet.Xml.TEXT_NODE) {
              node.removeChild(n);
          }
      });
      Ext.each(node.childNodes, ExtGeoNet.Xml.removeText);
    },
    createDom : function(xmlData) {
        var parser = ExtGeoNet.Xml.newParser();
        var d = parser.read(xmlData);
        parser.destroy();
        return d;
    },
    appendChildren : function(parent, xmlString) {
        // the root of xml string will be ignored, only the child nodes will be
        // added to the parent node
        var newNode = ExtGeoNet.Xml.createDom(xmlString);

        var newNodes = [];
        Ext.each(ExtGeoNet.Xml.firstEl(newNode).childNodes, function(n) {
            newNodes.push(ExtGeoNet.Xml.appendChild(parent,n));
        });
        return newNodes;
    },
    appendChild : function(parent,newNode) {
        // newNode can be a node from another document.  It will be imported
        var importedNode = ExtGeoNet.Xml.importNode(parent, newNode, true);
        parent.appendChild(importedNode);
        return importedNode;
    },
    insertSiblingFromText : function(parent, xmlString, sibling) {
        var newNode = ExtGeoNet.Xml.createDom(xmlString);
        return ExtGeoNet.Xml.insertSibling(parent,ExtGeoNet.Xml.firstEl(newNode), sibling);
    },
    insertSibling : function(parent, newNode, sibling) {
        var importedNode = ExtGeoNet.Xml.importNode(parent, newNode,true);

        var nextSibling = sibling !== undefined ? sibling.nextElementSibling : null;
        parent.insertBefore(importedNode,nextSibling);

        return importedNode;
    },
    importNode: function(destDoc, toImport, deep) {
        if(destDoc.ownerDocument !== undefined && destDoc.ownerDocument !== null) {
            destDoc = destDoc.ownerDocument;
        }
        if (toImport.nodeType == ExtGeoNet.Xml.DOCUMENT_NODE) {
            toImport = ExtGeoNet.Xml.firstEl(toImport);
        }
        if(!Ext.isIE && false) {            
            return destDoc.importNode(toImport, deep);
        } else {
            return ExtGeoNet.Xml._importNode(toImport, deep);
        }         
    },
    _importNode: function(oNode, bImportChildren) {
        var parser = ExtGeoNet.Xml.parser;
        
        var oNew;

        if (oNode.nodeType == ExtGeoNet.Xml.ELEMENT_NODE) {
            oNew = parser.createElementNS(oNode.namespaceURI, oNode.nodeName);

            for (var i = 0; i < oNode.attributes.length; i++) {
                if (oNode.attributes[i].nodeValue !== null && oNode.attributes[i].nodeValue !== '') {
                    var attrName = oNode.attributes[i].name;

                    if (attrName == "class") {
                        oNew.setAttribute("className", oNode.attributes[i].value);
                    } else {
                        parser.setAttributeNS(oNew, oNode.attributes[i].namespaceURI, attrName, oNode.attributes[i].value);
                    }
                }
            }

        } else if (oNode.nodeType == ExtGeoNet.Xml.TEXT_NODE) {
            oNew = parser.createTextNode(oNode.nodeValue);
        } else if (oNode.nodeType == ExtGeoNet.Xml.DOCUMENT_NODE) {
            oNew = ExtGeoNet.Xml._importNode(ExtGeoNet.Xml.firstEl(oNode));
        }

        if (bImportChildren && oNode.hasChildNodes()) {
            for (var oChild = oNode.firstChild; oChild; oChild = oChild.nextSibling) {
                oNew.appendChild(ExtGeoNet.Xml._importNode(oChild, true));
            }
        }

        return oNew;
    },
    removeChildren: function(node) {
        while ( node.childNodes.length >= 1 ) {
            node.removeChild( node.firstChild );       
        } 
    }

};

(function(){
    var NodeTypes = ['ELEMENT', 'ATTRIBUTE', 'TEXT', 'CDATA_SECTION',
                     'ENTITY_REFERENCE', 'ENTITY', 'PROCESSING_INSTRUCTION',
                     'COMMENT', 'DOCUMENT', 'DOCUMENT_TYPE',
                     'DOCUMENT_FRAGMENT', 'NOTATION'];
    for(var i=0;i<NodeTypes.length;i++) {
        ExtGeoNet.Xml[NodeTypes[i] + '_NODE'] = (i + 1);
    }
  })();
