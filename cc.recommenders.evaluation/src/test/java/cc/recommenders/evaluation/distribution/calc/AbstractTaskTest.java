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
package cc.recommenders.evaluation.distribution.calc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class AbstractTaskTest {

	private static final int CUR_FOLD = 5;
	private static final int NUM_FOLDS = 13;
	private static final String APP = "APP";
	private static final String OPTIONS = "OPT";
	private static final String TYPE = "TYPE";
	private TestTask sut;

	@Before
	public void setup() {
		sut = new TestTask();
		sut.app = APP;
		sut.currentFold = CUR_FOLD;
		sut.numFolds = NUM_FOLDS;
		sut.options = OPTIONS;
		sut.processingTimeInS = 0.234;
		sut.typeName = TYPE;
	}

	@Test
	public void defaultToString() {
		String actual = sut.toString();
		String expected = "TestTask: APP - TYPE (fold 6/13) - DETAILS";
		assertEquals(expected, actual);
	}

	@Test
	public void resultToString() {
		sut.out = "RES";
		String actual = sut.toString();
		String expected = "TestTask: APP - TYPE (fold 6/13) - DETAILS - :: RES :: (took 0.2s)";
		assertEquals(expected, actual);
	}

	@Test
	public void defaultValuesForHelperMethods() {
		AbstractTask sut = new AbstractTask() {
			private static final long serialVersionUID = -6744060519465364967L;
		};
		assertFalse(sut.hasResult());
		assertEquals("", sut.resultToString());
		assertEquals("", sut.detailsToString());
	}

	public class TestTask extends AbstractTask {

		private static final long serialVersionUID = 8167957810576154074L;
		public String out;

		@Override
		protected String detailsToString() {
			return "DETAILS";
		}

		@Override
		protected boolean hasResult() {
			return out != null;
		}

		@Override
		protected String resultToString() {
			return String.format(":: %s ::", out);
		}
	}
}