Ext.ns("ExtGeoNet.test");

ExtGeoNet.test.XmlTest = {

  test:function(noUI) {
    var Xml = ExtGeoNet.Xml;
    var dom = ExtGeoNet.test.SampleData.dom();
    var assert = ExtGeoNet.test.Assert.assert;
    var testXPath = function(path,size) {
      assert(size, Xml.find(dom, path).length, path);
    }

    testXPath("gmd:identificationInfo//gmd:language",1);
    testXPath("//gmd:language",2);
    testXPath("/*/gmd:language",1);
    testXPath("gmd:language",2);
    testXPath("*/gmd:language",2);
    testXPath("*//gmd:language",2);
    testXPath("*//language",2);
    testXPath("/*/*",7);
    testXPath("/ * / *",7);
    testXPath("* / / gmd:language",2);
    testXPath("* / / gmd:language",2);
    assert(2, Xml.findDescendantEl(dom, "language").length, "findDescendants language")
    assert(1, Xml.find(dom, "/*/element/@ref").length, "find /*/element/@ref")
    assert(1, Xml.find(dom, "/*/element/@ref").length, "find /*/element/@ref")
    var elem = Xml.find(dom, "/*/element")
    assert("1", Xml.findValues(elem, "@ref")[0], "findValues @ref")
    assert("1", Xml.findValues(dom, "/*/element/@ref")[0], "findValues /*/element/@ref")
    assert("1", Xml.findValue(dom, "/*/element/@ref"), "findValue /*/element/@ref")
    assert("deu", Ext.util.Format.trim(Xml.findValue(dom, "/*/gmd:language/CharacterString")), "findValue /*/gmd:language/CharacterString")
    
    var elem = Xml.find(ExtGeoNet.test.SampleData.dom(),  "title//LocalisedCharacterString")[0];
    assert("title", Xml.localName(Xml.findParent(elem, "title")));
    assert("title", Xml.localName(Xml.findParent(elem, 3)));
    
    ExtGeoNet.test.XmlTest.testImportNode();
    
    ExtGeoNet.test.XmlTest.testRemoveChildren();
    
    
    if(!noUI) {
        alert("XML tests passed");
    } 
  }, 
  testImportNode: function() {
      var Xml = ExtGeoNet.Xml;
      var assert = ExtGeoNet.test.Assert.assert;
      var assertBool = ExtGeoNet.test.Assert.assertBool;
      var dom1 = ExtGeoNet.test.SampleData.dom();
      var dom2 = ExtGeoNet.test.SampleData.dom();
      
      var toImport = Xml.find(dom2, "/*/gmd:language")[0];
      var newParent = Xml.find(dom1, "/*/gmd:language")[0].parentNode;
      
      var imported = Xml.importNode(newParent, toImport, true);
          
      assert(imported.childNodes.length, toImport.childNodes.length, "Imported node has same number of children");
      
      newParent.appendChild(imported);  // no exception? good
      
      var imported2 = Xml.importNode(newParent, toImport, false);
      
      assertBool(imported2.childNodes.length == 0, "importNode deep == false should not have children");
      
      assertBool(Xml.importNode(dom2, dom1, true) !== null, "importing a document will copy entire doc");
  },
  testRemoveChildren: function() {
      var Xml = ExtGeoNet.Xml;
      var assert = ExtGeoNet.test.Assert.assert;
      var assertBool = ExtGeoNet.test.Assert.assertBool;
      var dom1 = ExtGeoNet.test.SampleData.dom();
      
      var node = Xml.find(dom1, "/*/gmd:language")[0];
      var childCount = node.childNodes.length;
      
      ExtGeoNet.Xml.removeChildren(node);
      assert(0,node.childNodes.length, "not all nodes were removed!");
      assertBool(childCount > node.childNodes.length, "not all nodes were removed!");
  }
}
