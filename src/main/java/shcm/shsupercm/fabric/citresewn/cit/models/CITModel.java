package shcm.shsupercm.fabric.citresewn.cit.models;

import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import shcm.shsupercm.fabric.citresewn.CITResewn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

public final class CITModel {
    public static final String ABSOLUTE_PATH_NAMESPACE = "citresewn";

    public final Identifier propertiesIdentifier, modelLoaderIdentifier;
    private JsonUnbakedModel mainModel = null;

    public CITModelBakedListener bakedListener = null;

    public CITModel(Identifier propertiesIdentifier) {
        this.propertiesIdentifier = propertiesIdentifier;
        this.modelLoaderIdentifier = new Identifier(ABSOLUTE_PATH_NAMESPACE, propertiesIdentifier.getNamespace() + "/" + propertiesIdentifier.getPath());
    }

    public JsonUnbakedModel mainModel() {
        return this.mainModel;
    }

    public CITModel mainModel(JsonUnbakedModel unbakedModel) throws ModelReadException {
        if (this.mainModel != null)
            throw new ModelReadException(new IllegalStateException("Already set main model"));

        this.mainModel = unbakedModel;
        this.mainModel.id = modelLoaderIdentifier.toString();
        ((ModelWithCITModel) this.mainModel).citresewn$setCITModel(this);
        return this;
    }

    public CITModel mainModel(Reader json) throws ModelReadException {
        try {
            return mainModel(JsonUnbakedModel.deserialize(json));
        } catch (Exception e) {
            throw new ModelReadException(e);
        }
    }

    public CITModel mainModel(Identifier resourcePath) throws ModelReadException {
        try {
            BufferedReader reader = CITResewn.INSTANCE.activeModelLoader.resourceManager.getResource(resourcePath).orElseThrow().getReader();
            return mainModel(reader);
        } catch (IOException | NoSuchElementException e) {
            throw new ModelReadException(e);
        }
    }

    public CITModel mainModel(Item baseModel) throws ModelReadException {
        Identifier itemIdentifier = Registry.ITEM.getId(baseModel);
        if (Registry.ITEM.getDefaultId().equals(itemIdentifier) && baseModel != Items.AIR)
            throw new ModelReadException(new NullPointerException("Item not in registry"));

        itemIdentifier = new Identifier(itemIdentifier.getNamespace(), "item/" + itemIdentifier.getPath());

        try {
            return mainModel(CITResewn.INSTANCE.activeModelLoader.loadModelFromJson(itemIdentifier));
        } catch (IOException e) {
            throw new ModelReadException(e);
        }
    }

    public CITModel bake(CITModelBakedListener bakedListener) {
        if (mainModel == null)
            throw new IllegalStateException("No main model assigned");

        this.bakedListener = bakedListener;

        CITResewn.INSTANCE.activeModelLoader.unbakedModels.put(modelLoaderIdentifier, mainModel);
        CITResewn.INSTANCE.activeModelLoader.modelsToBake.put(modelLoaderIdentifier, mainModel);
        this.mainModel = null;
        return this;
    }
}
