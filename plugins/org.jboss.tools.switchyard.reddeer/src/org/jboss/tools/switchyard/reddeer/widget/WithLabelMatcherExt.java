package org.jboss.tools.switchyard.reddeer.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.jboss.reddeer.swt.handler.WidgetHandler;
import org.jboss.reddeer.swt.lookup.WidgetLookup;
import org.jboss.reddeer.swt.util.Display;

public class WithLabelMatcherExt extends BaseMatcher<String> {

	private java.util.List<Control> allWidgets;

	protected Matcher<String> matcher;

	public WithLabelMatcherExt(String label) {
		matcher = Is.<String> is(label);
	}

	@Override
	public boolean matches(Object obj) {
		if (!(obj instanceof Text || obj instanceof Combo || obj instanceof org.eclipse.swt.widgets.List)) {
			return false;
		}
		
		findAllWidgets();

		int widgetIndex = allWidgets.indexOf(obj);
		ListIterator<? extends Widget> listIterator = allWidgets.listIterator(widgetIndex);
		while (listIterator.hasPrevious()) {
			Widget previousWidget = listIterator.previous();
			if (isLabel(previousWidget)) {
				String label = WidgetHandler.getInstance().getText(previousWidget);
				if (matcher.matches(label)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void describeTo(Description desc) {
	}

	private boolean isLabel(Widget widget) {
		return widget instanceof Label || widget instanceof CLabel;
	}

	private List<Control> findAllWidgets() {
		final Control activeControl = WidgetLookup.getInstance().getActiveWidgetParentControl();
		allWidgets = new ArrayList<Control>();
		Display.syncExec(new Runnable() {

			@Override
			public void run() {
				findWidgets(activeControl);
			}
		});
		return allWidgets;
	}

	private void findWidgets(Control control) {
		allWidgets.add(control);
		if (control instanceof Composite) {
			Composite composite = (Composite) control;
			for (Control child : composite.getChildren()) {
				findWidgets(child);
			}
		}
	}
}
