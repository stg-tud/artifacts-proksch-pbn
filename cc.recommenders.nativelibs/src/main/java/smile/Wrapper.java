package smile;

import cc.recommenders.nativelibs.NativeLibLoader;

public abstract class Wrapper {
	public Wrapper() {
		ptrNative = createNative(null);
	}

	public Wrapper(Object param) {
		ptrNative = createNative(param);
	}

	protected void finalize() {
		deleteNative(ptrNative);
	}

	protected abstract long createNative(Object param);

	protected abstract void deleteNative(long nativePtr);

	private static native void nativeStaticInit();

	protected long ptrNative = 0;

	static {
		// was: System.loadLibrary("jsmile");
		new NativeLibLoader().loadLibrary("jsmile");

		nativeStaticInit();
	}
}
