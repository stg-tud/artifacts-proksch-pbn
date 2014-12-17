/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 */
package cc.recommenders.mining.calls.pbn;

import java.util.List;
import java.util.Set;

import org.eclipse.recommenders.commons.bayesnet.BayesianNetwork;

import cc.recommenders.io.Logger;
import cc.recommenders.mining.calls.DictionaryBuilder;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.mining.calls.Miner;
import cc.recommenders.mining.calls.MiningOptions;
import cc.recommenders.mining.calls.ModelBuilder;
import cc.recommenders.mining.calls.Pattern;
import cc.recommenders.mining.calls.PatternFinderFactory;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.mining.features.FeatureExtractor;
import cc.recommenders.mining.features.OptionAwareFeaturePredicate;
import cc.recommenders.mining.features.RareFeatureDropper;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;
import cc.recommenders.usages.features.UsageFeature;
import cc.recommenders.utils.dictionary.Dictionary;

import com.google.inject.Inject;

public class PBNMiner implements Miner<Usage, Query> {

	private final FeatureExtractor<Usage, UsageFeature> featureExtractor;
	private final DictionaryBuilder<Usage, UsageFeature> dictionaryBuilder;
	private final PatternFinderFactory<UsageFeature> patternFinderFactory;
	private final ModelBuilder<UsageFeature, BayesianNetwork> modelBuilder;
	private final RareFeatureDropper<UsageFeature> dropper;
	private final OptionAwareFeaturePredicate featurePred;
	private final QueryOptions qOpts;
	private final MiningOptions mOpts;

	private int lastNumberOfFeatures = 0;
	private int lastNumberOfPatterns = 0;

	@Inject
	public PBNMiner(FeatureExtractor<Usage, UsageFeature> featureExtractor,
			DictionaryBuilder<Usage, UsageFeature> dictionaryBuilder,
			PatternFinderFactory<UsageFeature> patternFinderFactory,
			ModelBuilder<UsageFeature, BayesianNetwork> modelBuilder, QueryOptions qOpts, MiningOptions mOpts,
			RareFeatureDropper<UsageFeature> dropper, OptionAwareFeaturePredicate featurePred) {
		this.featureExtractor = featureExtractor;
		this.dictionaryBuilder = dictionaryBuilder;
		this.patternFinderFactory = patternFinderFactory;
		this.modelBuilder = modelBuilder;
		this.qOpts = qOpts;
		this.mOpts = mOpts;
		this.dropper = dropper;
		this.featurePred = featurePred;
	}

	@Override
	public BayesianNetwork learnModel(List<Usage> usages) {
		Logger.debug("extracting features");
		List<List<UsageFeature>> features = extractFeatures(usages);
		Logger.debug("creating dictionary");
		Dictionary<UsageFeature> dictionary = createDictionary(usages, features);

		lastNumberOfFeatures = dictionary.size();

		Logger.debug("mining");
		List<Pattern<UsageFeature>> patterns = patternFinderFactory.createPatternFinder().find(features, dictionary);

		lastNumberOfPatterns = patterns.size();

		Logger.debug("building network");
		BayesianNetwork network = modelBuilder.build(patterns, dictionary);
		return network;
	}

	protected List<List<UsageFeature>> extractFeatures(List<Usage> usages) {
		return featureExtractor.extract(usages);
	}

	protected Dictionary<UsageFeature> createDictionary(List<Usage> usages, List<List<UsageFeature>> features) {
		Dictionary<UsageFeature> rawDictionary = dictionaryBuilder.newDictionary(usages, featurePred);
		if (mOpts.isFeatureDropping()) {
			Dictionary<UsageFeature> dictionary = dropper.dropRare(rawDictionary, features);
			Set<String> diff = DictionaryHelper.diff(rawDictionary, dictionary);

			Set<UsageFeature> rawClassContexts = new DictionaryHelper(rawDictionary).getClassContexts();
			Set<UsageFeature> classContexts = new DictionaryHelper(dictionary).getClassContexts();
			return dictionary;
		} else {
			return rawDictionary;
		}
	}

	@Override
	public ICallsRecommender<Query> createRecommender(List<Usage> in) {
		BayesianNetwork network = learnModel(in);
		return new PBNRecommender(network, qOpts);
	}

	public int getLastNumberOfFeatures() {
		return lastNumberOfFeatures;
	}

	public int getLastNumberOfPatterns() {
		return lastNumberOfPatterns;
	}
}