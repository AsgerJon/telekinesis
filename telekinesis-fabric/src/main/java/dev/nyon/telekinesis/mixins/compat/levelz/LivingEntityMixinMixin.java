package dev.nyon.telekinesis.mixins.compat.levelz;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.nyon.telekinesis.TelekinesisPolicy;
import dev.nyon.telekinesis.utils.PlayerUtils;
import dev.nyon.telekinesis.utils.TelekinesisUtils;
import net.levelz.init.ConfigInit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(value = LivingEntity.class, priority = 1500)
public class LivingEntityMixinMixin {
    @Unique
    final LivingEntity instance = (LivingEntity) (Object) this;

    @TargetHandler(
        mixin = "net.levelz.mixin.entity.LivingEntityMixin",
        name = "dropXpMixin"
    )
    @WrapWithCondition(
        method = "@MixinSquared:Handler",
        at = @At(
            value = "INVOKE",
            target = "net/levelz/entity/LevelExperienceOrbEntity.spawn (Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"
        )
    )
    protected boolean redirectLevelZExpDrop(
        ServerLevel world,
        Vec3 pos,
        int amount
    ) {
        if (ConfigInit.CONFIG.useIndependentExp) return true;
        if (!(instance.getLastAttacker() instanceof ServerPlayer _serverPlayer)) return true;
        final var hasTelekinesis = TelekinesisUtils.handleTelekinesis(
            TelekinesisPolicy.ExpDrops,
            _serverPlayer,
            null,
            serverPlayer -> PlayerUtils.addExpToPlayer(serverPlayer, amount));
        return !hasTelekinesis;
    }
}
