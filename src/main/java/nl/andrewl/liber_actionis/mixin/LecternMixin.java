package nl.andrewl.liber_actionis.mixin;

import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static nl.andrewl.liber_actionis.LiberActionis.runScript;

@Mixin(LecternBlockEntity.class)
public class LecternMixin {
	@Inject(at = @At("HEAD"), method = "setBook(Lnet/minecraft/item/ItemStack;)V")
	private void onBookSet(ItemStack book, CallbackInfo ci) {
		System.out.println("Book added to lectern.");
		LecternBlockEntity lectern = (LecternBlockEntity)(Object)this;
		runScript(book, lectern, lectern.getWorld());
	}

	@Inject(at = @At("TAIL"), method = "onBookRemoved")
	private void onBookRemoved(CallbackInfo ci) {
		System.out.println("Book removed from lectern.");
	}
}
