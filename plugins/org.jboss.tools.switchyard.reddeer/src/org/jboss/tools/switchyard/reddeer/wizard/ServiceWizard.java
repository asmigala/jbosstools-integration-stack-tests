package org.jboss.tools.switchyard.reddeer.wizard;

import org.hamcrest.core.IsNull;
import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.jface.wizard.WizardDialog;
import org.jboss.reddeer.swt.condition.JobIsRunning;
import org.jboss.reddeer.swt.condition.ShellWithTextIsActive;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.button.RadioButton;
import org.jboss.reddeer.swt.impl.link.DefaultLink;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.table.DefaultTable;
import org.jboss.reddeer.swt.impl.text.DefaultText;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.swt.matcher.RegexMatcher;
import org.jboss.reddeer.swt.wait.AbstractWait;
import org.jboss.reddeer.swt.wait.TimePeriod;
import org.jboss.reddeer.swt.wait.WaitUntil;
import org.jboss.reddeer.swt.wait.WaitWhile;
import org.jboss.tools.switchyard.reddeer.condition.TableHasRow;
import org.jboss.tools.switchyard.reddeer.editor.SwitchYardEditor;

/**
 * 
 * @author apodhrad
 * 
 */
public abstract class ServiceWizard<T extends ServiceWizard<?>> extends WizardDialog {

	private static Logger log = Logger.getLogger(SwitchYardEditor.class);

	private String dialogTitle;

	public ServiceWizard() {
		super();
	}

	public ServiceWizard(String dialogTitle) {
		super();
		this.dialogTitle = dialogTitle;
	}

	@SuppressWarnings("unchecked")
	public T activate() {
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		new DefaultShell(dialogTitle);
		AbstractWait.sleep(TimePeriod.SHORT);
		return (T) this;
	}

	public T setServiceName(String serviceName) {
		new LabeledText("Service Name:").setText(serviceName);
		return activate();
	}

	public T checkInterfaceType(String interfaceType) {
		log.info("Check interface of type '" + interfaceType + "'");
		AbstractWait.sleep(TimePeriod.SHORT);
		new RadioButton(interfaceType).click();
		AbstractWait.sleep(TimePeriod.SHORT);
		return activate();
	}

	public T selectJavaInterface(String javaInterface) {
		log.info("Select Java interface '" + javaInterface + "'");
		checkInterfaceType("Java").browse();
		new DefaultShell("");
		new DefaultText().setText(javaInterface);
		new WaitUntil(new TableHasRow(new DefaultTable(), new RegexMatcher(".*" + javaInterface + ".*")));
		new PushButton("OK").click();
		new WaitWhile(new ShellWithTextIsActive(new IsNull<String>()));
		return activate();
	}

	public T createJavaInterface(String javaInterface) {
		activate().checkInterfaceType("Java").clickInterface();
		new JavaInterfaceWizard().activate().setName(javaInterface).finish();
		return activate();
	}

	public T selectWSDLInterface(String wsdlInterface) {
		log.info("Select WSDL interface '" + wsdlInterface + "'");
		checkInterfaceType("WSDL").browse();
		new DefaultShell("Select WSDL file and portType");
		new DefaultText().setText(wsdlInterface);
		clickOK();
		return activate();
	}

	public T createWSDLInterface(String wsdlInterface) {
		checkInterfaceType("WSDL").clickInterface();
		new Java2WSDLWizard().activate().finish();
		return activate();
	}

	public T setFileName(String fileName) {
		new LabeledText("File name:").setText(fileName);
		return activate();
	}

	public void clickInterface() {
		new DefaultLink("Interface:").click();
	}

	private void clickOK() {
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		new PushButton("OK").click();
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
	}

	@Override
	public void finish() {
		super.finish(TimePeriod.VERY_LONG);
	}

	protected abstract void browse();

}
