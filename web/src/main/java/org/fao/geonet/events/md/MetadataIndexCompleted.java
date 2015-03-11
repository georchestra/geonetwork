/**
 *
 */
package org.fao.geonet.events.md;

/**
 * Event launched when the indexation of a metadata record is finished
 *
 * @author delawen
 */
public class MetadataIndexCompleted extends MetadataEvent {

    private static final long serialVersionUID = 6646733956246220509L;

    /**
     * @param metadata
     */
    public MetadataIndexCompleted(int metadataId) {
        super(metadataId);
    }

}
