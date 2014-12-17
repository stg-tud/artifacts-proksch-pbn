/**
 * Copyright (c) 2011-2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 */
package integration;

import static cc.recommenders.evaluation.evaluators.Validations.testValidation;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.recommenders.commons.bayesnet.BayesianNetwork;
import org.junit.Before;
import org.junit.Test;

import cc.recommenders.evaluation.data.Boxplot;
import cc.recommenders.evaluation.evaluators.F1Evaluator;
import cc.recommenders.evaluation.queries.PartialUsageQueryBuilder;
import cc.recommenders.evaluation.queries.QueryBuilderFactory;
import cc.recommenders.mining.calls.DictionaryBuilder;
import cc.recommenders.mining.calls.DistanceMeasureFactory;
import cc.recommenders.mining.calls.MiningOptions;
import cc.recommenders.mining.calls.ModelBuilder;
import cc.recommenders.mining.calls.PatternFinderFactory;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.mining.calls.pbn.PBNMiner;
import cc.recommenders.mining.calls.pbn.PBNModelBuilder;
import cc.recommenders.mining.features.FeatureExtractor;
import cc.recommenders.mining.features.OptionAwareFeaturePredicate;
import cc.recommenders.mining.features.RareFeatureDropper;
import cc.recommenders.mining.features.UsageFeatureExtractor;
import cc.recommenders.mining.features.UsageFeatureWeighter;
import cc.recommenders.names.IMethodName;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.CallSite;
import cc.recommenders.usages.CallSites;
import cc.recommenders.usages.Usage;
import cc.recommenders.usages.features.UsageFeature;

public abstract class AbstractIntegrationTest {

	private QueryOptions queryOptions;
	private MiningOptions miningOptions;

	private PartialUsageQueryBuilder queryBuilder;
	private F1Evaluator evaluator;

	private FeatureExtractor<Usage, UsageFeature> featureExtractor;
	private DictionaryBuilder<Usage, UsageFeature> dictionaryBuilder;
	private UsageFeatureWeighter objectUsageWeighter;
	private PatternFinderFactory<UsageFeature> patternFinderFactory;
	private ModelBuilder<UsageFeature, BayesianNetwork> modelBuilder;
	private PBNMiner ouMiner;
	private DistanceMeasureFactory distanceMeasureFactory;

	@Before
	public void setup() {
		queryOptions = QueryOptions.newQueryOptions("");
		miningOptions = new MiningOptions();

		queryBuilder = new PartialUsageQueryBuilder();
		QueryBuilderFactory queryBuilderFactory = new QueryBuilderFactory(queryOptions, null, queryBuilder);
		evaluator = new F1Evaluator(queryBuilderFactory, queryOptions);

		featureExtractor = new UsageFeatureExtractor(miningOptions);
		dictionaryBuilder = new DictionaryBuilder<Usage, UsageFeature>(featureExtractor);
		objectUsageWeighter = new UsageFeatureWeighter(miningOptions);
		distanceMeasureFactory = new DistanceMeasureFactory(miningOptions);
		patternFinderFactory = new PatternFinderFactory<UsageFeature>(objectUsageWeighter, miningOptions,
				distanceMeasureFactory);
		modelBuilder = new PBNModelBuilder();
		RareFeatureDropper<UsageFeature> rareFeatureDropper = new RareFeatureDropper<UsageFeature>();
		OptionAwareFeaturePredicate featurePred = new OptionAwareFeaturePredicate(queryOptions);
		ouMiner = new PBNMiner(featureExtractor, dictionaryBuilder, patternFinderFactory, modelBuilder,
				queryOptions, miningOptions, rareFeatureDropper, featurePred);

	}

	public MiningOptions getMiningOptions() {
		return miningOptions;
	}

	public QueryOptions getQueryOptions() {
		return queryOptions;
	}

	@Test
	public void runTest() {

		init();
		testValidation(ouMiner, evaluator, getTrainingData(), getValidationData());

		Boxplot actual = evaluator.getResults();
		Boxplot expected = getExpectation();
		assertEquals(expected, actual);
	}

	public void init() {
		// can be overridden to setup specific data
	}

	public abstract List<Usage> getTrainingData();

	public abstract List<Usage> getValidationData();

	public abstract Boxplot getExpectation();

	public static ITypeName newType(String typeName) {
		return VmTypeName.get(typeName);
	}

	public static IMethodName newMethod(String methodName) {
		return VmMethodName.get(methodName);
	}

	public static CallSite newReceiverCallSite(String methodName) {
		CallSite cs = CallSites.createReceiverCallSite(methodName);
		return cs;
	}
}