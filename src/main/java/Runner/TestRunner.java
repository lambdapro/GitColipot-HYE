package Runner;

import java.net.URL;
import java.util.HashMap;

import com.lambdatest.tunnel.Tunnel;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import cucumber.api.CucumberOptions;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.TestNGCucumberRunner;

@CucumberOptions(
        features = "src/main/java/Features",
        glue = {"Steps"},
        tags = {"~@Ignore"},
        format = {
                "pretty",
                "html:target/cucumber-reports/cucumber-pretty",
                "json:target/cucumber-reports/CucumberTestReport.json",
                "rerun:target/cucumber-reports/rerun.txt"
        },plugin = "json:target/cucumber-reports/CucumberTestReport.json")

public class TestRunner
{
    private TestNGCucumberRunner testNGCucumberRunner;

    public static RemoteWebDriver connection;
    Tunnel t;

    @BeforeClass(alwaysRun = true)
    public void setUpCucumber()
    {
    	 testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters({ "browser", "version", "platform" })
    public void setUpClass(String browser, String version, String platform) throws Exception
    {
        String username = System.getenv("LT_USERNAME") == null ? "LT_USERNAME" : System.getenv("LT_USERNAME");
        String accesskey = System.getenv("LT_ACCESS_KEY") == null ? "LT_ACCESS_KEY" : System.getenv("LT_ACCESS_KEY");
        t = new Tunnel();
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("user", username);
        options.put("key", accesskey);
        options.put("dir", "app");
//        t.start(options);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", browser);
        capabilities.setCapability("version",version);
        capabilities.setCapability("platform", platform);

        capabilities.setCapability("build", "Demo: GitCopilot testing");

        capabilities.setCapability("tunnel", true);
        capabilities.setCapability("video", true);
        capabilities.setCapability("console", true);
        capabilities.setCapability("visual", true);

        String gridURL = "https://" + username + ":" + accesskey + "@hub.lambdatest.com/wd/hub";
        connection = new RemoteWebDriver(new URL(gridURL), capabilities);
        System.out.println(connection.getSessionId());
}

    @Test(groups = "cucumber", description = "Runs Cucumber Feature", dataProvider = "features")
    public void feature(CucumberFeatureWrapper cucumberFeature)
    {
        testNGCucumberRunner.runCucumber(cucumberFeature.getCucumberFeature());
    }

    @DataProvider
    public Object[][] features()
    {
        return testNGCucumberRunner.provideFeatures();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws Exception
    {
        testNGCucumberRunner.finish();
//        t.stop();
    }
}
