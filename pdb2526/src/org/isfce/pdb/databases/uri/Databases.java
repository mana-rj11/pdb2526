package org.isfce.pdb.databases.uri;

public enum Databases implements IDbURL {
	H2(new H2_URL()), FIREBIRD(new Firebird_URL());

	private IDbURL iDbUrl;

	Databases(IDbURL url) {
		this.iDbUrl = url;
	}

	@Override
	public String getUrl() {
		return iDbUrl.getUrl();
	}

	@Override
	public String buildMemURL(String file) {
		return iDbUrl.buildMemURL(file);
	}

	@Override
	public String buildEmbeddedURL(String file) {
		return iDbUrl.buildEmbeddedURL(file);
	}

	@Override
	public String buildServeurURL(String file, String ip) {
		return iDbUrl.buildServeurURL(file, ip);
	}

	@Override
	public String buildServeurURL(String file, String ip, int port) {

		return iDbUrl.buildServeurURL(file, ip);
	}

	@Override
	public int getDefaultPort() {
		return iDbUrl.getDefaultPort();
	}

	@Override
	public boolean hasMemoryMode() {
		return iDbUrl.hasMemoryMode();
	}

	@Override
	public boolean hasEmbeddedMode() {
		return iDbUrl.hasEmbeddedMode();
	}

	@Override
	public boolean hasServeurMode() {
		return iDbUrl.hasServeurMode();
	}

}
