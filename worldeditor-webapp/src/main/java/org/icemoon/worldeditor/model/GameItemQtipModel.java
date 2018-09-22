package org.icemoon.worldeditor.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.Util;

@SuppressWarnings("serial")
public class GameItemQtipModel extends Model<String> {
	
	private IModel<GameItem> gameItemModel;

	public GameItemQtipModel(IModel<GameItem> gameItemModel) {
		this.gameItemModel = gameItemModel;
	}
	public String getItemId() {
		GameItem gameItem = gameItemModel.getObject();
		return	"inventory" + ( gameItem == null ? "Unknown" : gameItem.getEntityId().toString() );
	}

	public String getObject() {
		GameItem gameItem = gameItemModel.getObject();
//		StringBuilder tipContent = new StringBuilder("'"); 
//		if(gameItem == null) {
//			tipContent.append("Unknown item");
//		}
//		else {
//			tipContent.append(Util.escapeHTMLForJavaScript(gameItem.getDisplayName()));
//			tipContent.append("<br/>");
//			tipContent.append("Level ");
//			tipContent.append(gameItem.getLevel());
//			tipContent.append("<br/>");
//			tipContent.append(Util.toEnglish(gameItem.getQuality(), true));
//		}
//		tipContent.append("'");
		
		if(gameItem == null) {
			return "";
		}
		
		String tipContent = " { text: 'Loading ...', ajax: { url:'/itemtip.html?id=" + gameItem.getEntityId() + "' } }";
		
		return "$(\"#" + getItemId() + "\").qtip({ content: " + tipContent + ", show: 'mouseover', hide: 'mouseout' });";
	}
}
