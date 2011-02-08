package org.fao.geonet.services.publisher;

/**
 * A Geoserver node configuration
 * 
 * @author francois
 *
 */
public class GeoserverNode {
	public GeoserverNode(String id, String name, String url, String namespacePrefix,
			String namespaceUrl, String username, String userPassword) {
		setId(id);
		setName(name);
		setUrl(url);
		setNamespacePrefix(namespacePrefix);
		setNamespaceUrl(namespaceUrl);
		setUsername(username);
		setUserpassword(userPassword);
	}
	private String id;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNamespacePrefix() {
		return namespacePrefix;
	}

	public void setNamespacePrefix(String namespacePrefix) {
		this.namespacePrefix = namespacePrefix;
	}

	public String getNamespaceUrl() {
		return namespaceUrl;
	}

	public void setNamespaceUrl(String namespaceUrl) {
		this.namespaceUrl = namespaceUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserpassword() {
		return userpassword;
	}

	public void setUserpassword(String userpassword) {
		this.userpassword = userpassword;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	private String url;
	private String namespacePrefix;
	private String namespaceUrl;
	private String username;
	private String userpassword;
}
