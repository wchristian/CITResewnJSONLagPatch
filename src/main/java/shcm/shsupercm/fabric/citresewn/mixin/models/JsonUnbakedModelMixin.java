package shcm.shsupercm.fabric.citresewn.mixin.models;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.cit.models.CITModel;
import shcm.shsupercm.fabric.citresewn.cit.models.ModelWithCITModel;

import java.util.function.Function;

@Mixin(JsonUnbakedModel.class)
public class JsonUnbakedModelMixin implements ModelWithCITModel {
    private CITModel citresewn$citModel = null;

    @Override
    public void citresewn$setCITModel(CITModel citModel) {
        this.citresewn$citModel = citModel;
    }

    @Inject(method = "bake(Lnet/minecraft/client/render/model/ModelLoader;Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/model/BakedModel;", at =
    @At("RETURN"))
    private void citresewn$modelBake(ModelLoader loader, JsonUnbakedModel parent, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier id, boolean hasDepth, CallbackInfoReturnable<BakedModel> cir) {
        if (citresewn$citModel == null)
            return;

        //todo baking error handling
        citresewn$citModel.bakedListener.citModelBaked(citresewn$citModel, cir.getReturnValue());
    }
}
