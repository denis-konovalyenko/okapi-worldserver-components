package com.spartansoftwareinc.ws.okapi.filters.yaml;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.idiominc.wssdk.component.filter.WSFilter;
import com.spartansoftwareinc.ws.okapi.filters.FilterTestHarness;
import com.spartansoftwareinc.ws.okapi.filters.model.SegmentInfoHolder;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class YAMLFilterTest {

    @DataProvider
    public static Object[][] testParsingSegmentsResults() {
        return new Object[][] {{
            new SegmentInfoHolder[] {
                new SegmentInfoHolder("Failed to execute " + WSFilter.PLACEHOLDER + ": an error occurred", "{{model}}"),
                new SegmentInfoHolder("Failed to execute " + WSFilter.PLACEHOLDER + ": " + WSFilter.PLACEHOLDER +
                                      " errors occurred.", "{{model}}", "{{count}}"),
                new SegmentInfoHolder("Please verify the following:"),
                new SegmentInfoHolder("This item is included"),
                new SegmentInfoHolder("This item is excluded"),
                new SegmentInfoHolder("This item is invalid"),
                new SegmentInfoHolder("This item requires confirmation")
            }
        }};
    }

    @Test
    @UseDataProvider("testParsingSegmentsResults")
    public void testParsingSegments(SegmentInfoHolder[] expected) throws Exception {
        YAMLWSOkapiFilter filter = new TestYamlWSOkapiFilter();
        FilterTestHarness harness = new FilterTestHarness(filter);
        harness.extractAndExpectSegments("/TestFile.yml", filter.getOkapiFilterConfiguration(),
                StandardCharsets.UTF_8, expected);
    }

    @Test
    @UseDataProvider("testParsingSegmentsResults")
    public void testParsingExcludeEmptyString(SegmentInfoHolder[] expected) throws Exception {
        YAMLWSOkapiFilter filter = new TestYamlWSOkapiFilter("");
        FilterTestHarness harness = new FilterTestHarness(filter);
        harness.extractAndExpectSegments("/TestFile.yml", filter.getOkapiFilterConfiguration(),
                StandardCharsets.UTF_8, expected);
    }

    @DataProvider
    public static Object[][] testParsingExcludeKeysResults() {
        return new Object[][] {{
            new SegmentInfoHolder[] {
                new SegmentInfoHolder("Failed to execute " + WSFilter.PLACEHOLDER + ": an error occurred", "{{model}}"),
                new SegmentInfoHolder("This item is included"),
                new SegmentInfoHolder("This item is excluded"),
                new SegmentInfoHolder("This item is invalid"),
                new SegmentInfoHolder("This item requires confirmation")
            }
        }};
    }

    @Test
    @UseDataProvider("testParsingExcludeKeysResults")
    public  void testParsingExcludeKeys(SegmentInfoHolder[] expected) throws Exception {
        YAMLWSOkapiFilter filter = new TestYamlWSOkapiFilter("other", "body");
        FilterTestHarness harness = new FilterTestHarness(filter);
        harness.extractAndExpectSegments("/TestFile.yml", filter.getOkapiFilterConfiguration(),
                StandardCharsets.UTF_8, expected);
    }

    class TestYamlWSOkapiFilter extends YAMLWSOkapiFilter {
        private final YAMLFilterConfigurationData testData;

        public TestYamlWSOkapiFilter(String... excludes) {
            this.testData = new YAMLFilterConfigurationData();
            testData.setExcludedKeys(new HashSet<String>(Arrays.asList(excludes)));
        }

        @Override
        protected YAMLFilterConfigurationData getOkapiFilterConfiguration() {
            return this.testData;
        }
    }
}

