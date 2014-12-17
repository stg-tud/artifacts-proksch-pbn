package cc.recommenders.evaluation.optimization;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import cc.recommenders.evaluation.optimization.raster.RasterSuite;

@Ignore
@RunWith(Suite.class)
@SuiteClasses({ VectorTest.class, BoundsMatcherTest.class, RasterSuite.class })
public class OptimizationSuite {
}
