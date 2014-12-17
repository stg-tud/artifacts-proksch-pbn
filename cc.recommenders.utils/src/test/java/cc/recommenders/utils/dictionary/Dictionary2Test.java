package cc.recommenders.utils.dictionary;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.utils.dictionary.Dictionary;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Dictionary2Test {

	private Dictionary<String> sut;

	@Before
	public void setup() {
		sut = new Dictionary<String>();
	}

	@Test
	public void ensureStableOrder() {
		Set<String> expected = Sets.newLinkedHashSet();
		for (int i = 0; i < 1000; i++) {
			sut.add("num" + i);
			expected.add("num" + i);
		}

		Set<String> actual = sut.getAllEntries();
		assertSetEquals(expected, actual);
	}

	@Test
	public void ensureStableOrderWhenSerialized() {
		Set<String> expected = Sets.newLinkedHashSet();
		for (int i = 0; i < 1000; i++) {
			sut.add("num" + i);
			expected.add("num" + i);
		}

		String json = new Gson().toJson(sut);
		Type fooType = new TypeToken<Dictionary<String>>() {
		}.getType();

		Dictionary<String> deserializedSut = new Gson().fromJson(json, fooType);

		Set<String> actual = deserializedSut.getAllEntries();

		assertSetEquals(expected, actual);
	}

	private void assertSetEquals(Set<String> expected, Set<String> actual) {
		assertEquals(expected.size(), actual.size());
		Iterator<String> ait = actual.iterator();
		Iterator<String> eit = expected.iterator();
		while (ait.hasNext()) {
			String a = ait.next();
			String e = eit.next();
			assertEquals(e, a);
		}
	}
}