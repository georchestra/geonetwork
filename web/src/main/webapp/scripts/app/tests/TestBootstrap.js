Ext.ns("ExtGeoNet.test");

ExtGeoNet.jsimport("/app/tests", [
    "SampleData.js",
    "XmlTest.js",
    "Assert.js",
    "iso19139/MediatorTest.js"
]);


ExtGeoNet.test.runTests = function() {
    var failures = []
    var run = function(test) {
        try {
            eval(test + "(test);");
        } catch (err) {
            failures.push(test + ":" + err);
        }
    }
    
    
    run("ExtGeoNet.test.XmlTest.test")
    run("ExtGeoNet.test.iso19139.Mediator.test")



    if(failures.length == 0) {
        alert("All tests passed"); 
    } else {
        alert("Test failures detected: "+failures);
    }
}
