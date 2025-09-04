package testRunner;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features="src/test/java/features/",
				  glue={"stepDefinitions","hooks"},
				  tags="@profileUpdate or @Login or @home"
				  )
public class TestRunner {

}
