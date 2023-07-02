package net.mehvahdjukaar.sleep_tight.common.items;

import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.sleep_tight.SleepTightPlatformStuff;
import net.mehvahdjukaar.sleep_tight.core.ModEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class NightBagItem extends BlockItem {
    public NightBagItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide) {
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
            //player sleep check only works on server side because level.isDay() is true for client
        } else {
            BlockPos pos = BlockPos.containing(player.position().add(0, 1 / 16f, 0));
            ItemStack stack = player.getItemInHand(usedHand);

            //same logic as startSleepingInBed. Performed before actually committing. Hopefully these should match
            var problem = SleepTightPlatformStuff.invokeSleepChecksEvents(player, pos);
            if (problem != null) {

                Component m = problem.getMessage();
                if (problem == Player.BedSleepingProblem.NOT_POSSIBLE_HERE) {
                    m = Component.translatable("message.sleep_tight.not_possible_here");
                }
                if (m != null) player.displayClientMessage(m, true);

                return InteractionResultHolder.fail(stack);
            }

            BlockHitResult hit = new BlockHitResult(Vec3.atBottomCenterOf(pos).add(0, 1, 0), Direction.DOWN, pos, false);
            BlockPlaceContext context = new BlockPlaceContext(player, usedHand, stack, hit);
            InteractionResult r = this.place(context);
            return switch (r) {
                case SUCCESS -> InteractionResultHolder.consume(stack); //no swing anim
                case CONSUME, CONSUME_PARTIAL -> InteractionResultHolder.consume(stack);
                case FAIL -> {

                    player.displayClientMessage(Component.translatable("message.sleep_tight.night_bag"), true);

                    yield InteractionResultHolder.fail(stack);
                }
                default -> InteractionResultHolder.pass(stack);
            };
        }
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        return (!this.mustSurvive() || state.canSurvive(context.getLevel(), context.getClickedPos()));
    }
}
