package nl.andrewl.liber_actionis.lua;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class oslib extends TwoArgFunction {
	public oslib() {}

	@Override
	public LuaValue call(LuaValue modName, LuaValue env) {
		LuaValue lib = tableOf();
		lib.set("sleep", new sleep());
		env.set("os", lib);
		env.get("package").get("loaded").set("os", lib);
		return lib;
	}

	static class sleep extends OneArgFunction {
		public LuaValue call(LuaValue ms) {
			double seconds = ms.checkdouble();
			long millis = (long) (seconds * 1000.0);
			if (millis > 0) {
				try {
					Thread.sleep(millis);
				} catch (InterruptedException e) {
					throw new LuaError(e);
				}
			}
			return LuaValue.NIL;
		}
	}
}
