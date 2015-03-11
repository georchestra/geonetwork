package org.fao.geonet.events.md;



/**
 * Event launched when a metadata is created on the database
 * 
 * @author delawen
 *
 */
public class MetadataAdd extends MetadataEvent {

    private static final long serialVersionUID = 324534556246220509L;

    public MetadataAdd(int mdId) {
        super(mdId);
    }

}
