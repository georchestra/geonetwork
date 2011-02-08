package org.fao.geonet.kernel.sharedobject.support;

/**
 * User: jeichar
 * Date: Jul 23, 2010
 * Time: 1:24:14 PM
 */
interface SqlConstants {
    String TEMPLATES_TABLE = "sharedobject_templates";
    String SET_TEMPLATE_QUERY = "INSERT INTO "+TEMPLATES_TABLE+" VALUES  (?,?)";
    String TYPENAME_COL = "typename";
    String TEMPLATE_COL = "template";
    String LIST_TEMPLATES_QUERY = "SELECT " + TEMPLATE_COL + " FROM " + TEMPLATES_TABLE + " WHERE " + TYPENAME_COL + "=?";
    String ALTER_TABLE_QUERY = "ALTER TABLE %s ALTER %s TYPE text, ALTER search TYPE text";
}
