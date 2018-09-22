package org.icemoon.worldeditor.components;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;

public abstract class AjaxMouseEventBehavior extends AbstractDefaultAjaxBehavior {
	
	private static final long serialVersionUID = -2789032970017954587L;

	public enum EventType {
		CLICK,DBLCLICK,MOUSEDOWN,MOUSEENTER,MOUSELEAVE, MOUSEMOVE, MOUSEOUT, MOUSEOVER, MOUSEUP
	}

	private EventType[] events;

	@Override
	public final CharSequence getCallbackUrl() {
		return super.getCallbackUrl()
			+ "&x=' + ( e.pageX - $(e.target).parent().offset().left ) + '&y=' + ( e.pageY - $(e.target).parent().offset().top ) + '&t=' + e.type + '";
	}

	public AjaxMouseEventBehavior(EventType... events) {
		this.events = events;
	}

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		for (EventType event : events) {
			response.render(OnDomReadyHeaderItem.forScript("$('#" + getComponent().getMarkupId() + "').bind('" + event.name().toLowerCase() +"', function(e) { e.preventDefault(); e.stopImmediatePropagation() ;"
				+ getCallbackScript() + " });"));
		}
	}

	@Override
	protected final void respond(AjaxRequestTarget target) {
		Request request = RequestCycle.get().getRequest();
		double x = request.getQueryParameters().getParameterValue("x").toDouble();
		double y = request.getQueryParameters().getParameterValue("y").toDouble();
		EventType type = EventType.valueOf(request.getQueryParameters().getParameterValue("t").toString().toUpperCase());
		onMouseEvent(target, type, x, y);
	}

	protected abstract void onMouseEvent(AjaxRequestTarget target, EventType type, double x, double y);
}