package org.icemoon.worldeditor.behaviors;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * Turns a normal text area into a an editor via the EditArea javascript library
 * that supports syntax highlighting.
 * <p>
 * See {@linkplain http://www.cdolivet.com/editarea/editarea/docs/} for info
 * about EditArea. The syntax scheme can be set by passing the string to the
 * constructor.
 * </p>
 * 
 * @author aaime
 */
@SuppressWarnings("serial")
public class CodeMirrorBehavior extends AbstractAjaxBehavior {

	public interface CodeMirrorSettings extends Serializable {

		Collection<String> getAdditionalCss();

		String getMode();
		
		boolean isLineWrapping();

		boolean isHighlightSelection();

		boolean isHighlightCurrentLine();

		boolean isShowLineNumbers();

		boolean isMatchBrackets();

		Map<String, String> getExtraKeys();

		String getTheme();

		boolean isEnableHints();

		Collection<String> getAddOns();

	}

	public static class DefaultCodeMirrorSettings implements CodeMirrorSettings, Serializable {
		private String mode = "javascript";
		private boolean showLineNumbers = true;
		private boolean matchBrackets = true;
		private Map<String, String> extraKeys = new HashMap<String, String>();
		private final static CodeMirrorSettings instance = new DefaultCodeMirrorSettings();
		private final static Properties mimeTypeMap = new Properties();
		private String theme;
		private boolean highlightSelection;
		private boolean highlightCurrentLine;
		private boolean enableHints;
		private boolean lineWrapping;
		private List<String> addOns = new ArrayList<String>();
		private List<String> additionalCss = new ArrayList<String>();

		static {
			try {
				InputStream in = CodeMirrorBehavior.class.getResourceAsStream("mimeToMode.properties");
				if (in == null) {
					throw new IOException("Could not locate mimeToMode.properties. Is it on the class path?");
				}
				try {
					mimeTypeMap.load(in);
				} finally {
					in.close();
				}
			} catch (IOException ioe) {
				throw new RuntimeException("Failed to load mime type to mode map. " + ioe.getMessage());
			}
		}

		public DefaultCodeMirrorSettings() {
			this("javascript");
			extraKeys.put("Enter", "newlineAndIndentContinueComment");
		}

		public DefaultCodeMirrorSettings(String mode) {
			this.mode = mode;
		}

		public boolean isLineWrapping() {
			return lineWrapping;
		}

		public void setLineWrapping(boolean lineWrapping) {
			this.lineWrapping = lineWrapping;
		}

		public boolean isEnableHints() {
			return enableHints;
		}

		public void setEnableHints(boolean enableHints) {
			this.enableHints = enableHints;
			if(enableHints) {
				extraKeys.put("Ctrl-Space", "autocomplete");
			}
			else {
				extraKeys.remove("Ctrl-Space");
			}
		}

		public boolean isHighlightSelection() {
			return highlightSelection;
		}

		public void setHighlightSelection(boolean highlightSelection) {
			this.highlightSelection = highlightSelection;
		}

		public boolean isHighlightCurrentLine() {
			return highlightCurrentLine;
		}

		public void setHighlightCurrentLine(boolean highlightCurrentLine) {
			this.highlightCurrentLine = highlightCurrentLine;
		}

		public static CodeMirrorSettings get() {
			return instance;
		}

		public Map<String, String> getExtraKeys() {
			return extraKeys;
		}

		public void setExtraKeys(Map<String, String> extraKeys) {
			this.extraKeys = extraKeys;
		}

		public String getTheme() {
			return theme;
		}

		public void setTheme(String theme) {
			this.theme = theme;
		}

		public String getMode() {
			return mode;
		}

		public void setMode(String mode) {
			this.mode = mode;
		}

		public boolean isShowLineNumbers() {
			return showLineNumbers;
		}

		public void setShowLineNumbers(boolean showLineNumbers) {
			this.showLineNumbers = showLineNumbers;
		}

		public boolean isMatchBrackets() {
			return matchBrackets;
		}

		public void setMatchBrackets(boolean matchBrackets) {
			this.matchBrackets = matchBrackets;
		}

		public void setModeFromMimeType(String contentType) {
			if (mimeTypeMap.containsKey(contentType)) {
				this.mode = mimeTypeMap.getProperty(contentType);
				return;
			}
			throw new IllegalArgumentException("No mapped mode for content type " + contentType);

		}

		public void addAddOn(String addOn) {
			addOns.add(addOn);
		}

		public void addCss(String css) {
			additionalCss.add(css);
		}

		@Override
		public Collection<String> getAdditionalCss() {
			List<String> l = new ArrayList<String>(additionalCss);
			if (isEnableHints()) {
				l.add("addon/hint/show-hint.css");
			}
			return l;
		}

		@Override
		public Collection<String> getAddOns() {
			List<String> l = new ArrayList<String>();
			if (isHighlightCurrentLine()) {
				l.add("addon/selection/active-line.js");
			}
			if (isHighlightSelection()) {
				l.add("addon/search/searchcursor.js");
				l.add("addon/search/match-highlighter.js");
			}

			if (isEnableHints()) {
				l.add("addon/hint/show-hint.js");

				String baseName = CodeMirrorBehavior.class.getPackage().getName().replace(".", "/");
				String name = getHinterPath();
				URL url = Thread.currentThread().getContextClassLoader().getResource(baseName + "/" + name);
				if (url != null) {
					try {
						url.openStream();
						l.add(name);
					} catch (IOException ioe) {
					}
				}
			}
			l.addAll(addOns);
			return l;
		}

		protected String getHinterPath() {
			String name = "addon/hint/" + getMode() + "-hint.js";
			return name;
		}
	}

	private IModel<? extends CodeMirrorSettings> settings;
	private boolean leaveSubmitMethodAlone;

	public CodeMirrorBehavior() {
		this("javascript");
	}

	public CodeMirrorBehavior(String syntax) {
		this(new Model<DefaultCodeMirrorSettings>(new DefaultCodeMirrorSettings(syntax)));
	}
	public CodeMirrorBehavior(CodeMirrorSettings settings) {
		this(new Model<CodeMirrorSettings>(settings));
	}

	public CodeMirrorBehavior(IModel<? extends CodeMirrorSettings> settings) {
		this.settings = settings;
	}

	public boolean isLeaveSubmitMethodAlone() {
		return leaveSubmitMethodAlone;
	}

	public CodeMirrorBehavior setLeaveSubmitMethodAlone(boolean leaveSubmitMethodAlone) {
		this.leaveSubmitMethodAlone = leaveSubmitMethodAlone;
		return this;
	}

	public void refresh(AjaxRequestTarget target) {
		target.appendJavaScript(getGlobalVariableName() + ".refresh();");
	}

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		doRenderHead(response);
	}

	public void doRenderHead(IHeaderResponse response) {
		CodeMirrorSettings settingsObject = settings.getObject();
		if (settingsObject != null) {
			response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(CodeMirrorBehavior.class,
				"lib/codemirror.js")));
			response.render(CssHeaderItem.forReference(new CssResourceReference(CodeMirrorBehavior.class, "lib/codemirror.css")));
			String theme = settings.getObject().getTheme();
			if (StringUtils.isNotBlank(theme)) {
				response.render(CssHeaderItem.forReference(new CssResourceReference(CodeMirrorBehavior.class, "theme/" + theme
					+ ".css")));
			}
			String mode = settingsObject.getMode();
			if (StringUtils.isNotBlank(mode)) {
				response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(CodeMirrorBehavior.class, "mode/"
					+ mode + "/" + mode + ".js")));
				response.render(OnDomReadyHeaderItem.forScript(getRenderJavascript()));
			}

			// Adds ons
			for (String addOn : settingsObject.getAddOns()) {
				response
					.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(CodeMirrorBehavior.class, addOn)));
			}
			for (String css : settingsObject.getAdditionalCss()) {
				response.render(CssHeaderItem.forReference(new CssResourceReference(CodeMirrorBehavior.class, css)));
			}

		}
	}

	@Override
	protected void onComponentRendered() {
		super.onComponentRendered();
		CodeMirrorSettings settingsObject = settings.getObject();
		// if (settingsObject != null) {
		// Component component = getComponent();
		// Response response = component.getResponse();
		// response.write("<script type='text/javascript'>");
		// response.write(getRenderJavascript());
		// response.write("</script>");
		// }
	}

	@Override
	protected void onUnbind() {
		super.onUnbind();
	}

	protected String getRenderJavascript() {
		if (getComponent() == null) {
			throw new IllegalStateException("CodeMirrorBehavior is not bound to a component");
		}
		StringBuilder bui = new StringBuilder();
		CodeMirrorSettings object = settings.getObject();

		if (object.isEnableHints()) {
			String functionName = getComponent().getMarkupId() + "OnHintClose ";
			if (StringUtils.isNotBlank(getOnHintCloseScript())) {
				bui.append("var ");
				bui.append(functionName);
				bui.append(" = function(cm) { ");
				bui.append(getOnHintCloseScript());
				bui.append("};");
			}

			bui.append("CodeMirror.commands.autocomplete = function(cm) { ");
			bui.append(getOnHintPopupScript());
			bui.append("CodeMirror.showHint(cm, CodeMirror.javascriptHint");
			if (StringUtils.isNotBlank(getOnHintCloseScript())) {
				bui.append(", { onClose: ");
				bui.append(functionName);
				bui.append(" }");
			}
			bui.append(");");
			bui.append(" };");

		}

		bui.append(getGlobalVariableName() + " = CodeMirror.fromTextArea(document.getElementById('");
		bui.append(getComponent().getMarkupId());
		bui.append("'), {");
		bui.append("leaveSubmitMethodAlone: ");
		bui.append(leaveSubmitMethodAlone);
		bui.append(",lineWrapping: " + object.isLineWrapping());
		bui.append(",lineNumbers: " + object.isShowLineNumbers() + ",");
		if (StringUtils.isNotBlank(object.getMode())) {
			bui.append("mode: '" + object.getMode() + "',");
		}
		if (StringUtils.isNotBlank(object.getTheme())) {
			bui.append("theme: '" + object.getTheme() + "',");
		}
		bui.append("highlightSelectionMatches: " + object.isHighlightSelection() + ",");
		bui.append("styleActiveLine: " + object.isHighlightCurrentLine() + ",");
		bui.append("matchBrackets: " + object.isMatchBrackets() + ",");
		bui.append("extraKeys: {");
		for (Iterator<Map.Entry<String, String>> it = object.getExtraKeys().entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> k = it.next();
			bui.append("'" + k.getKey() + "': '" + k.getValue() + "'");
			if (it.hasNext()) {
				bui.append(",");
			}
		}
		bui.append("}");
		bui.append("});");
		bui.append(getGlobalVariableName());
		bui.append(".on(\"blur\", function(cm){");
		bui.append("cm.save();");
		bui.append("});");
		return bui.toString();
	}

	public IModel<? extends CodeMirrorSettings> getSettings() {
		return settings;
	}

	public void setSettings(IModel<? extends CodeMirrorSettings> settings) {
		this.settings = settings;
	}

	public String getOnHintPopupScript() {
		return "";
	}

	public String getOnHintCloseScript() {
		return "";
	}

	public String getGlobalVariableName() {
		String codeMirrorGlobalVariableName = "document." + getComponent().getMarkupId() + "CodeMirror";
		return codeMirrorGlobalVariableName;
	}

	@Override
	public void onBind() {
		super.onBind();
		getComponent().setOutputMarkupId(true);
	}

	@Override
	public void onRequest() {
	}
}
