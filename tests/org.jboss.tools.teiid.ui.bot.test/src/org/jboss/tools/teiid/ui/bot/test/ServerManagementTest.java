package org.jboss.tools.teiid.ui.bot.test;

import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.jboss.reddeer.eclipse.jdt.ui.packageexplorer.PackageExplorer;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.swt.condition.JobIsRunning;
import org.jboss.reddeer.swt.wait.TimePeriod;
import org.jboss.reddeer.swt.wait.WaitUntil;
import org.jboss.reddeer.swt.wait.WaitWhile;
import org.jboss.tools.teiid.reddeer.VDB;
import org.jboss.tools.teiid.reddeer.condition.ServerHasState;
import org.jboss.tools.teiid.reddeer.editor.SQLScrapbookEditor;
import org.jboss.tools.teiid.reddeer.editor.VDBEditor;
import org.jboss.tools.teiid.reddeer.manager.ConnectionProfileManager;
import org.jboss.tools.teiid.reddeer.perspective.DatabaseDevelopmentPerspective;
import org.jboss.tools.teiid.reddeer.perspective.TeiidPerspective;
import org.jboss.tools.teiid.reddeer.view.GuidesView;
import org.jboss.tools.teiid.reddeer.view.ModelExplorer;
import org.jboss.tools.teiid.reddeer.view.SQLResult;
import org.jboss.tools.teiid.reddeer.view.TeiidInstanceView;
import org.jboss.tools.teiid.reddeer.wizard.CreateVDB;
import org.jboss.tools.teiid.reddeer.wizard.ImportProjectWizard;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test server management use cases start with -Pprofiles - this will load and
 * install all required servers
 * 
 * @author lfabriko 
 */
@RunWith(RedDeerSuite.class)
@OpenPerspective(TeiidPerspective.class)
public class ServerManagementTest extends SWTBotTestCase {

	public static String[] properties = { "dv6.properties", "as5.properties" , "as7.properties"};

	public static String[] serverNames = { "EAP-6.1", "AS-5.1" , "AS-7.1"};

	private static final String PROJECT_NAME = "ServerMgmtTest";
	private static final String MODEL_NAME = "partssupModel1.xmi";
	private static final String HSQLDB_PROFILE = "HSQLDB Profile";
	private static final String VDB = "vdb";
	private static int n = -1;
	private static final String[] pathToVDB_EAP6 = {
			"EAP-6.1  [Started, Synchronized]", "Teiid Instance Configuration",
			"mm://localhost:9999  [default]", "VDBs" };
	private static final String[] pathToVDB_AS5 = {
			"AS-5.1  [Started, Synchronized]", "Teiid Instance Configuration",
			"mms://localhost:31443  [default]", "VDBs" };
	private static final String[] pathToVDB_AS7 = {
		"AS-7.1  [Started, Synchronized]", "Teiid Instance Configuration",
		"mm://localhost:9999  [default]", "VDBs" };
	private static TeiidBot teiidBot = new TeiidBot();

	private static String SERVER_NOT_CONNECTED = "Server is not connected";
	private static String TEIID_CONNECTION_FAILURE = "Teiid Connection Failure";
	private static String TEST_SQL1 = "select * from \"partssupModel1\".\"PARTS\"";
	private static String EAP6_URL = "mm://localhost:9999::admin (EAP-6.1)";
	private static String AS5_URL = "mms://localhost:31443::admin (AS-5.1)";
	private static String AS7_URL = "mm://localhost:9999::admin (AS-7.1)";
	
	@BeforeClass
	public static void createModelProject() {
		/*
		 * if (System.getProperty("swtbot.PLAYBACK_DELAY") == null) {
		 * SWTBotPreferences.PLAYBACK_DELAY = 1000; } else {
		 * SWTBotPreferences.PLAYBACK_DELAY = new Integer(
		 * System.getProperty("swtbot.PLAYBACK_DELAY"));//
		 * -Dswtbot.PLAYBACK_DELAY }
		 */

		// create HSQL profile
		try {
			//teiidBot.createHsqlProfile("resources/db/ds1.properties", HSQLDB_PROFILE, true, true);
			new ConnectionProfileManager().createCPWithDriverDefinition(HSQLDB_PROFILE, "resources/db/ds1.properties");
		} catch (Exception ex) {
			// redo manually
		}
		try {
			new ImportProjectWizard("resources/projects/ServerMgmtTest.zip")
					.execute(); // incorrect connection profile
			// set connection profile
			new ModelExplorer().changeConnectionProfile(HSQLDB_PROFILE,
					PROJECT_NAME, MODEL_NAME);
		} catch (Exception ex) {
			// redo manually
		}

	}

	/**
	 * No server defined
	 */
	@Test
	public void test01() {
		try {
			// SWTBotPreferences.PLAYBACK_DELAY = 1000;
			n++;
			// preview data - fails on "no teiid instance"
			assertFalse(canPreviewData("Confirm Enable Preview", "PARTS"));

			// create VDB - pass
			assertTrue(canCreateVDB(VDB + n, MODEL_NAME));

			// deploy VDB - fail
			// TODO (Error log shows new line)
			// assertFalse(canDeployVDB(SERVER_NOT_CONNECTED, VDB + n));

			// execute VDB - fail
			// TODO (NPE)
			// assertFalse(canExecuteVDB(
		} catch (Exception ex) {
			// redo manually
		}
	}

	/**
	 * Servers defined, not started
	 */
	@Test
	public void test02() {
		// SWTBotPreferences.PLAYBACK_DELAY = 2000;
		try {
			n++;
			for (int i = 0; i < properties.length; i++) {
				// TODO: Don't use properties anymore!
				// TeiidSuite.addServerWithProperties(properties[i]);// define
																	// AS-5,
																	// EAP-6.1
			}
		} catch (Exception ex) {
			// redo manually
		}
		// SWTBotPreferences.PLAYBACK_DELAY = 1000;
		System.out.println("---Servers added---");
		// bot.sleep(10000);

		try {
			// preview data - fail
			assertFalse(canPreviewData(TEIID_CONNECTION_FAILURE, "PARTS"));

			// create VDB - pass
			assertTrue(canCreateVDB(VDB + n, MODEL_NAME));

			// deploy VDB - fail
			assertFalse(canDeployVDB(SERVER_NOT_CONNECTED, VDB + n));

			// execute VDB - fail
			assertFalse(canExecuteVDB(SERVER_NOT_CONNECTED, VDB + n, TEST_SQL1));
		} catch (Exception ex) {
			// redo manually
		}
	}

	/**
	 * Servers both defined and started
	 */
	
	/**
	 * DV 6
	 */
	@Test
	public void test03() {
		// SWTBotPreferences.PLAYBACK_DELAY = 1000;

		n++;
		// start server EAP-6.1
		TeiidInstanceView teiidInstanceView = new TeiidInstanceView(true);
		try {
			teiidInstanceView.startServer(serverNames[0]);

			// specify the default teiid instance
			teiidInstanceView.setDefaultTeiidInstance(EAP6_URL);

			assertTrue(canPreviewData(null, "PARTS"));

			// switch back to Teiid Designer Perspective
			TeiidPerspective.getInstance();

			// create VDB - pass
			assertTrue(canCreateVDB(VDB + n, MODEL_NAME));

			// deploy VDB - pass
			assertTrue(canDeployVDB(null, VDB + n,
					createPathToVDB(VDB + n, pathToVDB_EAP6)));

			// execute VDB - pass
			assertTrue(canExecuteVDB(null, VDB + n, TEST_SQL1));

			// switch back to teiid designer perspective
			TeiidPerspective.getInstance();

			// stop server EAP-6.1
			teiidInstanceView.stopServer(serverNames[0]);

			new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		} catch (Exception ex) {
			// redo manually
		}
	}
	
	/**
	 * AS 5
	 */
	@Test
	public void test05(){
		TeiidInstanceView teiidInstanceView = new TeiidInstanceView(true);
		// server AS-5
		try {
			teiidInstanceView.setDefaultTeiidInstance(AS5_URL);
			n++;
			// start server AS-5
			teiidInstanceView.startServer(serverNames[1]);// AS5 server must
															// have in
															// profile/lib the
															// hsqldb.jar driver

			new WaitUntil(new ServerHasState(serverNames[1]), TimePeriod.LONG);

			assertTrue(canPreviewData(null, "PARTS"));

			// switch back to Teiid Designer Perspective
			TeiidPerspective.getInstance();

			// create VDB - pass
			assertTrue(canCreateVDB(VDB + n, MODEL_NAME));

			// deploy VDB - pass
			assertTrue(canDeployVDB(null, VDB + n,
					createPathToVDB(VDB + n, pathToVDB_AS5)));

			// execute vdb - pass
			assertTrue(canExecuteVDB(null, VDB + n, TEST_SQL1));

			// switch back to teiid designer perspective
			TeiidPerspective.getInstance();

			// stop server
			teiidInstanceView.stopServer(serverNames[1]);
			new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		} catch (Exception ex) {
			// redo manually
		}
	}
	
	/**
	 * AS 7
	 */
	@Test
	public void test06(){
		TeiidInstanceView teiidInstanceView = new TeiidInstanceView(true);
		// server AS-7
		try {
			teiidInstanceView.setDefaultTeiidInstance(AS7_URL);
			n++;
			// start server AS-7
			teiidInstanceView.startServer(serverNames[2]);

			new WaitUntil(new ServerHasState(serverNames[2]), TimePeriod.LONG);

			assertTrue(canPreviewData(null, "PARTS"));

			// switch back to Teiid Designer Perspective
			TeiidPerspective.getInstance();

			// create VDB - pass
			assertTrue(canCreateVDB(VDB + n, MODEL_NAME));

			// deploy VDB - pass
			assertTrue(canDeployVDB(null, VDB + n,
					createPathToVDB(VDB + n, pathToVDB_AS7)));

			// execute vdb - pass
			assertTrue(canExecuteVDB(null, VDB + n, TEST_SQL1));

			// switch back to teiid designer perspective
			TeiidPerspective.getInstance();

			// stop server
			teiidInstanceView.stopServer(serverNames[2]);
			new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		} catch (Exception ex) {
			// redo manually
		}

	}

	private boolean canPreviewData(String message, String tableName) {
		if (message != null) {
			new GuidesView().chooseAction("Model JDBC Source", "Preview Data");
			new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
			assertEquals(bot.activeShell().getText(), message);
			bot.activeShell().close();
			return false;
		} else {
			//new GuidesView().previewData(true, PROJECT_NAME, MODEL_NAME,tableName);
			new GuidesView().previewData(PROJECT_NAME, MODEL_NAME,tableName);
			SQLResult result = DatabaseDevelopmentPerspective.getInstance()
					.getSqlResultsView().getByOperation(TEST_SQL1);// "select * from \""+MODEL_NAME.substring(0,MODEL_NAME.indexOf("."))+"\".\""+tableName+"\""
			assertEquals(SQLResult.STATUS_SUCCEEDED, result.getStatus());
			return true;
		}
	}

	private boolean canCreateVDB(String vdb, String model) {
		CreateVDB createVDB = new CreateVDB();
		createVDB.setFolder(PROJECT_NAME);
		createVDB.setName(vdb);
		createVDB.execute(true);

		VDBEditor editor = VDBEditor.getInstance(vdb + ".vdb");
		editor.show();
		editor.addModel(PROJECT_NAME, model);
		editor.save();

		// check if VDB is in project
		return new PackageExplorer().getProject(PROJECT_NAME).containsItem(
				vdb + ".vdb");
	}

	private boolean canDeployVDB(String message, String vdb,
			String... pathToVDB) {
		VDB vdbItem = new ModelExplorer().getModelProject(PROJECT_NAME).getVDB(
				vdb + ".vdb");
		vdbItem.deployVDB();
		if (message != null) {
			assertEquals(bot.activeShell().getText(), message);
			bot.activeShell().close();
			return false;
		} else {

			// check if servers view contains deployed vdb
			return new TeiidInstanceView(true).containsVDB(true, pathToVDB);
		}
	}

	private boolean canExecuteVDB(String message, String vdbName, String sql) {
		VDB vdb = new ModelExplorer().getModelProject(PROJECT_NAME).getVDB(
				vdbName + ".vdb");

		if (message != null) {
			new GuidesView().chooseAction("Model JDBC Source", "Execute VDB");// just
																				// select
																				// action
																				// to
																				// execute
																				// VDB
																				// and
																				// confirm
																				// fail
																				// message
			assertEquals(bot.activeShell().getText(), message);
			bot.activeShell().close();
			return false;
		}
		vdb.executeVDB(true);

		SQLScrapbookEditor editor = new SQLScrapbookEditor("SQL Scrapbook0");
		editor.show();

		// TESTSQL_1
		editor.setText(sql);
		editor.executeAll();

		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);

		SQLResult result = DatabaseDevelopmentPerspective.getInstance()
				.getSqlResultsView().getByOperation(sql);
		assertEquals(SQLResult.STATUS_SUCCEEDED, result.getStatus());

		editor.close();

		return true;
	}

	private String[] createPathToVDB(String vdb, String... path) {
		String[] array = new String[path.length + 1];
		System.arraycopy(path, 0, array, 0, array.length - 1);
		array[array.length - 1] = vdb;
		return array;
	}

}
