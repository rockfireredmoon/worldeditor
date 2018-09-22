import java.io.File;

import org.icemoon.eartheternal.common.DefaultDatabase;
import org.icemoon.eartheternal.common.SpawnPackage;
import org.icemoon.eartheternal.common.SpawnPackages;

public class Test1 {
	public static void main(String[] args) throws Exception {
		DefaultDatabase dbb = new DefaultDatabase(new File(System.getProperty("user.home") + "/Documents/EE/Workspaces/VALD/vald"));
		SpawnPackages sp = dbb.getSpawnPackages();
		for(SpawnPackage p : sp.values()) {
			System.out.println(p.getStartPosition() + " -> " + p.getEndPosition() + " = " + p);
		}
	}
}
