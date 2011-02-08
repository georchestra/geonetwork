Ext.ns('ExtGeoNet.config.Layouts');

ExtGeoNet.config.Layouts.Summary = {
	title: 'Summary',
	tabTip: 'SummaryTabTip',
	fields: [
	    {Title: '*/gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:title'},
		{Abstract : 'abstract'},
		{
		    name : 'Keywords',
		    config : {
                xpath : 'descriptiveKeywords',
                multi : true
		    }
		},
		{
		    name : 'Contact',
		    config : {
                xpath : 'gmd:pointOfContact',
                multi : true
		    }
	    }
	]
};

ExtGeoNet.config.Layouts.Basic = {
    title: 'Standard',
    tabTip: 'StandardTip',
    fields:[{html: '<h1>Standard</h1>'}],
    children: [
        {tabTip: 'Sub1TabTip',title:'Sub1',items:[{html: '<h1>Sub1</h1>'}]}
    ]
};
ExtGeoNet.config.Layouts.Complete = {
	title: 'Complete',
	tabTip: 'CompleteTabTip',
	fields: [{
		html: '<h1>Complete</h1>'
	}]
};

ExtGeoNet.config.Layouts.Xml = {
	title: 'Xml',
	tabTip: 'XmlTabTip',
	fields: {
	    mapping : 'XML',
	    xpath : '/'
	}
};

ExtGeoNet.config.Layouts.items = [ExtGeoNet.config.Layouts.Summary,  ExtGeoNet.config.Layouts.Basic, 
                            ExtGeoNet.config.Layouts.Complete, ExtGeoNet.config.Layouts.Xml];

