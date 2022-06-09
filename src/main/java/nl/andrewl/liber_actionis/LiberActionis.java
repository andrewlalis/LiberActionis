package nl.andrewl.liber_actionis;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;
import nl.andrewl.liber_actionis.lua.oslib;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiberActionis implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("liber-actionis");

	@Override
	public void onInitialize() {
		LOGGER.info("Liber Actionis initialized.");
		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) -> {
			if (blockEntity instanceof LecternBlockEntity lectern) {
				runScript(lectern.getBook(), lectern, world);
			}
		});
		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
			if (blockEntity instanceof LecternBlockEntity lectern) {
				System.out.println("Lectern unloaded.");
			}
		});
	}

	public static void runScript(ItemStack book, LecternBlockEntity lectern, World world) {
		if (isBookExecutable(book)) {
			System.out.println("Executing book...");
			String script = getScript(book);
			if (script != null) {
				BlockState state = world.getBlockState(lectern.getPos());
				LecternBlock block = (LecternBlock) state.getBlock();
				System.out.println(state);
				System.out.println(block);

				Globals globals = JsePlatform.standardGlobals();
				globals.load(new oslib());
				LuaValue chunk = globals.load(script);
				new Thread(() -> {
					System.out.println("Executing script:\n" + script + "\n----------------------");
					chunk.invoke();
					System.out.println("Script has completed execution.");
				}).start();
			} else {
				System.out.println("Script is null!");
			}
		}
	}

	public static boolean isBookExecutable(ItemStack book) {
		System.out.println("Checking if book is executable...");
		if (book == null) return false;
		NbtCompound nbt = book.getNbt();
		if (nbt != null && nbt.contains("pages", NbtElement.LIST_TYPE)) {
			NbtList pageList = nbt.getList("pages", NbtElement.STRING_TYPE);
			if (pageList.size() > 0) {
				return pageList.get(0).asString().startsWith("#script\n");
			}
		}
		return false;
	}

	public static String getScript(ItemStack book) {
		NbtCompound nbt = book.getNbt();
		if (nbt != null && nbt.contains("pages", NbtElement.LIST_TYPE)) {
			NbtList pageList = nbt.getList("pages", NbtElement.STRING_TYPE);
			if (pageList.size() > 0) {
				StringBuilder scriptBuilder = new StringBuilder();
				scriptBuilder.append(pageList.get(0).asString().replaceFirst("#script\n", "")).append('\n');
				for (int i = 1; i < pageList.size(); i++) {
					scriptBuilder.append(pageList.get(i)).append('\n');
				}
				return scriptBuilder.toString();
			}
		}
		return null;
	}
}
