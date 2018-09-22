package Icon;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class Icons {
	
	private static Map<String, ResourceReference> cache =new HashMap<String, ResourceReference>();
	
	public static ResourceReference getIconResource(String name) {
		if(!cache.containsKey(name)) {
			cache.put(name, new PackageResourceReference(Icons.class, name));
		}
		return cache.get(name);
	}
}
