Ext.ns('ExtGeoNet');

Ext.QuickTips.init();
Ext.safeFirst = function(array) {
    if(array === undefined || array.length === 0) {
        return undefined;
    } else {
        return array[0];
    }
};
ExtGeoNet.jsimport("/app/", [
    "widget/extux/SuperBoxSelect.js",
    "widget/extux/RowEditor.js",
    "widget/extux/GroupTab.js",
    "widget/extux/GroupTabPanel.js",
    "widget/extux/PagingMemoryProxy.js",
    "widget/support/PagingWidget.js",
    "widget/support/Widget.js",
    "widget/support/Pager.js",
    "external/to-function.js",
    "external/functional.js",
    "widget/TextField.js",
    "widget/Keywords.js",
    "widget/TextArea.js",
    "widget/XmlArea.js",
    "widget/Factory.js",
    "widget/DefaultMapping.js",
    "widget/MultiTextField.js",
    "Main.js",
    "../editor/app.KeywordSelectionPanel.js",
    "../editor/app.SearchField.js",
    "../ext-ux/MultiselectItemSelector-3.0/DDView.js",
    "../ext-ux/MultiselectItemSelector-3.0/Multiselect.js",
    "Xml.js"
	
]);