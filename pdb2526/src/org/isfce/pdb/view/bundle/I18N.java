package org.isfce.pdb.view.bundle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class I18N {
	private static final String GLOBAL_BUNDLE_BASE_NAME =
		"org/isfce/pdb/view/bundle/installation";
	
	private static final ResourceBundle.Control HIERARCHICAL_CONTROL =
		new HierarchicalBundleControl(GLOBAL_BUNDLE_BASE_NAME);
	
	private static final I18N INSTANCE = new I18N();
	
	public static String getString(String key) {
		return INSTANCE.getGlobalBundle().getString(key);
	}
	
	private I18N() {
		// Constructeur privé - Singleton
	}
	
	public static I18N getInstance() {
		return INSTANCE;
	}
	
	public ResourceBundle getBundle(String specificBaseName) {
		return ResourceBundle.getBundle(specificBaseName,
			Locale.getDefault(), HIERARCHICAL_CONTROL);
	}
	
	public ResourceBundle getGlobalBundle() {
		return ResourceBundle.getBundle(GLOBAL_BUNDLE_BASE_NAME,
			Locale.getDefault());
	}
	
	public Properties getProperties() {
		Properties props = new Properties();
		ResourceBundle bundle = getGlobalBundle();
		for (String key : bundle.keySet()) {
			props.setProperty(key, bundle.getString(key));
		}
		return props;
	}

}
