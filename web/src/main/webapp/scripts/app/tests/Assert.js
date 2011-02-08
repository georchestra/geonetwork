Ext.ns("ExtGeoNet.test.Assert");

ExtGeoNet.test.Assert.assert = function(expected,actual,msg) {
    var error = function() {
        var error = "Test Failure: "+msg+" expected "+expected+" but got "+actual;
        throw(error);
    };
  if(expected instanceof Array && actual instanceof Array) {
      if(expected.length != actual.length) error();

      for(i = 0; i < expected.length; i++) {
          try {ExtGeoNet.test.Assert.assert(expected[i], actual[i]);} 
          catch (e) {error();}
      }
      return; // test passed
  }
  
  if(expected!=actual) error();
};

ExtGeoNet.test.Assert.assertObjEquals = function(expected,actual,msg) {
    var error = function(err) {
        var error = "Test Failure: "+msg+": "+err;
        throw(error);
    };
    for(i in expected) {
      try {ExtGeoNet.test.Assert.assert(expected[i], actual[i]);} 
      catch (e) {error(e);}
    }
    for(i in actual) {
      try {ExtGeoNet.test.Assert.assert(expected[i], actual[i]);} 
      catch (e) {error(e);}
    }
};

ExtGeoNet.test.Assert.intercept = function(test, msg) {
    try {
        if(test instanceof Function) test();
        else eval(test);

        throw ("Expected an exception but did not get one");
    } catch (e) { /* good */ }
};

ExtGeoNet.test.Assert.assertBool = function(test, msg) {
    ExtGeoNet.test.Assert.assert (true, test, msg);  
};
