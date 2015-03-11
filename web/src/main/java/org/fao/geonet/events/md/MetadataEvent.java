package org.fao.geonet.events.md;

import org.springframework.context.ApplicationEvent;

/**
 * Abstract class for metadata events. Should not be used directly
 * 
 * @author delawen
 *
 */
public abstract class MetadataEvent extends ApplicationEvent {

    private static final long serialVersionUID = 456874566246220509L;

    private int mdId;

    public MetadataEvent(int mdId) {
        super(mdId);

        this.mdId = mdId;
    }

    public int getMd() {
        return mdId;
    }

}
