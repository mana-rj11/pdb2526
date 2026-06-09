package org.isfce.pdb.view.bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class HierarchicalBundleControl extends ResourceBundle.Control {
	
	private final String globalBaseName;
	
	public HierarchicalBundleControl(String globalBaseName) {
		this.globalBaseName = globalBaseName;
	}
	
	@Override 
	public ResourceBundle newBundle(String baseName, Locale locale,
			String format, ClassLoader loader, boolean reload)
			throws IllegalAccessException, InstantiationException, IOException {
		
		List<ResourceBundle> bundles = new ArrayList<>();
		
		// 1. charger le bundle spécifique (priorité)
		ResourceBundle specificBundle = super.newBundle(
			baseName, locale, format, loader, reload);
		if (specificBundle != null)
			bundles.add(specificBundle);
	
	
		// 2. charger le bundle global
		ResourceBundle globalBundle = super.newBundle(
			globalBaseName, locale, format, loader, reload);
		if (globalBundle != null)
			bundles.add(globalBundle);
		
		return bundles.isEmpty() ? null : new ChainedResourceBundle(bundles);
	}
}
