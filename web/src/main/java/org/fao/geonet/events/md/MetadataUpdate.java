package org.fao.geonet.events.md;


/**
 * Event launched when a metadata is updated on the database
 * 
 * @author delawen
 *
 */
public class MetadataUpdate extends MetadataEvent {

    private static final long serialVersionUID = 324534556246220509L;

    public MetadataUpdate(int mdId) {
        super(mdId);
    }

}
