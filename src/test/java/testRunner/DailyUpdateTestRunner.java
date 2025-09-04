package testRunner;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features="src/test/java/features/dailyProfileUpdate.feature",
				  glue={"stepDefinitions","hooks"},
				  tags="@profileUpdate",
				  plugin = {
					  "pretty",
					  "html:target/cucumber-reports/html",
					  "json:target/cucumber-reports/report.json",
					  "junit:target/cucumber-reports/report.xml"
				  }
				  )
public class DailyUpdateTestRunner {

}
