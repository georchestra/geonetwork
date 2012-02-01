package org.fao.geonet.services.user;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Iterator;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.apache.commons.io.IOUtils;
import org.jdom.Element;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPJSSESecureSocketFactory;
import com.novell.ldap.LDAPSearchResults;

public class JpegPhoto implements Service {

    private String sldapHost;
    private String sldapPort;
    private String sldapBase;
    private String sldapDn;
    private String sldapPwd;

    private boolean isDisabled = true ;

    private int ildapPort ;

    public Element exec(Element params, ServiceContext context) throws Exception {

        Element response = new Element("img").setAttribute("src", "/geonetwork/images/jpegphoto-na.jpg");
        response.setAttribute("alt", "jpegPhoto image");
        String uidParam =  params.getChildText("uid");
        
        String filePath = context.getAppPath() + "/images/jpegphoto-" + uidParam + ".jpg";
        
        File fJpegPhoto = new File(filePath);
        
        if (fJpegPhoto.exists()) {
            response.setAttribute("src", "/geonetwork/images/jpegphoto-" + uidParam + ".jpg");
            return response;   
        }
        
        
        
        if (isDisabled == false)
        {
            String slapAtt[] = {"jpegPhoto"} ;

            String userUidFilter = "(uid=" +  uidParam + ")";
            
            LDAPConnection lc = null;    
            try 
            {
                if ((ildapPort == 636) || (ildapPort == 10636)) {
                    lc = new LDAPConnection(new LDAPJSSESecureSocketFactory());         
                } else {
                    lc = new LDAPConnection();
                }
                lc.setSocketTimeOut(20000);
                lc.connect(sldapHost, ildapPort);

                lc.bind(LDAPConnection.LDAP_V3, sldapDn, sldapPwd.getBytes());

                LDAPSearchResults searchResults = lc.search(sldapBase,
                        lc.SCOPE_SUB, userUidFilter, slapAtt, false);

                while (searchResults.hasMore()) {
                    LDAPEntry nextEntry = searchResults.next();
                    LDAPAttributeSet attributeSet = nextEntry.getAttributeSet();

                    Iterator allAttributes = attributeSet.iterator();

                    while (allAttributes.hasNext()) {
                        LDAPAttribute attribute = (LDAPAttribute) allAttributes.next();
                        String attributeName = attribute.getName();
                        Enumeration allValues = attribute.getByteValues();

                        if (attributeName.equals("jpegPhoto")) {
                            while (allValues.hasMoreElements()) {
                                byte[]  curVal = (byte[]) allValues.nextElement();
                                FileOutputStream fos = new FileOutputStream(filePath);
                                try {
                                    fos.write(curVal);
                                } finally {
                                    IOUtils.closeQuietly(fos);
                                }
                                response.setAttribute("src","/geonetwork/images/jpegphoto-" + uidParam + ".jpg");
                                break;
                            }
                        }
                        // get only the first jpegPhoto
                        break;
                    }
                    // get only the first result
                    break;
                } // end search result

            } catch (Exception e)
            {
                throw e ;
            } finally
            {
                if (lc != null && lc.isConnected()) {
                    lc.disconnect();
                }
            }
        }

        return response;
    }

    public void init(String appPath, ServiceConfig params) throws Exception {

        LDAPConnection lc = null;
        try 
        {
            sldapHost = params.getValue("LDAPhost");
            sldapPort = params.getValue("LDAPport");
            sldapBase = params.getValue("LDAPbase");
            sldapDn   = params.getValue("LDAPbindDn");
            sldapPwd  = params.getValue("LDAPbindPassword"); 


            try 
            {
                ildapPort = Integer.parseInt(sldapPort);
            } catch (NumberFormatException nfe)
            {
                ildapPort = LDAPConnection.DEFAULT_PORT;
            }
            // sometimes it seems that the ldap server does not answer in a
            // reasonable time, that blocks the initialization of other
            // webapps from tomcat. 10 seconds should be enough to consider
            // the service as activated or not.
            lc = new LDAPConnection(10);

            if (ildapPort == 636)
            {
                lc.setSocketFactory(new LDAPJSSESecureSocketFactory());
            }
            lc.setSocketTimeOut(20000);
            lc.connect(sldapHost, ildapPort);

            lc.bind(LDAPConnection.LDAP_V3, sldapDn, sldapPwd.getBytes());

            isDisabled = false;
        }
        catch (Exception e) {
            isDisabled = true;
        } finally {
            if(lc != null && lc.isConnected()) {
                lc.disconnect();
            }
        }

    }

}
