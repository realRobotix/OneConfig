/*
 * This file is part of OneConfig.
 * OneConfig - Next Generation Config Library for Minecraft: Java Edition
 * Copyright (C) 2021~2023 Polyfrost.
 *   <https://polyfrost.cc> <https://github.com/Polyfrost/>
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *   OneConfig is licensed under the terms of version 3 of the GNU Lesser
 * General Public License as published by the Free Software Foundation, AND
 * under the Additional Terms Applicable to OneConfig, as published by Polyfrost,
 * either version 1.0 of the Additional Terms, or (at your option) any later
 * version.
 *
 *   This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 * License.  If not, see <https://www.gnu.org/licenses/>. You should
 * have also received a copy of the Additional Terms Applicable
 * to OneConfig, as published by Polyfrost. If not, see
 * <https://polyfrost.cc/legal/oneconfig/additional-terms>
 */

package cc.polyfrost.oneconfig.internal.mixin;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.KeyInputEvent;
import cc.polyfrost.oneconfig.events.event.RawKeyEvent;
import net.minecraft.client.KeyboardListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardListener.class)
public class KeyboardMixin {

    @ModifyVariable(method = "onKeyEvent", at = @At(value = "STORE"), ordinal = 0)
    private boolean onKeyEvent(boolean original, long windowPointer, int key, int scanCode, int action, int modifiers) {
        EventManager.INSTANCE.register(new RawKeyEvent(key, action));
        return original;
    }

    @Inject(method = "onKeyEvent", at = @At(
            //#if FORGE
            value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;fireKeyInput(IIII)V", remap = false
            //#else
            //$$ "TAIL"
            //#endif
    ), remap = true)
    private void onKeyInputEvent(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        EventManager.INSTANCE.post(new KeyInputEvent());
    }
}
