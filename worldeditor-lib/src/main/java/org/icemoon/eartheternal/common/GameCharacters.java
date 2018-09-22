package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;
import org.icemoon.eartheternal.common.Appearance.Race;

@SuppressWarnings("serial")
public class GameCharacters extends AbstractSeparateINIFileEntities<GameCharacter, Long, String, IUserData>
		implements BaseCreatures<GameCharacter, IUserData> {
	public static final String NEXT_CHARACTER_ID = "NextCharacterID";
	public static final int CHARACTER_ID_INCREMENT = -8;
	private CreaturesSupport<GameCharacter> cs = new CreaturesSupport<GameCharacter>();

	public GameCharacters(String root) {
		this(null, root);
	}

	public GameCharacters(IUserData database, String root) {
		super(database, Long.class, root);
		setMinId(1l);
		setMaxId(Long.valueOf(Integer.MAX_VALUE));
	}

	@Override
	protected void doLoad() throws IOException {
		// TODO Auto-generated method stub
		super.doLoad();
	}

	@Override
	public GameCharacter getByDisplayName(String displayName) {
		return cs.getByDisplayName(displayName, values());
	}

	@Override
	public List<GameCharacter> getByLevel(int level) {
		return cs.getByLevel(level, values());
	}

	@Override
	public List<GameCharacter> getByRace(Race r) {
		return cs.getByRace(r, values());
	}

	@Override
	public List<GameCharacter> getBySubName(String subName) {
		return cs.getBySubName(subName, values());
	}

	@Override
	protected GameCharacter createItem() {
		return new GameCharacter(getDatabase());
	}

	@Override
	protected Long createKey(String filename) {
		return Long.parseLong(FilenameUtils.getBaseName(filename));
	}

	@Override
	protected void createNew(GameCharacter instance) throws IOException {
		// Just append. If the package name is new, it will get created
		FileObject f = VFS.getManager().resolveFile(getFile());
		FileObject packageFile = f.resolveFile(instance.getEntityId() + ".txt");
		instance.setFile(packageFile.getName().getURI());
		INIWriter pw = new INIWriter(packageFile.getContent().getOutputStream(true));
		try {
			pw.println();
			instance.write(pw);
		} finally {
			pw.close();
		}
	}

	@Override
	public synchronized void save(GameCharacter instance) {
		instance.setFile(getFile() + "/" + instance.getEntityId() + ".txt");
		super.save(instance);
	}
}
