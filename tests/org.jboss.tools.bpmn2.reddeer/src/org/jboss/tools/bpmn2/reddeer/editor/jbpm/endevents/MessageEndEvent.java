package org.jboss.tools.bpmn2.reddeer.editor.jbpm.endevents;

import org.jboss.tools.bpmn2.reddeer.editor.ConstructType;

/**
 * 
 */
public class MessageEndEvent extends EndEvent {
	
	/**
	 * 
	 * @param name
	 */
	public MessageEndEvent(String name) {
		super(name, ConstructType.MESSAGE_END_EVENT);
	}

}
