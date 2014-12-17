/**
 * Copyright (c) 2011-2014 Darmstadt University of Technology. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Proksch - initial API and implementation
 */
package exec.plm12;

import java.io.File;
import java.util.Map;

import org.eclipse.recommenders.commons.bayesnet.BayesianNetwork;

import cc.recommenders.evaluation.io.DecoratedObjectUsageStore;
import cc.recommenders.evaluation.io.ObjectUsageStore;
import cc.recommenders.evaluation.io.ProjectIndexer;
import cc.recommenders.evaluation.optimization.CandidateSelector;
import cc.recommenders.evaluation.optimization.MeanCalculator;
import cc.recommenders.evaluation.optimization.ScoreCalculator;
import cc.recommenders.evaluation.optimization.raster.RasterAroundCandidateSelector;
import cc.recommenders.evaluation.queries.QueryBuilder;
import cc.recommenders.evaluation.queries.ZeroCallQueryBuilder;
import cc.recommenders.io.Directory;
import cc.recommenders.mining.calls.MiningOptions;
import cc.recommenders.mining.calls.ModelBuilder;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.mining.calls.clustering.FeatureWeighter;
import cc.recommenders.mining.calls.pbn.PBNModelBuilder;
import cc.recommenders.mining.features.FeatureExtractor;
import cc.recommenders.mining.features.UsageFeatureExtractor;
import cc.recommenders.mining.features.UsageFeatureWeighter;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;
import cc.recommenders.usages.UsageFilter;
import cc.recommenders.usages.features.UsageFeature;

import com.codetrails.data.ObjectUsage;
import com.codetrails.data.ObjectUsageFilter;
import com.codetrails.data.ObjectUsageValidator;
import com.codetrails.data.UsageConverter;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public class Module extends AbstractModule {

	private final String rootFolder;
	private final String sizeInMegaBytes;

	public Module(String rootFolder, String sizeInMegaBytes) {
		this.rootFolder = rootFolder;
		this.sizeInMegaBytes = sizeInMegaBytes;
	}

	@Override
	protected void configure() {
		Directory analysisDir = new Directory(rootFolder + "unsorted/" + sizeInMegaBytes + "/");
		Directory projectIndexedDir = new Directory(rootFolder + "projectIndexed/" + sizeInMegaBytes + "/");
		Directory exportDir = new Directory(rootFolder + "export/" + sizeInMegaBytes + "/");
		Directory prepDir = new Directory(rootFolder + "objectusages/" + sizeInMegaBytes + "/");
		File mfFile = new File(rootFolder + "mf-project/" + sizeInMegaBytes + "/");
		Directory mfDir = new Directory(mfFile.getAbsolutePath());

		Map<String, Directory> dirs = Maps.newHashMap();
		dirs.put("export", exportDir);
		dirs.put("MF", mfDir);
		dirs.put("projectIndexed", projectIndexedDir);
		bindInstances(dirs);
		bind(File.class).annotatedWith(Names.named("MF")).toInstance(mfFile);

		// used to sort folderbased into per-type ObjectUsage ZIPs
		Predicate<ObjectUsage> defaultOuFilter = new ObjectUsageFilter();
		bind(ObjectUsageStore.class).toInstance(new ObjectUsageStore(prepDir, defaultOuFilter));
		// used to read per-type ObjectUsage zips to List<Usage>
		bind(DecoratedObjectUsageStore.class).toInstance(new DecoratedObjectUsageStore(prepDir, defaultOuFilter));

		bind(ProjectIndexer.class).toInstance(
				new ProjectIndexer(analysisDir, projectIndexedDir, new UsageConverter(), new ObjectUsageValidator(),
						new UsageFilter()));

		bind(QueryOptions.class).toInstance(new QueryOptions());
		bind(MiningOptions.class).toInstance(new MiningOptions());

		bind(ScoreCalculator.class).to(MeanCalculator.class);
		bind(CandidateSelector.class).to(RasterAroundCandidateSelector.class);
		bind(FeatureExtractor.class).to(UsageFeatureExtractor.class).in(Scopes.SINGLETON);
	}

	private void bindInstances(Map<String, Directory> dirs) {
		for (String name : dirs.keySet()) {
			Directory dir = dirs.get(name);
			bind(Directory.class).annotatedWith(Names.named(name)).toInstance(dir);
		}
	}

	@Provides
	public FeatureWeighter<UsageFeature> provideFeatureWeighter(MiningOptions options) {
		return new UsageFeatureWeighter(options);
	}

	@Provides
	public FeatureExtractor<Usage, UsageFeature> provideFeatureExtractor(MiningOptions options) {
		return new UsageFeatureExtractor(options);
	}

	@Provides
	public ModelBuilder<UsageFeature, BayesianNetwork> provideModelBuilder() {
		return new PBNModelBuilder();
	}

	@Provides
	public QueryBuilder<Usage, Query> provideQueryBuilder() {
		return new ZeroCallQueryBuilder();
		// PartialUsageQueryBuilder qb = new PartialUsageQueryBuilder();
		// qb.setNumOfQueries(3);
		// return qb;
	}
}