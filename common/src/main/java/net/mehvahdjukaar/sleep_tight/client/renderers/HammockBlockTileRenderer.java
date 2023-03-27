package net.mehvahdjukaar.sleep_tight.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mehvahdjukaar.sleep_tight.SleepTightClient;
import net.mehvahdjukaar.sleep_tight.common.blocks.HammockBlock;
import net.mehvahdjukaar.sleep_tight.common.tiles.HammockTile;
import net.mehvahdjukaar.sleep_tight.common.HammockPart;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class HammockBlockTileRenderer implements BlockEntityRenderer<HammockTile> {

    private final ModelPart model;
    private final ModelPart ropeF;
    private final ModelPart ropeB;

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("rope_b", CubeListBuilder.create().texOffs(32, 56).addBox(-8.0F, 0.0F, 0.0F, 16.0F, 8.0F, 0.0F), PartPose.offsetAndRotation(0.0F, -6.0F, -18.0F, -2.0071F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("rope_f", CubeListBuilder.create().texOffs(32, 48).addBox(-8.0F, -8.0F, 0.0F, 16.0F, 8.0F, 0.0F), PartPose.offsetAndRotation(0.0F, -6.0F, 18.0F, -1.1345F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("pillow", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -18.0F, -6.0F, 16.0F, 36.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public HammockBlockTileRenderer(BlockEntityRendererProvider.Context context) {
        this.model = context.bakeLayer(SleepTightClient.HAMMOCK);
        this.ropeF = model.getChild("rope_f");
        this.ropeB = model.getChild("rope_b");
    }

    @Override
    public void render(HammockTile blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        BlockState state = blockEntity.getBlockState();
        HammockPart value = state.getValue(HammockBlock.PART);
        boolean onRope = value.isOnFence();
        float zOffset;
        double dy = value.getPivotOffset();

        if (onRope) {
            zOffset = -0.5f;
            ropeB.xRot = (float) (Math.PI * -150 / 180f);
            ropeF.xRot = (float) (Math.PI * -30 / 180f);
        } else {
            zOffset = 0f;
            ropeB.xRot = (float) (-130 / 180f * Math.PI);
            ropeF.xRot = (float) (-50 / 180f * Math.PI);
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5 + dy, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.getValue(HammockBlock.FACING).toYRot()));


        float yaw = blockEntity.getRoll(partialTick);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F + yaw));

        //renderDebugPivot(poseStack, bufferSource);

        poseStack.translate(0, 0.5 + dy, zOffset);


        Material material = SleepTightClient.HAMMOCK_TEXTURES[blockEntity.getColor().getId()];
        VertexConsumer vertexConsumer = material.buffer(bufferSource, RenderType::entityCutoutNoCull);
        this.model.render(poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private static void renderDebugPivot(PoseStack poseStack, MultiBufferSource bufferSource) {
        var pBuffer = bufferSource.getBuffer(RenderType.lines());
        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        pBuffer.vertex(matrix4f, 0.0F, 0, -1.0F)
                .color(255, 0, 255, 255)
                .normal(matrix3f, 0, 1, 0).endVertex();
        pBuffer.vertex(matrix4f, 0, 0, 2)
                .color(255, 0, 255, 255)
                .normal(matrix3f, 0, 1, 0).endVertex();
    }
}
