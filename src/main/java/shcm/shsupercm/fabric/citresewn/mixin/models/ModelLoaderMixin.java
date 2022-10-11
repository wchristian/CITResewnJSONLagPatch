package shcm.shsupercm.fabric.citresewn.mixin.models;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import shcm.shsupercm.fabric.citresewn.cit.models.ModelWithCITModel;

@Mixin(ModelLoader.class)
public class ModelLoaderMixin {
    private UnbakedModel citresewn$listenToBaking$captureUnbakedModel = null;

    @ModifyVariable(method = "bake", at =
    @At(value = "STORE", target = "Lnet/minecraft/client/render/model/ModelLoader;getOrLoadModel(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/UnbakedModel;"))
    private UnbakedModel citresewn$listenToBaking$captureUnbakedModel(UnbakedModel original) {
        return citresewn$listenToBaking$captureUnbakedModel = original;
    }

    @Inject(method = "bake", locals = LocalCapture.CAPTURE_FAILEXCEPTION, at =
    @At("RETURN"), slice =
    @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;getOrLoadModel(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/UnbakedModel;")))
    private void citresewn$listenToBaking(Identifier id, ModelBakeSettings settings, CallbackInfoReturnable<BakedModel> cir) {
        if (citresewn$listenToBaking$captureUnbakedModel instanceof ModelWithCITModel citModel && citModel.citresewn$getCITModel() != null) {
            //todo baking error handling
            citModel.citresewn$getCITModel().bakedListener.citModelBaked(citModel.citresewn$getCITModel(), cir.getReturnValue());
        }
        citresewn$listenToBaking$captureUnbakedModel = null;
    }
}
