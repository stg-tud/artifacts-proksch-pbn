package cc.recommenders.evaluation.optimization;

import static cc.recommenders.evaluation.evaluators.Validations.nFoldCrossValidation;
import static cc.recommenders.mining.calls.MiningOptions.newMiningOptions;

import java.util.List;

import cc.recommenders.evaluation.evaluators.F1Evaluator;
import cc.recommenders.evaluation.evaluators.SizeCostAndF1Evaluator;
import cc.recommenders.evaluation.evaluators.SizeEvaluator;
import cc.recommenders.io.Logger;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.mining.calls.MiningOptions;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.mining.calls.pbn.PBNMiner;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.inject.Inject;

public class MeanCalculator implements ScoreCalculator {

	private static final int NUM_FOLDS = 3;
	private static final MiningOptions MINING_OPTIONS_FOR_SMALL_MODEL = newMiningOptions("CANOPY[2.00; 1.00]+W[0.00; 0.00; 0.00; 0.00]+L0");
	private static final MiningOptions MINING_OPTIONS_FOR_BIG_MODEL = newMiningOptions("CANOPY[2.00; 1.00]+W[1.00; 1.00; 1.00; 1.00]+L0");

	private final PBNMiner ouMiner;
	private final F1Evaluator f1Evaluator;
	private SizeCostAndF1Evaluator mixedEvaluator;
	private MiningOptions miningOptions;
	private QueryOptions queryOptionsSingleton;
	private List<Usage> usages;
	private SizeEvaluator sizeAveragor;

	@Inject
	public MeanCalculator(PBNMiner ouMiner, F1Evaluator evaluator, SizeEvaluator sizeAveragor,
			MiningOptions miningOptionsSingleton, QueryOptions queryOptionsSingleton) {
		this.ouMiner = ouMiner;
		this.f1Evaluator = evaluator;
		this.sizeAveragor = sizeAveragor;
		this.miningOptions = miningOptionsSingleton;
		this.queryOptionsSingleton = queryOptionsSingleton;
	}

	@Override
	public double eval(EvaluationOptions opts, List<Usage> usages) {
		this.usages = usages;
		this.miningOptions.setFrom(opts.miningOptions);
		this.queryOptionsSingleton.setFrom(opts.queryOptions);
		reinitEvaluator();

		nFoldCrossValidation(NUM_FOLDS, ouMiner, mixedEvaluator, usages);

		double score = mixedEvaluator.getResults();
		Logger.log("... calculated %s --> %f\n", opts, score);
		return score;
	}

	private void reinitEvaluator() {
		// TODO bäh
		if (mixedEvaluator == null) {
			int minSize = determineModelSize(MINING_OPTIONS_FOR_SMALL_MODEL);
			int maxSize = determineModelSize(MINING_OPTIONS_FOR_BIG_MODEL);
			Logger.log("Determined model sizes: min. %d, max. %d\n", minSize, maxSize);
			mixedEvaluator = new SizeCostAndF1Evaluator(f1Evaluator, sizeAveragor, miningOptions, minSize, maxSize);
		} else {
			mixedEvaluator.reinit();
		}
	}

	private int determineModelSize(MiningOptions newOpts) {
		MiningOptions old = setMiningOptions(newOpts);
		ICallsRecommender<Query> recommender = ouMiner.createRecommender(usages);
		setMiningOptions(old);
		return recommender.getSize();
	}

	private MiningOptions setMiningOptions(MiningOptions newo) {
		MiningOptions old = new MiningOptions();
		old.setFrom(miningOptions);

		miningOptions.setWeightClassContext(newo.getWeightClassContext());
		miningOptions.setWeightDefinition(newo.getWeightDefinition());
		miningOptions.setWeightMethodContext(newo.getWeightMethodContext());
		miningOptions.setWeightParameterSites(newo.getWeightParameterSites());
		// TODO add missing options

		return old;
	}
}
