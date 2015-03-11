package org.fao.geonet.events.md;


/**
 * Event launched when a metadata is removed from the database
 * 
 * @author delawen
 *
 */
public class MetadataRemove extends MetadataEvent {

    private static final long serialVersionUID = 324534556246220509L;

    public MetadataRemove(int mdId) {
        super(mdId);
    }

}
