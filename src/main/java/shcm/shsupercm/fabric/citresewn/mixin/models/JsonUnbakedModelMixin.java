package shcm.shsupercm.fabric.citresewn.mixin.models;

import net.minecraft.client.render.model.json.JsonUnbakedModel;
import org.spongepowered.asm.mixin.Mixin;
import shcm.shsupercm.fabric.citresewn.cit.models.CITModel;
import shcm.shsupercm.fabric.citresewn.cit.models.ModelWithCITModel;

@Mixin(JsonUnbakedModel.class)
public class JsonUnbakedModelMixin implements ModelWithCITModel {
    private CITModel citresewn$citModel = null;

    @Override
    public void citresewn$setCITModel(CITModel citModel) {
        this.citresewn$citModel = citModel;
    }

    @Override
    public CITModel citresewn$getCITModel() {
        return this.citresewn$citModel;
    }
}
