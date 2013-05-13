package jeeves.server.overrides;

import org.jdom.Element;

class AddInterceptUrlUpdater extends AbstractInterceptUrlUpdater {

    private final String access;

    public AddInterceptUrlUpdater(Element element) {
        super(element);
        this.access = element.getAttributeValue("access");
    }

    @Override
    protected void update(OverridesMetadataSource overrideSource) {
        overrideSource.addMapping(pattern, access);
    }

}